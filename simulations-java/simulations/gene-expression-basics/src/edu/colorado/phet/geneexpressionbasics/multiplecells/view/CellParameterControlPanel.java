// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.geneexpressionbasics.multiplecells.view;

import java.awt.Color;
import java.awt.Font;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.umd.cs.piccolo.PNode;

/**
 * Convenience class for constructing control panels that allow users to alter
 * some of the parameters of the multi-cell protein synthesis model.  This
 * exists to make the fonts, colors, etc. consistent across panels.
 *
 * @author John Blanco
 */
public class CellParameterControlPanel extends CollapsibleControlPanel {

    private static final Font TITLE_LABEL_FONT = new PhetFont( 16, true );
    private static final Color BACKGROUND_COLOR = new Color( 220, 236, 255 );

    public CellParameterControlPanel( String title, PNode controls ) {
        super( BACKGROUND_COLOR, title, TITLE_LABEL_FONT, controls );
    }
}
