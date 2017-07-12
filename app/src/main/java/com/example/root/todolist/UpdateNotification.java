package com.example.root.todolist;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class UpdateNotification extends AppCompatActivity {

    private int i;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_notification);

        Bundle intentdata = getIntent().getExtras();
        final int my=intentdata.getInt("val");

        final MyDBHandler dbHandler = new MyDBHandler(this);
        ArrayList<Tasks> list = dbHandler.databaseToString();
        String [] names = new String[list.size()];

        for(Tasks curr: list) {
            names[i] = curr.get_taskname();
            ++i;
        }

        ListAdapter buckysAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_checked, names);
        final ListView taskview = (ListView) findViewById(R.id.NotofyListView);
        taskview.setAdapter(buckysAdapter);

        Button save_notify = (Button) findViewById(R.id.save_notify);
        save_notify.setOnClickListener(new Button.OnClickListener(){

            @Override
            public void onClick(View v) {

                SparseBooleanArray sparseBooleanArray = taskview.getCheckedItemPositions();
                --i;
                while (i>=0){
                    if(sparseBooleanArray.get(i) ) {
                        dbHandler.updateNotify(my, String.valueOf(taskview.getItemAtPosition(i)));
                    }
                    --i;
                }

                Intent x= new Intent(getApplicationContext(), MainActivity.class);
                startActivity(x);
            }
        });
    }
}
