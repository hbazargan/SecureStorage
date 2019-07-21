package ir.hbazargan.securestore.modules.encryption.keystore;

import java.security.KeyPair;

import javax.crypto.SecretKey;

import ir.hbazargan.securestore.modules.encryption.EncryptionException;

public interface KeyStoreContract {

    public KeyPair getAndroidKeyStoreAsymmetricKeyPair() throws EncryptionException;

    /**
     * @return symmetric key from Android Key Store or null if any key with given alias exists
     */
    public SecretKey getKeyStoreSymmetricKey() throws EncryptionException;

    /**
     * Remove key with given alias from Android Key Store
     */
    public void removeKeyStoreKey(String alias) throws EncryptionException;
}
