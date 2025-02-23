package lk.sankaudeshika.androidfixers.ui.myprofile;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import lk.sankaudeshika.androidfixers.R;
import lk.sankaudeshika.androidfixers.model.ServerURL;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class SellerProfileFragment extends Fragment {

    private static final int GALLERY_PERMISSION_CODE = 101;
    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private ImageView imageView;
    private File UploadImageFile;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_seller_profile, container, false);

        imageView = view.findViewById(R.id.imageView2);
        setupImagePicker();

        //        Set Database ImagePath
        SharedPreferences sp = getActivity().getSharedPreferences("lk.sankaudeshika.androidfixers", Context.MODE_PRIVATE);
        String Logged_mobile = sp.getString("Default_mobile", "");
        String UserID = sp.getString("Default_vendor_id", "");

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("vendor")
                .whereEqualTo("mobile_1",Logged_mobile)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<DocumentSnapshot> documentList = queryDocumentSnapshots.getDocuments();

                        for (DocumentSnapshot documentItem :documentList ) {
                            try {
                                String ImagePath =documentItem.getString("profileImagePath");
                                if(!ImagePath.equals("null")){
                                    Log.i("appout", ServerURL.ServerImages+UserID+"seller_profileImage.jpg");
                                    Picasso.get()
                                            .load(ServerURL.ServerImages+UserID+"seller_profileImage.jpg")
                                            .resize(500, 500)
                                            .centerCrop()
                                            .into(imageView);
                                }
                            } catch (Exception e) {
                                Log.i("appout", "onSuccess: "+e.toString());
                                throw new RuntimeException(e);
                            }
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("appout", "onFailure: No Profile Image");
                    }
                });




        imageView.setOnClickListener(v -> {
            if (isPermissionGranted()) {
                Toast.makeText(view.getContext(), "Storage Permission Already Granted", Toast.LENGTH_SHORT).show();
                pickImageFromGallery();
            } else {
                requestGalleryPermission();
            }
        });

        // Upload Image
        Button ImageUploadBtn = view.findViewById(R.id.ImageUploadBtn);
        ImageUploadBtn.setOnClickListener(v -> {



            firestore.collection("vendor").document(UserID).get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && task.getResult() != null) {
                            String useridValue = task.getResult().getId();

                            String FileName = useridValue + "seller_profileImage";
                            HashMap<String, Object> updateProfileImage = new HashMap<>();
                            updateProfileImage.put("profileImagePath", FileName);
                            Log.i("appout", "onComplete: " + FileName);

                            firestore.collection("vendor").document(useridValue).update(updateProfileImage)
                                    .addOnSuccessListener(unused -> {
                                        Log.i("appout", "Firestore Update Success");

                                        // Run the network request on a background thread
                                        new Thread(() -> uploadImageToServer(FileName)).start();

                                    })
                                    .addOnFailureListener(e -> Log.e("appout", "Firestore Update Failed: " + e.getMessage()));
                        } else {
                            Log.e("appout", "Failed to fetch user document");
                        }
                    });
        });

        return  view;
    }

    private void uploadImageToServer(String fileName) {
        OkHttpClient okHttpClient = new OkHttpClient();

        if (UploadImageFile == null) {
            Log.e("appout", "UploadImageFile is null");
            return;
        }

        okhttp3.RequestBody fileBody = RequestBody.create(UploadImageFile, MediaType.get("image/*"));

        MultipartBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("image", UploadImageFile.getName(), fileBody)
                .addFormDataPart("newfileImageName", fileName)
                .build();

        Request request = new Request.Builder()
                .url(ServerURL.ServerUrl)
                .post(requestBody)
                .build();

        try {
            Response response = okHttpClient.newCall(request).execute();
            String responseText = response.body() != null ? response.body().string() : "No Response";
            Log.i("appout", "Upload Success: " + responseText);
        } catch (Exception e) {
            Log.e("appout", "Upload Error: " + e.toString());
        }
    }

    private void setupImagePicker() {
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null && data.getData() != null) {
                            Uri imageUri = data.getData();
                            try {
                                Bitmap bitmap = MediaStore.Images.Media.getBitmap(
                                        requireContext().getContentResolver(), imageUri);
                                imageView.setImageBitmap(bitmap);
                                UploadImageFile = bitmapToFile(getContext(), bitmap);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
        );
    }

    private boolean isPermissionGranted() {
        String permission = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
                ? Manifest.permission.READ_MEDIA_IMAGES
                : Manifest.permission.READ_EXTERNAL_STORAGE;

        return ContextCompat.checkSelfPermission(requireContext(), permission) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestGalleryPermission() {
        String permission = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
                ? Manifest.permission.READ_MEDIA_IMAGES
                : Manifest.permission.READ_EXTERNAL_STORAGE;

        requestPermissions(new String[]{permission}, GALLERY_PERMISSION_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == GALLERY_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(requireContext(), "Permission Granted!", Toast.LENGTH_SHORT).show();
                pickImageFromGallery();
            } else {
                Toast.makeText(requireContext(), "Permission Denied!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void pickImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        imagePickerLauncher.launch(intent);
    }

    private static File bitmapToFile(Context context, Bitmap bitmap) throws IOException {
        File file = new File(context.getCacheDir(), "upload_image_" + System.currentTimeMillis() + ".jpg");
        FileOutputStream out = new FileOutputStream(file);
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
        out.flush();
        out.close();
        return file;
    }



}