package edu.colorado.phet.testlocation.control;

import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.view.ControlPanel;
import edu.colorado.phet.testlocation.view.DebuggerGraphic;

/**
 * TestControlPanel is the control panel for the "Test Location" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class TestControlPanel extends ControlPanel {
    
    public TestControlPanel( Module module, final DebuggerGraphic debugger ) {
        super( module );  
        
        // Draw Registration Points check box
        final JCheckBox enableRegistrationPointsCheckBox = new JCheckBox( "Draw Registration Points" );
        enableRegistrationPointsCheckBox.setSelected( debugger.isLocationEnabled() );
        enableRegistrationPointsCheckBox.addChangeListener( new ChangeListener() {
            
            public void stateChanged( ChangeEvent e ) {
                debugger.setLocationEnabled( enableRegistrationPointsCheckBox.isSelected() );
            }
        } );
        
        // Draw Bounds check box
        final JCheckBox enableBoundsCheckBox = new JCheckBox( "Draw Bounds" );
        enableBoundsCheckBox.setSelected( debugger.isBoundsEnabled() );
        enableBoundsCheckBox.addChangeListener( new ChangeListener() {
            
            public void stateChanged( ChangeEvent e ) {
                debugger.setBoundsEnabled( enableBoundsCheckBox.isSelected() );
            }
        } );
        
        // Panel
        JPanel panel = new JPanel();
        panel.setLayout( new BoxLayout( panel, BoxLayout.Y_AXIS ) );
        panel.add( enableRegistrationPointsCheckBox );
        panel.add( enableBoundsCheckBox );
        super.add( panel );
    }
}