// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.piccolophet.nodes.periodictable;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.nodes.PText;

import static edu.colorado.phet.common.piccolophet.nodes.periodictable.PeriodicTableNode.CELL_DIMENSION;

/**
 * Basic, non-interactive cell for periodic table.
 */
public class BasicElementCell extends ElementCell {
    private final Font LABEL_FONT = new PhetFont( 12 );
    private final PText text;
    private final PhetPPath box;

    public BasicElementCell( final int atomicNumber, final Color backgroundColor ) {
        super( atomicNumber );

        box = new PhetPPath( new Rectangle2D.Double( 0, 0, CELL_DIMENSION, CELL_DIMENSION ),
                             backgroundColor, new BasicStroke( 1 ), Color.black );
        addChild( box );

        String abbreviation = SymbolTable.getSymbol( atomicNumber );
        text = new PText( abbreviation ) {{
            setFont( LABEL_FONT );
        }};
        text.setOffset( box.getFullBounds().getCenterX() - text.getFullBounds().getWidth() / 2,
                        box.getFullBounds().getCenterY() - text.getFullBounds().getHeight() / 2 );
        addChild( text );
    }

    protected PText getText() {
        return text;
    }

    protected PhetPPath getBox() {
        return box;
    }
}
