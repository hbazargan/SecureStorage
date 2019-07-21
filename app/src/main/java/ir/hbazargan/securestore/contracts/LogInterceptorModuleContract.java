package ir.hbazargan.securestore.contracts;

/**
 * Interceptor for all logs happens in the library
 */
public interface LogInterceptorModuleContract {

  /**
   * Will be triggered each time when a log is written
   *
   * @param message is the log message
   */
  void onLog(String message);
}
