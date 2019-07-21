package ir.hbazargan.securestore.modules.encryption.cipher;

import java.security.NoSuchAlgorithmException;

import javax.crypto.NoSuchPaddingException;

import ir.hbazargan.securestore.modules.encryption.EncryptionException;

abstract class Cipher implements CipherContract {

    public enum TransformationType{
        TRANSFORMATION_ASYMMETRIC("RSA/ECB/PKCS1Padding"),
        TRANSFORMATION_SYMMETRIC("AES/CBC/PKCS7Padding");
        private String value;

        TransformationType(String value)
        {
            this.value = value;
        }

        public String getValue()
        {
            return value;
        }
    }

    protected static final String IV_SEPARATOR = "]";
    protected TransformationType transformationType;
    protected javax.crypto.Cipher cipher;

    protected Cipher(TransformationType transformationType) throws EncryptionException
    {
        this.transformationType = transformationType;
        try
        {
            this.cipher = javax.crypto.Cipher.getInstance(this.transformationType.getValue());
        }
        catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
            throw new EncryptionException().withStackTrace(e.getStackTrace());
        }
        catch (NoSuchPaddingException e)
        {
            e.printStackTrace();
            throw new EncryptionException().withStackTrace(e.getStackTrace());
        }
    }

    @Override
    public abstract String encrypt(String data) throws EncryptionException;

    @Override
    public abstract String decrypt(String data) throws EncryptionException;
}
