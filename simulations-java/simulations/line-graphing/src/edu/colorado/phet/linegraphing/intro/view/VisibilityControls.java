// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.intro.view;

import java.awt.Font;
import java.awt.Insets;
import java.text.MessageFormat;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JSeparator;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.controls.PropertyCheckBoxWithIcon;
import edu.colorado.phet.common.phetcommon.view.util.GridPanel;
import edu.colorado.phet.common.phetcommon.view.util.GridPanel.Anchor;
import edu.colorado.phet.common.phetcommon.view.util.GridPanel.Fill;
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
                                                                   Strings.SYMBOL_Y,
                                                                   Strings.SYMBOL_X );
    private static final String Y_EQUALS_NEGATIVE_X = MessageFormat.format( "{0} = -1{1}",
                                                                            Strings.SYMBOL_Y,
                                                                            Strings.SYMBOL_X );

    public VisibilityControls( Property<Boolean> linesVisible, Property<Boolean> riseOverRunVisible,
                               Property<Boolean> yEqualsXVisible, Property<Boolean> yEqualsNegativeXVisible,
                               Property<Boolean> pointToolVisible ) {

        // components
        JComponent title = new JLabel( Strings.SHOW ) {{
            setFont( TITLE_FONT );
        }};
        JComponent linesCheckBox = new PropertyCheckBoxWithIcon( UserComponents.linesCheckBox, Strings.LINES, CONTROL_FONT, Images.LINES_ICON, linesVisible );
        final JComponent riseOverRunCheckBox = new PropertyCheckBoxWithIcon( UserComponents.riseOverRunCheckBox, Strings.RISE_OVER_RUN, CONTROL_FONT, Images.RISE_OVER_RUN_ICON, riseOverRunVisible );
        final JComponent positiveCheckBox = new PropertyCheckBoxWithIcon( UserComponents.yEqualsXCheckBox, Y_EQUALS_X, CONTROL_FONT, Images.Y_EQUALS_X_ICON, yEqualsXVisible );
        final JComponent negativeCheckBox = new PropertyCheckBoxWithIcon( UserComponents.yEqualsNegativeXCheckBox, Y_EQUALS_NEGATIVE_X, CONTROL_FONT, Images.Y_EQUALS_NEGATIVE_X_ICON, yEqualsNegativeXVisible );
        JComponent pointToolCheckBox = new PropertyCheckBoxWithIcon( UserComponents.pointToolCheckBox, Strings.POINT_TOOL, CONTROL_FONT, PointToolNode.createImage( 45 ), pointToolVisible );

        // layout
        GridPanel panel = new GridPanel();
        panel.setGridX( 0 ); // vertical
        panel.setInsets( new Insets( 2, 2, 2, 2 ) );
        panel.setAnchor( Anchor.CENTER ); // centered
        panel.add( title );
        panel.setFill( Fill.HORIZONTAL );
        panel.add( new JSeparator() );
        panel.setFill( Fill.NONE );
        panel.setAnchor( Anchor.WEST ); // left justified
        panel.add( linesCheckBox );
        panel.add( riseOverRunCheckBox );
        panel.add( positiveCheckBox );
        panel.add( negativeCheckBox );
        panel.add( pointToolCheckBox );

        // wrap Swing in a Piccolo control panel
        addChild( new ControlPanelNode( panel ) );

        linesVisible.addObserver( new VoidFunction1<Boolean>() {
            public void apply( Boolean visible ) {
                riseOverRunCheckBox.setEnabled( visible );
                positiveCheckBox.setEnabled( visible );
                negativeCheckBox.setEnabled( visible );
            }
        } );
    }
}
