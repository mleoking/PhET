package edu.colorado.phet.naturalselection.test.sprites;

import java.awt.*;
import java.awt.geom.AffineTransform;

import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;

public class Background extends PNode {
    private PPath mainBackground;
    private PPath skyBackground;

    public Background( double width, double height ) {

        mainBackground = new PPath();

        mainBackground.setStroke( new BasicStroke( 20 ) );
        mainBackground.setPaint( Color.BLACK );
        mainBackground.setStrokePaint( Color.BLUE );

        DoubleGeneralPath path = new DoubleGeneralPath();
        path.moveTo( 0, 0 );
        path.lineTo( Landscape.LANDSCAPE_SIZE.getWidth(), 0 );
        path.lineTo( Landscape.LANDSCAPE_SIZE.getWidth(), Landscape.LANDSCAPE_SIZE.getHeight() );
        path.lineTo( 0, Landscape.LANDSCAPE_SIZE.getHeight() );
        path.lineTo( 0, 0 );

        mainBackground.setPathTo( path.getGeneralPath() );

        addChild( mainBackground );

        skyBackground = new PPath();

        skyBackground.setPaint( Color.WHITE );
        skyBackground.setStroke( new BasicStroke( 2 ) );
        skyBackground.setStrokePaint( Color.GREEN );


        DoubleGeneralPath skyPath = new DoubleGeneralPath();
        skyPath.moveTo( 0, 0 );
        skyPath.lineTo( Landscape.LANDSCAPE_SIZE.getWidth(), 0 );
        skyPath.lineTo( Landscape.LANDSCAPE_SIZE.getWidth(), Landscape.LANDSCAPE_HORIZON );
        skyPath.lineTo( 0, Landscape.LANDSCAPE_HORIZON );
        skyPath.lineTo( 0, 0 );

        skyBackground.setPathTo( skyPath.getGeneralPath() );

        addChild( skyBackground );

        updateLayout( width, height );

    }

    public void updateLayout( double width, double height ) {
        AffineTransform trans = new AffineTransform();
        trans.setToScale( width / Landscape.LANDSCAPE_SIZE.getWidth(), height / Landscape.LANDSCAPE_SIZE.getHeight() );

        setTransform( trans );
    }
}
