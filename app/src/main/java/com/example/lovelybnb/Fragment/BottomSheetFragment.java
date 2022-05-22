package com.example.lovelybnb.Fragment;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lovelybnb.Data.Receipt;
import com.example.lovelybnb.MainActivity;
import com.example.lovelybnb.R;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;


public class BottomSheetFragment extends BottomSheetDialogFragment {

    TextView orderPrice, totalPrice, orderAddress, orderName, orderPlace,checkinday,checkoutday, checkinTime,checkoutTime, orderContact;
    ImageView orderImg;

    int mDayIn,mMonthIn,mYearIn, mDayOut,mMonthOut,mYearOut;
    int priceint, fullprice;
    String itemId;
    LinearLayout linearOut, linearIn;
    Button orderBtn;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference detailRef, hostRef, userRef;
    Date dateStart;
    Date dateEnd;
    String dayDifference = "";
    String img;

    public BottomSheetFragment() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_bottom_sheet, container, false);

        firebaseDatabase = FirebaseDatabase.getInstance("https://lovelybnb-b90d2-default-rtdb.asia-southeast1.firebasedatabase.app");
        detailRef = firebaseDatabase.getReference("Items");
        hostRef = firebaseDatabase.getReference("Item Detail");
        userRef = firebaseDatabase.getReference("Registered users");

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentUserId = user.getUid();

        orderName = view.findViewById(R.id.orderItemName);
        orderPrice = view.findViewById(R.id.orderItemPrice);
        orderPlace = view.findViewById(R.id.orderItemPlace);
        orderAddress = view.findViewById(R.id.orderItemAddress);
        totalPrice = view.findViewById(R.id.orderItemPriceTotal);
        checkinday = view.findViewById(R.id.TextCheckIn);
        checkoutday = view.findViewById(R.id.TextCheckOut);
        orderImg = view.findViewById(R.id.orderItemImg);
        linearIn = view.findViewById(R.id.LinearIn);
        linearOut = view.findViewById(R.id.LinearOut);

        orderBtn = view.findViewById(R.id.placeOrder);
        checkinTime = view.findViewById(R.id.checkInTime);
        checkoutTime = view.findViewById(R.id.checkOutTime);
        orderContact = view.findViewById(R.id.orderItemContact);

        checkoutday.setText(checkinday.getText().toString());

        itemId = getArguments().getString("itemId");
        getOrderItem();

        linearIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Calendar calendar = Calendar.getInstance();
                //set time zone
                calendar.setTimeZone(TimeZone.getTimeZone("UTC"));

                int selectedYear = calendar.get(Calendar.YEAR);
                int selectedMonth = calendar.get(Calendar.MONTH);
                int selectedDay = calendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                        new DatePickerDialog.OnDateSetListener() {
                            public void onDateSet(DatePicker view, int selectedYear,
                                                  int selectedMonth, int selectedDay) {
                                mDayIn = selectedDay;
                                mMonthIn = selectedMonth;
                                mYearIn = selectedYear;

                                checkinday.setText(mDayIn + "-" + (mMonthIn+1) + "-" + mYearIn);
                            }
                        }, mDayIn, mMonthIn, mYearIn);


                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);

                final Calendar calendar2 = Calendar.getInstance();
                calendar2.set(2023, 1, 1);
                datePickerDialog.getDatePicker().setMaxDate(calendar2.getTimeInMillis());
                datePickerDialog.setTitle("Select Date Check In");
                datePickerDialog.show();
                checkoutday.setText(null);
            }
        });

        linearOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkinday.getText().toString().equals("")){
                    Toast.makeText(getContext(),"You have to choose check in day first",Toast.LENGTH_SHORT).show();
                }else {
                    final Calendar calendar = Calendar.getInstance();
                    //set time zone
                    calendar.setTimeZone(TimeZone.getTimeZone("UTC"));

                    int selectedYear = calendar.get(Calendar.YEAR);
                    int selectedMonth = calendar.get(Calendar.MONTH);
                    int selectedDay = calendar.get(Calendar.DAY_OF_MONTH);
                    DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                            new DatePickerDialog.OnDateSetListener() {
                                public void onDateSet(DatePicker view, int selectedYear,
                                                      int selectedMonth, int selectedDay) {
                                    mDayOut = selectedDay;
                                    mMonthOut = selectedMonth;
                                    mYearOut = selectedYear;

                                    checkoutday.setText(mDayOut + "-" + (mMonthOut+1) + "-" + mYearOut);

                                        try {
                                            String startDay = checkinday.getText().toString();
                                            String endDay = checkoutday.getText().toString();


                                            SimpleDateFormat dates = new SimpleDateFormat("dd-MM-yyyy");
                                            dateStart = dates.parse(startDay);
                                            dateEnd = dates.parse(endDay);
                                            long dateStartInMs = dateStart.getTime();
                                            long dateEndInMs = dateEnd.getTime();

                                            long difference = 0;
                                            if (dateStartInMs>dateEndInMs){
                                                difference = dateStartInMs - dateEndInMs;
                                            }else {
                                                difference = dateEndInMs - dateStartInMs;
                                            }

                                            int differenceDates = (int) (difference / (24 * 60 * 60 * 1000));
                                            dayDifference = Long.toString(differenceDates);

                                            Toast.makeText(getContext(),"You choose "+dayDifference+" day",Toast.LENGTH_SHORT).show();
                                            fullprice = (int) (priceint*(differenceDates));
                                            totalPrice.setText(String.valueOf(fullprice));

                                        }catch (Exception e) {
                                            e.printStackTrace();

                                        }



                                }
                            }, mDayOut, mMonthOut, mYearOut);


                    final Calendar calendar2 = Calendar.getInstance();
                    calendar2.set(mYearIn, mMonthIn, mDayIn + 1);
                    datePickerDialog.getDatePicker().setMinDate(calendar2.getTimeInMillis());


                    final Calendar calendar3 = Calendar.getInstance();
                    calendar3.set(2023, 1, 1);
                    datePickerDialog.getDatePicker().setMaxDate(calendar3.getTimeInMillis());
                    datePickerDialog.setTitle("Select Date Check Out");
                    datePickerDialog.show();

                }
            }

        });

        Receipt receipt = new Receipt();

        orderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (dayDifference.isEmpty()){
                Toast.makeText(getContext(), "You have to choose check in and check out day", Toast.LENGTH_SHORT).show();
                }else {

                    receipt.setReceiptAddress(orderAddress.getText().toString());
                    receipt.setReceiptName(orderName.getText().toString());
                    receipt.setReceiptPlace(orderPlace.getText().toString());
                    receipt.setReceiptPrice(totalPrice.getText().toString());
                    receipt.setReceiptDaycheckin(checkinday.getText().toString());
                    receipt.setReceiptDaycheckout(checkoutday.getText().toString());
                    receipt.setDayStay(dayDifference);
                    receipt.setReceiptImg(img);
                    receipt.setReceiptContact(orderContact.getText().toString());

                    String dayOrder = checkinday.getText().toString();

                    userRef.child(currentUserId).child("Trip").child(itemId).setValue(receipt).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(getContext(),"Request reservation successfully",Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getContext(), MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getContext(),"Can't place Request reservation",Toast.LENGTH_SHORT).show();
                        }
                    });
                }

            }
        });


        return view;
    }

    private void getOrderItem() {
        detailRef.child(itemId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String name = snapshot.child("name").getValue().toString();
                img = snapshot.child("image").getValue().toString();
                String price = snapshot.child("price").getValue().toString();
                String place = snapshot.child("place").getValue().toString();

                orderName.setText(name);
                orderPlace.setText(place);
                orderPrice.setText(price);
                Picasso.get().load(img).into(orderImg);
                priceint = Integer.parseInt(orderPrice.getText().toString());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        hostRef.child(itemId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String checkin = snapshot.child("check in").getValue().toString();
                String checkout = snapshot.child("check out").getValue().toString();
                String address = snapshot.child("address").getValue().toString();
                String contact = snapshot.child("host phone").getValue().toString();

                orderAddress.setText(address);
                checkinTime.setText(checkin);
                checkoutTime.setText(checkout);
                orderContact.setText(contact);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}