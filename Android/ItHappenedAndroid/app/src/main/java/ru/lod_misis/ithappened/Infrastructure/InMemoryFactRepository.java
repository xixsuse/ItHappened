package ru.lod_misis.ithappened.Infrastructure;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import ru.lod_misis.ithappened.Domain.Tracking;
import ru.lod_misis.ithappened.Statistics.Facts.Fact;
import ru.lod_misis.ithappened.Statistics.Facts.FunctionApplicability;

/**
 * Created by Ded on 15.03.2018.
 */

public class InMemoryFactRepository {

    List<Tracking> trackingCollection;

    public InMemoryFactRepository(){
        functionApplicability = new FunctionApplicability();
    }

    public rx.Observable calculateOneTrackingFacts(List<Tracking> trackingCollection)
    {
        oneTrackingFactCollection = new ArrayList<>();
        for (Tracking tracking : trackingCollection) {
            functionApplicabilityCheck(tracking);
        }

        return rx.Observable.from(oneTrackingFactCollection);
    }

    public rx.Observable<Fact> onChangeCalculateOneTrackingFacts(List<Tracking> trackingCollection, UUID trackingId)
    {
        Tracking changedTracking = null;
        List<Fact> factCollectionCheck = oneTrackingFactCollection;

        for (Fact fact: factCollectionCheck) {
            if (fact.getTrackingId() == trackingId)
                oneTrackingFactCollection.remove(fact);
        }

        for (Tracking tracking : trackingCollection) {
            if (tracking.GetTrackingID().equals(trackingId))
                changedTracking = tracking;
        }

        if (changedTracking == null)
            throw new IllegalArgumentException("tracking not exists");

        functionApplicabilityCheck(changedTracking);

        return rx.Observable.from(oneTrackingFactCollection);
    }

    public rx.Observable<Fact> calculateAllTrackingsFacts(List<Tracking> trackingCollection) {
        Fact factToAdd;
        allTrackingsFactCollection = new ArrayList<>();

        factToAdd = FunctionApplicability.allEventsCountFactApplicability(trackingCollection);
        if (factToAdd != null) {
            factToAdd.calculateData();
            allTrackingsFactCollection.add(factToAdd);
        }

        factToAdd = FunctionApplicability.mostFrequentEventApplicability(trackingCollection);
        if (factToAdd != null) {
            factToAdd.calculateData();
            allTrackingsFactCollection.add(factToAdd);
        }

        return rx.Observable.from(allTrackingsFactCollection);
    }

    private void functionApplicabilityCheck(Tracking tracking)
    {
        Fact factToAdd;

        factToAdd = FunctionApplicability.avrgRatingApplicability(tracking);
        if (factToAdd != null) {
            factToAdd.calculateData();
            oneTrackingFactCollection.add(factToAdd);
        }

        factToAdd = FunctionApplicability.avrgScaleApplicability(tracking);
        if (factToAdd != null) {
            factToAdd.calculateData();
            oneTrackingFactCollection.add(factToAdd);
        }

        factToAdd = FunctionApplicability.sumScaleApplicability(tracking);
        if (factToAdd != null) {
            factToAdd.calculateData();
            oneTrackingFactCollection.add(factToAdd);
        }

        factToAdd = FunctionApplicability.trackingEventsCountApplicability(tracking);
        if (factToAdd != null) {
            factToAdd.calculateData();
            oneTrackingFactCollection.add(factToAdd);
        }

        factToAdd = FunctionApplicability.worstEventApplicability(tracking);
        if (factToAdd != null) {
            factToAdd.calculateData();
            oneTrackingFactCollection.add(factToAdd);
        }

        factToAdd = FunctionApplicability.bestEventApplicability(tracking);
        if (factToAdd != null) {
            factToAdd.calculateData();
            oneTrackingFactCollection.add(factToAdd);
        }

        factToAdd = FunctionApplicability.certainWeekDaysApplicability(tracking);
        if (factToAdd != null){
            oneTrackingFactCollection.add(factToAdd);
        }
    }


    List<Fact> oneTrackingFactCollection = new ArrayList<>();
    List<Fact> allTrackingsFactCollection = new ArrayList<>();
    FunctionApplicability functionApplicability;

}
