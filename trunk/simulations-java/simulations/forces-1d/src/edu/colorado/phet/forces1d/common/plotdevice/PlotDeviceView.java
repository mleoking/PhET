// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.forces1d.common.plotdevice;

import java.awt.*;

import edu.colorado.phet.common.phetgraphics.view.ApparatusPanel;


/**
 * User: Sam Reid
 * Date: Nov 27, 2004
 * Time: 8:23:13 PM
 */
public interface PlotDeviceView {
    void relayout();

    Color getBackgroundColor();

    ApparatusPanel getApparatusPanel();

    void repaintBackground();

    void repaintBackground( Rectangle viewBounds );

}
