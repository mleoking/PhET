package edu.colorado.phet.movingman.view;

import edu.colorado.phet.common.motion.charts.MotionSliderNode;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.motion.charts.TemporalChart;
import edu.colorado.phet.common.motion.charts.TextBox;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;

import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * This is the panel to the left side of a chart with readouts and controls pertaining to the chart variables.
 *
 * @author Sam Reid
 */
public class MovingManChartControl extends PNode {
    public MovingManChartControl(String title, Color color, TextBoxListener textBoxDecorator, final TemporalChart chart, String units) {
        PText titleNode = new PText(title);
        titleNode.setFont(new PhetFont(12, true));
        titleNode.setTextPaint(color);
        this.addChild(titleNode);

        final TextBox textBox = new TextBox();
        textBoxDecorator.addListeners(textBox);
        textBox.setOffset(15, titleNode.getFullBounds().getHeight());
        this.addChild(textBox);

        PText unitsReadout = new PText(units);
        unitsReadout.setFont(textBox.getFont());
        unitsReadout.setTextPaint(color);
        unitsReadout.setOffset(textBox.getFullBounds().getMaxX() + 2, textBox.getFullBounds().getCenterY() - unitsReadout.getFullBounds().getHeight() / 2);
        this.addChild(unitsReadout);
        SimpleObserver simpleObserver = new SimpleObserver() {
            public void update() {
                setVisible(chart.getMaximized().getValue());
            }
        };
        simpleObserver.update();
        chart.getMaximized().addObserver(simpleObserver);
    }
}
