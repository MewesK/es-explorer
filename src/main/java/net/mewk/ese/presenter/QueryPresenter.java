package net.mewk.ese.presenter;

import com.google.gson.*;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import net.mewk.ese.CodeArea;
import net.mewk.ese.Highlighter;
import net.mewk.ese.mapper.ui.ResultViewMapper;
import net.mewk.ese.model.server.Server;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.Glyph;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.search.SearchResponse;
import org.fxmisc.richtext.StyleSpansBuilder;
import org.fxmisc.richtext.TwoDimensional;

import javax.inject.Inject;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class QueryPresenter implements Initializable {

    private static final Logger logger = LogManager.getLogger();

    private final ObjectProperty<Server> server = new SimpleObjectProperty<>();

    @Inject
    ResultViewMapper resultViewMapper;

    @FXML
    public SplitPane querySplitPane;
    @FXML
    public CodeArea queryCodeArea;
    @FXML
    public AnchorPane resultPane;
    @FXML
    public Glyph hideResultPaneButtonGlyph;
    @FXML
    public CodeArea resultCodeArea;
    @FXML
    public TreeTableView<Object> resultTreeTableView;
    @FXML
    public TreeTableColumn<TableView, Object> resultTreeTableViewIndexColumn;
    @FXML
    public TreeTableColumn<TableView, Object> resultTreeTableViewNameColumn;
    @FXML
    public TreeTableColumn<TableView, Object> resultTreeTableViewValueColumn;
    @FXML
    public TreeTableColumn<TableView, Object> resultTreeTableViewScoreColumn;

    public void initialize(URL location, ResourceBundle resources) {
        resultTreeTableViewIndexColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("index"));
        resultTreeTableViewNameColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("name"));
        resultTreeTableViewValueColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("value"));
        resultTreeTableViewScoreColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("score"));

        queryCodeArea.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                JsonParser jp = new JsonParser();

                try {
                    JsonElement je = jp.parse(newValue);
                } catch (JsonSyntaxException e) {
                    logger.error(e.getMessage(), e);

                    Pattern pattern = Pattern.compile("line (?<line>\\d+) column (?<column>\\d+)");
                    Matcher matcher = pattern.matcher(e.getMessage());
                    if (matcher.find()) {
                        String line = matcher.group("line");
                        String column = matcher.group("column");
                        if (line != null && column != null) {
                            int parsedLine = Integer.parseInt(line) - 1;
                            TwoDimensional.Position position = queryCodeArea.position(parsedLine, 0);
                            String text = queryCodeArea.getText(parsedLine);

                            StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();
                            spansBuilder.add(Collections.emptyList(), position.toOffset());
                            spansBuilder.add(Collections.singleton("error"), text.length());
                            spansBuilder.add(Collections.emptyList(), newValue.length() - position.toOffset() - text.length());

                            queryCodeArea.setStyleSpans(0, spansBuilder.create());
                        }
                    }
                }
            }
        });

        resultPane.heightProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.equals(oldValue)) {
                if (newValue.intValue() == resultPane.getMinHeight()) {
                    hideResultPaneButtonGlyph.setIcon(FontAwesome.Glyph.CHEVRON_UP);
                    if (querySplitPane.getUserData() == null) {
                        querySplitPane.setUserData(0.7);
                    }
                } else {
                    hideResultPaneButtonGlyph.setIcon(FontAwesome.Glyph.CHEVRON_DOWN);
                    querySplitPane.setUserData(null);
                }
            }
        });
    }

    public void handleQueryRunButtonAction(ActionEvent actionEvent) {
        if (server.get() != null) {
            // Run search on different thread
            server.get().search(new String[]{"_all"}, queryCodeArea.getText(), new ActionListener<SearchResponse>() {
                @Override
                public void onResponse(SearchResponse searchResponse) {
                    TreeItem<Object> resultItem = resultViewMapper.map(searchResponse);
                    // Return to FX thread
                    Platform.runLater(() -> {
                        resultTreeTableView.setRoot(resultItem);
                        resultCodeArea.replaceText(searchResponse.toString());
                        resultCodeArea.setStyleSpans(0, Highlighter.computeHighlighting(resultCodeArea.getText()));
                    });
                }

                @Override
                public void onFailure(Throwable e) {
                    logger.error(e.getMessage(), e);
                }
            });
        }
    }

    public void handleResultTabSelectionChanged(Event event) {
        // Redraw hack
        resultCodeArea.replaceText(resultCodeArea.getText());
        resultCodeArea.setStyleSpans(0, Highlighter.computeHighlighting(resultCodeArea.getText()));
    }

    public void handleHideResultPaneButton(ActionEvent actionEvent) {
        if (querySplitPane.getUserData() == null) {
            querySplitPane.setUserData(querySplitPane.getDividerPositions()[0]);
            querySplitPane.setDividerPosition(0, 1);
            hideResultPaneButtonGlyph.setIcon(FontAwesome.Glyph.CHEVRON_UP);
        } else {
            querySplitPane.setDividerPosition(0, (double) querySplitPane.getUserData());
            querySplitPane.setUserData(null);
            hideResultPaneButtonGlyph.setIcon(FontAwesome.Glyph.CHEVRON_DOWN);
        }
    }

    public void handleReformatButtonAction(ActionEvent actionEvent) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonParser jp = new JsonParser();

        try {
            JsonElement je = jp.parse(queryCodeArea.getText());
            String prettyText = gson.toJson(je);
            if (prettyText != null) {
                queryCodeArea.replaceText(prettyText);
            }
        } catch (JsonSyntaxException e) {
            logger.error(e.getMessage(), e);
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
