package edu.colorado.phet.movingman.force1d_orig.common.plotdevice;

import java.awt.*;

import edu.colorado.phet.movingman.force1d_orig.common_force1d.view.ApparatusPanel;

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
