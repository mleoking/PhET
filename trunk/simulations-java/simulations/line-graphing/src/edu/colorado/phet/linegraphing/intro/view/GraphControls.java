// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.intro.view;

import java.awt.Font;
import java.awt.Insets;
import java.text.MessageFormat;

import javax.swing.JComponent;
import javax.swing.JPanel;

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
import edu.colorado.phet.common.piccolophet.nodes.ControlPanelNode;
import edu.colorado.phet.linegraphing.LGResources.Strings;
import edu.colorado.phet.linegraphing.LGSimSharing.UserComponents;
import edu.colorado.phet.linegraphing.intro.model.SlopeInterceptLine.StandardLine;
import edu.umd.cs.piccolo.PNode;

import static edu.colorado.phet.linegraphing.LGSimSharing.UserComponents.*;

/**
 * Control for various features related to the graph.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
class GraphControls extends PNode {

    private static final PhetFont CONTROL_FONT = new PhetFont( Font.PLAIN, 14 );
    private static final String Y_EQUALS_X = MessageFormat.format( "{0} = {1}", Strings.SYMBOL_Y, Strings.SYMBOL_X );
    private static final String Y_EQUALS_NEGATIVE_X = MessageFormat.format( "{0} = -{1}", Strings.SYMBOL_Y, Strings.SYMBOL_X );

    public GraphControls( final LineGraphNode graphNode, final ObservableList<StandardLine> standardLines ) {

        final Property<Boolean> yEqualsXVisible = new Property<Boolean>( standardLines.contains( StandardLine.Y_EQUALS_X_LINE ) );
        final Property<Boolean> yEqualsNegativeXVisible = new Property<Boolean>( standardLines.contains( StandardLine.Y_EQUALS_NEGATIVE_X_LINE ) );

        // components
        final JComponent linesCheckBox = new PropertyCheckBox( UserComponents.linesCheckBox, Strings.HIDE_LINES, new SettableNot( graphNode.linesVisible ) );
        linesCheckBox.setFont( CONTROL_FONT );
        final JComponent positiveCheckBox = new PropertyCheckBoxWithIcon( yEqualsXCheckBox, Y_EQUALS_X, CONTROL_FONT, LineGraphNode.createYEqualsXIcon( 60 ), yEqualsXVisible );
        final JComponent negativeCheckBox = new PropertyCheckBoxWithIcon( yEqualsNegativeXCheckBox, Y_EQUALS_NEGATIVE_X, CONTROL_FONT, LineGraphNode.createYEqualsNegativeXIcon( 60 ), yEqualsNegativeXVisible );
        final JComponent slopeCheckBox = new PropertyCheckBoxWithIcon( riseOverRunCheckBox, Strings.SLOPE, CONTROL_FONT, RiseRunBracketNode.createIcon( 60 ), graphNode.slopeVisible );

        // layout
        GridPanel panel = new GridPanel();
        panel.setGridX( 0 ); // vertical
        panel.setInsets( new Insets( 2, 2, 2, 2 ) );
        panel.setAnchor( Anchor.CENTER ); // centered
        panel.setFill( Fill.NONE );
        panel.setAnchor( Anchor.WEST ); // left justified
        panel.add( new JPanel() {{ add( linesCheckBox ); }} ); // wrap with JPanel to get same horizontal alignment as PropertyCheckBoxWithIcon
        panel.add( positiveCheckBox );
        panel.add( negativeCheckBox );
        panel.add( slopeCheckBox );

        // wrap Swing in a Piccolo control panel
        addChild( new ControlPanelNode( panel ) );

        // when lines are not visible, hide related controls
        graphNode.linesVisible.addObserver( new VoidFunction1<Boolean>() {
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
                    standardLines.add( StandardLine.Y_EQUALS_X_LINE );
                }
                else {
                    standardLines.remove( StandardLine.Y_EQUALS_X_LINE );
                }
            }
        } );

        // Add/remove standard line "y = -x"
        yEqualsNegativeXVisible.addObserver( new VoidFunction1<Boolean>() {
            public void apply( Boolean visible ) {
                if ( visible ) {
                    standardLines.add( StandardLine.Y_EQUALS_NEGATIVE_X_LINE );
                }
                else {
                    standardLines.remove( StandardLine.Y_EQUALS_NEGATIVE_X_LINE );
                }
            }
        } );

        // Check boxes when standard lines are added
        standardLines.addElementAddedObserver( new VoidFunction1<StandardLine>() {
            public void apply( StandardLine line ) {
                if ( line == StandardLine.Y_EQUALS_X_LINE ) {
                    yEqualsXVisible.set( true );
                }
                else if ( line == StandardLine.Y_EQUALS_NEGATIVE_X_LINE ) {
                    yEqualsNegativeXVisible.set( true );
                }
            }
        } );

        // Uncheck boxes when standard lines are removed
        standardLines.addElementRemovedObserver( new VoidFunction1<StandardLine>() {
            public void apply( StandardLine line ) {
                if ( line == StandardLine.Y_EQUALS_X_LINE ) {
                    yEqualsXVisible.set( false );
                }
                else if ( line == StandardLine.Y_EQUALS_NEGATIVE_X_LINE ) {
                    yEqualsNegativeXVisible.set( false );
                }
            }
        } );
    }
}
