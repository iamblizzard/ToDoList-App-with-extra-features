package com.example.root.todolist;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DownloadManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.ContextThemeWrapper;
import android.view.ActionMode;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private List<Tasks> taskList = new ArrayList<>();
    private RecyclerView recyclerView;
    private TaskAdapter mAdapter;
    private GoogleApiClient mGoogleApiClient;

    private static MyDBHandler dbHandler;
    private PopupWindow pw;
    public static EditText userinput;
    public View popup_layout;
    public View view;
    public static DateClass date = new DateClass();

    AlarmManager am;
    PendingIntent sender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*String ret = "";

        try {
            InputStream inputStream = this.openFileInput("backup.txt");

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    ret+=receiveString+"\n";
                }

                inputStream.close();
                Toast.makeText(getApplicationContext(), ret, Toast.LENGTH_LONG).show();
            }
        }
        catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        } */

        am = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent intent = new Intent(this, NotificationAlarm.class);
        sender = PendingIntent.getBroadcast(this, 0, intent, 0);
        boolean alarmUp = (PendingIntent.getBroadcast(MainActivity.this,
                0, new Intent(MainActivity.this, NotificationAlarm.class),
                PendingIntent.FLAG_NO_CREATE) != null);

        if (alarmUp)
            am.cancel(sender);

        dbHandler = new MyDBHandler(this);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        mAdapter = new TaskAdapter(taskList, this, this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        showDatabase();

    }

    protected void onResume() {
        super.onResume();
        am.setRepeating(AlarmManager.RTC_WAKEUP,
                System.currentTimeMillis(), 3600000*4,
                sender);
    }

    protected void onStop() {
        super.onStop();
        am.setRepeating(AlarmManager.RTC_WAKEUP,
                System.currentTimeMillis(), 3600000*4,
                sender);
    }

    public void addButtonClicked(View view) {
        initiatePopupWindow(view);
    }

    private void initiatePopupWindow(View v) {
        try {
            final LayoutInflater inflater = (LayoutInflater) MainActivity.this
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            popup_layout = inflater.inflate(R.layout.popup,
                    (ViewGroup) findViewById(R.id.popup_element));

            final AlertDialog alertDialog = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.AlertDialogCustom))
            .setView(popup_layout)
            .setTitle("Enter Task")
            .setPositiveButton("Save", null)
            .setNeutralButton("Set Date", null)
            .create();

            alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {

                @Override
                public void onShow(DialogInterface dialog) {

                    date.s = new SimpleDateFormat("dd/MM/yyyy").format(Calendar.getInstance().getTime());

                    Button save = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                    save.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {

                            userinput = (EditText) popup_layout.findViewById(R.id.userinput);
                            String input = userinput.getText().toString().trim();

                            if (input != null && !input.isEmpty()) {

                                Tasks task = new Tasks(input, date.s, "low_priority", 1);
                                dbHandler.addTask(task);
                                taskList.add(task);
                                mAdapter.notifyDataSetChanged();
                            }

                            alertDialog.dismiss();
                        }
                    });

                    Button date = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_NEUTRAL);
                    date.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {

                            DialogFragment newFragment = new DatePickerFragment();
                            newFragment.show(getSupportFragmentManager(), "datePicker");
                        }
                    });
                }
            });

            alertDialog.show();

            /*pw = new PopupWindow(layout, RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT, true);
            pw.showAtLocation(v, Gravity.CENTER, 0, 0);

            Button date_btn = (Button) layout.findViewById(R.id.date_btn);
            date_btn.setOnClickListener(new Button.OnClickListener() {

                @Override
                public void onClick(View v) {
                    DialogFragment newFragment = new DatePickerFragment();
                    newFragment.show(getSupportFragmentManager(), "datePicker");
                }
            });

            Button save_btn = (Button) layout.findViewById(R.id.save_btn);
            save_btn.setOnClickListener(new Button.OnClickListener() {

                @Override
                public void onClick(View v) {

                    ...
                }
            });

            Button cancel_btn = (Button) layout.findViewById(R.id.cancel_btn);
            cancel_btn.setOnClickListener(new Button.OnClickListener() {

                @Override
                public void onClick(View v) {
                    pw.dismiss();
                }
            });*/


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            // Do something with the date chosen by the user
            ++month;
            String mon = ((Integer) month).toString();
            String d = ((Integer) day).toString();
            if (month < 10)
                mon = "0" + mon;
            if (day < 10)
                d = "0" + d;
            date.s = d + "/" + mon + "/" + ((Integer) year).toString();
        }
    }

    public void showDatabase() {

        ArrayList<Tasks> dbString = dbHandler.databaseToString();
        for (Tasks curr : dbString) {
            taskList.add(curr);
        }

        mAdapter.notifyDataSetChanged();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    private String m_chosenDir = "";
    private boolean m_newFolderEnabled = true;
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.clear_data:

                AlertDialog dialog = Confirm();
                dialog.show();
                return true;

            case R.id.create_backup:

                dialog = Choose();
                dialog.show();

                return true;

            case R.id.enable_notify:

                Intent i = new Intent(this, UpdateNotification.class);
                i.putExtra("val", 1);
                startActivity(i);
                return true;

            case R.id.stop_notify:

                i = new Intent(this, UpdateNotification.class);
                i.putExtra("val", 0);
                startActivity(i);
                return true;

            case R.id.share:

                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                ArrayList<Tasks> dbString = dbHandler.databaseToString();
                String s="Tasks\t\t\t\t\t\tDeadline\n";
                for (Tasks curr : dbString) {
                    s+=curr.get_taskname()+"  "+curr.get_date()+"\n";
                }
                sendIntent.putExtra(Intent.EXTRA_TEXT, s);
                sendIntent.setType("text/plain");
                startActivity(Intent.createChooser(sendIntent, "Share TaskList via.."));
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public AlertDialog Confirm()
    {
        AlertDialog myConfirmDialogBox =new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.AlertDialogCustom))

                .setTitle("Delete All")
                .setMessage("Do you want to Delete All Tasks")
                .setIcon(R.drawable.delete)

                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {

                        dbHandler.clearTask();
                        while (taskList.size() > 0) {
                            taskList.remove(0);
                        }
                        showDatabase();

                        dialog.dismiss();
                    }

                })

                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();

                    }
                })
                .create();
        return myConfirmDialogBox;
    }

    public AlertDialog Choose()
    {
        AlertDialog myConfirmDialogBox =new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.AlertDialogCustom))

                .setTitle("Location")
                .setMessage("Select your Preference")

                .setPositiveButton("Change", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {

                        dialog.dismiss();
                        // Create DirectoryChooserDialog and register a callback
                        DirectoryChooserDialog directoryChooserDialog =
                                new DirectoryChooserDialog(MainActivity.this,
                                        new DirectoryChooserDialog.ChosenDirectoryListener()
                                        {
                                            @Override
                                            public void onChosenDir(String chosenDir)
                                            {
                                                m_chosenDir = chosenDir;
                                                Toast.makeText( MainActivity.this, "Chosen directory: "+chosenDir, Toast.LENGTH_SHORT).show();
                                                SharedPreferences backupLocation = getSharedPreferences("backupLocation", Context.MODE_PRIVATE);
                                                SharedPreferences.Editor editor = backupLocation.edit();
                                                editor.putString("location", m_chosenDir).apply();
                                                StoreBackup(m_chosenDir);
                                            }
                                        });
                        // Toggle new folder button enabling
                        directoryChooserDialog.setNewFolderEnabled(m_newFolderEnabled);
                        // Load directory chooser dialog for initial 'm_chosenDir' directory.
                        // The registered callback will be called upon final directory selection.
                        directoryChooserDialog.chooseDirectory(m_chosenDir);
                        //m_newFolderEnabled = ! m_newFolderEnabled;

                    }

                })

                .setNegativeButton("Use Default", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        SharedPreferences backupLocation = getSharedPreferences("backupLocation", Context.MODE_PRIVATE);
                        String name = backupLocation.getString("location", "");
                        if(name == "") {

                            Toast.makeText(getApplicationContext(), "There is no default storage", Toast.LENGTH_LONG).show();
                        }
                        else {

                            StoreBackup(name);
                            dialog.dismiss();
                        }

                    }
                })
                .create();
        return myConfirmDialogBox;
    }

    public void StoreBackup(String path) {

        try {

            //File sdCard = Environment.getExternalStorageDirectory();
            File dir = new File (path);
            dir.mkdirs();
            File file = new File(dir, "backup.txt");

            FileWriter f = new FileWriter(file);

            ArrayList<Tasks> list;
            list = dbHandler.databaseToString();

            String s = "";
            for(Tasks curr: list) {

                s+="Task: "+curr.get_taskname()+" Deadline: "+curr.get_date()+" "+curr.get_color()+"\n";
            }
            f.write(s);
            f.close();
                Toast.makeText(getApplicationContext(), "Backup Created", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
        }
    }

}


