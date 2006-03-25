/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.quantumtunneling.debug;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;

import edu.colorado.phet.quantumtunneling.control.RichardsonControlsDialog;
import edu.colorado.phet.quantumtunneling.model.RichardsonSolver;
import edu.colorado.phet.quantumtunneling.module.QTModule;


/**
 * QTDeveloperMenu is a menu of developer-only features.
 * This menu is not localized.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class QTDeveloperMenu extends JMenu implements ActionListener {
    
    //----------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------
    
    private QTModule _module;
    
    private JCheckBoxMenuItem _richardsonControlsItem;
    private RichardsonControlsDialog _richardsonControlsDialog;
    
    //----------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------
    
    public QTDeveloperMenu( QTModule module ) {
        super( "Developer" );
        setMnemonic( 'D' );
        
        _module = module;
        
        // Richardson Controls...
        if ( _module.getRichardsonSolver() instanceof RichardsonSolver ) {
            _richardsonControlsItem = new JCheckBoxMenuItem( "Richardson Controls..." );
            _richardsonControlsItem.setMnemonic( 'R' );
            _richardsonControlsItem.addActionListener( this );
            add( _richardsonControlsItem );
        }
    }
    
    //----------------------------------------------------------------------
    // Event handling
    //----------------------------------------------------------------------
    
    public void actionPerformed( ActionEvent event ) {
        
        // Richardson Controls
        if ( event.getSource() == _richardsonControlsItem ) {
            if ( _richardsonControlsItem.isSelected() ) {
                RichardsonSolver solver = _module.getRichardsonSolver();
                if ( solver != null ) {
                    _richardsonControlsDialog = new RichardsonControlsDialog( _module.getFrame(), solver );
                    _richardsonControlsDialog.show();
                    _richardsonControlsDialog.addWindowListener( new WindowAdapter() {
                        public void windowClosing( WindowEvent event ) {
                            handleRichardsonDialogClosed();
                        }
                        public void windowClosed( WindowEvent event ) {
                            handleRichardsonDialogClosed();
                        }
                    } );
                }
            }
            else {
                if ( _richardsonControlsDialog != null ) {
                    _richardsonControlsDialog.dispose();
                }
            }
        }
    }
    
    private void handleRichardsonDialogClosed() {
        _richardsonControlsDialog = null;
        _richardsonControlsItem.setSelected( false );
    }
}
