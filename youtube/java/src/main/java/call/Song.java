package call;

import java.io.IOException;
import javafx.application.Application;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.beans.value.ChangeListener;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import java.awt.Desktop;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;



public class Song extends Application {
    
    public String nameSong="";

    public String getNameSong() {
        return nameSong;
    }

    public void setNameSong(String nameSong) {
        this.nameSong = nameSong;
    }
            
    @Override
    public void start(final Stage stage) throws Exception {
        stage.setWidth(1000);
        stage.setHeight(850);
        Scene scene = new Scene(new Group());


        final WebView browser = new WebView();
        final WebEngine webEngine = browser.getEngine();

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(browser);

        webEngine.getLoadWorker().stateProperty()
                .addListener(new ChangeListener<Worker.State>() {
                    @Override
                    public void changed(ObservableValue ov, Worker.State oldState, Worker.State newState) {

                        if (newState == Worker.State.SUCCEEDED) {
                            stage.setTitle(webEngine.getLocation());
                        }

                    }
                });
        webEngine.load("https://www.youtube.com/watch?v="+nameSong);
        
        scene.setRoot(scrollPane);

        stage.setScene(scene);
        stage.show();
    }


    public void callSong(String[] args,String name) {
        try {
            setNameSong("https://www.youtube.com/watch?v="+name);
            System.out.println("***********"+getNameSong());
            Desktop d = Desktop.getDesktop();
            d.browse(new URI(getNameSong()));
        } catch (IOException ex) {
            Logger.getLogger(Song.class.getName()).log(Level.SEVERE, null, ex);
        } catch (URISyntaxException ex) {
            Logger.getLogger(Song.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}