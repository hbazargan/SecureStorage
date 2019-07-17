package ir.hbazargan.securestore.encryption;

import android.util.Base64;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.KeyPair;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;

public final class AsymmetricCipherHelper extends CipherHelper {

    private static final String IV_SEPARATOR = "]";

    private KeyPair key;

    private AsymmetricCipherHelper(KeyPair key, TransformationType transformationType) throws EncryptionException
    {
        super(transformationType);
        this.key = key;
    }

    @Override
    public String encrypt(String data) throws EncryptionException
    {

        try
        {
            cipher.init(Cipher.ENCRYPT_MODE, key.getPublic());

            String result = "";

            byte[] bytes = cipher.doFinal(data.getBytes());
            result += Base64.encodeToString(bytes, Base64.DEFAULT);

            return result;
        }
        catch (InvalidKeyException e)
        {
            e.printStackTrace();
            throw new EncryptionException().withStackTrace(e.getStackTrace());
        }
        catch (BadPaddingException e)
        {
            e.printStackTrace();
            throw new EncryptionException().withStackTrace(e.getStackTrace());
        }
        catch (IllegalBlockSizeException e)
        {
            e.printStackTrace();
            throw new EncryptionException().withStackTrace(e.getStackTrace());
        }
    }

    @Override
    public String decrypt(String data) throws EncryptionException
    {
        String encodedString;


        encodedString = data;
        try
        {
            cipher.init(Cipher.DECRYPT_MODE, key.getPrivate());
        }
        catch (InvalidKeyException e)
        {
            e.printStackTrace();
            throw new EncryptionException().withStackTrace(e.getStackTrace());
        }

        byte[] encryptedData = Base64.decode(encodedString, Base64.DEFAULT);
        byte[] decodedData = new byte[0];
        String s;
        try
        {
            decodedData = cipher.doFinal(encryptedData);
            s = new String(decodedData, "UTF-8");
        }
        catch (IllegalBlockSizeException e)
        {
            e.printStackTrace();
            throw new EncryptionException().withStackTrace(e.getStackTrace());
        }
        catch (BadPaddingException e)
        {
            e.printStackTrace();
            throw new EncryptionException().withStackTrace(e.getStackTrace());
        }
        catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
            throw new EncryptionException().withStackTrace(e.getStackTrace());
        }
        return s;
    }

    public static class Builder
    {
        private KeyPair key;

        public Builder(KeyPair key)
        {
            this.key = key;
        }
        public CipherHelperInterface build() throws EncryptionException
        {
            return new AsymmetricCipherHelper(key, TransformationType.TRANSFORMATION_ASYMMETRIC);
        }
    }
}