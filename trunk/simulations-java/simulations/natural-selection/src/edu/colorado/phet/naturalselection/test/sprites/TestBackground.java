// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.naturalselection.test.sprites;

import java.awt.*;
import java.awt.geom.AffineTransform;

import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;

public class TestBackground extends PNode {
    private PPath mainBackground;
    private PPath skyBackground;

    public TestBackground( double width, double height ) {

        mainBackground = new PPath();

        mainBackground.setStroke( new BasicStroke( 20 ) );
        mainBackground.setPaint( Color.BLACK );
        mainBackground.setStrokePaint( Color.BLUE );

        DoubleGeneralPath path = new DoubleGeneralPath();
        path.moveTo( 0, 0 );
        path.lineTo( TestLandscape.LANDSCAPE_SIZE.getWidth(), 0 );
        path.lineTo( TestLandscape.LANDSCAPE_SIZE.getWidth(), TestLandscape.LANDSCAPE_SIZE.getHeight() );
        path.lineTo( 0, TestLandscape.LANDSCAPE_SIZE.getHeight() );
        path.lineTo( 0, 0 );

        mainBackground.setPathTo( path.getGeneralPath() );

        addChild( mainBackground );

        skyBackground = new PPath();

        skyBackground.setPaint( Color.WHITE );
        skyBackground.setStroke( new BasicStroke( 2 ) );
        skyBackground.setStrokePaint( Color.GREEN );


        DoubleGeneralPath skyPath = new DoubleGeneralPath();
        skyPath.moveTo( 0, 0 );
        skyPath.lineTo( TestLandscape.LANDSCAPE_SIZE.getWidth(), 0 );
        skyPath.lineTo( TestLandscape.LANDSCAPE_SIZE.getWidth(), TestLandscape.LANDSCAPE_HORIZON );
        skyPath.lineTo( 0, TestLandscape.LANDSCAPE_HORIZON );
        skyPath.lineTo( 0, 0 );

        skyBackground.setPathTo( skyPath.getGeneralPath() );

        addChild( skyBackground );

        updateLayout( width, height );

    }

    public void updateLayout( double width, double height ) {
        AffineTransform trans = new AffineTransform();
        trans.setToScale( width / TestLandscape.LANDSCAPE_SIZE.getWidth(), height / TestLandscape.LANDSCAPE_SIZE.getHeight() );

        setTransform( trans );
    }
}
