package com.example.remoteexam_propelrr;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.Year;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private static final String URL_FOR_REGISTRATION = "https://run.mocky.io/v3/a6b6c99a-c4d6-4514-ba9c-90e4957adc76";
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_FNAME = "fullname";

    com.google.android.material.textfield.TextInputEditText getFullname, getEmail, getPhone;
    com.google.android.material.button.MaterialButton btnSubmit;
    TextView getAge, getDOB;
    ImageView imgGetDate;
    Spinner setGender;
    DatePickerDialog datePicker;

    String[] gender = {"Male", "Female"};

    JSONParser jsonParser = new JSONParser();

    StringBuffer buffer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 8) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);

            getFullname = (com.google.android.material.textfield.TextInputEditText) findViewById(R.id.et_fullname);
            getEmail = (com.google.android.material.textfield.TextInputEditText) findViewById(R.id.et_email_address);
            getPhone = (com.google.android.material.textfield.TextInputEditText) findViewById(R.id.et_mobile_no);
            getDOB = (TextView) findViewById(R.id.tv_dob);
            getAge = (TextView) findViewById(R.id.tv_age);
            imgGetDate = (ImageView) findViewById(R.id.img_date_picker);
            setGender = (Spinner) findViewById(R.id.spinner_gender);
            btnSubmit = (com.google.android.material.button.MaterialButton) findViewById(R.id.btn_submit);

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
                            if (myMonth < month) {
                                getAge.setText(String.valueOf(year - myYear));
                            } else {
                                getAge.setText(String.valueOf((year - 1) - myYear));
                            }
                        }
                    }, year, month, day);
                    datePicker.show();
                }
            });

            btnSubmit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getFullname.getText().toString().equals("") || getEmail.getText().toString().equals("") ||
                            getPhone.getText().toString().equals("") || getDOB.getText().toString().equals("")) {
                        Toast.makeText(getApplicationContext(), "All fields are required.", Toast.LENGTH_SHORT).show();
                    } else if(!ValidateEmail() || !ValidateNumber() || getPhone.length() < 11 || !ValidateAge()) {

                        if (!ValidateEmail()) {
                            getEmail.setError("Invalid Email Format");
                        }
                        if (!ValidateNumber() || getPhone.length() < 11) {
                            getPhone.setError("Invalid Phone Number Format");
                        }
                        if (!ValidateAge()) {
                            Toast.makeText(getApplicationContext(), "Age must be 18 or above.", Toast.LENGTH_SHORT).show();
                        }

                    }else{
                        new RegisterUser().execute();
                    }
                }
            });
        }

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

    class RegisterUser extends AsyncTask<String, String, String>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String data) {
            if(data != null){
                try{
                    JSONObject jsonObject = new JSONObject(data);
                    int success = jsonObject.getInt(TAG_SUCCESS);
                    if(success==1){

                        buffer = new StringBuffer();
                        buffer.append("Account Name: " + jsonObject.getString(TAG_FNAME) + "\n");
                        buffer.append("has been successfully registered.");

                        DialogBox();

                    }else{
                        buffer = new StringBuffer();
                        buffer.append("Registration Failed");
                        DialogBox();
                    }
                }catch (JSONException e){
                    e.printStackTrace();
                }
            }else{
                buffer = new StringBuffer();
                buffer.append("Server no response.");
                DialogBox();
            }
        }

        @Override
        protected String doInBackground(String... strings) {

            try{
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                return jsonParser.makeHttpRequest(URL_FOR_REGISTRATION,"POST", params).toString();
            }catch (Exception e){
                e.printStackTrace();
                return null;
            }
        }
    }

    private void DialogBox(){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setCancelable(true);
        builder.setTitle("System Message");
        builder.setMessage(buffer.toString());
        builder.show();
    }

}