package edu.colorado.phet.movingman.motion.ramps;

import java.awt.*;

import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.common.phetcommon.model.ModelElement;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.movingman.motion.FreeBodyDiagramNode;

/**
 * Created by: Sam
 * Dec 7, 2007 at 10:14:08 AM
 */
public class Force1DMotionControlPanel extends VerticalLayoutPanel {
    public Force1DMotionControlPanel( final Force1DMotionModel model ) {
        PhetPCanvas phetPCanvas = new PhetPCanvas();
        phetPCanvas.addScreenChild( new FreeBodyDiagramNode( new FreeBodyDiagramNode.IFBDObject() {
            public void startRecording() {
                model.startRecording();
            }

            public void setAppliedForce( double v ) {
                model.setAppliedForce( v );
            }

            public void addModelElement( final ModelElement modelElement ) {
                model.getClock().addClockListener( new ClockAdapter() {
                    public void simulationTimeChanged( ClockEvent clockEvent ) {
                        modelElement.stepInTime( clockEvent.getSimulationTimeChange() );
                    }
                } );
            }

            public double getViewAngle() {
                return 0;
            }

            public Vector2D.Double getAppliedForce() {
                return new Vector2D.Double( model.getAppliedForce().getValue(), 0 );
            }

            public Vector2D.Double getFrictionForce() {
                return new Vector2D.Double( model.getFrictionForce().getValue(), 0 );
            }

            public Vector2D.Double getTotalForce() {
                return new Vector2D.Double( model.getNetForce().getValue(), 0 );
            }

            public Vector2D.Double getWallForce() {
                return new Vector2D.Double( model.getWallForce().getValue(), 0 );
            }

            public Vector2D.Double getGravityForce() {
                return new Vector2D.Double();
//                return new Vector2D.Double( 0, model.getGravity().getValue() * model.getMass().getValue() );
            }

            public Vector2D.Double getNormalForce() {
                return new Vector2D.Double();
//                return new Vector2D.Double( 0, model.getNormalForce().getValue() );
            }
        } ) );
        phetPCanvas.setSize( 200, 200 );
        phetPCanvas.setPreferredSize( new Dimension( 200, 200 ) );
        add( phetPCanvas );
        add( new ObjectSelectionPanel( model.getObjects(), new ObjectSelectionPanel.Listener() {
            public void objectChanged( Force1DObject force1DObject ) {
                model.setObject( force1DObject );
            }
        } ) );
    }
}
