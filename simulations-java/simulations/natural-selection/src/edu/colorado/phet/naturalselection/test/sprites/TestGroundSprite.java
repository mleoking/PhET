// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.naturalselection.test.sprites;

import java.awt.*;

import edu.colorado.phet.common.phetcommon.math.Point3D;
import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;
import edu.umd.cs.piccolo.nodes.PPath;

public class TestGroundSprite extends TestSprite {

    private PPath graphics;

    public TestGroundSprite( TestLandscape landscape, Point3D position ) {
        super( landscape, position );

        graphics = new PPath();

        graphics.setStroke( new BasicStroke( 1 ) );
        graphics.setPaint( Color.DARK_GRAY );
        graphics.setStrokePaint( Color.BLACK );

        DoubleGeneralPath path = new DoubleGeneralPath();
        path.moveTo( 0, 1 );
        path.lineTo( 10, 10 );
        path.lineTo( 0, 20 );
        path.lineTo( -10, 10 );
        path.lineTo( 0, 0 );

        graphics.setPathTo( path.getGeneralPath() );

        addChild( graphics );

        graphics.setScale( 2 * 200 / position.getZ() );
    }
}