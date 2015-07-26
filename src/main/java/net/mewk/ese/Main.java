package net.mewk.ese;

import com.google.inject.Guice;
import com.google.inject.Injector;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import net.mewk.ese.view.MainView;

public class Main extends Application {

    public static final Injector INJECTOR = Guice.createInjector();

    @Override
    public void start(Stage stage) throws Exception {
        // Create main view
        MainView mainView = new MainView();

        // Initialize stage
        stage.setTitle("ES Explorer");
        stage.getIcons().add(new Image("/net/mewk/ese/icon.png"));
        stage.setScene(new Scene(mainView.getView()));
        stage.show();
    }

    @Override
    public void stop() throws Exception {
    }

    public static void main(String[] args) {
        System.setProperty("prism.text", "t2k");
        System.setProperty("prism.lcdtext", "true");

        launch(args);
    }
}
