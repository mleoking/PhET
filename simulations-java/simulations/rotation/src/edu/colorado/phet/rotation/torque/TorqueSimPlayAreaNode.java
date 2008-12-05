package edu.colorado.phet.rotation.torque;

import java.awt.*;
import java.awt.geom.Line2D;

import edu.colorado.phet.common.phetcommon.view.graphics.Arrow;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.rotation.controls.VectorViewModel;
import edu.colorado.phet.rotation.model.AngleUnitModel;
import edu.colorado.phet.rotation.model.RotationPlatform;
import edu.colorado.phet.rotation.view.RotationPlayAreaNode;
import edu.umd.cs.piccolo.PNode;

/**
 * Author: Sam Reid
 * Aug 8, 2007, 7:28:37 PM
 */
public class TorqueSimPlayAreaNode extends RotationPlayAreaNode {
    private TorqueModel torqueModel;
    private PhetPPath appliedForceVector;
    private PhetPPath brakeForceVector;
    private PhetPPath tangentialComponentVector;
    private BrakeNode brakeNode;

    public TorqueSimPlayAreaNode( final TorqueModel torqueModel, VectorViewModel vectorViewModel, AngleUnitModel angleUnitModel ) {
        super( torqueModel, vectorViewModel, angleUnitModel );
        this.torqueModel = torqueModel;
        getPlatformNode().addInputEventListener( new RotationPlatformTorqueHandler( getPlatformNode(), torqueModel, torqueModel.getRotationPlatform() ) );
        getPlatformNode().addInputEventListener( new CursorHandler() );

        appliedForceVector = new PhetPPath( null, Color.blue, new BasicStroke( 0.01f ), Color.black );
        brakeForceVector = new PhetPPath( null, Color.red, new BasicStroke( 0.01f ), Color.black );
        tangentialComponentVector = new PhetPPath( null, Color.green, new BasicStroke( 0.01f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1f, new float[]{0.05f, 0.05f}, 0 ), Color.darkGray );
        torqueModel.getAppliedForceObject().addListener( new AppliedForce.Listener() {
            public void changed() {
                updateArrows();
            }
        } );
        torqueModel.getBrakeForceObject().addListener( new AppliedForce.Listener() {
            public void changed() {
                updateArrows();
            }
        } );
        brakeNode = new BrakeNode( torqueModel.getRotationPlatform(), torqueModel );
        addChild( tangentialComponentVector );
        addChild( appliedForceVector );
        addChild( brakeForceVector );
        addChild( 0, brakeNode );

        torqueModel.addListener( new TorqueModel.Adapter() {
            public void showComponentsChanged() {
                tangentialComponentVector.setVisible( torqueModel.isShowComponents() );
            }
        } );
        updateArrows();
    }

    protected PNode createRotationPlatformNode( RotationPlatform rotationPlatform ) {
        return new RotationPlatformNodeWithMassDisplay( rotationPlatform );
    }

    private void updateArrows() {
        Line2D.Double force = torqueModel.getAppliedForce();
//        System.out.println( "force.getP1() = " + force.getP1()+", -> "+force.getP2() );
        appliedForceVector.setPathTo( getForceShape( force ) );
        brakeForceVector.setPathTo( getForceShape( torqueModel.getBrakeForceValue() ) );
        tangentialComponentVector.setPathTo( getForceShape( torqueModel.getTangentialAppliedForce() ) );
    }

    private Shape getForceShape( Line2D.Double vector ) {
        return new Arrow( vector.getP1(), vector.getP2(), 0.25, 0.25, 0.1, 1.0, true ).getShape();
    }

}
