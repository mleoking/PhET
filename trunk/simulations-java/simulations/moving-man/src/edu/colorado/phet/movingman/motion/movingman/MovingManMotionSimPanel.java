package edu.colorado.phet.movingman.motion.movingman;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.io.IOException;
import java.text.DecimalFormat;

import edu.colorado.phet.common.motion.graphs.GraphSetModel;
import edu.colorado.phet.common.motion.graphs.GraphSetNode;
import edu.colorado.phet.common.motion.graphs.GraphSuite;
import edu.colorado.phet.common.motion.graphs.MinimizableControlGraph;
import edu.colorado.phet.common.motion.model.ITemporalVariable;
import edu.colorado.phet.common.phetcommon.view.util.PhetDefaultFont;
import edu.colorado.phet.common.phetcommon.view.util.SimStrings;
import edu.colorado.phet.common.piccolophet.event.PDebugKeyHandler;
import edu.colorado.phet.movingman.MMUtil;
import edu.colorado.phet.movingman.motion.AbstractMotionSimPanel;
import edu.colorado.phet.movingman.motion.MovingManResources;
import edu.colorado.phet.movingman.motion.MotionVectorNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * Created by: Sam
 * Dec 4, 2007 at 1:44:35 PM
 */
public class MovingManMotionSimPanel extends AbstractMotionSimPanel {
    private MovingManNode movingManNode;
    private GraphSetNode graphSetNode;
    private MotionVectorNode velocityVector;
    private MotionVectorNode accelVector;
    private TimeReadoutNode timeReadoutNode;

    public MovingManMotionSimPanel( final MovingManMotionModel motionModel ) {

        try {
            movingManNode = new MovingManNode( motionModel );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
        addScreenChild( movingManNode );

        timeReadoutNode = new TimeReadoutNode( motionModel );
        movingManNode.addListener( new AbstractMovingManNode.Listener() {
            public void directionChanged() {
                updateTimeReadoutTransform();
            }
        } );
        updateTimeReadoutTransform();
        movingManNode.addChild( timeReadoutNode );

        PNode vectorLayer = new PNode();
        movingManNode.addChild( vectorLayer );

        velocityVector = new MotionVectorNode( new PhetDefaultFont( 14, true ), MovingManResources.getString( "variables.velocity"), MMUtil.transparify( Color.red, 128 ), new BasicStroke( 0.03f ), Color.black, -0.5 );
        velocityVector.setVisible( true );
        final ITemporalVariable.ListenerAdapter velocityVectorUpdate = new ITemporalVariable.ListenerAdapter() {
            public void valueChanged() {
                velocityVector.setVector( motionModel.getPosition(), 2, 15.0 / 500 * 10 * motionModel.getVelocity(), 0 );
            }
        };
        motionModel.getXVariable().addListener( velocityVectorUpdate );
        motionModel.getVVariable().addListener( velocityVectorUpdate );
        vectorLayer.addChild( velocityVector );

        accelVector = new MotionVectorNode( new PhetDefaultFont( 14, true ), MovingManResources.getString( "variables.acceleration"), MMUtil.transparify( Color.green, 128 ), new BasicStroke( 0.03f ), Color.black, 0.0 );
        accelVector.setVisible( true );
        final ITemporalVariable.ListenerAdapter accelVectorUpdate = new ITemporalVariable.ListenerAdapter() {
            public void valueChanged() {
                accelVector.setVector( motionModel.getPosition(), 2, 40.0 / 500 * 20 * motionModel.getAcceleration(), 0 );
            }
        };
        motionModel.getXVariable().addListener( accelVectorUpdate );
        motionModel.getAVariable().addListener( accelVectorUpdate );
        vectorLayer.addChild( accelVector );

        final MovingManGraph xGraph = getXGraph( motionModel );
        final MovingManGraph vGraph = getVGraph( motionModel );
        final MovingManGraph aGraph = getAGraph( motionModel );

        motionModel.addListener( new MovingManMotionModel.Adapter() {
            public void updateStrategyChanged() {
                xGraph.setSliderSelected( motionModel.isPositionDriven() );
                vGraph.setSliderSelected( motionModel.isVelocityDriven() );
                aGraph.setSliderSelected( motionModel.isAccelerationDriven() );
            }
        } );
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

    private void updateTimeReadoutTransform() {
        timeReadoutNode.setTransform( new AffineTransform() );
        timeReadoutNode.scale( 1.0 / 35.0 );
        timeReadoutNode.setOffset( -7, 0.02 );
        if ( movingManNode.getScaleX() < 0 ) {
            timeReadoutNode.transformBy( AffineTransform.getScaleInstance( -1, 1 ) );
            timeReadoutNode.translate( -3 * 35, 0 );
        }
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

    public void setRightDirPositive( boolean rightPositive ) {
        movingManNode.setRightDirPositive( rightPositive, this );
    }

    private class TimeReadoutNode extends PNode {
        private DecimalFormat decimalFormat = new DecimalFormat( "0.00" );

        public TimeReadoutNode( final MovingManMotionModel motionModel ) {
            final PText text = new PText();
            text.setFont( new PhetDefaultFont( 16, true ) );
            motionModel.getTimeVariable().addListener( new ITemporalVariable.ListenerAdapter() {
                public void valueChanged() {
                    text.setText( decimalFormat.format( motionModel.getTime() ) + " "+MovingManResources.getString( "units.seconds"));
                }
            } );
            addChild( text );
        }
    }
}
