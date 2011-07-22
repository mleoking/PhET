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
    private Property<DispenserType> dispenserType;
    private Item[] items;

    public DispenserRadioButtonSet( final Property<DispenserType> dispenserType, final Item... items ) {
        super( new VerticalLayoutPanel() {{
            for ( Item item : items ) {
                add( new PropertyRadioButton<DispenserType>( item.name, dispenserType, item.dispenserType ) {{setFont( CONTROL_FONT );}} );
            }
        }} );
        this.dispenserType = dispenserType;
        this.items = items;
    }

    //When switching to a new kit, switch to a dispenser that is in the set (if not already selecting it).  If switching from a set that contains NaCl to a new set that also contains NaCl, then keep the selection
    public void setSelected() {
        if ( !containsDispenser() ) {
            dispenserType.set( items[0].dispenserType );
        }
    }

    //Determine whether the currently selected dispenser is one of the choices in this kit
    private boolean containsDispenser() {
        boolean isDispenserAvailable = false;
        for ( Item item : items ) {
            if ( item.dispenserType == dispenserType.get() ) {
                isDispenserAvailable = true;
            }
        }
        return isDispenserAvailable;
    }
}