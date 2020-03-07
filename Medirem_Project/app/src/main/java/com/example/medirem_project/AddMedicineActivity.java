package com.example.medirem_project;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.DialogFragment;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.TimePickerDialog;

import android.content.DialogInterface;
import android.util.Log;
import android.widget.DatePicker;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import static com.example.medirem_project.App.CHANNEL_1_ID;

/**
 * Add medicine activity adds custom medicine to the list which the user has to type in.
 * @author Eric Keränen & Salla Mikkonen
 * @version 1.5 2/2020
 */
public class AddMedicineActivity extends AppCompatActivity
        implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    private boolean repeat;
    private NotificationManagerCompat notificationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_medicine);

        repeat = false;
        notificationManager = NotificationManagerCompat.from(this);

        /**
         * Date button to open a calendar (DatePicker fragment) when the user presses the select date button.
         * Date picker comes from DatePicker class and calls the calendar.
         */
        Button dateButton = (Button) findViewById(R.id.openDatePicker);
        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment datePicker = new com.example.medirem_project.DatePicker();
                datePicker.show(getSupportFragmentManager(), "date picker");
            }
        });

        /**
         * Time button to open a clock (TimePicker fragment) when the user presses the select time button.
         * Time picker comes from TimePicker class and calls the clock.
         */
        Button timeButton = (Button) findViewById(R.id.openTimePicker);
        timeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment timePicker = new com.example.medirem_project.TimePicker();
                timePicker.show(getSupportFragmentManager(), "time picker");
            }
        });
    }

    /**
     * This method saves the information that the user selects in the opened calendar.
     * After picking a date it will be displayed in a text view.
     *
     * @param view used for finding something in the screen view (View)
     * @param year is found from datePicker activity with Calendar.YEAR (int)
     * @param month is found from datePicker activity with Calendar.MONTH (int)
     * @param dayOfMonth is found from datePicker activity with Calendar.DAY_OF_MONTH (int)
     */
    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        String currentDateString = DateFormat.getDateInstance(DateFormat.MEDIUM).format(c.getTime());

        TextView dateTextView = (TextView) findViewById(R.id.dateView);
        dateTextView.setText(currentDateString);
    }

    /**
     * Radio buttons are used for selecting the repeat type, which are "Do not repeat" or "Repeat
     * for a week" , depending on which is selected the variable repeat (boolean) is changed
     * and when the add button is pressed the medicine will be saved in the way selected
     * @param v used for finding something in the screen view (View)
     */
    public void radioButton(View v) {
        boolean checked = ((RadioButton) v).isChecked();

        switch (v.getId()) {
            case R.id.yesRepeat:
                if (checked) {
                    Log.d("LOG", "Repeat is on");
                    repeat = true;
                }
                break;
            case R.id.noRepeat:
                if (checked) {
                    Log.d("LOG", "Repeat is off");
                    repeat = false;
                }
                break;
        }
    }

    /**
     * This method saves the information that the user selects in the opened clock.
     * After picking the time it will be displayed in a text view. The displaying has
     * a small logic pool to add zeroes in front of numbers smaller than 10 (e.g. 8:1 -> 08:01).
     * @param view used for finding something in the screen view (View)
     * @param hourOfDay is found from timePicker activity with Calendar.HOUR_OF_DAY (int)
     * @param minute is found from timePicker activity with Calendar.Minute (int)
     */
    @Override
    public void onTimeSet(android.widget.TimePicker view, int hourOfDay, int minute) {
        TextView tvTime = (TextView) findViewById(R.id.timeView);
        if(hourOfDay < 10 && minute < 10){
            tvTime.setText("0" + hourOfDay + ":0" + minute);
        }else if(hourOfDay < 10){
            tvTime.setText("0" + hourOfDay + ":" + minute);
        }else if(minute < 10){
            tvTime.setText(hourOfDay + ":0" + minute);
        }else{
            tvTime.setText(hourOfDay + ":" + minute);
        }
    }

    /**
     * When the user presses the add button, the Strings in the editText and textView
     * fields will be saved into the singleton list via saveMedicine method.
     * The medicine will be saved in two ways: repeating (seven times) or no repeating.
     * If the user has not inputted any date or name for the medicine, the medicine will not save
     * and prompt a pop-up message stating that there must be a date and a name for the medicine.
     * After adding the medicine this activity will end.
     * @param v used for finding something in the screen view (View)
     */
    public void addButton(View v){
        EditText etMed = (EditText) findViewById(R.id.nameTheMed);
        String medName = etMed.getText().toString();
        EditText etDesc = (EditText) findViewById(R.id.nameTheDesc);
        String medDesc = etDesc.getText().toString();
        TextView tvDate = (TextView) findViewById(R.id.dateView);
        String medDate = tvDate.getText().toString();
        TextView tvTime = (TextView) findViewById(R.id.timeView);
        String medTime = tvTime.getText().toString();

        if(medName.isEmpty()){
            /**
             * See "removeButton" method in medicine details activity to know how the alert dialog is built.
             */
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Please add a name");
            builder.setMessage("If you want to add a medicine you need to specify a name for it.");
            builder.setIcon(R.drawable.chillpilllogoround);
            builder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
        }else if(medDate.isEmpty()){
            /**
             * See "removeButton" method in medicine details activity to know how the alert dialog is built.
             */
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Please add a date");
            builder.setMessage("If you want to add a medicine you need to specify a date for it.");
            builder.setIcon(R.drawable.chillpilllogoround);
            builder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
        }else{
            if(!repeat){
                SavedMedicine.getInstance().saveMedicine(medName, medDesc, medDate, medTime);

                Notification notification = new NotificationCompat.Builder(this, CHANNEL_1_ID)
                        .setSmallIcon(R.drawable.chillpilllogoround)
                        .setContentTitle("Remember to take your medicine!")
                        .setContentText("You have set " + medName + " to this time.")
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                        .build();
                notificationManager.notify(1, notification);

                setResult(1);
                finish();
            }else if(repeat){
                for(int i = 0; i <= 6; i++){

                    // ======== INCREMENT DAY BY ONE EVERY LOOP ======== //
                    String oldMedDate = tvDate.getText().toString();
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("d.M.yyyy");
                    Calendar c = Calendar.getInstance();
                    try{
                        c.setTime(simpleDateFormat.parse(oldMedDate));
                    }catch(ParseException e){
                        e.printStackTrace();
                    }
                    c.add(Calendar.DAY_OF_MONTH, i);
                    String newMedDate = simpleDateFormat.format(c.getTime());
                    Log.d("LOG", "newMedDate is " + newMedDate);
                    // ================================================= //

                    SavedMedicine.getInstance().saveMedicine(medName, medDesc, newMedDate, medTime);
                }
                setResult(2);
                finish();
            }
        }
    }

    /**
     * When the user presses the back button, this method will call the onBackPressed()
     * method from the superclass that returns the user back to the previous view.
     * @param v used for finding something in the screen view (View)
     */
    public  void  onBackPressed(View v){
        super.onBackPressed();
        setResult(0);
        finish();
    }

}
