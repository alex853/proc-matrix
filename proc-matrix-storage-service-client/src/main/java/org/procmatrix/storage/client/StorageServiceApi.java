package org.procmatrix.storage.client;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.*;

interface StorageServiceApi {

    @PUT("/storage/v1/save")
    @Headers("Content-Type: application/octet-stream")
    Call<String> save(@Body RequestBody matrix);

    @GET("/storage/v1/load")
    Call<ResponseBody> load(@Query("id") String id);

    @DELETE("/storage/v1/delete")
    Call<Void> delete(@Query("id") String id);
}
