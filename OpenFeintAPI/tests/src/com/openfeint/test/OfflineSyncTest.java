package com.openfeint.test;

import android.test.AndroidTestCase;

import com.openfeint.internal.offline.OfflineSupport.DB;
import com.openfeint.internal.offline.OfflineSupport.OfflineAchievement;
import com.openfeint.internal.offline.OfflineSupport.OfflineScore;

public class OfflineSyncTest extends AndroidTestCase {

    public void setUp() {
    }

    public void testOfflineScore() {
    	OfflineScore os = new OfflineScore();
    	os.score = 20;
    	os.leaderboardID = "1234";
    	
    	OfflineScore os2 = os.dup();
    	
    	assertTrue(null != os2);
    	assertTrue(os.eq(os2));
    	assertTrue(os != os2);
    	assertEquals(20, os2.score);
    	assertTrue(os2.leaderboardID.equals("1234"));
    }
    
    public void testOfflineAchievement() {
    	OfflineAchievement oa = new OfflineAchievement();
    	oa.clientCompletionPercentage = 40.0f;
    	oa.resourceID = "9001";

    	OfflineAchievement oa2 = oa.dup();
    	
    	assertTrue(null != oa2);
    	assertTrue(oa.eq(oa2));
    	assertTrue(oa != oa2);
    	assertEquals(40.0f, oa2.clientCompletionPercentage);
    	assertTrue("9001".equals(oa2.resourceID));
    }
    
    private boolean containsByValue(DB db, OfflineScore os) {
    	for (OfflineScore score : db.scores) {
			if (score.eq(os)) {
				return true;
			}
		}
    	return false;
    }
    
    private boolean containsByIdentity(DB db, OfflineScore os) {
    	for (OfflineScore score : db.scores) {
			if (score == os) {
				return true;
			}
		}
    	return false;
    }
    
    private boolean containsByValue(DB db, OfflineAchievement oa) {
    	for (OfflineAchievement ach : db.achievements) {
			if (ach.eq(oa)) {
				return true;
			}
		}
    	return false;
    }
    
    private boolean containsByIdentity(DB db, OfflineAchievement oa) {
    	for (OfflineAchievement ach : db.achievements) {
			if (ach == oa) {
				return true;
			}
		}
    	return false;
    }

    public void testClone() {
    	DB db = new DB();

    	OfflineScore os = new OfflineScore();
    	os.score = 20;
    	os.leaderboardID = "1234";
    	db.scores.add(os);
    	
    	OfflineScore os2 = new OfflineScore();
    	os2.score = 50;
    	os2.leaderboardID = "5678";
    	db.scores.add(os2);
    	
    	OfflineScore os3 = new OfflineScore();
    	os3.score = 80;
    	os3.leaderboardID = "5678";
    	db.scores.add(os3);
    	
    	OfflineAchievement oa = new OfflineAchievement();
    	oa.clientCompletionPercentage = 40.0f;
    	oa.resourceID = "9001";
    	db.achievements.add(oa);

    	DB db2 = db.dup();
    	assertTrue(db2 != null);
    	assertEquals(db.scores.size(), db2.scores.size());
    	assertEquals(db.achievements.size(), db2.achievements.size());
    	
    	for (OfflineScore localScore : db.scores) {
    		assertTrue(containsByValue(db2, localScore));
    		assertFalse(containsByIdentity(db2, localScore));
    	}

    	for (OfflineAchievement localAchievement : db.achievements) {
    		assertTrue(containsByValue(db2, localAchievement));
    		assertFalse(containsByIdentity(db2, localAchievement));
    	}
    }
    
    public void testMerge() {
    	DB db = new DB();

    	OfflineScore os = new OfflineScore();
    	os.score = 20;
    	os.leaderboardID = "1234";
    	db.scores.add(os);
    	
    	OfflineScore os2 = new OfflineScore();
    	os2.score = 50;
    	os2.leaderboardID = "5678";
    	db.scores.add(os2);
    	
    	OfflineAchievement oa = new OfflineAchievement();
    	oa.clientCompletionPercentage = 40.0f;
    	oa.resourceID = "9001";
    	db.achievements.add(oa);
    	
    	OfflineAchievement originalOA = oa.dup();  // save this for comparison later.
    	assertTrue(containsByValue(db, originalOA));

    	DB db2 = new DB();
    	
    	OfflineScore os3 = new OfflineScore();
    	os3.score = 80;
    	os3.leaderboardID = "5678";
    	db2.scores.add(os3);
    	
    	OfflineAchievement oa2 = new OfflineAchievement();
    	oa2.clientCompletionPercentage = 50.0f;
    	oa2.resourceID = "9001";
    	db2.achievements.add(oa2);

    	OfflineAchievement oa3 = new OfflineAchievement();
    	oa3.clientCompletionPercentage = 100.0f;
    	oa3.resourceID = "6789";
    	db2.achievements.add(oa3);

    	db.merge(db2);
    	assertEquals(3, db.scores.size());
    	assertEquals(2, db.achievements.size());
    	assertTrue(containsByValue(db, os));
    	assertTrue(containsByValue(db, os2));
    	assertTrue(containsByValue(db, os3));
    	assertFalse(containsByValue(db, originalOA)); // should be overwritten by oa2, since they're same resID
    	assertTrue(containsByValue(db, oa2));
    	assertTrue(containsByValue(db, oa3));
    }
    
    public void testUpdateOnUpload() {
    	DB db = new DB();

    	OfflineScore os = new OfflineScore();
    	os.score = 20;
    	os.leaderboardID = "1234";
    	db.scores.add(os);
    	
    	OfflineScore os2 = new OfflineScore();
    	os2.score = 50;
    	os2.leaderboardID = "5678";
    	db.scores.add(os2);
    	
    	OfflineAchievement oa = new OfflineAchievement();
    	oa.clientCompletionPercentage = 40.0f;
    	oa.resourceID = "9001";
    	db.achievements.add(oa);
    	
    	DB db2 = db.dup();
    	
    	OfflineScore os3 = new OfflineScore();
    	os3.score = 80;
    	os3.leaderboardID = "5678";
    	db.scores.add(os3);

    	// update ach 9001 in place from 40 to 50.
    	assertTrue(containsByIdentity(db, oa));
    	oa.clientCompletionPercentage = 50.f;

    	OfflineAchievement oa2 = new OfflineAchievement();
    	oa2.clientCompletionPercentage = 100.0f;
    	oa2.resourceID = "6789";
    	db.achievements.add(oa2);
    	
    	db.updateOnUpload(db2);
    	
    	assertEquals(1, db.scores.size());
    	assertFalse(containsByValue(db, os));
    	assertFalse(containsByValue(db, os2));
    	assertTrue(containsByValue(db, os3));
    	
    	assertEquals(2, db.achievements.size());
    	
    	// ach 9001 should be at client completion 50 (since we updated it while the sync
    	// was in progress) but at server completion 40 (because that's what we sent up).
    	OfflineAchievement shouldFind = new OfflineAchievement();
    	shouldFind.resourceID = "9001";
    	shouldFind.clientCompletionPercentage = 50.f;
    	shouldFind.serverCompletionPercentage = 40.f;
    	assertTrue(containsByValue(db, shouldFind));
    	
    	// ach 6789 should still be in the DB, but it should be at client 100, server 0
    	// (because it hadn't been added yet at the time we started the sync).
    	OfflineAchievement shouldFind2 = new OfflineAchievement();
    	shouldFind2.resourceID = "6789";
    	shouldFind2.clientCompletionPercentage = 100.f;
    	shouldFind2.serverCompletionPercentage = 0.f;
    	assertTrue(containsByValue(db, shouldFind2));
    	
    }
}
