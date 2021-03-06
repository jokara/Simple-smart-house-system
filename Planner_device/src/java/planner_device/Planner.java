/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package planner_device;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author jn160139
 */
public class Planner {
    private ArrayList<Duty> dutyList;
    
    public Planner(){
        dutyList=new ArrayList<Duty>();
    }
    
    public void print_Duty(){
        check();
        
        for (Duty duty : dutyList) {
            System.out.println("Obaveza:"+duty.getDuty()+" Vreme:"+duty.getHour()+":"+duty.getMinute()+":"+duty.getSecond());
            if (duty.getDestionation()!=null){
                System.out.println("Destinacija:"+duty.getDestionation());
            }
            
        }
    }
    
    public void addDuty(Duty d){
        dutyList.add(d);
    }
    
    
    public void removeDuty(String name){
        for (Duty duty : dutyList) {
            if (duty.getDuty()==name){
                dutyList.remove(duty);
            }
        }
    }
    
    private void check(){
        if (!dutyList.isEmpty()){
            Date date=new Date();
            int hours=date.getHours();
            int minutes=date.getMinutes();
            int seconds=date.getSeconds();
        
            for (Duty duty : dutyList) {
                if (duty.getHour()<hours) dutyList.remove(duty);
                if ((duty.getHour()==hours)&&(duty.getMinute()<minutes)) dutyList.remove(duty);
            }
        }
    }
}
