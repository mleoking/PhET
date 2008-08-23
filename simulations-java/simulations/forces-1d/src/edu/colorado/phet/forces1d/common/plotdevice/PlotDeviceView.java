package edu.colorado.phet.forces1d.common.plotdevice;

import java.awt.*;

import edu.colorado.phet.forces1d.common_force1d.view.ApparatusPanel;

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

//    BufferedPhetGraphic getBackground();

}
