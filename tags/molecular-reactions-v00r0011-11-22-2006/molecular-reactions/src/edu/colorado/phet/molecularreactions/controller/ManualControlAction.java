/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.molecularreactions.controller;

import edu.colorado.phet.molecularreactions.modules.MRModule;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * ManualControlAction
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class ManualControlAction extends AbstractAction {

    private MRModule module;

    public ManualControlAction( MRModule module ) {
        this.module = module;
    }

    public void actionPerformed( ActionEvent e ) {
        module.setManualControl( true );
    }
}
