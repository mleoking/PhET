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
 * Control for choosing standard equations.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
class StandardEquationsControl extends PNode {

    private static final String UNIT_POSITIVE_EQUATION = MessageFormat.format( "{0} = +1{1}",
                                                                      Strings.SYMBOL_VERTICAL_AXIS,
                                                                      Strings.SYMBOL_HORIZONTAL_AXIS );
    private static final String UNIT_NEGATIVE_EQUATION = MessageFormat.format( "{0} = -1{1}",
                                                                      Strings.SYMBOL_VERTICAL_AXIS,
                                                                      Strings.SYMBOL_HORIZONTAL_AXIS );

    public StandardEquationsControl( Property<Boolean> unitPositiveSlopeVisible, Property<Boolean> unitNegativeSlopeVisible ) {

        //TODO add "Show" title

        //TODO add icons for each standard line

        PropertyCheckBox positiveCheckBox = new PropertyCheckBox( UserComponents.unitPositiveSlopeCheckBox, UNIT_POSITIVE_EQUATION, unitPositiveSlopeVisible );
        PropertyCheckBox negativeCheckBox = new PropertyCheckBox( UserComponents.unitNegativeSlopeCheckBox, UNIT_NEGATIVE_EQUATION, unitNegativeSlopeVisible );

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
