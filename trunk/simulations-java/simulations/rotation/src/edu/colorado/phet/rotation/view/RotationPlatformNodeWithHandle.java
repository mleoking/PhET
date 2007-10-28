package edu.colorado.phet.rotation.view;

import java.awt.*;

import edu.colorado.phet.common.piccolophet.nodes.HandleNode;
import edu.colorado.phet.rotation.model.RotationPlatform;

/**
 * Created by: Sam
 * Oct 28, 2007 at 4:34:09 PM
 */
public class RotationPlatformNodeWithHandle extends RotationPlatformNode {
    private double handleHeight = 10 * RotationPlayAreaNode.SCALE;

    public RotationPlatformNodeWithHandle( final RotationPlatform rotationPlatform ) {
        super( rotationPlatform );
        HandleNode handleNode2 = new HandleNode( handleHeight / 2 * 7, handleHeight * 7, Color.gray );
        handleNode2.setStroke( new BasicStroke( 1.0f / 50.0f ) );
        handleNode2.setOffset( rotationPlatform.getRadius() + handleNode2.getFullBounds().getWidth() * 0.9, handleNode2.getFullBounds().getHeight() / 2 );
        handleNode2.rotate( Math.PI );
        super.addContentNode( handleNode2 );
    }
}
