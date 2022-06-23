package com.example.lovelybnb.FragmentAdmin;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.AlignmentSpan;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.example.lovelybnb.Adapter.ItemAdminViewHolder;
import com.example.lovelybnb.Adapter.PropertyItemsViewHolder;
import com.example.lovelybnb.CreateItemAdminActivity;
import com.example.lovelybnb.Data.Favorite;
import com.example.lovelybnb.Data.PropertyItems;
import com.example.lovelybnb.ItemAdminActivity;
import com.example.lovelybnb.ItemAdminDetailActivity;
import com.example.lovelybnb.ItemClickListener;
import com.example.lovelybnb.ItemDetailActivity;
import com.example.lovelybnb.R;
import com.example.lovelybnb.SearchActivity;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.UUID;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SearchAdminFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchAdminFragment extends Fragment {
    int PICK_IMG_REQUEST = 1705;
    Uri uri;

    SearchView searchView;
    RecyclerView rvSearch;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference itemRef,sliderRef,itemDetailRef;
    Toolbar toolbar;
    FirebaseRecyclerOptions<PropertyItems> options;
    FirebaseRecyclerAdapter<PropertyItems, ItemAdminViewHolder> adapter;

    LottieAnimationView searchWaiting;
    PropertyItems propertyItems;

    String itemPositionId, itemName, catename;

    String Name, Price, Place, Rating, Image, Description;
    public RoundedImageView imgItem;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SearchAdminFragment() {
        // Required empty public constructor
    }

    public static SearchAdminFragment newInstance(String param1, String param2) {
        SearchAdminFragment fragment = new SearchAdminFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search_admin, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        firebaseDatabase = FirebaseDatabase.getInstance("https://lovelybnb-b90d2-default-rtdb.asia-southeast1.firebasedatabase.app");
        itemRef = firebaseDatabase.getReference("Items");
        sliderRef = firebaseDatabase.getReference("Slider");
        itemDetailRef = firebaseDatabase.getReference("Item Detail");

        toolbar = view.findViewById(R.id.toolbarSearchAd);

        rvSearch = view.findViewById(R.id.rvSearchAdmin);
        rvSearch.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));

        searchWaiting = view.findViewById(R.id.searchWaiting);

        searchView = toolbar.findViewById(R.id.searchViewAdmin);
        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchWaiting.setVisibility(View.INVISIBLE);
                rvSearch.setVisibility(View.VISIBLE);
                searchView.setMinimumWidth(300);
                loadDataSearch("");

            }
        });
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                searchWaiting.setVisibility(View.VISIBLE);
                rvSearch.setVisibility(View.INVISIBLE);
                searchView.setMinimumWidth(50);
                return false;
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                loadDataSearch(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                loadDataSearch(newText);
                return false;
            }
        });
    }

    private void loadDataSearch(String searchText) {
        Query query;
        if(searchText==""){
            query = itemRef.orderByChild("itemName");
        }
        else {
            query = itemRef.orderByChild("itemName").startAt(searchText).endAt(searchText+"\uf8ff");
        }

        options = new FirebaseRecyclerOptions.Builder<PropertyItems>().setQuery(query,PropertyItems.class).build();
        adapter = new FirebaseRecyclerAdapter<PropertyItems, ItemAdminViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ItemAdminViewHolder holder, int position, @NonNull PropertyItems model) {
                String postKey = adapter.getRef(position).getKey();

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                String currentUserId = user.getUid();

                itemRef.child(postKey).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        Name = snapshot.child("itemName").getValue().toString();
                        Price = snapshot.child("itemPrice").getValue().toString();
                        Place = snapshot.child("itemPlace").getValue().toString();
                        Rating = snapshot.child("itemRating").getValue().toString();
                        Image = snapshot.child("itemImage").getValue().toString();

                        holder.ItemAdName.setText(Name);
                        holder.ItemAdPlace.setText(Place);
                        holder.ItemAdPrice.setText(Price);
                        holder.ItemAdRating.setText(Rating);
                        Picasso.get().load(Image).into(holder.ItemAdImg);

                        holder.btnDeleteItem.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                itemPositionId = adapter.getRef(holder.getBindingAdapterPosition()).getKey();
                                itemName = Name;
                                deleteItem();
                            }
                        });
                        holder.setItemClickListener(new ItemClickListener() {
                            @Override
                            public void onClick(View view, int position, boolean isLongClick) {
                                Intent intent = new Intent(getContext(), ItemAdminDetailActivity.class);
                                intent.putExtra("itemAdId",adapter.getRef(position).getKey());
                                intent.putExtra("itemAdName",Name);
                                startActivity(intent);
                            }
                        });
                        holder.btnModifyItem.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                itemPositionId = adapter.getRef(holder.getBindingAdapterPosition()).getKey();
                                updateItem();
                            }
                        });

                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
            }

            @NonNull
            @Override
            public ItemAdminViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_item_admin, parent,false);
                ItemAdminViewHolder viewHolder = new ItemAdminViewHolder(view);
                return viewHolder;
            }
        };
        rvSearch.setAdapter(adapter);
        adapter.startListening();
    }

    private void updateItem() {
        AlertDialog.Builder builder =
                new AlertDialog.Builder
                        (getContext());
        View view = LayoutInflater.from(getContext()).inflate(
                R.layout.dialog_update_item,
                (NestedScrollView) getView().findViewById(R.id.layoutDialogContainer)
        );
        builder.setView(view);

        ((TextView) view.findViewById(R.id.textTitle))
                .setText("Update Item");
        ((TextView) view.findViewById(R.id.textMessage))
                .setText("Fill all information to update");
        ((Button) view.findViewById(R.id.buttonYes))
                .setText("Update");
        ((Button) view.findViewById(R.id.buttonNo))
                .setText("Cancel");

        Button select = (Button) view.findViewById(R.id.btnSelect);
        Button upload = (Button) view.findViewById(R.id.btnUpload);
        EditText itemName = (EditText) view.findViewById(R.id.edtItemName);
        EditText itemPlace = (EditText) view.findViewById(R.id.edtItemPlace);
        EditText itemPrice = (EditText) view.findViewById(R.id.edtItemPrice);
        EditText itemRating = (EditText) view.findViewById(R.id.edtItemRating);
        EditText itemDes = (EditText) view.findViewById(R.id.edtItemDes);
        imgItem = view.findViewById(R.id.imgItem);

        itemRef.child(itemPositionId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Name = snapshot.child("itemName").getValue().toString();
                Price = snapshot.child("itemPrice").getValue().toString();
                Place = snapshot.child("itemPlace").getValue().toString();
                Rating = snapshot.child("itemRating").getValue().toString();
                Image = snapshot.child("itemImage").getValue().toString();
                Description = snapshot.child("itemDescription").getValue().toString();

                itemName.setText(Name);
                itemPlace.setText(Place);
                itemPrice.setText(Price);
                itemRating.setText(Rating);
                itemDes.setText(Description);
                Picasso.get().load(Image).into(imgItem);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImg();
            }

        });

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (uri!=null){
                    ProgressDialog progressDialog;
                    progressDialog = new ProgressDialog(getContext());
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
                            Toast.makeText(getContext(),"Upload succeed",Toast.LENGTH_SHORT).show();

                            //get uri img from storage
                            imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    propertyItems = new PropertyItems(itemDes.getText().toString(),itemPositionId,uri.toString(),itemName.getText().toString(),itemPlace.getText().toString(),itemPrice.getText().toString(),itemRating.getText().toString());
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(getContext(),"Upload failed",Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

        final AlertDialog alertDialog = builder.create();

        view.findViewById(R.id.buttonYes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
                    if (uri==null){
                        alertDialog.dismiss();
                        propertyItems = new PropertyItems(itemDes.getText().toString(),itemPositionId,Image,itemName.getText().toString(),itemPlace.getText().toString(),itemPrice.getText().toString(),itemRating.getText().toString());
                        itemRef.child(itemPositionId).setValue(propertyItems).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(getContext(),"Update succeed",Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    else{
                        alertDialog.dismiss();
                        itemRef.child(itemPositionId).setValue(propertyItems).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(getContext(),"Update succeed",Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }


            }
        });
        view.findViewById(R.id.buttonNo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
        if (alertDialog.getWindow() != null){
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        alertDialog.show();
    }


    private void deleteItem() {
        AlertDialog.Builder builder =
                new AlertDialog.Builder
                        (getContext());
        View view = LayoutInflater.from(getContext()).inflate(
                R.layout.dialog_alert,
                (ConstraintLayout) getView().findViewById(R.id.layoutDialogContainer)
        );
        builder.setView(view);
        ((TextView) view.findViewById(R.id.textTitle))
                .setText("Delete item");
        ((TextView) view.findViewById(R.id.textMessage))
                .setText("Are you sure you want delete"+ " "+itemName+" "+"?");
        ((Button) view.findViewById(R.id.buttonYes))
                .setText("Yes");
        ((Button) view.findViewById(R.id.buttonNo))
                .setText("No");
        final AlertDialog alertDialog = builder.create();
        view.findViewById(R.id.buttonYes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                itemRef.child(itemPositionId).removeValue();
                sliderRef.child(itemPositionId).removeValue();
                itemDetailRef.child(itemPositionId).removeValue();
                alertDialog.dismiss();
            }
        });
        view.findViewById(R.id.buttonNo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
        if (alertDialog.getWindow() != null){
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        alertDialog.show();
    }

    private void chooseImg() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Picture"),PICK_IMG_REQUEST);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMG_REQUEST && resultCode == Activity.RESULT_OK
                && data!=null && data.getData() != null){
            uri = data.getData();
            if (null != uri) {
                // update the preview image in the layout
                imgItem.setImageURI(uri);
            }
        }
    }
}