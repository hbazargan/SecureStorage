package ir.hbazargan.benchmark;

import androidx.appcompat.app.AppCompatActivity;
import ir.hbazargan.securestore.R;
import ir.hbazargan.securestore.SecureStorage;
import ir.hbazargan.securestore.SecureStorageContract;
import ir.hbazargan.securestore.contracts.LogInterceptorModuleContract;
import ir.hbazargan.securestore.modules.encryption.cipher.AlgorithmType;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {

    private SecureStorageContract secureStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        timeSecureStorageInit();
        timeSecureStoragePut();
        timeSecureStorageGet();
        timeSecureStorageContains();
        timeSecureStorageCount();
        timeSecureStorageDelete();
    }

    private void timeSecureStorageInit() {
        long startTime = System.currentTimeMillis();

        SecureStorage.SecureStorageBuilder builder = new SecureStorage.SecureStorageBuilder(getApplicationContext());
        builder.setAlgorithm(AlgorithmType.ASYMMETRIC);
        secureStorage = builder.setLogInterceptorModuleContract(new LogInterceptorModuleContract() {
            @Override public void onLog(String message) {
                Log.d("SecureStorage", message);
            }
        }).build();

        long endTime = System.currentTimeMillis();
        System.out.println("SecureStorage.init: " + (endTime - startTime) + "ms");
    }

    private void timeSecureStoragePut() {
        long startTime = System.currentTimeMillis();

        secureStorage.put("key", getSampleString());

        long endTime = System.currentTimeMillis();
        System.out.println("SecureStorage.put: " + (endTime - startTime) + "ms");
    }

    private void timeSecureStorageGet() {
        long startTime = System.currentTimeMillis();

        String text = secureStorage.get("key");

        ((TextView) findViewById(R.id.output)).setText(text);

        long endTime = System.currentTimeMillis();
        System.out.println("SecureStorage.get: " + (endTime - startTime) + "ms");
    }

    private void timeSecureStorageCount() {
        long startTime = System.currentTimeMillis();

        secureStorage.count();

        long endTime = System.currentTimeMillis();
        System.out.println("SecureStorage.count: " + (endTime - startTime) + "ms");
    }

    private void timeSecureStorageContains() {
        long startTime = System.currentTimeMillis();

        secureStorage.contains("key");

        long endTime = System.currentTimeMillis();
        System.out.println("SecureStorage.count: " + (endTime - startTime) + "ms");
    }

    private void timeSecureStorageDelete() {
        long startTime = System.currentTimeMillis();

        secureStorage.delete("key");

        long endTime = System.currentTimeMillis();
        System.out.println("SecureStorage.count: " + (endTime - startTime) + "ms");
    }

    private String getSampleString() {
        StringBuilder termsString = new StringBuilder();
        BufferedReader reader;
        try {
            reader = new BufferedReader(
                    new InputStreamReader(getAssets().open("sample.txt")));

            String str;
            while ((str = reader.readLine()) != null) {
                termsString.append(str);
            }

            reader.close();
            return termsString.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
