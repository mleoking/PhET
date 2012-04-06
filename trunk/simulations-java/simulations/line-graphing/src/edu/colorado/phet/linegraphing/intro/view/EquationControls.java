// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.intro.view;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.text.MessageFormat;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.simsharing.messages.ParameterSet;
import edu.colorado.phet.common.phetcommon.util.IntegerRange;
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
import edu.colorado.phet.linegraphing.LGSimSharing.ParameterKeys;
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
        PNode minimizeMaximizeButtonNode = new ToggleButtonNode( UserComponents.equationMinimizeMaximizeButton, maximized, Images.MINIMIZE_BUTTON, Images.MAXIMIZE_BUTTON ) {
            @Override protected ParameterSet getParameterSet() {
                return super.getParameterSet().with( ParameterKeys.maximized, !maximized.get() );
            }
        };
        final PNode equationNode = new ZeroOffsetNode( new InteractiveEquationNode( interactiveLine, riseRange, runRange, interceptRange, EQUATION_FONT ) );
        final TextButtonNode saveLineButton = new TextButtonNode( Strings.SAVE_LINE, LGConstants.CONTROL_FONT, LGColors.SAVE_LINE_BUTTON ) {{
            setUserComponent( UserComponents.saveLineButton );
        }};
        final TextButtonNode eraseLinesButton = new TextButtonNode( Strings.ERASE_LINES, LGConstants.CONTROL_FONT, LGColors.ERASE_LINES_BUTTON ) {{
            setUserComponent( UserComponents.eraseLinesButton );
            setEnabled( false );
        }};
        final PText xEqualsZeroNode = new PhetPText( MessageFormat.format( "{0} = 0", Strings.SYMBOL_X ), EQUATION_FONT );

        final PNode panelNode = new PNode();
        panelNode.addChild( titleNode );
        panelNode.addChild( minimizeMaximizeButtonNode );

        // Stuff that is hidden when minimized must be attached to this node.
        final PNode subPanelNode = new PNode();
        panelNode.addChild( subPanelNode );
        subPanelNode.addChild( equationNode );
        subPanelNode.addChild( saveLineButton );
        subPanelNode.addChild( eraseLinesButton );
        subPanelNode.addChild( xEqualsZeroNode );

        // horizontal separators
        double maxWidth = panelNode.getFullBoundsReference().getWidth();
        final PNode titleSeparator = new PPath( new Line2D.Double( 0, 0, maxWidth, 0 ) ) {{
            setStrokePaint( new Color( 212, 212, 212 ) );
        }};
         final PNode buttonsSeparator = new PPath( new Line2D.Double( 0, 0, maxWidth, 0 ) ) {{
            setStrokePaint( new Color( 212, 212, 212 ) );
        }};
        subPanelNode.addChild( titleSeparator );
        subPanelNode.addChild( buttonsSeparator );

        // Horizontal strut, to prevent control panel from resizing when minimized.
        PPath strutNode = new PPath( new Rectangle2D.Double( 0, 0, panelNode.getFullBoundsReference().getWidth() + 4, 1 ) );
        strutNode.setStroke( null );
        panelNode.addChild( strutNode );
        strutNode.moveToBack();

        // layout
        {
            final double ySpacing = 10;
            final double titleHeight = Math.max( titleNode.getFullBoundsReference().getHeight(), minimizeMaximizeButtonNode.getFullBoundsReference().getHeight() );
            strutNode.setOffset( 0, 0 );
            minimizeMaximizeButtonNode.setOffset( 5, ( titleHeight - minimizeMaximizeButtonNode.getFullBoundsReference().getHeight() ) / 2 );
            titleNode.setOffset( minimizeMaximizeButtonNode.getFullBoundsReference().getMaxX() + 8,
                                 ( titleHeight - titleNode.getFullBoundsReference().getHeight() ) / 2 );
            titleSeparator.setOffset( 0, titleHeight + ySpacing );
            equationNode.setOffset( ( maxWidth / 2 ) - ( equationNode.getFullBoundsReference().getWidth() / 2 ),
                                    titleSeparator.getFullBoundsReference().getMaxY() + ySpacing );
            xEqualsZeroNode.setOffset( equationNode.getFullBoundsReference().getCenterX() - ( xEqualsZeroNode.getFullBoundsReference().getWidth() / 2 ),
                                       equationNode.getFullBoundsReference().getCenterY() - ( xEqualsZeroNode.getFullBoundsReference().getHeight() / 2 ) );
            buttonsSeparator.setOffset( 0, equationNode.getFullBoundsReference().getMaxY() + ySpacing );
            saveLineButton.setOffset( ( maxWidth / 2 ) - saveLineButton.getFullBoundsReference().getWidth() - 3,
                                      buttonsSeparator.getFullBoundsReference().getMaxY() + ySpacing );
            eraseLinesButton.setOffset( ( maxWidth / 2 ) + 3, saveLineButton.getYOffset() );
        }

        // Wrap everything in a Piccolo control panel
        addChild( new ControlPanelNode( panelNode ) );

        // Minimize/maximize the control panel
        maximized.addObserver( new VoidFunction1<Boolean>() {
            public void apply( Boolean maximized ) {
                if ( maximized ) {
                    panelNode.addChild( subPanelNode );
                }
                else {
                    panelNode.removeChild( subPanelNode );
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
