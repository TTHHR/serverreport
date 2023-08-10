package com.yaasoosoft.serverreport.utils;

import android.util.Log;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class NetworkUtils {
    private static final String TAG = "NetworkUtils";
    static String ipurl = "http://v4v6.ipv6-test.com/api/myip.php";
    static String postUrl = "https://dxkite.cn/static/tthhr/myip.php";

    public static String getIPV4V6Address() {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(ipurl)
                .build();
        try {
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                String responseBody = response.body().string();
                System.out.println(responseBody);
                return responseBody;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static boolean postIPV4V6Address(String address) {
        boolean ret = false;
        TrustManager[] trustAllCertificates = new TrustManager[]{
                new X509TrustManager() {
                    @Override
                    public void checkClientTrusted(java.security.cert.X509Certificate[] x509Certificates, String s) throws CertificateException {

                    }

                    @Override
                    public void checkServerTrusted(java.security.cert.X509Certificate[] x509Certificates, String s) throws CertificateException {

                    }

                    @Override
                    public X509Certificate[] getAcceptedIssuers() {
                        return new X509Certificate[0];
                    }
                }
        };
        // Create a custom SSL context that trusts all certificates
        SSLContext sslContext = null;
        try {
            sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustAllCertificates, new java.security.SecureRandom());
// Create OkHttpClient with custom SSL context
            OkHttpClient client = new OkHttpClient.Builder()
                    .sslSocketFactory(sslContext.getSocketFactory(), (X509TrustManager) trustAllCertificates[0])
                    .hostnameVerifier((hostname, session) -> true)
                    .build();
            // Create RequestBody
            RequestBody requestBody = new FormBody.Builder()
                    .add("ip", address)
                    .build();

            // Create POST request
            Request request = new Request.Builder()
                    .url(postUrl) // Replace with the actual URL
                    .post(requestBody)
                    .build();
            // Execute the request synchronously
            Response response = client.newCall(request).execute();

            if (response.isSuccessful()) {
                String responseBody = response.body().string();
                ret = true;
                Log.i(TAG, "Response: " + responseBody);
            } else {
                Log.w(TAG, "Request failed: " + response.message());
            }
        } catch (Exception e) {
            Log.e(TAG, "Request Exception: " + e);
        }
        return ret;
    }
}
