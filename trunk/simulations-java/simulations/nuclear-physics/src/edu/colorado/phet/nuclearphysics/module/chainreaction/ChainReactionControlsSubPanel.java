/* Copyright 2008, University of Colorado */

package edu.colorado.phet.nuclearphysics.module.chainreaction;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.text.DecimalFormat;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.LinearValueControl;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.nuclearphysics.NuclearPhysicsStrings;

/**
 * This class defines a subpanel that goes on the main control panel for the
 * chain reaction tab and controls various aspects of the chain reaction.
 *
 * @author John Blanco
 */
public class ChainReactionControlsSubPanel extends VerticalLayoutPanel {
    //------------------------------------------------------------------------
    // Class Data
    //------------------------------------------------------------------------
    
    //------------------------------------------------------------------------
    // Instance Data
    //------------------------------------------------------------------------
    private LinearValueControl _u235AmountControl;
    private LinearValueControl _u238AmountControl;
    private JCheckBox          _enableContainmentVesselCheckBox;
    private JTextField         _percentFissioned;
    private ChainReactionModel _model;
    private boolean            _ignoreStateChanges;

    //------------------------------------------------------------------------
    // Constructor
    //------------------------------------------------------------------------
    
    public ChainReactionControlsSubPanel(ChainReactionModel model) {
        
        _model = model;
        
        // Register as a listener with the model so that we know when it gets
        // reset.
        _model.addListener( new ChainReactionModel.Adapter(){
            public void resetOccurred(){
                _u235AmountControl.setValue( _model.getNumU235Nuclei() );
                _u238AmountControl.setValue( _model.getNumU238Nuclei() );
                _enableContainmentVesselCheckBox.setSelected( _model.getContainmentVessel().getIsEnabled() );
            }
            public void percentageU235FissionedChanged(double percentU235Fissioned){
                DecimalFormat formatter = new DecimalFormat( "##0.00" );
                _percentFissioned.setText( new String (formatter.format(percentU235Fissioned) + "%" ));
            }
            public void reactiveNucleiNumberChanged(){
                _u235AmountControl.setValue( _model.getNumU235Nuclei() );
                _u238AmountControl.setValue( _model.getNumU238Nuclei() );
            }
        });
        
        // Add the border around the sub panel.
        BevelBorder baseBorder = (BevelBorder)BorderFactory.createRaisedBevelBorder();
        TitledBorder titledBorder = BorderFactory.createTitledBorder( baseBorder,
                NuclearPhysicsStrings.MULTI_NUCLEUS_CONTROLS_BORDER,
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new PhetFont( Font.BOLD, 14 ),
                Color.GRAY );
        
        setBorder( titledBorder );
        
        // Add the check box for the containment vessel.
        _enableContainmentVesselCheckBox = new JCheckBox( NuclearPhysicsStrings.CONTAINMENT_VESSEL_CHECK_BOX );
        _enableContainmentVesselCheckBox.setSelected( _model.getContainmentVessel().getIsEnabled() );
        _enableContainmentVesselCheckBox.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                _model.getContainmentVessel().setIsEnabled( _enableContainmentVesselCheckBox.isSelected() );
                // Update the sliders, since the number of nuclei may have
                // changed.
                _u235AmountControl.setValue( _model.getNumU235Nuclei() );
                _u238AmountControl.setValue( _model.getNumU238Nuclei() );
            }
        } );
        _enableContainmentVesselCheckBox.setBorder( BorderFactory.createEtchedBorder() );
        add(_enableContainmentVesselCheckBox);
        
        // Add a little spacing in order to make the controls easier to spot.
        JPanel spacePanel = new JPanel();
        spacePanel.setLayout( new BoxLayout( spacePanel, BoxLayout.Y_AXIS ) );
        spacePanel.add( Box.createVerticalStrut( 20 ) );
        add( spacePanel );
        
        // Add the slider that controls the number of U-235 nuclei that appear.
        _u235AmountControl = new LinearValueControl( 0, 100, NuclearPhysicsStrings.U235_LABEL, "###", NuclearPhysicsStrings.NUCLEI_LABEL );
        _u235AmountControl.setUpDownArrowDelta( 1 );
        _u235AmountControl.setTextFieldEditable( true );
        _u235AmountControl.setFont( new PhetFont( Font.PLAIN, 14 ) );
        _u235AmountControl.setTickPattern( "0" );
        _u235AmountControl.setMajorTickSpacing( 25 );
        _u235AmountControl.setMinorTickSpacing( 12.5 );
        _u235AmountControl.setBorder( BorderFactory.createEtchedBorder() );
        _u235AmountControl.setValue( _model.getNumU235Nuclei() );
        _u235AmountControl.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                if (_ignoreStateChanges == false){
                    _model.setNumU235Nuclei( (int)Math.round(_u235AmountControl.getValue()) );
                }
            }
        });        
        _u235AmountControl.getSlider().addMouseListener( new MouseListener() {
            public void mouseReleased(MouseEvent e) {
                // Handle the event that is created when the user releases the
                // slider.  This is done to create a "snap back" effect in
                // cases where the user slides the slider to a level that
                // cannot actually be accomodated by the simulation.  For some
                // reason, it must be set to an incorrect value and then back
                // to a correct one for this to work right.
                _ignoreStateChanges = true;
                double numNuclei = _model.getNumU235Nuclei();
                if (numNuclei > 0){
                    _u235AmountControl.setValue( numNuclei - 1 );
                }
                else{
                    _u235AmountControl.setValue( numNuclei + 1 );
                }
                _u235AmountControl.setValue( numNuclei );
                _ignoreStateChanges = false;
            }
            public void mouseClicked(MouseEvent e) {}
            public void mousePressed(MouseEvent e) {}
            public void mouseEntered(MouseEvent e) {}
            public void mouseExited(MouseEvent e)  {}
        });
        add(_u235AmountControl);
        
        // Add the slider that controls the number of U-238 nuclei that appear.
        _u238AmountControl = new LinearValueControl( 0, 100, NuclearPhysicsStrings.U238_LABEL, "###", NuclearPhysicsStrings.NUCLEI_LABEL );
        _u238AmountControl.setUpDownArrowDelta( 1 );
        _u238AmountControl.setTextFieldEditable( true );
        _u238AmountControl.setFont( new PhetFont( Font.PLAIN, 14 ) );
        _u238AmountControl.setTickPattern( "0" );
        _u238AmountControl.setMajorTickSpacing( 25 );
        _u238AmountControl.setMinorTickSpacing( 12.5 );
        _u238AmountControl.setBorder( BorderFactory.createEtchedBorder() );
        _u238AmountControl.setValue( _model.getNumU238Nuclei() );
        _u238AmountControl.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                if (_ignoreStateChanges == false){
                    _model.setNumU238Nuclei( (int)Math.round(_u238AmountControl.getValue()) );
                }
            }
        } );
        _u238AmountControl.getSlider().addMouseListener( new MouseListener() {
            public void mouseReleased(MouseEvent e) {
                // Handle the event that is created when the user releases the
                // slider.  This is done to create a "snap back" effect in
                // cases where the user slides the slider to a level that
                // cannot actually be accomodated by the simulation.  For some
                // reason, it must be set to an incorrect value and then back
                // to a correct one for this to work right.
                _ignoreStateChanges = true;
                double numNuclei = _model.getNumU238Nuclei();
                if (numNuclei > 0){
                    _u238AmountControl.setValue( numNuclei - 1 );
                }
                else{
                    _u238AmountControl.setValue( numNuclei + 1 );
                }
                _u238AmountControl.setValue( numNuclei );
                _ignoreStateChanges = false;
            }
            public void mouseClicked(MouseEvent e) {}
            public void mousePressed(MouseEvent e) {}
            public void mouseEntered(MouseEvent e) {}
            public void mouseExited(MouseEvent e)  {}
        });
        add(_u238AmountControl);
        
        // Add the percentage fissioned information.
        JPanel fissionInfoPanel = new JPanel();
        JLabel percentFissionedLabel = new JLabel(NuclearPhysicsStrings.PERCENT_FISSIONED_LABEL);
        fissionInfoPanel.add(percentFissionedLabel);
        _percentFissioned = new JTextField( 5 );
        _percentFissioned.setHorizontalAlignment( JTextField.RIGHT );
        _percentFissioned.setText( "0" );
        _percentFissioned.setEditable( false );
        fissionInfoPanel.add(_percentFissioned);
        add(fissionInfoPanel);
    }
}
