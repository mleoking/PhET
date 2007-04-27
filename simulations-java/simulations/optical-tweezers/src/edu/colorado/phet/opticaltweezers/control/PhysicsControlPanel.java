/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.control;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.LogarithmicValueControl;
import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.opticaltweezers.OTResources;
import edu.colorado.phet.opticaltweezers.defaults.PhysicsDefaults;
import edu.colorado.phet.opticaltweezers.model.OTClock;
import edu.colorado.phet.opticaltweezers.module.PhysicsModule;

/**
 * PhysicsControlPanel is the control panel for PhysicsModule.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class PhysicsControlPanel extends AbstractControlPanel {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    // Prints debugging output for event handler entries
    private static final boolean PRINT_DEBUG_EVENT_HANDLERS = false;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private PhysicsModule _module;
    private OTClock _clock;
    
    private LogarithmicValueControl _timestepControl;
    
    private JCheckBox _electricFieldCheckBox;
    private JCheckBox _beadChargesCheckBox;
    private JRadioButton _allChargesRadioButton;
    private JRadioButton _excessChargesRadioButton;
   
    private JCheckBox _trapForceCheckBox;
    private JLabel _horizontalTrapForceLabel;
    private JRadioButton _wholeBeadRadioButton;
    private JRadioButton _halfBeadRadioButton;
    private JCheckBox _fluidDragCheckBox;
    private JCheckBox _brownianForceCheckBox;
    
    private JCheckBox _rulerCheckBox;
    private JCheckBox _positionHistogramCheckBox;
    
    private JButton _advancedButton;
    private Box _advancedPanel;
    private JCheckBox _fluidControlsCheckBox;
    private JCheckBox _momemtumChangeCheckBox;
    private JCheckBox _potentialEnergyChartCheckBox;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     * 
     * @param module
     */
    public PhysicsControlPanel( PhysicsModule module ) {
        super( module );
        
        _module = module;
        _clock = _module.getOTClock();

        // Set the control panel's minimum width.
        int minimumWidth = OTResources.getInt( "int.minControlPanelWidth", 215 );
        setMinumumWidth( minimumWidth );
        
        // Clock speed
        JPanel speedPanel = new JPanel();
        {
            double min = PhysicsDefaults.CLOCK_DT_RANGE.getMin();
            double max = PhysicsDefaults.CLOCK_DT_RANGE.getMax();
            String label = OTResources.getString( "label.timestep" );
            String valuePattern = PhysicsDefaults.CLOCK_CONTROL_PATTERN;
            String units = OTResources.getString( "units.time" );
            _timestepControl = new LogarithmicValueControl( min, max, label, valuePattern, units );
            _timestepControl.setTextFieldColumns( 6 );
            _timestepControl.setFont( CONTROL_FONT );
            
            EasyGridBagLayout layout = new EasyGridBagLayout( speedPanel );
            speedPanel.setLayout( layout );
            layout.setAnchor( GridBagConstraints.WEST );
            layout.setFill( GridBagConstraints.HORIZONTAL );
            int row = 0;
            int column = 0;
            layout.addComponent( _timestepControl, row++, column );
        }
        
        // Fields and charged 
        JPanel fieldAndChargesPanel = new JPanel();
        {
            JLabel titleLabel = new JLabel( OTResources.getString( "label.fieldsAndCharges" ) );
            titleLabel.setFont( TITLE_FONT );
            
            _electricFieldCheckBox = new JCheckBox( OTResources.getString( "label.showElectricField" ) );
            _beadChargesCheckBox = new JCheckBox( OTResources.getString( "label.showBeadCharges" ) );
            _allChargesRadioButton = new JRadioButton( OTResources.getString("label.allCharges" ) );
            _excessChargesRadioButton = new JRadioButton( OTResources.getString( "label.excessCharges" ) );
            ButtonGroup bg = new ButtonGroup();
            bg.add( _allChargesRadioButton );
            bg.add( _excessChargesRadioButton );
            
            JPanel innerPanel = new JPanel();
            EasyGridBagLayout layout = new EasyGridBagLayout( innerPanel );
            innerPanel.setLayout( layout );
            layout.setAnchor( GridBagConstraints.WEST );
            layout.setFill( GridBagConstraints.HORIZONTAL );
            layout.setMinimumWidth( 0, 20 );
            int row = 0;
            layout.addComponent( titleLabel, row++, 0, 2, 1 );
            layout.addComponent( _electricFieldCheckBox, row++, 0, 2, 1 );
            layout.addComponent( _beadChargesCheckBox, row++, 0, 2, 1 );
            layout.addComponent( _allChargesRadioButton, row++, 1 );
            layout.addComponent( _excessChargesRadioButton, row++, 1 );
            
            fieldAndChargesPanel.setLayout( new BorderLayout() );
            fieldAndChargesPanel.add( innerPanel, BorderLayout.WEST );
        }
        
        // Forces on bead 
        JPanel forcesPanel = new JPanel();
        {
            JLabel titleLabel = new JLabel( OTResources.getString( "label.forcesOnBead" ) );
            titleLabel.setFont( TITLE_FONT );
            
            _trapForceCheckBox = new JCheckBox( OTResources.getString( "label.showTrapForce" ) );
            _fluidDragCheckBox = new JCheckBox( OTResources.getString( "label.showFluidDrag" ) );
            _brownianForceCheckBox = new JCheckBox( OTResources.getString( "label.showBrownianForce" ) );

            _horizontalTrapForceLabel = new JLabel( OTResources.getString( "label.horizontalTrapForce" ) );
            _horizontalTrapForceLabel.setFont( CONTROL_FONT );
            _wholeBeadRadioButton = new JRadioButton( OTResources.getString( "label.wholeBead" ) );
            _halfBeadRadioButton = new JRadioButton( OTResources.getString( "label.halfBead" ) );
            ButtonGroup bg = new ButtonGroup();
            bg.add( _wholeBeadRadioButton );
            bg.add( _halfBeadRadioButton );
            
            JPanel innerPanel = new JPanel();
            EasyGridBagLayout layout = new EasyGridBagLayout( innerPanel );
            innerPanel.setLayout( layout );
            layout.setAnchor( GridBagConstraints.WEST );
            layout.setFill( GridBagConstraints.HORIZONTAL );
            layout.setMinimumWidth( 0, 20 );
            int row = 0;
            layout.addComponent( titleLabel, row++, 0, 2, 1 );
            layout.addComponent( _trapForceCheckBox, row++, 0, 2, 1 );
            layout.addComponent( _horizontalTrapForceLabel, row++, 1 );
            layout.addComponent( _wholeBeadRadioButton, row++, 1 );
            layout.addComponent( _halfBeadRadioButton, row++, 1 );
            layout.addComponent( _fluidDragCheckBox, row++, 0, 2, 1 );
            layout.addComponent( _brownianForceCheckBox, row++, 0, 2, 1 );
            
            forcesPanel.setLayout( new BorderLayout() );
            forcesPanel.add( innerPanel, BorderLayout.WEST );
        }
        
        // Ruler
        _rulerCheckBox = new JCheckBox( OTResources.getString( "label.showRuler" ) );
        
        // Histogram
        _positionHistogramCheckBox = new JCheckBox( OTResources.getString( "label.showPositionHistogram" ) );
        
        // Advanced features
        JPanel advancedPanel = new JPanel();
        {
            _advancedButton = new JButton( OTResources.getString( "label.showAdvanced" ) );
            _fluidControlsCheckBox = new JCheckBox( OTResources.getString( "label.controlFluidFlow" ) );
            _momemtumChangeCheckBox = new JCheckBox( OTResources.getString( "label.showMomentumChange" ) );
            _potentialEnergyChartCheckBox = new JCheckBox( OTResources.getString( "label.showPotentialEnergyChart" ) );
            
            _advancedPanel = new Box( BoxLayout.Y_AXIS );
            _advancedPanel.add( _fluidControlsCheckBox );
            _advancedPanel.add( _momemtumChangeCheckBox );
            _advancedPanel.add( _potentialEnergyChartCheckBox );
            
            JPanel innerPanel = new JPanel();
            EasyGridBagLayout layout = new EasyGridBagLayout( innerPanel );
            innerPanel.setLayout( layout );
            layout.setAnchor( GridBagConstraints.WEST );
            layout.setFill( GridBagConstraints.HORIZONTAL );
            layout.setMinimumWidth( 0, 0 );
            int row = 0;
            layout.addComponent( _advancedButton, row++, 1 );
            layout.addComponent( _advancedPanel, row++, 1 );
            
            advancedPanel.setLayout( new BorderLayout() );
            advancedPanel.add( innerPanel, BorderLayout.WEST );
        }
        
        // Fonts
        {
            _timestepControl.setFont( CONTROL_FONT );
            
            _electricFieldCheckBox.setFont( CONTROL_FONT );
            _beadChargesCheckBox.setFont( CONTROL_FONT );
            _allChargesRadioButton.setFont( CONTROL_FONT );
            _excessChargesRadioButton.setFont( CONTROL_FONT );
           
            _trapForceCheckBox.setFont( CONTROL_FONT );
            _wholeBeadRadioButton.setFont( CONTROL_FONT );
            _halfBeadRadioButton.setFont( CONTROL_FONT );
            _fluidDragCheckBox.setFont( CONTROL_FONT );
            _brownianForceCheckBox.setFont( CONTROL_FONT );
            
            _rulerCheckBox.setFont( CONTROL_FONT );
            _positionHistogramCheckBox.setFont( CONTROL_FONT );
            
            _advancedButton.setFont( CONTROL_FONT );
            _fluidControlsCheckBox.setFont( CONTROL_FONT );
            _momemtumChangeCheckBox.setFont( CONTROL_FONT );
            _potentialEnergyChartCheckBox.setFont( CONTROL_FONT );
        }
        
        // Layout
        {
            addControlFullWidth( speedPanel );
            addSeparator();
            addControlFullWidth( fieldAndChargesPanel );
            addSeparator();
            addControlFullWidth( forcesPanel );
            addSeparator();
            addControlFullWidth( _rulerCheckBox );
            addControlFullWidth( _positionHistogramCheckBox );
            addSeparator();
            addControlFullWidth( advancedPanel );
            addSeparator();
            addResetButton();
        }
        
        // Listeners
        {
            EventListener listener = new EventListener();
            _timestepControl.addChangeListener( listener );
            _electricFieldCheckBox.addActionListener( listener );
            _beadChargesCheckBox.addActionListener( listener );
            _allChargesRadioButton.addActionListener( listener );
            _excessChargesRadioButton.addActionListener( listener );
            _trapForceCheckBox.addActionListener( listener );
            _wholeBeadRadioButton.addActionListener( listener );
            _halfBeadRadioButton.addActionListener( listener );
            _fluidDragCheckBox.addActionListener( listener );
            _brownianForceCheckBox.addActionListener( listener );
            _rulerCheckBox.addActionListener( listener );
            _positionHistogramCheckBox.addActionListener( listener );
            _advancedButton.addActionListener( listener );
            _fluidControlsCheckBox.addActionListener( listener );
            _momemtumChangeCheckBox.addActionListener( listener );
            _potentialEnergyChartCheckBox.addActionListener( listener );
        }
        
        // Default state
        {
            _timestepControl.setValue( _clock.getDt() );
            
            _electricFieldCheckBox.setSelected( false );
            _beadChargesCheckBox.setSelected( false );
            _allChargesRadioButton.setSelected( true );
            _allChargesRadioButton.setEnabled( _beadChargesCheckBox.isSelected() );
            _excessChargesRadioButton.setEnabled( false );
            _excessChargesRadioButton.setEnabled( _beadChargesCheckBox.isSelected() );
            
            _trapForceCheckBox.setSelected( false );
            _horizontalTrapForceLabel.setEnabled( _trapForceCheckBox.isSelected() );
            _wholeBeadRadioButton.setSelected( true );
            _wholeBeadRadioButton.setEnabled( _trapForceCheckBox.isSelected() );
            _halfBeadRadioButton.setSelected( false );
            _halfBeadRadioButton.setEnabled( _trapForceCheckBox.isSelected() );
            _fluidDragCheckBox.setSelected( false );
            _brownianForceCheckBox.setSelected( false );

            _rulerCheckBox.setSelected( false  );
            _positionHistogramCheckBox.setSelected( false );
            
            _advancedPanel.setVisible( false );
            _fluidControlsCheckBox.setSelected( false );
            _momemtumChangeCheckBox.setSelected( false );
            _potentialEnergyChartCheckBox.setSelected( false );
            
            handleClockSpeedControl(); // enable & disable controls based on clock speed
        }
        
        //XXX use red foreground for controls that aren't implemented
        {
            Color fg = Color.RED;
            _electricFieldCheckBox.setForeground( fg );
            _beadChargesCheckBox.setForeground( fg );
            _allChargesRadioButton.setForeground( fg );
            _excessChargesRadioButton.setForeground( fg );
            _horizontalTrapForceLabel.setForeground( fg );
            _wholeBeadRadioButton.setForeground( fg );
            _halfBeadRadioButton.setForeground( fg );
            _fluidDragCheckBox.setForeground( fg );
            _brownianForceCheckBox.setForeground( fg );
            _momemtumChangeCheckBox.setForeground( fg );
        }
    }
    
    //----------------------------------------------------------------------------
    // Mutators and accessors
    //----------------------------------------------------------------------------
    
    public void setClockSpeed( double dt ) {
        _timestepControl.setValue( dt );
        handleClockSpeedControl();
    }
    
    public double getClockSpeed() {
        return _timestepControl.getValue();
    }
    
    public void setElectricFieldSelected( boolean b ) {
        _electricFieldCheckBox.setSelected( b );
        handleElectricFieldCheckBox();
    }
    
    public boolean isElectricFieldSelected() {
        return _electricFieldCheckBox.isSelected();
    }
   
    public void setBeadChargesSelected( boolean b ) {
        _beadChargesCheckBox.setSelected( b );
        handleBeadChargesCheckBox();
    }
    
    public boolean isBeadChargesSelected() {
        return _beadChargesCheckBox.isSelected();
    }
    
    public void setAllChargesSelected( boolean b ) {
        _allChargesRadioButton.setSelected( b );
        handleAllChargesRadioButton();
    }
    
    public boolean isAllChargesSelected() {
        return _allChargesRadioButton.isSelected();
    }
    
    public void setExcessChargesSelected( boolean b ) {
        _excessChargesRadioButton.setSelected( b );
        handleExcessChargesRadioButton();
    }
    
    public boolean isExcessChargesSelected() {
        return _excessChargesRadioButton.isSelected();
    }
    
    public void setTrapForceSelected( boolean b ) {
        _trapForceCheckBox.setSelected( b );
        handleTrapForceCheckBox();
    }
    
    public boolean isTrapForceSelected() {
        return _trapForceCheckBox.isSelected();
    }
    
    public void setWholeBeadSelected( boolean b ) {
        _wholeBeadRadioButton.setSelected( b );
        handleWholeBeadRadioButton();
    }
    
    public boolean isWholeBeadSelected() {
        return _wholeBeadRadioButton.isSelected();
    }
    
    public void setHalfBeadSelected( boolean b ) {
        _halfBeadRadioButton.setSelected( b );
        handleHalfBeadRadioButton();
    }
    
    public boolean isHalfBeadSelected() {
        return _halfBeadRadioButton.isSelected();
    }
   
    public void setFluidDragSelected( boolean b ) {
        _fluidDragCheckBox.setSelected( b );
        handleFluidDragCheckBox();
    }
    
    public boolean isFluidDragSelected() {
        return _fluidDragCheckBox.isSelected();
    }
   
    public void setBrownianForceSelected( boolean b ) {
        _brownianForceCheckBox.setSelected( b );
        handleBrownianForceCheckBox();
    }
    
    public boolean isBrownianForceSelected() {
        return _brownianForceCheckBox.isSelected();
    }
    
    public void setRulerSelected( boolean b ) {
        _rulerCheckBox.setSelected( b );
        handleRulerCheckBox();
    }
    
    public boolean isRulerSelected() {
        return _rulerCheckBox.isSelected();
    }
    
    public void setPositionHistogramSelected( boolean b ) {
        _positionHistogramCheckBox.setSelected( b );
        handlePositionHistogramCheckBox();
    }
    
    public boolean isPositionHistogramSelected() {
        return _positionHistogramCheckBox.isSelected();
    }
    
    public void setAdvancedVisible( boolean b ) {
        if ( b ^ _advancedPanel.isVisible() ) {
            handleAdvancedButton();
        }
    }
    
    public boolean isAdvancedVisible() {
        return _advancedButton.isVisible();
    }
    
    public void setFluidControlSelected( boolean b ) {
        _fluidControlsCheckBox.setSelected( b );
        handleFluidControlsCheckBox();
    }
    
    public boolean isFluidControlsSelected() {
        return _fluidControlsCheckBox.isSelected();
    }
    
    public void setMomentumChangeSelected( boolean b ) {
        _momemtumChangeCheckBox.setSelected( b );
        handleMomentumChangeCheckBox();
    }
    
    public boolean isMomentumChangeSelected() {
        return _momemtumChangeCheckBox.isSelected();
    }
    
    public void setPotentialChartSelected( boolean b ) {
        _potentialEnergyChartCheckBox.setSelected( b );
        handlePotentialEnergyChartCheckBox();
    }
    
    public boolean isPotentialChartSelected() {
        return _potentialEnergyChartCheckBox.isSelected();
    }
    
    //----------------------------------------------------------------------------
    // Event dispatching
    //----------------------------------------------------------------------------
    
    private class EventListener implements ActionListener, ChangeListener {

        public void actionPerformed( ActionEvent e ) {
            Object source = e.getSource();
            if ( source == _electricFieldCheckBox ) {
                handleElectricFieldCheckBox();
            }
            else if ( source == _beadChargesCheckBox ) {
                handleBeadChargesCheckBox();
            }
            else if ( source == _allChargesRadioButton ) {
                handleAllChargesRadioButton();
            }
            else if ( source == _excessChargesRadioButton ) {
                handleExcessChargesRadioButton();
            }
            else if ( source == _trapForceCheckBox ) {
                handleTrapForceCheckBox();
            }
            else if ( source == _wholeBeadRadioButton ) {
                handleWholeBeadRadioButton();
            }
            else if ( source == _halfBeadRadioButton ) {
                handleHalfBeadRadioButton();
            }
            else if ( source == _fluidDragCheckBox ) {
                handleFluidDragCheckBox();
            }
            else if ( source == _brownianForceCheckBox ) {
                handleBrownianForceCheckBox();
            }
            else if ( source == _rulerCheckBox ) {
                handleRulerCheckBox();
            }
            else if ( source == _positionHistogramCheckBox ) {
                handlePositionHistogramCheckBox();
            }
            else if ( source == _advancedButton ) {
                handleAdvancedButton();
            }
            else if ( source == _fluidControlsCheckBox ) {
                handleFluidControlsCheckBox();
            }
            else if ( source == _momemtumChangeCheckBox ) {
                handleMomentumChangeCheckBox();
            }
            else if ( source == _potentialEnergyChartCheckBox ) {
                handlePotentialEnergyChartCheckBox();
            }
        }

        public void stateChanged( ChangeEvent e ) {
            Object source = e.getSource();
            if ( source == _timestepControl ) {
                handleClockSpeedControl();
            }
        }
        
    }
    
    //----------------------------------------------------------------------------
    // Event handlers
    //----------------------------------------------------------------------------
    
    private void handleClockSpeedControl() {
        
        boolean isSlow = _timestepControl.getValue() < ( ( _timestepControl.getMaximum() - _timestepControl.getMinimum() ) / 3 );//XXX
        
        _electricFieldCheckBox.setEnabled( isSlow );
        _beadChargesCheckBox.setEnabled( isSlow );
        _allChargesRadioButton.setEnabled( isSlow && _beadChargesCheckBox.isSelected() );
        _excessChargesRadioButton.setEnabled( isSlow && _beadChargesCheckBox.isSelected() );
        
        _horizontalTrapForceLabel.setEnabled( isSlow && _trapForceCheckBox.isSelected() );
        _wholeBeadRadioButton.setEnabled( isSlow && _trapForceCheckBox.isSelected() );
        _halfBeadRadioButton.setEnabled( isSlow && _trapForceCheckBox.isSelected() );
        
        _clock.setDt( _timestepControl.getValue() );
    }

    private void handleElectricFieldCheckBox() {
        
        final boolean selected = _electricFieldCheckBox.isSelected();
        
        if ( PRINT_DEBUG_EVENT_HANDLERS ) {
            System.out.println( "PhysicsControlPanel.handleElectricFieldCheckBox " + selected );
        }
        
        //XXX
    }
    
    private void handleBeadChargesCheckBox() {
        
        final boolean selected = _beadChargesCheckBox.isSelected();
        
        if ( PRINT_DEBUG_EVENT_HANDLERS ) {
            System.out.println( "PhysicsControlPanel.handleBeadChargesCheckBox " + selected );
        }
        
        _allChargesRadioButton.setEnabled( selected );
        _excessChargesRadioButton.setEnabled( selected );
        
        //XXX
    }
    
    private void handleAllChargesRadioButton() {
        
        if ( PRINT_DEBUG_EVENT_HANDLERS ) {
            System.out.println( "PhysicsControlPanel.handleAllChargesRadioButton" );
        }
        
        //XXX
    }
    
    private void handleExcessChargesRadioButton() {
        
        if ( PRINT_DEBUG_EVENT_HANDLERS ) {
            System.out.println( "PhysicsControlPanel.handleExcessChargesRadioButton" );
        }
        
        //XXX
    }
    
    private void handleTrapForceCheckBox() {
        
        final boolean selected = _trapForceCheckBox.isSelected();

        if ( PRINT_DEBUG_EVENT_HANDLERS ) {
            System.out.println( "PhysicsControlPanel.handleTrapForceCheckBox " + selected );
        }
        
        _horizontalTrapForceLabel.setEnabled( selected );
        _wholeBeadRadioButton.setEnabled( selected );
        _halfBeadRadioButton.setEnabled( selected );
        
        _module.setTrapForceVisible( selected );
    }
    
    private void handleWholeBeadRadioButton() {
        
        if ( PRINT_DEBUG_EVENT_HANDLERS ) {
            System.out.println( "PhysicsControlPanel.handleWholeBeadRadioButton" );
        }
        
        //XXX
    }
    
    private void handleHalfBeadRadioButton() {
        
        if ( PRINT_DEBUG_EVENT_HANDLERS ) {
            System.out.println( "PhysicsControlPanel.handleHalfBeadRadioButton" );
        }
        
        //XXX
    }
    
    private void handleFluidDragCheckBox() {
        
        final boolean selected = _fluidDragCheckBox.isSelected();
        
        if ( PRINT_DEBUG_EVENT_HANDLERS ) {
            System.out.println( "PhysicsControlPanel.handleFluidDragCheckBox " + selected );
        }
        
        //XXX
    }
    
    private void handleBrownianForceCheckBox() {
        
        final boolean selected = _brownianForceCheckBox.isSelected();
        
        if ( PRINT_DEBUG_EVENT_HANDLERS ) {
            System.out.println( "PhysicsControlPanel.handleBrownianForceCheckBox " + selected );
        }
        
        //XXX
    }
    
    private void handleRulerCheckBox() {
       
        final boolean selected = _rulerCheckBox.isSelected();
        
        if ( PRINT_DEBUG_EVENT_HANDLERS ) {
            System.out.println( "PhysicsControlPanel.handleRulerCheckBox " + selected );
        }
        
        _module.setRulerVisible( selected );
    }
    
    private void handlePositionHistogramCheckBox() {
        boolean selected = _positionHistogramCheckBox.isSelected();
        if ( PRINT_DEBUG_EVENT_HANDLERS ) {
            System.out.println( "PhysicsControlPanel.handlePositionHistogramCheckBox " + selected );
        }
        if ( selected ) {
            //HACK: only show one chart
            _potentialEnergyChartCheckBox.setSelected( false );
            _module.setPotentialEnergyChartVisible( false );
        }
        _module.setPositionHistogramChartVisible( selected );
    }
    
    private void handleAdvancedButton() {
        
        if ( PRINT_DEBUG_EVENT_HANDLERS ) {
            System.out.println( "PhysicsControlPanel.handleAdvancedButton" );
        }
        
        _advancedPanel.setVisible( !_advancedPanel.isVisible() );
        if ( _advancedPanel.isVisible() ) {
            _advancedButton.setText( OTResources.getString( "label.hideAdvanced" ) );
        }
        else {
            _advancedButton.setText( OTResources.getString( "label.showAdvanced" ) );
        }
    }
    
    private void handleFluidControlsCheckBox() {
        
        final boolean selected = _fluidControlsCheckBox.isSelected();
        
        if ( PRINT_DEBUG_EVENT_HANDLERS ) {
            System.out.println( "PhysicsControlPanel.handleFluidControlsCheckBox " + selected );
        }
        
        _module.setFluidControlsVisible( selected );
    }
    
    private void handleMomentumChangeCheckBox() {
        
        final boolean selected = _momemtumChangeCheckBox.isSelected();
        
        if ( PRINT_DEBUG_EVENT_HANDLERS ) {
            System.out.println( "PhysicsControlPanel.handleMomentumChangeCheckBox " + selected );
        }
        
        //XXX
    }
    
    private void handlePotentialEnergyChartCheckBox() {
        boolean selected = _potentialEnergyChartCheckBox.isSelected();
        if ( PRINT_DEBUG_EVENT_HANDLERS ) {
            System.out.println( "PhysicsControlPanel.handlePotentialEnergyChartCheckBox " + selected );
        }
        if ( selected ) {
            //HACK: only show one chart
            _positionHistogramCheckBox.setSelected( false );
            _module.setPositionHistogramChartVisible( false );
        }
        _module.setPotentialEnergyChartVisible( selected );
    }
}
