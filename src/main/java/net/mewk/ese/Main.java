package net.mewk.ese;

import com.airhacks.afterburner.injection.Injector;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import net.mewk.ese.manager.ConnectionManager;
import net.mewk.ese.manager.MapperManager;
import net.mewk.ese.manager.ServerManager;
import net.mewk.ese.view.MainView;

import java.util.HashMap;
import java.util.Map;

public class Main extends Application {

    private static ConnectionManager connectionManager;
    private static ServerManager serverManager;
    private static MapperManager mapperManager;

    @Override
    public void start(Stage stage) throws Exception {
        connectionManager = new ConnectionManager();
        serverManager = new ServerManager();
        mapperManager = new MapperManager();

        Map<Object, Object> customProperties = new HashMap<>();
        customProperties.put("connectionManager", connectionManager);
        customProperties.put("serverManager", serverManager);
        customProperties.put("mapperManager", mapperManager);
        Injector.setConfigurationSource(customProperties::get);

        MainView mainView = new MainView();

        stage.setTitle("ES Explorer");
        stage.getIcons().add(new Image("/icon.png"));
        stage.setScene(new Scene(mainView.getView()));
        stage.show();
    }

    @Override
    public void stop() throws Exception {
        connectionManager.stop();
        serverManager.stop();
        Injector.forgetAll();
    }

    public static void main(String[] args) {
        launch(args);
    }

    public static ConnectionManager getConnectionManager() {
        return connectionManager;
    }

    public static ServerManager getServerManager() {
        return serverManager;
    }

    public static MapperManager getMapperManager() {
        return mapperManager;
    }
}
