package edu.colorado.phet.forces1d.common.plotdevice;

import edu.colorado.phet.common_force1d.view.ApparatusPanel;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: Nov 27, 2004
 * Time: 8:23:13 PM
 *
 */
public interface PlotDeviceView {
    void relayout();

    Color getBackgroundColor();

    ApparatusPanel getApparatusPanel();

    void repaintBackground();

    void repaintBackground( Rectangle viewBounds );

//    BufferedPhetGraphic getBackground();

}
