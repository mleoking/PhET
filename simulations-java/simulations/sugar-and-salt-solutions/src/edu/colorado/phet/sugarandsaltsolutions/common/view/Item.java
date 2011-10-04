// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.common.view;

import edu.colorado.phet.sugarandsaltsolutions.common.model.DispenserType;

//REVIEW needs a more descriptive name. Or why not make this class unnecessary by changing DispenserType.name to DispenserType.soluteName and adding DispenserType.name?

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
