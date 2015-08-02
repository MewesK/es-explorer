package net.mewk.fx.esexplorer.view;

import com.airhacks.afterburner.views.FXMLView;
import net.mewk.fx.esexplorer.model.connection.Connection;
import net.mewk.fx.esexplorer.model.query.Query;
import net.mewk.fx.esexplorer.presenter.ServerPresenter;

public class QueryView extends FXMLView {

    public QueryView(Connection connection, Query query, ServerPresenter serverPresenter) {
        super(s -> {
            switch (s) {
                case "connection":
                    return connection;
                case "query":
                    return query;
                case "serverPresenter":
                    return serverPresenter;
                default:
                    return null;
            }
        });
    }
}
