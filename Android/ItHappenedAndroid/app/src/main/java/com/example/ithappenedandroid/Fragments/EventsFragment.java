package com.example.ithappenedandroid.Fragments;

import android.app.Fragment;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.example.ithappenedandroid.Application.TrackingService;
import com.example.ithappenedandroid.Domain.Tracking;
import com.example.ithappenedandroid.Infrastructure.ITrackingRepository;
import com.example.ithappenedandroid.R;
import com.example.ithappenedandroid.Recyclers.EventsAdapter;
import com.example.ithappenedandroid.StaticInMemoryRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class EventsFragment extends Fragment{

    RecyclerView eventsRecycler;
    EventsAdapter eventsAdpt;

    Button dateFrom;
    Button dateTo;

    Spinner trackingsSpinner;
    TrackingService trackingService;

    ITrackingRepository collection = StaticInMemoryRepository.getInstance();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_events_history, null);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        eventsRecycler = (RecyclerView) view.findViewById(R.id.evetsRec);
        eventsRecycler.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
        eventsAdpt = new EventsAdapter(collection.FilterEvents(null,null,null,null,null,null,null), getActivity(), 0);
        eventsRecycler.setAdapter(eventsAdpt);

        trackingService = new TrackingService("testUser", collection);

        ArrayList<UUID> idCollection = new ArrayList<UUID>();
        ArrayList<String> strings = new ArrayList<String>();

        List<Tracking> trackings = new ArrayList<>();
        trackings = trackingService.GetTrackingCollection();

        for(int i=0;i<trackings.size();i++){
            strings.add(trackings.get(i).GetTrackingName());
            idCollection.add(trackings.get(i).GetTrackingID());
        }

        trackingsSpinner = (Spinner) view.findViewById(R.id.spinnerForTrackings);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, strings);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        trackingsSpinner.setAdapter(adapter);

        dateFrom = (Button) view.findViewById(R.id.dateFromButton);
        dateTo = (Button) view.findViewById(R.id.dateToButton);

       /* dateFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = getFragmentManager();
                DialogFragment picker = new DatePickerFragment();
                picker.show(fragmentManager, "tag");
            }
        });*/
    }

}