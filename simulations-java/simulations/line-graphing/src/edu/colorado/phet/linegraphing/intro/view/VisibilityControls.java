// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.intro.view;

import java.awt.Font;
import java.awt.Insets;
import java.text.MessageFormat;

import javax.swing.JComponent;
import javax.swing.JLabel;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.model.property.SettableProperty;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;
import edu.colorado.phet.common.phetcommon.view.controls.PropertyCheckBox;
import edu.colorado.phet.common.phetcommon.view.util.GridPanel;
import edu.colorado.phet.common.phetcommon.view.util.GridPanel.Anchor;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.ControlPanelNode;
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
        JComponent positiveCheckBox = new VisibilityCheckBox( UserComponents.yEqualsXCheckBox, Y_EQUALS_X, yEqualsXVisible );
        JComponent negativeCheckBox = new VisibilityCheckBox( UserComponents.yEqualsNegativeXCheckBox, Y_EQUALS_NEGATIVE_X, yEqualsNegativeXVisible );
        JComponent pointToolCheckBox = new VisibilityCheckBox( UserComponents.pointToolCheckBox, Strings.POINT_TOOL, pointToolVisible );
        JComponent riseOverRunCheckBox = new VisibilityCheckBox( UserComponents.riseOverRunCheckBox, Strings.RISE_OVER_RUN, riseOverRunVisible );
        JComponent graphLinesCheckBox = new VisibilityCheckBox( UserComponents.graphLinesCheckBox, Strings.GRAPH_LINES, graphLinesVisible );

        GridPanel panel = new GridPanel();
        panel.setGridX( 0 ); // vertical
        panel.setInsets( new Insets( 5, 5, 5, 5 ) );
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

    private static class VisibilityCheckBox extends PropertyCheckBox {
        public VisibilityCheckBox( IUserComponent userComponent, final String text, final SettableProperty<Boolean> property ) {
            super( userComponent, text, property );
            setFont( CONTROL_FONT );
        }
    }
}
