package ir.hbazargan.securestore.encryption;

public interface CipherHelperInterface {

    String encrypt(String data) throws EncryptionException;

    String decrypt(String data) throws EncryptionException;
}
