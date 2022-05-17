package com.example.lovelybnb.Fragment;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lovelybnb.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.Calendar;
import java.util.TimeZone;


public class BottomSheetFragment extends BottomSheetDialogFragment {

    TextView orderPrice, totalPrice, orderRating, orderName, orderPlace,checkinday,checkoutday;

    int mDayIn,mMonthIn,mYearIn, mDayOut,mMonthOut,mYearOut;
    int price, fullprice;
    boolean isDob;
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


        orderName = view.findViewById(R.id.orderItemName);
        orderPrice = view.findViewById(R.id.orderItemPrice);
        orderPlace = view.findViewById(R.id.orderItemPlace);
        orderRating = view.findViewById(R.id.orderItemRating);
        totalPrice = view.findViewById(R.id.orderItemPriceTotal);
        checkinday = view.findViewById(R.id.TextCheckIn);
        checkoutday = view.findViewById(R.id.TextCheckOut);

        checkinday.setText("Select day check in");
        checkoutday.setText("Select day check out");


         price = Integer.valueOf(orderPrice.getText().toString());
         fullprice=price;
         totalPrice.setText(String.valueOf(fullprice));

        checkinday.setOnClickListener(new View.OnClickListener() {
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

                                checkinday.setText(mDayIn + "/" + mMonthIn + "/" + mYearIn);
                            }
                        }, mDayIn, mMonthIn, mYearIn);


                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                datePickerDialog.setTitle("Select Date");


                final Calendar calendar2 = Calendar.getInstance();
                calendar2.set(2023, 1, 1);
                datePickerDialog.getDatePicker().setMaxDate(calendar2.getTimeInMillis());
                datePickerDialog.setTitle("Select Date");
                datePickerDialog.show();
            }
        });


        checkoutday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkinday.getText().toString().equals("Select day check in")){
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

                                    checkoutday.setText(mDayOut + "/" + mMonthOut + "/" + mYearOut);

                                    if (mMonthIn == mMonthOut){
                                        fullprice = price*(mDayOut-mDayIn);
                                        totalPrice.setText(String.valueOf(fullprice));
                                    }
                                }
                            }, mDayOut, mMonthOut, mYearOut);


                    final Calendar calendar2 = Calendar.getInstance();
                    calendar2.set(mYearIn, mMonthIn, mDayIn + 1);
                    datePickerDialog.getDatePicker().setMinDate(calendar2.getTimeInMillis());
                    datePickerDialog.setTitle("Select Date");

                    final Calendar calendar3 = Calendar.getInstance();
                    calendar3.set(2023, 1, 1);
                    datePickerDialog.getDatePicker().setMaxDate(calendar3.getTimeInMillis());
                    datePickerDialog.setTitle("Select Date");
                    datePickerDialog.show();



                }
            }

        });

        return view;
    }

}