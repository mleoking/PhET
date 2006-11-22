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
import edu.colorado.phet.molecularreactions.modules.SimpleModule;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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
