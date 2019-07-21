package ir.hbazargan.securestore.modules.encryption;

interface EncryptionContract {

    String encrypt(String data) throws EncryptionException;

    String decrypt(String data) throws EncryptionException;
}
