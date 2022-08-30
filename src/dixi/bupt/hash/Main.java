package dixi.bupt.hash;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created on 2022-08-29
 */
public class Main {
    public static void main(String[] args) {
        // Mess
        String original = "this is plaintext asdkjaskdhaskdhkashdaishdiashdisadhsad";
        String sign;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            md.update(original.getBytes());
            byte[] res = md.digest();

            System.out.println(byteArrayToHexString(res));
        } catch (NoSuchAlgorithmException e) {

        }

    }

    public static String byteArrayToHexString(byte[] b) {
        String result = "";
        for (int i=0; i < b.length; i++) {
            result +=
                    Integer.toString( ( b[i] & 0xff ) + 0x100, 16).substring( 1 );
        }
        return result;
    }
}
