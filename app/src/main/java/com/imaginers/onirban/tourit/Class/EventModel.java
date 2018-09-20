package com.imaginers.onirban.tourit.Class;

public class EventModel {

    //This class is the model of arraylist used for event fragment
    //By using it we can get data and set data and populate the arraylist

    private String eventName,eventDesti,eventDate,eventTime,eventDes,eventBudget,delId;

    public EventModel() {

    }

    public EventModel(String eventName, String eventDesti, String eventDate, String eventTime, String eventDes, String eventBudget, String delId) {

        this.eventName=eventName;
        this.eventDesti=eventDesti;
        this.eventDate=eventDate;
        this.eventTime=eventTime;
        this.eventDes=eventDes;
        this.eventBudget=eventBudget;
        this.delId=delId;
    }

    public String getEventBudget() {
        return eventBudget;
    }

    public String getEventDate() {
        return eventDate;
    }

    public String getEventDes() {
        return eventDes;
    }

    public String getEventDesti() {
        return eventDesti;
    }

    public String getEventName() {
        return eventName;
    }

    public String getEventTime() {
        return eventTime;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public void setEventDesti(String eventDesti) {
        this.eventDesti = eventDesti;
    }

    public String getDelId() {
        return delId;
    }
}
