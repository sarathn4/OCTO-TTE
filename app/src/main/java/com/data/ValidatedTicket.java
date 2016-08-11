package com.data;

import org.json.JSONObject;

import java.io.Serializable;

public class ValidatedTicket implements Serializable {
    String ticketid;
    String passenger_id;
    String station_id;
    String tte_id;
    String validated_on;
    String validated_status;

    public String getTicketid() {
        return ticketid;
    }

    public void setTicketid(String ticketid) {
        this.ticketid = ticketid;
    }

    public String getPassenger_id() {
        return passenger_id;
    }

    public void setPassenger_id(String passenger_id) {
        this.passenger_id = passenger_id;
    }

    public String getStation_id() {
        return station_id;
    }

    public void setStation_id(String station_id) {
        this.station_id = station_id;
    }

    public String getTte_id() {
        return tte_id;
    }

    public void setTte_id(String tte_id) {
        this.tte_id = tte_id;
    }

    public String getValidated_on() {
        return validated_on;
    }

    public void setValidated_on(String validated_on) {
        this.validated_on = validated_on;
    }

    public String getValidated_status() {
        return validated_status;
    }

    public void setValidated_status(String validated_status) {
        this.validated_status = validated_status;
    }

    public JSONObject getJsonObject(){

        JSONObject validated_ticket = null;
        try {
            validated_ticket = new JSONObject();
            validated_ticket.put("ticketid",getTicketid());
            validated_ticket.put("passenger_id",getPassenger_id());
            validated_ticket.put("station_id",getStation_id());
            validated_ticket.put("tte_id",getTte_id());
            validated_ticket.put("validated_on",getValidated_on());
            validated_ticket.put("validated_status",getValidated_status());
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return validated_ticket;
    }
//station_id' => 2,
// 'tte_id' => 2,
// 'passenger_id' => 10,
// 'ticketid' => '1',
// 'validated_on' => '2015-10-10',
// 'validated_status' => 'Valid'
}
