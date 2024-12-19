package org.procmatrix.computations.client;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

import java.util.concurrent.TimeUnit;

public class ComputationsServiceClientFactory {

    public static ComputationsService createService(final String serviceUrl) {
        final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(300, TimeUnit.SECONDS) // Connection timeout
                .readTimeout(300, TimeUnit.SECONDS)    // Read timeout
                .writeTimeout(300, TimeUnit.SECONDS)   // Write timeout
                .build();

        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(serviceUrl)
                .client(okHttpClient)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return new ComputationsServiceImpl(retrofit.create(ComputationsServiceApi.class));
    }
}
