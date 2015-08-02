package net.mewk.fx.esexplorer.view;

import com.airhacks.afterburner.views.FXMLView;
import net.mewk.fx.esexplorer.model.connection.Connection;

public class ServerView extends FXMLView {

    public ServerView(Connection connection) {
        super(s -> {
            switch (s) {
                case "connection":
                    return connection;
                default:
                    return null;
            }
        });
    }
}
