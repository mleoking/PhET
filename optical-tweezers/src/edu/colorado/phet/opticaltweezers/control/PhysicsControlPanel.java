/* Copyright 2007, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.opticaltweezers.control;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.opticaltweezers.module.PhysicsModule;


public class PhysicsControlPanel extends AbstractControlPanel {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    // Prints debugging output for event handler entries
    private static final boolean PRINT_DEBUG_EVENT_HANDLERS = true;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private PhysicsModule _module;
    
    private SpeedControl _speedControl;
    
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
    private JCheckBox _fluidFlowCheckBox;
    private JCheckBox _momemtumChangeCheckBox;
    private JCheckBox _potentialChartCheckBox;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public PhysicsControlPanel( PhysicsModule module ) {
        super( module );
        
        setFont( CONTROL_FONT );
        
        _module = module;

        // Set the control panel's minimum width.
        String widthString = SimStrings.get( "width.controlPanel" );
        if ( widthString != null ) {
            int width = Integer.parseInt( widthString );
            setMinumumWidth( width );
        }
        
        // Clock speed
        _speedControl = new SpeedControl( TITLE_FONT, CONTROL_FONT );
        
        // Fields and charged 
        JPanel fieldAndChargesPanel = new JPanel();
        {
            JLabel titleLabel = new JLabel( SimStrings.get( "label.fieldsAndCharges" ) );
            titleLabel.setFont( TITLE_FONT );
            
            _electricFieldCheckBox = new JCheckBox( SimStrings.get( "label.showElectricField" ) );
            _beadChargesCheckBox = new JCheckBox( SimStrings.get( "label.showBeadCharges" ) );
            _allChargesRadioButton = new JRadioButton( SimStrings.get( "label.allCharges" ) );
            _excessChargesRadioButton = new JRadioButton( SimStrings.get( "label.excessCharges" ) );
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
            JLabel titleLabel = new JLabel( SimStrings.get( "label.forcesOnBead" ) );
            titleLabel.setFont( TITLE_FONT );
            
            _trapForceCheckBox = new JCheckBox( SimStrings.get( "label.showTrapForce" ) );
            _fluidDragCheckBox = new JCheckBox( SimStrings.get( "label.showFluidDrag" ) );
            _brownianForceCheckBox = new JCheckBox( SimStrings.get( "label.showBrownianForce" ) );

            _horizontalTrapForceLabel = new JLabel( SimStrings.get( "label.horizontalTrapForce" ) );
            _horizontalTrapForceLabel.setFont( CONTROL_FONT );
            _wholeBeadRadioButton = new JRadioButton( SimStrings.get( "label.wholeBead" ) );
            _halfBeadRadioButton = new JRadioButton( SimStrings.get( "label.halfBead" ) );
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
        _rulerCheckBox = new JCheckBox( SimStrings.get( "label.showRuler" ) );
        
        // Histogram
        _positionHistogramCheckBox = new JCheckBox( SimStrings.get( "label.showPositionHistogram" ) );
        
        // Advanced features
        JPanel advancedPanel = new JPanel();
        {
            _advancedButton = new JButton( SimStrings.get( "label.showAdvanced" ) );
            _fluidFlowCheckBox = new JCheckBox( SimStrings.get( "label.controlFluidFlow" ) );
            _momemtumChangeCheckBox = new JCheckBox( SimStrings.get( "label.showMomentumChange" ) );
            _potentialChartCheckBox = new JCheckBox( SimStrings.get( "label.showPotentialChart" ) );
            
            _advancedPanel = new Box( BoxLayout.Y_AXIS );
            _advancedPanel.add( _fluidFlowCheckBox );
            _advancedPanel.add( _momemtumChangeCheckBox );
            _advancedPanel.add( _potentialChartCheckBox );
            
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
            _fluidFlowCheckBox.setFont( CONTROL_FONT );
            _momemtumChangeCheckBox.setFont( CONTROL_FONT );
            _potentialChartCheckBox.setFont( CONTROL_FONT );
        }
        
        // Layout
        {
            addControlFullWidth( _speedControl );
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
            _speedControl.addChangeListener( listener );
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
            _fluidFlowCheckBox.addActionListener( listener );
            _momemtumChangeCheckBox.addActionListener( listener );
            _potentialChartCheckBox.addActionListener( listener );
        }
        
        // Default state
        {
            _allChargesRadioButton.setSelected( true );
            _allChargesRadioButton.setEnabled( false );
            _excessChargesRadioButton.setEnabled( false );
            _wholeBeadRadioButton.setSelected( true );
            _wholeBeadRadioButton.setEnabled( false );
            _halfBeadRadioButton.setEnabled( false );
            _advancedPanel.setVisible( false );
        }
    }
    
    //----------------------------------------------------------------------------
    // Mutators and accessors
    //----------------------------------------------------------------------------
    
    public void setSpeedSlow( boolean b ) {
        _speedControl.setSlowSelected( b );
    }
    
    public boolean isSpeedSlow() {
        return _speedControl.isSlowSelected();
    }
    
    public void setElectricFieldSelected( boolean b ) {
        _electricFieldCheckBox.setSelected( b );
    }
    
    public boolean isElectricFieldSelected() {
        return _electricFieldCheckBox.isSelected();
    }
   
    public void setBeadChargesSelected( boolean b ) {
        _beadChargesCheckBox.setSelected( b );
    }
    
    public boolean isBeadChargesSelected() {
        return _beadChargesCheckBox.isSelected();
    }
    
    public void setAllChargesSelected( boolean b ) {
        _allChargesRadioButton.setSelected( b );
    }
    
    public boolean AllChargesSelected() {
        return _allChargesRadioButton.isSelected();
    }
    
    public void setExcessChargesSelected( boolean b ) {
        _excessChargesRadioButton.setSelected( b );
    }
    
    public boolean isExcessChargesSelected() {
        return _excessChargesRadioButton.isSelected();
    }
    
    public void setTrapForceSelected( boolean b ) {
        _trapForceCheckBox.setSelected( b );
    }
    
    public boolean isTrapForceSelected() {
        return _trapForceCheckBox.isSelected();
    }
    
    public void setWholeBeadSelected( boolean b ) {
        _wholeBeadRadioButton.setSelected( b );
    }
    
    public boolean isWholeBeadSelected() {
        return _wholeBeadRadioButton.isSelected();
    }
    
    public void setHalfBeadSelected( boolean b ) {
        _halfBeadRadioButton.setSelected( b );
    }
    
    public boolean isHalfBeadSelected() {
        return _halfBeadRadioButton.isSelected();
    }
   
    public void setFluidDragSelected( boolean b ) {
        _fluidDragCheckBox.setSelected( b );
    }
    
    public boolean isFluidDragSelected() {
        return _fluidDragCheckBox.isSelected();
    }
   
    public void setBrownianForceSelected( boolean b ) {
        _brownianForceCheckBox.setSelected( b );
    }
    
    public boolean isBrownianForceSelected() {
        return _brownianForceCheckBox.isSelected();
    }
    
    public void setAdvancedVisible( boolean b ) {
        if ( b ^ _advancedPanel.isVisible() ) {
            handleAdvancedButton();
        }
    }
    
    public void setFluidFlowSelected( boolean b ) {
        _fluidFlowCheckBox.setSelected( b );
    }
    
    public boolean isFluidFlowSelected() {
        return _fluidFlowCheckBox.isSelected();
    }
    
    public void setMomentumChangeSelected( boolean b ) {
        _momemtumChangeCheckBox.setSelected( b );
    }
    
    public boolean isMomentumChangeSelected() {
        return _momemtumChangeCheckBox.isSelected();
    }
    
    public void setPotentialChartSelected( boolean b ) {
        _potentialChartCheckBox.setSelected( b );
    }
    
    public boolean isPotentialChartSelected() {
        return _potentialChartCheckBox.isSelected();
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
            else if ( source == _fluidFlowCheckBox ) {
                handleFluidFlowCheckBox();
            }
            else if ( source == _momemtumChangeCheckBox ) {
                handleMomentumChangeCheckBox();
            }
            else if ( source == _potentialChartCheckBox ) {
                handlePotentialChartCheckBox();
            }
        }

        public void stateChanged( ChangeEvent e ) {
            Object source = e.getSource();
            if ( source == _speedControl ) {
                handleSpeedControl();
            }
        }
        
    }
    
    //----------------------------------------------------------------------------
    // Event handlers
    //----------------------------------------------------------------------------
    
    private void handleSpeedControl() {
        
        boolean isSlow = _speedControl.isSlowSelected();
        
        if ( PRINT_DEBUG_EVENT_HANDLERS ) {
            String mode = ( isSlow ? "slow" : "fast" );
            double speed = ( isSlow ? _speedControl.getSlowSpeed() : _speedControl.getFastSpeed() );
            System.out.println( "PhysicsControlPanel.handleSpeedChange mode=" + mode + " speed=" + speed );
        }
        
        _electricFieldCheckBox.setEnabled( isSlow );
        _beadChargesCheckBox.setEnabled( isSlow );
        _allChargesRadioButton.setEnabled( isSlow && _beadChargesCheckBox.isSelected() );
        _excessChargesRadioButton.setEnabled( isSlow && _beadChargesCheckBox.isSelected() );
        _horizontalTrapForceLabel.setEnabled( isSlow && _trapForceCheckBox.isSelected() );
        _wholeBeadRadioButton.setEnabled( isSlow && _trapForceCheckBox.isSelected() );
        _halfBeadRadioButton.setEnabled( isSlow && _trapForceCheckBox.isSelected() );
        
        //XXX
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
        
        //XXX
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
        
        //XXX
    }
    
    private void handlePositionHistogramCheckBox() {
        
        final boolean selected = _positionHistogramCheckBox.isSelected();
        
        if ( PRINT_DEBUG_EVENT_HANDLERS ) {
            System.out.println( "PhysicsControlPanel.handlePositionHistogramCheckBox " + selected );
        }
        
        //XXX
    }
    
    private void handleAdvancedButton() {
        
        if ( PRINT_DEBUG_EVENT_HANDLERS ) {
            System.out.println( "PhysicsControlPanel.handleAdvancedButton" );
        }
        
        _advancedPanel.setVisible( !_advancedPanel.isVisible() );
        if ( _advancedPanel.isVisible() ) {
            _advancedButton.setText( SimStrings.get( "label.hideAdvanced" ) );
        }
        else {
            _advancedButton.setText( SimStrings.get( "label.showAdvanced" ) );
        }
    }
    
    private void handleFluidFlowCheckBox() {
        
        final boolean selected = _fluidFlowCheckBox.isSelected();
        
        if ( PRINT_DEBUG_EVENT_HANDLERS ) {
            System.out.println( "PhysicsControlPanel.handleFluidFlowCheckBox " + selected );
        }
        
        //XXX
    }
    
    private void handleMomentumChangeCheckBox() {
        
        final boolean selected = _momemtumChangeCheckBox.isSelected();
        
        if ( PRINT_DEBUG_EVENT_HANDLERS ) {
            System.out.println( "PhysicsControlPanel.handleMomentumChangeCheckBox " + selected );
        }
        
        //XXX
    }
    
    private void handlePotentialChartCheckBox() {
        
        final boolean selected = _potentialChartCheckBox.isSelected();
        
        if ( PRINT_DEBUG_EVENT_HANDLERS ) {
            System.out.println( "PhysicsControlPanel.handlePotentialChartCheckBox " + selected );
        }
        
        //XXX
    }
}
