package ir.hbazargan.securestore;

import android.content.Context;

import com.google.gson.Gson;

import ir.hbazargan.securestore.converter.Converter;
import ir.hbazargan.securestore.converter.SecureStorageConverter;
import ir.hbazargan.securestore.encryption.AlgorithmType;
import ir.hbazargan.securestore.encryption.Encryption;
import ir.hbazargan.securestore.encryption.EncryptionException;
import ir.hbazargan.securestore.encryption.EncryptionHelper;
import ir.hbazargan.securestore.encryption.NoEncryption;
import ir.hbazargan.securestore.logger.LogInterceptor;
import ir.hbazargan.securestore.parser.GsonParser;
import ir.hbazargan.securestore.parser.Parser;
import ir.hbazargan.securestore.serializer.SecureStorageSerializer;
import ir.hbazargan.securestore.serializer.Serializer;
import ir.hbazargan.securestore.storage.SharedPreferencesStorage;
import ir.hbazargan.securestore.storage.Storage;

public final class SecureStorageBuilder {

  /**
   * NEVER ever change STORAGE_TAG_DO_NOT_CHANGE and TAG_INFO.
   * It will break backward compatibility in terms of keeping previous data
   */
  private static final String STORAGE_TAG_DO_NOT_CHANGE = "SecureStorage1";
  private final AlgorithmType algorithmType;

  private Context context;
  private Storage cryptoStorage;
  private Converter converter;
  private Parser parser;
  private Encryption encryption;
  private Serializer serializer;
  private LogInterceptor logInterceptor;

  public SecureStorageBuilder(Context context, AlgorithmType algorithmType) {
    Utils.checkNull("Context", context);

    this.context = context.getApplicationContext();
    this.algorithmType = algorithmType;
  }

  public SecureStorageBuilder setStorage(Storage storage) {
    this.cryptoStorage = storage;
    return this;
  }

  public SecureStorageBuilder setParser(Parser parser) {
    this.parser = parser;
    return this;
  }

  public SecureStorageBuilder setSerializer(Serializer serializer) {
    this.serializer = serializer;
    return this;
  }

  public SecureStorageBuilder setLogInterceptor(LogInterceptor logInterceptor) {
    this.logInterceptor = logInterceptor;
    return this;
  }

  public SecureStorageBuilder setConverter(Converter converter) {
    this.converter = converter;
    return this;
  }

  public SecureStorageBuilder setEncryption(Encryption encryption) {
    this.encryption = encryption;
    return this;
  }

  LogInterceptor getLogInterceptor() {
    if (logInterceptor == null) {
      logInterceptor = new LogInterceptor() {
        @Override
        public void onLog(String message) {
          //empty implementation
        }
      };
    }
    return logInterceptor;
  }

  Storage getStorage() {
    if (cryptoStorage == null) {
      cryptoStorage = new SharedPreferencesStorage(context, STORAGE_TAG_DO_NOT_CHANGE);
    }
    return cryptoStorage;
  }

  Converter getConverter() {
    if (converter == null) {
      converter = new SecureStorageConverter(getParser());
    }
    return converter;
  }

  Parser getParser() {
    if (parser == null) {
      parser = new GsonParser(new Gson());
    }
    return parser;
  }

  Encryption getEncryption() {
    if (encryption == null) {
      try
      {
        encryption = (Encryption) new EncryptionHelper.Builder(context,algorithmType).build();
      }
      catch (EncryptionException e)
      {
        e.printStackTrace();
        encryption = new NoEncryption();
      }
    }
    return encryption;
  }

  Serializer getSerializer() {
    if (serializer == null) {
      serializer = new SecureStorageSerializer(getLogInterceptor());
    }
    return serializer;
  }

  public void build() {
    SecureStorage.build(this);
  }
}
