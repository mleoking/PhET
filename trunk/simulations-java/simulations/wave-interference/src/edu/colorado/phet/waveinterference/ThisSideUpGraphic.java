// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.waveinterference;

import java.awt.*;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.math.Function;
import edu.colorado.phet.common.phetcommon.view.graphics.Arrow;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.waveinterference.util.WIStrings;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PAffineTransform;

/**
 * User: Sam Reid
 * Date: Aug 23, 2006
 * Time: 8:35:43 PM
 */

public class ThisSideUpGraphic extends PhetPNode {
    private double origHeight;

    public ThisSideUpGraphic() {
        PText textNode = new PText( WIStrings.getString( "controls.top" ) );
        addChild( textNode );
        PPath arrowNode = new PPath( new Arrow( new Point2D.Double( 0, 0 ), new Point2D.Double( 0, -100 ), 30, 30, 15 ).getShape() );
        arrowNode.setPaint( Color.yellow );
        arrowNode.setStroke( new BasicStroke( 2 ) );
        arrowNode.setStrokePaint( Color.black );
        addChild( arrowNode );

        textNode.setFont( new PhetFont( Font.BOLD, 24 ) );
        arrowNode.setOffset( textNode.getFullBounds().getWidth() + arrowNode.getFullBounds().getWidth() / 2, textNode.getFullBounds().getHeight() / 2 + arrowNode.getFullBounds().getHeight() / 2 );
        origHeight = getFullBounds().getHeight();
    }

    public void setAngle( double angle ) {
        if ( angle > 0 ) {
            setVisible( true );
            Function.LinearFunction linearFunction = new Function.LinearFunction( 0, Math.PI / 2, 0, 1 );
            double newSY = linearFunction.evaluate( angle );
            double offsetY = newSY * origHeight / 2.0;
            PAffineTransform at = getTransformReference( true );
            getTransformReference( true ).setTransform( 1, at.getShearY(), at.getShearX(), newSY, at.getTranslateX(), offsetY );
            invalidatePaint();
            invalidateFullBounds();
            firePropertyChange( PROPERTY_CODE_TRANSFORM, PROPERTY_TRANSFORM, null, getTransform() );
        }
        else {
            setVisible( false );
        }
    }
}
