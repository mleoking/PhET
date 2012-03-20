// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.intro.view;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import edu.colorado.phet.common.piccolophet.nodes.TextButtonNode;
import edu.colorado.phet.linegraphing.LGConstants;
import edu.colorado.phet.linegraphing.LGResources.Strings;
import edu.umd.cs.piccolo.PNode;

/**
 * Control for saving and erasing lines.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
class SavedLinesControl extends PNode {

    public SavedLinesControl() {

        final TextButtonNode saveLineButton = new TextButtonNode( Strings.SAVE_LINE, LGConstants.CONTROL_FONT, new Color( 0, 255, 255 ) );
        final TextButtonNode eraseLinesButton = new TextButtonNode( Strings.ERASE_LINES, LGConstants.CONTROL_FONT, new Color( 0, 255, 255 ) ) {{
            setEnabled( false );
        }};

        addChild( saveLineButton );
        addChild( eraseLinesButton );

        // "Erase" to right of "Save"
        eraseLinesButton.setOffset( saveLineButton.getFullBoundsReference().getWidth() + 10, saveLineButton.getYOffset() );

        saveLineButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                eraseLinesButton.setEnabled( true );
                //TODO save the current line
            }
        } );

        eraseLinesButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                eraseLinesButton.setEnabled( false );
                //TODO erase the saved lines
            }
        } );
    }
}
