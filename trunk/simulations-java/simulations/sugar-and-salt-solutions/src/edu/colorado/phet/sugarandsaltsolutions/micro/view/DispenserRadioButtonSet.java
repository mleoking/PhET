// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.micro.view;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.colorado.phet.common.phetcommon.view.controls.PropertyRadioButton;
import edu.colorado.phet.sugarandsaltsolutions.common.model.DispenserType;
import edu.umd.cs.piccolox.pswing.PSwing;

import static edu.colorado.phet.sugarandsaltsolutions.common.view.SugarAndSaltSolutionsCanvas.CONTROL_FONT;

/**
 * Radio buttons to choose between dispensers and hence solutes
 *
 * @author Sam Reid
 */
public class DispenserRadioButtonSet extends PSwing {
    public DispenserRadioButtonSet( final Property<DispenserType> dispenserType, final Item... items ) {
        super( new VerticalLayoutPanel() {{
            for ( Item item : items ) {
                add( new PropertyRadioButton<DispenserType>( item.name, dispenserType, item.dispenserType ) {{setFont( CONTROL_FONT );}} );
            }
        }} );
    }
}