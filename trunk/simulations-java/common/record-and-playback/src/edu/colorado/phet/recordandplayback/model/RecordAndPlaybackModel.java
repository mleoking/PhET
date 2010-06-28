package edu.colorado.phet.recordandplayback.model;

import edu.colorado.phet.common.phetcommon.util.SimpleObservable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * This is the main model class for sims that support recording and playing back.  This is done by recording discrete states,
 * then being able to set re-apply them to the model.  This library does not currently provide support for interpolation between states.
 * <p/>
 * This mixture of side-effects and state capturing seems to simplify graphics updating of normal model updating,
 * though it can create additional complexity during playback.
 *
 * @author Sam Reid
 * @param <T> the type of state that is recorded and restored, should be immutable.
 */
public abstract class RecordAndPlaybackModel<T> extends SimpleObservable {

    /**
     * Behavior modes that were decided upon after testing
     */
    public static final boolean pauseAtEndOfPlayback = true;
    public static final boolean recordAtEndOfPlayback = false;

    //The history of data points that have been recorded from the model.
    private final ArrayList<DataPoint<T>> recordHistory = new ArrayList<DataPoint<T>>();

    //See resetAll() for default values
    private Mode mode; // the current mode, one of playback, record or live
    private Record recordMode = new Record(); //samples data from the mode and stores it
    private Playback playbackMode = new Playback(); //plays back recorded data
    private Live liveMode = new Live(); //runs the model without recording it

    private boolean paused;//True if the current mode is paused
    private double time;//Current time of recording or playback

    private ArrayList<HistoryClearListener> historyClearListeners = new ArrayList<HistoryClearListener>();
    private ArrayList<HistoryRemainderClearListener> historyRemainderClearListeners = new ArrayList<HistoryRemainderClearListener>();
    private int maxRecordPoints;

    //Extension points for subclasses

    /**
     * Update the simulation model (should cause side effects to update the view), returning a snapshot of the state after the update.
     * The returned state could be ignored if the simulation is not in record mode.
     *
     * @param simulationTimeChange the amount of time to update the simulation (in whatever units the simulation model is using).
     * @return the updated state, which can be used to restore the model during playback
     */
    public abstract T step(double simulationTimeChange);

    /**
     * This method should popuplate the model + view of the application with the data from the specified state.
     * This state was obtained through playing back or stepping the recorded history.
     *
     * @param state the state to display
     */
    public abstract void setPlaybackState(T state);

    //todo: i'm considering this interface for recording and playback:
    //interface UpdateRule{T stepModel(T, double dt)}
    //interface Model{setState(T)}

    protected RecordAndPlaybackModel(int maxRecordPoints) {
        this.maxRecordPoints = maxRecordPoints;
        resetAll();
    }

    public int getMaxRecordPoints() {
        return maxRecordPoints;
    }

    /**
     * Empty function handle, which can be overriden to provide custom functionality when record was pressed
     * during playback.  This is useful since many sims have other data (or charts) that must be cleared when
     * record is pressed during playback.
     */
    protected void handleRecordStartedDuringPlayback() {
    }

    /**
     * Look up a recorded state based on the specified time
     */
    private DataPoint<T> getPlaybackState() {
        //todo: binary search?  Or use better heuristics, such as assuming that points are equally spaced?
        ArrayList<DataPoint<T>> sorted = new ArrayList<DataPoint<T>>(recordHistory);
        Collections.sort(sorted, new Comparator<DataPoint<T>>() {
            public int compare(DataPoint<T> o1, DataPoint<T> o2) {
                return Double.compare(Math.abs(o1.getTime() - time), Math.abs(o2.getTime() - time));//todo: this is horribly inefficient, but hasn't caused noticeable slowdown during testing
            }
        });
        return sorted.get(0);
    }

    /**
     * Switches to playback mode with the specified playback speed.
     *
     * @param speed the speed to use for playback, 1.0 = normal speed
     */
    public void setPlayback(double speed) {
        setPlaybackSpeed(speed);
        setRecord(false);
    }

    public void rewind() {
        setTime(getMinRecordedTime());
    }

    public void setTime(double t) {
        time = t;
        if (isPlayback() && getNumRecordedPoints() > 0) {//Only restore state if during playback and state has been recorded
            setPlaybackState(getPlaybackState().getState()); //Sets the model state to reflect the current playback index.
        }
        notifyObservers();
    }

    public void resetAll() {
        setPlaybackSpeed(1.0);
        clearHistory();
        setTime(0.0);
        setRecord(true);
        setPaused(true);
    }

    public void addRecordedPoint(DataPoint<T> point) {
        recordHistory.add(point);
        notifyObservers();
    }

    public void removeHistoryPoint(int point) {
        recordHistory.remove(point);
        notifyObservers();
    }

    /**
     * Returns a defensive copy of the recorded history points.
     *
     * @return a list of the recorded states
     */
    public ArrayList<DataPoint<T>> getRecordingHistory() {
        ArrayList<DataPoint<T>> data = new ArrayList<DataPoint<T>>();
        data.addAll(recordHistory);
        return data;
    }

    public void setPlaybackSpeed(double speed) {
        if (speed != playbackMode.getSpeed()) {
            playbackMode.setSpeed(speed);
            notifyObservers();
        }
    }

    /**
     * @param rec
     * @deprecated, use setmode
     */
    public void setRecord(boolean rec) {
        if (rec && mode != recordMode) {
            mode = recordMode;
            clearHistoryRemainder();
            handleRecordStartedDuringPlayback();
            notifyObservers();
        } else if (!rec && mode != playbackMode) {
            mode = playbackMode;
            notifyObservers();
        }
    }

    public int getNumRecordedPoints() {
        return recordHistory.size();
    }

    public void clearHistoryRemainder() {
        ArrayList<DataPoint<T>> keep = new ArrayList<DataPoint<T>>();
        for (DataPoint<T> dataPoint : recordHistory) {
            if (dataPoint.getTime() < time) {
                keep.add(dataPoint);
            }
        }
        recordHistory.clear();
        recordHistory.addAll(keep);
        notifyObservers();
        for (HistoryRemainderClearListener historyRemainderClearListener : historyRemainderClearListeners) {
            historyRemainderClearListener.historyRemainderCleared();
        }
    }

    public void addHistoryRemainderClearListener(HistoryRemainderClearListener historyRemainderClearListener) {
        historyRemainderClearListeners.add(historyRemainderClearListener);
    }

    //Estimates what DT should be by the spacing of the data points.
    //This should provide some support for non-equal spaced samples, but other algorithms may be better

    private double getPlaybackDT() {
        if (getNumRecordedPoints() == 0) return 0;
        else if (getNumRecordedPoints() == 1) return recordHistory.get(0).getTime();
        else
            return (recordHistory.get(recordHistory.size() - 1).getTime() - recordHistory.get(0).getTime()) / recordHistory.size();
    }

    public void addHistoryClearListener(HistoryClearListener listener) {
        historyClearListeners.add(listener);
    }

    public void clearHistory() {
        recordHistory.clear();
        setTime(0.0);//For some reason, time has to be reset to 0.0 here, or charts don't clear in motion-series on first press of clear button
        notifyObservers();
        for (HistoryClearListener historyClearListener : historyClearListeners) {
            historyClearListener.historyCleared();
        }
    }

    public boolean isPlayback() {
        return mode == playbackMode;
    }

    public boolean isRecord() {
        return mode == recordMode;
    }

    public boolean isLive() {
        return mode == liveMode;
    }

    public void setPaused(boolean p) {
        if (paused != p) {
            paused = p;
            notifyObservers();
        }
    }

    public boolean isPaused() {
        return paused;
    }

    public boolean isRecordingFull() {
        return recordHistory.size() >= getMaxRecordPoints();
    }

    public double getRecordedTimeRange() {
        if (recordHistory.size() == 0) {
            return 0;
        } else {
            return recordHistory.get(recordHistory.size() - 1).getTime() - recordHistory.get(0).getTime();
        }
    }

    public double getTime() {
        return time;
    }

    public double getMaxRecordedTime() {
        if (recordHistory.size() == 0) return 0.0;
        else return recordHistory.get(recordHistory.size() - 1).getTime();
    }

    public double getMinRecordedTime() {
        if (recordHistory.size() == 0) return 0.0;
        else return recordHistory.get(0).getTime();
    }

    public double getPlaybackSpeed() {
        return playbackMode.getSpeed();
    }

    public static interface HistoryClearListener {
        void historyCleared();
    }

    public static interface HistoryRemainderClearListener {
        void historyRemainderCleared();
    }

    public void startRecording() {
        setModeRecord();
        setPaused(false);
    }

    public void stepInTime(double simulationTimeChange) {
        if (!isPaused()) {
            mode.step(simulationTimeChange);
        }
    }

    public void step() {
        mode.step(getPlaybackDT());
    }

    protected void setMode(Mode mode) {
        this.mode = mode;
        notifyObservers();
    }
    
    public void setModeLive(){
    	setMode(liveMode);
    }

    public void setModeRecord(){
    	setMode(recordMode);
    }

    public void setModePlayback(){
    	setMode(playbackMode);
    }

    public static interface Mode {
        void step(double simulationTimeChange);
    }

    public class Record implements Mode {
        public void step(double simulationTimeChange) {
            setTime(getTime() + simulationTimeChange);
            T state = RecordAndPlaybackModel.this.step(simulationTimeChange);
            //todo: only record the point if we have space
            addRecordedPoint(new DataPoint<T>(getTime(), state));
        }
        
        @Override
    	public String toString() {
    		return "Record";
    	}
    }

    public class Playback implements Mode {
        private double speed;//1 is full speed; i.e. the time between the original samples

        public void setSpeed(double speed) {
            this.speed = speed;
        }

        public double getSpeed() {
            return speed;
        }

        public void step(double simulationTimeChange) {
        	if (speed > 0){
        		// Playing forwards.
        		if (getTime() < getMaxRecordedTime()) {
        			setTime(time + speed * getPlaybackDT());
        			notifyObservers();
        		} else {
        			if (RecordAndPlaybackModel.recordAtEndOfPlayback) {
        				setRecord(true);
        			}
        			if (RecordAndPlaybackModel.pauseAtEndOfPlayback) {
        				setPaused(true);
        			}
        		}
        	}
        	else if (speed < 0){
        		// Playing backwards.
        		if (getTime() > getMinRecordedTime()) {
        			setTime(time + speed * getPlaybackDT());
        			notifyObservers();
        		}
        	}
        }
    @Override
	public String toString() {
		return "Playback";
	}
}

    public class Live implements Mode {
        public void step(double simulationTimeChange) {
            setTime(getTime() + simulationTimeChange);
            RecordAndPlaybackModel.this.step(simulationTimeChange);
        }

		@Override
		public String toString() {
			return "Live";
		}
    }
}