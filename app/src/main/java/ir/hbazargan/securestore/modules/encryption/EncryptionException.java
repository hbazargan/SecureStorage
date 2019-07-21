package ir.hbazargan.securestore.modules.encryption;

public final class EncryptionException extends Exception {
    public EncryptionException withStackTrace(StackTraceElement[] stackTrace){
        setStackTrace(stackTrace);
        return this;
    }
}
