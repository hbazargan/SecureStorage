package ir.hbazargan.securestore.encryption;

import java.security.KeyPair;

import javax.crypto.SecretKey;

public final class CipherFactory {
    public static CipherHelper create(KeyStoreHelperInterface keyStoreHelperInterface, AlgorithmType algorithmType) throws EncryptionException
    {
        CipherHelperInterface cipherHelperInterface = null;
        switch (algorithmType){
            case SYMMETRIC:
                SecretKey secretKey = keyStoreHelperInterface.getKeyStoreSymmetricKey();
                cipherHelperInterface = new SymmetricCipherHelper.
                        Builder(secretKey).build();
                break;
            case ASYMMETRIC:
                KeyPair keyPair = keyStoreHelperInterface.getAndroidKeyStoreAsymmetricKeyPair();
                cipherHelperInterface = new AsymmetricCipherHelper.Builder(keyPair).build();
                break;
        }
        if(cipherHelperInterface==null)
            throw new EncryptionException();
        return (CipherHelper) cipherHelperInterface;
    }
}
