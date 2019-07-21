package ir.hbazargan.securestore.modules.encryption.cipher;

import java.security.KeyPair;

import javax.crypto.SecretKey;

import ir.hbazargan.securestore.modules.encryption.EncryptionException;
import ir.hbazargan.securestore.modules.encryption.keystore.KeyStoreContract;

public final class CipherFactory {
    public static Cipher create(KeyStoreContract keyStoreContract, AlgorithmType algorithmType) throws EncryptionException
    {
        CipherContract cipherContract = null;
        switch (algorithmType){
            case SYMMETRIC:
                SecretKey secretKey = keyStoreContract.getKeyStoreSymmetricKey();
                cipherContract = new SymmetricCipher.
                        Builder(secretKey).build();
                break;
            case ASYMMETRIC:
                KeyPair keyPair = keyStoreContract.getAndroidKeyStoreAsymmetricKeyPair();
                cipherContract = new AsymmetricCipher.Builder(keyPair).build();
                break;
        }
        if(cipherContract ==null)
            throw new EncryptionException();
        return (Cipher) cipherContract;
    }
}
