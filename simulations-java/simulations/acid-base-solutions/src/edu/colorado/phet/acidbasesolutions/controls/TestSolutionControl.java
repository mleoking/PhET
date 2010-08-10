/* Copyright 2010, University of Colorado */

package edu.colorado.phet.acidbasesolutions.controls;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import edu.colorado.phet.acidbasesolutions.constants.ABSConstants;
import edu.colorado.phet.acidbasesolutions.constants.ABSStrings;
import edu.colorado.phet.acidbasesolutions.model.ABSModel;
import edu.colorado.phet.acidbasesolutions.model.AqueousSolution;
import edu.colorado.phet.acidbasesolutions.model.PureWaterSolution;
import edu.colorado.phet.acidbasesolutions.model.ABSModel.ModelChangeListener;
import edu.colorado.phet.acidbasesolutions.model.StrongAcidSolution.TestStrongAcidSolution;
import edu.colorado.phet.acidbasesolutions.model.StrongBaseSolution.TestStrongBaseSolution;
import edu.colorado.phet.acidbasesolutions.model.WeakAcidSolution.TestWeakAcidSolution;
import edu.colorado.phet.acidbasesolutions.model.WeakBaseSolution.TestWeakBaseSolution;
import edu.colorado.phet.acidbasesolutions.view.ABSRadioButton;
import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;

/**
 * Control used to select a "test" solution.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class TestSolutionControl extends JPanel {
    
    private final ABSModel model;
    private final JRadioButton waterRadioButton;
    private final JRadioButton strongAcidRadioButton, weakAcidRadioButton;
    private final JRadioButton strongBaseRadioButton, weakBaseRadioButton;

    public TestSolutionControl( ABSModel model ) {
        
        // model
        this.model = model;
        this.model.addModelChangeListener( new ModelChangeListener() {
            public void solutionChanged() {
                // when the model changes, update this control
                updateControl();
            }
        } );
        
        // border
        TitledBorder titledBorder = new TitledBorder( ABSStrings.SOLUTION );
        titledBorder.setTitleFont( ABSConstants.TITLED_BORDER_FONT );
        titledBorder.setBorder( new LineBorder( Color.BLACK, 1 ) );
        setBorder( titledBorder );
        
        // radio buttons
        ButtonGroup buttonGroup = new ButtonGroup();
        ActionListener actionListener = new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                // when a radio button is pressed, update the model
                updateModel();
            }
        };
        waterRadioButton = new ABSRadioButton( ABSStrings.WATER, buttonGroup, actionListener );
        strongAcidRadioButton = new ABSRadioButton( ABSStrings.STRONG_ACID, buttonGroup, actionListener );
        weakAcidRadioButton = new ABSRadioButton( ABSStrings.WEAK_ACID, buttonGroup, actionListener );
        strongBaseRadioButton = new ABSRadioButton( ABSStrings.STRONG_BASE, buttonGroup, actionListener );
        weakBaseRadioButton = new ABSRadioButton( ABSStrings.WEAK_BASE, buttonGroup, actionListener );
        
        // layout
        EasyGridBagLayout layout = new EasyGridBagLayout( this );
        setLayout( layout );
        int row = 0;
        int column = 0;
        layout.addComponent( waterRadioButton, row++, column );
        layout.addComponent( strongAcidRadioButton, row++, column );
        layout.addComponent( weakAcidRadioButton, row++, column );
        layout.addComponent( strongBaseRadioButton, row++, column );
        layout.addComponent( weakBaseRadioButton, row++, column );
        
        // default state
        updateControl();
    }
    
    /*
     * Updates this control to match the model.
     */
    private void updateControl() {
        AqueousSolution solution = model.getSolution();
        if ( solution instanceof PureWaterSolution ) {
            waterRadioButton.setSelected( true );
        }
        else if ( solution instanceof TestStrongAcidSolution ) {
            strongAcidRadioButton.setSelected( true );
        }
        else if ( solution instanceof TestWeakAcidSolution ) {
            weakAcidRadioButton.setSelected( true );
        }
        else if ( solution instanceof TestStrongBaseSolution ) {
            strongBaseRadioButton.setSelected( true );
        }
        else if ( solution instanceof TestWeakBaseSolution ) {
            weakBaseRadioButton.setSelected( true );
        }
        else {
            throw new UnsupportedOperationException( "unsupported solution type: " + solution.getClass().getName() );
        }
    }
    
    /*
     * Updates the model to match this control.
     */
    private void updateModel() {
        if ( waterRadioButton.isSelected() ) {
            model.setSolution( new PureWaterSolution() );
        }
        else if ( strongAcidRadioButton.isSelected() ) {
            model.setSolution( new TestStrongAcidSolution() );
        }
        else if ( weakAcidRadioButton.isSelected() ) {
            model.setSolution( new TestWeakAcidSolution() );
        }
        else if ( strongBaseRadioButton.isSelected() ) {
            model.setSolution( new TestStrongBaseSolution() );
        }
        else if ( weakBaseRadioButton.isSelected() ) {
            model.setSolution( new TestWeakBaseSolution() );
        }
        else {
            throw new IllegalStateException( "illegal state, no radio button selected" );
        }
    }
}
