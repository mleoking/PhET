/* Copyright 2005-2009, University of Colorado */

package edu.colorado.phet.quantumtunneling.debug;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JCheckBoxMenuItem;

import edu.colorado.phet.quantumtunneling.control.RichardsonControlsDialog;
import edu.colorado.phet.quantumtunneling.model.RichardsonSolver;
import edu.colorado.phet.quantumtunneling.module.QTModule;


/**
 * Menu item that provides access to developer controls for the Richardson algorithm.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class RichardsonControlsMenuItem extends JCheckBoxMenuItem {
    
    //----------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------
    
    private QTModule _module;
    private RichardsonControlsDialog dialog;
    
    //----------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------
    
    public RichardsonControlsMenuItem( QTModule module ) {
        super( "Richardson Controls..." );
        
        _module = module;
        
        addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                handleAction();
            }
        });
    }
    
    //----------------------------------------------------------------------
    // Event handling
    //----------------------------------------------------------------------
    
    private void handleAction() {
            if ( isSelected() ) {
                RichardsonSolver solver = _module.getRichardsonSolver();
                if ( solver != null ) {
                    dialog = new RichardsonControlsDialog( _module.getFrame(), solver );
                    dialog.setVisible( true );
                    dialog.addWindowListener( new WindowAdapter() {
                        public void windowClosing( WindowEvent event ) {
                            handleDialogClosed();
                        }
                        public void windowClosed( WindowEvent event ) {
                            handleDialogClosed();
                        }
                    } );
                }
            }
            else {
                if ( dialog != null ) {
                    dialog.dispose();
                }
            }
    }
    
    private void handleDialogClosed() {
        dialog = null;
        setSelected( false );
    }
}
