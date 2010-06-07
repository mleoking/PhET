package edu.colorado.phet.movingmanii;

import java.awt.geom.Point2D;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: Sam
 * Date: Jun 7, 2010
 * Time: 9:33:59 AM
 * To change this template use File | Settings | File Templates.
 */
public class MovingManDataSeries {
    private ArrayList<Point2D> data = new ArrayList<Point2D>();
    private ArrayList<Listener> listeners = new ArrayList<Listener>();

    public Point2D[] getData() {
        return data.toArray(new Point2D[0]);
    }

    public void addPoint(double x, double y) {
        data.add(new Point2D.Double(x, y));
        for (Listener listener : listeners) {
            listener.changed();
        }
    }

    public int getNumPoints() {
        return data.size();
    }

    public Point2D getDataPoint(int i) {
        return data.get(i);
    }

    public static interface Listener {
        void changed();
    }

    public void addListener(Listener listener) {
        listeners.add(listener);
    }
}
