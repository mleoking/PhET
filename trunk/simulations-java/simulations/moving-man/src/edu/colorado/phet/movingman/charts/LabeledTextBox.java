package edu.colorado.phet.movingman.charts;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.umd.cs.piccolo.nodes.PText;

import java.awt.*;

/**
 * This component allows the user to enter data for a series, and it reads out values for series.
 *
 * @author Sam Reid
 */
public class LabeledTextBox extends TextBox {
    public LabeledTextBox(String title, Color color) {
        PText titleNode = new PText(title);
        titleNode.setFont(new PhetFont(12, true));
        titleNode.setTextPaint(color);
        addChild(titleNode);
        textField.setOffset(titleNode.getFullBounds().getX(), titleNode.getFullBounds().getMaxY());
    }
}