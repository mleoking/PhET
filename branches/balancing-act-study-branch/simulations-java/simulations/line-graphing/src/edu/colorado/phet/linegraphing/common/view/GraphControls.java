// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.common.view;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.text.MessageFormat;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.model.property.SettableNot;
import edu.colorado.phet.common.phetcommon.util.ObservableList;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.controls.PropertyCheckBox;
import edu.colorado.phet.common.phetcommon.view.controls.PropertyCheckBoxWithIcon;
import edu.colorado.phet.common.phetcommon.view.util.GridPanel;
import edu.colorado.phet.common.phetcommon.view.util.GridPanel.Anchor;
import edu.colorado.phet.common.phetcommon.view.util.GridPanel.Fill;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.ControlPanelNode;
import edu.colorado.phet.linegraphing.common.LGColors;
import edu.colorado.phet.linegraphing.common.LGResources.Strings;
import edu.colorado.phet.linegraphing.common.LGSimSharing.UserComponents;
import edu.colorado.phet.linegraphing.common.model.Line;
import edu.umd.cs.piccolo.PNode;

import static edu.colorado.phet.linegraphing.common.LGSimSharing.UserComponents.*;

/**
 * Controls for various features related to the graph.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class GraphControls extends PNode {

    private static final PhetFont CONTROL_FONT = new PhetFont( Font.PLAIN, 14 );
    private static final String Y_EQUALS_X = MessageFormat.format( "{0} = {1}", Strings.SYMBOL_Y, Strings.SYMBOL_X );
    private static final String Y_EQUALS_NEGATIVE_X = MessageFormat.format( "{0} = -{1}", Strings.SYMBOL_Y, Strings.SYMBOL_X );

    // Use this constructor for a control panel with all features.
    public GraphControls( final Property<Boolean> linesVisible, final Property<Boolean> slopeVisible, final ObservableList<Line> standardLines ) {
        this( linesVisible, slopeVisible, standardLines, true );
    }

    // Use this constructor for a control panel without check boxes standard lines.
    public GraphControls( final Property<Boolean> linesVisible, final Property<Boolean> slopeVisible ) {
        this( linesVisible, slopeVisible, new ObservableList<Line>(), false );
    }

    /**
     * Constructor
     *
     * @param linesVisible         are lines visible on the graph?
     * @param slopeVisible         are the slope (rise/run) brackets visible on the graphed line?
     * @param standardLines        standard lines (eg, y=x) that are available for viewing
     * @param includeStandardLines should check boxes for standard lines be accessible?
     */
    private GraphControls( final Property<Boolean> linesVisible, final Property<Boolean> slopeVisible, final ObservableList<Line> standardLines, boolean includeStandardLines ) {

        // private properties for standard-line check boxes
        final Property<Boolean> yEqualsXVisible = new Property<Boolean>( standardLines.contains( Line.Y_EQUALS_X_LINE ) );
        final Property<Boolean> yEqualsNegativeXVisible = new Property<Boolean>( standardLines.contains( Line.Y_EQUALS_NEGATIVE_X_LINE ) );

        // components
        final JComponent hideLinesCheckBox = new PropertyCheckBox( UserComponents.linesCheckBox, Strings.HIDE_LINES, new SettableNot( linesVisible ) );
        hideLinesCheckBox.setFont( CONTROL_FONT );
        final JComponent positiveCheckBox = new PropertyCheckBoxWithIcon( yEqualsXCheckBox, Y_EQUALS_X, CONTROL_FONT, GraphNode.createYEqualsXIcon( 60, LGColors.Y_EQUALS_X ), yEqualsXVisible );
        final JComponent negativeCheckBox = new PropertyCheckBoxWithIcon( yEqualsNegativeXCheckBox, Y_EQUALS_NEGATIVE_X, CONTROL_FONT, GraphNode.createYEqualsNegativeXIcon( 60, LGColors.Y_EQUALS_NEGATIVE_X ), yEqualsNegativeXVisible );
        final JComponent slopeCheckBox = new PropertyCheckBoxWithIcon( riseOverRunCheckBox, Strings.SLOPE, CONTROL_FONT, SlopeToolNode.createIcon( 60 ), slopeVisible );

        // layout
        GridPanel panel = new GridPanel();
        panel.setGridX( 0 ); // vertical
        panel.setInsets( new Insets( 2, 2, 2, 2 ) );
        panel.setAnchor( Anchor.CENTER ); // centered
        panel.setFill( Fill.NONE );
        panel.setAnchor( Anchor.WEST ); // left justified
        panel.add( slopeCheckBox );
        if ( includeStandardLines ) {
            panel.add( positiveCheckBox );
            panel.add( negativeCheckBox );
        }
        panel.add( new JPanel() {{ add( hideLinesCheckBox ); }} ); // wrap with JPanel to get same horizontal alignment as PropertyCheckBoxWithIcon

        // wrap Swing in a Piccolo control panel
        addChild( new ControlPanelNode( panel, LGColors.GRAPH_CONTROL_PANEL ) );

        // when lines are not visible, hide related controls
        linesVisible.addObserver( new VoidFunction1<Boolean>() {
            public void apply( Boolean visible ) {
                slopeCheckBox.setEnabled( visible );
                positiveCheckBox.setEnabled( visible );
                negativeCheckBox.setEnabled( visible );
            }
        } );

        // Add/remove standard line "y = x"
        yEqualsXVisible.addObserver( new VoidFunction1<Boolean>() {
            public void apply( Boolean visible ) {
                if ( visible ) {
                    standardLines.add( Line.Y_EQUALS_X_LINE );
                }
                else {
                    standardLines.remove( Line.Y_EQUALS_X_LINE );
                }
            }
        } );

        // Add/remove standard line "y = -x"
        yEqualsNegativeXVisible.addObserver( new VoidFunction1<Boolean>() {
            public void apply( Boolean visible ) {
                if ( visible ) {
                    standardLines.add( Line.Y_EQUALS_NEGATIVE_X_LINE );
                }
                else {
                    standardLines.remove( Line.Y_EQUALS_NEGATIVE_X_LINE );
                }
            }
        } );

        // Select appropriate check boxes when standard lines are added.
        standardLines.addElementAddedObserver( new VoidFunction1<Line>() {
            public void apply( Line line ) {
                if ( line == Line.Y_EQUALS_X_LINE ) {
                    yEqualsXVisible.set( true );
                }
                else if ( line == Line.Y_EQUALS_NEGATIVE_X_LINE ) {
                    yEqualsNegativeXVisible.set( true );
                }
            }
        } );

        // Deselect appropriate check boxes when standard lines are removed.
        standardLines.addElementRemovedObserver( new VoidFunction1<Line>() {
            public void apply( Line line ) {
                if ( line == Line.Y_EQUALS_X_LINE ) {
                    yEqualsXVisible.set( false );
                }
                else if ( line == Line.Y_EQUALS_NEGATIVE_X_LINE ) {
                    yEqualsNegativeXVisible.set( false );
                }
            }
        } );
    }

    // test
    public static void main( String[] args ) {

        final Property<Boolean> linesVisible = new Property<Boolean>( true );
        final Property<Boolean> slopeVisible = new Property<Boolean>( true );
        final ObservableList<Line> standardLines = new ObservableList<Line>();

        PNode panel1 = new GraphControls( linesVisible, slopeVisible, standardLines );
        PNode panel2 = new GraphControls( linesVisible, slopeVisible );

        PhetPCanvas canvas = new PhetPCanvas();
        canvas.setPreferredSize( new Dimension( 300, 600 ) );
        canvas.getLayer().addChild( panel1 );
        canvas.getLayer().addChild( panel2 );

        panel1.setOffset( 50, 50 );
        panel2.setOffset( panel1.getXOffset(), panel1.getFullBoundsReference().getMaxY() + 20 );

        JFrame frame = new JFrame();
        frame.setContentPane( canvas );
        frame.pack();
        frame.setDefaultCloseOperation( WindowConstants.EXIT_ON_CLOSE );
        frame.setVisible( true );
    }
}
