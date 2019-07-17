package ir.hbazargan.securestore.encryption;

public class EncryptionException extends Exception {
    public EncryptionException withStackTrace(StackTraceElement[] stackTrace){
        setStackTrace(stackTrace);
        return this;
    }
}
