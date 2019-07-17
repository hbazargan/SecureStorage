package ir.hbazargan.securestore.encryption;

import android.content.Context;
import android.os.Build;

import java.security.KeyPair;

import javax.crypto.SecretKey;

public final class EncryptionHelper implements EncryptionHelperInterface, Encryption {

    @Override
    public boolean init()
    {
        return true;
    }

    @Override
    public String encrypt(String key, String value) throws Exception
    {
        return encrypt(value);
    }

    @Override
    public String decrypt(String key, String value) throws Exception
    {
        return decrypt(value);
    }
    private CipherHelperInterface cipherHelperInterface;
    private KeyStoreHelperInterface keyStoreHelperInterface;

    private EncryptionHelper(Context context, AlgorithmType algorithmType) throws EncryptionException
    {
        keyStoreHelperInterface = new KeyStoreHelper.Builder(context).build();
        cipherHelperInterface = CipherFactory.create(keyStoreHelperInterface, algorithmType);
    }

    @Override
    public String encrypt(String data) throws EncryptionException
    {
        return cipherHelperInterface.encrypt(data);
    }

    @Override
    public String decrypt(String data) throws EncryptionException
    {
        return cipherHelperInterface.decrypt(data);
    }

    public static class Builder
    {
        private Context context;
        private AlgorithmType algorithmType = AlgorithmType.SYMMETRIC;

        public Builder(Context context, AlgorithmType algorithmType)
        {
            this.context = context;
            this.algorithmType = algorithmType;
        }

        public EncryptionHelperInterface build() throws EncryptionException
        {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2)
            {
                return new EncryptionHelper(context, algorithmType);
            }else{
                throw new EncryptionException();
            }
        }
    }
}
