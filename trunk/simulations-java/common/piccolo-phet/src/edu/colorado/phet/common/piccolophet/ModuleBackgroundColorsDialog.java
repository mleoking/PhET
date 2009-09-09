package edu.colorado.phet.common.piccolophet;

import java.awt.Color;

import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.application.Module;
import edu.colorado.phet.common.phetcommon.application.PaintImmediateDialog;
import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.phetcommon.view.controls.ColorControl;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;


public class ModuleBackgroundColorsDialog extends PaintImmediateDialog {
    
    private PhetApplication app;
    private final ColorControl colorControl;

    public ModuleBackgroundColorsDialog( PhetApplication app ) {
        super( app.getPhetFrame() );
        
        this.app = app;
        
        colorControl = new ColorControl( app.getPhetFrame(), "control panel background color: ", Color.WHITE );
        colorControl.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent event ) {
                setControlPanelBackground( colorControl.getColor() );
            }
        } );
        
        JPanel panel = new JPanel();
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
