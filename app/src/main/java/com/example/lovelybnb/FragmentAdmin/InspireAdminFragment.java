package com.example.lovelybnb.FragmentAdmin;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lovelybnb.Adapter.CateAdminViewHolder;
import com.example.lovelybnb.Adapter.InspireAdminViewHolder;
import com.example.lovelybnb.Data.Inspire;
import com.example.lovelybnb.Data.PropertyType;
import com.example.lovelybnb.ItemAdminActivity;
import com.example.lovelybnb.ItemClickListener;
import com.example.lovelybnb.MainAdminActivity;
import com.example.lovelybnb.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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
 * Use the {@link InspireAdminFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class InspireAdminFragment extends Fragment {

    RecyclerView rvAdInspire;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference inspireRef;
    FloatingActionButton openCreateInspire;

    private final int PICK_IMG_REQUEST = 1705;
    Uri uri = null;
    RoundedImageView imgInspire;

    String inspireId, inspirePlace, img, inspirePositionId;
    Inspire inspire;
    ArrayList<String> arrayList = null;
    FirebaseRecyclerAdapter<Inspire, InspireAdminViewHolder> adapter;

    private TextView countInspire;

    boolean isUpLoad=false;

    String isNavBarExpand;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public InspireAdminFragment() {
        // Required empty public constructor
    }

    public static InspireAdminFragment newInstance(String param1, String param2) {
        InspireAdminFragment fragment = new InspireAdminFragment();
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
        return inflater.inflate(R.layout.fragment_inspire_admin, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        firebaseDatabase = FirebaseDatabase.getInstance("https://lovelybnb-b90d2-default-rtdb.asia-southeast1.firebasedatabase.app");
        inspireRef = firebaseDatabase.getReference("Inspire Data");

        rvAdInspire = view.findViewById(R.id.rvInspireAdmin);
        rvAdInspire.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        inspire = new Inspire();
        openCreateInspire = view.findViewById(R.id.openCreateInspire);
        openCreateInspire.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addInspire();
            }
        });

        countInspire = view.findViewById(R.id.countInspire);
        //get id for new category
        inspireRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                arrayList = new ArrayList<String>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    arrayList.add(dataSnapshot.getKey());
                }
                //count cate
                countInspire.setText(Integer.toString(arrayList.size()));

                //get last item in cate and create id for new cate
                String inspireidString = arrayList.get(arrayList.size()-1);
                int inspireidInt = Integer.parseInt(inspireidString) +1;
                inspireId = Integer.toString(inspireidInt);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        getInspireAdmin();
    }

    private void getInspireAdmin() {
        FirebaseRecyclerOptions<Inspire> options = new FirebaseRecyclerOptions.Builder<Inspire>().setQuery(inspireRef,Inspire.class).build();

        adapter = new FirebaseRecyclerAdapter<Inspire, InspireAdminViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull InspireAdminViewHolder holder, int position, @NonNull Inspire model) {
                String id = getRef(position).getKey();

                inspireRef.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String inspireplace = snapshot.child("place").getValue().toString();
                        String inspiredes = snapshot.child("description").getValue().toString();
                        String inspireimg = snapshot.child("image").getValue().toString();

                        holder.inspireAdPlace.setText(inspireplace);
//                        holder.inspireAdDes.setText(inspiredes);
                        Picasso.get().load(inspireimg).into(holder.inspireAdImg);


                        //edit inspire
                        holder.btnmodify.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //get cate id onclick
                                inspirePositionId = adapter.getRef(holder.getBindingAdapterPosition()).getKey();
                                modifyCate();
                            }
                        });

                        //delete inspire
                        holder.btndelete.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                inspirePositionId = adapter.getRef(holder.getBindingAdapterPosition()).getKey();
                                inspirePlace = inspireplace;
                                deleteCate();
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
            public InspireAdminViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_inspire_admin,parent,false);
                InspireAdminViewHolder viewHolder = new InspireAdminViewHolder(view);
                return viewHolder;
            }
        };
        rvAdInspire.setAdapter(adapter);
        adapter.startListening();
    }

    private void modifyCate() {
        AlertDialog.Builder builder =
                new AlertDialog.Builder
                        (getContext());
        View view = LayoutInflater.from(getContext()).inflate(
                R.layout.dialog_inspire_admin,
                (ConstraintLayout) getView().findViewById(R.id.layoutDialogContainer)
        );
        builder.setView(view);

        ((TextView) view.findViewById(R.id.textTitle))
                .setText("Update Inspire");
        ((TextView) view.findViewById(R.id.textMessage))
                .setText("Fill all information upload image");
        ((Button) view.findViewById(R.id.buttonYes))
                .setText("Update");
        ((Button) view.findViewById(R.id.buttonNo))
                .setText("Cancel");

        Button select = (Button) view.findViewById(R.id.btnSelect);
        Button upload = (Button) view.findViewById(R.id.btnUpload);
        EditText inspirePlace = (EditText) view.findViewById(R.id.edtInspirePlace);
        EditText inspireDes = (EditText) view.findViewById(R.id.edtInspireDes);
        imgInspire = (RoundedImageView) view.findViewById(R.id.imgInspire);

        inspireRef.child(inspirePositionId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                img = snapshot.child("image").getValue().toString();
                inspirePlace.setText(snapshot.child("place").getValue().toString());
                inspireDes.setText(snapshot.child("description").getValue().toString());
                Picasso.get().load(img).into(imgInspire);
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
                    StorageReference imageFolder = storageReference.child("Images/inspires/"+imageName);

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
                                    inspire = new Inspire(inspirePlace.getText().toString(),inspireDes.getText().toString(),uri.toString());
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            isUpLoad=false;
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
                if (TextUtils.isEmpty(inspirePlace.getText().toString().trim())){
                    inspirePlace.setError("You have to fill this information!");
                    inspirePlace.requestFocus();
                }
                if (TextUtils.isEmpty(inspireDes.getText().toString().trim())){
                    inspireDes.setError("You have to fill this information!");
                    inspireDes.requestFocus();
                }
                else {
                    if (uri==null){
                        alertDialog.dismiss();
                        inspire = new Inspire(inspirePlace.getText().toString(),inspireDes.getText().toString(),img);
                        inspireRef.child(inspirePositionId).setValue(inspire).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(getContext(),"Update succeed",Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    else{
                        if (isUpLoad==false){
                            Toast.makeText(getContext(),"You have to upload image",Toast.LENGTH_SHORT).show();
                        }
                        alertDialog.dismiss();
                        inspireRef.child(inspirePositionId).setValue(inspire).addOnSuccessListener(new OnSuccessListener<Void>() {
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

    private void deleteCate() {
        AlertDialog.Builder builder =
                new AlertDialog.Builder
                        (getContext());
        View view = LayoutInflater.from(getContext()).inflate(
                R.layout.dialog_alert,
                (ConstraintLayout) getView().findViewById(R.id.layoutDialogContainer)
        );
        builder.setView(view);
        ((TextView) view.findViewById(R.id.textTitle))
                .setText("Delete inspire");
        ((TextView) view.findViewById(R.id.textMessage))
                .setText("Are you sure you want delete"+ " "+inspirePlace+" "+"inspire?");
        ((Button) view.findViewById(R.id.buttonYes))
                .setText("Yes");
        ((Button) view.findViewById(R.id.buttonNo))
                .setText("No");
        final AlertDialog alertDialog = builder.create();
        view.findViewById(R.id.buttonYes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inspireRef.child(inspirePositionId).removeValue();
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

    private void addInspire() {
        AlertDialog.Builder builder =
                new AlertDialog.Builder
                        (getContext());
        View view = LayoutInflater.from(getContext()).inflate(
                R.layout.dialog_inspire_admin,
                (ConstraintLayout) getView().findViewById(R.id.layoutDialogContainer)
        );
        builder.setView(view);

        ((TextView) view.findViewById(R.id.textTitle))
                .setText("Add new Inspire place");
        ((TextView) view.findViewById(R.id.textMessage))
                .setText("Fill all information and upload image");
        ((Button) view.findViewById(R.id.buttonYes))
                .setText("Create");
        ((Button) view.findViewById(R.id.buttonNo))
                .setText("Cancel");

        Button select = (Button) view.findViewById(R.id.btnSelect);
        Button upload = (Button) view.findViewById(R.id.btnUpload);
        EditText inspirePlace = (EditText) view.findViewById(R.id.edtInspirePlace);
        EditText inspireDes = (EditText) view.findViewById(R.id.edtInspireDes);
        imgInspire = (RoundedImageView) view.findViewById(R.id.imgInspire);

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
                    StorageReference imageFolder = storageReference.child("Images/inspires/"+imageName);

                    //put img to storage
                    imageFolder.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            isUpLoad=true;
                            progressDialog.dismiss();
                            Toast.makeText(getContext(),"Upload sussessed",Toast.LENGTH_SHORT).show();

                            //get uri img from storage
                            imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    inspire = new Inspire(inspirePlace.getText().toString(),inspireDes.getText().toString(),uri.toString());
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            isUpLoad=false;
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
                if (TextUtils.isEmpty(inspirePlace.getText().toString().trim())){
                    inspirePlace.setError("You have to fill this information!");
                    inspirePlace.requestFocus();
                }
                if (TextUtils.isEmpty(inspireDes.getText().toString().trim())){
                    inspireDes.setError("You have to fill this information!");
                    inspireDes.requestFocus();
                }
                else if(uri==null){
                    Toast.makeText(getContext(),"You have to fill full information to create category",Toast.LENGTH_SHORT).show();
                }
                else if (isUpLoad==false){
                    Toast.makeText(getContext(),"You have to upload image",Toast.LENGTH_SHORT).show();
                }
                else {
                    if (inspire != null){
                        alertDialog.dismiss();
                        inspireRef.child(inspireId).setValue(inspire).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(getContext(),"Success create category",Toast.LENGTH_SHORT).show();
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
                imgInspire.setImageURI(uri);
            }
        }
    }
}