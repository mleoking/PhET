/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.davissongermer;

import javax.swing.*;
import java.awt.*;

/**
 * User: Sam Reid
 * Date: Feb 5, 2006
 * Time: 2:40:30 PM
 * Copyright (c) Feb 5, 2006 by Sam Reid
 */

public class DGPlotFrame extends JDialog {
    private DGPlotPanel dgPlotPanel;

    public DGPlotFrame( Frame owner, DGModule dgModule ) {
        super( owner, "Intensity Plot", false );
        dgPlotPanel = new DGPlotPanel( dgModule );
        setContentPane( dgPlotPanel );
        pack();
    }

    public DGPlotPanel getDgPlotPanel() {
        return dgPlotPanel;
    }
}
