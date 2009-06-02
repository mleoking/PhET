package edu.colorado.phet.naturalselection.test.sprites;

import java.awt.*;

import edu.colorado.phet.common.phetcommon.math.Point3D;
import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;
import edu.umd.cs.piccolo.nodes.PPath;

public class ActiveSprite extends Sprite {

    private PPath graphics;

    public ActiveSprite( Landscape landscape, Point3D position ) {
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

        addChild( graphics );

        graphics.setScale( 200 / position.getZ() );
    }

    public void perturb() {
        Point3D cur = getPosition();
        double x = cur.getX() + Math.random() * 4 - 2;
        double z = cur.getZ() + Math.random() * 4 - 2;
        if ( z > Landscape.LANDSCAPE_FARPLANE ) {
            z = Landscape.LANDSCAPE_FARPLANE;
        }
        if ( z < Landscape.LANDSCAPE_NEARPLANE ) {
            z = Landscape.LANDSCAPE_NEARPLANE;
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