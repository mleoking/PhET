package edu.colorado.phet.forces1d.common.plotdevice;

import edu.colorado.phet.common.view.ApparatusPanel;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: Nov 27, 2004
 * Time: 8:23:13 PM
 * Copyright (c) Nov 27, 2004 by Sam Reid
 */
public interface PlotDeviceView {
    void relayout();

    Color getBackgroundColor();

    ApparatusPanel getApparatusPanel();

    void repaintBackground();

    void repaintBackground( Rectangle viewBounds );

//    BufferedPhetGraphic getBackground();

}
