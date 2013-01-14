package com.openfeint.internal.ui;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonParser;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.openfeint.api.OpenFeintDelegate;
import com.openfeint.api.R;
import com.openfeint.api.resource.Score;
import com.openfeint.api.resource.User;
import com.openfeint.api.ui.Dashboard;
import com.openfeint.internal.ImagePicker;
import com.openfeint.internal.JsonResourceParser;
import com.openfeint.internal.OpenFeintInternal;
import com.openfeint.internal.Util;
import com.openfeint.internal.db.DB;
import com.openfeint.internal.request.IRawRequestDelegate;
import com.openfeint.internal.resource.ScoreBlobDelegate;

public class WebNav extends Activity {
	
	protected static boolean s_bNeedRefreshOF = false;	
	// This is necessary because when we ditch out to the image picker,
	// the game might get evicted.
	protected void onSaveInstanceState(Bundle outState) {
		OpenFeintInternal.saveInstanceState(outState);
	}
	
	// This is necessary because when we ditch out to the image picker,
	// the game might get evicted.
	protected void onRestoreInstanceState(Bundle inState) {
		OpenFeintInternal.restoreInstanceState(inState);		
	}
	
	protected static final String TAG = "WebUI";

	// sub-activity request codes go here. make sure they don't collide in
	// derived classes
	protected static final int REQUEST_CODE_NATIVE_BROWSER = 25565;

	private WebView mWebView;
	private WebNavClient mWebViewClient;

	ActionHandler mActionHandler;

	public ActionHandler getActionHandler() {
		return mActionHandler;
	}

	Dialog mLaunchLoadingView;

	public Dialog getLaunchLoadingView() {
		return mLaunchLoadingView;
	}

	boolean mIsFrameworkLoaded = false;
	private String rootPath = "";

	protected void setFrameworkLoaded(boolean value) {
	    OpenFeintInternal.log("ForTest", "WebNav::setFrameworkLoaded::" + value );
		mIsFrameworkLoaded = value;
	}

	protected int pageStackCount;
	boolean mIsVisible = false;
	
	private boolean mShouldRefreshOnResume = true;

	protected ArrayList<String> mPreloadConsoleOutput = new ArrayList<String>();

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        Util.setOrientation(this);
		setContentView(R.layout.of_webnav);
		OpenFeintInternal.log(TAG, "--- WebUI Bootup ---");
		
		OpenFeintInternal.getInstance().addActivity(this);
		// Setup helper views
		pageStackCount = 0;

		// Setup WebView
		mWebView = (WebView) findViewById(R.id.webview);
		mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.getSettings().setPluginsEnabled(false);
		mWebView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
		mWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
		
		try {
			Method setDomStorageEnabled = mWebView.getSettings().getClass().getMethod("setDomStorageEnabled", boolean.class);
			Method setAppCacheMaxSize = mWebView.getSettings().getClass().getMethod("setAppCacheMaxSize",  long.class);
			if(null != setDomStorageEnabled){
				setDomStorageEnabled.invoke(mWebView.getSettings(), true);
				if(null != setAppCacheMaxSize){
				  setAppCacheMaxSize.invoke(mWebView.getSettings(), 50 * 1024 * 1024);
				}
			}
		} catch (Exception e) {
			OpenFeintInternal.log(TAG, "API doesn't support setDomStorageEnabled function");
		} 

		// Setup native loader to show while we chew on stuff
		mLaunchLoadingView = new Dialog(this, R.style.OFLoading);
		mLaunchLoadingView
				.setOnCancelListener(new DialogInterface.OnCancelListener() {
					public void onCancel(DialogInterface dialog) {
						WebNav.this.finish();
					}
				});
		mLaunchLoadingView.setCancelable(true);
		mLaunchLoadingView.setContentView(R.layout.of_native_loader);

		ProgressBar progress = (ProgressBar) mLaunchLoadingView
				.findViewById(R.id.progress);
		progress.setIndeterminate(true);
		progress.setIndeterminateDrawable(OpenFeintInternal.getInstance()
				.getContext().getResources().getDrawable(
						R.drawable.of_native_loader_progress));
				mLaunchLoadingView.show();

		// Setup the WebViewClient and its ActionHandler
		mActionHandler = createActionHandler(this);
		mWebViewClient = new WebNavClient(mActionHandler);
		mWebView.setWebViewClient(mWebViewClient);
		mWebView.setWebChromeClient(new WebNavChromeClient());

		// Setup the JavaScript bridge
		mWebView.addJavascriptInterface(new Object() {
			@SuppressWarnings("unused")
			public void action(final String actionUri) {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						getActionHandler().dispatch(Uri.parse(actionUri));
					}
				});
			}

			@SuppressWarnings("unused")
			public void frameworkLoaded() {
				WebNav.this.setFrameworkLoaded(true);
			}
		}, "NativeInterface");

		String path = initialContentPath();
		if (path.contains("?"))
			path = path.split("\\?")[0];
		if (!path.endsWith(".json"))
			path += ".json";
		WebViewCache.prioritize(path);
		load(false);
	}

	
	@Override
	/**
	 * Load the menu for this activity
	 */
	public boolean onCreateOptionsMenu(Menu menu) {
	    getMenuInflater().inflate(R.menu.of_dashboard, menu);
	    return true;
	}
	
	@Override
	/**
	 * If we are already on the home screen, don't show the home menu button
	 */
	public boolean onPrepareOptionsMenu (Menu menu) {
		if(OpenFeintInternal.getInstance().isUserLoggedIn() == false){
		  menu.findItem(R.id.home).setVisible(false);
		  menu.findItem(R.id.settings).setVisible(false);
		}else{
		  menu.findItem(R.id.home).setVisible(true);
		  menu.findItem(R.id.settings).setVisible(true);
		}
		return true;
	}

	/**
	 * Tell the Dashboard JavaScript menu handler function that we just
	 * pressed something.
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		String menuButtonName = null;
		
	    if (item.getItemId() == R.id.home) {
	    	menuButtonName = "home";
	    } else if (item.getItemId() == R.id.settings) {
	    	menuButtonName = "settings";
	    } else if (item.getItemId() == R.id.exit_feint) {
	    	menuButtonName = "exit";
	    }
	    
	    if (menuButtonName == null) return super.onOptionsItemSelected(item);
	    if(menuButtonName.equals("exit")){
	    	mActionHandler.dismiss(mNativeBrowserParameters);
	    }else{
	      executeJavascript(String.format("OF.menu('%s')", menuButtonName));
	    }
	    
	    return true;
	}
	
	protected String rootPage() {
		return "index.html";
	}
	
	private void load(final boolean reload) {
	    OpenFeintInternal.log("ForTest", "WebNav::load::reload=" + reload );
	    OpenFeintInternal.log("ForTest", "WebNav::load::rootPage=" + rootPage());
	    
	    String state = Environment.getExternalStorageState();
	    if (Environment.MEDIA_MOUNTED.equals(state)) {
	    	File sdcard = Environment.getExternalStorageDirectory();
	    	File feintRoot = new File(sdcard, "the9GameCenter");
	    	File webui = new File(feintRoot, "webui");
	      	rootPath = webui.getAbsolutePath() + "/";
	    } else {
			final File baseDir = getApplicationContext().getFilesDir();
			File rootDir = new File(baseDir, "webui");
			rootPath = rootDir.getAbsolutePath() +"/";
	    }

		File file = new File(rootPath+"upgrade_ok.txt"); 
	    if(!file.exists()){
	    	boolean exist_index_file = true;
	    	AssetManager mg = getResources().getAssets();
	    	try {
	    	  mg.open("webui/index.html");
	    	  mg.open("webui/manifest.plist");
	    	} catch (IOException ex) {
	    	  exist_index_file = false;
	    	}
	    	if(exist_index_file){
	    		mWebView.loadUrl("file:///android_asset/webui/index.html");
	    		DB.createDB(OpenFeintInternal.getInstance().getContext());
	    		WebViewCache.start();
	    	}else{
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setTitle(R.string.load_assets_file_tip);
				builder.setMessage(R.string.fail_load_assets_file);
				builder.setNegativeButton(OpenFeintInternal.getRString(R.string.of_ioexception_reading_body), new OnClickListener() {
  						public void onClick(DialogInterface dialog, int which) {
  							finish();
  						}
				  }
				);
				builder.show();
	    	}
	    }else{
			WebViewCache.trackPath(rootPage(), new WebViewCacheCallback() {
				public void pathLoaded(String itemPath) {
				    OpenFeintInternal.log("ForTest", "load::WebViewCache.trackPath::pathLoaded::"+ itemPath );
					if (mWebView != null) {
						String url = "file://"+rootPath+itemPath;
						OpenFeintInternal.log(TAG, "Loading URL: " + url);
						if (reload) {
							mWebView.reload();
						} else {
							mWebView.loadUrl(url);
						}
					}
				}
	            @Override
	            public void failLoaded() {
	                closeForDiskError();
	            }
			});
			DB.createDB(OpenFeintInternal.getInstance().getContext());
			WebViewCache.start();
	    }
	}

	private void closeForDiskError() {
        // THIS is a hack, since we may rewrite the web cache in next version
        // let's just hack it here.
	    this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dismissDialog();
                String place = (Util.sdcardReady(WebNav.this))? OpenFeintInternal.getRString(R.string.of_sdcard):
                    OpenFeintInternal.getRString(R.string.of_device);
                new AlertDialog.Builder(WebNav.this)
                .setMessage(String.format(OpenFeintInternal.getRString(R.string.of_nodisk), place))
                .setPositiveButton(OpenFeintInternal.getRString(R.string.of_no), new OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        WebNav.this.finish();
                    }
                }).show();
            }
	    });
	}

	private static final String jsQuotedStringLiteral(String unquotedString) {
		if (unquotedString == null) return "''";
		return "'" + unquotedString.replace("\\", "\\\\").replace("'", "\\'") + "'";
	}
	
	/**
	 * Ensure the current user data is up to date in case it changed since this
	 * activity was last in the foreground.
	 */
	@Override
	public void onResume() {
		mIsFrameworkLoaded = true;
//		mShouldRefreshOnResume = false;
		OpenFeintInternal.log("ForTest", "WebNav::onResume");
		super.onResume();
//		OpenFeintInternal.log("ForTest", "WebNav::onResume:: mIsFrameworkLoaded = "+ mIsFrameworkLoaded);
//		OpenFeintInternal.log("ForTest", "WebNav::onResume:: mShouldRefreshOnResume = "+ mShouldRefreshOnResume);
//		OpenFeintInternal.log("ForTest", "WebNav::onResume:: s_bNeedRefreshOF = "+ s_bNeedRefreshOF);
		if( s_bNeedRefreshOF )
		{
			User localUser = OpenFeintInternal.getInstance().getCurrentUser();
			if (localUser != null && mIsFrameworkLoaded) {
				executeJavascript(
					//String.format("if (OF.user) { OF.user.name = '%s'; OF.user.id = '%s'; }",
					String.format("if (OF.user) { OF.user.name = %s; OF.user.id = '%s'; }",
						//localUser.name.replace("'", "\\'"), 
						jsQuotedStringLiteral(localUser.name),
						localUser.resourceID()));
				if (mShouldRefreshOnResume) {
					executeJavascript("if (OF.page) OF.refresh();");
				}
					
			}
			s_bNeedRefreshOF = false;
		}
		mShouldRefreshOnResume = true;
	}
	
	@Override
	public void onStop() {
		super.onStop();
		dismissDialog();
	}

	private void dismissDialog() {
	    OpenFeintInternal.log("ForTest", "WebNavClient::dismissDialog" );
		if (mLaunchLoadingView.isShowing())
		    mLaunchLoadingView.dismiss();
	}

	private void showDialog() {
	    OpenFeintInternal.log("ForTest", "WebNavClient::showDialog" );
		if (!mLaunchLoadingView.isShowing())
			mLaunchLoadingView.show();
	}

	/**
	 * Tell the WebNav about an orientation change
	 */
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		String orientationString = null;
		if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
			orientationString = "landscape";
		} else {
			orientationString = "portrait";
		}

		executeJavascript(String.format("OF.setOrientation('%s');",
				orientationString));
	}

	/**
	 * After the page context is ready, load the first page of content.
	 */
	public void loadInitialContent() {
		String path = initialContentPath();
		if (path.contains("?"))
			path = path.split("\\?")[0];
		if (!path.endsWith(".json"))
			path += ".json";

		WebViewCache.trackPath(path, new WebViewCacheCallback() {
			public void pathLoaded(String itemPath) {
			    OpenFeintInternal.log("ForTest", "WebNav::loadInitialContent::WebViewCache::trackPath::"+ itemPath );
				executeJavascript("OF.navigateToUrl('" + initialContentPath()
						+ "')");
			}

			@Override
            public void failLoaded() {
                closeForDiskError();
            }
		});
	}

	/**
	 * Create the native action handler for this flow. Override in subclasses to
	 * add new actions.
	 * 
	 * @param webNav
	 *            the WebNav instance from which actions will be called.
	 * @return an ActionHandler instance on which to call native actions.
	 */
	protected ActionHandler createActionHandler(WebNav webNav) {
		return new ActionHandler(webNav);
	}

	/**
	 * Override in subclasses to set what template path is loaded as the first
	 * content.
	 * 
	 * @return the template path to load when this WebNav appears.
	 */
	protected String initialContentPath() {
		String contentPath = getIntent().getStringExtra("content_path");
		if (contentPath == null) {
			throw new RuntimeException(
					"WebNav intent requires extra value 'content_path'");
		}
		return contentPath;
	}

	/**
	 * Intercept the back button. If there is history to go back to in the
	 * WebNav, then go back. Otherwise, finish the Activity and return.
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_SEARCH) {
			executeJavascript(String.format("OF.menu('%s')", "search"));
			return true;
		}

		if (keyCode == KeyEvent.KEYCODE_BACK && pageStackCount > 1) {
			executeJavascript("OF.goBack()");
			return true;
		}

		return super.onKeyDown(keyCode, event);
	}

	/**
	 * Run some JavaScript in the WebView.
	 * 
	 * @param js
	 *            the JavaScript to execute.
	 */
	public void executeJavascript(String js) {
		if (mWebView != null) {
			//OpenFeintInternal.log("ForTest", "executeJavascript::mWebView.loadUrl::javascript:" + js );
			mWebView.loadUrl("javascript:" + js);
		}
		else
		{
			OpenFeintInternal.log("ForTest", "mWebView=null::javascript:" + js );
		}
	}

	/**
	 * Fades the webview in or out
	 * 
	 * @param toVisible
	 *            true for fade in, false for fade out.
	 */
	public void fade(boolean toVisible) {
		if (mWebView != null) {
			if (mIsVisible != toVisible) {
				mIsVisible = toVisible;
				AlphaAnimation anim = new AlphaAnimation(toVisible ? 0.0f
						: 1.0f, toVisible ? 1.0f : 0.0f);
				anim.setDuration(toVisible ? 200 : 0);
				anim.setFillAfter(true);
				mWebView.startAnimation(anim);
				if (mWebView.getVisibility() == View.INVISIBLE)
					mWebView.setVisibility(View.VISIBLE);
			}
		}
	}

	/**
	 * Class to handle action URI navigation events as messages.
	 * 
	 * @author Alex Wayne
	 */
	private class WebNavClient extends WebViewClient {
		ActionHandler mActionHandler;

		/**
		 * Constructor
		 * 
		 * @param anActionHandler
		 *            ActionHandler instance to use with the WebNav
		 */
		public WebNavClient(ActionHandler anActionHandler) {
			mActionHandler = anActionHandler;
		}

		/**
		 * Intercept URL loading and inspect for messages being passed to native
		 * code.
		 */
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String stringUrl) {
			Uri uri = Uri.parse(stringUrl);
			if (uri.getScheme().equals("http")
					|| uri.getScheme().equals("https")) {
				view.loadUrl(stringUrl);
			} else if (uri.getScheme().equals("openfeint")) {
				mActionHandler.dispatch(uri);
			} else {
				OpenFeintInternal.log(TAG, "UNHANDLED PROTOCOL: "
						+ uri.getScheme());
			}

			return true;
		}

		/**
		 * Make sure the loading is hidden on generic failures so interaction is
		 * not blocked
		 */
		@Override
		public void onReceivedError(WebView view, int errorCode,
				String description, String failingUrl) {
			mActionHandler.hideLoader(null);
		}

		@Override
		public void onPageFinished(WebView view, String url) {
		    OpenFeintInternal.log("ForTest", "WebNavClient::onPageFinished::"+ mIsFrameworkLoaded );
			if (mIsFrameworkLoaded) {
				loadInitialContent();
			} else {
			    OpenFeintInternal.log("ForTest", "WebNavClient::onPageFinished2::"+ mIsFrameworkLoaded );
				// call onPageFinished after restore
			    if (WebViewCache.recover()) {
				    OpenFeintInternal.log("ForTest", "WebNavClient::onPageFinished3::"+ mIsFrameworkLoaded );
	                load(true);
				    OpenFeintInternal.log("ForTest", "WebNavClient::onPageFinished4::"+ mIsFrameworkLoaded );
	                new AlertDialog.Builder(view.getContext())
  					.setMessage(OpenFeintInternal.getRString(R.string.of_crash_report_query))
  					.setNegativeButton(OpenFeintInternal.getRString(R.string.of_no), new OnClickListener() {
  						public void onClick(DialogInterface dialog, int which) {
  							WebNav.this.finish();
  						}
  					})
  					.setPositiveButton(OpenFeintInternal.getRString(R.string.of_yes), new OnClickListener() {
  						public void onClick(DialogInterface dialog, int which) {
  							WebNavClient.this.submitCrashReport();
  						}
  					})
  					.show();
			    } else if (!WebViewCache.isDiskError()) {
			        finish();
			    }
			}
		}

		private void submitCrashReport() {
			Map<String, Object> crashReport = new HashMap<String, Object>();
			crashReport.put("console", new JSONArray(mPreloadConsoleOutput));

			JSONObject json = new JSONObject(crashReport);

			Map<String, Object> params = new HashMap<String, Object>();
			params.put("crash_report", json.toString());
			OpenFeintInternal.genericRequest("/webui/crash_report", "POST",
					params, null, null);
		}

		/**
		 * Initial HTML/JS framework is loaded, so inject the client
		 * configuration. Then load the initial content
		 */
		public void loadInitialContent() {

			OpenFeintInternal of = OpenFeintInternal.getInstance();
			User localUser = of.getCurrentUser();
			int orientation = getResources().getConfiguration().orientation;

			HashMap<String, Object> user = new HashMap<String, Object>();
			if (localUser != null) {
				user.put("id", localUser.resourceID());
				user.put("name", localUser.name);
			}

			HashMap<String, Object> game = new HashMap<String, Object>();
			game.put("id", of.getAppID());
			game.put("name", of.getAppName());
			game.put("version", of.getAppVersion());

			Map<String, Object> device = OpenFeintInternal.getInstance()
					.getDeviceParams();

			HashMap<String, Object> config = new HashMap<String, Object>();
			config.put("platform", "android");
			config.put("clientVersion", of.getOFVersion());
			config.put("hasNativeInterface", true);

			config.put("dpi", 		Util.getDpiName(WebNav.this));
			config.put("locale", 	getResources().getConfiguration().locale.toString());
			config.put("user", 		new JSONObject(user));
			config.put("game", 		new JSONObject(game));
			config.put("device", 	new JSONObject(device));
			config.put("actions", 	new JSONArray(getActionHandler().getActionList()));
			
			config.put("orientation",
					orientation == Configuration.ORIENTATION_LANDSCAPE ?
							"landscape" : "portrait");
			
			config.put("serverUrl", of.getServerUrl());

			JSONObject json = new JSONObject(config);

			// Run the environment variables setup JavaScript
			executeJavascript(String.format("OF.init.clientBoot(%s)", json
					.toString()));

			// Load the first content
			mActionHandler.mWebNav.loadInitialContent();
		}
	}

	/**
	 * Class to handle browser chrome tasks, like alerts
	 * 
	 * @author Alex Wayne
	 */
	private class WebNavChromeClient extends WebChromeClient {
		public boolean onJsAlert(WebView view, String url, String message,
				final JsResult result) {
			new AlertDialog.Builder(view.getContext()).setMessage(message)
					.setNegativeButton(
							OpenFeintInternal.getRString(R.string.of_ok),
							new OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									result.cancel();
								}
							}).setOnCancelListener(new OnCancelListener() {
						public void onCancel(DialogInterface dialog) {
							result.cancel();
						}
					}).show();
			return true;
		}

		public boolean onJsConfirm(WebView view, String url, String message,
				final JsResult result) {
			new AlertDialog.Builder(view.getContext()).setMessage(message)
					.setPositiveButton(
							OpenFeintInternal.getRString(R.string.of_ok),
							new OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									result.confirm();
								}
							}).setNegativeButton(
							OpenFeintInternal.getRString(R.string.of_cancel),
							new OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									result.cancel();
								}
							}).setOnCancelListener(new OnCancelListener() {
						public void onCancel(DialogInterface dialog) {
							result.cancel();
						}
					}).show();
			return true;
		}

		public void onConsoleMessage(String message, int lineNumber,
				String sourceID) {
			if (!mIsFrameworkLoaded) {
				String line = String.format("%s at %s:%d)", message, sourceID,
						lineNumber);
				mPreloadConsoleOutput.add(line);
			}
		}
	}

	/**
	 * Class to handle native code actions from OF.sendAction("someAction",
	 * {options: ... })
	 * 
	 * @author Alex Wayne
	 */
	protected class ActionHandler extends Object {

		WebNav mWebNav;

		List<String> mActionList;

		protected List<String> getActionList() {
			return mActionList;
		}

		/**
		 * Constructor
		 * 
		 * @param webNav
		 *            WebNav calling the actions
		 */
		public ActionHandler(WebNav webNav) {
			mWebNav = webNav;

			mActionList = new ArrayList<String>();
			populateActionList(mActionList);
		}

		/**
		 * Add action names to the supported actionList. Override this to
		 * support additional actions. Every action you add MUST have a
		 * corresponding method named identically to handle that action.
		 * 
		 * @param actionList
		 *            list of actions supported by the superclass. Add your
		 *            custom actions to this list.
		 */
		protected void populateActionList(List<String> actionList) {
			// Available in v1.0 forward
			actionList.add("log");
			actionList.add("apiRequest");
			actionList.add("contentLoaded");
			actionList.add("startLoading");
			actionList.add("back");
			actionList.add("showLoader");
			actionList.add("hideLoader");
			actionList.add("alert");
			actionList.add("dismiss");
			actionList.add("openMarket");
			actionList.add("openGameStore");
			actionList.add("InstalledGameStore");
			actionList.add("isApplicationInstalled");
			actionList.add("openYoutubePlayer");
			actionList.add("profilePicture");
			actionList.add("downloadFromThe9GameCenter");
			
			// Available in v1.5 forward
			actionList.add("openBrowser");
			actionList.add("downloadBlob");

			// Available in v1.6 forward
			actionList.add("dashboard");
			
			// Available in v1.7.1 forward
			actionList.add("readSetting");
			actionList.add("writeSetting");
			actionList.add("openSettings");
		
			actionList.add("requireLoginWithReturn"); 
			actionList.add("openSpecifyDashboardScreen");
		}

		
		public void openSettings(final Map<String,String> options) {
			Settings.open();
		}
		
		/**
		 * Dispatch an action URI to an identically named method to handle it
		 * 
		 * @param uri
		 *            the action URI to parse
		 */
		public void dispatch(Uri uri) {
			if (uri.getHost().equals("action")) {

				// Prepare action values
				Map<String, Object> options = parseQueryString(uri);
				String actionName = uri.getPath().replaceFirst("/", "");
				if( actionName == null )
				{
					OpenFeintInternal.log(TAG, "actionName name = null:"
							+ uri.getHost());
					return;
				}

				// Log the action unless this is an explicit log action
				if (!actionName.equals("log")) {
					Map<String, Object> escapedOptions = new HashMap<String, Object>(
							options);
					String params = (String) options.get("params");
					if (params != null && params.contains("password")) {
						escapedOptions.put("params", "---FILTERED---");
					}

					OpenFeintInternal.log(TAG, "ACTION: " + actionName + " "
							+ escapedOptions.toString());
				}

				// Find the native method to call
				if (mActionList.contains(actionName)) {
					try {
						getClass().getMethod(actionName, Map.class).invoke(
								this, options);
					} catch (NoSuchMethodException e) {
						OpenFeintInternal.log(TAG,
								"mActionList contains this method, but it is not implemented: "
										+ actionName);
					} catch (Exception e) {
						OpenFeintInternal.log(TAG, "Unhandled Exception: "
								+ e.toString() + "   " + e.getCause());
					}
				} else {
					OpenFeintInternal.log(TAG, "UNHANDLED ACTION: "
							+ actionName);
				}

			} else {
				OpenFeintInternal.log(TAG, "UNHANDLED MESSAGE TYPE: "
						+ uri.getHost());
			}
		}

		/**
		 * Utility method to turn a URI's query string string into a Map of keys
		 * and values
		 * 
		 * @param uri
		 *            Action URI that contains the query string to parse
		 * @return a Map of Strings to Strings containing the parsed query
		 *         string.
		 */
		private Map<String, Object> parseQueryString(Uri uri) {
			return parseQueryString(uri.getEncodedQuery());
		}

		/**
		 * Utility method to turn query string string into a Map of keys and
		 * values
		 * 
		 * @param queryString
		 *            The query string to parse
		 * @return a Map of Strings to Strings containing the parsed query
		 *         string.
		 */
		private Map<String, Object> parseQueryString(String queryString) {
			Map<String, Object> options = new HashMap<String, Object>();

			if (queryString != null) {
				String[] pairs = queryString.split("&");

				for (String stringPair : pairs) {
					String[] pair = stringPair.split("=");
					if (pair.length == 2) {
						try{
							options.put(pair[0], Uri.decode(pair[1]));
						}
						catch( java.lang.OutOfMemoryError e )
						{						
				    		OpenFeintInternal.log("ForTest", "parseQueryString exception::" + e.getMessage() );
							options.put(pair[0], null);
						}
					} else {
						options.put(pair[0], null);
					}
				}
			}

			return options;
		}
		
		/**
		 * Utility method to turn query map into a string
		 * for example
		 * map.put("color", "red"); 
		 * map.put("empty", "");
		 * return color=red&empty= 
		 */
		 private String mapToString(Map<String, String> map) {  
			   StringBuilder stringBuilder = new StringBuilder();  
			  
			   for (String key : map.keySet()) {  
			    if (stringBuilder.length() > 0) {  
			     stringBuilder.append("&");  
			    }  
			    String value = map.get(key);  
			    try {  
			     stringBuilder.append((key != null ? URLEncoder.encode(key, "UTF-8") : ""));  
			     stringBuilder.append("=");  
			     stringBuilder.append(value != null ? URLEncoder.encode(value, "UTF-8") : "");  
			    } catch (UnsupportedEncodingException e) {  
			     throw new RuntimeException("This method requires UTF-8 encoding support", e);  
			    }  
			   }  
			  
			   return stringBuilder.toString();  
			  }  

		// ---
		// --- Action Methods ---
		// ---

		/**
		 * Make a signed and authenticate generic API request. Return the result
		 * into the WebNav with the status code and response body.
		 */
		public void apiRequest(Map<String, String> options) {
			final String requestID = options.get("request_id");
			Map<String, Object> params = parseQueryString(options.get("params"));
			Map<String, Object> httpParams = parseQueryString(options
					.get("httpParams"));

			OpenFeintInternal.log("ForTest", "ActionHandler::apiRequest::" + options.get("path") );
			OpenFeintInternal.genericRequest(options.get("path"), options
					.get("method"), params, httpParams,
					new IRawRequestDelegate() {
						public void onResponse(int statusCode, String responseBody) {
							String response = responseBody.trim();
							if (response.length() == 0) response = "{}";
							String js = String
									.format(
											"OF.api.completeRequest(\"%s\", \"%d\", %s)",
											requestID, statusCode, response);
							mWebNav.executeJavascript(js);
						}
					});
		}

		public void contentLoaded(Map<String, String> options) {
		    OpenFeintInternal.log("ForTest", "ActionHandler::contentLoaded" );
			if (!(options.get("keepLoader") != null && options
					.get("keepLoader").equals("true"))) {
				hideLoader(null);
				setTitle(options.get("title"));
			}

			mWebNav.fade(true);
			dismissDialog();
		}

		public void startLoading(Map<String, String> options) {
		    OpenFeintInternal.log("ForTest", "ActionHandler::startLoading" );
			mWebNav.fade(false);
			showLoader(null);

			WebViewCache.trackPath(options.get("path"),
					new WebViewCacheCallback() {
						public void pathLoaded(String itemPath) {
						    OpenFeintInternal.log("ForTest", "startLoading::WebViewCacheCallback::pathLoaded::"+ itemPath );
							executeJavascript("OF.navigateToUrlCallback()");
						}

						@Override
			            public void failLoaded() {
						    OpenFeintInternal.log("ForTest", "startLoading::WebViewCacheCallback::failLoaded" );
			                closeForDiskError();
			            }
						
						public void onTrackingNeeded() {
						    OpenFeintInternal.log("ForTest", "startLoading::WebViewCacheCallback::onTrackingNeeded" );
							showDialog();
						}
					});

			mWebNav.pageStackCount++;
		}

		public void back(Map<String, String> options) {
		    OpenFeintInternal.log("ForTest", "ActionHandler::back" );
			mWebNav.fade(false);
			String root = options.get("root");
			if (root != null && !root.equals("false")) {
				mWebNav.pageStackCount = 1;
			}

			if (mWebNav.pageStackCount > 1) {
				mWebNav.pageStackCount--;
			}
		}

		public void showLoader(Map<String, String> options) {
			// mWebNav.getLoadingView().setVisibility(View.VISIBLE);
		}

		public void hideLoader(Map<String, String> options) {
			// mWebNav.getLoadingView().setVisibility(View.GONE);
		}

		public void log(Map<String, String> options) {
			String message = options.get("message");
			if (message != null)
			{
				OpenFeintInternal.log(TAG, "WEBLOG: " + options.get("message"));
				OpenFeintInternal.log("ForTest", "WEBLOG: " + options.get("message"));
			}
			else
			{
				OpenFeintInternal.log("ForTest", "WEBLOG: message=null!!" );			
			}
		}

		public void alert(Map<String, String> options) {
			OpenFeintInternal.log("ForTest", "ActionHandler: alert::" + options.get("message") );			
			AlertDialog.Builder builder = new AlertDialog.Builder(mWebNav);
			builder.setTitle(options.get("title"));
			builder.setMessage(options.get("message"));
			builder.setNegativeButton(OpenFeintInternal
					.getRString(R.string.of_ok), null);
			builder.show();
		}

		public void dismiss(Map<String, String> options) {
		    OpenFeintInternal.log("ForTest", "ActionHandler::dismiss" );
//		    finish();
		    OpenFeintInternal.getInstance().finishAllActivities();
		}

		public void openMarket(final Map<String, String> options) {
			String packageName = options.get("package_name");
			Intent intent = new Intent(Intent.ACTION_VIEW, Uri
					.parse("market://details?id=" + packageName));
			mWebNav.startActivity(intent);
		}
		
//		public String getCustomizedStoreName(String cp_identifier){
//			String store_name = mWebNav.getString(R.string.default_store);
//			if(cp_identifier.equals("mm")){
//				 store_name = mWebNav.getString(R.string.mm_store);
//			}else if(cp_identifier.equals("estore")){
//				 store_name = mWebNav.getString(R.string.emarket_store);
//			}else if(cp_identifier.equals("wostore")){
//				 store_name = mWebNav.getString(R.string.wo_store);
//			}else if(cp_identifier.equals("tianyu")){
//				store_name = mWebNav.getString(R.string.china_telecom);
//			}else if(cp_identifier.equals("htc")){
//				store_name = mWebNav.getString(R.string.china_unicom);
//			}else if(cp_identifier.equals("motorola")){
//				store_name = mWebNav.getString(R.string.china_telecom);
//			}
//			return store_name;
//		}

		public void openGameStore(final Map<String, String> options) {
			Intent startupIntent = new Intent();
			final PackageManager packageManager = getPackageManager();
			final String api_enable = options.get("game_store[api_enable]");
			final String game_store_name = options.get("game_store[name]");
			final String game_store_platform_status = options.get("game_store_platform[status]");
			final String game_store_download_url = options.get("game_store[download_url]");
			String identifier = options.get("game_store[identifier]");
			String installed = "true";
//			String customized_store_name = getCustomizedStoreName(identifier);
			
			//Check the device has installed the store or not
			if(identifier.equals("non-cp") || identifier.startsWith("non-cp-for-")){
				installed = options.get("game_store[installed]");
			}
			
			if (installed.equals("true")) {
				if (api_enable.equals("true") && game_store_platform_status.equals("1")) {
					try {
						if(identifier.equals("tianyu")){
							String KMARKET_PACKAGENAME = "com.osa.market";
							String KMARKET_ENTRY_ACTIVITY = "com.osa.market.ui.SplashLogo";
							String INTENT_APPID_KEY = "appId";
							String INTENT_THIRDPARTY_KEY = "thirdPartyId";
							String INTENT_ISTHIRDPARTY_KEY = "thirdParty";
						    Intent intent = new Intent(Intent.ACTION_MAIN, null);
						    intent.setClassName(KMARKET_PACKAGENAME, KMARKET_ENTRY_ACTIVITY);
						    String appId = options.get("game_store_platform[package_name]");
						    String thirdPartyId = "G01jich"; 
						    intent.putExtra(INTENT_ISTHIRDPARTY_KEY, INTENT_ISTHIRDPARTY_KEY);
						    intent.putExtra(INTENT_APPID_KEY, appId);
						    intent.putExtra(INTENT_THIRDPARTY_KEY, thirdPartyId);
						    mWebNav.startActivity(intent);
						}else{
							String packageName = options.get("game_store_platform[package_name]");
							String open_api = "";
							if (packageName.matches("^\\d+$") == true) {
								open_api = options.get("game_store[open_api_with_id]");
							} else {
								open_api = options.get("game_store[open_api]");
							}
						
							startupIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(open_api + packageName));
							if(options.get("game_store[name]").contains(mWebNav.getString(R.string.wo_store))){
								startupIntent.putExtra("oauth_token", "openfeint");
							}
							mWebNav.startActivity(startupIntent);
						} 
					}catch (Exception e) {
						if(identifier.equals("non-cp") || identifier.startsWith("non-cp-for-")){
							startAppStoreByName(packageManager, game_store_name);
						}else{
							startAppStoreByPackageName(packageManager,options.get("game_store[package_name]"),game_store_name);
						}
					}
				}else if(game_store_platform_status.equals("0") && !identifier.equals("non-cp")){
					noGameInAppStore(game_store_name+getString(R.string.no_game_in_cp_store));
				}else{
					if(identifier.equals("non-cp") || identifier.startsWith("non-cp-for-")){
						startAppStoreByName(packageManager, game_store_name);
					}else{
						startAppStoreByPackageName(packageManager,options.get("game_store[package_name]"),game_store_name);
					}
				}
			} else {
				if(identifier.startsWith("non-cp-for-")){
					String cp_name = getCpName(identifier.replaceAll("non-cp-for-", ""));
					showDownload(getString(R.string.only_for_cp_store_prefix)+cp_name+getString(R.string.only_for_cp_store_suffix), game_store_download_url);
				}else{
					//Handle with request from customized version 
					//If the customized store has this game, just notice user install the store first then download the game from it
					//else just notice user that the customized store has no this game with alert dialog
					if(game_store_platform_status.equals("1")){
						showDownload(getString(R.string.uninstall_game_store), game_store_download_url);
					}else{
						noGameInAppStore(game_store_name+getString(R.string.no_game_in_cp_store));
					}
				}
			}
		}

		public String getCpName(String _identifier){
			String cp_name = "";
			if(_identifier.equals("mm")){
				 cp_name = mWebNav.getString(R.string.china_mobile);
			}else if(_identifier.equals("estore")){
				cp_name = mWebNav.getString(R.string.china_telecom);
			}else if(_identifier.equals("wostore")){
				cp_name = mWebNav.getString(R.string.china_unicom);
			}
			return cp_name;
		}
		
		public void noGameInAppStore(String message){
			AlertDialog.Builder builder = new AlertDialog.Builder(mWebNav);
			builder.setTitle(R.string.download_game_tip);
			builder.setMessage(message);
			builder.setNegativeButton(getString(R.string.back_game_store), null);
			builder.show();
		}
		
		public void startAppStoreByPackageName(PackageManager pm, String store_package_name,String customized_store_name){
			try {
				mWebNav.startActivity(pm.getLaunchIntentForPackage(store_package_name));
			} catch (Exception e) {
				noGameInAppStore(getString(R.string.can_not_download_prefix)+customized_store_name+getString(R.string.can_not_download_suffix));
			}
		}
		
		public void startAppStoreByName(final PackageManager pm, final String store_name){
			new Thread(){
			  public void run(){
			    List<ApplicationInfo> installedApps = pm.getInstalledApplications(0);
				  for (ApplicationInfo info : installedApps) {
					String name = (String) info.loadLabel(pm);
					if(name.contains(store_name)){
						mWebNav.startActivity(pm.getLaunchIntentForPackage(info.packageName));
						break;
					}
				}
			}
		  }.start();
		}
		
		public void showDownload(String alert_tip,final String game_store_download_url) {
			AlertDialog.Builder builder = new AlertDialog.Builder(mWebNav);
			builder.setTitle(R.string.download_game_tip);
			builder.setMessage(alert_tip);
			builder.setPositiveButton(OpenFeintInternal
					.getRString(R.string.download_game_store),
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int okButton) {
							Uri data = Uri.parse(game_store_download_url);
							Intent i = new Intent(Intent.ACTION_VIEW, data);
							mWebNav.startActivity(i);
						}
					});
			builder.setNegativeButton(OpenFeintInternal.getRString(R.string.back_game_store), null);
			builder.show();
		}

		public void showNotice() {
			AlertDialog.Builder builder = new AlertDialog.Builder(mWebNav);
			builder.setTitle(R.string.download_game_tip);
			builder.setMessage(R.string.fail_open_game_store);
			builder.setNegativeButton(OpenFeintInternal.getRString(R.string.back_game_store), null);
			builder.show();
		}

		public void isApplicationInstalled(final Map<String, String> options) {
			boolean installed = true;
			PackageManager packageManager = mWebNav.getPackageManager();
			try {
				packageManager.getPackageInfo(options.get("package_name"), 0);
			} catch (NameNotFoundException e) {
				installed = false;
			}
			executeJavascript("OF.page.onIsInstalled("+installed+",'"+com.openfeint.internal.OpenFeintInternal.getAppStore()+"')");
		}

		public void downloadFromThe9GameCenter(final Map<String, String> options){
			String the9_store_download_url = options.get("the9_store_download_url");
			Uri data = Uri.parse(the9_store_download_url);
			Intent i = new Intent(Intent.ACTION_VIEW, data);
			mWebNav.startActivity(i);
		}
		
		public void InstalledGameStore(final Map<String, String> options) {
			// TODO
			new Thread(){public void run(){
				PackageManager manager = mWebNav.getPackageManager();
				String installedGameStoreString = "";
				String[] game_store_array = options.get("game_store").split(",");
				List<ApplicationInfo> installedApps = manager.getInstalledApplications(0);
				StringBuffer installedAppName = new StringBuffer();
				for (ApplicationInfo info : installedApps) {
					String name = (String) info.loadLabel(manager);
					installedAppName.append(name+"|");
				}
				
				String s = installedAppName.toString();
				for (int i = 0; i < game_store_array.length; i++) {
					String game_store_name = game_store_array[i];
					if(s.contains(game_store_name)){
						installedGameStoreString += game_store_name+"|";
					}
				}
				final String _installedGameStoreString = installedGameStoreString;
				WebNav.this.runOnUiThread(new Runnable(){
					@Override
					public void run() {
						executeJavascript("OF.page.renderGameStoreList('"+ _installedGameStoreString + "')");
					}});
			}}.start();
		}

		public void openYoutubePlayer(Map<String, String> options) {
			final String videoID = options.get("video_id");
			final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + videoID)); 
			
			 List<ResolveInfo> list = getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
			 if (list.size() == 0){
				 Toast.makeText(mWebNav, OpenFeintInternal.getRString(R.string.of_no_video), Toast.LENGTH_SHORT).show();
			 } else {
				 mWebNav.startActivity(intent);
			 }
		}

		final public void profilePicture(final Map<String,String> options) {
		    OpenFeintInternal.log("ForTest", "ActionHandler::profilePicture" );
			ImagePicker.show(WebNav.this);
		}

		public void openBrowser(Map<String, String> options) {
		    OpenFeintInternal.log("ForTest", "ActionHandler::openBrowser" );
			final Intent browserIntent = new Intent(mWebNav,
					NativeBrowser.class);
			mNativeBrowserParameters = new HashMap<String, String>();

			for (String arg : new String[] { "src", "callback", "on_cancel", "on_failure", "timeout"}) {
				final String val = options.get(arg);
				if (null != val) {
					// save it for the return...
					mNativeBrowserParameters.put(arg, val);
					// ... and send it to the browser.
					browserIntent.putExtra(NativeBrowser.INTENT_ARG_PREFIX
							+ arg, val);
				}
			}
			startActivityForResult(browserIntent, REQUEST_CODE_NATIVE_BROWSER);
		}

		public void downloadBlob(Map<String, String> options) {
			final String scoreJSON = options.get("score");
			final String onError = options.get("onError");
			final String onSuccess = options.get("onSuccess");

			try {
				JsonFactory jsonFactory = new JsonFactory(); // for thread
																// safety, we
																// make our own.
				JsonParser jp = jsonFactory.createJsonParser(new StringReader(
						scoreJSON));
				JsonResourceParser jrp = new JsonResourceParser(jp);
				Object scoreObject = jrp.parse();

				if (scoreObject != null && scoreObject instanceof Score) {
					final Score score = (Score) scoreObject;
					score.downloadBlob(new Score.DownloadBlobCB() {
						@Override
						public void onSuccess() {
							if (onSuccess != null) {
								executeJavascript(String.format("%s()",
										onSuccess));
							}
							ScoreBlobDelegate.notifyBlobDownloaded(score);
						}

						@Override
						public void onFailure(String exceptionMessage) {
							if (onError != null) {
								executeJavascript(String.format("%s(%s)",
										onError, exceptionMessage));
							}
						}
					});
				}
			} catch (Exception e) { // => JsonParseException, IOException
				if (onError != null) {
					executeJavascript(String.format("%s(%s)", onError, e
							.getLocalizedMessage()));
				}
			}

		}

		public void dashboard(Map<String, String> options) {
			Dashboard.openFromSpotlight();
		}
		
		public void requireLoginWithReturn(Map<String, String> options){
			final Intent i = new Intent(mWebNav,IntroFlow.class);
			final String redirectScreen = options.get("redirect_screen");
			final String toScreen = options.get("to_screen");
			String contentName = "login";
				
			if(toScreen  != null){
				contentName = toScreen;
			}
			
			if(redirectScreen  != null){
				i.putExtra("content_name", contentName + "?" + mapToString(options));  
			 } else {
				i.putExtra("content_name", contentName);
			 }
			
			OpenFeintInternal.log("ForTest","requirelogin made");
			mWebNav.startActivity(i);
		}
		
		public void openSpecifyDashboardScreen(Map<String, String> options){
			String screen_name = options.get("redirect_screen");
			screen_name = screen_name + "?" + mapToString(options);
			Dashboard.openSpecifyScreen(screen_name);
			OpenFeintInternal.log("ForTest","screen_name loaded  " + screen_name);
		}
		
		private static final String WEBUI_PREFS = "OFWebUI";
		private static final String WEBUI_SETTING_PREFIX = "OFWebUISetting_";

		public void readSetting(final Map<String, String> options) {
			final String k = options.get("key");
			final String cb = options.get("callback");
			if (cb != null) {
				final String key = (k != null ? (WEBUI_SETTING_PREFIX + k) : null);   
				final SharedPreferences prefs = OpenFeintInternal.getInstance().getContext().getSharedPreferences(WEBUI_PREFS, Context.MODE_PRIVATE);
				
				String val = prefs.getString(key, null);
				OpenFeintInternal.log(TAG, String.format("readSetting(%s) => %s", k, val));
				executeJavascript(String.format("%s(%s)", cb, val != null ? val : "null"));
			}
		}

		public void writeSetting(final Map<String, String> options) {
			final String k = options.get("key");
			final String v = options.get("value");
		    //OpenFeintInternal.log("ForTest", "ActionHandler::writeSetting::" + k + "::" + v );
			if (k != null && v != null) {
				final String key = WEBUI_SETTING_PREFIX + k;   
				final SharedPreferences.Editor editor = OpenFeintInternal.getInstance().getContext().getSharedPreferences(WEBUI_PREFS, Context.MODE_PRIVATE).edit();
				editor.putString(key, v);
				editor.commit();
			}
		}
	}

	private Map<String, String> mNativeBrowserParameters = null;

    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	super.onActivityResult(requestCode, resultCode, data);

	    OpenFeintInternal.log("ForTest", "WebNav::onActivityResult::requestCode="+requestCode+", resultCode="+resultCode );
   	
    	if (mNativeBrowserParameters != null && requestCode == REQUEST_CODE_NATIVE_BROWSER) {
	    	if (resultCode != Activity.RESULT_CANCELED) {
	    		
	    	    OpenFeintInternal.log("ForTest", "WebNav::onActivityResult::Temporarily, we don't want to refresh" );
	    		// Temporarily, we don't want to refresh.
	    		mShouldRefreshOnResume = false;
	    		
	    		if (data.getBooleanExtra(NativeBrowser.INTENT_ARG_PREFIX + "failed", false)) {
		    		final String cb = mNativeBrowserParameters.get("on_failure");
		    		if (cb != null) {
			    		final int code = data.getIntExtra(NativeBrowser.INTENT_ARG_PREFIX + "failure_code", 0);
			    		final String desc = data.getStringExtra(NativeBrowser.INTENT_ARG_PREFIX + "failure_desc");
		    			executeJavascript(String.format("%s(%d, %s)", cb, code, jsQuotedStringLiteral(desc)));
		    		}
	    		}
	    		else
	    		{
		    		final String cb = mNativeBrowserParameters.get("callback");
		    		if (cb != null) {
			    		final String rv = data.getStringExtra(NativeBrowser.INTENT_ARG_PREFIX + "result");
		    			executeJavascript(String.format("%s(%s)", cb, (rv != null ? rv : "")));
		    		}
	    		}
	    	} else {
	    		final String cb = mNativeBrowserParameters.get("on_cancel");
	    		if (cb != null) {
		    	    OpenFeintInternal.log("ForTest", "WebNav::onActivityResult::executeJavascript::" + cb );
	    			executeJavascript(String.format("%s()", cb));
	    		}
	    		else
	    		{
		    	    OpenFeintInternal.log("ForTest", "WebNav::onActivityResult::executeJavascript::null" );
	    		}
	    	}
	    	mNativeBrowserParameters = null;
    	} else if (ImagePicker.isImagePickerActivityResult(requestCode)) {
    		Bitmap image = ImagePicker.onImagePickerActivityResult(WebNav.this, resultCode, 50, data);
    		if (image != null) {
    			String apiPath = "/xp/users/"+ OpenFeintInternal.getInstance().getCurrentUser().resourceID() +"/profile_picture";
    			ImagePicker.compressAndUpload(image, apiPath, new OpenFeintInternal.IUploadDelegate() {
					@Override public void fileUploadedTo(String url, boolean success) {
						if (success) {
							// OF might not be there, the function might not be there, etc
							// so we try/catch.
							WebNav.this.executeJavascript("try { OF.page.onProfilePictureChanged('" + url + "'); } catch (e) {}");
						}
					}
				});
    		}
    	} else {
    	    OpenFeintInternal.log("ForTest", "WebNav::onActivityResult::hopefully a subclass handled it" );
    	       		// hopefully a subclass handled it
    	}
    }

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		OpenFeintDelegate d = OpenFeintInternal.getInstance().getDelegate();
		if (d != null) {
			if (hasFocus) {
				d.onDashboardAppear();
			} else {
				d.onDashboardDisappear();
			}
		}
	}

	@Override
	public void onDestroy() {
		mWebView.destroy();
		mWebView = null;
		super.onDestroy();
	}
}
