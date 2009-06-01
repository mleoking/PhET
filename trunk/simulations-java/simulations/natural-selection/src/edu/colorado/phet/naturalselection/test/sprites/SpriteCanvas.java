package edu.colorado.phet.naturalselection.test.sprites;

import java.awt.*;
import java.awt.geom.AffineTransform;

import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.umd.cs.piccolo.PNode;

public class SpriteCanvas extends PhetPCanvas {

    public static final Dimension BASE = new Dimension( 640, 480 );
    private Background back;
    private Landscape landscape;

    public SpriteCanvas() {
        super( BASE );

        setWorldTransformStrategy( new ConstantTransformStrategy( new AffineTransform() ) );

        PNode root = new PNode();

        addWorldChild( root );

        back = new Background( 640, 480 );
        root.addChild( back );

        landscape = new Landscape();
        root.addChild( landscape );


    }

    public Landscape getLandscape() {
        return landscape;
    }

    @Override
    protected void updateLayout() {
        back.updateLayout( getWidth(), getHeight() );
        landscape.updateLayout( getWidth(), getHeight() );
    }
}
