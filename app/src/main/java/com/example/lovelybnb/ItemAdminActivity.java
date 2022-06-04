package com.example.lovelybnb;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.AlignmentSpan;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lovelybnb.Adapter.ItemAdminViewHolder;
import com.example.lovelybnb.Adapter.PropertyItemsViewHolder;
import com.example.lovelybnb.Data.PropertyItems;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class ItemAdminActivity extends AppCompatActivity {
    TextView countItem, itemCateName,goback;
    RecyclerView rvItemAd;
    FloatingActionButton openCreateItem;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference itemRef,cateRef;

    FloatingActionButton openAddItem;

    String cateId;
    FirebaseRecyclerAdapter<PropertyItems, ItemAdminViewHolder> adapter;

    String itemPositionId, itemName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_admin);

        firebaseDatabase = FirebaseDatabase.getInstance("https://lovelybnb-b90d2-default-rtdb.asia-southeast1.firebasedatabase.app");
        itemRef = firebaseDatabase.getReference("Items");

        goback = findViewById(R.id.backprevious);
        goback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        rvItemAd = findViewById(R.id.rvItemAdmin);
        rvItemAd.setLayoutManager(new LinearLayoutManager(ItemAdminActivity.this,LinearLayoutManager.VERTICAL,false));

        cateId = getIntent().getStringExtra("categoryId");

        openAddItem = findViewById(R.id.openCreateItem);
        openAddItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createItem();
            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        if (cateId!=null){
            getItemData();
        }
    }

    private void getItemData() {
        Query query = itemRef.orderByChild("propertyTypeId").equalTo(cateId);
        FirebaseRecyclerOptions<PropertyItems> options = new FirebaseRecyclerOptions.Builder<PropertyItems>().setQuery(query,PropertyItems.class).build();

        adapter = new FirebaseRecyclerAdapter<PropertyItems, ItemAdminViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ItemAdminViewHolder holder, int position, @NonNull PropertyItems model) {
                String postKey = getRef(position).getKey();

                itemRef.child(postKey).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        String Name = snapshot.child("itemName").getValue().toString();
                        String Price = snapshot.child("itemPrice").getValue().toString();
                        String Place = snapshot.child("itemPlace").getValue().toString();
                        String Rating = snapshot.child("itemRating").getValue().toString();
                        String Image = snapshot.child("itemImage").getValue().toString();

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
                                Intent intent = new Intent(ItemAdminActivity.this, ItemAdminDetailActivity.class);
                                intent.putExtra("itemAdId",adapter.getRef(position).getKey());
                                startActivity(intent);
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
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_item_admin,parent,false);
                ItemAdminViewHolder viewHolder = new ItemAdminViewHolder(view);
                return viewHolder;
            }
        };
        rvItemAd.setAdapter(adapter);
        adapter.startListening();
    }

    private void createItem() {

    }

    private void deleteItem() {
        AlertDialog.Builder builder =
                new AlertDialog.Builder
                        (ItemAdminActivity.this);
        View view = LayoutInflater.from(ItemAdminActivity.this).inflate(
                R.layout.dialog_alert,
                (ConstraintLayout) findViewById(R.id.layoutDialogContainer)
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
}