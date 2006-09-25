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

import edu.colorado.phet.molecularreactions.modules.SimpleModule;
import edu.colorado.phet.molecularreactions.modules.MRModule;
import edu.colorado.phet.common.application.Module;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/**
 * RunAction
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class RunAction implements ActionListener {
    private MRModule module;

    public RunAction( SimpleModule module ) {
        this.module = module;
    }

    public void actionPerformed( ActionEvent e ) {
        module.getClock().start();        
    }
}
