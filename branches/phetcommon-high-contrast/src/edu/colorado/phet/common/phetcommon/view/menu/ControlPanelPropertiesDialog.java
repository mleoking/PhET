package edu.colorado.phet.common.phetcommon.view.menu;

import java.awt.Color;

import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.application.Module;
import edu.colorado.phet.common.phetcommon.application.PaintImmediateDialog;
import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.phetcommon.view.controls.ColorControl;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;

/**
 * Controls for properties related to a Module's standard control panels.
 * These are developer controls and are therefore not internationalized.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ControlPanelPropertiesDialog extends PaintImmediateDialog {
    
    private PhetApplication app;
    private final ColorControl colorControl;

    public ControlPanelPropertiesDialog( PhetApplication app ) {
        super( app.getPhetFrame() );
        setTitle( "Control Panel properties" );
        
        this.app = app;
        
        colorControl = new ColorControl( app.getPhetFrame(), "background color: ", Color.WHITE );
        colorControl.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent event ) {
                setControlPanelBackground( colorControl.getColor() );
            }
        } );
        
        JPanel panel = new JPanel();
        panel.setBorder( new EmptyBorder( 5, 5, 5, 5 ) );
        panel.add( colorControl );
        
        setContentPane( panel );
        pack();
        SwingUtils.centerDialogInParent( this );
    }
    
    @Override 
    public void setVisible( boolean visible ) {
        if ( app.getModules().length > 0 ) {
            Module module = app.getModule( 0 );
            if ( module.getControlPanel() != null ) {
                colorControl.setColor( module.getControlPanel().getBackground() );
            }
        }
        super.setVisible( visible );
    }
    
    private void setControlPanelBackground( Color color ) {
        Module[] modules = app.getModules();
        for ( int i = 0; i < modules.length; i++ ) {
            modules[i].setControlPanelBackground( color );
            modules[i].setClockControlPanelBackground( color );
            modules[i].setHelpPanelBackground( color );
        }
    }
}
