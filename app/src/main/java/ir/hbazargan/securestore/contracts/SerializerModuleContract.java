package ir.hbazargan.securestore.contracts;

import ir.hbazargan.securestore.DataInfo;
import ir.hbazargan.securestore.modules.SerializerModuleImplementation;

/**
 * Intermediate layer that is used to serialize/deserialize the cipher text
 *
 * <p>Use custom implementation if built-in implementation is not enough.</p>
 *
 * @see SerializerModuleImplementation
 */
public interface SerializerModuleContract {

  /**
   * Serialize the cipher text along with the given data type
   *
   * @return serialized string
   */
  <T> String serialize(String cipherText, T value);

  /**
   * Deserialize the given text according to given DataInfo
   *
   * @return original object
   */
  DataInfo deserialize(String plainText);
}