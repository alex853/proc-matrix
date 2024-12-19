package org.procmatrix.storage.client;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

import java.util.concurrent.TimeUnit;

public class StorageServiceClientFactory {

    public static StorageService createService(final String serviceUrl) {
        final OkHttpClient customOkHttpClient = new OkHttpClient.Builder()
                .connectTimeout(300, TimeUnit.SECONDS) // Connection timeout
                .readTimeout(300, TimeUnit.SECONDS)    // Read timeout
                .writeTimeout(300, TimeUnit.SECONDS)   // Write timeout
                .build();

        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(serviceUrl)
                .client(customOkHttpClient)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return new StorageServiceImpl(retrofit.create(StorageServiceApi.class));
    }

}
