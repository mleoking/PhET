// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.intro.view;

import java.awt.Insets;
import java.text.MessageFormat;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.view.controls.PropertyCheckBox;
import edu.colorado.phet.common.phetcommon.view.util.GridPanel;
import edu.colorado.phet.common.piccolophet.nodes.ControlPanelNode;
import edu.colorado.phet.linegraphing.LGConstants;
import edu.colorado.phet.linegraphing.LGResources.Strings;
import edu.colorado.phet.linegraphing.LGSimSharing.UserComponents;
import edu.umd.cs.piccolo.PNode;

/**
 * Visibility controls for various features.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
class VisibilityControls extends PNode {

    private static final String Y_EQUALS_X = MessageFormat.format( "{0} = +1{1}",
                                                                   Strings.SYMBOL_VERTICAL_AXIS,
                                                                   Strings.SYMBOL_HORIZONTAL_AXIS );
    private static final String Y_EQUALS_NEGATIVE_X = MessageFormat.format( "{0} = -1{1}",
                                                                            Strings.SYMBOL_VERTICAL_AXIS,
                                                                            Strings.SYMBOL_HORIZONTAL_AXIS );

    public VisibilityControls( Property<Boolean> yEqualsXVisible, Property<Boolean> yEqualsNegativeXVisible ) {

        //TODO add "Show" title

        //TODO add icons

        PropertyCheckBox positiveCheckBox = new PropertyCheckBox( UserComponents.yEqualsXCheckBox, Y_EQUALS_X, yEqualsXVisible );
        PropertyCheckBox negativeCheckBox = new PropertyCheckBox( UserComponents.yEqualsNegativeXCheckBox, Y_EQUALS_NEGATIVE_X, yEqualsNegativeXVisible );

        positiveCheckBox.setFont( LGConstants.CONTROL_FONT );
        negativeCheckBox.setFont( LGConstants.CONTROL_FONT );

        GridPanel panel = new GridPanel();
        panel.setGridX( 0 ); // vertical
        panel.setInsets( new Insets( 5, 5, 5, 5 ) );
        panel.add( positiveCheckBox );
        panel.add( negativeCheckBox );

        addChild( new ControlPanelNode( panel ) );
    }
}
