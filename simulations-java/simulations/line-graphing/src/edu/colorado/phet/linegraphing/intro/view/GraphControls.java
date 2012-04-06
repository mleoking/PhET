// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.intro.view;

import java.awt.Font;
import java.awt.Insets;
import java.text.MessageFormat;

import javax.swing.JComponent;
import javax.swing.JPanel;

import edu.colorado.phet.common.phetcommon.model.property.Property;
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

    public GraphControls( final InteractiveLineGraphNode graphNode ) {

        // components
        final JComponent linesCheckBox = new PropertyCheckBox( UserComponents.linesCheckBox, Strings.HIDE_LINES,
                                                               // wrapper to map between "visible" and "hidden"
                                                               new Property<Boolean>( !graphNode.linesVisible.get() ) {{
                                                                   graphNode.linesVisible.addObserver( new VoidFunction1<Boolean>() {
                                                                       public void apply( Boolean aBoolean ) {

                                                                       }
                                                                   } );
                                                                   addObserver( new VoidFunction1<Boolean>() {
                                                                       public void apply( Boolean hidden ) {
                                                                           graphNode.linesVisible.set( !hidden );
                                                                       }
                                                                   } );
                                                               }} );
        linesCheckBox.setFont( CONTROL_FONT );
        final JComponent slopeCheckBox = new PropertyCheckBoxWithIcon( riseOverRunCheckBox, Strings.SLOPE, CONTROL_FONT, BracketValueNode.createIcon( 30 ), graphNode.riseOverRunVisible );
        final JComponent positiveCheckBox = new PropertyCheckBoxWithIcon( yEqualsXCheckBox, Y_EQUALS_X, CONTROL_FONT, LineGraphNode.createYEqualsXIcon( 60 ), graphNode.yEqualsXVisible );
        final JComponent negativeCheckBox = new PropertyCheckBoxWithIcon( yEqualsNegativeXCheckBox, Y_EQUALS_NEGATIVE_X, CONTROL_FONT, LineGraphNode.createYEqualsNegativeXIcon( 60 ), graphNode.yEqualsNegativeXVisible );
        JComponent pointToolCheckBox = new PropertyCheckBoxWithIcon( UserComponents.pointToolCheckBox, Strings.POINT_TOOL, CONTROL_FONT, PointToolNode.createIcon( 45 ), graphNode.pointToolVisible );

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
        panel.add( pointToolCheckBox );

        // wrap Swing in a Piccolo control panel
        addChild( new ControlPanelNode( panel ) );
    }
}
