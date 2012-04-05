// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.intro.view;

import java.awt.Font;
import java.awt.Insets;
import java.text.MessageFormat;

import javax.swing.JComponent;

import edu.colorado.phet.common.phetcommon.view.controls.PropertyCheckBoxWithIcon;
import edu.colorado.phet.common.phetcommon.view.util.GridPanel;
import edu.colorado.phet.common.phetcommon.view.util.GridPanel.Anchor;
import edu.colorado.phet.common.phetcommon.view.util.GridPanel.Fill;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.ControlPanelNode;
import edu.colorado.phet.linegraphing.LGResources.Strings;
import edu.colorado.phet.linegraphing.LGSimSharing.UserComponents;
import edu.umd.cs.piccolo.PNode;

/**
 * Control for various features related to the graph.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
class GraphControls extends PNode {

    private static final PhetFont CONTROL_FONT = new PhetFont( Font.PLAIN, 14 );
    private static final String Y_EQUALS_X = MessageFormat.format( "{0} = +1{1}", Strings.SYMBOL_Y, Strings.SYMBOL_X );
    private static final String Y_EQUALS_NEGATIVE_X = MessageFormat.format( "{0} = -1{1}", Strings.SYMBOL_Y, Strings.SYMBOL_X );

    public GraphControls( InteractiveLineGraphNode graphNode ) {

        // components
        JComponent linesCheckBox = new PropertyCheckBoxWithIcon( UserComponents.linesCheckBox, Strings.LINES, CONTROL_FONT, InteractiveLineGraphNode.createLinesIcons( 40 ), graphNode.linesVisible );
        final JComponent riseOverRunCheckBox = new PropertyCheckBoxWithIcon( UserComponents.riseOverRunCheckBox, Strings.RISE_OVER_RUN, CONTROL_FONT, BracketValueNode.createIcon( 30 ), graphNode.riseOverRunVisible );
        final JComponent positiveCheckBox = new PropertyCheckBoxWithIcon( UserComponents.yEqualsXCheckBox, Y_EQUALS_X, CONTROL_FONT, InteractiveLineGraphNode.createYEqualsXIcon( 40 ), graphNode.yEqualsXVisible );
        final JComponent negativeCheckBox = new PropertyCheckBoxWithIcon( UserComponents.yEqualsNegativeXCheckBox, Y_EQUALS_NEGATIVE_X, CONTROL_FONT, InteractiveLineGraphNode.createYEqualsNegativeXIcon( 40 ), graphNode.yEqualsNegativeXVisible );
        JComponent pointToolCheckBox = new PropertyCheckBoxWithIcon( UserComponents.pointToolCheckBox, Strings.POINT_TOOL, CONTROL_FONT, PointToolNode.createIcon( 45 ), graphNode.pointToolVisible );

        // layout
        GridPanel panel = new GridPanel();
        panel.setGridX( 0 ); // vertical
        panel.setInsets( new Insets( 2, 2, 2, 2 ) );
        panel.setAnchor( Anchor.CENTER ); // centered
        panel.setFill( Fill.NONE );
        panel.setAnchor( Anchor.WEST ); // left justified
        panel.add( linesCheckBox );
        panel.add( positiveCheckBox );
        panel.add( negativeCheckBox );
        panel.add( riseOverRunCheckBox );
        panel.add( pointToolCheckBox );

        // wrap Swing in a Piccolo control panel
        addChild( new ControlPanelNode( panel ) );
    }
}
