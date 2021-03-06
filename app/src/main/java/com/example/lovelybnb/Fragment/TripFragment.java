package com.example.lovelybnb.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lovelybnb.Adapter.InspireViewHolder;
import com.example.lovelybnb.Adapter.TripViewHolder;
import com.example.lovelybnb.Data.Receipt;
import com.example.lovelybnb.ItemClickListener;
import com.example.lovelybnb.R;
import com.example.lovelybnb.ReceiptActivity;
import com.example.lovelybnb.SearchActivity;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;


public class TripFragment extends Fragment {
    FirebaseDatabase firebaseDatabase;
    DatabaseReference receiptRef;
    RecyclerView rvTrip;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    Receipt receipt;
    FirebaseRecyclerAdapter<Receipt, TripViewHolder> adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_trip, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        firebaseDatabase = FirebaseDatabase.getInstance("https://lovelybnb-b90d2-default-rtdb.asia-southeast1.firebasedatabase.app");
        receiptRef = firebaseDatabase.getReference("Receipt");

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        rvTrip = view.findViewById(R.id.rvTrip);
        rvTrip.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        receipt = new Receipt();
        getTripData();

    }

    @Override
    public void onStart() {
        super.onStart();
        receipt = new Receipt();
        getTripData();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    private void getTripData() {
        String currentUserId = firebaseUser.getUid();
        Query query = receiptRef.child(currentUserId);
        FirebaseRecyclerOptions<Receipt> options = new FirebaseRecyclerOptions.Builder<Receipt>().setQuery(query,Receipt.class).build();

        adapter = new FirebaseRecyclerAdapter<Receipt, TripViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull TripViewHolder holder, int position, @NonNull Receipt model) {
                String id = getRef(position).getKey();

                receiptRef.child(currentUserId).child(id).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String name = snapshot.child("receiptName").getValue().toString();
                        String address = snapshot.child("receiptAddress").getValue().toString();
                        String img = snapshot.child("receiptImg").getValue().toString();
                        String checkinday = snapshot.child("receiptDaycheckin").getValue().toString();
                        String checkoutday = snapshot.child("receiptDaycheckout").getValue().toString();

                        holder.TripName.setText(name);
                        holder.TripAddress.setText(address);
                        holder.TripCheckIn.setText(checkinday);
                        holder.TripCheckOut.setText(checkoutday);
                        Picasso.get().load(img).into(holder.TripImg);

                        holder.setItemClickListener(new ItemClickListener() {
                            @Override
                            public void onClick(View view, int position, boolean isLongClick) {
                                Intent intent = new Intent(getContext(), ReceiptActivity.class);
                                intent.putExtra("itemId", adapter.getRef(position).getKey());
                                startActivity(intent);
                                getActivity().overridePendingTransition(R.anim.zoom_in,R.anim.no_animation);
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
            public TripViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_trip, parent,false);
                TripViewHolder viewHolder = new TripViewHolder(view);
                return viewHolder;
            }
        };
        rvTrip.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        adapter.startListening();
    }
}