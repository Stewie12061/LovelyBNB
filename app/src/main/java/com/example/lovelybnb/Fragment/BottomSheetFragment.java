package com.example.lovelybnb.Fragment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
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

    TextView quantity, orderPrice, totalPrice, orderAddress, orderName, orderPlace,checkinday,checkoutday, checkinTime,checkoutTime, orderContact;
    ImageView orderImg;

    int mDayIn,mMonthIn,mYearIn, mDayOut,mMonthOut,mYearOut;
    int priceint, fullprice;
    String itemId, currentUserId;
    LinearLayout linearOut, linearIn, lnCalender;
    Button orderBtn;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference detailRef, hostRef, receiptRef;
    Date dateStart;
    Date dateEnd;
    String dayDifference = "";
    String img;
    ImageButton add,minus;
    int totalQuantity = 1;
    Receipt receipt;

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
        receiptRef = firebaseDatabase.getReference("Receipt");

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        currentUserId = user.getUid();

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

        add = view.findViewById(R.id.btnAdd);
        minus = view.findViewById(R.id.btnMinus);
        quantity = view.findViewById(R.id.quantity);
        getHowManyPeople();

        receipt = new Receipt();
        startOrder();

        return view;
    }

    private void startOrder() {
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
                    receipt.setPeopleQuantity(quantity.getText().toString());
                    receipt.setReceiptTimeCheckin(checkinTime.getText().toString());
                    receipt.setReceiptTimeCheckout(checkoutTime.getText().toString());

                    String dayOrder = checkinday.getText().toString();

                    receiptRef.child(currentUserId).child(itemId).setValue(receipt).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Dialog dialog = new Dialog(getContext(),R.style.CustomDialog);
                            dialog.setContentView(R.layout.dialog_booking_loading);
                            new Handler().postDelayed(new Runnable() {
                                                          @Override
                                                          public void run() {
                                                              Intent intent = new Intent(getContext(),MainActivity.class);
                                                              intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                              int idTrip = 2;
                                                              String IDtrip = Integer.toString(idTrip);
                                                              intent.putExtra("Fragment",IDtrip);

                                                              dialog.dismiss();
                                                              startActivity(intent);
                                                          }
                                                      }, 5000
                            );

                            dialog.show();
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
    }

    private void getHowManyPeople() {
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (totalQuantity<10){
                    totalQuantity++;
                    quantity.setText(String.valueOf(totalQuantity));
                }
            }
        });

        minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (totalQuantity>1){
                    totalQuantity--;
                    quantity.setText(String.valueOf(totalQuantity));
                }
            }
        });
    }

    private void getOrderItem() {
        detailRef.child(itemId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String name = snapshot.child("itemName").getValue().toString();
                img = snapshot.child("itemImage").getValue().toString();
                String price = snapshot.child("itemPrice").getValue().toString();
                String place = snapshot.child("itemPlace").getValue().toString();

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
                String checkin = snapshot.child("checkIn").getValue().toString();
                String checkout = snapshot.child("checkOut").getValue().toString();
                String address = snapshot.child("address").getValue().toString();
                String contact = snapshot.child("hostPhone").getValue().toString();

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