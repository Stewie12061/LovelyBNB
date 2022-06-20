package com.example.lovelybnb;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.interfaces.ItemClickListener;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.lovelybnb.Data.ItemDetail;
import com.example.lovelybnb.Data.PropertyItems;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class CreateItemAdminActivity extends AppCompatActivity {
    FirebaseDatabase firebaseDatabase;
    DatabaseReference itemRef, sliderRef,itemDetailRef;
    String itemIdForCreate, cateId;
    ArrayList<String> arrayList;
    TextView cateNameText, goback;

    RoundedImageView imgItem;
    Button select, upload, createImgSlider, btnCreateItem;
    EditText itemName, itemPlace, itemPrice, itemRating, itemDes;

    Uri uri;
    int PICK_IMG_REQUEST = 1705;
    int RECEIVE_BOOLEAN = 1;
    int PICK_AVATAR_REQUEST = 1111;

    PropertyItems propertyItems;
    ItemDetail itemDetail;

    ImageSlider imageSlider;
    String checkSliderString = null;
    Boolean uploadCheck = false;
    Boolean uploadAvatarCheck = false;

    EditText hostName, hostMail, hostPhone, address;
    TextView adCheckInTime,adCheckOutTime;
    CircleImageView adAvatar;
    Button itemSelectImg, itemUploadImg;
    LinearLayout adCheckInPicker, adCheckOutPicker;

    TimePickerDialog picker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_item_admin);

        goback = findViewById(R.id.backprevious);
        goback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sliderRef.child(itemIdForCreate).removeValue();
                finish();
            }
        });

        firebaseDatabase = FirebaseDatabase.getInstance("https://lovelybnb-b90d2-default-rtdb.asia-southeast1.firebasedatabase.app");
        itemRef = firebaseDatabase.getReference("Items");
        sliderRef = firebaseDatabase.getReference("Slider");
        itemDetailRef = firebaseDatabase.getReference("Item Detail");

        getNewItemKeyForCreate();
        cateId = getIntent().getStringExtra("cateId");

        //image slider
        imageSlider = findViewById(R.id.sliderAdCreate);
        final List<SlideModel> slideModelArrayList = new ArrayList<>();
        if (checkSliderString!=null){
            getDetailImgSlider(itemIdForCreate, slideModelArrayList);
        }

        cateNameText = findViewById(R.id.cateNameCreateItem);
        cateNameText.setText("Fill all information to create new item "+getIntent().getStringExtra("cateName"));

        createImgSlider = findViewById(R.id.createImgSlider);
        createImgSlider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CreateItemAdminActivity.this, EditImageSliderActivity.class);
                intent.putExtra("itemId",itemIdForCreate);
                intent.putExtra("idGoBack","createItemAdmin");
                startActivityForResult(intent, RECEIVE_BOOLEAN);
            }
        });

        //edt for create item
        itemName = findViewById(R.id.edtItemNameCreate);
        itemPlace = findViewById(R.id.edtItemPlaceCreate);
        itemPrice = findViewById(R.id.edtItemPriceCreate);
        itemRating = findViewById(R.id.edtItemRatingCreate);
        itemDes = findViewById(R.id.edtItemDesCreate);
        imgItem = findViewById(R.id.imgItemCreate);

        //select and upload image
        select = findViewById(R.id.btnSelectCreate);
        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImg();
            }
        });
        upload = findViewById(R.id.btnUploadCreate);
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(itemName.getText().toString().trim())){
                    itemName.setError("You have to fill this information!");
                    itemName.requestFocus();
                }
                else if(TextUtils.isEmpty(itemPlace.getText().toString().trim())){
                    itemPlace.setError("You have to fill this information!");
                    itemPlace.requestFocus();
                }
                else if(TextUtils.isEmpty(itemPrice.getText().toString().trim())){
                    itemPrice.setError("You have to fill this information!");
                    itemPrice.requestFocus();
                }
                else if(TextUtils.isEmpty(itemDes.getText().toString().trim())){
                    itemDes.setError("You have to fill this information!");
                    itemDes.requestFocus();
                }
                else if(TextUtils.isEmpty(itemRating.getText().toString().trim())){
                    itemRating.setError("You have to fill this information!");
                    itemRating.requestFocus();
                }
                else {
                if (uri!=null){
                    ProgressDialog progressDialog;
                    progressDialog = new ProgressDialog(CreateItemAdminActivity.this);
                    progressDialog.show();
                    progressDialog.setContentView(R.layout.dialog_progress);
                    progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

                    FirebaseStorage firebaseStorage;
                    StorageReference storageReference;

                    // get the Firebase  storage reference
                    firebaseStorage = FirebaseStorage.getInstance();
                    storageReference = firebaseStorage.getReference();

                    String imageName = UUID.randomUUID().toString();
                    StorageReference imageFolder = storageReference.child("Images/items/"+imageName);

                    //put img to storage
                    imageFolder.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(CreateItemAdminActivity.this,"Upload succeed",Toast.LENGTH_SHORT).show();

                            //get uri img from storage
                            imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    uploadCheck = true;
                                    propertyItems = new PropertyItems(itemDes.getText().toString(),cateId,uri.toString(),itemName.getText().toString(),itemPlace.getText().toString(),itemPrice.getText().toString(),itemRating.getText().toString());
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            uploadCheck = false;
                            progressDialog.dismiss();
                            Toast.makeText(CreateItemAdminActivity.this,"Upload failed",Toast.LENGTH_SHORT).show();
                        }
                    });
                }}
            }
        });

        //edt for create item detail
        hostName = findViewById(R.id.edtHostNameCreate);
        hostMail = findViewById(R.id.edtHostMailCreate);
        hostPhone = findViewById(R.id.edtHostPhoneCreate);
        address = findViewById(R.id.edtAdminAddressCreate);
        adCheckInTime = findViewById(R.id.AdCheckInTimeCreate);
        adCheckOutTime = findViewById(R.id.AdCheckOutTimeCreate);
        adAvatar = findViewById(R.id.adAvatarCreate);

        adCheckInPicker = findViewById(R.id.AdCheckInPickerCreate);
        adCheckInPicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                int hour = calendar.get(Calendar.HOUR);
                int minutes = calendar.get(Calendar.MINUTE);
                // time picker dialog
                picker = new TimePickerDialog(CreateItemAdminActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                int hourFormat = hourOfDay % 12;
                                adCheckInTime.setText(String.format("%2d:%02d %s", hourFormat == 0 ? 12 : hourFormat,
                                        minute, hourOfDay < 12 ? "AM" : "PM"));
                            }
                        }, hour, minutes, false);
                picker.show();
            }
        });

        adCheckOutPicker = findViewById(R.id.AdCheckOutPickerCreate);
        adCheckOutPicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                int hour = calendar.get(Calendar.HOUR);
                int minutes = calendar.get(Calendar.MINUTE);
                // time picker dialog
                picker = new TimePickerDialog(CreateItemAdminActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                int hourFormat = hourOfDay % 12;
                                adCheckOutTime.setText(String.format("%2d:%02d %s", hourFormat == 0 ? 12 : hourFormat,
                                        minute, hourOfDay < 12 ? "AM" : "PM"));
                            }
                        }, hour, minutes, false);
                picker.show();
            }
        });

        itemDetail = new ItemDetail();

        itemSelectImg = findViewById(R.id.itemSelectImgCreate);
        itemSelectImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseAvatar();
            }
        });
        itemUploadImg = findViewById(R.id.itemUploadImgCreate);
        itemUploadImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(hostName.getText().toString().trim())){
                    hostName.setError("You have to fill this information!");
                    hostName.requestFocus();
                }
                else if(TextUtils.isEmpty(hostMail.getText().toString().trim())){
                    hostMail.setError("You have to fill this information!");
                    hostMail.requestFocus();
                }
                else if(TextUtils.isEmpty(hostPhone.getText().toString().trim())){
                    hostPhone.setError("You have to fill this information!");
                    hostPhone.requestFocus();
                }
                else if(TextUtils.isEmpty(address.getText().toString().trim())){
                    address.setError("You have to fill this information!");
                    address.requestFocus();
                }
                else {
                    if (uri != null) {
                        ProgressDialog progressDialog;
                        progressDialog = new ProgressDialog(CreateItemAdminActivity.this);
                        progressDialog.show();
                        progressDialog.setContentView(R.layout.dialog_progress);
                        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

                        FirebaseStorage firebaseStorage;
                        StorageReference storageReference;

                        // get the Firebase  storage reference
                        firebaseStorage = FirebaseStorage.getInstance();
                        storageReference = firebaseStorage.getReference();

                        String imageName = UUID.randomUUID().toString();
                        StorageReference imageFolder = storageReference.child("Images/detail/" + imageName);

                        //put img to storage
                        imageFolder.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                progressDialog.dismiss();
                                Toast.makeText(CreateItemAdminActivity.this, "Upload sussessed", Toast.LENGTH_SHORT).show();

                                //get uri img from storage
                                imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        uploadAvatarCheck = true;
                                        itemDetail = new ItemDetail(address.getText().toString(), adCheckInTime.getText().toString(), adCheckOutTime.getText().toString(), uri.toString(), hostMail.getText().toString(), hostName.getText().toString(), hostPhone.getText().toString());
                                    }
                                });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressDialog.dismiss();
                                Toast.makeText(CreateItemAdminActivity.this, "Upload failed", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            }
        });

        //button create item
        btnCreateItem = findViewById(R.id.btnCreateItem);
        btnCreateItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (uploadCheck==true && uploadAvatarCheck==true){
                    ProgressDialog progressDialog;
                    progressDialog = new ProgressDialog(CreateItemAdminActivity.this);
                    progressDialog.show();
                    progressDialog.setContentView(R.layout.dialog_progress);
                    progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                    //create item
                    itemRef.child(itemIdForCreate).setValue(propertyItems).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(),"Create item succeed",Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(),"Something went wrong!",Toast.LENGTH_SHORT).show();
                        }
                    });
                    //create item detail
                    itemDetailRef.child(itemIdForCreate).setValue(itemDetail).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(),"Create item detail succeed",Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(),"Something went wrong!",Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                else {
                    Toast.makeText(CreateItemAdminActivity.this,"You have to fill all information and upload image",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void getDetailImgSlider(String itemIdForCreate, List<SlideModel> slideModelArrayList) {
        sliderRef.child(itemIdForCreate).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    slideModelArrayList.add(new SlideModel(dataSnapshot.child("url").getValue().toString(), ScaleTypes.CENTER_CROP));
                    imageSlider.setImageList(slideModelArrayList, ScaleTypes.CENTER_CROP);
                    imageSlider.setItemClickListener(new ItemClickListener() {
                        @Override
                        public void onItemSelected(int i) {
                            Intent intent = new Intent(getApplicationContext(),ImageDetailActivity.class);
                            intent.putExtra("itemId",itemIdForCreate);
                            startActivity(intent);
                            overridePendingTransition(R.anim.slide_from_top,R.anim.slide_to_bottom);
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getNewItemKeyForCreate() {
        itemRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                arrayList = new ArrayList<String>();
                for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                    arrayList.add(dataSnapshot.getKey());
                }
                //get last itemId in item and create id for new item
                String itemidString = arrayList.get(arrayList.size()-1);
                int itemidInt = Integer.parseInt(itemidString) +1;
                itemIdForCreate = Integer.toString(itemidInt);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void chooseImg() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Picture"),PICK_IMG_REQUEST);
    }
    private void chooseAvatar() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Picture"),PICK_AVATAR_REQUEST);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMG_REQUEST && resultCode == Activity.RESULT_OK){
            uri = data.getData();

            if (null != uri) {
//                 update the preview image in the layout
                imgItem.setImageURI(uri);
            }
        }
        if (requestCode == PICK_AVATAR_REQUEST && resultCode == Activity.RESULT_OK){
            uri = data.getData();

            if (null != uri) {
//                 update the preview image in the layout
                adAvatar.setImageURI(uri);
            }
        }
        if (requestCode == RECEIVE_BOOLEAN && resultCode == Activity.RESULT_OK){
            checkSliderString = data.getStringExtra("checkSlider");
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        final List<SlideModel> slideModelArrayList = new ArrayList<>();
        if (checkSliderString!=null){
            getDetailImgSlider(itemIdForCreate, slideModelArrayList);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        final List<SlideModel> slideModelArrayList = new ArrayList<>();
        if (checkSliderString!=null){
            getDetailImgSlider(itemIdForCreate, slideModelArrayList);
        }
    }
}
