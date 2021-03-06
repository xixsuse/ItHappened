package ru.lod_misis.ithappened.Statistics.Facts.OneTrackingStatistcs;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import ru.lod_misis.ithappened.Domain.Event;
import ru.lod_misis.ithappened.Domain.Tracking;
import ru.lod_misis.ithappened.Statistics.Facts.Fact;
import ru.lod_misis.ithappened.Statistics.Facts.Models.IllustartionModel;
import ru.lod_misis.ithappened.Statistics.Facts.Models.IllustrationType;

/**
 * Created by Ded on 23.03.2018.
 */

public class BestEvent extends Fact {

    Tracking tracking;
    List<Event> eventCollection;
    Event bestEvent;

    public BestEvent(Tracking tracking)
    {
        this.tracking = tracking;
        this.trackingId = tracking.GetTrackingID();
        eventCollection = new ArrayList<>();
    }

    @Override
    public void calculateData() {

        for(Event event : tracking.GetEventCollection()){
            if(!event.GetStatus() && event.GetRating()!=null){
                eventCollection.add(event);
            }
        }

        bestEvent = eventCollection.get(0);

        for(Event event : eventCollection)
        {
            if (bestEvent.GetRating().getRating() <= event.GetRating().getRating()
                    && bestEvent.GetEventDate().after(event.GetEventDate()))
                bestEvent = event;
        }

        illustartion = new IllustartionModel(IllustrationType.EVENTREF);
        illustartion.setEventRef(bestEvent);

        calculatePriority();
    }

    @Override
    public void calculatePriority() {
        priority = (double)bestEvent.GetRating().getRating();
    }

    @Override
    public String textDescription() {
        Locale loc = new Locale("ru");
        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm", loc);

        String toReturn = String.format("Лучшее событие <b>%s</b> произошло <b>%s</b>, " +
                        "вы поставили ему <b>%s</b>", tracking.getTrackingName(),
                format.format(bestEvent.GetEventDate()), bestEvent.GetRating().getRating());

        if (bestEvent.GetComment() == null) return toReturn;

        return String.format(toReturn, " с комментарием <b>%s</b>", bestEvent.GetComment());
    }

    public Event getBestEvent() { return bestEvent; }
}
