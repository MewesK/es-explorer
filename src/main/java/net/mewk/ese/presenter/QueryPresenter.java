package net.mewk.ese.presenter;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableView;
import net.mewk.ese.Main;
import net.mewk.ese.model.Server;
import org.elasticsearch.action.search.SearchResponse;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.StyleSpans;
import org.fxmisc.richtext.StyleSpansBuilder;

import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class QueryPresenter implements Initializable {

    private static final String[] KEYS = new String[]{
            "aggs",
            "bool",
            "boost",
            "boundary_chars",
            "distance_type",
            "exclude",
            "explain",
            "facets",
            "factor",
            "field",
            "fielddata_fields",
            "fields",
            "filter",
            "filtered",
            "force_source",
            "fragment_size",
            "from",
            "function_score",
            "gt",
            "gte",
            "has_child",
            "has_parent",
            "highlight",
            "include",
            "index_options",
            "indices_boost",
            "inner_hits",
            "keywords",
            "lat",
            "lon",
            "lt",
            "lte",
            "match_all",
            "match_phrase",
            "match",
            "matched_fields",
            "min_score",
            "minimum_should_match",
            "missing",
            "mode",
            "must",
            "name",
            "nested_filter",
            "nested",
            "no_match_size",
            "number_of_fragments",
            "operator",
            "order",
            "params",
            "partial_fields",
            "path",
            "phrase_slop",
            "post_filter",
            "post_tags",
            "pre_tags",
            "query_string",
            "query_weight",
            "query",
            "require_field_match",
            "rescore_query_weight",
            "rescore_query",
            "rescore",
            "reverse",
            "script_fields",
            "script_score",
            "script",
            "should",
            "size",
            "slop",
            "sort",
            "stats",
            "tags_schema",
            "term_vector",
            "term",
            "terms",
            "type_name",
            "type",
            "unit",
            "unmapped_type",
            "version",
            "window_size"
    };

    private static final String[] VALUES = new String[]{
            "arc",
            "asc",
            "avg",
            "boolean",
            "desc",
            "double",
            "float",
            "integer",
            "long",
            "max",
            "min",
            "multiply",
            "offsets",
            "phrase",
            "plain",
            "plane",
            "sloppy_arc",
            "string",
            "styled",
            "sum",
            "total",
            "with_positions_offsets"
    };

    private static final String KEY_PATTERN = "\"(" + String.join("|", KEYS) + ")\"";
    private static final String VALUE_PATTERN = "\"(_.+?|" + String.join("|", VALUES) + ")\"";
    private static final String BRACE_PATTERN = "\\{|\\}";
    private static final String BRACKET_PATTERN = "\\[|\\]";
    private static final String STRING_PATTERN = "\"([^\"\\\\]|\\\\.)*\"";
    private static final Pattern PATTERN = Pattern.compile(
            "(?<KEY>" + KEY_PATTERN + ")"
                    + "|(?<VALUE>" + VALUE_PATTERN + ")"
                    + "|(?<BRACE>" + BRACE_PATTERN + ")"
                    + "|(?<BRACKET>" + BRACKET_PATTERN + ")"
                    + "|(?<STRING>" + STRING_PATTERN + ")"
    );


    private ObjectProperty<Server> server = new SimpleObjectProperty<>();

    @FXML
    public CodeArea queryCodeArea;
    @FXML
    public CodeArea resultCodeArea;

    public void initialize(URL location, ResourceBundle resources) {
        queryCodeArea.setParagraphGraphicFactory(LineNumberFactory.get(queryCodeArea));
        queryCodeArea.textProperty().addListener((obs, oldText, newText) -> {
            queryCodeArea.setStyleSpans(0, computeHighlighting(newText));
        });
    }

    private static StyleSpans<Collection<String>> computeHighlighting(String text) {
        Matcher matcher = PATTERN.matcher(text);
        int lastKwEnd = 0;
        StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();
        while (matcher.find()) {
            String styleClass =
                    matcher.group("KEY") != null ? "key" :
                            matcher.group("VALUE") != null ? "value" :
                                    matcher.group("BRACE") != null ? "brace" :
                                            matcher.group("BRACKET") != null ? "bracket" :
                                                    matcher.group("STRING") != null ? "string" :
                                                            null;
            assert styleClass != null;
            spansBuilder.add(Collections.emptyList(), matcher.start() - lastKwEnd);
            spansBuilder.add(Collections.singleton(styleClass), matcher.end() - matcher.start());
            lastKwEnd = matcher.end();
        }
        spansBuilder.add(Collections.emptyList(), text.length() - lastKwEnd);
        return spansBuilder.create();
    }

    public void handleRunButtonAction(ActionEvent actionEvent) {
        if (getServer() != null) {
            getServer().search(new String[]{"_all"}, queryCodeArea.getText()).searchResponsePropertyProperty().addListener(new ChangeListener<SearchResponse>() {
                @Override
                public void changed(ObservableValue<? extends SearchResponse> observable, SearchResponse oldValue, SearchResponse newValue) {
                    // TODO
                    Platform.runLater(() -> resultCodeArea.replaceText(newValue.toString()));
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
