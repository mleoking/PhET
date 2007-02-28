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

import edu.colorado.phet.molecularreactions.model.MRModel;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * ResetAllAction
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class ResetAllAction extends AbstractAction {
    private MRModel model;

    public ResetAllAction( MRModel model ) {
        this.model = model;
    }

    public void actionPerformed( ActionEvent e ) {
        
    }
}
