package edu.colorado.phet.cck.piccolo_cck;

import edu.colorado.phet.cck.model.CCKModel;

import javax.swing.*;

/**
 * User: Sam Reid
 * Date: Sep 14, 2006
 * Time: 11:15:22 AM
 * Copyright (c) Sep 14, 2006 by Sam Reid
 */

public class CCKSimulationPanel extends JComponent {
    private CCKModel model;

    public CCKSimulationPanel( CCKModel model ) {
        this.model = model;
    }
}
