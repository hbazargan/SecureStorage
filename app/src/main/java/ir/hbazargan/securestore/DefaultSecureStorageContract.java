package ir.hbazargan.securestore;

import ir.hbazargan.securestore.contracts.ConverterModuleContract;
import ir.hbazargan.securestore.contracts.EncryptionModuleContract;
import ir.hbazargan.securestore.contracts.LogInterceptorModuleContract;
import ir.hbazargan.securestore.contracts.SerializerModuleContract;
import ir.hbazargan.securestore.contracts.StorageModuleContract;

final class DefaultSecureStorageContract implements SecureStorageContract {

  private final StorageModuleContract storageModuleContract;
  private final ConverterModuleContract converterModuleContract;
  private final EncryptionModuleContract encryptionModuleContract;
  private final SerializerModuleContract serializerModuleContract;
  private final LogInterceptorModuleContract logInterceptorModuleContract;

  public DefaultSecureStorageContract(SecureStorage.SecureStorageBuilder builder) {
    encryptionModuleContract = builder.getEncryptionModuleContract();
    storageModuleContract = builder.getStorage();
    converterModuleContract = builder.getConverterModuleContract();
    serializerModuleContract = builder.getSerializerModuleContract();
    logInterceptorModuleContract = builder.getLogInterceptorModuleContract();

    logInterceptorModuleContract.onLog("SecureStorage.init -> EncryptionModuleContract : " + encryptionModuleContract.getClass().getSimpleName());
  }

  @Override
  public <T> boolean put(String key, T value) {
    // Validate
    Utils.checkNull("Key", key);
    log("SecureStorage.put -> key: " + key + ", value: " + value);

    // If the value is null, delete it
    if (value == null) {
      log("SecureStorage.put -> Value is null. Any existing value will be deleted with the given key");
      return delete(key);
    }

    // 1. Convert to text
    String plainText = converterModuleContract.toString(value);
    log("SecureStorage.put -> Converted to " + plainText);
    if (plainText == null) {
      log("SecureStorage.put -> ConverterModuleContract failed");
      return false;
    }

    // 2. Encrypt the text
    String cipherText = null;
    try {
      cipherText = encryptionModuleContract.encrypt(key, plainText);
      log("SecureStorage.put -> Encrypted to " + cipherText);
    } catch (Exception e) {
      e.printStackTrace();
    }
    if (cipherText == null) {
      log("SecureStorage.put -> EncryptionModuleContract failed");
      return false;
    }

    // 3. Serialize the given object along with the cipher text
    String serializedText = serializerModuleContract.serialize(cipherText, value);
    log("SecureStorage.put -> Serialized to " + serializedText);
    if (serializedText == null) {
      log("SecureStorage.put -> Serialization failed");
      return false;
    }

    // 4. Save to the storageModuleContract
    if (storageModuleContract.put(key, serializedText)) {
      log("SecureStorage.put -> Stored successfully");
      return true;
    } else {
      log("SecureStorage.put -> Store operation failed");
      return false;
    }
  }

  @Override
  public <T> T get(String key) {
    log("SecureStorage.get -> key: " + key);
    if (key == null) {
      log("SecureStorage.get -> null key, returning null value ");
      return null;
    }

    // 1. Get serialized text from the storageModuleContract
    String serializedText = storageModuleContract.get(key);
    log("SecureStorage.get -> Fetched from storageModuleContract : " + serializedText);
    if (serializedText == null) {
      log("SecureStorage.get -> Fetching from storageModuleContract failed");
      return null;
    }

    // 2. Deserialize
    DataInfo dataInfo = serializerModuleContract.deserialize(serializedText);
    log("SecureStorage.get -> Deserialized");
    if (dataInfo == null) {
      log("SecureStorage.get -> Deserialization failed");
      return null;
    }

    // 3. Decrypt
    String plainText = null;
    try {
      plainText = encryptionModuleContract.decrypt(key, dataInfo.cipherText);
      log("SecureStorage.get -> Decrypted to : " + plainText);
    } catch (Exception e) {
      log("SecureStorage.get -> Decrypt failed: " + e.getMessage());
    }
    if (plainText == null) {
      log("SecureStorage.get -> Decrypt failed");
      return null;
    }

    // 4. Convert the text to original data along with original type
    T result = null;
    try {
      result = converterModuleContract.fromString(plainText, dataInfo);
      log("SecureStorage.get -> Converted to : " + result);
    } catch (Exception e) {
      log("SecureStorage.get -> ConverterModuleContract failed");
    }

    return result;
  }

  @Override
  public <T> T get(String key, T defaultValue) {
    T t = get(key);
    if (t == null) return defaultValue;
    return t;
  }

  @Override
  public long count() {
    return storageModuleContract.count();
  }

  @Override
  public boolean deleteAll() {
    return storageModuleContract.deleteAll();
  }

  @Override
  public boolean delete(String key) {
    return storageModuleContract.delete(key);
  }

  @Override
  public boolean contains(String key) {
    return storageModuleContract.contains(key);
  }

  @Override
  public boolean isBuilt() {
    return true;
  }

  @Override
  public void destroy() {
  }

  private void log(String message) {
    logInterceptorModuleContract.onLog(message);
  }
}
