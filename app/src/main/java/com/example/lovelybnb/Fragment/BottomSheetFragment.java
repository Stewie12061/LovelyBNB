package com.example.lovelybnb.Fragment;

import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.example.lovelybnb.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.Calendar;
import java.util.TimeZone;


public class BottomSheetFragment extends BottomSheetDialogFragment {

    EditText checkinday, checkoutday;
    TextView orderPrice, totalPrice, orderRating, orderName, orderPlace;

    int mDay,mMonth,mYear;
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
        checkinday = view.findViewById(R.id.editTextCheckIn);
        checkoutday = view.findViewById(R.id.editTextCheckOut);


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
                                mDay = selectedDay;
                                mMonth = selectedMonth;
                                mYear = selectedYear;
                                StringBuilder Date = new StringBuilder("");
                                String conver = Integer.toString(selectedYear);
                                Date.append(conver);
                                Date.append("-");
                                selectedMonth++;
                                conver = Integer.toString(selectedMonth);
                                Date.append(conver);
                                Date.append("-");
                                conver = Integer.toString(selectedDay);
                                Date.append(conver);
                                isDob = true;
                            }
                        }, mDay, mMonth, mYear);

                datePickerDialog.getDatePicker().setMaxDate(calendar.getTimeInMillis());


                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                datePickerDialog.setTitle("Select Date");
                datePickerDialog.show();


                final Calendar calendar3 = Calendar.getInstance();
                //Set Maximum date of calendar
                calendar3.set(2023, 1, 1);
                //Set One Month date from today date to calendar
                //calendar3.add(Calendar.MONTH, 1);
                datePickerDialog.getDatePicker().setMaxDate(calendar3.getTimeInMillis());
                datePickerDialog.setTitle("Select Date");
                datePickerDialog.show();
            }
        });



        return view;
    }
}