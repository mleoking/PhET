package edu.colorado.phet.common.motion.graphs;

import java.util.ArrayList;

import org.jfree.chart.JFreeChart;

import edu.colorado.phet.common.jfreechartphet.piccolo.JFreeChartCursorNode;
import edu.colorado.phet.common.motion.model.UpdateStrategy;
import edu.colorado.phet.common.motion.model.UpdateableObject;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.ZoomControlNode;
import edu.colorado.phet.common.timeseries.model.TimeSeriesModel;

/**
 * This subclass of ControlGraph is automatically connected to the supplied MotionModel
 * for update/notification messaging.
 *
 * @author Sam Reid
 */
public class MotionControlGraph extends ControlGraph {
    private ArrayList<Listener> listeners = new ArrayList<Listener>();
    private JFreeChartCursorNode jFreeChartCursorNode;
    private UpdateableObject updateableObject;
    private UpdateStrategy updateStrategy;
    private TimeSeriesModel timeSeriesModel;

    public MotionControlGraph( PhetPCanvas pSwingCanvas, final ControlGraphSeries series, String label, String title,
                               double min, double max, boolean editable, TimeSeriesModel timeSeriesModel, UpdateableObject updateableObject ) {
        this( pSwingCanvas, series, label, title, min, max, editable, timeSeriesModel, null, updateableObject );
    }

    public MotionControlGraph( PhetPCanvas pSwingCanvas, final ControlGraphSeries series, String label, String title,
                               double min, double max, boolean editable, final TimeSeriesModel timeSeriesModel, final UpdateStrategy updateStrategy, UpdateableObject updateableObject ) {
        this( pSwingCanvas, series, label, title, min, max, editable, timeSeriesModel, updateStrategy, 20, updateableObject );
    }

    public MotionControlGraph( PhetPCanvas pSwingCanvas, final ControlGraphSeries series, String label, String title,
                               double min, double max, boolean editable, final TimeSeriesModel timeSeriesModel, final UpdateStrategy updateStrategy, double maxDomainValue, final UpdateableObject updateableObject ) {
        this( ControlGraph.createDefaultChart( title ), pSwingCanvas, series, label, title, min, max, editable, timeSeriesModel, updateStrategy, maxDomainValue, updateableObject );
    }

    public MotionControlGraph( JFreeChart chart, PhetPCanvas pSwingCanvas, final ControlGraphSeries series, String label, String title,
                               double min, double max, boolean editable, final TimeSeriesModel timeSeriesModel, final UpdateStrategy updateStrategy, double maxDomainValue, final UpdateableObject updateableObject ) {
        super( chart, pSwingCanvas, series, min, max, timeSeriesModel, maxDomainValue );
        this.updateableObject = updateableObject;
        this.timeSeriesModel = timeSeriesModel;
        this.updateStrategy = updateStrategy;
        addHorizontalZoomListener( new ZoomControlNode.ZoomListener() {
            public void zoomedOut() {
                notifyZoomChanged();
            }

            public void zoomedIn() {
                notifyZoomChanged();
            }
        } );
        setEditable( editable );

        jFreeChartCursorNode = new JFreeChartCursorNode( getJFreeChartNode() );
        addChild( jFreeChartCursorNode );
        timeSeriesModel.addPlaybackTimeChangeListener( new TimeSeriesModel.PlaybackTimeListener() {
            public void timeChanged() {
                updateCursorLocation();
            }
        } );
        jFreeChartCursorNode.addListener( new JFreeChartCursorNode.Listener() {
            public void cursorTimeChanged() {
                timeSeriesModel.setPlaybackTime( jFreeChartCursorNode.getTime() );
            }
        } );
        timeSeriesModel.addListener( new TimeSeriesModel.Adapter() {
            public void modeChanged() {
                updateCursorVisible();
            }

            public void pauseChanged() {
                updateCursorLocation();
                updateCursorVisible();
            }
        } );
        jFreeChartCursorNode.addListener( new JFreeChartCursorNode.Listener() {
            public void cursorTimeChanged() {
                timeSeriesModel.setPlaybackMode();
                timeSeriesModel.setPlaybackTime( jFreeChartCursorNode.getTime() );
//                System.out.println( "playback time=" + jFreeChartCursorNode.getTime() );
            }
        } );
        timeSeriesModel.addListener( new TimeSeriesModel.Adapter() {
            public void dataSeriesChanged() {
                updateCursorMaxDragTime();
//                System.out.println( "max record time=" + timeSeriesModel.getRecordTime() );
            }

            public void dataSeriesCleared() {
                clear();
                getDynamicJFreeChartNode().forceUpdateAll();
            }
        } );
        updateCursorVisible();

        addListener( new Adapter() {
            public void controlFocusGrabbed() {
                handleControlFocusGrabbed();
            }
        } );
    }

    protected void updateCursorMaxDragTime() {
        jFreeChartCursorNode.setMaxDragTime( getMaxCursorDragTime() );
    }

    protected double getMaxCursorDragTime() {
        return timeSeriesModel.getRecordTime();
    }

    protected void handleControlFocusGrabbed() {
        super.handleControlFocusGrabbed();
        if ( updateStrategy != null ) {
            updateableObject.setUpdateStrategy( updateStrategy );
        }
    }

    protected void updateCursorLocation() {
        jFreeChartCursorNode.setTime( getPlaybackTime() );
    }

    protected double getPlaybackTime() {
        return timeSeriesModel.getTime();
    }

    protected void updateCursorVisible() {
        jFreeChartCursorNode.setVisible( getCursorShouldBeVisible() );
    }

    protected boolean getCursorShouldBeVisible() {
        return timeSeriesModel.isPlaybackMode() || timeSeriesModel.isPaused();
    }

    public boolean hasListener( Listener listener ) {
        return listeners.contains( listener );
    }

    public static interface Listener {
        void horizontalZoomChanged( MotionControlGraph source );
    }

    public void addListener( Listener listener ) {
        listeners.add( listener );
    }

    public void removeListener( Listener listener ) {
        listeners.remove( listener );
    }

    public void notifyZoomChanged() {
        super.notifyZoomChanged();
        if ( listeners != null ) {
            for ( Listener listener : listeners ) {
                listener.horizontalZoomChanged( this );
            }
        }
    }

}
