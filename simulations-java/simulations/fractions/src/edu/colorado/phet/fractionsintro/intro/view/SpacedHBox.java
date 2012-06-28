// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractionsintro.intro.view;

import edu.colorado.phet.common.piccolophet.RichPNode;
import edu.umd.cs.piccolo.PNode;

/**
 * For layout of the pies, necessary because when sliced into different regions, the bounding boxes can extend beyond the pie.
 * So instead we ignore the full bounds and just space based on diameter
 *
 * @author Sam Reid
 */
public class SpacedHBox extends RichPNode {
    private final double spacing;
    private double x = 0;

    public SpacedHBox( double spacing ) {
        this.spacing = spacing;
    }

    @Override public void addChild( PNode child ) {
        child.setOffset( x, child.getYOffset() );
        super.addChild( child );
        x += spacing;
    }
}