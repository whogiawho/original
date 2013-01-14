package com.openfeint.test;

import android.test.AndroidTestCase;

import com.openfeint.api.resource.CloudStorage;

public class CloudStorageTest extends AndroidTestCase {

    public void setUp() {
    }

    public void testValidKeys() {
    	assertTrue(CloudStorage.isValidKey("buddy"));
    	assertTrue(CloudStorage.isValidKey("Kitty"));
    	assertTrue(CloudStorage.isValidKey("PUPPY"));
    	assertTrue(CloudStorage.isValidKey("I-Am-Totally-_EMPHATIC_-About-My-CloudStorage-Name"));
    	assertTrue(CloudStorage.isValidKey("SaveSlot12"));
    	assertTrue(CloudStorage.isValidKey("X___X---X___"));
    	
    	assertFalse(CloudStorage.isValidKey("0day"));
    	assertFalse(CloudStorage.isValidKey("Save Slot 12"));
    	assertFalse(CloudStorage.isValidKey("ûÆú"));
    	
    	assertFalse(CloudStorage.isValidKey(null));
    }
    
}
