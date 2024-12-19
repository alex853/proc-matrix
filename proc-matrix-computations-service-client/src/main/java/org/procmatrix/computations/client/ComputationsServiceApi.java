package org.procmatrix.computations.client;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.*;

import java.util.List;

public interface ComputationsServiceApi {

    @POST("/computations/v1/batch/submit")
    Call<String> submitBatch(@Body RequestBody body);

    @POST("/computations/v1/batch/retrieve")
    Call<ResponseBody> retrieveResults(@Body List<String> operationIds);

}
