/* Copyright 2004, Sam Reid */
package edu.colorado.phet.ec3;

import edu.colorado.phet.ec3.plots.Range2D;
import edu.colorado.phet.ec3.plots.TimePlotSuitePNode;
import edu.colorado.phet.piccolo.PhetPCanvas;

/**
 * User: Sam Reid
 * Date: Oct 23, 2005
 * Time: 3:12:06 PM
 * Copyright (c) Oct 23, 2005 by Sam Reid
 */

public class ChartCanvas extends PhetPCanvas {
    private EC3Module ec3Module;

    public ChartCanvas( EC3Module ec3Module ) {
        this.ec3Module = ec3Module;
        TimePlotSuitePNode plot = new TimePlotSuitePNode( this,
                                                          new Range2D( 0, 0, 100, 100 ), "Name",
                                                          "units", ec3Module.getTimeSeriesModel(), 200, false );
        addScreenChild( plot );
    }
}
