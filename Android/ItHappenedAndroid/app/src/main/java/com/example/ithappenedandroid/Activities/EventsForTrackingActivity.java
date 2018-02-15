package com.example.ithappenedandroid.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.ithappenedandroid.Application.TrackingService;
import com.example.ithappenedandroid.Domain.Event;
import com.example.ithappenedandroid.Domain.Tracking;
import com.example.ithappenedandroid.Infrastructure.ITrackingRepository;
import com.example.ithappenedandroid.R;
import com.example.ithappenedandroid.Recyclers.EventsAdapter;
import com.example.ithappenedandroid.StaticInMemoryRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class EventsForTrackingActivity extends AppCompatActivity {

    RecyclerView eventsRecycler;
    EventsAdapter eventsAdpt;

    TextView hintForEvents;

    FloatingActionButton addNewEvent;
    Tracking thisTracking;

    List<Event> events;
    UUID trackingId;

    ITrackingRepository trackingsCollection;
    TrackingService trackingService;
    int trackingPosition;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events_history_for_tracking);
    }

    @Override
    public void onResume() {
        super.onResume();

        trackingsCollection = new StaticInMemoryRepository(getApplicationContext()).getInstance();

        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);


        Intent intent = getIntent();

        hintForEvents = (TextView) findViewById(R.id.hintForEventsFragment);

            trackingId = UUID.fromString(intent.getStringExtra("id"));


        trackingService = new TrackingService("testUser", trackingsCollection);

        thisTracking = trackingsCollection.GetTracking(trackingId);
        actionBar.setTitle(thisTracking.GetTrackingName());

        events = thisTracking.GetEventCollection();

        for(int i = 0;i<events.size();i++){
            if(events.get(i).GetStatus()){
                events.remove(i);
            }
        }

        List<Event> visibleEvents = new ArrayList<>();

        for(int i=0;i<events.size();i++){
            if(!events.get(i).GetStatus()){
                visibleEvents.add(events.get(i));
            }
        }

        if(visibleEvents.size()!=0){
            hintForEvents.setVisibility(View.INVISIBLE);
        }

        setTitle(thisTracking.GetTrackingName());

        eventsRecycler = (RecyclerView) findViewById(R.id.eventsForTrackingRV);
        eventsRecycler.setLayoutManager(new LinearLayoutManager(this));
        eventsAdpt = new EventsAdapter(visibleEvents , this, 0);
        eventsRecycler.setAdapter(eventsAdpt);


        addNewEvent = (FloatingActionButton) findViewById(R.id.addNewEvent);

        addNewEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getApplicationContext(), AddNewEventActivity.class);
                intent.putExtra("trackingId", thisTracking.GetTrackingID().toString());

                startActivity(intent);

            }
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        recreate();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
