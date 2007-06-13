/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.control.developer;

import java.awt.Font;
import java.awt.Insets;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JPanel;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.LinearValueControl;
import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.LogarithmicValueControl;
import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.opticaltweezers.model.Laser;

/**
 * LaserDeveloperPanel contains developer controls for the laser model.
 * This panel is for developers only, and it is not localized.
 * 
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class LaserDeveloperPanel extends JPanel implements Observer {

    private Laser _laser;
    
    private LinearValueControl _trapForceRatioControl;
    
    public LaserDeveloperPanel( Font titleFont, Font controlFont, Laser laser ) {
        super();
        
        _laser = laser;
        _laser.addObserver( this );
        
        TitledBorder border = new TitledBorder( "Laser model" );
        border.setTitleFont( titleFont );
        this.setBorder( border );
        
        double min = laser.getTrapForceRatioRange().getMin();
        double max = laser.getTrapForceRatioRange().getMax();
        _trapForceRatioControl = new LinearValueControl( min, max, "<html>F<sub>z</sub> ratio:</html>", "0.00", "" );
        _trapForceRatioControl.setTextFieldColumns( 4 );
        _trapForceRatioControl.setFont( controlFont );
        _trapForceRatioControl.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent event ) {
                handleTrapForceRatioControl();
            }
        });
        
        // Layout
        EasyGridBagLayout layout = new EasyGridBagLayout( this );
        layout.setInsets( new Insets( 0, 0, 0, 0 ) );
        this.setLayout( layout );
        int row = 0;
        int column = 0;
        layout.addComponent( _trapForceRatioControl, row++, column );
        
        // Default state
        _trapForceRatioControl.setValue( _laser.getTrapForceRatio() );
    }
    
    public void cleanup() {
        _laser.deleteObserver( this );
    }
    
    //----------------------------------------------------------------------------
    // Event handlers
    //----------------------------------------------------------------------------
    
    private void handleTrapForceRatioControl() {
        final double value = _trapForceRatioControl.getValue();
        _laser.setTrapForceRatio( value );
    }

    //----------------------------------------------------------------------------
    // Observer implementation
    //----------------------------------------------------------------------------
    
    public void update( Observable o, Object arg ) {
        if ( o == _laser ) {
            if ( arg == Laser.PROPERTY_TRAP_FORCE_RATIO ) {
                //XXX
            }
        }
        
    }
}
