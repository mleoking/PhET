// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.naturalselection.test.sprites;

import java.awt.*;
import java.awt.geom.AffineTransform;

import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.umd.cs.piccolo.PNode;

public class TestSpriteCanvas extends PhetPCanvas {

    public static final Dimension BASE = new Dimension( 640, 480 );
    private TestBackground back;
    private TestLandscape landscape;

    public TestSpriteCanvas() {
        super( BASE );

        setWorldTransformStrategy( new ConstantTransformStrategy( new AffineTransform() ) );

        PNode root = new PNode();

        addWorldChild( root );

        back = new TestBackground( 640, 480 );
        root.addChild( back );

        landscape = new TestLandscape();
        root.addChild( landscape );


    }

    public TestLandscape getLandscape() {
        return landscape;
    }

    @Override
    protected void updateLayout() {
        back.updateLayout( getWidth(), getHeight() );
        landscape.updateLayout( getWidth(), getHeight() );
    }
}
