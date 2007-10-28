package edu.colorado.phet.rotation.torque;

import edu.colorado.phet.rotation.AngleUnitModel;
import edu.colorado.phet.rotation.controls.VectorViewModel;
import edu.colorado.phet.rotation.model.RotationModel;
import edu.colorado.phet.rotation.model.RotationPlatform;
import edu.colorado.phet.rotation.view.RotationPlatformNode;
import edu.colorado.phet.rotation.view.RotationPlayAreaNode;
import edu.umd.cs.piccolo.PNode;

/**
 * Created by IntelliJ IDEA.
 * User: Sam
 * Date: Oct 5, 2007
 * Time: 1:52:23 PM
 */
public class IntroPlayAreaNode extends RotationPlayAreaNode {
    public IntroPlayAreaNode( RotationModel rotationModel, VectorViewModel vectiorViewModel, AngleUnitModel angleUnitModel ) {
        super( rotationModel, vectiorViewModel, angleUnitModel );
    }

    protected PNode createRotationPlatformNode( RotationPlatform rotationPlatform ) {
        return new RotationPlatformNode( rotationPlatform );
    }
}
