// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.common.view;

import java.awt.Color;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.PhetPText;
import edu.colorado.phet.linegraphing.common.LGResources.Strings;
import edu.colorado.phet.linegraphing.common.model.StraightLine;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * Base class for all factories that creates a node for displaying an equation in reduced form.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class EquationNodeFactory {

    protected static final double X_SPACING = 3;
    protected static final double Y_SPACING = 0;

    public abstract EquationNode createNode( StraightLine line, PhetFont font );

    /*
     * Base class for all reduced forms of the equation.
     */
    protected static abstract class ReducedEquationNode extends EquationNode {

        public ReducedEquationNode() {
            setPickable( false );
        }

        // Changes the color of the equation
        public void setEquationColor( Color color ) {
            for ( int i = 0; i < getChildrenCount(); i++ ) {
                PNode child = getChild( i );
                if ( child instanceof PText ) {
                    ( (PText) child ).setTextPaint( color );
                }
                else if ( child instanceof PPath ) {
                    ( (PPath) child ).setStrokePaint( color );
                }
            }
        }
    }

    /*
     * Slope is undefined.
     */
    protected static class UndefinedSlopeNode extends ReducedEquationNode {
        public UndefinedSlopeNode( StraightLine line, PhetFont font ) {
            setPickable( false );
            addChild( new PhetPText( Strings.SLOPE_UNDEFINED, font, line.color ) );
        }
    }
}
