package ir.hbazargan.securestore.modules.encryption;

import android.content.Context;
import android.os.Build;

import ir.hbazargan.securestore.contracts.EncryptionModuleContract;
import ir.hbazargan.securestore.modules.encryption.cipher.AlgorithmType;
import ir.hbazargan.securestore.modules.encryption.cipher.CipherFactory;
import ir.hbazargan.securestore.modules.encryption.cipher.CipherContract;
import ir.hbazargan.securestore.modules.encryption.keystore.KeyStoreImplementation;
import ir.hbazargan.securestore.modules.encryption.keystore.KeyStoreContract;

public final class EncryptionModuleImplementation implements EncryptionContract, EncryptionModuleContract {

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
    private CipherContract cipherContract;
    private KeyStoreContract keyStoreContract;

    private EncryptionModuleImplementation(Context context, AlgorithmType algorithmType) throws EncryptionException
    {
        keyStoreContract = new KeyStoreImplementation.Builder(context).build();
        cipherContract = CipherFactory.create(keyStoreContract, algorithmType);
    }

    @Override
    public String encrypt(String data) throws EncryptionException
    {
        return cipherContract.encrypt(data);
    }

    @Override
    public String decrypt(String data) throws EncryptionException
    {
        return cipherContract.decrypt(data);
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

        public EncryptionContract build() throws EncryptionException
        {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2)
            {
                return new EncryptionModuleImplementation(context, algorithmType);
            }else{
                throw new EncryptionException();
            }
        }
    }
}
