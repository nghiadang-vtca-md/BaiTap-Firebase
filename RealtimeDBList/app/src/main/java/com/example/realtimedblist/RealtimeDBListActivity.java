package com.example.realtimedblist;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;

public class RealtimeDBListActivity extends AppCompatActivity {

    private ListView dataListView;
    private EditText itemText;
    private Button findButton;
    private Button addButton;
    private Button deleteButton;

    private Boolean searchMode = false;
    private Boolean itemSelected = false;
    private int selectedPosition = 0;

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference dbRef = database.getReference("todo");

    ArrayList<String> listItems = new ArrayList<String>();
    ArrayList<String> listKeys = new ArrayList<String>();
    ArrayAdapter<String> adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_realtime_dblist);



        dataListView = findViewById(R.id.dataListView);
        itemText = findViewById(R.id.itemText);
        findButton = findViewById(R.id.findButton);
        deleteButton = findViewById(R.id.deleteButton);
        addButton = findViewById(R.id.addButton);

        deleteButton.setEnabled(false);

        adapter = new ArrayAdapter<String>(RealtimeDBListActivity.this
                                            ,android.R.layout.simple_list_item_single_choice
                                            ,listItems);
        dataListView.setAdapter(adapter);
        dataListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        dataListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedPosition = position;
                itemSelected = true;
                deleteButton.setEnabled(true);
            }
        });

        addChildEventListener();
    }



    private void addChildEventListener() {
        ChildEventListener childListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                adapter.add((String)dataSnapshot.child("description").getValue());

                listKeys.add(dataSnapshot.getKey());
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                String key = dataSnapshot.getKey();
                int index = listKeys.indexOf(key);

                if(index != -1) {
                    listItems.remove(index);
                    listKeys.remove(index);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        dbRef.addChildEventListener(childListener);
    }

    public void addItem(View view){
        String item = itemText.getText().toString();
        String key = dbRef.push().getKey();

        itemText.setText("");
        dbRef.child(key).child("description").setValue(item);

        adapter.notifyDataSetChanged();
    }

    public void deleteItem(View view){
        dataListView.setItemChecked(selectedPosition, false);
        dbRef.child(listKeys.get(selectedPosition)).removeValue();
    }

    public void findItems(View view){
        Query query;
        String textSearch = itemText.getText().toString();

        if(!searchMode){
            findButton.setText("Clear");
            //query = dbRef.orderByChild("description").equalTo(itemText.getText().toString());
            query = dbRef.orderByChild("description").startAt(textSearch).endAt(textSearch + "\uF8FF");
            // https://stackoverflow.com/questions/38618953/how-to-do-a-simple-search-in-string-in-firebase-database?noredirect=1&lq=1
            searchMode = true;
        }
        else {
            searchMode = false;
            findButton.setText("Find");
            itemText.setText("");
            query = dbRef.orderByKey();
        }

        if(itemSelected){
            dataListView.setItemChecked(selectedPosition,false);
            itemSelected = false;
            deleteButton.setEnabled(false);
        }


        query.addListenerForSingleValueEvent(queryValueListener);
    }

    ValueEventListener queryValueListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            Iterable<DataSnapshot> snapshotIterator = dataSnapshot.getChildren();
            Iterator<DataSnapshot> iterator = snapshotIterator.iterator();

            adapter.clear();
            listKeys.clear();

            while (iterator.hasNext()){
                DataSnapshot next = (DataSnapshot)iterator.next();

                String match = (String)next.child("description").getValue();
                String key = next.getKey();

                listKeys.add(key);
                adapter.add(match);
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };
}
