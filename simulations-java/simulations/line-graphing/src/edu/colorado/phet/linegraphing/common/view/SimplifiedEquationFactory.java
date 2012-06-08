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
 * Base class for all factories that create nodes for equations in simplified form.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class SimplifiedEquationFactory {

    protected static final double X_SPACING = 3;
    protected static final double Y_SPACING = 0;

    public abstract EquationNode createNode( StraightLine line, PhetFont font );

    /*
     * Base class for all simplified equations.
     */
    protected static abstract class SimplifiedEquationNode extends EquationNode {

        public SimplifiedEquationNode() {
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
    protected static class UndefinedSlopeNode extends SimplifiedEquationNode {
        public UndefinedSlopeNode( StraightLine line, PhetFont font ) {
            setPickable( false );
            addChild( new PhetPText( Strings.SLOPE_UNDEFINED, font, line.color ) );
        }
    }
}
