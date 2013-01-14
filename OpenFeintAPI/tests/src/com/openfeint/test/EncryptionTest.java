package com.openfeint.test;

import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;

import android.test.AndroidTestCase;

import com.openfeint.internal.Encryption;

public class EncryptionTest extends AndroidTestCase {
    
    public void testEncryption() throws IllegalBlockSizeException, BadPaddingException {
        // SharedPreferences savedSession = this.getSharedPreferences("save", Context.MODE_PRIVATE);
        //byte[] old = savedSession.g
        Encryption.init("pass");
        String testStr = "test";
        byte[] b = testStr.getBytes();
        
        byte[] en = Encryption.encrypt(b);
        byte[] de = Encryption.decrypt(en);
        assertTrue(Arrays.equals(de, b));
    }
}
