/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package user_device;

import java.util.Scanner;
import javax.annotation.Resource;
import javax.jms.ConnectionFactory;
import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.JMSProducer;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.TextMessage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 *
 * @author jn160139
 */
public class Main {
    
    @Resource(lookup="java:comp/DefaultJMSConnectionFactory")
    private static ConnectionFactory connectionFactory;
    @Resource(lookup="myQueue")
    private static Queue queue;
    @Resource(lookup="YTQueue")
    private static Queue queue1;
    @Resource(lookup="myPlaner")
    private static Queue queue2;
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        JMSContext context=connectionFactory.createContext();
        JMSProducer producer=context.createProducer();
        int opcija=999;
        int opcija1=999;
        int id=0;
        Scanner scanner=new Scanner(System.in);
        
            while (opcija1!=2){
                
                System.out.println("Unesite svoj ID:");
                id=scanner.nextInt();
                opcija=999;
                while(opcija!=4){
                    System.out.println("Unesite zeljenu instrukciju:");
                    System.out.println("1. Pustanje zeljene pesme");
                    System.out.println("2. Navijanje alarma");
                    System.out.println("3. Otvori planer");
                    System.out.println("4. Izloguj se");
                    System.out.println("5. Izlistaj sve pesme za datog korisnika");
                    opcija=scanner.nextInt();

                    switch(opcija){
                        case 1: {
                        try {
                            System.out.println("Unesite zeljenu pesmu:");
                            String link=scanner.next();
                            TextMessage tm=context.createTextMessage(link);
                            tm.setIntProperty("id", id);
                            producer.send(queue1, tm);
                            break;
                        } catch (JMSException ex) {
                            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        }
                        case 2:{
                            System.out.println("Da li zelite da navijete periodican alarm?");
                            System.out.println("1. Da");
                            System.out.println("2. Ne");
                            int periodican=scanner.nextInt();
                            System.out.println("Unesite zeljenu pesmu za alarm:");
                            String link=scanner.next();
                            if (periodican==1){
                                System.out.println("Unesite na koliko minuta zelite da se ponavlja:");
                                int minuti=scanner.nextInt();
                                try {
                                    ObjectMessage msg=context.createObjectMessage();
                                    msg.setStringProperty("song", link);
                                    msg.setIntProperty("min", minuti);
                                    msg.setIntProperty("planer", 0);
                                    msg.setIntProperty("per", 1);
                                    msg.setIntProperty("id", id);
                                    producer.send(queue, msg);
                                } catch (JMSException ex) {
                                    Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            }
                            else {
                                try {
                                    TextMessage msg=context.createTextMessage(link);
                                    msg.setIntProperty("id", id);
                                    producer.send(queue, msg);
                                } catch (JMSException ex) {
                                    Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            }

                            break;
                        }
                        case 3:{

                            System.out.println("Unesite zeljenu opciju:");
                            System.out.println("1. Napravi obavezu");
                            System.out.println("2. Izlistaj sve obaveze");
                            int izbor=scanner.nextInt();
                            switch(izbor){
                                case 1:{
                                    System.out.println("Unesite zeljenu pesmu za planer:");
                                    String link=scanner.next();
                                    System.out.println("Unesite Vasu obavezu:");
                                    String obaveza=scanner.next();
                                    System.out.println("U koliko sati je vasa obaveza?");
                                    int sat=scanner.nextInt();
                                    System.out.println("U koliko minuta je vasa obaveza?");
                                    int minut=scanner.nextInt();
                                    System.out.println("Da li Vam treba destinacija?");
                                    System.out.println("1. Da");
                                    System.out.println("2. Ne");
                                    int dest=scanner.nextInt();
                                    String destinacija=null;
                                    int mx=0,sx=0;
                                    if (dest==1){
                                        System.out.println("Unesite grad u kom se nalazite:");
                                        String grad1=scanner.next();
                                        System.out.println("Unesite ulicu u kojoj se nalazite(%20 umesto razmaka):");
                                        String ulica1=scanner.next();
                                        System.out.println("Unesite grad gde zelite da idete:");
                                        String grad2=scanner.next();
                                        System.out.println("Unesite ulicu gde zelite da idete idete(%20 umesto razmaka):");
                                        String ulica2=scanner.next();
                                        Double [] niz1=koordinate(grad1, ulica1);
                                        Double [] niz2=koordinate(grad2, ulica2);
                                        Double dist=distance(niz1[0], niz1[1], niz2[0], niz2[1],'K');
                                        double t=dist/15;//h
                                        double mi=t*60,sa=0;
                                        if(mi<1) mi=1;
                                        else{
                                           sa=mi/60;
                                           mi=mi%60;
                                        }
                                        mi=Math.round(mi);
                                        sa=Math.round(sa);
                                        mx=(int) mi;
                                        sx=(int) sa;
                                        System.out.println("Vreme potrebno da stignete: "+sa+"h i "+mi+" min");
                                    }
                                    int rez_h=0,rez_m=0;
                                    rez_h=sat-sx;
                                    rez_m=minut-mx;
                                    if(rez_m<0){
                                        rez_h--;
                                        rez_m=60+rez_m;
                                    }
                                    try {
                                        ObjectMessage msg=context.createObjectMessage();
                                        msg.setStringProperty("song", link);
                                        msg.setStringProperty("obaveza", obaveza);
                                        //msg.setStringProperty("dest", destinacija);
                                        msg.setIntProperty("min",minut);
                                        msg.setIntProperty("sat",sat);
                                        msg.setIntProperty("planer", 1);
                                        msg.setIntProperty("per", 0);
                                        msg.setIntProperty("id", id);
                                        producer.send(queue2, msg);
                                        } catch (JMSException ex) {
                                            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                                        }
                                    break;
                                    }
                                case 2: {
                                    TextMessage tm=context.createTextMessage("Izlistaj");
                                    producer.send(queue2, tm);
                                    break;
                                }                      
                                }
                            break;
                            }
                        case 5:{
                        try {
                            ObjectMessage obj=context.createObjectMessage();
                            obj.setIntProperty("list", 1);
                            obj.setIntProperty("id", id);
                            producer.send(queue1, obj);
                            break;
                        } catch (JMSException ex) {
                            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                        }
                            
                        }



                        default: break;
                    }
                }
                
                System.out.println("Da li drugi korisnik zeli da koristi sistem?");
                System.out.println("1. Da");
                System.out.println("2. Ne");
                opcija1=scanner.nextInt();
                
            }
        
        //https://www.youtube.com/watch?v=TVuipQx5zYY
            /*try {
                ObjectMessage msg=context.createObjectMessage();
                msg.setStringProperty("song", "https://www.youtube.com/watch?v=TVuipQx5zYY");
                msg.setIntProperty("min", 1);
                msg.setIntProperty("planer", 0);
                msg.setIntProperty("per", 1);
                producer.send(queue, msg);
                
                Thread.sleep(1000);
            
                TextMessage tm=context.createTextMessage("https://www.youtube.com/watch?v=IC58RluetnQ");
                producer.send(queue,tm);
                
            } catch (InterruptedException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            
        } catch (JMSException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }*/
        
    }
    
    
    private static double rad2deg(double rad) {
      return (rad * 180.0 / Math.PI);
    }
    
    private static double deg2rad(double deg) {
      return (deg * Math.PI / 180.0);
    }
    
    private static double distance(double lat1, double lon1, double lat2, double lon2, char unit) {
      double theta = lon1 - lon2;
      double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
      dist = Math.acos(dist);
      dist = rad2deg(dist);
      dist = dist * 60 * 1.1515;
      if (unit == 'K') {
        dist = dist * 1.609344;
      } else if (unit == 'N') {
        dist = dist * 0.8684;
        }
      return (dist);
    }
    
    @SuppressWarnings("empty-statement")
    public static Double[] koordinate(String g, String u){
            Double x = null;
            Double y = null;
        try {
            
            String grad=g;
            String ulica=u;
            URL url = new URL("http://geocoder.api.here.com/6.2/geocode.json?searchtext=1%20"+grad+"%20"+ulica+"%20Serbia&app_id=hxCiIFl1ivyBT3sg4Db1&app_code=f8bxpSdJVSqQiTO00I1XMA&gen=8&fbclid=IwAR38eM8SUMva4DFdsOvE32WJkMNXrEkcUvDFPkqxn1X9KJGGAUG9EbjoXCc");
            //URL url=new URL("http://example.com");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            int status = con.getResponseCode();
            BufferedReader in = new BufferedReader(
            new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer content = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            
            //System.out.println(content.toString());
            String s = content.toString();
            String[] tokens = s.split(",");
            int i=0;
            String longitude="", latitude="";
            for (String t : tokens){
                i++;
                //System.out.println(i+":"+t);
                if((i==13)){
                   // System.out.println("10:"+t);
                    String[] xx = t.split(":");
                    longitude=xx[2];
                    x=Double.parseDouble(longitude);
                    
                }
                if((i==14)){
                    //System.out.println("11:"+t);
                    String[] yy = t.split(":");
                    latitude=yy[1];
                    String[] l = latitude.split("}");
                    String lat=l[0];
                    y=Double.parseDouble(lat);
                    
                }
            }
            in.close();
            con.disconnect();
            //System.out.println("lat: "+latitude+" long "+longitude);
        } catch (ProtocolException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        Double [] niz = new Double[2];
        niz[0]=x; niz[1]=y;
        return niz;

    
    }  
}
