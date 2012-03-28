// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.intro.view;

import java.awt.Font;
import java.awt.Insets;
import java.text.MessageFormat;

import javax.swing.JComponent;
import javax.swing.JLabel;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.controls.PropertyCheckBoxWithIcon;
import edu.colorado.phet.common.phetcommon.view.util.GridPanel;
import edu.colorado.phet.common.phetcommon.view.util.GridPanel.Anchor;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.ControlPanelNode;
import edu.colorado.phet.linegraphing.LGResources.Images;
import edu.colorado.phet.linegraphing.LGResources.Strings;
import edu.colorado.phet.linegraphing.LGSimSharing.UserComponents;
import edu.umd.cs.piccolo.PNode;

/**
 * Visibility controls for various features.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
class VisibilityControls extends PNode {

    private static final PhetFont TITLE_FONT = new PhetFont( Font.BOLD, 16 );
    private static final PhetFont CONTROL_FONT = new PhetFont( Font.PLAIN, 14 );

    private static final String Y_EQUALS_X = MessageFormat.format( "{0} = +1{1}",
                                                                   Strings.SYMBOL_VERTICAL_AXIS,
                                                                   Strings.SYMBOL_HORIZONTAL_AXIS );
    private static final String Y_EQUALS_NEGATIVE_X = MessageFormat.format( "{0} = -1{1}",
                                                                            Strings.SYMBOL_VERTICAL_AXIS,
                                                                            Strings.SYMBOL_HORIZONTAL_AXIS );

    public VisibilityControls( Property<Boolean> yEqualsXVisible, Property<Boolean> yEqualsNegativeXVisible,
                               Property<Boolean> pointToolVisible, Property<Boolean> riseOverRunVisible, Property<Boolean> graphLinesVisible ) {

        // components
        JComponent title = new JLabel( Strings.SHOW ) {{
            setFont( TITLE_FONT );
        }};
        JComponent pointToolCheckBox = new PropertyCheckBoxWithIcon( UserComponents.pointToolCheckBox, Strings.POINT_TOOL, CONTROL_FONT, Images.POINT_TOOL_ICON, pointToolVisible );
        JComponent riseOverRunCheckBox = new PropertyCheckBoxWithIcon( UserComponents.riseOverRunCheckBox, Strings.RISE_OVER_RUN, CONTROL_FONT, Images.RISE_OVER_RUN_ICON, riseOverRunVisible );
        JComponent graphLinesCheckBox = new PropertyCheckBoxWithIcon( UserComponents.graphLinesCheckBox, Strings.GRAPH_LINES, CONTROL_FONT, Images.GRAPH_LINES_ICON, graphLinesVisible );
        final JComponent positiveCheckBox = new PropertyCheckBoxWithIcon( UserComponents.yEqualsXCheckBox, Y_EQUALS_X, CONTROL_FONT, Images.Y_EQUALS_X_ICON, yEqualsXVisible );
        final JComponent negativeCheckBox = new PropertyCheckBoxWithIcon( UserComponents.yEqualsNegativeXCheckBox, Y_EQUALS_NEGATIVE_X, CONTROL_FONT, Images.Y_EQUALS_NEGATIVE_X_ICON, yEqualsNegativeXVisible );

        // layout
        GridPanel panel = new GridPanel();
        panel.setGridX( 0 ); // vertical
        panel.setInsets( new Insets( 2, 2, 2, 2 ) );
        panel.setAnchor( Anchor.CENTER ); // centered
        panel.add( title );
        panel.setAnchor( Anchor.WEST ); // left justified
        panel.add( pointToolCheckBox );
        panel.add( riseOverRunCheckBox );
        panel.add( graphLinesCheckBox );
        panel.add( positiveCheckBox );
        panel.add( negativeCheckBox );

        // wrap Swing in a Piccolo control panel
        addChild( new ControlPanelNode( panel ) );

        graphLinesVisible.addObserver( new VoidFunction1<Boolean>() {
            public void apply( Boolean visible ) {
                positiveCheckBox.setEnabled( visible );
                negativeCheckBox.setEnabled( visible );
            }
        } );
    }
}
