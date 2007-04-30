/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.dialog;

import java.awt.Font;
import java.awt.Frame;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.LinearValueControl;
import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;
import edu.colorado.phet.opticaltweezers.OTConstants;
import edu.colorado.phet.opticaltweezers.OTResources;
import edu.colorado.phet.opticaltweezers.model.Bead;
import edu.colorado.phet.opticaltweezers.module.PhysicsModule;

/**
 * Developer controls for PhysicsModule.
 * These controls will not be available to the user, and are not localized.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class PhysicsDeveloperDialog extends JDialog {
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final Font TITLE_FONT = new Font( OTConstants.DEFAULT_FONT_NAME, Font.BOLD, 14 );
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private PhysicsModule _module;
    
    private LinearValueControl _brownianMotionScaleControl;
   
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public PhysicsDeveloperDialog( Frame owner, PhysicsModule module ) {
        super( owner );
        setTitle( OTResources.getString( "title.physicsOfTweezers" ) + " Developer Controls");
        
        _module = module;
        
        JPanel inputPanel = createInputPanel();
        
        VerticalLayoutPanel panel = new VerticalLayoutPanel();
        panel.setFillHorizontal();
        panel.add( inputPanel );
        
        setContentPane( panel );
        setResizable( false );
        pack();
        SwingUtils.centerDialogInParent( this );
    }
    
    private JPanel createInputPanel() {
        
        JPanel beadMotionPanel = new JPanel();
        {
            TitledBorder titledBorder = new TitledBorder( "Bead motion algorithm" );
            titledBorder.setTitleFont( TITLE_FONT );
            beadMotionPanel.setBorder( titledBorder );
            
            _brownianMotionScaleControl = new LinearValueControl( 0, 5, "Brownian motion scale:", "0.00", "" );
            
            EasyGridBagLayout layout = new EasyGridBagLayout( beadMotionPanel );
            beadMotionPanel.setLayout( layout );
            int row = 0;
            int column = 0;
            layout.addComponent( _brownianMotionScaleControl, row++, column );
        }
        
        // Layout
        JPanel panel = new JPanel();
        panel.setBorder( new EmptyBorder( 5, 5, 5, 5 ) );
        EasyGridBagLayout layout = new EasyGridBagLayout( panel );
        layout.setInsets( new Insets( 3, 5, 3, 5 ) );
        panel.setLayout( layout );
        int row = 0;
        int column = 0;
        {
           layout.addComponent( beadMotionPanel, row++, column );
        }
        
        // Event handling
        EventListener listener = new EventListener();
        _brownianMotionScaleControl.addChangeListener( listener );
        
        // Default values
        _brownianMotionScaleControl.setValue( _module.getPhysicsModel().getBead().getBrownianMotionScale() );
        
        return panel;
    }
    
    //----------------------------------------------------------------------------
    // Event handling
    //----------------------------------------------------------------------------
    
    private class EventListener extends MouseAdapter implements ChangeListener {
        
        public EventListener() {}

        public void stateChanged( ChangeEvent event ) {
            Object source = event.getSource();
            if ( source == _brownianMotionScaleControl ) {
                handleBrownianMotionScaleControl();
            }
            else {
                throw new UnsupportedOperationException( "unsupported ChangeEvent source: " + source );
            }
        }
        
        public void mouseClicked( MouseEvent event ) {
            Object source = event.getSource();
//            else {
//                throw new UnsupportedOperationException( "unsupported MouseEvent source: " + source );
//            }
        }
    }
    
    private void handleBrownianMotionScaleControl() {
        double value = _brownianMotionScaleControl.getValue();
        Bead bead = _module.getPhysicsModel().getBead();
        bead.setBrownianMotionScale( value );
    }
}
