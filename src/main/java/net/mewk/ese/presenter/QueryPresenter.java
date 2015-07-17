package net.mewk.ese.presenter;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableView;
import net.mewk.ese.Highlighter;
import net.mewk.ese.Main;
import net.mewk.ese.mapper.ui.ResultViewMapper;
import net.mewk.ese.model.Server;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.search.SearchResponse;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;

import java.net.URL;
import java.util.ResourceBundle;

public class QueryPresenter implements Initializable {

    private ObjectProperty<Server> server = new SimpleObjectProperty<>();

    @FXML
    public CodeArea queryCodeArea;
    @FXML
    public CodeArea resultCodeArea;
    @FXML
    public TreeTableView resultTreeTableView;

    public void initialize(URL location, ResourceBundle resources) {
        queryCodeArea.setParagraphGraphicFactory(LineNumberFactory.get(queryCodeArea));
        queryCodeArea.textProperty().addListener((obs, oldText, newText) -> {
            queryCodeArea.setStyleSpans(0, Highlighter.computeHighlighting(newText));
        });

        resultCodeArea.setParagraphGraphicFactory(LineNumberFactory.get(queryCodeArea));
        resultCodeArea.textProperty().addListener((obs, oldText, newText) -> {
            resultCodeArea.setStyleSpans(0, Highlighter.computeHighlighting(newText));
        });
    }

    public void handleRunButtonAction(ActionEvent actionEvent) {
        if (server.get() != null) {
            // Run search on different thread
            server.get().search(new String[]{"_all"}, queryCodeArea.getText(), new ActionListener<SearchResponse>() {
                @Override
                public void onResponse(SearchResponse searchResponse) {
                    TreeItem<Object> resultItem = (TreeItem<Object>) Main.getMapperManager().findByClass(ResultViewMapper.class).map(searchResponse);
                    // Return to FX thread
                    Platform.runLater(() -> {
                        resultTreeTableView.setRoot(resultItem);
                        resultCodeArea.replaceText(searchResponse.toString());
                    });
                }

                @Override
                public void onFailure(Throwable e) {
                    // TODO
                }
            });
        }
    }

    public Server getServer() {
        return server.get();
    }

    public ObjectProperty<Server> serverProperty() {
        return server;
    }

    public void setServer(Server server) {
        this.server.set(server);
    }
}
