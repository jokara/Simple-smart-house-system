/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package alarm_device;

import entiteti.Song;
import java.net.URL;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.jms.ConnectionFactory;
import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.TextMessage;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

/**
 *
 * @author jn160139
 */
public class Main {

    @Resource(lookup="java:comp/DefaultJMSConnectionFactory")
    private static ConnectionFactory connectionFactory;
    @Resource(lookup="myQueue")
    private static Queue queue;
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        JMSContext context=connectionFactory.createContext();
        JMSConsumer consumer=context.createConsumer(queue);
        boolean indikator=false;
        
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("Alarm_devicePU");
        EntityManager em = emf.createEntityManager();
        
        while(true){
        
            try {
                
                Message msgg=consumer.receive();
                if (msgg instanceof TextMessage) {
                    TextMessage msgt=(TextMessage) msgg;
                    Query q1 = em.createQuery("select s from Song s where s.name=:pesma and s.idUser=:idU");
                    q1.setParameter("pesma", msgt.getText());
                    q1.setParameter("idU", msgt.getIntProperty("id"));
                    List result = q1.getResultList();
                    if (result.isEmpty()){
                        em.getTransaction().begin();
                        Song song=new Song(msgt.getText(),msgt.getIntProperty("id"));
                        em.persist(song);
                        em.getTransaction().commit();
                    }
                    AlarmTrigger at=new AlarmTrigger(msgt.getText());
                    at.call_alarm(msgt.getText(),0,0,0);
                }
                else {
                    ObjectMessage msgo=(ObjectMessage) msgg;
                    if (msgo.getIntProperty("planer")==0){
                        int id=msgo.getIntProperty("id");
                        Query q1 = em.createQuery("select s from Song s where s.name=:pesma and s.idUser=:idU");
                        q1.setParameter("pesma", msgo.getStringProperty("song"));
                        q1.setParameter("idU", id);
                        List result = q1.getResultList();
                        if (result.isEmpty()){
                            em.getTransaction().begin();
                            Song song=new Song(msgo.getStringProperty("song"),id);
                            em.persist(song);
                            em.getTransaction().commit();
                        }
                        AlarmTrigger at=new AlarmTrigger(msgo.getStringProperty("song"),msgo.getIntProperty("min"));     
                        at.call_alarm(msgo.getStringProperty("song"),1,msgo.getIntProperty("min"),0);
                    }
                    else {
                        //planer
                        AlarmTrigger at=new AlarmTrigger(msgo.getIntProperty("sat"),msgo.getIntProperty("min"),msgo.getStringProperty("song"));     
                        at.call_alarm(msgo.getStringProperty("song"),2,0,0);
                    }
                }
                Thread.sleep(5000);
                
            } catch (JMSException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InterruptedException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
 
}
