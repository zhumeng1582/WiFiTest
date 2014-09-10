package com.example.wifitest;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class Des {
private static final String Algorithm = "DESede"; 
    
	public static byte[] encryptMode(byte[] keybyte, byte[] src) {
		
		try {
			SecretKey deskey = new SecretKeySpec(keybyte, Algorithm);
			Cipher c1 = Cipher.getInstance(Algorithm);
			c1.init(Cipher.ENCRYPT_MODE, deskey);
            return c1.doFinal(src);
        } catch (java.security.NoSuchAlgorithmException e1) {
        	e1.printStackTrace();
        } catch (javax.crypto.NoSuchPaddingException e2) {
        	e2.printStackTrace();
        } catch (java.lang.Exception e3) {
        	e3.printStackTrace();
        }
        return null;
    }

    public static byte[] decryptMode(byte[] keybyte, byte[] src) {      
	
    	try {
    		SecretKey deskey = new SecretKeySpec(keybyte, Algorithm);
    		Cipher c1 = Cipher.getInstance(Algorithm);
    		c1.init(Cipher.DECRYPT_MODE, deskey);
            return c1.doFinal(src);
        } catch (java.security.NoSuchAlgorithmException e1) {
            e1.printStackTrace();
        } catch (javax.crypto.NoSuchPaddingException e2) {
            e2.printStackTrace();
        } catch (java.lang.Exception e3) {
            e3.printStackTrace();
        }
        return null;
    }
    
    //转换成十六进制字符串
    
    public static String byte2hex(byte[] b) {
        String hs="";
        String stmp="";

        for (int n=0;n<b.length;n++) {
            stmp=(java.lang.Integer.toHexString(b[n] & 0XFF));
            if (stmp.length()==1) hs=hs+"0"+stmp;
            else hs=hs+stmp;
            //if (n<b.length-1)  hs=hs+":";
        }
        return hs.toUpperCase();
    }
}
