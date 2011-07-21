// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.micro.view;

import edu.colorado.phet.sugarandsaltsolutions.common.model.DispenserType;

/**
 * Item to be displayed in a DispenserRadioButtonSet
 *
 * @author Sam Reid
 */
public class Item {
    public final String name;
    public final DispenserType dispenserType;

    public Item( String name, DispenserType dispenserType ) {
        this.name = name;
        this.dispenserType = dispenserType;
    }
}
