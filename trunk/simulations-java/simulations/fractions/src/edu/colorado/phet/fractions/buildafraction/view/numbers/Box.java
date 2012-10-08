// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.buildafraction.view.numbers;

import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.fractions.FractionsResources.Images;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;

/**
 * Data structure which handles one of the {whole, numerator or denominator} parts in a FractionNode.
 * Parts are null until assigned and null if not attached.
 *
 * @author Sam Reid
 */
public class Box {
    public ShapeContainer box;
    public NumberCardNode cardNode;
    public NumberNode numberNode;
    public PNode parent;
    private final PNode notAllowedIcon = new PImage( Images.NOT_ALLOWED );

    //Sets whether the box is visible and interactive
    public void setEnabled( final boolean enabled ) {
        box.setVisible( enabled );

        //Show a "not allowed" sign but only if the spot isn't filled with a number
        if ( cardNode == null ) {
            if ( !enabled ) {
                box.getParent().addChild( notAllowedIcon );
                notAllowedIcon.centerFullBoundsOnPoint( box.getFullBoundsReference().getCenterX(), box.getFullBoundsReference().getCenterY() );
            }
            else if ( box.getParent().getChildrenReference().indexOf( notAllowedIcon ) > 0 ) {
                box.getParent().removeChild( notAllowedIcon );
            }
        }
    }

    public boolean isEnabled() { return box.getVisible(); }

    //Extra layer so it can have 2 levels of enabled/disabled
    public static class ShapeContainer extends PNode {
        public final PhetPPath shape;

        public ShapeContainer( PhetPPath shape ) {
            this.shape = shape;
            addChild( shape );
        }
    }
}