/** Sam Reid*/
package edu.colorado.phet.forces1d.view;

import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.forces1d.Force1DModule;
import edu.colorado.phet.forces1d.common.plotdevice.PlotDeviceView;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: Nov 27, 2004
 * Time: 8:32:03 PM
 * Copyright (c) Nov 27, 2004 by Sam Reid
 */
public class Force1DPlotDeviceView implements PlotDeviceView {
    private Force1DModule module;
    private Force1DPanel force1DPanel;

    public Force1DPlotDeviceView( Force1DModule module, Force1DPanel force1DPanel ) {
        this.module = module;
        this.force1DPanel = force1DPanel;
    }

    public void relayout() {
        module.relayout();
    }

    public Color getBackgroundColor() {
        return Color.white;
    }

    public ApparatusPanel getApparatusPanel() {
        return force1DPanel;
    }

    public void repaintBackground() {
    }

    public void repaintBackground( Rectangle viewBounds ) {
    }

//    public BufferedPhetGraphic getBackground() {
//        return force1DPanel.getBufferedGraphic();
//    }
}
