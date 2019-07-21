package ir.hbazargan.securestore.modules;

import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ir.hbazargan.securestore.DataInfo;
import ir.hbazargan.securestore.Utils;
import ir.hbazargan.securestore.contracts.ConverterModuleContract;
import ir.hbazargan.securestore.contracts.ParserModuleContract;

/**
 * Concrete implementation of encoding and decoding.
 * List types will be encoded/decoded by parserModuleContract
 * Serializable types will be encoded/decoded object stream
 * Not serializable objects will be encoded/decoded by parserModuleContract
 */
public final class ConverterModuleImplementation implements ConverterModuleContract {

  private final ParserModuleContract parserModuleContract;

  public ConverterModuleImplementation(ParserModuleContract parserModuleContract) {
    if (parserModuleContract == null) {
      throw new NullPointerException("ParserModuleContract should not be null");
    }
    this.parserModuleContract = parserModuleContract;
  }

  @Override
  public <T> String toString(T value) {
    if (value == null) {
      return null;
    }
    return parserModuleContract.toJson(value);
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> T fromString(String value, DataInfo info) throws Exception
  {
    if (value == null) {
      return null;
    }
    Utils.checkNull("data info", info);

    Class<?> keyType = info.keyClazz;
    Class<?> valueType = info.valueClazz;

    switch (info.dataType) {
      case DataInfo.TYPE_OBJECT:
        return toObject(value, keyType);
      case DataInfo.TYPE_LIST:
        return toList(value, keyType);
      case DataInfo.TYPE_MAP:
        return toMap(value, keyType, valueType);
      case DataInfo.TYPE_SET:
        return toSet(value, keyType);
      default:
        return null;
    }
  }

  private <T> T toObject(String json, Class<?> type) throws Exception
  {
    return parserModuleContract.fromJson(json, type);
  }

  @SuppressWarnings("unchecked")
  private <T> T toList(String json, Class<?> type) throws Exception
  {
    if (type == null) {
      return (T) new ArrayList<>();
    }
    List<T> list = parserModuleContract.fromJson(
        json,
        new TypeToken<List<T>>() {
        }.getType()
    );

    int size = list.size();
    for (int i = 0; i < size; i++) {
      list.set(i, (T) parserModuleContract.fromJson(parserModuleContract.toJson(list.get(i)), type));
    }
    return (T) list;
  }

  @SuppressWarnings("unchecked")
  private <T> T toSet(String json, Class<?> type) throws Exception
  {
    Set<T> resultSet = new HashSet<>();
    if (type == null) {
      return (T) resultSet;
    }
    Set<T> set = parserModuleContract.fromJson(json, new TypeToken<Set<T>>() {
    }.getType());

    for (T t : set) {
      String valueJson = parserModuleContract.toJson(t);
      T value = parserModuleContract.fromJson(valueJson, type);
      resultSet.add(value);
    }
    return (T) resultSet;
  }

  @SuppressWarnings("unchecked")
  private <K, V, T> T toMap(String json, Class<?> keyType, Class<?> valueType) throws Exception
  {
    Map<K, V> resultMap = new HashMap<>();
    if (keyType == null || valueType == null) {
      return (T) resultMap;
    }
    Map<K, V> map = parserModuleContract.fromJson(json, new TypeToken<Map<K, V>>() {
    }.getType());

    for (Map.Entry<K, V> entry : map.entrySet()) {
      String keyJson = parserModuleContract.toJson(entry.getKey());
      K k = parserModuleContract.fromJson(keyJson, keyType);

      String valueJson = parserModuleContract.toJson(entry.getValue());
      V v = parserModuleContract.fromJson(valueJson, valueType);
      resultMap.put(k, v);
    }
    return (T) resultMap;
  }

}
