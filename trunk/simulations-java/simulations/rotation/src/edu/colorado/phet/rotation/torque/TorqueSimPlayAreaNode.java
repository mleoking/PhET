package edu.colorado.phet.rotation.torque;

import edu.colorado.phet.common.phetcommon.view.graphics.Arrow;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.rotation.AngleUnitModel;
import edu.colorado.phet.rotation.controls.VectorViewModel;
import edu.colorado.phet.rotation.view.RotationPlayAreaNode;

import java.awt.*;
import java.awt.geom.GeneralPath;

/**
 * Author: Sam Reid
 * Aug 8, 2007, 7:28:37 PM
 */
public class TorqueSimPlayAreaNode extends RotationPlayAreaNode {
    TorqueModel torqueModel;

    public TorqueSimPlayAreaNode( TorqueModel torqueModel, VectorViewModel vectorViewModel, AngleUnitModel angleUnitModel ) {
        super( torqueModel, vectorViewModel, angleUnitModel );
        this.torqueModel = torqueModel;
        getPlatformNode().addInputEventListener( new RotationPlatformTorqueHandler( getPlatformNode(), torqueModel, torqueModel.getRotationPlatform() ) );
        getPlatformNode().addInputEventListener( new CursorHandler() );

        final PhetPPath arrowNode = new PhetPPath( getShape(), Color.blue, new BasicStroke( 0.01f ), Color.black );
        torqueModel.addListener( new TorqueModel.Listener() {
            public void changed() {
                arrowNode.setPathTo( getShape() );
            }
        } );
        addChild( arrowNode );
    }

    private GeneralPath getShape() {
        return new Arrow( torqueModel.getAppliedForce().getP1(), torqueModel.getAppliedForce().getP2(), 0.25, 0.25, 0.1, 1.0, true ).getShape();
    }
}
