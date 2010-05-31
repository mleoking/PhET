package edu.colorado.phet.recordandplayback.model;

import edu.colorado.phet.common.phetcommon.math.Function;
import edu.colorado.phet.common.phetcommon.util.SimpleObservable;

import java.util.ArrayList;

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

    private boolean record = true;//True if the sim is in record mode instead of playback mode (may be paused too)
    private boolean paused = true;//True if the current mode is paused 
    private double time = 0.0;//Current time of recording or playback
    private double playbackIndexFloat = 0.0; //floor this to get playbackIndex //todo: remove this and replace with time usages only?
    private double playbackSpeed = 1.0;//The speed at which playback will occur.

    private ArrayList<HistoryClearListener> historyClearListeners = new ArrayList<HistoryClearListener>();
    private ArrayList<HistoryRemainderClearListener> historyRemainderClearListeners = new ArrayList<HistoryRemainderClearListener>();
    private int maxRecordPoints;

    //Extension points for subclasses

    /**
     * Update the simulation model (should cause side effects to update the view), returning a snapshot of the state after the update.
     *
     * @param simulationTimeChange the amount of time to update the simulation (in whatever units the simulation model is using).
     * @return the updated state, which can be used to restore the model during playback
     */
    public abstract T stepRecording(double simulationTimeChange);//what about when you want to update without persisting to a state?

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
     * Sets the model state to reflect the current playback index.
     */
    private void setStateToPlaybackIndex() {
        int playbackIndex = getPlaybackIndex();
        if (playbackIndex >= 0 && playbackIndex < recordHistory.size()) {
            setPlaybackState(recordHistory.get(getPlaybackIndex()).getState());
            time = recordHistory.get(getPlaybackIndex()).getTime();
        }
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
        setPlaybackIndexFloat(0.0);
    }

    public void setTime(double t) {
        time = t;
        //todo: shouldn't there be a notification here?
    }

    public void resetAll() {
        record = true;
        paused = true;
        playbackIndexFloat = 0.0;
        playbackSpeed = 1.0;
        recordHistory.clear();
        time = 0;

        notifyObservers();
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
        if (speed != playbackSpeed) {
            playbackSpeed = speed;
            notifyObservers();
        }
    }

    public double getPlaybackIndexFloat() {
        return playbackIndexFloat;
    }

    public void setRecord(boolean rec) {
        if (record != rec) {
            record = rec;
            if (record) {
                clearHistoryRemainder();
                handleRecordStartedDuringPlayback();
            }

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

    public void stepPlayback() {
        if (getPlaybackIndex() < recordHistory.size()) {
            setStateToPlaybackIndex();
            time = recordHistory.get(getPlaybackIndex()).getTime();
            playbackIndexFloat = playbackIndexFloat + playbackSpeed;
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
        return !record;
    }

    public boolean isRecord() {
        return record;
    }

    public void setPlaybackIndexFloat(double index) {
        playbackIndexFloat = index;
        setStateToPlaybackIndex();
        notifyObservers();
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

    public int getPlaybackIndex() {
        return (int) Math.floor(playbackIndexFloat);
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

    public void setPlaybackTime(double time) {
        Function.LinearFunction f = new Function.LinearFunction(getMinRecordedTime(), getMaxRecordedTime(), 0, recordHistory.size() - 1);
        setPlaybackIndexFloat(f.evaluate(time));
    }

    public double getFloatTime() {
        Function.LinearFunction f = new Function.LinearFunction(0, recordHistory.size() - 1, getMinRecordedTime(), getMaxRecordedTime());
        return f.evaluate(playbackIndexFloat);
    }

    public static interface HistoryClearListener {
        void historyCleared();
    }

    public static interface HistoryRemainderClearListener {
        void historyRemainderCleared();
    }

    public void startRecording() {
        setRecord(true);
        setPaused(false);
    }

    public void stepInTime(double simulationTimeChange) {
        if (!isPaused()) {
            if (isPlayback()) stepPlayback();
            else {
                setTime(getTime() + simulationTimeChange);
                T state = stepRecording(simulationTimeChange);
                //todo: only record the point if we have space
                addRecordedPoint(new DataPoint<T>(getTime(), state));
            }
        }
    }
}