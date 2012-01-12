// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.rotation.view;

import java.awt.*;

import edu.colorado.phet.common.motion.model.IVariable;
import edu.colorado.phet.common.piccolophet.nodes.HandleNode;
import edu.colorado.phet.rotation.model.RotationPlatform;
import edu.umd.cs.piccolo.PNode;

/**
 * Created by: Sam
 * Oct 28, 2007 at 4:34:09 PM
 */
public class RotationPlatformNodeWithHandle extends PlatformNode2 {
    private double handleHeight = 10 * RotationPlayAreaNode.SCALE;
    private PNode contentNode = new PNode();
    private HandleNode handleNode2;
    private RotationPlatform rotationPlatform;

    public RotationPlatformNodeWithHandle( final RotationPlatform rotationPlatform ) {
        super( rotationPlatform );
        this.rotationPlatform = rotationPlatform;
        handleNode2 = new HandleNode( handleHeight / 2 * 7, handleHeight * 7, Color.gray );
        handleNode2.setStroke( new BasicStroke( 1.0f / 50.0f ) );
        handleNode2.setOffset( rotationPlatform.getRadius() + handleNode2.getFullBounds().getWidth() * 0.9, handleNode2.getFullBounds().getHeight() / 2 );
        handleNode2.rotate( Math.PI );

        addChild( contentNode );
        contentNode.addChild( handleNode2 );
        rotationPlatform.getPositionVariable().addListener( new IVariable.Listener() {
            public void valueChanged() {
                doUpdateAngle();
            }
        } );
    }

    private void doUpdateAngle() {
        contentNode.setRotation( 0 );
        contentNode.setOffset( 0, 0 );
        contentNode.rotateAboutPoint( rotationPlatform.getPosition(), rotationPlatform.getCenter().getX(), rotationPlatform.getCenter().getY() );
    }

}
