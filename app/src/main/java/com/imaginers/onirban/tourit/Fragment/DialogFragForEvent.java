package com.imaginers.onirban.tourit.Fragment;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import com.imaginers.onirban.tourit.Class.EventDatabase;
import com.imaginers.onirban.tourit.Class.EventDatabaseOpenHelper;
import com.imaginers.onirban.tourit.R;


public class DialogFragForEvent extends android.support.v4.app.DialogFragment implements View.OnClickListener{

    private Button addEvent;
    private EditText title,destination,date,timeHour,timeMin,description,budget;
    private Spinner timeFormat;
    private String eventName,eventDesti,eventDate,eventTime,eventDes,eventBudget,delId,keyToEdit;
    private boolean check=false;

    public DialogFragForEvent() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        //sets the interface

        View rootView=inflater.inflate(R.layout.dialog_frag_for_event,container,false);


        //initializing the views

        addEvent= rootView.findViewById(R.id.addEvent);
        title= rootView.findViewById(R.id.setTitle);
        destination= rootView.findViewById(R.id.setDestination);
        date= rootView.findViewById(R.id.setDate);
        timeHour= rootView.findViewById(R.id.setTimeHour);
        timeMin= rootView.findViewById(R.id.setTimeMin);
        timeFormat= rootView.findViewById(R.id.setTimeFormat);
        description= rootView.findViewById(R.id.shortDescription);
        budget= rootView.findViewById(R.id.setBudget);


        //check if user clicked edit or not

        SharedPreferences sharedPreferences=getActivity().getSharedPreferences("EDIT_EVENT", Context.MODE_PRIVATE);
        check=sharedPreferences.getBoolean("update",true);
        if(check) {

            addEvent.setText("Update");

            //set the title

            getDialog().setTitle("Update Event");

            keyToEdit=sharedPreferences.getString("edit_id","");
            if(keyToEdit!=null) {
                populateDialogFields(keyToEdit);
            }
            SharedPreferences.Editor editor=sharedPreferences.edit();
            editor.putBoolean("update",false);
            editor.commit();
        }else {
            //set the title
            getDialog().setTitle("Add Event");
        }

        //on click listener

        addEvent.setOnClickListener(this);
        return rootView;
    }


    @Override
    public void onClick(View v) {

        //getting values from the ui

        eventName = title.getText().toString();
        eventDesti = destination.getText().toString();
        eventDate = date.getText().toString();
        eventTime = timeHour.getText().toString() + ":" + timeMin.getText().toString() + " " + timeFormat.getSelectedItem().toString();
        eventDes = description.getText().toString();
        eventBudget = budget.getText().toString();
        if(eventName.length()>0&&eventDesti.length()>0&&eventDate.length()>0&&eventTime.length()>0&&eventDes.length()>0&&eventBudget.length()>0) {

            if (addEvent.getText().toString().toLowerCase().equals("add event")) {

                delId = String.valueOf(System.currentTimeMillis());
                //saving those data into the database

                saveIntoDatabase(eventName, eventDesti, eventDate, eventTime, eventDes, eventBudget, delId);
            } else {

                updateEvent(eventName, eventDesti, eventDate, eventTime, eventDes, eventBudget, keyToEdit);
            }

            //reloading the Event Fragment, because new data has been added
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.motherView, new EventFragment(), "event_frag").commit();

            //Dismissing the Dialog Fragment
            getDialog().dismiss();
        }
        else{
            Toast.makeText(getContext(), "Please Fill all the required data!", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveIntoDatabase(String eName, String eDesti, String eDate, String eTime, String eDes, String eBudget, String delId) {

        EventDatabaseOpenHelper mDbhelper=new EventDatabaseOpenHelper(getContext());
        //opening the database in writable format
        SQLiteDatabase db=mDbhelper.getWritableDatabase();

        //query to inset data
        ContentValues values=new ContentValues();
        values.put(EventDatabase.DefineTable.COLUMN_NAME_ENAME,eName);
        values.put(EventDatabase.DefineTable.COLUMN_NAME_DESTI,eDesti);
        values.put(EventDatabase.DefineTable.COLUMN_NAME_DATE,eDate);
        values.put(EventDatabase.DefineTable.COLUMN_NAME_TIME,eTime);
        values.put(EventDatabase.DefineTable.COLUMN_NAME_DES,eDes);
        values.put(EventDatabase.DefineTable.COLUMN_NAME_BUDGET,eBudget);
        values.put(EventDatabase.DefineTable.COLUMN_NAME_DEL_ID,delId);
        db.insert(EventDatabase.DefineTable.TABLE_NAME,null,values);

        Toast.makeText(getContext(), "Inserted", Toast.LENGTH_SHORT).show();

    }

    //initialize the list
    private void populateDialogFields(String editId) {
        //getting data from the database and populating the dialog
        EventDatabaseOpenHelper mDbhelper=new EventDatabaseOpenHelper(getContext());
        SQLiteDatabase db=mDbhelper.getReadableDatabase();

        String[] projection = {
                EventDatabase.DefineTable.COLUMN_NAME_ENAME,
                EventDatabase.DefineTable.COLUMN_NAME_DESTI,
                EventDatabase.DefineTable.COLUMN_NAME_DATE,
                EventDatabase.DefineTable.COLUMN_NAME_TIME,
                EventDatabase.DefineTable.COLUMN_NAME_DES,
                EventDatabase.DefineTable.COLUMN_NAME_BUDGET,
                EventDatabase.DefineTable.COLUMN_NAME_DEL_ID
        };

        String selection = EventDatabase.DefineTable.COLUMN_NAME_DEL_ID + " = ?";
        String[] selectionArgs = { editId };

        Cursor res=db.query(EventDatabase.DefineTable.TABLE_NAME,projection,selection,selectionArgs,null,null,null);

        while (res.moveToNext()) {
            eventName=res.getString(0);
            eventDesti=res.getString(1);
            eventDate=res.getString(2);
            eventTime=res.getString(3);
            eventDes=res.getString(4);
            eventBudget=res.getString(5);
            delId=res.getString(6);
        }

        //splitting time string

        String timeHour,restOfTheString,timeMin,timeFormat;

        String fullTime[]=eventTime.split(":");
        timeHour=fullTime[0];
        restOfTheString=fullTime[1];
        String timeMinAndFormat[]=restOfTheString.split(" ");
        timeMin=timeMinAndFormat[0];
        timeFormat=timeMinAndFormat[1];


        title.setText(eventName);
        destination.setText(eventDesti);
        date.setText(eventDate);
        this.timeHour.setText(timeHour);
        this.timeMin.setText(timeMin);

        if(timeFormat.equals("Am")) {
            this.timeFormat.setSelection(0);
        }
        else {
            this.timeFormat.setSelection(1);
        }

        description.setText(eventDes);
        budget.setText(eventBudget);
    }

    private void updateEvent(String eName, String eDesti, String eDate, String eTime, String eDes, String eBudget, String editKey) {

        EventDatabaseOpenHelper mDbhelper=new EventDatabaseOpenHelper(getContext());
        SQLiteDatabase db=mDbhelper.getReadableDatabase();

        //query to inset data
        ContentValues values=new ContentValues();
        values.put(EventDatabase.DefineTable.COLUMN_NAME_ENAME,eName);
        values.put(EventDatabase.DefineTable.COLUMN_NAME_DESTI,eDesti);
        values.put(EventDatabase.DefineTable.COLUMN_NAME_DATE,eDate);
        values.put(EventDatabase.DefineTable.COLUMN_NAME_TIME,eTime);
        values.put(EventDatabase.DefineTable.COLUMN_NAME_DES,eDes);
        values.put(EventDatabase.DefineTable.COLUMN_NAME_BUDGET,eBudget);

        String selection = EventDatabase.DefineTable.COLUMN_NAME_DEL_ID + " LIKE ?";
        String[] selectionArgs = { editKey };

        db.update(EventDatabase.DefineTable.TABLE_NAME,values,selection,selectionArgs);
        Toast.makeText(getContext(), "updated", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onDestroy() {
        EventDatabaseOpenHelper mDbhelper=new EventDatabaseOpenHelper(getContext());
        mDbhelper.close();
        super.onDestroy();

    }
}

