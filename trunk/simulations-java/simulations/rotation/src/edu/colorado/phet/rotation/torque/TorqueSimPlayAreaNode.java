package edu.colorado.phet.rotation.torque;

import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.rotation.AngleUnitModel;
import edu.colorado.phet.rotation.controls.VectorViewModel;
import edu.colorado.phet.rotation.model.RotationModel;
import edu.colorado.phet.rotation.view.RotationPlatformDragHandler;
import edu.colorado.phet.rotation.view.RotationPlayAreaNode;

/**
 * Author: Sam Reid
 * Aug 8, 2007, 7:28:37 PM
 */
public class TorqueSimPlayAreaNode extends RotationPlayAreaNode {
    public TorqueSimPlayAreaNode( RotationModel rotationModel, VectorViewModel vectorViewModel, AngleUnitModel angleUnitModel ) {
        super( rotationModel, vectorViewModel, angleUnitModel );

        getPlatformNode().addInputEventListener( new RotationPlatformTorqueHandler( getPlatformNode(), rotationModel, rotationModel.getRotationPlatform() ) );
        getPlatformNode().addInputEventListener( new CursorHandler() );
    }
}
