// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractionsintro.intro.view.pieset;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.AffineTransform;

import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.fractionsintro.intro.model.pieset.Slice;
import edu.umd.cs.piccolo.PNode;

/**
 * Node for showing the shape of a Slice that has a shape() function that matches the desired representation, for example for squares or circles (but not for cakes or water glasses).
 *
 * @author Sam Reid
 */
public class ShapeNode extends PNode {
    private static final Paint SHADOW_PAINT = new Color( 0, 0, 0, (int) ( 0.75 * 255 ) );

    public ShapeNode( Slice slice ) {

        //A piece should have a shadow if it is being dragged or animating to a location
        boolean showShadow = slice.dragging || slice.animationTarget != null;

        final Shape origShape = slice.shape();
        Shape shape = showShadow ? AffineTransform.getTranslateInstance( -5, -5 ).createTransformedShape( origShape ) : origShape;
        if ( Double.isNaN( shape.getBounds2D().getX() ) || Double.isNaN( shape.getBounds2D().getY() ) ) {
            //TODO: Find and prevent the NaNs in the first place
            return;
        }

        //Show a shadow behind the object so it will look like it is a bit out of the screen and hence not part of the fraction
        if ( showShadow ) {
            addChild( new PhetPPath( origShape, SHADOW_PAINT ) );
        }

        PhetPPath path = new PhetPPath( shape, slice.color, new BasicStroke( 2 ), Color.black );
        addChild( path );
    }
}