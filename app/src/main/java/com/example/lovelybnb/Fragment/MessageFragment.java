package com.example.lovelybnb.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lovelybnb.Adapter.ItemUserMessageViewHolder;
import com.example.lovelybnb.Data.AdminProfile;
import com.example.lovelybnb.ItemClickListener;
import com.example.lovelybnb.MessageActivity;
import com.example.lovelybnb.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class MessageFragment extends Fragment {

    FirebaseDatabase firebaseDatabase;
    DatabaseReference userRef;

    RecyclerView rvAdminList;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    private String mParam1;
    private String mParam2;

    public MessageFragment() {
        // Required empty public constructor
    }

    public static MessageFragment newInstance(String param1, String param2) {
        MessageFragment fragment = new MessageFragment();
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
        return inflater.inflate(R.layout.fragment_message, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        firebaseDatabase = FirebaseDatabase.getInstance("https://lovelybnb-b90d2-default-rtdb.asia-southeast1.firebasedatabase.app");
        userRef = firebaseDatabase.getReference("Registered users");

        rvAdminList = view.findViewById(R.id.rvAdminList);
        rvAdminList.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));


    }

    @Override
    public void onStart() {
        super.onStart();
        getAdList();
    }

    private void getAdList() {
        Query query = userRef.orderByChild("Role").equalTo("Admin");
        FirebaseRecyclerOptions<AdminProfile> options = new FirebaseRecyclerOptions.Builder<AdminProfile>().setQuery(query,AdminProfile.class).build();
        FirebaseRecyclerAdapter<AdminProfile, ItemUserMessageViewHolder> adapter = new FirebaseRecyclerAdapter<AdminProfile, ItemUserMessageViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ItemUserMessageViewHolder holder, int position, @NonNull AdminProfile model) {
                String id = getRef(position).getKey();
                userRef.child(id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String name = snapshot.child("FullName").getValue().toString();
                        String email = snapshot.child("Email").getValue().toString();
                        String gender = snapshot.child("Gender").getValue().toString();
                        String phone = snapshot.child("PhoneNumber").getValue().toString();
                        String avatar = snapshot.child("avatar").getValue().toString();

                        holder.adminPhone.setText(phone);
                        holder.adminMail.setText(email);
                        holder.adminName.setText(name);
                        holder.adminGender.setText(gender);
                        Picasso.get().load(avatar).into(holder.adminAvatar);

                        holder.setItemClickListener(new ItemClickListener() {
                            @Override
                            public void onClick(View view, int position, boolean isLongClick) {
                                Intent intent = new Intent(getContext(), MessageActivity.class);
                                intent.putExtra("idAdmin",id);
                                Toast.makeText(getContext(),id,Toast.LENGTH_SHORT).show();
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
            public ItemUserMessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_admin_list,parent,false);
                ItemUserMessageViewHolder viewHolder = new ItemUserMessageViewHolder(view);
                return viewHolder;
            }
        };
        rvAdminList.setAdapter(adapter);
        adapter.startListening();
    }

}