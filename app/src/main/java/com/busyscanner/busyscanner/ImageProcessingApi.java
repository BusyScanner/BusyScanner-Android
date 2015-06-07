package com.busyscanner.busyscanner;

import retrofit.Callback;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.Part;
import retrofit.mime.TypedFile;

public interface ImageProcessingApi {

    @Multipart
    @POST("/receiveImg")
    void uploadCardImage(@Part("image") TypedFile imageFile,
                         @Part("desc") String description,
                         Callback<BizCardResponse> callback);
}