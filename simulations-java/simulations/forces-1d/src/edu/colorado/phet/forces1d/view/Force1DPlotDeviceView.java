package edu.colorado.phet.forces1d.view;

import java.awt.*;

import edu.colorado.phet.common_force1d.view.ApparatusPanel;
import edu.colorado.phet.forces1d.Forces1DModule;
import edu.colorado.phet.forces1d.common.plotdevice.PlotDeviceView;

/**
 * User: Sam Reid
 * Date: Nov 27, 2004
 * Time: 8:32:03 PM
 */
public class Force1DPlotDeviceView implements PlotDeviceView {
    private Forces1DModule module;
    private Force1DPanel force1DPanel;

    public Force1DPlotDeviceView( Forces1DModule module, Force1DPanel force1DPanel ) {
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
