package net.mewk.fx.control.codearea;

import javafx.scene.Node;
import javafx.scene.control.Label;
import org.reactfx.collection.LiveList;
import org.reactfx.value.Val;

import java.util.function.IntFunction;

public class LineNumberFactory implements IntFunction<Node> {

    private final Val<Integer> paragraphs;

    public LineNumberFactory(CodeArea codeArea) {
        this.paragraphs = LiveList.sizeOf(codeArea.getParagraphs());
    }

    @Override
    public Node apply(int index) {
        Label lineNumber = new Label();
        lineNumber.getStyleClass().add("line-number");

        // When removed from the scene, bind label's text to constant "",
        // which is a fake binding that consumes no resources, instead of
        // staying bound to area's paragraphs.
        lineNumber.textProperty().bind(Val.flatMap(
                lineNumber.sceneProperty(),
                scene -> scene != null
                        ? paragraphs.map(n -> format(index + 1, n))
                        : Val.constant("")));

        return lineNumber;
    }

    private String format(int x, int max) {
        int digits = (int) Math.floor(Math.log10(max)) + 1;
        return String.format("%" + digits + "d", x);
    }
}
