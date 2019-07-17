package ir.hbazargan.securestore.encryption;

import android.util.Base64;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

public final class SymmetricCipherHelper extends CipherHelper {

    private static final String IV_SEPARATOR = "]";

    private SecretKey key;

    private SymmetricCipherHelper(SecretKey key, TransformationType transformationType) throws EncryptionException
    {
        super(transformationType);
        this.key = key;
    }

    @Override
    public String encrypt(String data) throws EncryptionException
    {

        try
        {
            cipher.init(Cipher.ENCRYPT_MODE, key);

            String result = "";

            byte[] iv = cipher.getIV();
            String ivString = Base64.encodeToString(iv, Base64.DEFAULT);
            result = ivString + IV_SEPARATOR;

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

        String[] split = data.split(IV_SEPARATOR);
        if (split.length != 2)
//                throw IllegalArgumentException("Passed data is incorrect. There was no IV specified with it.")
            throw new EncryptionException();

        String ivString = split[0];
        encodedString = split[1];
        IvParameterSpec ivSpec = new IvParameterSpec(Base64.decode(ivString, Base64.DEFAULT));
        try
        {
            cipher.init(Cipher.DECRYPT_MODE, key, ivSpec);
        }
        catch (InvalidKeyException e)
        {
            e.printStackTrace();
            throw new EncryptionException().withStackTrace(e.getStackTrace());
        }
        catch (InvalidAlgorithmParameterException e)
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
        private SecretKey key;

        public Builder(SecretKey key)
        {
            this.key = key;
        }
        public CipherHelperInterface build() throws EncryptionException
        {
            return new SymmetricCipherHelper(key, TransformationType.TRANSFORMATION_SYMMETRIC);
        }
    }
}