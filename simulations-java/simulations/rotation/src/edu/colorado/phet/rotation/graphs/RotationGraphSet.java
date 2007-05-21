package edu.colorado.phet.rotation.graphs;

import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.ZoomControlNode;
import edu.colorado.phet.common.timeseries.model.TimeSeriesModel;
import edu.colorado.phet.rotation.RotationResources;
import edu.colorado.phet.rotation.model.RotationModel;
import edu.colorado.phet.rotation.model.SimulationVariable;
import edu.colorado.phet.rotation.util.UnicodeUtil;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * User: Sam Reid
 * Date: Dec 28, 2006
 * Time: 8:23:39 AM
 */

public class RotationGraphSet {
    private GraphComponent angleGraph;
    private GraphComponent angularVelocityGraph;
    private GraphComponent angularAccelerationGraph;
    private GraphComponent positionGraph;
    private GraphComponent speedGraph;
    private GraphComponent accelerationGraph;
    private ArrayList graphComponents = new ArrayList();
    private GraphSuite[] suites;
    private CursorModel cursorModel;

    public RotationGraphSet( PhetPCanvas pSwingCanvas, final RotationModel rotationModel ) {
        this( pSwingCanvas, rotationModel, new CursorModel( rotationModel.getTimeSeriesModel() ) );
    }

    public RotationGraphSet( PhetPCanvas pSwingCanvas, final RotationModel rotationModel, CursorModel cursorModel ) {
        this.cursorModel = cursorModel;
        angleGraph = new GraphComponent( UnicodeUtil.THETA, toControlGraph( pSwingCanvas, UnicodeUtil.THETA, "Angular Position", -Math.PI * 3, Math.PI * 3, Color.blue, rotationModel.getXVariable(), new PImage( loadImage( "blue-arrow.png" ) ), true, cursorModel, rotationModel ) );
        angularVelocityGraph = new GraphComponent( UnicodeUtil.OMEGA, toControlGraph( pSwingCanvas, UnicodeUtil.OMEGA, "Angular Velocity", -0.1, 0.1, Color.red, rotationModel.getVVariable(), new PImage( loadImage( "red-arrow.png" ) ), true, cursorModel, rotationModel ) );
        angularAccelerationGraph = new GraphComponent( UnicodeUtil.ALPHA, toControlGraph( pSwingCanvas, UnicodeUtil.ALPHA, "Angular Acceleration", -0.001, 0.001, Color.green, rotationModel.getAVariable(), new PImage( loadImage( "green-arrow.png" ) ), true, cursorModel, rotationModel ) );

//        ControlGraph positionControlGraph = toControlGraph( pSwingCanvas, "x", "Position", -1.2, 1.2, Color.blue, rotationModel.getXPositionVariable(), new PImage( loadImage( "blue-arrow.png" ) ), false );
        ControlGraph positionControlGraph = toControlGraph( pSwingCanvas, "x", "Position", 0, 500, Color.blue, rotationModel.getXPositionVariable(), new PImage( loadImage( "blue-arrow.png" ) ), false, cursorModel, rotationModel );
        positionControlGraph.addSeries( "Position", Color.red, "y", rotationModel.getYPositionVariable() );

        positionGraph = new GraphComponent( "x,y", positionControlGraph );
        ControlGraph speedControlGraph = toControlGraph( pSwingCanvas, "|v|", "Linear Speed", 0, 0.1, Color.red, rotationModel.getSpeedVariable(), new PImage( loadImage( "red-arrow.png" ) ), false, cursorModel, rotationModel );
        speedGraph = new GraphComponent( "vx, vy", speedControlGraph );

        accelerationGraph = new GraphComponent( "a", toControlGraph( pSwingCanvas, "a", "Centripetal Acceleration", 0, 0.001, Color.green, rotationModel.getCentripetalAcceleration(), new PImage( loadImage( "green-arrow.png" ) ), false, cursorModel, rotationModel ) );
        graphComponents.addAll( Arrays.asList( new GraphComponent[]{angleGraph, angularVelocityGraph, angularAccelerationGraph, positionGraph, speedGraph, accelerationGraph} ) );
        suites = new GraphSuite[]{
//                new GraphSuite( new GraphComponent[]{getAngleGraph(), getAngularVelocityGraph(), getPositionGraph()} ),//todo: remove after testing
                new GraphSuite( new GraphComponent[]{getAngleGraph(), getAngularVelocityGraph(), getPositionGraph(), getAngularAccelerationGraph()} ),
                new GraphSuite( new GraphComponent[]{getAngleGraph(), getAngularVelocityGraph(), getAngularAccelerationGraph()} ),
                new GraphSuite( new GraphComponent[]{getAngleGraph(), getAngularVelocityGraph(), getSpeedGraph()} ),
                new GraphSuite( new GraphComponent[]{getAngleGraph(), getAngularVelocityGraph(), getAccelerationGraph()} ),
                new GraphSuite( new GraphComponent[]{getAngleGraph(), getAngularVelocityGraph(), getAngularAccelerationGraph(), positionGraph, speedGraph, accelerationGraph} )
        };

        rotationModel.addListener( new RotationModel.Listener() {
            public void steppedInTime() {
                angleGraph.addValue( rotationModel.getTime(), rotationModel.getAngle() );
                angularVelocityGraph.addValue( rotationModel.getTime(), rotationModel.getAngularVelocity() );
                angularAccelerationGraph.addValue( rotationModel.getTime(), rotationModel.getAngularAcceleration() );

                positionGraph.addValue( 0, rotationModel.getTime(), rotationModel.getRotationBody( 0 ).getX() );
                positionGraph.addValue( 1, rotationModel.getTime(), rotationModel.getRotationBody( 0 ).getY() );
                speedGraph.addValue( rotationModel.getTime(), rotationModel.getRotationBody( 0 ).getVelocity().getMagnitude() );
                accelerationGraph.addValue( rotationModel.getTime(), rotationModel.getRotationBody( 0 ).getAcceleration().getMagnitude() );
            }
        } );
        angleGraph.addControlGraphListener( new ControlGraph.Adapter() {
            public void controlFocusGrabbed() {
                rotationModel.setPositionDriven();
            }
        } );
        angularVelocityGraph.addControlGraphListener( new ControlGraph.Adapter() {
            public void controlFocusGrabbed() {
                rotationModel.setVelocityDriven();
            }
        } );
        angularAccelerationGraph.addControlGraphListener( new ControlGraph.Adapter() {
            public void controlFocusGrabbed() {
                rotationModel.setAccelerationDriven();
            }
        } );
    }

    private Image loadImage( String s ) {
        try {
            return RotationResources.loadBufferedImage( s );
        }
        catch( IOException e ) {
            e.printStackTrace();
            throw new RuntimeException( e );
        }
    }

    ControlGraph toControlGraph( PhetPCanvas pSwingCanvas, String label, String title,
                                 double min, double max, Color color,
                                 SimulationVariable simulationVariable, PNode thumb, boolean editable, final CursorModel cursorModel,
                                 final RotationModel rotationModel ) {
        final ControlGraph controlGraph = new ControlGraph( pSwingCanvas, simulationVariable, label, title, min, max, color, thumb );

        //todo: move this adapter code into ControlGraph (or a subclass)
        controlGraph.addHorizontalZoomListener( new ZoomControlNode.ZoomListener() {
            public void zoomedOut() {
                horizontalZoomChanged( controlGraph.getMaxDataX() );
            }

            public void zoomedIn() {
                horizontalZoomChanged( controlGraph.getMaxDataX() );
            }
        } );
        controlGraph.setEditable( editable );

        final JFreeChartCursorNode jFreeChartCursorNode = new JFreeChartCursorNode( controlGraph.getJFreeChartNode() );
        controlGraph.addChild( jFreeChartCursorNode );
        cursorModel.addListener( new CursorModel.Listener() {
            public void changed() {
                jFreeChartCursorNode.setTime( cursorModel.getTime() );
            }
        } );
        jFreeChartCursorNode.addListener( new JFreeChartCursorNode.Listener() {
            public void changed() {
                cursorModel.setTime( jFreeChartCursorNode.getTime() );
            }
        } );
        rotationModel.getTimeSeriesModel().addListener( new TimeSeriesModel.Adapter() {
            public void modeChanged() {
                updateCursorVisible( jFreeChartCursorNode, rotationModel );
            }

            public void pauseChanged() {
                updateCursorLocation( jFreeChartCursorNode, rotationModel );
                updateCursorVisible( jFreeChartCursorNode, rotationModel );
            }
        } );
        updateCursorVisible( jFreeChartCursorNode, rotationModel );
        jFreeChartCursorNode.addListener( new JFreeChartCursorNode.Listener() {
            public void changed() {
                rotationModel.getTimeSeriesModel().setPlaybackMode();
                rotationModel.getTimeSeriesModel().setPlaybackTime( jFreeChartCursorNode.getTime() );
            }
        } );
        rotationModel.getTimeSeriesModel().addListener( new TimeSeriesModel.Adapter() {
            public void dataSeriesChanged() {
                jFreeChartCursorNode.setMaxDragTime( rotationModel.getTimeSeriesModel().getRecordTime() );
            }
        });
        return controlGraph;
    }

    private void updateCursorLocation( JFreeChartCursorNode jFreeChartCursorNode, RotationModel rotationModel ) {
        jFreeChartCursorNode.setTime( rotationModel.getTimeSeriesModel().getTime() );
    }

    private void updateCursorVisible( JFreeChartCursorNode jFreeChartCursorNode, RotationModel rotationModel ) {
        jFreeChartCursorNode.setVisible( rotationModel.getTimeSeriesModel().isPlaybackMode() || rotationModel.getTimeSeriesModel().isPaused() );
    }

    private void horizontalZoomChanged( double maxDataX ) {
        for( int i = 0; i < graphComponents.size(); i++ ) {
            GraphComponent graphComponent = (GraphComponent)graphComponents.get( i );
            graphComponent.getControlGraph().setDomainUpperBound( maxDataX );
        }
    }

    public GraphComponent getAngleGraph() {
        return angleGraph;
    }

    public GraphComponent getAngularVelocityGraph() {
        return angularVelocityGraph;
    }

    public GraphComponent getAngularAccelerationGraph() {
        return angularAccelerationGraph;
    }

    public GraphComponent getPositionGraph() {
        return positionGraph;
    }

    public GraphComponent getSpeedGraph() {
        return speedGraph;
    }

    public GraphComponent getAccelerationGraph() {
        return accelerationGraph;
    }

    public GraphSuite[] getGraphSuites() {
        return suites;
    }

    public GraphSuite getGraphSuite( int i ) {
        return suites[i];
    }

    public void clear() {
        angleGraph.clear();
        angularVelocityGraph.clear();
        angularAccelerationGraph.clear();

        positionGraph.clear();
        positionGraph.clear();
        speedGraph.clear();
        accelerationGraph.clear();
    }
}
