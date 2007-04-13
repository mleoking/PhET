/** Sam Reid*/
package edu.colorado.phet.forces1d.view;

import edu.colorado.phet.common_force1d.view.ApparatusPanel;
import edu.colorado.phet.forces1d.Force1DApplication;
import edu.colorado.phet.forces1d.common.plotdevice.PlotDeviceView;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: Nov 27, 2004
 * Time: 8:32:03 PM
 * Copyright (c) Nov 27, 2004 by Sam Reid
 */
public class Force1DPlotDeviceView implements PlotDeviceView {
    private Force1DApplication module;
    private Force1DPanel force1DPanel;

    public Force1DPlotDeviceView( Force1DApplication module, Force1DPanel force1DPanel ) {
        this.module = module;
        this.force1DPanel = force1DPanel;
    }

    public void relayout() {
        module.relayoutPlots();
    }

    public Color getBackgroundColor() {
        return Color.white;
    }

    public ApparatusPanel getApparatusPanel() {
        return force1DPanel;
    }

    public void repaintBackground() {
        module.relayoutPlots();
    }

    public void repaintBackground( Rectangle viewBounds ) {
        module.relayoutPlots();
    }

//    public BufferedPhetGraphic getBackground() {
//        return force1DPanel.getBufferedGraphic();
//    }
}
