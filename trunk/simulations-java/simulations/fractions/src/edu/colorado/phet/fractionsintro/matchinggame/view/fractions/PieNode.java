// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractionsintro.matchinggame.view.fractions;

import java.awt.Color;

import edu.colorado.phet.common.piccolophet.nodes.kit.ZeroOffsetNode;
import edu.colorado.phet.fractionsintro.intro.model.containerset.ContainerSet;
import edu.umd.cs.piccolo.PNode;

/**
 * Graphic for one pie, which is a set of cells
 *
 * @author Sam Reid
 */
public class PieNode extends PNode implements IColor {
    private final Color color;

    public PieNode( ContainerSet containerSet, Color color ) {
        this.color = color;

        PieSetFractionNode pieSetFractionNode = new PieSetFractionNode( containerSet, color );
        addChild( new ZeroOffsetNode( pieSetFractionNode ) );

        scale( 0.5 );
    }

    public Color getColor() {
        return color;
    }
}