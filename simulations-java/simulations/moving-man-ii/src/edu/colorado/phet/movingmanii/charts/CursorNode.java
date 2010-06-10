package edu.colorado.phet.movingmanii.charts;

import edu.colorado.phet.common.motion.model.TimeData;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.movingmanii.charts.ChartCursor;
import edu.colorado.phet.movingmanii.charts.MovingManChart;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

import java.awt.*;
import java.awt.geom.Rectangle2D;

/**
 * @author Sam Reid
 */
public class CursorNode extends PNode {
    private ChartCursor cursor;
    private MovingManChart chart;
    private PhetPPath path;

    public CursorNode(final ChartCursor cursor, final MovingManChart chart) {
        this.cursor = cursor;
        this.chart = chart;
        cursor.addListener(new ChartCursor.Adapter() {
            public void positionChanged() {
                update();
            }
        });
        cursor.addListener(new ChartCursor.Adapter() {
            public void visibilityChanged() {
                updateVisibility();
            }
        });
        updateVisibility();
        path = new PhetPPath(new Color(50, 50, 200, 45), new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1, new float[]{4, 2}, 0), new Color(20, 20, 120, 100));
        addChild(path);

        update();

        addInputEventListener(new CursorHandler());
        addInputEventListener(new PBasicInputEventHandler() {
            @Override
            public void mouseDragged(PInputEvent event) {
                double dx = event.getCanvasDelta().getWidth();
                double w = chart.viewToModelDeltaX(dx);
                cursor.setTime(cursor.getTime() + w);
            }
        });
    }

    private void updateVisibility() {
        setVisible(cursor.isVisible());
    }

    private void update() {
        double time = cursor.getTime();
        double chartX = chart.modelToView(new TimeData(0, time)).getX();//todo: provide mapValue and mapTime
        double width = 5.0;
        path.setPathTo(new Rectangle2D.Double(chartX - width / 2, 0, width, chart.getViewDimension().getHeight()));
    }
}
