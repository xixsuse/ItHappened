package com.example.ithappenedandroid.Infrastructure;

import com.example.ithappenedandroid.Domain.Comparison;
import com.example.ithappenedandroid.Domain.Event;
import com.example.ithappenedandroid.Domain.Rating;
import com.example.ithappenedandroid.Domain.Tracking;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;


public class InMemoryTrackingRepository implements ITrackingRepository
{
    public InMemoryTrackingRepository()
    {
        trackingCollection = new ArrayList<Tracking>();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public Tracking GetTracking(UUID trackingId)
    {
<<<<<<< HEAD
        for (Tracking item: trackingCollection)
        {
            if (item.GetTrackingID().equals(trackingId))
            {
                return item;
            }
        }
        throw new IllegalArgumentException("Tracking with such ID doesn't exists");
=======
        Optional<Tracking> tracking;
        tracking = trackingCollection.stream()
                .filter((item) -> item.GetTrackingID().equals(trackingId))
                .findFirst();
        if (tracking.isPresent())
            return tracking.get();
        else throw new IllegalArgumentException("Tracking with such ID does not exist");
>>>>>>> parent of 525bbbf... removed stream api and optional
    }

    public List<Tracking> GetTrackingCollection()
    {
        return trackingCollection;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void ChangeTracking(final Tracking tracking)
    {

        int index = 0;
        boolean contains = false;
        for (Tracking item: trackingCollection)
        {
            if (item.GetTrackingID().equals(tracking.GetTrackingID()))
            {
                contains = true;
                break;
            }
            index++;
        }
        if (contains)
            trackingCollection.set(index, tracking);
        else throw new IllegalArgumentException("Tracking with such ID doesn't exists");
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void AddNewTracking(Tracking tracking)
    {
        if (!trackingCollection.stream()
                .anyMatch((item) -> item.GetTrackingID().equals(tracking.GetTrackingID())))
            trackingCollection.add(tracking);
        else throw new IllegalArgumentException("Tracking with such ID already exists");
    }


    public List<Event> FilterEvents(UUID trackingId, Date dateFrom, Date dateTo,
                                    Comparison scaleComparison, Double scale,
                                    Comparison ratingComparison, Rating rating)
    {
        List<Event> notFilteredEvents = new ArrayList<Event>();
        List<Event> filteredEvents = new ArrayList<Event>();
        for (Tracking trackig : trackingCollection) {
            notFilteredEvents.addAll(trackig.GetEventCollection());
        }

        filteredEvents = notFilteredEvents;

        if (trackingId != null) {
            for (Event event : notFilteredEvents) {
                if (event.GetTrackingId().equals(trackingId))
                    filteredEvents.add(event);
            }
        }

        if (dateFrom != null && dateTo != null){
            notFilteredEvents = filteredEvents;
            filteredEvents.clear();
            for (Event event : notFilteredEvents) {
                if (event.GetEventDate().compareTo(dateFrom) >= 0 && event.GetEventDate().compareTo(dateTo) <=0)
                    filteredEvents.add(event);
            }
        }

        if (scaleComparison != null && scale != null) {
            notFilteredEvents = filteredEvents;
            filteredEvents.clear();
            for (Event event : notFilteredEvents) {
                if (CompareValues(scaleComparison, event.GetScale(), scale))
                    filteredEvents.add(event);
            }
        }

        if (ratingComparison != null && rating != null) {
            notFilteredEvents = filteredEvents;
            filteredEvents.clear();
            for (Event event : notFilteredEvents) {
                if (CompareValues(ratingComparison, event.GetRating().GetRatingValue().doubleValue(), scale))
                    filteredEvents.add(event);
            }
        }

        return  filteredEvents;
    }

    private boolean CompareValues(Comparison comparison, Double firstValue, Double secondValue)
    {
        if (comparison == Comparison.Less)
        {
            if (firstValue <= secondValue)
                return true;
            return false;
        }
        if (comparison == Comparison.More)
        {
            if (firstValue >= secondValue)
                return true;
        }
        return false;
    }


    private List<Tracking> trackingCollection;
}
