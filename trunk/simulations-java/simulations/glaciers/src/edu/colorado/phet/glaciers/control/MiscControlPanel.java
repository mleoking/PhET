/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.glaciers.control;

import java.awt.Frame;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;

import edu.colorado.phet.common.phetcommon.application.Module;
import edu.colorado.phet.common.phetcommon.view.ResetAllButton;
import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;
import edu.colorado.phet.glaciers.GlaciersStrings;
import edu.colorado.phet.glaciers.dialog.GlacierPictureDialog;
import edu.colorado.phet.glaciers.model.Glacier;
import edu.colorado.phet.glaciers.model.Glacier.GlacierAdapter;

/**
 * MiscControlPanel contains miscellaneous controls.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class MiscControlPanel extends JPanel {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private final Glacier _glacier;
    private final Frame _dialogOwner;
    
    private final JButton _pictureButton;
    private final JButton _steadyStateButton;
    private final ResetAllButton _resetAllButton;
    private final HelpButton _helpButton;
    
    private JDialog _glacierPictureDialog;
    private boolean _glacierPictureDialogWasOpen;
    private Point _glacierPictureDialogLocation;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public MiscControlPanel( Glacier glacier, Frame dialogOwner, final Module module ) {
        super();
        
        _dialogOwner = dialogOwner;
        
        _glacier = glacier;
        glacier.addGlacierListener( new GlacierAdapter() {
            public void steadyStateChanged() {
                setSteadyStateButtonEnabled( !_glacier.isSteadyState() );
            }
        } );
        
        _pictureButton = new JButton( GlaciersStrings.BUTTON_GLACIER_PICTURE );
        _pictureButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                handlePictureButton();
            }
        } );
        
        _steadyStateButton = new JButton( GlaciersStrings.BUTTON_STEADY_STATE );
        _steadyStateButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                _glacier.setSteadyState();
            }
        });
        
        _resetAllButton = new ResetAllButton( module, _dialogOwner );
        
        _helpButton = new HelpButton( module );
        
        EasyGridBagLayout layout = new EasyGridBagLayout( this );
        setLayout( layout );
        int column = 0;
        layout.addComponent( _pictureButton, 0, column++ );
        layout.addComponent( _steadyStateButton, 0, column++ );
        layout.addComponent( _resetAllButton, 0, column++ );
        if ( module.hasHelp() ) {
            layout.addComponent( _helpButton, 0, column++ );
        }
    }
    
    //----------------------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------------------
    
    public void setSteadyStateButtonEnabled( boolean enabled ) {
        _steadyStateButton.setEnabled( enabled );
    }
    
    public void setHelpEnabled( boolean enabled ) {
        _helpButton.setHelpEnabled( enabled );
    }
    
    /**
     *  For attaching Help items.
     */
    public JComponent getSteadyStateButton() {
        return _steadyStateButton;
    }
    
    public void activate() {
        if ( _glacierPictureDialogWasOpen ) {
            handlePictureButton();
        }
    }
    
    public void deactivate() {
        _glacierPictureDialogWasOpen = ( _glacierPictureDialog != null );
        if ( _glacierPictureDialog != null ) {
            _glacierPictureDialog.dispose();
        }
    }
    
    //----------------------------------------------------------------------------
    // Event handlers
    //----------------------------------------------------------------------------
    
    private void handlePictureButton() {
        if ( _glacierPictureDialog == null ) {
            
            _glacierPictureDialog = new GlacierPictureDialog( _dialogOwner );
            
            if ( _glacierPictureDialogLocation != null ) {
                // open the dialog where it was last
                _glacierPictureDialog.setLocation( _glacierPictureDialogLocation );
            }
            else {
                // center in parent
                SwingUtils.centerDialogInParent( _glacierPictureDialog );
            }
            
            _glacierPictureDialog.addWindowListener( new WindowAdapter() {
                // called when the close button in the dialog's window dressing is clicked
                public void windowClosing( WindowEvent e ) {
                    _glacierPictureDialogLocation = _glacierPictureDialog.getLocation();
                    _glacierPictureDialog.dispose();
                    _glacierPictureDialog = null;
                }
            } );
            
            _glacierPictureDialog.setVisible( true );
        }
    }
}
