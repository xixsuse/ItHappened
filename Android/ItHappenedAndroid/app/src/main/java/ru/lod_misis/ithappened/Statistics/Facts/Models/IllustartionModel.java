package ru.lod_misis.ithappened.Statistics.Facts.Models;

import java.util.List;

import ru.lod_misis.ithappened.Domain.Event;

public class IllustartionModel {

    private List<IllustrationType> types;
    private List<Double> pieData;
    private Event eventRef;
    private List<Double> graphData;
    private List<Double> barData;

    public IllustartionModel(List<IllustrationType> types,
                             List<Double> pieData,
                             Event eventRef,
                             List<Double> graphData,
                             List<Double> barData){
        this.types = types;
        this.pieData = pieData;
        this.eventRef = eventRef;
        this.graphData = graphData;
        this.barData = barData;
    }

    public List<IllustrationType> getTypes() {
        return types;
    }

    public void setTypes(List<IllustrationType> types) {
        this.types = types;
    }

    public List<Double> getPieData() {
        return pieData;
    }

    public void setPieData(List<Double> pieData) {
        this.pieData = pieData;
    }

    public Event getEventRef() {
        return eventRef;
    }

    public void setEventRef(Event eventRef) {
        this.eventRef = eventRef;
    }

    public List<Double> getGraphData() {
        return graphData;
    }

    public void setGraphData(List<Double> graphData) {
        this.graphData = graphData;
    }

    public List<Double> getBarData() {
        return barData;
    }

    public void setBarData(List<Double> barData) {
        this.barData = barData;
    }
}
