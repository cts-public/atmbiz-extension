package com.atmbiz.extensions.dao;

import com.generalbytes.batm.server.extensions.IOpeningHours;

public class OpeningHours {

    IOpeningHours.OpeningDay day;

    String from;

    String to;

    Integer order;


    public IOpeningHours.OpeningDay getDay() {
        return day;
    }

    public void setDay(IOpeningHours.OpeningDay day) {
        this.day = day;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }
}