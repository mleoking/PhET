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
import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.opticaltweezers.OTConstants;
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
    private LinearValueControl _electricFieldScaleControl;
    
    public LaserDeveloperPanel( Font titleFont, Font controlFont, Laser laser ) {
        super();
        
        _laser = laser;
        _laser.addObserver( this );
        
        TitledBorder border = new TitledBorder( "Laser" );
        border.setTitleFont( titleFont );
        this.setBorder( border );
        
        double min = laser.getTrapForceRatioRange().getMin();
        double max = laser.getTrapForceRatioRange().getMax();
        _trapForceRatioControl = new LinearValueControl( min, max, "<html>F<sub>z</sub>/F<sub>x</sub>&nbsp;=</html>", "0.000", "" );
        _trapForceRatioControl.setTextFieldColumns( 4 );
        _trapForceRatioControl.setFont( controlFont );
        _trapForceRatioControl.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent event ) {
                handleTrapForceRatioControl();
            }
        });
        
        min = laser.getElectricFieldScaleRange().getMin();
        max = laser.getElectricFieldScaleRange().getMax();
        _electricFieldScaleControl = new LinearValueControl( min, max, "E-field scale:", "0.00", "" );
        _electricFieldScaleControl.setTextFieldColumns( 4 );
        _electricFieldScaleControl.setFont( controlFont );
        _electricFieldScaleControl.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent event ) {
                handleElectricFieldScaleControl();
            }
        });
        
        // Layout
        EasyGridBagLayout layout = new EasyGridBagLayout( this );
        layout.setInsets( OTConstants.SUB_PANEL_INSETS );
        this.setLayout( layout );
        int row = 0;
        int column = 0;
        layout.addComponent( _trapForceRatioControl, row++, column );
        layout.addComponent( _electricFieldScaleControl, row++, column );
        
        // Default state
        _trapForceRatioControl.setValue( _laser.getTrapForceRatio() );
        _electricFieldScaleControl.setValue( _laser.getElectricFieldScale() );
    }
    
    public void cleanup() {
        _laser.deleteObserver( this );
    }
    
    //----------------------------------------------------------------------------
    // Event handlers
    //----------------------------------------------------------------------------
    
    private void handleTrapForceRatioControl() {
        final double value = _trapForceRatioControl.getValue();
        _laser.deleteObserver( this );
        _laser.setTrapForceRatio( value );
        _laser.addObserver( this );
    }
    
    private void handleElectricFieldScaleControl() {
        final double value = _electricFieldScaleControl.getValue();
        _laser.deleteObserver( this );
        _laser.setElectricFieldScale( value );
        _laser.addObserver( this );
    }

    //----------------------------------------------------------------------------
    // Observer implementation
    //----------------------------------------------------------------------------
    
    public void update( Observable o, Object arg ) {
        if ( o == _laser ) {
            if ( arg == Laser.PROPERTY_TRAP_FORCE_RATIO ) {
                _trapForceRatioControl.setValue( _laser.getTrapForceRatio() );
            }
            else if ( arg == Laser.PROPERTY_ELECTRIC_FIELD_SCALE ) {
                _electricFieldScaleControl.setValue( _laser.getElectricFieldScale() );
            }
        }
    }
}
