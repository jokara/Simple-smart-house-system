/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package planner_device;

import entiteti.Song;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.jms.ConnectionFactory;
import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.JMSProducer;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.TextMessage;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.swing.JFrame;

/*
 *
 * @author jn160139
 */

public class Main {
    
    @Resource(lookup="java:comp/DefaultJMSConnectionFactory")
    public static ConnectionFactory connectionFactory;
    @Resource(lookup="myPlaner")
    public static Queue myPlaner;
    @Resource(lookup="myQueue")
    public static Queue queue;
    
    public static void main(String[] args) {
     
        JMSContext context=connectionFactory.createContext();
        JMSConsumer consumer=context.createConsumer(myPlaner);
        JMSProducer producer=context.createProducer();
        
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("Planner_devicePU");
        EntityManager em = emf.createEntityManager();
        
        Planner planer=new Planner();
        
        
        while(true){
            
            
            try {
                Message msg=consumer.receive();
                if (msg instanceof ObjectMessage){
                    ObjectMessage msg1=(ObjectMessage)msg; 
                    String dest=msg1.getStringProperty("dest");
                    String obaveza=msg1.getStringProperty("obaveza");
                    String pesma=msg1.getStringProperty("song");
                    int sat=msg1.getIntProperty("sat");
                    int minut=msg1.getIntProperty("min");
                    int id=msg1.getIntProperty("id");
                    Query q1 = em.createQuery("select s from Song s where s.name=:pesma and s.idUser=:idU");
                    q1.setParameter("pesma", pesma);
                    q1.setParameter("idU", id);
                    List result = q1.getResultList();
                    if (result.isEmpty()){
                        em.getTransaction().begin();
                        Song song=new Song(pesma,id);
                        em.persist(song);
                        em.getTransaction().commit();
                    }
                    Duty duty=new Duty(obaveza,sat,minut,0);
                    planer.addDuty(duty);
                
                    ObjectMessage msg2=context.createObjectMessage();
                    msg2.setIntProperty("planer", 1);
                    msg2.setIntProperty("sat", sat);
                    msg2.setIntProperty("min", minut);
                    msg2.setStringProperty("song", pesma);
                    producer.send(queue, msg2);
                }
                else {
                    planer.print_Duty();
                }
            } catch (JMSException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }
            }
        
        } 

     
    
}
