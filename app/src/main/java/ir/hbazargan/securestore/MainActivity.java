package ir.hbazargan.securestore;

import androidx.appcompat.app.AppCompatActivity;
import ir.hbazargan.securestore.logger.LogInterceptor;

import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        timeHawkInit();
        timeHawkPut();
        timeHawkGet();
        timeHawkContains();
        timeHawkCount();
        timeHawkDelete();
    }

    private void timeHawkInit() {
        long startTime = System.currentTimeMillis();

        SecureStorage.init(this).setLogInterceptor(new LogInterceptor() {
            @Override public void onLog(String message) {
                Log.d("SecureStorage", message);
            }
        }).build();

        long endTime = System.currentTimeMillis();
        System.out.println("SecureStorage.init: " + (endTime - startTime) + "ms");
    }

    private void timeHawkPut() {
        long startTime = System.currentTimeMillis();

        SecureStorage.put("key", getString(R.string.sample_data));

        long endTime = System.currentTimeMillis();
        System.out.println("SecureStorage.put: " + (endTime - startTime) + "ms");
    }

    private void timeHawkGet() {
        long startTime = System.currentTimeMillis();

        SecureStorage.get("key");

        long endTime = System.currentTimeMillis();
        System.out.println("SecureStorage.get: " + (endTime - startTime) + "ms");
    }

    private void timeHawkCount() {
        long startTime = System.currentTimeMillis();

        SecureStorage.count();

        long endTime = System.currentTimeMillis();
        System.out.println("SecureStorage.count: " + (endTime - startTime) + "ms");
    }

    private void timeHawkContains() {
        long startTime = System.currentTimeMillis();

        SecureStorage.contains("key");

        long endTime = System.currentTimeMillis();
        System.out.println("SecureStorage.count: " + (endTime - startTime) + "ms");
    }

    private void timeHawkDelete() {
        long startTime = System.currentTimeMillis();

        SecureStorage.delete("key");

        long endTime = System.currentTimeMillis();
        System.out.println("SecureStorage.count: " + (endTime - startTime) + "ms");
    }
}
