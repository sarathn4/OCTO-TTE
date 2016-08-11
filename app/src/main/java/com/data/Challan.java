package com.data;


import org.json.JSONObject;

public class Challan {
    String invoice_no;
    String tte_id;
    String station_id;
    String payment_date;
    String passenger_name;
    String phone;
    String penalty_type;
    String description;
    String challan_amount;
    String payment_mode;
    String passenger_id;
    public String getPayment_mode() {
        return payment_mode;
    }

    public void setPayment_mode(String payment_mode) {
        this.payment_mode = payment_mode;
    }

    public String getPassenger_id() {
        return passenger_id;
    }

    public void setPassenger_id(String passenger_id) {
        this.passenger_id = passenger_id;
    }


    public String getInvoice_no() {
        return invoice_no;
    }

    public void setInvoice_no(String invoice_no) {
        this.invoice_no = invoice_no;
    }

    public String getTte_id() {
        return tte_id;
    }

    public void setTte_id(String tte_id) {
        this.tte_id = tte_id;
    }

    public String getStation_id() {
        return station_id;
    }

    public void setStation_id(String station_id) {
        this.station_id = station_id;
    }

    public String getPayment_date() {
        return payment_date;
    }

    public void setPayment_date(String payment_date) {
        this.payment_date = payment_date;
    }

    public String getPassenger_name() {
        return passenger_name;
    }

    public void setPassenger_name(String passenger_name) {
        this.passenger_name = passenger_name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPenalty_type() {
        return penalty_type;
    }

    public void setPenalty_type(String penalty_type) {
        this.penalty_type = penalty_type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getChallan_amount() {
        return challan_amount;
    }

    public void setChallan_amount(String challan_amount) {
        this.challan_amount = challan_amount;
    }


    public JSONObject getJSONObject(){
//        station_id' => 2,
// 'tte_id' => 2,
// 'passenger_id' => 10,
// 'passenger_name' => 'test1',
// 'mobile' => '11111',
// 'penalty_type' => 'Without Ticket',
// 'payment_date' => '2015-10-10',
// 'payment_mode' => 'Cash',
// 'challan_amount' => 1,
// 'description' => "description"
        JSONObject challan = null;
        try{
            challan = new JSONObject();
            challan.put("invoice_no",getInvoice_no());
            challan.put("station_id",getStation_id());
            challan.put("tte_id",getTte_id());
            challan.put("passenger_id",getPassenger_id());
            challan.put("passenger_name",getPassenger_name());
            challan.put("mobile",getPhone());
            challan.put("penalty_type",getPenalty_type());
            challan.put("payment_date",getPayment_date());
            challan.put("payment_mode",getPayment_mode());
            challan.put("challan_amount",getChallan_amount());
            challan.put("description",getDescription());

        }
        catch(Exception e){
            e.printStackTrace();
        }
        return challan;
    }
}
