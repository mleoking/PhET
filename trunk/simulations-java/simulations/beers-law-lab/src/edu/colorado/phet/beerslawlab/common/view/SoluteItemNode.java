// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.beerslawlab.common.view;

import java.awt.Color;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.beerslawlab.common.BLLConstants;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.HTMLNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * Representation of a Solute item in a combo box.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class SoluteItemNode extends PComposite {

    public SoluteItemNode( final Property<Color> color, final String label ) {

        // solute color chip
        final PPath colorNode = new PPath( new Rectangle2D.Double( 0, 0, 20, 20 ) );
        colorNode.setStroke( BLLConstants.FLUID_STROKE );
        addChild( colorNode );

        // solute label
        HTMLNode labelNode = new HTMLNode();
        labelNode.setHTML( label );
        labelNode.setFont( new PhetFont( BLLConstants.CONTROL_FONT_SIZE ) );
        addChild( labelNode );

        // layout, color chip to left of label, centers vertically aligned
        colorNode.setOffset( 0, Math.max( 0, ( labelNode.getFullBoundsReference().getHeight() - colorNode.getFullBoundsReference().getHeight() ) / 2 ) );
        labelNode.setOffset( colorNode.getFullBoundsReference().getMaxX() + 5,
                             Math.max( 0, ( colorNode.getFullBoundsReference().getHeight() - labelNode.getFullBoundsReference().getHeight() ) / 2 ) );

        // keep the color chip synchronized with the color property
        color.addObserver( new VoidFunction1<Color>() {
            public void apply( Color color ) {
                colorNode.setPaint( color );
                colorNode.setStrokePaint( BLLConstants.createFluidStrokeColor( color ) );
            }
        } );
    }
}
