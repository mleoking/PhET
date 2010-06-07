package edu.colorado.phet.movingmanii;

import edu.colorado.phet.common.motion.model.TimeData;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by IntelliJ IDEA.
 * User: Sam
 * Date: Jun 7, 2010
 * Time: 9:33:59 AM
 * To change this template use File | Settings | File Templates.
 */
public class MovingManDataSeries {
    private ArrayList<TimeData> data = new ArrayList<TimeData>();
    private ArrayList<Listener> listeners = new ArrayList<Listener>();

    public TimeData[] getData() {
        return data.toArray(new TimeData[0]);
    }

    public void addPoint(double value, double time) {
        data.add(new TimeData(value, time));
        notifyDataChanged();
    }

    private void notifyDataChanged() {
        for (Listener listener : listeners) {
            listener.changed();
        }
    }

    public int getNumPoints() {
        return data.size();
    }

    public TimeData getDataPoint(int i) {
        return data.get(i);
    }

    public void setData(TimeData[] point2Ds) {
        data.clear();
        data.addAll(Arrays.asList(point2Ds));
        notifyDataChanged();
    }

    public TimeData[] getPointsInRange(int min, int max) {
        ArrayList<TimeData> points = new ArrayList<TimeData>();
        for (int i = min; i <= max; i++) {
            if (i >= 0 && i < getNumPoints()) {
                points.add(new TimeData(data.get(i).getValue(), data.get(i).getTime()));
            }
        }
        return points.toArray(new TimeData[0]);
    }

    public static interface Listener {
        void changed();
    }

    public void addListener(Listener listener) {
        listeners.add(listener);
    }
}
