#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.demo;

import java.net.URL;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;

import ${package}.Bus;
import ${package}.Events;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import net.engio.mbassy.listener.Handler;
import net.engio.mbassy.listener.Listener;

@Listener
public class DemoPresenter implements Initializable {
	final static Logger logger = Logger.getLogger(DemoPresenter.class);
	
    @FXML
    private Label label;
    
    @FXML
    private void handleButtonAction(ActionEvent event) {
        System.out.println("You clicked me!");
        label.setText("Hello World!");
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    	Bus.getInstance().subscribe(this);
    } 
    
    @Handler
    public void init(Events.Init event) {
    	
    }
    
    @Handler
	public void close(Events.Close event) {
		Bus.getInstance().unsubscribe(this);
	}
}
