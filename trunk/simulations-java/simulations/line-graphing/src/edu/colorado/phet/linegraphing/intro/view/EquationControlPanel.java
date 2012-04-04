// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.intro.view;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.text.MessageFormat;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.IntegerRange;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.nodes.ControlPanelNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPText;
import edu.colorado.phet.common.piccolophet.nodes.TextButtonNode;
import edu.colorado.phet.common.piccolophet.nodes.ToggleButtonNode;
import edu.colorado.phet.common.piccolophet.nodes.kit.ZeroOffsetNode;
import edu.colorado.phet.linegraphing.LGColors;
import edu.colorado.phet.linegraphing.LGConstants;
import edu.colorado.phet.linegraphing.LGResources.Images;
import edu.colorado.phet.linegraphing.LGResources.Strings;
import edu.colorado.phet.linegraphing.LGSimSharing.UserComponents;
import edu.colorado.phet.linegraphing.intro.model.SlopeInterceptLine;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;

/**
 * Control panel for interacting with the line equation.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
class EquationControlPanel extends PhetPNode {

    // y = mx + b
    private static String TITLE = MessageFormat.format( "{0} = {1}{2} + {3}", // eg, y = mx + b,
                                                           Strings.SYMBOL_Y,
                                                           Strings.SYMBOL_SLOPE,
                                                           Strings.SYMBOL_X,
                                                           Strings.SYMBOL_INTERCEPT );

    public EquationControlPanel( final Property<SlopeInterceptLine> interactiveLine, final LineGraphNode lineGraphNode, final TextButtonNode eraseLinesButton,
                                 IntegerRange riseRange, IntegerRange runRange, IntegerRange interceptRange ) {

        final Property<Boolean> maximized = new Property<Boolean>( true );

        PNode titleNode = new PhetPText( TITLE, new PhetFont( Font.BOLD, 18 ) );
        PNode minimizeMaximizeButtonNode = new ToggleButtonNode( UserComponents.equationMinimizeMaximizeButton, maximized, Images.MINIMIZE_BUTTON, Images.MAXIMIZE_BUTTON );
        final PNode equationNode = new ZeroOffsetNode( new SlopeInterceptEquationNodeOld( interactiveLine, riseRange, runRange, interceptRange ) );
        PNode strutNode = new PPath( new Rectangle2D.Double( 0, 0, getFullBoundsReference().getWidth(), 1 ) );
        final TextButtonNode saveLineButton = new TextButtonNode( Strings.SAVE_LINE, LGConstants.CONTROL_FONT, LGColors.SAVE_LINE_BUTTON );

        final PNode parentNode = new PNode();
        parentNode.addChild( titleNode );
        parentNode.addChild( minimizeMaximizeButtonNode );
        parentNode.addChild( equationNode );
        parentNode.addChild( strutNode );
        parentNode.addChild( saveLineButton );

        double maxWidth = parentNode.getFullBoundsReference().getWidth();

        // Background behind title, prevents control panel from resizing when minimized
        final double backgroundHeight = Math.max( titleNode.getFullBoundsReference().getHeight(), minimizeMaximizeButtonNode.getFullBoundsReference().getHeight() ) + 6;
        PNode titleBackgroundNode = new PPath( new RoundRectangle2D.Double( 0, 0, maxWidth, backgroundHeight, 10, 10 ) ) {{
            setPaint( new Color( 0, 0, 0, 0 ) );
            setStroke( null );
        }};
        parentNode.addChild( titleBackgroundNode );
        titleBackgroundNode.moveInBackOf( titleNode );

        // layout
        {
            strutNode.setOffset( 0, 0 );
            titleBackgroundNode.setOffset( ( maxWidth / 2 ) - ( titleBackgroundNode.getFullBoundsReference().getWidth() / 2 ),
                                 strutNode.getFullBoundsReference().getMaxY() + 1 );
            minimizeMaximizeButtonNode.setOffset( titleBackgroundNode.getFullBoundsReference().getMinY() + 5,
                                                  titleBackgroundNode.getFullBoundsReference().getCenterY() - ( minimizeMaximizeButtonNode.getFullBoundsReference().getHeight() / 2 ) );
            titleNode.setOffset( minimizeMaximizeButtonNode.getFullBoundsReference().getMaxY() + 8,
                                 titleBackgroundNode.getFullBoundsReference().getCenterY() - ( titleNode.getFullBoundsReference().getHeight() / 2 ) );
            equationNode.setOffset( ( maxWidth / 2 ) - ( equationNode.getFullBoundsReference().getWidth() / 2 ),
                                    titleNode.getFullBoundsReference().getMaxY() + 15 );
            saveLineButton.setOffset( ( maxWidth / 2 ) - ( saveLineButton.getFullBoundsReference().getWidth() / 2 ),
                                      equationNode.getFullBoundsReference().getMaxY() + 15 );
        }

        addChild( new ControlPanelNode( parentNode ) );

        // Minimize/maximize the control panel
        maximized.addObserver( new VoidFunction1<Boolean>() {
            public void apply( Boolean maximized ) {
                if ( maximized ) {
                    parentNode.addChild( equationNode );
                    parentNode.addChild( saveLineButton );
                }
                else {

                    parentNode.removeChild( equationNode );
                    parentNode.removeChild( saveLineButton );
                }
            }
        } );

        // Save the current state of the interactive line.
        saveLineButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                eraseLinesButton.setEnabled( true );
                saveLineButton.setEnabled( false );
                lineGraphNode.saveLine( interactiveLine.get() );
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
