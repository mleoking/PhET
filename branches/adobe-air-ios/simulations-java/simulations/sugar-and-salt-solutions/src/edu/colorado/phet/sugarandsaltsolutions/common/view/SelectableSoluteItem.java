// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.common.view;

import edu.colorado.phet.sugarandsaltsolutions.common.model.DispenserType;

/**
 * Item to be displayed in a DispenserRadioButtonSet
 *
 * @author Sam Reid
 */
public class SelectableSoluteItem {
    public final String name;
    public final DispenserType dispenserType;

    public SelectableSoluteItem( String name, DispenserType dispenserType ) {
        this.name = name;
        this.dispenserType = dispenserType;
    }
}