/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.photoelectric.view;

import edu.colorado.phet.common.view.ApparatusPanel2;
import edu.colorado.phet.common.model.clock.AbstractClock;

import javax.swing.*;
import java.awt.*;

/**
 * GraphWindow
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class GraphWindow extends JDialog {
    private CurrentVsVoltageGraph currentVsVoltageGraph;

    public GraphWindow( Frame frame, Component component, AbstractClock clock ) {
        super( frame, false );
        ApparatusPanel2 graphPanel = new ApparatusPanel2( clock );
        graphPanel.setUseOffscreenBuffer( true );
        currentVsVoltageGraph = new CurrentVsVoltageGraph( graphPanel );
        graphPanel.setPreferredSize( new Dimension( 300, 200 ) );
        setContentPane( graphPanel );
        currentVsVoltageGraph.setLocation( (int)( graphPanel.getPreferredSize().getWidth() - currentVsVoltageGraph.getWidth()) / 2,
                                           (int)( graphPanel.getPreferredSize().getHeight() - currentVsVoltageGraph.getHeight()) / 2);
        graphPanel.addGraphic( currentVsVoltageGraph );
        pack();
    }

    public CurrentVsVoltageGraph getCurrentVsVoltageGraph() {
        return currentVsVoltageGraph;
    }
}
