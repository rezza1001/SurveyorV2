package com.wadaro.surveyor.ui.survey;

import org.json.JSONException;
import org.json.JSONObject;

public class OrderHolder {
    public String tmpId         = "";
    public String customer      = "";
    public String customerName      = "";
    public String goods         = "";
    public String goodsName     = "";
    public String qty           = "";
    public String survey           = "0";


    public JSONObject pack(){
        JSONObject data = new JSONObject();
        try {
            data.put("tmpId",tmpId);
            data.put("customer",customer);
            data.put("customerName",customerName);
            data.put("goods",goods);
            data.put("goodsName",goodsName);
            data.put("qty",qty);
            data.put("survey",survey);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return data;
    }

    public void unPack(String pData){
        try {
            JSONObject data = new JSONObject(pData);
            tmpId = data.getString("tmpId");
            customer = data.getString("customer");
            customerName = data.getString("customerName");
            goods = data.getString("goods");
            goodsName = data.getString("goodsName");
            qty = data.getString("qty");
            survey = data.getString("survey");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
