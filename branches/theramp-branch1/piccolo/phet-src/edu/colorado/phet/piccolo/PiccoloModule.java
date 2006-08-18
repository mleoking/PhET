/* Copyright 2004, Sam Reid */
package edu.colorado.phet.piccolo;

import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.model.clock.AbstractClock;

import javax.swing.*;

/**
 * User: Sam Reid
 * Date: Sep 15, 2005
 * Time: 4:34:00 PM
 * Copyright (c) Sep 15, 2005 by Sam Reid
 */

public class PiccoloModule extends Module {
    private PhetPCanvas phetPCanvas;

    /**
     * @param name
     * @param clock
     */
    public PiccoloModule( String name, AbstractClock clock ) {
        super( name, clock );
    }

    public PhetPCanvas getPhetPCanvas() {
        return phetPCanvas;
    }

    public void setPhetPCanvas( PhetPCanvas phetPCanvas ) {
        this.phetPCanvas = phetPCanvas;
    }

    public boolean moduleIsWellFormed() {
        boolean result = true;
        result &= this.getModel() != null;
        result &= this.getPhetPCanvas() != null;
        return result;
    }

    protected void handleUserInput() {
//        getApparatusPanel().handleUserInput();
    }

    public JComponent getSimulationPanel() {
        return getPhetPCanvas();
    }

    public void refresh() {
//        super.refresh();
    }
}
