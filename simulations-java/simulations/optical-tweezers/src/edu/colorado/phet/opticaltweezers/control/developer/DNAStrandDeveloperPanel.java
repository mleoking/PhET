/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.control.developer;

import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.LinearValueControl;
import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.opticaltweezers.OTConstants;
import edu.colorado.phet.opticaltweezers.model.DNAStrand;
import edu.colorado.phet.opticaltweezers.view.DNAStrandNode;

/**
 * DNAStrandDeveloperPanel constains developer controls for the DNA strand model.
 * This panel is for developers only, and it is not localized.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class DNAStrandDeveloperPanel extends JPanel implements Observer, PropertyChangeListener {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private DNAStrand _dnaStrand;
    private DNAStrandNode _dnaStrandNode;
    
    private JCheckBox _pivotsCheckBox;
    private JCheckBox _extensionCheckBox;
    private LinearValueControl _springConstantControl;
    private LinearValueControl _dragCoefficientControl;
    private LinearValueControl _kickConstant;
    private LinearValueControl _numberOfEvolutionsPerClockTickControl;
    private LinearValueControl _evolutionDtControl;
    private LinearValueControl _fluidDragCoefficientControl;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public DNAStrandDeveloperPanel( String title, Font titleFont, Font controlFont, DNAStrand dnaStrand, DNAStrandNode dnaStrandNode ) {
        super();
        
        _dnaStrand = dnaStrand;
        _dnaStrand.addObserver( this );
        
        _dnaStrandNode = dnaStrandNode;
        _dnaStrandNode.addPropertyChangeListener( this );
        
        TitledBorder border = new TitledBorder( title );
        border.setTitleFont( titleFont );
        this.setBorder( border );
        
        _pivotsCheckBox = new JCheckBox( "Show pivots" );
        _pivotsCheckBox.setFont( controlFont );
        _pivotsCheckBox.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent event ) {
                handlePivotsCheckBox();
            }
        } );
        
        _extensionCheckBox = new JCheckBox( "Show extension" );
        _extensionCheckBox.setFont( controlFont );
        _extensionCheckBox.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent event ) {
                handleExtensionCheckBox();
            }
        } );
        
        double min = dnaStrand.getSpringConstantRange().getMin();
        double max = dnaStrand.getSpringConstantRange().getMax();
        _springConstantControl = new LinearValueControl( min, max, "k/m:", "#0.0", "(spring)" );
        _springConstantControl.setUpDownArrowDelta( 0.1 );
        _springConstantControl.setFont( controlFont );
        _springConstantControl.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent event ) {
                handleSpringConstantControl();
            }
        });
        
        min = dnaStrand.getDragCoefficientRange().getMin();
        max = dnaStrand.getDragCoefficientRange().getMax();
        _dragCoefficientControl = new LinearValueControl( min, max, "b:", "0.0", "(damping)" );
        _dragCoefficientControl.setUpDownArrowDelta( 0.1 );
        _dragCoefficientControl.setFont( controlFont );
        _dragCoefficientControl.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent event ) {
                handleDampingConstantControl();
            }
        });
        
        min = dnaStrand.getEvolutionDtRange().getMin();
        max = dnaStrand.getEvolutionDtRange().getMax();
        _evolutionDtControl = new LinearValueControl( min, max, "dt:", "0.00", "" );
        _evolutionDtControl.setUpDownArrowDelta( 0.01 );
        _evolutionDtControl.setFont( controlFont );
        _evolutionDtControl.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent event ) {
                handleEvolutionDtScaleControl();
            }
        });
        
        min = dnaStrand.getKickConstantRange().getMin();
        max = dnaStrand.getKickConstantRange().getMax();
        _kickConstant = new LinearValueControl( min, max, "kick:", "##0", "" );
        _kickConstant.setUpDownArrowDelta( 1 );
        _kickConstant.setFont( controlFont );
        _kickConstant.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent event ) {
                handleKickControl();
            }
        });
        
        min = dnaStrand.getNumberOfEvolutionsPerClockTickRange().getMin();
        max = dnaStrand.getNumberOfEvolutionsPerClockTickRange().getMax();
        _numberOfEvolutionsPerClockTickControl = new LinearValueControl( min, max, "evolutions:", "##0", "" );
        _numberOfEvolutionsPerClockTickControl.setUpDownArrowDelta( 1 );
        _numberOfEvolutionsPerClockTickControl.setFont( controlFont );
        _numberOfEvolutionsPerClockTickControl.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent event ) {
                handleNumberOfEvolutionsPerClockTickControl();
            }
        });
        
        min = dnaStrand.getFluidDragCoefficientRange().getMin();
        max = dnaStrand.getFluidDragCoefficientRange().getMax();
        _fluidDragCoefficientControl = new LinearValueControl( min, max, "fluid drag coeff:", "0.000000", "" );
        _fluidDragCoefficientControl.setUpDownArrowDelta( 0.1 );
        _fluidDragCoefficientControl.setFont( controlFont );
        _fluidDragCoefficientControl.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent event ) {
                handleFluidDragCoefficientControl();
            }
        });
        
        // Layout
        EasyGridBagLayout layout = new EasyGridBagLayout( this );
        layout.setInsets( OTConstants.SUB_PANEL_INSETS );
        this.setLayout( layout );
        int row = 0;
        int column = 0;
        layout.addComponent( _pivotsCheckBox, row++, column );
        layout.addComponent( _extensionCheckBox, row++, column );
        layout.addComponent( _springConstantControl, row++, column );
        layout.addComponent( _dragCoefficientControl, row++, column );
        layout.addComponent( _evolutionDtControl, row++, column );
        layout.addComponent( _kickConstant, row++, column );
        layout.addComponent( _numberOfEvolutionsPerClockTickControl, row++, column );
        layout.addComponent( _fluidDragCoefficientControl, row++, column );
        
        // Default state
        _pivotsCheckBox.setSelected( _dnaStrandNode.isPivotsVisible() );
        _extensionCheckBox.setSelected( _dnaStrandNode.isExtensionVisible() );
        _springConstantControl.setValue( _dnaStrand.getSpringConstant() );
        _dragCoefficientControl.setValue( _dnaStrand.getDragCoefficient() );
        _kickConstant.setValue( _dnaStrand.getKickConstant() );
        _numberOfEvolutionsPerClockTickControl.setValue( _dnaStrand.getNumberOfEvolutionsPerClockTick() );
        _evolutionDtControl.setValue( _dnaStrand.getEvolutionDt() );
        _fluidDragCoefficientControl.setValue( _dnaStrand.getFluidDragCoefficient() );
    }
    
    public void cleanup() {
        _dnaStrand.deleteObserver( this );
        _dnaStrandNode.removePropertyChangeListener( this );
    }
    
    //----------------------------------------------------------------------------
    // Event handlers
    //----------------------------------------------------------------------------
    
    private void handlePivotsCheckBox() {
        _dnaStrandNode.setPivotsVisible( _pivotsCheckBox.isSelected() );
    }
    
    private void handleExtensionCheckBox() {
        _dnaStrandNode.setExtensionVisible( _extensionCheckBox.isSelected() );
    }
    
    private void handleSpringConstantControl() {
        double value = _springConstantControl.getValue();
        _dnaStrand.deleteObserver( this );
        _dnaStrand.setSpringConstant( value );
        _dnaStrand.addObserver( this );
    }
    
    private void handleDampingConstantControl() {
        double value = _dragCoefficientControl.getValue();
        _dnaStrand.deleteObserver( this );
        _dnaStrand.setDragCoefficient( value );
        _dnaStrand.addObserver( this );
    }
    
    private void handleKickControl() {
        double value = _kickConstant.getValue();
        _dnaStrand.deleteObserver( this );
        _dnaStrand.setKickConstant( value );
        _dnaStrand.addObserver( this );
    }
    
    private void handleNumberOfEvolutionsPerClockTickControl() {
        int value = (int) Math.round( _numberOfEvolutionsPerClockTickControl.getValue() );
        _dnaStrand.deleteObserver( this );
        _dnaStrand.setNumberOfEvolutionsPerClockTick( value );
        _dnaStrand.addObserver( this );
    }
    
    private void handleEvolutionDtScaleControl() {
        double value = _evolutionDtControl.getValue();
        _dnaStrand.deleteObserver( this );
        _dnaStrand.setEvolutionDt( value );
        _dnaStrand.addObserver( this );
    }
    
    private void handleFluidDragCoefficientControl() {
        double value = _fluidDragCoefficientControl.getValue();
        _dnaStrand.deleteObserver( this );
        _dnaStrand.setFluidDragCoefficient( value );
        _dnaStrand.addObserver( this );
    }

    //----------------------------------------------------------------------------
    // Observer implementation
    //----------------------------------------------------------------------------
    
    public void update( Observable o, Object arg ) {
        if ( o == _dnaStrand ) {
            if ( arg == DNAStrand.PROPERTY_SPRING_CONSTANT ) {
                _springConstantControl.setValue( _dnaStrand.getSpringConstant() );
            }
            else if ( arg == DNAStrand.PROPERTY_DRAG_COEFFICIENT ) {
                _dragCoefficientControl.setValue( _dnaStrand.getDragCoefficient() );
            }
            else if ( arg == DNAStrand.PROPERTY_KICK_CONSTANT ) {
                _kickConstant.setValue( _dnaStrand.getKickConstant() );
            }
            else if ( arg == DNAStrand.PROPERTY_NUMBER_OF_EVOLUTIONS_PER_CLOCK_TICK ) {
                _numberOfEvolutionsPerClockTickControl.setValue( _dnaStrand.getNumberOfEvolutionsPerClockTick() ); 
            }
            else if ( arg == DNAStrand.PROPERTY_EVOLUTION_DT ) {
                _evolutionDtControl.setValue( _dnaStrand.getEvolutionDt() );
            }
            else if ( arg == DNAStrand.PROPERTY_FLUID_DRAG_COEFFICIENT ) {
                _fluidDragCoefficientControl.setValue( _dnaStrand.getFluidDragCoefficient() );
            }
        }
    }

    //----------------------------------------------------------------------------
    // PropertChangeListener implementation
    //----------------------------------------------------------------------------
    
    public void propertyChange( PropertyChangeEvent event ) {
        if ( event.getSource() == _dnaStrandNode ) {
            if ( event.getPropertyName() == DNAStrandNode.PROPERTY_PIVOTS_VISIBLE ) {
                _pivotsCheckBox.setSelected( _dnaStrandNode.isPivotsVisible() );
            }
            else if ( event.getPropertyName() == DNAStrandNode.PROPERTY_EXTENSION_VISIBLE ) {
                _extensionCheckBox.setSelected( _dnaStrandNode.isExtensionVisible() );
            }
        }
    }
}
