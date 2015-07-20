package net.mewk.ese;

import com.google.inject.Guice;
import com.google.inject.Injector;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import net.mewk.ese.manager.ConnectionManager;
import net.mewk.ese.manager.ServerManager;
import net.mewk.ese.view.MainView;

import javax.inject.Inject;

public class Main extends Application {

    public static final Injector INJECTOR = Guice.createInjector();

    @Inject
    ConnectionManager connectionManager;
    @Inject
    ServerManager serverManager;

    @Override
    public void start(Stage stage) throws Exception {
        INJECTOR.injectMembers(this);

        MainView mainView = new MainView();

        stage.setTitle("ES Explorer");
        stage.getIcons().add(new Image("/net/mewk/ese/icon.png"));
        stage.setScene(new Scene(mainView.getView()));
        stage.show();
    }

    @Override
    public void stop() throws Exception {
        connectionManager.stop();
        serverManager.stop();
    }

    public static void main(String[] args) {
        System.setProperty("prism.text", "t2k");
        System.setProperty("prism.lcdtext", "true");

        launch(args);
    }
}
