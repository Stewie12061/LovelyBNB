package com.example.lovelybnb;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.interfaces.ItemClickListener;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.lovelybnb.Data.Favorite;
import com.example.lovelybnb.Fragment.BottomSheetFragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.varunest.sparkbutton.SparkButton;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ItemDetailActivity extends FragmentActivity implements OnMapReadyCallback {

    TextView detailItemRating, detailItemName, detailItemPlace, detailItemPrice, detailItemDes,
    hostName, address, hostEmail, hostPhone, checkIn, checkOut;

    String itemId;
    String currentUserId;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference detailRef, sliderRef, userRef, hostRef;
    TextView goback;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    ImageView hostAvatar;

    ImageSlider imageSlider;
    Button opensheet;
    Boolean isInMyFavorite = false;
    Favorite favorite;
    private GoogleMap mMap;
    Geocoder geocoder;

    SparkButton sparkButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_detail);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(ItemDetailActivity.this);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentUserId = user.getUid();

        firebaseDatabase = FirebaseDatabase.getInstance("https://lovelybnb-b90d2-default-rtdb.asia-southeast1.firebasedatabase.app");
        detailRef = firebaseDatabase.getReference("Items");
        sliderRef = firebaseDatabase.getReference("Slider");
        userRef = firebaseDatabase.getReference("Registered users");
        hostRef = firebaseDatabase.getReference("Item Detail");

        detailItemDes = findViewById(R.id.detailItemDes);
        detailItemName = findViewById(R.id.detailItemName);
        detailItemPlace = findViewById(R.id.detailItemPlace);
        detailItemPrice = findViewById(R.id.detailItemPrice);
        detailItemRating = findViewById(R.id.detailItemRating);
        sparkButton = findViewById(R.id.spark_button);

        hostEmail = findViewById(R.id.hostEmail);
        hostAvatar = findViewById(R.id.hostAvatar);
        hostName = findViewById(R.id.hostName);
        hostPhone = findViewById(R.id.hostPhone);
        address = findViewById(R.id.address);
        checkIn = findViewById(R.id.checkInTime);
        checkOut = findViewById(R.id.checkOutTime);


        favorite = new Favorite();

        imageSlider = findViewById(R.id.imageSlider);
        final List<SlideModel> slideModelArrayList = new ArrayList<>();

        goback = findViewById(R.id.backprevious);
        goback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        itemId = getIntent().getStringExtra("itemId");
        if (itemId != null){
            getDetailItem(itemId);
            getDetailImgSlider(itemId, slideModelArrayList);
            getdetailHost(itemId);
        }

        opensheet = findViewById(R.id.openSheet);

        opensheet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("itemId", itemId);
                BottomSheetFragment bottomSheetFragment = new BottomSheetFragment();
                bottomSheetFragment.setArguments(bundle);
                bottomSheetFragment.show(getSupportFragmentManager(),bottomSheetFragment.getTag());

            }
        });



    }

    private void getdetailHost(String itemId) {
        hostRef.child(itemId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String HostName = snapshot.child("host name").getValue().toString();
                String HostEmail = snapshot.child("host email").getValue().toString();
                String HostPhone = snapshot.child("host phone").getValue().toString();
                String HostAvatar = snapshot.child("host avatar").getValue().toString();
                String ItemAddress = snapshot.child("address").getValue().toString();
                String CheckIn = snapshot.child("check in").getValue().toString();
                String CheckOut = snapshot.child("check out").getValue().toString();


                hostName.setText(HostName);
                hostEmail.setText(HostEmail);
                hostPhone.setText(HostPhone);
                address.setText(ItemAddress);
                checkIn.setText(CheckIn);
                checkOut.setText(CheckOut);
                Picasso.get().load(HostAvatar).into(hostAvatar);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getDetailImgSlider(String itemId, List<SlideModel> slideModelArrayList) {

        sliderRef.child(itemId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    slideModelArrayList.add(new SlideModel(dataSnapshot.child("url").getValue().toString(),ScaleTypes.CENTER_CROP));
                    imageSlider.setImageList(slideModelArrayList, ScaleTypes.CENTER_CROP);
                    imageSlider.setItemClickListener(new ItemClickListener() {
                        @Override
                        public void onItemSelected(int i) {
                            Intent intent = new Intent(getApplicationContext(),ImageDetailActivity.class);
                            intent.putExtra("itemId",itemId);
                            startActivity(intent);
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getDetailItem(String itemId) {
        detailRef.child(itemId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String Name = snapshot.child("name").getValue().toString();
                String Price = snapshot.child("price").getValue().toString();
                String Place = snapshot.child("place").getValue().toString();
                String Rating = snapshot.child("rating").getValue().toString();
                String Description = snapshot.child("description").getValue().toString();
                String Image = snapshot.child("image").getValue().toString();

                detailItemDes.setText(Description);
                detailItemName.setText(Name);
                detailItemPlace.setText(Place);
                detailItemPrice.setText(Price);
                detailItemRating.setText(Rating);

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                String currentUserId = user.getUid();

                favoriteCheck();

                favorite.setfavoriteName(Name);
                favorite.setfavoritePrice(Price);
                favorite.setfavoriteRating(Rating);
                favorite.setfavoritePlace(Place);
                favorite.setfavoriteImage(Image);

                sparkButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sparkButton.playAnimation();
                        if (isInMyFavorite){
                            userRef.child(currentUserId).child("Favorites").child(itemId).removeValue()
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Toast.makeText(ItemDetailActivity.this, "Removed from favorite list", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }else {
                            userRef.child(currentUserId).child("Favorites").child(itemId).setValue(favorite)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Toast.makeText(ItemDetailActivity.this, "Added to favorite list", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void favoriteCheck() {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final String userId = user.getUid();

        userRef.child(userId).child("Favorites").child(itemId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        isInMyFavorite = snapshot.exists();
                        if (isInMyFavorite){
                            sparkButton.setChecked(true);
                        }else {
                            sparkButton.setChecked(false);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        geocoder = new Geocoder(this,Locale.getDefault());
        List<Address> addressList = null;

        hostRef.child(itemId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String ItemAddress = snapshot.child("address").getValue().toString();

                try {
                    List<Address> addressList = geocoder.getFromLocationName(ItemAddress,1);

                    if (addressList != null && addressList.size() > 0){
                        Address address = addressList.get(0);

                        LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                        mMap.addMarker(new MarkerOptions().position(latLng).title(ItemAddress).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE)));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,15));
                        mMap.getUiSettings().setZoomControlsEnabled(true);
                    }
                    else {
                        Toast.makeText(ItemDetailActivity.this, "Can't find location",Toast.LENGTH_SHORT).show();

                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }

}