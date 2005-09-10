/* Copyright 2004, Sam Reid */
package edu.colorado.phet.theramp;

import edu.colorado.phet.theramp.view.RampPanel;

/**
 * User: Sam Reid
 * Date: Aug 9, 2005
 * Time: 1:25:55 AM
 * Copyright (c) Aug 9, 2005 by Sam Reid
 */

public class SimpleRampPanel extends RampPanel {
    private SimpleRampModule simpleRampModule;

    public SimpleRampPanel( SimpleRampModule simpleRampModule ) {
        super( simpleRampModule );
        this.simpleRampModule = simpleRampModule;

        getRampPlotSet().minimizeAllPlots();
        setAllBarsMinimized( true );
        addWiggleMe();
        super.maximizeForcePlot();
    }

}
