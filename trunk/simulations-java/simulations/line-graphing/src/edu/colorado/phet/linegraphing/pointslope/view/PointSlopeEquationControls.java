// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.pointslope.view;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.text.MessageFormat;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.simsharing.messages.ParameterSet;
import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.common.phetcommon.util.ObservableList;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.nodes.ControlPanelNode;
import edu.colorado.phet.common.piccolophet.nodes.HTMLNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPText;
import edu.colorado.phet.common.piccolophet.nodes.TextButtonNode;
import edu.colorado.phet.common.piccolophet.nodes.ToggleButtonNode;
import edu.colorado.phet.common.piccolophet.nodes.kit.ZeroOffsetNode;
import edu.colorado.phet.linegraphing.common.LGColors;
import edu.colorado.phet.linegraphing.common.LGConstants;
import edu.colorado.phet.linegraphing.common.LGResources.Images;
import edu.colorado.phet.linegraphing.common.LGResources.Strings;
import edu.colorado.phet.linegraphing.common.LGSimSharing.ParameterKeys;
import edu.colorado.phet.linegraphing.common.LGSimSharing.UserComponents;
import edu.colorado.phet.linegraphing.common.model.StraightLine;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;

//TODO lots of duplication with SlopeInterceptEquationControls
/**
 * Control panel for interacting with a line's equation in point-slope form.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
class PointSlopeEquationControls extends PhetPNode {

    private static String TITLE = MessageFormat.format( "({0} - {1}) = {2}({3} - {4})", // (y-y1)=m(x-x1),
                                                           Strings.SYMBOL_Y,
                                                           Strings.SYMBOL_Y1,
                                                           Strings.SYMBOL_SLOPE,
                                                           Strings.SYMBOL_X,
                                                           Strings.SYMBOL_X1 );

    public PointSlopeEquationControls( final Property<Boolean> maximized,
                                       final Property<StraightLine> interactiveLine,
                                       Property<DoubleRange> riseRange,
                                       Property<DoubleRange> runRange,
                                       Property<DoubleRange> x1Range,
                                       Property<DoubleRange> y1Range,
                                       final ObservableList<StraightLine> savedLines,
                                       final Property<Boolean> linesVisible ) {

        PNode titleNode = new HTMLNode( TITLE, interactiveLine.get().color, new PhetFont( Font.BOLD, 18 ) );
        PNode minimizeMaximizeButtonNode = new ToggleButtonNode( UserComponents.equationMinimizeMaximizeButton, maximized, Images.MINIMIZE_BUTTON, Images.MAXIMIZE_BUTTON ) {
            @Override protected ParameterSet getParameterSet() {
                return super.getParameterSet().with( ParameterKeys.maximized, !maximized.get() );
            }
        };
        final PNode equationNode = new ZeroOffsetNode(new PointSlopeEquationNodeSpinners( interactiveLine, riseRange, runRange, x1Range, y1Range, LGConstants.INTERACTIVE_EQUATION_FONT ) );
        final TextButtonNode saveLineButton = new TextButtonNode( Strings.SAVE_LINE, LGConstants.CONTROL_FONT, LGColors.SAVE_LINE_BUTTON ) {{
            setUserComponent( UserComponents.saveLineButton );
        }};
        final TextButtonNode eraseLinesButton = new TextButtonNode( Strings.ERASE_LINES, LGConstants.CONTROL_FONT, LGColors.ERASE_LINES_BUTTON ) {{
            setUserComponent( UserComponents.eraseLinesButton );
            setEnabled( false );
        }};

        final PNode panelNode = new PNode();
        panelNode.addChild( titleNode );
        panelNode.addChild( minimizeMaximizeButtonNode );

        // Stuff that is hidden when minimized must be attached to this node.
        final PNode subPanelNode = new PNode();
        panelNode.addChild( subPanelNode );
        subPanelNode.addChild( equationNode );
        subPanelNode.addChild( saveLineButton );
        subPanelNode.addChild( eraseLinesButton );

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
                savedLines.add( new StraightLine( interactiveLine.get(), LGColors.SAVED_LINE_NORMAL, LGColors.SAVED_LINE_HIGHLIGHT ) );
            }
        } );

        // Erase all lines that have been saved.
        eraseLinesButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                savedLines.clear();
            }
        } );

        // Sets the enabled states of the Save and Erase buttons
        final VoidFunction0 enableButtons = new VoidFunction0() {
            public void apply() {
                saveLineButton.setEnabled( linesVisible.get() );
                eraseLinesButton.setEnabled( linesVisible.get() && ( savedLines.size() > 0 ) );
            }
        };

        // Enabled/disable buttons when saved lines are added/removed.
        final VoidFunction1<StraightLine> savedLinesChanged = new VoidFunction1<StraightLine>() {
            public void apply( StraightLine line ) {
                enableButtons.apply();
            }
        };
        savedLines.addElementAddedObserver( savedLinesChanged );
        savedLines.addElementRemovedObserver( savedLinesChanged );

        // Enable/disable buttons when visibility of lines on the graph changes.
        linesVisible.addObserver( new VoidFunction1<Boolean>() {
            public void apply( Boolean visible ) {
                enableButtons.apply();
            }
        } );
    }
}
