package edu.colorado.phet.movingman.motion.movingman;

import java.awt.*;

import edu.colorado.phet.common.motion.graphs.GraphSetModel;
import edu.colorado.phet.common.motion.graphs.GraphSetNode;
import edu.colorado.phet.common.motion.graphs.GraphSuite;
import edu.colorado.phet.common.motion.graphs.MinimizableControlGraph;
import edu.colorado.phet.common.motion.model.ITemporalVariable;
import edu.colorado.phet.common.phetcommon.view.util.PhetDefaultFont;
import edu.colorado.phet.common.phetcommon.view.util.SimStrings;
import edu.colorado.phet.common.piccolophet.BufferedPhetPCanvas;
import edu.colorado.phet.common.piccolophet.event.PDebugKeyHandler;
import edu.colorado.phet.movingman.MMUtil;
import edu.colorado.phet.movingman.motion.MotionProjectLookAndFeel;
import edu.umd.cs.piccolo.PNode;

/**
 * Created by: Sam
 * Dec 4, 2007 at 1:44:35 PM
 */
public class MovingManMotionSimPanel extends BufferedPhetPCanvas {
    private MovingManNode movingManNode;
    private GraphSetNode graphSetNode;
    private MotionVectorNode velocityVector;
    private MotionVectorNode accelVector;

    public MovingManMotionSimPanel( final MovingManMotionModel motionModel ) {
        setBackground( MotionProjectLookAndFeel.BACKGROUND_COLOR );
        movingManNode = new MovingManNode( motionModel );
        addScreenChild( movingManNode );

        PNode vectorLayer = new PNode();
        movingManNode.addChild( vectorLayer );

        velocityVector = new MotionVectorNode( new PhetDefaultFont( 14, true ), "Velocity", MMUtil.transparify( Color.red, 128 ), new BasicStroke( 0.03f ), Color.black, -0.5 );
        velocityVector.setVisible( true );
        final ITemporalVariable.ListenerAdapter velocityVectorUpdate = new ITemporalVariable.ListenerAdapter() {
            public void valueChanged() {
                velocityVector.setVector( motionModel.getPosition(), 2, 15 * motionModel.getVelocity(), 0 );
            }
        };
        motionModel.getXVariable().addListener( velocityVectorUpdate );
        motionModel.getVVariable().addListener( velocityVectorUpdate );
        vectorLayer.addChild( velocityVector );

        accelVector = new MotionVectorNode( new PhetDefaultFont( 14, true ), "Acceleration", MMUtil.transparify( Color.green, 128 ), new BasicStroke( 0.03f ), Color.black, 0.0 );
        accelVector.setVisible( true );
        final ITemporalVariable.ListenerAdapter accelVectorUpdate = new ITemporalVariable.ListenerAdapter() {
            public void valueChanged() {
                accelVector.setVector( motionModel.getPosition(), 2, 40 * motionModel.getAcceleration(), 0 );
            }
        };
        motionModel.getXVariable().addListener( accelVectorUpdate );
        motionModel.getAVariable().addListener( accelVectorUpdate );
        vectorLayer.addChild( accelVector );
//        ControlGraphSeries[] s = manMotionModel.getControlGraphSeriesArray();
//        for ( int i = 0; i < s.length; i++ ) {
//            ControlGraphSeries controlGraphSeries = s[i];
//            MovingManGraph graph = new MovingManGraph(
//                    this, controlGraphSeries, controlGraphSeries.getTitle(), -10, 10,
//                    motionModel, true, motionModel.getTimeSeriesModel(), motionModel.getPositionDriven(), MovingManMotionModel.MAX_T, motionModel );
//        }

        MovingManGraph xGraph = new MovingManGraph(
                this, motionModel.getXSeries(), SimStrings.get( "variables.position.abbreviation" ), "x", -10, 10,
                motionModel, true, motionModel.getTimeSeriesModel(), motionModel.getPositionDriven(), MovingManMotionModel.MAX_T, motionModel );

        MovingManGraph vGraph = new MovingManGraph(
                this, motionModel.getVSeries(), SimStrings.get( "variables.velocity.abbreviation" ), "x", -0.1, 0.1,
                motionModel, true, motionModel.getTimeSeriesModel(), motionModel.getVelocityDriven(), MovingManMotionModel.MAX_T, motionModel );

        MovingManGraph aGraph = new MovingManGraph(
                this, motionModel.getASeries(), SimStrings.get( "variables.position.abbreviation" ), "x", -0.01, 0.01,
                motionModel, true, motionModel.getTimeSeriesModel(), motionModel.getAccelDriven(), MovingManMotionModel.MAX_T, motionModel );

        graphSetNode = new GraphSetNode( new GraphSetModel( new GraphSuite( new MinimizableControlGraph[]{
                new MinimizableControlGraph( SimStrings.get( "variables.position.abbreviation" ), xGraph ),
                new MinimizableControlGraph( SimStrings.get( "variables.velocity.abbreviation" ), vGraph ),
                new MinimizableControlGraph( SimStrings.get( "variables.acceleration.abbreviation" ), aGraph )
        } ) ) );

        graphSetNode.setAlignedLayout();
        addScreenChild( graphSetNode );
        requestFocus();
        addKeyListener( new PDebugKeyHandler() );

        updateLayout();
    }

    protected void updateLayout() {
        super.updateLayout();
        movingManNode.setTransform( 22.0, getWidth() );

        int insetX = 2;
        graphSetNode.setBounds( insetX, movingManNode.getFullBounds().getMaxY(), getWidth() - 2 * insetX, getHeight() - movingManNode.getFullBounds().getMaxY() );
    }

    public void setShowVelocityVector( boolean selected ) {
        velocityVector.setVisible( selected );
    }

    public void setShowAccelerationVector( boolean selected ) {
        accelVector.setVisible( selected );
    }
}
