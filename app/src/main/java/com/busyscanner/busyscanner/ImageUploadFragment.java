package com.busyscanner.busyscanner;


import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedFile;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ImageUploadFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ImageUploadFragment extends Fragment implements Callback<List<BizCardResponse>> {

    public static final String TAG = ImageUploadFragment.class.getSimpleName();
    private static final String ARG_IMG_URI = "img_uri";
    private MsgFragment msgFragment;
    private File imagePath;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param imageUri Full image path in the local file system
     * @return A new instance of fragment ImageUploadFragment.
     */
    public static ImageUploadFragment newInstance(String imageUri) {
        ImageUploadFragment fragment = new ImageUploadFragment();
        Bundle args = new Bundle();
        args.putString(ARG_IMG_URI, imageUri);
        fragment.setArguments(args);
        return fragment;
    }

    public ImageUploadFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imagePath = new File(getArguments().getString(ARG_IMG_URI));

        msgFragment = new MsgFragment();
        getChildFragmentManager().beginTransaction()
                .replace(R.id.msgfragment_container, msgFragment)
                .commit();
        uploadImage();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_image_upload, container, false);
    }

    private void uploadImage() {
        ImageProcessingApi imageProcessingApi = Access.getInstance().getImageProcessingApi();

        msgFragment.pushBusy();

        Bitmap bm = BitmapFactory.decodeFile(imagePath.getPath());
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 20, baos); //bm is the bitmap object
        byte[] byteArrayImage = baos.toByteArray();
        String encodedImage = Base64.encodeToString(byteArrayImage, Base64.DEFAULT);
        BizCardRequest request = new BizCardRequest("Testing encoded image string thing", encodedImage);
//        imageProcessingApi.uploadImageString(request, this);

        TypedFile typedFile = new TypedFile("image/jpg", imagePath);
        String desc = "TEST!";
        imageProcessingApi.uploadCardImage(typedFile, desc, this);
    }

    @Override
    public void failure(RetrofitError error) {
        msgFragment.popBusy();
        error.printStackTrace();
        if (getActivity() != null) {
            msgFragment.setMsg(error.toString());
        }
    }

    /**
     * Successful HTTP response.
     *
     * @param bizCardResponse
     * @param response
     */
    @Override
    public void success(List<BizCardResponse> bizCardResponse, Response response) {
        msgFragment.popBusy();
        Toast.makeText(getActivity(), "Image upload success", Toast.LENGTH_LONG).show();
    }


}
