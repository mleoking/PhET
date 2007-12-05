package edu.colorado.phet.common.motion.graphs;

import java.util.ArrayList;

import edu.colorado.phet.common.jfreechartphet.piccolo.JFreeChartCursorNode;
import edu.colorado.phet.common.motion.model.MotionModel;
import edu.colorado.phet.common.motion.model.UpdateStrategy;
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
    private ArrayList listeners = new ArrayList();
    private MotionModel motionModel;
    private JFreeChartCursorNode jFreeChartCursorNode;
    private IUpdateStrategy iPositionDriven;
    private UpdateStrategy updateStrategy;

    public MotionControlGraph( PhetPCanvas pSwingCanvas, final ControlGraphSeries series, String title,
                               double min, double max, final MotionModel motionModel,
                               boolean editable, TimeSeriesModel timeSeriesModel, IUpdateStrategy iPositionDriven ) {
        this( pSwingCanvas, series, title, min, max, motionModel, editable, timeSeriesModel, null, iPositionDriven );
    }

    public MotionControlGraph( PhetPCanvas pSwingCanvas, final ControlGraphSeries series, String title,
                               double min, double max, final MotionModel motionModel,
                               boolean editable, final TimeSeriesModel timeSeriesModel, final UpdateStrategy updateStrategy, IUpdateStrategy iPositionDriven ) {
        this( pSwingCanvas, series, title, min, max, motionModel, editable, timeSeriesModel, updateStrategy, 1000, iPositionDriven );
    }

    public MotionControlGraph( PhetPCanvas pSwingCanvas, final ControlGraphSeries series, String title,
                               double min, double max, final MotionModel motionModel,
                               boolean editable, final TimeSeriesModel timeSeriesModel, final UpdateStrategy updateStrategy, double maxDomainValue, final IUpdateStrategy iPositionDriven ) {
        super( pSwingCanvas, series, title, min, max, timeSeriesModel, maxDomainValue );
        this.iPositionDriven = iPositionDriven;
        this.motionModel = motionModel;
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
                jFreeChartCursorNode.setTime( timeSeriesModel.getTime() );
            }
        } );
        jFreeChartCursorNode.addListener( new JFreeChartCursorNode.Listener() {
            public void cursorTimeChanged() {
                timeSeriesModel.setPlaybackTime( jFreeChartCursorNode.getTime() );
            }
        } );
        motionModel.getTimeSeriesModel().addListener( new TimeSeriesModel.Adapter() {
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
                motionModel.getTimeSeriesModel().setPlaybackMode();
                motionModel.getTimeSeriesModel().setPlaybackTime( jFreeChartCursorNode.getTime() );
//                System.out.println( "playback time=" + jFreeChartCursorNode.getTime() );
            }
        } );
        motionModel.getTimeSeriesModel().addListener( new TimeSeriesModel.Adapter() {
            public void dataSeriesChanged() {
                jFreeChartCursorNode.setMaxDragTime( motionModel.getTimeSeriesModel().getRecordTime() );
//                System.out.println( "max record time=" + motionModel.getTimeSeriesModel().getRecordTime() );
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

    protected void handleControlFocusGrabbed() {
        super.handleControlFocusGrabbed();
        if ( updateStrategy != null ) {
            iPositionDriven.setUpdateStrategy( updateStrategy );
        }
    }

    private void updateCursorLocation() {
        jFreeChartCursorNode.setTime( motionModel.getTimeSeriesModel().getTime() );
    }

    private void updateCursorVisible() {
        jFreeChartCursorNode.setVisible( motionModel.getTimeSeriesModel().isPlaybackMode() || motionModel.getTimeSeriesModel().isPaused() );
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
        for ( int i = 0; i < listeners.size(); i++ ) {
            ( (Listener) listeners.get( i ) ).horizontalZoomChanged( this );
        }
    }

}
