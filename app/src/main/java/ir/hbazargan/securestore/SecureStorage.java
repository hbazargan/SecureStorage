package ir.hbazargan.securestore;

import android.content.Context;

import com.google.gson.Gson;

import ir.hbazargan.securestore.contracts.ConverterModuleContract;
import ir.hbazargan.securestore.contracts.EncryptionModuleContract;
import ir.hbazargan.securestore.contracts.SerializerModuleContract;
import ir.hbazargan.securestore.contracts.StorageModuleContract;
import ir.hbazargan.securestore.modules.ConverterModuleImplementation;
import ir.hbazargan.securestore.modules.encryption.NoEncryptionModuleImplementation;
import ir.hbazargan.securestore.modules.encryption.cipher.AlgorithmType;
import ir.hbazargan.securestore.modules.encryption.EncryptionException;
import ir.hbazargan.securestore.modules.encryption.KeyStoreEncryptionModuleImplementation;
import ir.hbazargan.securestore.contracts.LogInterceptorModuleContract;
import ir.hbazargan.securestore.modules.GsonParserModuleImplementation;
import ir.hbazargan.securestore.contracts.ParserModuleContract;
import ir.hbazargan.securestore.modules.SerializerModuleImplementation;
import ir.hbazargan.securestore.modules.SharedPreferencesStorageModuleImplementation;

/**
 * Secure, simple key-value storage for Android.
 */
public final class SecureStorage implements SecureStorageContract {

  private SecureStorage() {
    // no instance
  }

  private SecureStorageContract secureStorageContract = new SecureStorageContract.EmptySecureStorageContract();

  /**
   * This will init the hawk without password protection.
   *
   * @param context is used to instantiate context based objects.
   *                ApplicationContext will be used
   * @param algorithmType is used to define encryptionModuleContract algorithm.
   *
   */
  private SecureStorage(Context context, AlgorithmType algorithmType, SecureStorageBuilder secureStorageBuilder) {
    Utils.checkNull("Context", context);
    secureStorageContract = null;
    secureStorageContract = new DefaultSecureStorageContract(secureStorageBuilder);
  }

  /**
   * Saves any type including any collection, primitive values or custom objects
   *
   * @param key   is required to differentiate the given data
   * @param value is the data that is going to be encrypted and persisted
   * @return true if the operation is successful. Any failure in any step will return false
   */
  @Override
  public <T> boolean put(String key, T value) {
    return secureStorageContract.put(key, value);
  }

  /**
   * Gets the original data along with original type by the given key.
   * This is not guaranteed operation since SecureStorage uses serialization. Any change in in the requested
   * data type might affect the result. It's guaranteed to return primitive types and String type
   *
   * @param key is used to get the persisted data
   * @return the original object
   */
  @Override
  public <T> T get(String key) {
    return secureStorageContract.get(key);
  }

  /**
   * Gets the saved data, if it is null, default value will be returned
   *
   * @param key          is used to get the saved data
   * @param defaultValue will be return if the response is null
   * @return the saved object
   */
  @Override
  public <T> T get(String key, T defaultValue) {
    return secureStorageContract.get(key, defaultValue);
  }

  /**
   * Size of the saved data. Each key will be counted as 1
   *
   * @return the size
   */
  @Override
  public long count() {
    return secureStorageContract.count();
  }

  /**
   * Clears the storage, note that crypto data won't be deleted such as salt key etc.
   * Use resetCrypto in order to deleteAll crypto information
   *
   * @return true if deleteAll is successful
   */
  @Override
  public boolean deleteAll() {
    return secureStorageContract.deleteAll();
  }

  /**
   * Removes the given key/value from the storage
   *
   * @param key is used for removing related data from storage
   * @return true if delete is successful
   */
  @Override
  public boolean delete(String key) {
    return secureStorageContract.delete(key);
  }

  /**
   * Checks the given key whether it exists or not
   *
   * @param key is the key to check
   * @return true if it exists in the storage
   */
  @Override
  public boolean contains(String key) {
    return secureStorageContract.contains(key);
  }

  /**
   * Use this method to verify if SecureStorage is ready to be used.
   *
   * @return true if correctly initialised and built. False otherwise.
   */
  @Override
  public boolean isBuilt() {
    return secureStorageContract.isBuilt();
  }

  @Override
  public void destroy() {
    secureStorageContract.destroy();
  }

  public static final class SecureStorageBuilder {

    /**
     * NEVER ever change STORAGE_TAG_DO_NOT_CHANGE and TAG_INFO.
     * It will break backward compatibility in terms of keeping previous data
     */
    private static final String STORAGE_TAG_DO_NOT_CHANGE = "SecureStorage1";

    private Context context;
    private AlgorithmType algorithm;
    private StorageModuleContract cryptoStorageModuleContract;
    private ConverterModuleContract converterModuleContract;
    private ParserModuleContract parserModuleContract;
    private EncryptionModuleContract encryptionModuleContract;
    private SerializerModuleContract serializerModuleContract;
    private LogInterceptorModuleContract logInterceptorModuleContract;

    public SecureStorageBuilder(Context context) {
      this(context, AlgorithmType.SYMMETRIC);
    }

    public SecureStorageBuilder(Context context, AlgorithmType algorithmType) {
      Utils.checkNull("Context", context);

      this.context = context.getApplicationContext();
      this.algorithm = algorithmType;
    }

    public SecureStorageBuilder setStorage(StorageModuleContract storageModuleContract) {
      this.cryptoStorageModuleContract = storageModuleContract;
      return this;
    }

    public SecureStorageBuilder setAlgorithm(AlgorithmType algorithm) {
      this.algorithm = algorithm;
      return this;
    }

    public SecureStorageBuilder setParserModuleContract(ParserModuleContract parserModuleContract) {
      this.parserModuleContract = parserModuleContract;
      return this;
    }

    public SecureStorageBuilder setSerializerModuleContract(SerializerModuleContract serializerModuleContract) {
      this.serializerModuleContract = serializerModuleContract;
      return this;
    }

    public SecureStorageBuilder setLogInterceptorModuleContract(LogInterceptorModuleContract logInterceptorModuleContract) {
      this.logInterceptorModuleContract = logInterceptorModuleContract;
      return this;
    }

    public SecureStorageBuilder setConverterModuleContract(ConverterModuleContract converterModuleContract) {
      this.converterModuleContract = converterModuleContract;
      return this;
    }

    public SecureStorageBuilder setEncryptionModuleContract(EncryptionModuleContract encryptionModuleContract) {
      this.encryptionModuleContract = encryptionModuleContract;
      return this;
    }

    LogInterceptorModuleContract getLogInterceptorModuleContract() {
      if (logInterceptorModuleContract == null) {
        logInterceptorModuleContract = new LogInterceptorModuleContract() {
          @Override
          public void onLog(String message) {
            //empty implementation
          }
        };
      }
      return logInterceptorModuleContract;
    }

    StorageModuleContract getStorage() {
      if (cryptoStorageModuleContract == null) {
        cryptoStorageModuleContract = new SharedPreferencesStorageModuleImplementation(context, STORAGE_TAG_DO_NOT_CHANGE);
      }
      return cryptoStorageModuleContract;
    }

    ConverterModuleContract getConverterModuleContract() {
      if (converterModuleContract == null) {
        converterModuleContract = new ConverterModuleImplementation(getParserModuleContract());
      }
      return converterModuleContract;
    }

    ParserModuleContract getParserModuleContract() {
      if (parserModuleContract == null) {
        parserModuleContract = new GsonParserModuleImplementation(new Gson());
      }
      return parserModuleContract;
    }

    EncryptionModuleContract getEncryptionModuleContract() {
      if (encryptionModuleContract == null) {
        try
        {
          encryptionModuleContract = (EncryptionModuleContract) new KeyStoreEncryptionModuleImplementation.Builder(context,algorithm).build();
        }
        catch (EncryptionException e)
        {
          e.printStackTrace();
          encryptionModuleContract = new NoEncryptionModuleImplementation();
        }
      }
      return encryptionModuleContract;
    }

    SerializerModuleContract getSerializerModuleContract() {
      if (serializerModuleContract == null) {
        serializerModuleContract = new SerializerModuleImplementation(getLogInterceptorModuleContract());
      }
      return serializerModuleContract;
    }

    public SecureStorageContract build() {
      return new SecureStorage(context, algorithm, this);
    }
  }
}
