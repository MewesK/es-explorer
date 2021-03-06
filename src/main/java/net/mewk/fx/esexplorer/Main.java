package net.mewk.fx.esexplorer;

import com.airhacks.afterburner.injection.Injector;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import net.mewk.fx.esexplorer.view.MainView;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        // Create main view
        MainView mainView = new MainView();

        // Initialize stage
        stage.setTitle("ES Explorer");
        stage.getIcons().add(new Image("/net/mewk/fx/esexplorer/es-explorer.png"));
        stage.setScene(new Scene(mainView.getView()));
        stage.show();
    }

    @Override
    public void stop() throws Exception {
        Injector.forgetAll();
    }

    public static void main(String[] args) {
        System.setProperty("prism.text", "t2k");
        System.setProperty("prism.lcdtext", "true");

        launch(args);
    }
}
