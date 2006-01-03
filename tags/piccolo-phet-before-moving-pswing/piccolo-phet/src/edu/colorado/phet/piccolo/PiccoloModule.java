/* Copyright 2003-2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.piccolo;

import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.model.clock.IClock;

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
    public PiccoloModule( String name, IClock clock ) {
        super( name, clock );
    }

    public PiccoloModule( String name, IClock clock, boolean startsPaused ) {
        super( name, clock, startsPaused );
    }

    public PhetPCanvas getPhetPCanvas() {
        return phetPCanvas;
    }

    public void setPhetPCanvas( PhetPCanvas phetPCanvas ) {
        this.phetPCanvas = phetPCanvas;
        super.setSimulationPanel( phetPCanvas );
    }

    public boolean isWellFormed() {
        boolean result = true;
        result &= this.getModel() != null;
        result &= this.getPhetPCanvas() != null;
        return result;
    }

    protected void handleUserInput() {
//        getApparatusPanel().handleUserInput();
    }
}