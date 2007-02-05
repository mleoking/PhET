/* Copyright 2006, University of Colorado */

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

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import edu.colorado.phet.common.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.opticaltweezers.module.PhysicsModule;


public class PhysicsControlPanel extends AbstractControlPanel {

    private PhysicsModule _module;
    
    private SpeedControl _speedControl;
    
    private JCheckBox _electricFieldCheckBox;
    private JCheckBox _beadChargesCheckBox;
    private JRadioButton _allChargesRadioButton;
    private JRadioButton _excessChargesRadioButton;
   
    private JCheckBox _trapForceCheckBox;
    private JRadioButton _wholeBeadRadioButton;
    private JRadioButton _halfBeadRadioButton;
    private JCheckBox _fluidDragCheckBox;
    private JCheckBox _brownianForceCheckBox;
    
    private JCheckBox _rulerCheckBox;
    private JCheckBox _positionHistogramCheckBox;
    
    private JButton _advancedButton;
    private JCheckBox _fluidFlowCheckBox;
    private JCheckBox _momemtumChangeCheckBox;
    private JCheckBox _potentialChartCheckBox;
    
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
            TitledBorder titledBorder = new TitledBorder( SimStrings.get( "label.fieldsAndCharges" ) );
            titledBorder.setTitleFont( TITLE_FONT );
            fieldAndChargesPanel.setBorder( titledBorder );
            
            _electricFieldCheckBox = new JCheckBox( SimStrings.get( "label.showElectricField" ) );
            _beadChargesCheckBox = new JCheckBox( SimStrings.get( "label.showBeadCharges" ) );
            _allChargesRadioButton = new JRadioButton( SimStrings.get( "label.allCharges" ) );
            _excessChargesRadioButton = new JRadioButton( SimStrings.get( "label.excessCharges" ) );
            ButtonGroup bg = new ButtonGroup();
            bg.add( _allChargesRadioButton );
            bg.add( _excessChargesRadioButton );
            
            EasyGridBagLayout layout = new EasyGridBagLayout( fieldAndChargesPanel );
            fieldAndChargesPanel.setLayout( layout );
            layout.setInsets( INSETS );
            layout.setAnchor( GridBagConstraints.WEST );
            layout.setFill( GridBagConstraints.HORIZONTAL );
            layout.setMinimumWidth( 0, 20 );
            int row = 0;
            layout.addComponent( _electricFieldCheckBox, row++, 0, 2, 1 );
            layout.addComponent( _beadChargesCheckBox, row++, 0, 2, 1 );
            layout.addComponent( _allChargesRadioButton, row++, 1 );
            layout.addComponent( _excessChargesRadioButton, row++, 1 );
        }
        
        // Forces on bead 
        JPanel forcesPanel = new JPanel();
        {
            TitledBorder titledBorder = new TitledBorder( SimStrings.get( "label.forcesOnBead" ) );
            titledBorder.setTitleFont( TITLE_FONT );
            forcesPanel.setBorder( titledBorder );
            
            _trapForceCheckBox = new JCheckBox( SimStrings.get( "label.showTrapForce" ) );
            _fluidDragCheckBox = new JCheckBox( SimStrings.get( "label.showFluidDrag" ) );
            _brownianForceCheckBox = new JCheckBox( SimStrings.get( "label.showBrownianForce" ) );

            JLabel label = new JLabel( SimStrings.get( "label.horizontalTrapForce" ) );
            _wholeBeadRadioButton = new JRadioButton( SimStrings.get( "label.wholeBead" ) );
            _halfBeadRadioButton = new JRadioButton( SimStrings.get( "label.halfBead" ) );
            ButtonGroup bg = new ButtonGroup();
            bg.add( _wholeBeadRadioButton );
            bg.add( _halfBeadRadioButton );
            
            EasyGridBagLayout layout = new EasyGridBagLayout( forcesPanel );
            forcesPanel.setLayout( layout );
            layout.setInsets( INSETS );
            layout.setAnchor( GridBagConstraints.WEST );
            layout.setFill( GridBagConstraints.HORIZONTAL );
            layout.setMinimumWidth( 0, 20 );
            int row = 0;
            layout.addComponent( _trapForceCheckBox, row++, 0, 2, 1 );
            layout.addComponent( label, row++, 1 );
            layout.addComponent( _wholeBeadRadioButton, row++, 1 );
            layout.addComponent( _halfBeadRadioButton, row++, 1 );
            layout.addComponent( _fluidDragCheckBox, row++, 0, 2, 1 );
            layout.addComponent( _brownianForceCheckBox, row++, 0, 2, 1 );
        }
        
        // Ruler
        _rulerCheckBox = new JCheckBox( SimStrings.get( "label.showRuler" ) );
        
        // Histogram
        _positionHistogramCheckBox = new JCheckBox( SimStrings.get( "label.showPositionHistogram" ) );
        
        // Advanced features
        JPanel advancedPanel = new JPanel();
        {
            Border border = BorderFactory.createEtchedBorder();
            advancedPanel.setBorder( border );
            
            _advancedButton = new JButton( SimStrings.get( "label.showAdvanced" ) );
            _fluidFlowCheckBox = new JCheckBox( SimStrings.get( "label.controlFluidFlow" ) );
            _momemtumChangeCheckBox = new JCheckBox( SimStrings.get( "label.showMomentumChange" ) );
            _potentialChartCheckBox = new JCheckBox( SimStrings.get( "label.showPotentialChart" ) );
            
            JPanel innerPanel = new JPanel();
            EasyGridBagLayout layout = new EasyGridBagLayout( innerPanel );
            innerPanel.setLayout( layout );
            layout.setInsets( INSETS );
            layout.setAnchor( GridBagConstraints.WEST );
            layout.setFill( GridBagConstraints.HORIZONTAL );
            layout.setMinimumWidth( 0, 0 );
            int row = 0;
            layout.addComponent( _advancedButton, row++, 1 );
            layout.addComponent( _fluidFlowCheckBox, row++, 1 );
            layout.addComponent( _momemtumChangeCheckBox, row++, 1 );
            layout.addComponent( _potentialChartCheckBox, row++, 1 );
            
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
            setInsets( INSETS );
            addControlFullWidth( _speedControl );
            addControlFullWidth( fieldAndChargesPanel );
            addControlFullWidth( forcesPanel );
            addControlFullWidth( _rulerCheckBox );
            addControlFullWidth( _positionHistogramCheckBox );
            addControlFullWidth( advancedPanel );
            addSeparator();
            addResetButton();
        }
    }

}
