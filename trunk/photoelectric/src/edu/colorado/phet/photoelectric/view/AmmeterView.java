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

import edu.colorado.phet.photoelectric.model.Ammeter;
import edu.colorado.phet.photoelectric.model.util.ScalarDataRecorder;
import edu.colorado.phet.common.application.PhetApplication;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/**
 * AmmeterView
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class AmmeterView extends JDialog {

    private Ammeter ammeter;
    private JTextField currentTF;

    public AmmeterView( JFrame frame, Ammeter ammeter ) {
        super( frame, false );
        JPanel currentPanel = new JPanel( new GridLayout( 1, 2 ));
        currentPanel.add( new JLabel( "Current: "));
        currentTF = new JTextField( 10 );
        currentPanel.add( currentTF );
        this.getContentPane().add( currentPanel );
        this.pack();

        this.ammeter = ammeter;
        this.ammeter.addUpdateListener( new ScalarDataRecorder.UpdateListener() {
            public void update( ScalarDataRecorder.UpdateEvent event ) {
                double current = AmmeterView.this.ammeter.getCurrent();
                currentTF.setText( Double.toString( current ));
            }
        } );
    }
}
