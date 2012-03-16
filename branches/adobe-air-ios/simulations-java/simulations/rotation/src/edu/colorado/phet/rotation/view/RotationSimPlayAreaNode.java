// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.rotation.view;

import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.rotation.controls.VectorViewModel;
import edu.colorado.phet.rotation.model.AngleUnitModel;
import edu.colorado.phet.rotation.model.RotationModel;

/**
 * Author: Sam Reid
 * Aug 8, 2007, 7:28:20 PM
 */
public class RotationSimPlayAreaNode extends RotationPlayAreaNode {
    public RotationSimPlayAreaNode( RotationModel rotationModel, VectorViewModel vectorViewModel, AngleUnitModel angleUnitModel ) {
        super( rotationModel, vectorViewModel, angleUnitModel );

        //todo should subclass platformnode
        getPlatformNode().addInputEventListener( new RotationPlatformDragHandler( getPlatformNode(), rotationModel, rotationModel.getRotationPlatform() ) );
        getPlatformNode().addInputEventListener( new CursorHandler() );
    }
}
