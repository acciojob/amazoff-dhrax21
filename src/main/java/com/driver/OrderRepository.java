package com.driver;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;

@Repository
public class OrderRepository {

    HashMap<String,Order> orderDb=new HashMap<>();
    HashMap<String,DeliveryPartner> partnerDb=new HashMap<>();
    HashMap<String, List<String>> pairDb=new HashMap<>();
    HashMap<String,String> assignedDb=new HashMap<>();

    public String addOrder(Order order){
        orderDb.put(order.getId(),order);
        return "Added";
    }
    public String addPartner(String partnerId){
        DeliveryPartner dp=new DeliveryPartner(partnerId);
        partnerDb.put(partnerId,dp);
        return "Added";
    }

    public String addOrderPartnerPair(String orderId,String partnerId){
        List<String> list=pairDb.getOrDefault(partnerId,new ArrayList<>());
        list.add(orderId);
        pairDb.put(partnerId,list);
        assignedDb.put(orderId,partnerId);

        DeliveryPartner dp=partnerDb.get(partnerId);
        dp.setNumberOfOrders(list.size());
        return "Added";
    }

    public Order getOrderById(String orderId){
        for(String s : orderDb.keySet()){
            if(s.equals(orderId)){
                return orderDb.get(s);
            }
        }
        return null;
    }

    public DeliveryPartner getPartnerById(String partnerId){
        if(partnerDb.containsKey(partnerId)){
            return partnerDb.get(partnerId);
        }
        return null;
    }

    public int getOrderCountByPartnerId(String partnerId){
        int orders = pairDb.getOrDefault(partnerId, new ArrayList<>()).size();
        return orders;
    }

    public List<String> getOrdersByPartnerId(String partnerId){
        List<String> orders=pairDb.getOrDefault(partnerId,new ArrayList<>());
        return orders;
    }

    public List<String> getAllOrders(){
        List<String> orders=new ArrayList<>();
        for(String s : orderDb.keySet()){
            orders.add(s);
        }
        return orders;
    }
    public int getCountOfUnassignedOrders(){
        int count=orderDb.size()-assignedDb.size();
        return count;
    }

    public int getOrdersLeftAfterGivenTimeByPartnerId(String time,String partnerId){

        int count=0;
        List<String> list=pairDb.get(partnerId);
        int deliveryTime=Integer.parseInt(time.substring(0,2)) * 60 + Integer.parseInt(time.substring(3));
        for(String s : list){
            Order order=orderDb.get(s);
            if(order.getDeliveryTime()>deliveryTime){
                count++;
            }
        }
        return count;
    }

    public String getLastDeliveryTimeByPartnerId(String partnerId){
        String time="";
        List<String> list=pairDb.get(partnerId);
        int deliveryTime=0;

        for(String s : list){
            Order order=orderDb.get(s);
            deliveryTime=Math.max(deliveryTime,order.getDeliveryTime());
        }
        int hour=deliveryTime/60;

        String shour="";

        if(hour<10){
            shour="0"+String.valueOf(hour);
        }else{
            shour=String.valueOf(hour);
        }

        int min=deliveryTime%60;

        String smin="";
        if(min<10){
            smin="0"+String.valueOf(min);
        }else{
            smin=String.valueOf(min);
        }
        time=shour + ":" + smin;
        return time;
    }

    public String deletePartnerById(String partnerId){
        partnerDb.remove(partnerId);

        List<String> list=pairDb.getOrDefault(partnerId,new ArrayList<>());
        ListIterator<String> itr=list.listIterator();

        while(itr.hasNext()){
            String str=itr.next();
            assignedDb.remove(str);
        }
        pairDb.remove(partnerId);
        return "Deleted";
    }
    public String deleteOrderById(String orderId) {

        // Delete an order and also
        // remove it from the assigned order of that partnerId
        orderDb.remove(orderId);
        String partnerId = assignedDb.get(orderId);
        assignedDb.remove(orderId);
        List<String> list = pairDb.get(partnerId);

        ListIterator<String> itr = list.listIterator();
        while (itr.hasNext()) {
            String s = itr.next();
            if (s.equals(orderId)) {
                itr.remove();
            }
        }
        pairDb.put(partnerId, list);

        return "Deleted";

    }




}
