// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.intro.view;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.nodes.ControlPanelNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPText;
import edu.colorado.phet.common.piccolophet.nodes.TextButtonNode;
import edu.colorado.phet.common.piccolophet.nodes.kit.ZeroOffsetNode;
import edu.colorado.phet.linegraphing.LGColors;
import edu.colorado.phet.linegraphing.LGConstants;
import edu.colorado.phet.linegraphing.LGResources.Strings;
import edu.colorado.phet.linegraphing.intro.model.SlopeInterceptLine;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * Control panel for interacting with the line equation.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
class EquationControlPanel extends PhetPNode {

    public EquationControlPanel( final Property<SlopeInterceptLine> interactiveLine, final LineGraphNode lineGraphNode, final TextButtonNode eraseLinesButton ) {

        PNode titleNode = new PhetPText( "y = mx + b", new PhetFont( Font.BOLD, 18 ) );
        PNode equationNode = new ZeroOffsetNode( new SlopeInterceptEquationNode( interactiveLine ) );
        PNode strutNode = new PPath( new Rectangle2D.Double( 0, 0, getFullBoundsReference().getWidth(), 1 ) );
        final TextButtonNode saveLineButton = new TextButtonNode( Strings.SAVE_LINE, LGConstants.CONTROL_FONT, new Color( 0, 255, 255 ) );

        PNode parentNode = new PNode();
        parentNode.addChild( titleNode );
        parentNode.addChild( equationNode );
        parentNode.addChild( strutNode );
        parentNode.addChild( saveLineButton );

        // layout
        {
            double maxWidth = parentNode.getFullBoundsReference().getWidth();
            strutNode.setOffset( 0, 0 );
            titleNode.setOffset( ( maxWidth / 2 ) - ( titleNode.getFullBoundsReference().getWidth() / 2 ),
                                 strutNode.getFullBoundsReference().getMaxY() + 1 );
            equationNode.setOffset( ( maxWidth / 2 ) - ( equationNode.getFullBoundsReference().getWidth() / 2 ),
                                    titleNode.getFullBoundsReference().getMaxY() + 15 );
            saveLineButton.setOffset( ( maxWidth / 2 ) - ( saveLineButton.getFullBoundsReference().getWidth() / 2 ),
                                      equationNode.getFullBoundsReference().getMaxY() + 15 );
        }

        addChild( new ControlPanelNode( parentNode ) );

        // Save the current state of the interactive line.
        saveLineButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                eraseLinesButton.setEnabled( true );
                saveLineButton.setEnabled( false );
                lineGraphNode.saveLine( interactiveLine.get(), LGColors.SAVED_LINE ); //TODO same or different colors for saved lines?
            }
        } );

        // When the interactive line has been changed, enabled the "Save" button.
        interactiveLine.addObserver( new SimpleObserver() {
            public void update() {
                saveLineButton.setEnabled( true );
            }
        } );
    }
}
