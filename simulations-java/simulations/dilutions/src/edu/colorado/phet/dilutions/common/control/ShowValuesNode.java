// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.dilutions.common.control;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.view.controls.PropertyCheckBox;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.dilutions.DilutionsResources;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * Control for showing values.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ShowValuesNode extends PhetPNode {
    public ShowValuesNode( Property<Boolean> valuesVisible ) {
        addChild( new PSwing( new PropertyCheckBox( DilutionsResources.Strings.SHOW_VALUES, valuesVisible ) {{
            setFont( new PhetFont( 20 ) );
        }} ) );
    }
}
