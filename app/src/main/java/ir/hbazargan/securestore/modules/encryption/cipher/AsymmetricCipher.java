package ir.hbazargan.securestore.modules.encryption.cipher;

import android.util.Base64;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.KeyPair;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;

import ir.hbazargan.securestore.modules.encryption.EncryptionException;

final class AsymmetricCipher extends Cipher {

    private static final String IV_SEPARATOR = "]";

    private KeyPair key;

    private AsymmetricCipher(KeyPair key, TransformationType transformationType) throws EncryptionException
    {
        super(transformationType);
        this.key = key;
    }

    @Override
    public String encrypt(String data) throws EncryptionException
    {

        try
        {
            cipher.init(javax.crypto.Cipher.ENCRYPT_MODE, key.getPublic());

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
            cipher.init(javax.crypto.Cipher.DECRYPT_MODE, key.getPrivate());
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
        public CipherContract build() throws EncryptionException
        {
            return new AsymmetricCipher(key, TransformationType.TRANSFORMATION_ASYMMETRIC);
        }
    }
}