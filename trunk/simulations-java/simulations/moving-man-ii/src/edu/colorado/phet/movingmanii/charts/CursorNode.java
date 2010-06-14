package edu.colorado.phet.movingmanii.charts;

import edu.colorado.phet.common.motion.model.TimeData;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
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
        path = new HighQualityPhetPPath(new Color(50, 50, 200, 83), new BasicStroke(1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1, new float[]{5, 2}, 0), new Color(20, 20, 30, 160));
        addChild(path);

        final ChartCursor.Adapter pathUpdater = new ChartCursor.Adapter() {
            public void positionChanged() {
                double time = cursor.getTime();
                double chartX = chart.modelToView(new TimeData(0, time)).getX();//todo: provide mapValue and mapTime
                double width = 6.0;
                path.setPathTo(new Rectangle2D.Double(chartX - width / 2, 0, width, chart.getViewDimension().getHeight()));
            }
        };
        pathUpdater.positionChanged();
        cursor.addListener(pathUpdater);
        final ChartCursor.Adapter visibilityUpdater = new ChartCursor.Adapter() {
            public void visibilityChanged() {
                setVisible(cursor.isVisible());
            }
        };
        visibilityUpdater.visibilityChanged();
        cursor.addListener(visibilityUpdater);
        addInputEventListener(new CursorHandler());
        addInputEventListener(new PBasicInputEventHandler() {
            double relativeGrabPoint = Double.NaN;

            public void mousePressed(PInputEvent event) {
                super.mousePressed(event);
                updateRelativeGrabPoint(event);
            }

            public void mouseDragged(PInputEvent event) {
                super.mouseDragged(event);
                if (Double.isNaN(relativeGrabPoint)) {
                    updateRelativeGrabPoint(event);
                }
                cursor.setTime(getModelPoint(event) - relativeGrabPoint);
            }

            public void mouseReleased(PInputEvent event) {
                super.mouseReleased(event);
                relativeGrabPoint = Double.NaN;
            }

            private void updateRelativeGrabPoint(PInputEvent event) {
                relativeGrabPoint = getModelPoint(event) - cursor.getTime();
            }

            private double getModelPoint(PInputEvent event) {
                return chart.viewToModel(event.getPositionRelativeTo(chart).getX());
            }
        });
    }
}