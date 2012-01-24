// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.dilutions.common.control;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.view.controls.simsharing.SimSharingPropertyCheckBox;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.dilutions.DilutionsResources.Strings;
import edu.colorado.phet.dilutions.DilutionsSimSharing.UserComponents;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * Control for showing/hiding values.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ShowValuesNode extends PNode {

    public ShowValuesNode( Property<Boolean> valuesVisible ) {
        addChild( new PSwing( new SimSharingPropertyCheckBox( UserComponents.showValuesCheckBox, Strings.SHOW_VALUES, valuesVisible ) {{
            setFont( new PhetFont( 18 ) );
            setOpaque( false );
        }} ) );
    }
}
