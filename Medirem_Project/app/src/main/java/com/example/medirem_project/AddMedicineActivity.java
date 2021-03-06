package com.example.medirem_project;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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


/**
 * Add medicine activity adds custom medicine to the list which the user has to type in.
 * @author Eric Keränen & Salla Mikkonen & Joonatan Pakkanen
 * @version 1.8 2/2020
 */
public class AddMedicineActivity extends AppCompatActivity
        implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    private boolean repeat;
    private int hour, minute, month, day, year;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_medicine);

        /**
         * If no time is set, 12:00 will be the default time for notifications
         */
        this.hour = 12;
        this.minute = 0;
        this.repeat = false;

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
     * Radio buttons are used for selecting the repeat type, which are "Do not repeat" or "Repeat
     * for a week", depending on which is selected the variable repeat (boolean) is changed
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
     * This method saves the information that the user selects in the opened calendar.
     * After picking a date it will be displayed in a text view and saved to some variables.
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

        this.month = month;
        this.day = dayOfMonth;
        this.year = year;
    }

    /**
     * This method saves the information that the user selects in the opened clock.
     * After picking the time it will be displayed in a text view. The displaying has
     * a small logic pool to add zeroes in front of numbers smaller than 10 (e.g. 8:1 -> 08:01).
     * Chosen time is transferred to startAlarm method as c parameter.
     * Hour and minute will be saved to a few variables.
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
        this.hour = hourOfDay;
        this.minute = minute;
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
             * Dialog alerts you that the medicine name is empty.
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
             * Dialog alerts you that the medicine date is empty.
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
            /**
             * If repeat is off, save medicine and notification once.
             * If repeat is on, save medicine and notification seven times.
             */
            if(!repeat){
                SavedMedicine.getInstance().saveMedicine(medName, medDesc, medDate, medTime);
                Calendar c = Calendar.getInstance();

                /**
                 * Saved variables from onDateSet and onTimeSet will be set to an alarm here.
                 * This alarm will fire off only once.
                 */
                c.set(Calendar.YEAR, year);
                c.set(Calendar.MONTH, month);
                c.set(Calendar.DAY_OF_MONTH, day);
                c.set(Calendar.HOUR_OF_DAY, hour);
                c.set(Calendar.MINUTE, minute);
                c.set(Calendar.SECOND, 0);

                startAlarm(c);

                setResult(1);
                finish();
            }else if(repeat){
                Calendar c = Calendar.getInstance();

                for(int i = 0; i <= 6; i++){
                    // ======== INCREMENT DAY BY ONE EVERY LOOP ======== //
                    String oldMedDate = tvDate.getText().toString();
                    SimpleDateFormat oldToNewDate = new SimpleDateFormat("d.M.yyyy");
                    try{
                        c.setTime(oldToNewDate.parse(oldMedDate));
                    }catch(ParseException e){
                        e.printStackTrace();
                    }
                    c.set(Calendar.DAY_OF_MONTH, day);
                    c.add(Calendar.DAY_OF_MONTH, i);
                    String newMedDate = oldToNewDate.format(c.getTime());
                    Log.d("LOG", "newMedDate is " + newMedDate);
                    // ================================================= //

                    SavedMedicine.getInstance().saveMedicine(medName, medDesc, newMedDate, medTime);

                    /**
                     * Saved variables from onDateSet and onTimeSet will be set to an alarm here.
                     * This alarm will be first set to the day that is selected and in the for loop
                     * the alarm will be set to the next six days in addition to the first.
                     */
                    c.set(Calendar.YEAR, year);
                    c.set(Calendar.MONTH, month);
                    c.set(Calendar.HOUR_OF_DAY, hour);
                    c.set(Calendar.MINUTE, minute);
                    c.set(Calendar.SECOND, 0);

                    startAlarmRepeat(c);
                }

                setResult(2);
                finish();
            }
        }
    }

    /**
     * This method will call for the alarm manager and create a pending intent, that will be fire off
     * when the time is right. The time is exactly the times the user has set for the medicine.
     * Used for setting one alarm.
     * @param c takes in a date and a time when the alarm should fire (Calendar)
     */
    private void startAlarm(Calendar c){
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlertReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, (int)System.currentTimeMillis(), intent,0);

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);
    }

    /**
     * This method will call for the alarm manager and create a pending intent, that will be fire off
     * when the time is right. The time is exactly the times the user has set for the medicine.
     * Used for setting multiple alarms.
     * @param c takes in a date and a time when the alarm should fire (Calendar)
     */
    private void startAlarmRepeat(Calendar c){
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlertReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, (int)System.currentTimeMillis(), intent,0);

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);
    }

    /**
     * This method cancels the pending intent and therefore cancels the alarm.
     */
    private void cancelAlarm(){
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlertReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, (int)System.currentTimeMillis(), intent,0);

        alarmManager.cancel(pendingIntent);
    }

    /**
     * When the user presses the back button, this method will call the onBackPressed()
     * method from the superclass that returns the user back to the previous view.
     * @param v used for finding something in the screen view (View)
     */
    public  void  onBackPressed(View v){
        super.onBackPressed();
        cancelAlarm();
        setResult(0);
        finish();
    }
}
