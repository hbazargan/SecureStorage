package ir.hbazargan.securestore.encryption;

import java.security.KeyPair;

import javax.crypto.SecretKey;

public interface KeyStoreHelperInterface {

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
