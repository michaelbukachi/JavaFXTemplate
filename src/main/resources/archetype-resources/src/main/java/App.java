#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package};


import com.airhacks.afterburner.injection.Injector;
import ${package}.demo.DemoView;
import java.net.CookieHandler;
import java.net.CookiePolicy;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import static javafx.application.Application.launch;
import javafx.stage.WindowEvent;


public class App extends Application {

    @Override
    public void start(Stage stage) throws Exception { 
        CookieHandler.setDefault(new CustomCookieManager(new PersistentCookieStore(), CookiePolicy.ACCEPT_ALL));
        DemoView appView = new DemoView();
        Scene scene = new Scene(appView.getView());
        final String uri = getClass().getResource("app.css").toExternalForm();
        scene.getStylesheets().add(uri);
        
        stage.setScene(scene);
        stage.setOnShown((WindowEvent event) -> {
            Bus.getInstance().post(new Object()).now();
        });
        stage.show();
    }

    @Override
    public void stop() throws Exception {
        Injector.forgetAll();
    }

    
    public static void main(String[] args) {
        launch(args);
    }

}
