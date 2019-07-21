package ir.hbazargan.securestore.modules.encryption.cipher;

import ir.hbazargan.securestore.modules.encryption.EncryptionException;

public interface CipherContract {

    String encrypt(String data) throws EncryptionException;

    String decrypt(String data) throws EncryptionException;
}
