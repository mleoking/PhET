// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.micro.view;

import edu.colorado.phet.sugarandsaltsolutions.common.view.barchart.BarItem;

/**
 * A kit the user can choose from, for showing the appropriate bars in the concentration bar chart.  Other information about kits is contained in the MicroModel.selectedKit and its dependencies
 *
 * @author Sam Reid
 */
public class MicroKit {

    //Bars to be shown in the concentration bar chart
    public final BarItem[] barItems;

    public MicroKit( BarItem... barItems ) {
        this.barItems = barItems;
    }
}