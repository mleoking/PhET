// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.naturalselection.test.sprites;

import java.awt.*;

import edu.colorado.phet.common.phetcommon.math.Point3D;
import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;

public class TestActiveSprite extends TestSprite {

    private PPath graphics;

    public TestActiveSprite( TestLandscape landscape, Point3D position ) {
        super( landscape, position );

        graphics = new PPath();

        graphics.setStroke( new BasicStroke( 1 ) );
        graphics.setPaint( new Color( (int) ( Math.random() * 256 ), (int) ( Math.random() * 256 ), (int) ( Math.random() * 256 ) ) );
        graphics.setStrokePaint( Color.WHITE );

        final double M = 10.0;

        DoubleGeneralPath path = new DoubleGeneralPath();
        path.moveTo( -M, 0 );
        path.lineTo( M, 0 );
        path.lineTo( M, -2 * M );
        path.lineTo( -M, -2 * M );
        path.lineTo( -M, 0 );

        graphics.setPathTo( path.getGeneralPath() );

        PNode a = new PNode();
        PNode b = new PNode();
        PNode c = new PNode();
        c.addChild( graphics );
        b.addChild( c );
        a.addChild( b );
        addChild( a );
        //addChild( graphics );

        graphics.setScale( 200 / position.getZ() );
    }

    public void perturb() {
        Point3D cur = getPosition();
        double x = cur.getX() + Math.random() * 4 - 2;
        double z = cur.getZ() + Math.random() * 4 - 2;
        if ( z > TestLandscape.LANDSCAPE_FARPLANE ) {
            z = TestLandscape.LANDSCAPE_FARPLANE;
        }
        if ( z < TestLandscape.LANDSCAPE_NEARPLANE ) {
            z = TestLandscape.LANDSCAPE_NEARPLANE;
        }
        double mx = landscape.getMaximumX( z );
        if ( x > mx ) {
            x = mx;
        }
        if ( x < -mx ) {
            x = -mx;
        }
        double y = landscape.getGroundY( x, z );
        setPosition( new Point3D.Double( x, y, z ) );
        graphics.setScale( 200 / z );
    }
}