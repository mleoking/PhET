package edu.colorado.phet.movingmanii.model;

import edu.colorado.phet.common.motion.model.TimeData;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author Sam Reid
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
        int size = getNumPoints();//moved here for performance improvements, was taking 15% of application in inner loop
        for (int i = min; i <= max; i++) {
            if (i >= 0 && i < size) {
                points.add(data.get(i));
            }
        }
        return points.toArray(new TimeData[points.size()]);
    }

    public void clear() {
        data.clear();
        notifyDataChanged();
    }

    public static interface Listener {
        void changed();
    }

    public void addListener(Listener listener) {
        listeners.add(listener);
    }
}
