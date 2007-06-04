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
import edu.colorado.phet.opticaltweezers.model.DNAStrand;

/**
 * DNAStrandControlPanel constains developer controls for the DNA strand model.
 * This panel is for developers only, and it not localized.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class DNAStrandControlPanel extends JPanel implements Observer {

    private DNAStrand _dnaStrand;
    
    private LinearValueControl _springConstantControl;
    private LinearValueControl _dampingConstantControl;
    private LinearValueControl _kickConstant;
    private LinearValueControl _numberOfEvolutionsPerClockTickControl;
    
    public DNAStrandControlPanel( Font titleFont, Font controlFont, DNAStrand dnaStrand ) {
        super();
        
        _dnaStrand = dnaStrand;
        
        TitledBorder border = new TitledBorder( "DNA strand behavior:" );
        border.setTitleFont( titleFont );
        this.setBorder( border );
        
        double min = dnaStrand.getSpringConstantRange().getMin();
        double max = dnaStrand.getSpringConstantRange().getMax();
        _springConstantControl = new LinearValueControl( min, max, "spring:", "0.00", "" );
        _springConstantControl.setUpDownArrowDelta( 0.01 );
        _springConstantControl.setFont( controlFont );
        _springConstantControl.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent event ) {
                handleSpringConstantControl();
            }
        });
        
        min = dnaStrand.getDampingConstantRange().getMin();
        max = dnaStrand.getDampingConstantRange().getMax();
        _dampingConstantControl = new LinearValueControl( min, max, "damping:", "0.00", "" );
        _dampingConstantControl.setUpDownArrowDelta( 0.01 );
        _dampingConstantControl.setFont( controlFont );
        _dampingConstantControl.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent event ) {
                handleDampingConstantControl();
            }
        });
        
        min = dnaStrand.getKickConstantRange().getMin();
        max = dnaStrand.getKickConstantRange().getMax();
        _kickConstant = new LinearValueControl( min, max, "kick:", "0.00", "" );
        _kickConstant.setUpDownArrowDelta( 0.01 );
        _kickConstant.setFont( controlFont );
        _kickConstant.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent event ) {
                handleKickControl();
            }
        });
        
        min = dnaStrand.getNumberOfEvolutionsPerClockTickRange().getMin();
        max = dnaStrand.getNumberOfEvolutionsPerClockTickRange().getMax();
        _numberOfEvolutionsPerClockTickControl = new LinearValueControl( min, max, "evolutions/tick:", "#0", "" );
        _numberOfEvolutionsPerClockTickControl.setUpDownArrowDelta( 1 );
        _numberOfEvolutionsPerClockTickControl.setFont( controlFont );
        _numberOfEvolutionsPerClockTickControl.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent event ) {
                handleNumberOfEvolutionsPerClockTickControl();
            }
        });
        
        // Layout
        EasyGridBagLayout layout = new EasyGridBagLayout( this );
        layout.setInsets( new Insets( 0, 0, 0, 0 ) );
        this.setLayout( layout );
        int row = 0;
        int column = 0;
        layout.addComponent( _springConstantControl, row++, column );
        layout.addComponent( _dampingConstantControl, row++, column );
        layout.addComponent( _kickConstant, row++, column );
        layout.addComponent( _numberOfEvolutionsPerClockTickControl, row++, column );
        
        // Default state
        _springConstantControl.setValue( _dnaStrand.getSpringConstant() );
        _dampingConstantControl.setValue( _dnaStrand.getDampingConstant() );
        _kickConstant.setValue( _dnaStrand.getKickConstant() );
        _numberOfEvolutionsPerClockTickControl.setValue( _dnaStrand.getNumberOfEvolutionsPerClockTick() );
    }
    
    private void handleSpringConstantControl() {
        double value = _springConstantControl.getValue();
        _dnaStrand.setSpringConstant( value );
    }
    
    private void handleDampingConstantControl() {
        double value = _dampingConstantControl.getValue();
        _dnaStrand.setDampingConstant( value );
    }
    
    private void handleKickControl() {
        double value = _kickConstant.getValue();
        _dnaStrand.setKickConstant( value );
    }
    
    private void handleNumberOfEvolutionsPerClockTickControl() {
        int value = (int) Math.round( _numberOfEvolutionsPerClockTickControl.getValue() );
        _dnaStrand.setNumberOfEvolutionsPerClockTick( value );
    }

    public void update( Observable o, Object arg ) {
        if ( o == _dnaStrand ) {
            if ( arg == DNAStrand.PROPERTY_SPRING_CONSTANT ) {
                _springConstantControl.setValue( _dnaStrand.getSpringConstant() );
            }
            else if ( arg == DNAStrand.PROPERTY_DAMPING_CONSTANT ) {
                _dampingConstantControl.setValue( _dnaStrand.getDampingConstant() );
            }
            else if ( arg == DNAStrand.PROPERTY_KICK_CONSTANT ) {
                _kickConstant.setValue( _dnaStrand.getKickConstant() );
            }
            else if ( arg == DNAStrand.PROPERTY_NUMBER_OF_EVOLUTIONS_PER_CLOCK_TICK ) {
                _numberOfEvolutionsPerClockTickControl.setValue( _dnaStrand.getNumberOfEvolutionsPerClockTick() ); 
            }
        }
    }
}
