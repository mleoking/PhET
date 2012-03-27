// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.intro.view;

import java.awt.Font;
import java.awt.Image;
import java.awt.Insets;
import java.text.MessageFormat;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.model.property.SettableProperty;
import edu.colorado.phet.common.phetcommon.simsharing.components.SimSharingIcon;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.common.phetcommon.view.controls.PropertyCheckBox;
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

        //TODO add "Show" title

        //TODO add icons

        JComponent title = new JLabel( Strings.SHOW ) {{
            setFont( TITLE_FONT );
        }};
        JComponent positiveCheckBox = new CheckBoxWithIcon( UserComponents.yEqualsXCheckBox, Y_EQUALS_X, Images.Y_EQUALS_X_ICON, yEqualsXVisible );
        JComponent negativeCheckBox = new CheckBoxWithIcon( UserComponents.yEqualsNegativeXCheckBox, Y_EQUALS_NEGATIVE_X, Images.Y_EQUALS_NEGATIVE_X_ICON, yEqualsNegativeXVisible );
        JComponent pointToolCheckBox = new CheckBoxWithIcon( UserComponents.pointToolCheckBox, Strings.POINT_TOOL, Images.POINT_TOOL_ICON, pointToolVisible );
        JComponent riseOverRunCheckBox = new CheckBoxWithIcon( UserComponents.riseOverRunCheckBox, Strings.RISE_OVER_RUN, Images.RISE_OVER_RUN_ICON, riseOverRunVisible );
        JComponent graphLinesCheckBox = new CheckBoxWithIcon( UserComponents.graphLinesCheckBox, Strings.GRAPH_LINES, Images.GRAPH_LINES_ICON, graphLinesVisible );

        GridPanel panel = new GridPanel();
        panel.setGridX( 0 ); // vertical
        panel.setInsets( new Insets( 2, 2, 2, 2 ) );
        panel.setAnchor( Anchor.CENTER ); // centered
        panel.add( title );
        panel.setAnchor( Anchor.WEST ); // left justified
        panel.add( positiveCheckBox );
        panel.add( negativeCheckBox );
        panel.add( pointToolCheckBox );
        panel.add( riseOverRunCheckBox );
        panel.add( graphLinesCheckBox );

        addChild( new ControlPanelNode( panel ) );
    }

    // Check box with associated icon. Clicking either the check box or icon toggles the property.
    private static class CheckBoxWithIcon extends JPanel {
        public CheckBoxWithIcon( IUserComponent userComponent, final String text, Image image, final SettableProperty<Boolean> property ) {
            add( new PropertyCheckBox( userComponent, text, property ) {{
                setFont( CONTROL_FONT );
            }} );
            add( new SimSharingIcon( userComponent, new ImageIcon( image ), new VoidFunction0() {
                public void apply() {
                    property.set( !property.get() );
                }
            } ) );
        }
    }
}
