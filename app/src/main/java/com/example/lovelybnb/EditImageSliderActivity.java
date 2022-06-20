package com.example.lovelybnb;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.lovelybnb.Data.ImageDetail;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class EditImageSliderActivity extends AppCompatActivity {
    FirebaseDatabase firebaseDatabase;
    DatabaseReference sliderRef;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;
    String itemId;
    TextView notifi, goback;

    Button chooseImg, uploadImg;
    private int PICK_IMAGE=1705;

    ArrayList<Uri> imageList = new ArrayList<Uri>();
    private Uri imageUri;
    private int upload_count = 0;
    private int sliderId =0;
    private int sliderIdStorage=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_image_slider);

        firebaseDatabase = FirebaseDatabase.getInstance("https://lovelybnb-b90d2-default-rtdb.asia-southeast1.firebasedatabase.app");
        sliderRef = firebaseDatabase.getReference("Slider");

        itemId = getIntent().getStringExtra("itemId");

        goback = findViewById(R.id.backprevious);
        goback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        notifi = findViewById(R.id.notifi);
        notifi.setVisibility(View.INVISIBLE);

        chooseImg = findViewById(R.id.chooseImg);
        chooseImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                startActivityForResult(intent,PICK_IMAGE);
            }
        });

        uploadImg = findViewById(R.id.uploadImg);
        uploadImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProgressDialog progressDialog;
                progressDialog = new ProgressDialog(EditImageSliderActivity.this);
                progressDialog.show();
                progressDialog.setContentView(R.layout.dialog_progress);
                progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

                // get the Firebase  storage reference
                firebaseStorage = FirebaseStorage.getInstance();
                storageReference = firebaseStorage.getReference();

                String imageRandomName = UUID.randomUUID().toString();
                StorageReference imageFolder = storageReference.child("Images/slider/"+itemId);

                //count image selected and push to storage
                for (upload_count=0; upload_count<imageList.size(); upload_count++){
                    Uri uri = imageList.get(upload_count);
                    sliderIdStorage = sliderIdStorage + 1;
                    StorageReference imageName = imageFolder.child("slider"+ sliderIdStorage);

                    imageName.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            imageName.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    //push image to realtime database
                                    String url = uri.toString();
                                    HashMap<String,String> hashMap = new HashMap<>();
                                    hashMap.put("url",url);

                                    sliderId = sliderId+1;
                                    sliderRef.child(itemId).child("slider"+sliderId).setValue(hashMap);
                                    progressDialog.dismiss();
                                }
                            });
                        }
                    });
                }

            }
        });

        Button confirm = findViewById(R.id.confirmImgSlider);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog = new Dialog(EditImageSliderActivity.this,R.style.CustomDialog);
                dialog.setContentView(R.layout.dialog_booking_loading);
                new Handler().postDelayed(new Runnable() {
                                              @Override
                                              public void run() {
                                                  getIntent().getStringExtra("idGoBack").equals("itemAdminDetail");
                                                  Intent intent = new Intent();
                                                  intent.putExtra("checkSlider","true");
                                                  setResult(Activity.RESULT_OK, intent);

                                                  dialog.dismiss();
                                                  finish();
                                              }
                                          }, 5000
                );

                dialog.show();
            }
        });
    }
    @Override
    public void onBackPressed() {

        // đặt resultCode là Activity.RESULT_CANCELED thể hiện
        // đã thất bại khi người dùng click vào nút Back.
        // Khi này sẽ không trả về data.
        setResult(Activity.RESULT_CANCELED);
        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE){
            if (resultCode == RESULT_OK){
                if (data.getClipData() != null){
                    int countClipData = data.getClipData().getItemCount();

                    int currentImageSelected = 0;

                    while (currentImageSelected<countClipData){

                        imageUri = data.getClipData().getItemAt(currentImageSelected).getUri();
                        imageList.add(imageUri);
                        currentImageSelected = currentImageSelected +1;

                        notifi.setVisibility(View.VISIBLE);
                        notifi.setText("You have selected " + imageList.size()+ " Images");
                    }

                }else {
                    Toast.makeText(this,"Please select multiple image",Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}