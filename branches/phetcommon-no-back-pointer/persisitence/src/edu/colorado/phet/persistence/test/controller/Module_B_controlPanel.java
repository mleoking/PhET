/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.persistence.test.controller;

import edu.colorado.phet.common.view.ControlPanel;
import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.persistence.test.Module_B;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/**
 * Module_B_controlPanel
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class Module_B_controlPanel extends ControlPanel {

    public Module_B_controlPanel( Module_B module ) {
        super( module );

        JButton addButton = new JButton( "Add particle" );
        add( addButton );
        addButton.addActionListener( new ParticleAdder( module ) );
    }

    ///////////////////////////////////////////
    // Inner classes
    //
    private class ParticleAdder implements ActionListener {
        private Module_B module;

        public ParticleAdder( Module_B module ) {
            this.module = module;
        }

        public void actionPerformed( ActionEvent e ) {
            module.addParticle();
        }
    }
}
