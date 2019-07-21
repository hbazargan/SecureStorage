package ir.hbazargan.securestore.modules.encryption.keystore;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.security.KeyPairGeneratorSpec;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Calendar;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.security.auth.x500.X500Principal;

import ir.hbazargan.securestore.modules.encryption.EncryptionException;

public final class KeyStoreImplementation implements KeyStoreContract {
    public static final String ANDROID_KEY_STORE = "AndroidKeyStore";
    public static final String DEFAULT_KEY_STORE_NAME = "defaultKeyStoreName";
    public static final String ASYMMETRIC = "ASYMMETRIC";
    public static final String SYMMETRIC = "SYMMETRIC";
    private final Context context;

    private KeyStore keyStore = null;
    private KeyStore defaultKeyStore = null;
    private File defaultKeyStoreFile = null;

    private KeyStoreImplementation(Context context) throws EncryptionException
    {
        this.context = context;
        createKeyStore();
        if (!isMarshmallow())
        {
            createKeyStoreDefault(context);
        }
    }

    //region AndroidKeyStore
    private void createKeyStore() throws EncryptionException
    {
        try
        {
            this.keyStore = KeyStore.getInstance(ANDROID_KEY_STORE);
            this.keyStore.load(null);
            PrivateKey privateKey = (PrivateKey) keyStore.getKey(ASYMMETRIC, null);
            if (privateKey == null)
            {
                createAndroidKeyStoreAsymmetricKey();
            }
            SecretKey secretKey = (SecretKey) keyStore.getKey(SYMMETRIC, null);;
            if (secretKey == null && isMarshmallow())
            {
                createAndroidKeyStoreSymmetricKey();
            }
        }
        catch (KeyStoreException e)
        {
            e.printStackTrace();
            throw new EncryptionException().withStackTrace(e.getStackTrace());
        }
        catch (CertificateException e)
        {
            e.printStackTrace();
            throw new EncryptionException().withStackTrace(e.getStackTrace());
        }
        catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
            throw new EncryptionException().withStackTrace(e.getStackTrace());
        }
        catch (IOException e)
        {
            e.printStackTrace();
            throw new EncryptionException().withStackTrace(e.getStackTrace());
        }
        catch (UnrecoverableKeyException e)
        {
            e.printStackTrace();
            throw new EncryptionException().withStackTrace(e.getStackTrace());
        }
    }

    private void createAndroidKeyStoreAsymmetricKey() throws EncryptionException
    {
        try
        {
            KeyPairGenerator generator = KeyPairGenerator.getInstance(KeyProperties.KEY_ALGORITHM_RSA, ANDROID_KEY_STORE);

            if (isMarshmallow()) {
                initGeneratorWithKeyGenParameterSpec(generator);
            } else {
                initGeneratorWithKeyPairGeneratorSpec(generator);
            }

            generator.generateKeyPair();
        }
        catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
            throw new EncryptionException().withStackTrace(e.getStackTrace());
        }
        catch (NoSuchProviderException e)
        {
            e.printStackTrace();
            throw new EncryptionException().withStackTrace(e.getStackTrace());
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void initGeneratorWithKeyGenParameterSpec(KeyPairGenerator generator) throws EncryptionException
    {
        KeyGenParameterSpec.Builder builder = new KeyGenParameterSpec.Builder(ASYMMETRIC, KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                .setBlockModes(KeyProperties.BLOCK_MODE_ECB)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_PKCS1);
        try
        {
            generator.initialize(builder.build());
        }
        catch (InvalidAlgorithmParameterException e)
        {
            e.printStackTrace();
            throw new EncryptionException().withStackTrace(e.getStackTrace());
        }
    }

    private void initGeneratorWithKeyPairGeneratorSpec(KeyPairGenerator generator) throws EncryptionException
    {
        Calendar startDate = Calendar.getInstance();
        Calendar endDate = Calendar.getInstance();
        endDate.add(Calendar.YEAR, 20);

        KeyPairGeneratorSpec.Builder builder = new KeyPairGeneratorSpec.Builder(context)
                .setAlias(ASYMMETRIC)
                .setSerialNumber(BigInteger.ONE)
                .setSubject(new X500Principal("CN="+ASYMMETRIC+" CA Certificate"))
                .setStartDate(startDate.getTime())
                .setEndDate(endDate.getTime());

        try
        {
            generator.initialize(builder.build());
        }
        catch (InvalidAlgorithmParameterException e)
        {
            e.printStackTrace();
            throw new EncryptionException().withStackTrace(e.getStackTrace());
        }
    }

    /**
     * Creates symmetric [KeyProperties.KEY_ALGORITHM_AES] key with default [KeyProperties.BLOCK_MODE_CBC] and
     * [KeyProperties.ENCRYPTION_PADDING_PKCS7] and saves it to Android Key Store.
     */
    @TargetApi(Build.VERSION_CODES.M)
    private void createAndroidKeyStoreSymmetricKey() throws EncryptionException
    {
        try
        {
            KeyGenerator keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEY_STORE);

            KeyGenParameterSpec.Builder builder = new KeyGenParameterSpec.Builder(SYMMETRIC, KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7);
            keyGenerator.init(builder.build());
            keyGenerator.generateKey();
        }
        catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
            throw new EncryptionException().withStackTrace(e.getStackTrace());
        }
        catch (NoSuchProviderException e)
        {
            e.printStackTrace();
            throw new EncryptionException().withStackTrace(e.getStackTrace());
        }
        catch (InvalidAlgorithmParameterException e)
        {
            e.printStackTrace();
            throw new EncryptionException().withStackTrace(e.getStackTrace());
        }
    }
    //endregion
    //region DefaultKeyStore
    private void createKeyStoreDefault(Context context) throws EncryptionException
    {
        defaultKeyStoreFile = new File(context.getFilesDir(), DEFAULT_KEY_STORE_NAME);
        KeyPair keyPair = getAndroidKeyStoreAsymmetricKeyPair();
        if (keyPair!=null)
        {
            String password = getPasswordByPrivateKey(keyPair);
            try
            {
                if(defaultKeyStore==null){
                    createDefaultKeyStore();
                }else
                {
                    SecretKey secretKey = (SecretKey) defaultKeyStore.getKey(SYMMETRIC, password.toCharArray());
                    if (secretKey == null)
                    {
                        createDefaultKeyStore();
                    }
                }
            }
            catch (KeyStoreException e)
            {
                e.printStackTrace();
                throw new EncryptionException().withStackTrace(e.getStackTrace());
            }
            catch (NoSuchAlgorithmException e)
            {
                e.printStackTrace();
                throw new EncryptionException().withStackTrace(e.getStackTrace());
            }
            catch (UnrecoverableKeyException e)
            {
                e.printStackTrace();
                throw new EncryptionException().withStackTrace(e.getStackTrace());
            }
            catch (Exception e){
                e.printStackTrace();
                throw new EncryptionException().withStackTrace(e.getStackTrace());
            }
        }
    }

    private String getPasswordByPrivateKey(KeyPair keyPair)
    {
        return keyPair.getPrivate().toString().substring(0,30);
    }

    private void createDefaultKeyStore() throws EncryptionException
    {
        try
        {
            defaultKeyStore = KeyStore.getInstance(KeyStore.getDefaultType());

            if (!defaultKeyStoreFile.exists()) {
                defaultKeyStore.load(null);
            } else {
                defaultKeyStore.load(new FileInputStream(defaultKeyStoreFile), null);
            }
            createDefaultKeyStoreSymmetricKey();
        }
        catch (KeyStoreException e)
        {
            e.printStackTrace();
            throw new EncryptionException().withStackTrace(e.getStackTrace());
        }
        catch (IOException e)
        {
            e.printStackTrace();
            throw new EncryptionException().withStackTrace(e.getStackTrace());
        }
        catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
            throw new EncryptionException().withStackTrace(e.getStackTrace());
        }
        catch (CertificateException e)
        {
            e.printStackTrace();
            throw new EncryptionException().withStackTrace(e.getStackTrace());
        }
    }

    private void createDefaultKeyStoreSymmetricKey() throws EncryptionException
    {
        SecretKey key = generateDefaultSymmetricKey();
        KeyStore.SecretKeyEntry keyEntry = new KeyStore.SecretKeyEntry(key);

        try
        {
            String password = getPasswordByPrivateKey(getAndroidKeyStoreAsymmetricKeyPair());
            defaultKeyStore.setEntry(SYMMETRIC, keyEntry, new KeyStore.PasswordProtection(password.toCharArray()));
            defaultKeyStore.store(new FileOutputStream(defaultKeyStoreFile), password.toCharArray());
        }
        catch (KeyStoreException e)
        {
            e.printStackTrace();
            throw new EncryptionException().withStackTrace(e.getStackTrace());
        }
        catch (CertificateException e)
        {
            e.printStackTrace();
            throw new EncryptionException().withStackTrace(e.getStackTrace());
        }
        catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
            throw new EncryptionException().withStackTrace(e.getStackTrace());
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
            throw new EncryptionException().withStackTrace(e.getStackTrace());
        }
        catch (IOException e)
        {
            e.printStackTrace();
            throw new EncryptionException().withStackTrace(e.getStackTrace());
        }
    }

    /**
     * Generates symmetric [KeyProperties.KEY_ALGORITHM_AES] key with default [KeyProperties.BLOCK_MODE_CBC] and
     * [KeyProperties.ENCRYPTION_PADDING_PKCS7] using default provider.
     */
    private SecretKey generateDefaultSymmetricKey() throws EncryptionException
    {
        try
        {
            KeyGenerator keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES);
            return keyGenerator.generateKey();
        }
        catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
            throw new EncryptionException().withStackTrace(e.getStackTrace());
        }
    }
    //endregion

    private boolean isMarshmallow()
    {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    //region PublicAPI

    @Override
    /**
     * @return asymmetric keypair from Android Key Store or null if any key with given alias exists
     */
    public KeyPair getAndroidKeyStoreAsymmetricKeyPair() throws EncryptionException
    {
        try
        {
            PrivateKey privateKey = (PrivateKey) keyStore.getKey(ASYMMETRIC, null);
            PublicKey publicKey = keyStore.getCertificate(ASYMMETRIC).getPublicKey();

            if (privateKey != null && publicKey != null) {
                return new KeyPair(publicKey, privateKey);
            } else {
                throw new EncryptionException();
            }
        }
        catch (KeyStoreException e)
        {
            e.printStackTrace();
            throw new EncryptionException().withStackTrace(e.getStackTrace());
        }
        catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
            throw new EncryptionException().withStackTrace(e.getStackTrace());
        }
        catch (UnrecoverableKeyException e)
        {
            e.printStackTrace();
            throw new EncryptionException().withStackTrace(e.getStackTrace());
        }
    }

    @Override
    public SecretKey getKeyStoreSymmetricKey() throws EncryptionException
    {
        if(isMarshmallow()){
            return getAndroidKeyStoreSymmetricKey();
        }else{
            return getDefaultKeyStoreSymmetricKey();
        }
    }

    /**
     * @return symmetric key from Android Key Store or null if any key with given alias exists
     */
    public SecretKey getAndroidKeyStoreSymmetricKey() throws EncryptionException
    {
        try
        {
            return (SecretKey) keyStore.getKey(SYMMETRIC, null);
        }
        catch (KeyStoreException e)
        {
            e.printStackTrace();
            throw new EncryptionException().withStackTrace(e.getStackTrace());
        }
        catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
            throw new EncryptionException().withStackTrace(e.getStackTrace());
        }
        catch (UnrecoverableKeyException e)
        {
            e.printStackTrace();
            throw new EncryptionException().withStackTrace(e.getStackTrace());
        }
    }

    /**
     * @return symmetric key from Default Key Store or null if any key with given alias exists
     */
    public SecretKey getDefaultKeyStoreSymmetricKey() throws EncryptionException
    {
        try {
            KeyPair keyPair = getAndroidKeyStoreAsymmetricKeyPair();
            if (keyPair!=null)
            {
                String password = getPasswordByPrivateKey(keyPair);
                return (SecretKey) defaultKeyStore.getKey(SYMMETRIC, password.toCharArray());
            }
            else
            {
                throw new EncryptionException();
            }
        }
        catch (UnrecoverableKeyException e)
        {
            e.printStackTrace();
            throw new EncryptionException().withStackTrace(e.getStackTrace());
        }
        catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
            throw new EncryptionException().withStackTrace(e.getStackTrace());
        }
        catch (KeyStoreException e)
        {
            e.printStackTrace();
            throw new EncryptionException().withStackTrace(e.getStackTrace());
        }
    }

    @Override
    /**
     * Remove key with given alias from Android Key Store
     */
    public void removeKeyStoreKey(String alias) throws EncryptionException
    {
        try
        {
            keyStore.deleteEntry(alias);
            defaultKeyStore.deleteEntry(alias);
        }
        catch (KeyStoreException e)
        {
            e.printStackTrace();
            throw new EncryptionException().withStackTrace(e.getStackTrace());
        }
    }
    //endregion

    public static class Builder
    {
        private Context context;

        public Builder(Context context)
        {
            this.context = context;
        }
        public KeyStoreContract build() throws EncryptionException
        {
            return new KeyStoreImplementation(this.context);
        }
    }
}
