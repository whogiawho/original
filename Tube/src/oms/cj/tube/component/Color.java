/*
 * Copyright (C) 2008 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package oms.cj.tube.component;

import java.io.Serializable;

import android.util.Log;

public class Color implements Serializable{ 
	/**
	 * 
	 */
	private static final long serialVersionUID = -7147711065840644327L;
	
	private final static String TAG = "Color";
	public final static Color red = new Color(0.541f, 0.0f, 0.054f);
	public final static Color orange = new Color(1.0f, 0.27f, 0.0f);
	public final static Color yellow = new Color(0.996f, 0.816f, 0.0f);
	public final static Color white = new Color(0.745f, 0.745f, 0.749f);
	public final static Color blue = new Color(0.0f, 0.196f, 0.447f);
	public final static Color green = new Color(0.0f, 0.45f, 0.18f);
	
	public final static Color black = new Color(0.0f, 0,0f, 0.0f);
	public final static Color gray = new Color(0.517f, 0.517f, 0.517f);
	public final static Color transparent = new Color(1.0f, 1.0f, 1.0f, 0.0f);
	
	private final static int INTOFWHITE=-4342338;
	private final static int INTOFRED=-7798771;
	private final static int INTOFBLUE=-16764559;
	private final static int INTOFGREEN=-16747987;
	private final static int INTOFORANGE=-48128;
	private final static int INTOFYELLOW=-143360;
	
	public final static int INDEXOFYELLOW = 0;
	public final static int INDEXOFWHITE = 1;
	public final static int INDEXOFRED = 2;
	public final static int INDEXOFORANGE = 3;
	public final static int INDEXOFBLUE = 4;
	public final static int INDEXOFGREEN = 5;
	public final static Color[] standardCubeColors = {
		Color.yellow, 
		Color.white, 
		Color.red, 
		Color.orange, 
		Color.blue, 
		Color.green
	};
	
	public final float _red;
	public final float _green;
	public final float _blue;
	public final float _alpha;
	
	public Color(float _red, float _green, float _blue, float _alpha) {
		this._red = _red;
		this._green = _green;
		this._blue = _blue;
		this._alpha = _alpha;
	}

	public Color(float f, float g, float h) {
		this._red = f;
		this._green = g;
		this._blue = h;
		this._alpha = 1.0f;
	}
	
	public Color(int color){
		float i0 = color&0x000000ff;			//b
		float i1 = (color&0x0000ff00)>>8;		//g
		float i2 = (color&0x00ff0000)>>16;		//r
		float i3 = (color&0xff000000)>>24;		//a
		_red = i2/255f;
		_green = i1/255f;
		_blue = i0/255f;
		_alpha = i3/255f;
	}
	
	public boolean equals(Object other) {
		if (other instanceof Color) {
			Color color = (Color)other;
				return this.toInt() == color.toInt();
		}
		return false;
	}

	public String toString() {
		if(toInt()==INTOFRED)
			return "red";
		if(toInt()==INTOFWHITE)
			return "white";
		if(toInt()==INTOFGREEN)
			return "green";
		if(toInt()==INTOFORANGE)
			return "orange";
		if(toInt()==INTOFBLUE)
			return "blue";
		if(toInt()==INTOFYELLOW)
			return "yellow";
		return "black";
	}
	
	public int toInt() {
		int value=0;
		
		int i0 = (int)(_blue*255f);
		int i1 = (int)(_green*255f);
		int i2 = (int)(_red*255f);
		int i3 = (int)(_alpha*255f);
		value = i0|(i1<<8)|(i2<<16)|(i3<<24);
		
		return value;
	}
	public int getR(){
		return (toInt()&0x00ff0000)>>16;
	}
	public int getG(){
		return (toInt()&0x0000ff00)>>8;
	}
	public int getB(){
		return (toInt()&0x000000ff);
	}
	public static Color reverse(Color c){
		return new Color(1.0f-c._red, 1.0f-c._green, 1.0f-c._blue, c._alpha);
	}
	
// -----------------------------------------------------------------------------------
	private static Color[] definedCubeColors = {
		Color.yellow, 		//0
		Color.white, 
		Color.red, 			//2
		Color.orange, 		//3
		Color.blue, 
		Color.green
	};
	public static void setDefinedCubeColors(int idx, int color){
		if(idx<0||idx>=definedCubeColors.length){
			Log.e(TAG+".setDefinedCubeColors", "invalid idx = " + idx);
			return;
		}	
		
		definedCubeColors[idx] =  new Color(color);
	}
	
	public static Color closestColor(Color c){	
		Way way = new Way2();
		
		return way.closestColor(c);
	}
	
	private interface Way {
		public Color closestColor(Color c);	
	};
	
	public static class Way1 implements Way {
		
		@Override
		public Color closestColor(Color c){	
			double min = Double.MAX_VALUE;
			int colorIdx = 0;

			//phase1
			for(int i=0;i<definedCubeColors.length;i++){
				double current = ColorConversion.getRGBDistance(definedCubeColors[i], c);
				Log.i("Color.Way1"+".closestColor", "R="+definedCubeColors[i].getR()+";G="+definedCubeColors[i].getG()+";B="+definedCubeColors[i].getB());
				Log.i("Color.Way1"+".closestColor", "distance="+current);
				if(current<min){
					colorIdx = i;
					min = current;
				}
			}
			
			return standardCubeColors[colorIdx];
		}
	}
	
	public static class Way2 implements Way {
		
		@Override
		public Color closestColor(Color c){	
			double min = Double.MAX_VALUE;
			int colorIdx = 0;

			//phase1
			for(int i=0;i<definedCubeColors.length;i++){
				double current = ColorConversion.getRGBDistance(definedCubeColors[i], c);
				Log.i("Color.Way2"+".closestColor", "R="+definedCubeColors[i].getR()+";G="+definedCubeColors[i].getG()+";B="+definedCubeColors[i].getB());
				Log.i("Color.Way2"+".closestColor", "distance="+current);
				if(current<min){
					colorIdx = i;
					min = current;
				}
			}
			
			//phase2
			if(colorIdx==0||colorIdx==2||colorIdx==3){	//yellow£¬red£¬orange
				colorIdx = diffFromYRO(c);
			}
			return standardCubeColors[colorIdx];
		}
		
		private static double getRGRatio(Color c){
			if(c.getG()==0)
				return Double.MAX_VALUE-1;
			else 
				return c.getR()/c.getG();
		}
		private static int diffFromYRO(Color c){
			int[] YOIdx = {0, 2, 3};		//yellow red orange
			double cRGRatio = getRGRatio(c);
			int idx=0;
			
			double yRGRatio = getRGRatio(definedCubeColors[YOIdx[0]]);
			double oRGRatio = getRGRatio(definedCubeColors[YOIdx[2]]);
			double rRGRatio = getRGRatio(definedCubeColors[YOIdx[1]]);
			
			double limit0 = (yRGRatio + oRGRatio)/2;
			double limit1 = (oRGRatio + rRGRatio)/2;
			if(cRGRatio<limit0)
				idx = 0; 
			else if(cRGRatio>=limit0&&cRGRatio<limit1)
				idx = 2;
			else if(cRGRatio>=limit1)
				idx = 1;
			else 
				idx = 2;
			
			return YOIdx[idx];
		}
	}
	
	public static class Way3 implements Way {

		@Override
		public Color closestColor(Color c) {
			double min = Double.MAX_VALUE;
			int colorIdx = 0;

			//phase1
			for(int i=0;i<definedCubeColors.length;i++){
				double current = getHSVDistance(definedCubeColors[i], c);
				Log.i("Color.Way3"+".closestColor", "R="+definedCubeColors[i].getR()+";G="+definedCubeColors[i].getG()+";B="+definedCubeColors[i].getB());
				Log.i("Color.Way3"+".closestColor", "distance="+current);
				if(current<min){
					colorIdx = i;
					min = current;
				}
			}
			
			return standardCubeColors[colorIdx];
		}
		
		public static double getHSVDistance(Color c1, Color c2){
			double[] lab1 = ColorConversion.RGBtoHSV(c1.getR(), c1.getG(), c1.getB());
			double[] lab2 = ColorConversion.RGBtoHSV(c2.getR(), c2.getG(), c2.getB());
			
			double r1 = lab1[0] - lab2[0];
			double r2 = lab1[1] - lab2[1];
			double r3 = lab1[2] - lab2[2];
		
			return ColorConversion.distance(r1, r2, r3, ColorConversion.R1SET|ColorConversion.R2SET);
		}
	}
}
