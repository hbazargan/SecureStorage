package ir.hbazargan.securestore.modules;

import android.content.Context;
import android.content.SharedPreferences;

import ir.hbazargan.securestore.Utils;
import ir.hbazargan.securestore.contracts.StorageModuleContract;

public final class SharedPreferencesStorageModuleImplementation implements StorageModuleContract {

  private final SharedPreferences preferences;

  public SharedPreferencesStorageModuleImplementation(Context context, String tag) {
    preferences = context.getSharedPreferences(tag, Context.MODE_PRIVATE);
  }

  SharedPreferencesStorageModuleImplementation(SharedPreferences preferences) {
    this.preferences = preferences;
  }

  @Override
  public <T> boolean put(String key, T value) {
    Utils.checkNull("key", key);
    return getEditor().putString(key, String.valueOf(value)).commit();
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> T get(String key) {
    return (T) preferences.getString(key, null);
  }

  @Override
  public boolean delete(String key) {
    return getEditor().remove(key).commit();
  }

  @Override
  public boolean contains(String key) {
    return preferences.contains(key);
  }

  @Override
  public boolean deleteAll() {
    return getEditor().clear().commit();
  }

  @Override
  public long count() {
    return preferences.getAll().size();
  }

  private SharedPreferences.Editor getEditor() {
    return preferences.edit();
  }

}
