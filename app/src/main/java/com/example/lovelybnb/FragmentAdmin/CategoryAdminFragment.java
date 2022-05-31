package com.example.lovelybnb.FragmentAdmin;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.lovelybnb.Adapter.CateAdminViewHolder;
import com.example.lovelybnb.Data.PropertyType;
import com.example.lovelybnb.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CategoryAdminFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CategoryAdminFragment extends Fragment {

    RecyclerView rvAdCate;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference cateRef;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public CategoryAdminFragment() {
        // Required empty public constructor
    }

    public static CategoryAdminFragment newInstance(String param1, String param2) {
        CategoryAdminFragment fragment = new CategoryAdminFragment();
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
        return inflater.inflate(R.layout.fragment_category_admin, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        firebaseDatabase = FirebaseDatabase.getInstance("https://lovelybnb-b90d2-default-rtdb.asia-southeast1.firebasedatabase.app");
        cateRef = firebaseDatabase.getReference("Categories");

        rvAdCate = view.findViewById(R.id.rvCategoryAdmin);
        rvAdCate.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

    }

    @Override
    public void onStart() {
        super.onStart();
        getCateAdmin();
    }

    private void getCateAdmin() {
        FirebaseRecyclerOptions<PropertyType> options = new FirebaseRecyclerOptions.Builder<PropertyType>().setQuery(cateRef,PropertyType.class).build();

        FirebaseRecyclerAdapter<PropertyType, CateAdminViewHolder> adapter = new FirebaseRecyclerAdapter<PropertyType, CateAdminViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull CateAdminViewHolder holder, int position, @NonNull PropertyType model) {
                String id = getRef(position).getKey();

                cateRef.child(id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String propertyname = snapshot.child("Name").getValue().toString();
                        String propertyImg = snapshot.child("Image").getValue().toString();

                        holder.cateadName.setText(propertyname);
                        Picasso.get().load(propertyImg).into(holder.cateadImg);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @NonNull
            @Override
            public CateAdminViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cate_admin,parent,false);
                CateAdminViewHolder viewHolder = new CateAdminViewHolder(view);
                return viewHolder;
            }
        };
        rvAdCate.setAdapter(adapter);
        adapter.startListening();
    }
}