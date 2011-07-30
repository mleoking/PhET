// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.micro.view;

import edu.colorado.phet.sugarandsaltsolutions.common.view.barchart.BarItem;

/**
 * A kit the user can choose from, for showing the appropriate bar charts + controls
 *
 * @author Sam Reid
 */
public class Kit {
    private BarItem[] barItems;

    public Kit( BarItem... barItems ) {
        this.barItems = barItems;
    }

    public BarItem[] getBars() {
        return barItems;
    }
}
