package edu.colorado.phet.common.motion.graphs;

import edu.colorado.phet.common.jfreechartphet.piccolo.JFreeChartCursorNode;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.ZoomControlNode;
import edu.colorado.phet.common.timeseries.model.TimeSeriesModel;
import edu.colorado.phet.common.motion.model.MotionModel;
import edu.colorado.phet.common.motion.model.SimulationVariable;
import edu.umd.cs.piccolo.PNode;

import java.awt.*;
import java.util.ArrayList;

/**
 * This subclass of ControlGraph is automatically connected to the supplied MotionModel for update/notification messaging.
 *
 * @author Sam Reid
 */
public class MotionControlGraph extends ControlGraph {
    private ArrayList listeners = new ArrayList();

    public MotionControlGraph( PhetPCanvas pSwingCanvas, SimulationVariable simulationVariable, String label, String title,
                               double min, double max, Color color, PNode thumb, final MotionModel motionModel, boolean editable, final CursorModel cursorModel ) {
        super( pSwingCanvas, simulationVariable, label, title, min, max, color, thumb );
        addHorizontalZoomListener( new ZoomControlNode.ZoomListener() {
            public void zoomedOut() {
                notifyZoomChanged();
            }

            public void zoomedIn() {
                notifyZoomChanged();
            }
        } );
        setEditable( editable );

        final JFreeChartCursorNode jFreeChartCursorNode = new JFreeChartCursorNode( getJFreeChartNode() );
        addChild( jFreeChartCursorNode );
        cursorModel.addListener( new CursorModel.Listener() {
            public void changed() {
                jFreeChartCursorNode.setTime( cursorModel.getTime() );
            }
        } );
        jFreeChartCursorNode.addListener( new JFreeChartCursorNode.Listener() {
            public void cursorTimeChanged() {
                cursorModel.setTime( jFreeChartCursorNode.getTime() );
            }
        } );
        motionModel.getTimeSeriesModel().addListener( new TimeSeriesModel.Adapter() {
            public void modeChanged() {
                updateCursorVisible( jFreeChartCursorNode, motionModel );
            }

            public void pauseChanged() {
                updateCursorLocation( jFreeChartCursorNode, motionModel );
                updateCursorVisible( jFreeChartCursorNode, motionModel );
            }
        } );
//        rotationModel.getTimeSeriesModel().addPlaybackTimeChangeListener( );
        updateCursorVisible( jFreeChartCursorNode, motionModel );
        jFreeChartCursorNode.addListener( new JFreeChartCursorNode.Listener() {
            public void cursorTimeChanged() {
                motionModel.getTimeSeriesModel().setPlaybackMode();
                motionModel.getTimeSeriesModel().setPlaybackTime( jFreeChartCursorNode.getTime() );
            }
        } );
        motionModel.getTimeSeriesModel().addListener( new TimeSeriesModel.Adapter() {
            public void dataSeriesChanged() {
                jFreeChartCursorNode.setMaxDragTime( motionModel.getTimeSeriesModel().getRecordTime() );
            }
        } );
    }

    private void updateCursorLocation( JFreeChartCursorNode jFreeChartCursorNode, MotionModel rotationModel ) {
        jFreeChartCursorNode.setTime( rotationModel.getTimeSeriesModel().getTime() );
    }

    private void updateCursorVisible( JFreeChartCursorNode jFreeChartCursorNode, MotionModel rotationModel ) {
        jFreeChartCursorNode.setVisible( rotationModel.getTimeSeriesModel().isPlaybackMode() || rotationModel.getTimeSeriesModel().isPaused() );
    }


    public static interface Listener {
        void horizontalZoomChanged();
    }

    public void addListener( Listener listener ) {
        listeners.add( listener );
    }

    public void removeListener( Listener listener ) {
        listeners.remove( listener );
    }

    public void notifyZoomChanged() {
        for( int i = 0; i < listeners.size(); i++ ) {
            ( (Listener)listeners.get( i ) ).horizontalZoomChanged();
        }
    }

}
