/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package youtube_device;

import entiteti.Song;
import java.io.IOException;
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
    @Resource(lookup="YTQueue")
    private static Queue queue;

    
    public static String nameSong="";
    
    public static void main(String[] args) {
        JMSContext context=connectionFactory.createContext();
        JMSConsumer consumer=context.createConsumer(queue);
        //
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("YouTube_devicePU");
        EntityManager em = emf.createEntityManager();
        
        while(true) {
            try {
                Message msg1=consumer.receive();
                
                if (msg1 instanceof TextMessage){
                    TextMessage msg=(TextMessage) msg1;
                    nameSong=msg.getText();
                    int id=msg.getIntProperty("id");
                    Query q1 = em.createQuery("select s from Song s where s.name=:pesma and s.idUser=:idU");
                    q1.setParameter("pesma", nameSong);
                    q1.setParameter("idU", id);
                    List result = q1.getResultList();
                    if (result.isEmpty()){
                        em.getTransaction().begin();
                        Song song=new Song(nameSong,id);
                        em.persist(song);
                        em.getTransaction().commit();
                    }
                    //Process novi=Runtime.getRuntime().exec("java -jar C:\\Users\\jn160139\\Desktop\\is1_projekat_2019\\youtube\\java\\target\\samples-0.0.1-SNAPSHOT-jar-with-dependencies.jar "+nameSong);
                    ProcessBuilder pb = new ProcessBuilder("cmd", "/c \"start java -jar C:\\Users\\jn160139\\Desktop\\is1_projekat_2019\\youtube\\java\\target\\samples-0.0.1-SNAPSHOT-jar-with-dependencies.jar \"",nameSong);
                    pb.start();
                }
                else {
                    ObjectMessage obj=(ObjectMessage) msg1;
                    Query q1 = em.createQuery("select s from Song s where s.idUser=:idU");
                    q1.setParameter("idU", msg1.getIntProperty("id"));
                    List result = q1.getResultList();
                    System.out.println("Pesme koje je pustao korisnik"+msg1.getIntProperty("id")+":");
                    for (Object object : result) {
                        Song s=(Song) object;
                        System.out.println(s.getName());
                    }
                }
               
            } catch (JMSException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }
            catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }   
   
        }
  
    }
    
}
