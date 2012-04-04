// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.intro.view;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Line2D;
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
import edu.colorado.phet.linegraphing.LGResources;
import edu.colorado.phet.linegraphing.LGResources.Images;
import edu.colorado.phet.linegraphing.LGResources.Strings;
import edu.colorado.phet.linegraphing.LGSimSharing.UserComponents;
import edu.colorado.phet.linegraphing.intro.model.SlopeInterceptLine;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * Control panel for interacting with the line equation.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
class EquationControls extends PhetPNode {

    private static final PhetFont EQUATION_FONT = new PhetFont( Font.BOLD, 38 );

    private static String TITLE = MessageFormat.format( "{0} = {1}{2} + {3}", // y = mx + b,
                                                           Strings.SYMBOL_Y,
                                                           Strings.SYMBOL_SLOPE,
                                                           Strings.SYMBOL_X,
                                                           Strings.SYMBOL_INTERCEPT );

    public EquationControls( final Property<SlopeInterceptLine> interactiveLine, final LineGraphNode lineGraphNode,
                             IntegerRange riseRange, IntegerRange runRange, IntegerRange interceptRange ) {

        final Property<Boolean> maximized = new Property<Boolean>( true );

        PNode titleNode = new PhetPText( TITLE, new PhetFont( Font.BOLD, 18 ) );
        PNode minimizeMaximizeButtonNode = new ToggleButtonNode( UserComponents.equationMinimizeMaximizeButton, maximized, Images.MINIMIZE_BUTTON, Images.MAXIMIZE_BUTTON );
        final PNode equationNode = new ZeroOffsetNode( new InteractiveSlopeInterceptEquationNode( interactiveLine, riseRange, runRange, interceptRange, EQUATION_FONT ) );
        PNode strutNode = new PPath( new Rectangle2D.Double( 0, 0, getFullBoundsReference().getWidth(), 1 ) );
        final TextButtonNode saveLineButton = new TextButtonNode( Strings.SAVE_LINE, LGConstants.CONTROL_FONT, LGColors.SAVE_LINE_BUTTON );
        final TextButtonNode eraseLinesButton = new TextButtonNode( Strings.ERASE_LINES, LGConstants.CONTROL_FONT, LGColors.ERASE_LINES_BUTTON ) {{
            setEnabled( false );
        }};
        final PText xEqualsZeroNode = new PhetPText( MessageFormat.format( "{0} = 0", Strings.SYMBOL_X ), EQUATION_FONT );

        final PNode parentNode = new PNode();
        parentNode.addChild( titleNode );
        parentNode.addChild( minimizeMaximizeButtonNode );
        parentNode.addChild( equationNode );
        parentNode.addChild( strutNode );
        parentNode.addChild( saveLineButton );
        parentNode.addChild( eraseLinesButton );
        parentNode.addChild( xEqualsZeroNode );

        double maxWidth = parentNode.getFullBoundsReference().getWidth();

        // Background behind title, prevents control panel from resizing when minimized
        final double backgroundHeight = Math.max( titleNode.getFullBoundsReference().getHeight(), minimizeMaximizeButtonNode.getFullBoundsReference().getHeight() ) + 6;
        PNode titleBackgroundNode = new PPath( new RoundRectangle2D.Double( 0, 0, maxWidth, backgroundHeight, 10, 10 ) ) {{
            setPaint( new Color( 0, 0, 0, 0 ) );
            setStroke( null );
        }};
        parentNode.addChild( titleBackgroundNode );
        titleBackgroundNode.moveInBackOf( titleNode );

        // horizontal separators
        final PNode titleSeparator = new PPath( new Line2D.Double( 0, 0, maxWidth, 0 ) ) {{
            setStrokePaint( new Color( 212, 212, 212 ) );
        }};
         final PNode buttonsSeparator = new PPath( new Line2D.Double( 0, 0, maxWidth, 0 ) ) {{
            setStrokePaint( new Color( 212, 212, 212 ) );
        }};
        parentNode.addChild( titleSeparator );
        parentNode.addChild( buttonsSeparator );

        // layout
        {
            strutNode.setOffset( 0, 0 );
            titleBackgroundNode.setOffset( ( maxWidth / 2 ) - ( titleBackgroundNode.getFullBoundsReference().getWidth() / 2 ),
                                 strutNode.getFullBoundsReference().getMaxY() + 1 );
            minimizeMaximizeButtonNode.setOffset( titleBackgroundNode.getFullBoundsReference().getMinY() + 5,
                                                  titleBackgroundNode.getFullBoundsReference().getCenterY() - ( minimizeMaximizeButtonNode.getFullBoundsReference().getHeight() / 2 ) );
            titleNode.setOffset( minimizeMaximizeButtonNode.getFullBoundsReference().getMaxY() + 8,
                                 titleBackgroundNode.getFullBoundsReference().getCenterY() - ( titleNode.getFullBoundsReference().getHeight() / 2 ) );
            titleSeparator.setOffset( 0, titleBackgroundNode.getFullBoundsReference().getMaxY() + 2 );
            equationNode.setOffset( ( maxWidth / 2 ) - ( equationNode.getFullBoundsReference().getWidth() / 2 ),
                                    titleSeparator.getFullBoundsReference().getMaxY() + 15 );
            xEqualsZeroNode.setOffset( equationNode.getXOffset(),
                                       equationNode.getFullBoundsReference().getCenterY() - ( xEqualsZeroNode.getFullBoundsReference().getHeight() / 2 ) );
            buttonsSeparator.setOffset( 0, equationNode.getFullBoundsReference().getMaxY() + 15 );
            saveLineButton.setOffset( ( maxWidth / 2 ) - saveLineButton.getFullBoundsReference().getWidth() - 3,
                                      buttonsSeparator.getFullBoundsReference().getMaxY() + 8 );
            eraseLinesButton.setOffset( ( maxWidth / 2 ) + 3, saveLineButton.getYOffset() );
        }

        addChild( new ControlPanelNode( parentNode ) );

        // Minimize/maximize the control panel
        maximized.addObserver( new VoidFunction1<Boolean>() {
            public void apply( Boolean maximized ) {
                if ( maximized ) {
                    //TODO put all of these things under a common parent, so visibility can be changed with 1 call
                    parentNode.addChild( titleSeparator );
                    parentNode.addChild( buttonsSeparator );
                    parentNode.addChild( equationNode );
                    parentNode.addChild( saveLineButton );
                    parentNode.addChild( eraseLinesButton );
                    parentNode.addChild( xEqualsZeroNode );
                }
                else {
                    parentNode.removeChild( titleSeparator );
                    parentNode.removeChild( buttonsSeparator );
                    parentNode.removeChild( equationNode );
                    parentNode.removeChild( saveLineButton );
                    parentNode.removeChild( eraseLinesButton );
                    parentNode.removeChild( xEqualsZeroNode );
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

        // Erase all lines that have been saved.
        eraseLinesButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                eraseLinesButton.setEnabled( false );
                saveLineButton.setEnabled( true );
                lineGraphNode.eraseLines();
            }
        } );

        // When the interactive line changes...
        interactiveLine.addObserver( new VoidFunction1<SlopeInterceptLine>() {
            public void apply( SlopeInterceptLine line ) {
                equationNode.setVisible( line.isDefined() );
                xEqualsZeroNode.setVisible( !line.isDefined() );
                saveLineButton.setEnabled( true );
            }
        } );
    }
}
