package com.wadaro.surveyor.ui.report.adapter;

import org.json.JSONObject;

import java.util.Date;

public class BookingHolder {
    public String booking_id = "";
    public String booking_demo = "";
    public Date booking_date = new Date();
    public String coordinator_name = "";
    public String sales = "";
    public String status = "";
    public JSONObject data = new JSONObject();
}
