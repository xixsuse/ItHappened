package ru.lod_misis.ithappened.Statistics.Facts.Models;

import java.util.List;

import ru.lod_misis.ithappened.Domain.Event;

public class IllustartionModel {

    private IllustrationType type;
    private List<Double> pieData;
    private Event eventRef;
    private List<Double> graphData;
    private List<Double> barData;
    private List<FrequentEventsFactModel> frequentEventsList;
    private List<WeekDaysFactModel> weekDaysFactList;
    private List<DayTimeFactModel> dayTimeFactList;

    public IllustartionModel(IllustrationType type){
        this.type = type;
        this.pieData = null;
        this.eventRef = null;
        this.graphData = null;
        this.barData = null;
    }

    public List<DayTimeFactModel> getDayTimeFactList() {
        return dayTimeFactList;
    }

    public void setDayTimeFactList(List<DayTimeFactModel> dayTimeFactList) {
        this.dayTimeFactList = dayTimeFactList;
    }

    public List<WeekDaysFactModel> getWeekDaysFactList() {
        return weekDaysFactList;
    }

    public void setWeekDaysFactList(List<WeekDaysFactModel> weekDaysFactList) {
        this.weekDaysFactList = weekDaysFactList;
    }

    public IllustrationType getType() {
        return type;
    }

    public void setType(IllustrationType type) { this.type = type; }

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

    public List<FrequentEventsFactModel> getFrequentEventsList() {
        return frequentEventsList;
    }

    public void setFrequentEventsModelList(List<FrequentEventsFactModel> frequentEventsModelList) {
        this.frequentEventsList = frequentEventsModelList;
    }
}
