package com.example.remoteexam_propelrr;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.time.Year;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    com.google.android.material.textfield.TextInputEditText getFullname, getEmail, getPhone;
    com.google.android.material.button.MaterialButton btnSubmit;
    TextView getAge, getDOB;
    ImageView imgGetDate;
    Spinner setGender;
    DatePickerDialog datePicker;

    String[] gender = {"Male", "Female"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getFullname = (com.google.android.material.textfield.TextInputEditText)findViewById(R.id.et_fullname);
        getEmail = (com.google.android.material.textfield.TextInputEditText)findViewById(R.id.et_email_address);
        getPhone = (com.google.android.material.textfield.TextInputEditText)findViewById(R.id.et_mobile_no);
        getDOB = (TextView) findViewById(R.id.tv_dob);
        getAge = (TextView) findViewById(R.id.tv_age);
        imgGetDate = (ImageView) findViewById(R.id.img_date_picker);
        setGender = (Spinner) findViewById(R.id.spinner_gender);
        btnSubmit = (com.google.android.material.button.MaterialButton)findViewById(R.id.btn_submit);

        ArrayAdapter<String> genderAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, gender);
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        setGender.setAdapter(genderAdapter);
        setGender.setOnItemSelectedListener(this);

        imgGetDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int month = calendar.get(Calendar.MONTH);
                int year = calendar.get(Calendar.YEAR);
                datePicker = new DatePickerDialog(MainActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int myYear, int myMonth, int myDay) {
                        getDOB.setText((myMonth + 1) + "/" + myDay + "/" + myYear);
                        if(myMonth<month) {
                            getAge.setText(String.valueOf(year - myYear));
                        }else{
                            getAge.setText(String.valueOf((year-1) - myYear));
                        }
                    }
                }, year, month, day);
                datePicker.show();
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(getFullname.getText().toString().equals("") || getEmail.getText().toString().equals("") ||
                    getPhone.getText().toString().equals("") || getDOB.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(),"All fields are required.",Toast.LENGTH_SHORT).show();
                }else{
                    if (!ValidateEmail()) {
                        getEmail.setError("Invalid Email Format");
                    }
                    if (!ValidateNumber() || getPhone.length()<11) {
                        getPhone.setError("Invalid Phone Number Format");
                    }
                    if (!ValidateAge()) {
                        Toast.makeText(getApplicationContext(), "Age must be 18 or above.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public boolean ValidateEmail(){
        Pattern pattern;
        Matcher matcher;
        final String EMAIL_PATTERN = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        pattern = Pattern.compile(EMAIL_PATTERN);
        matcher = pattern.matcher(getEmail.getText().toString());
        return matcher.matches();
    }

    public boolean ValidateNumber(){
        String getPrefix = getPhone.getText().toString().substring(0,2);
        return getPrefix.equals("09");
    }

    public boolean ValidateAge(){
        return Integer.parseInt(getAge.getText().toString()) >= 18;
    }

}