/* Copyright 2010, University of Colorado */

package edu.colorado.phet.acidbasesolutions.controls;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.TitledBorder;

import edu.colorado.phet.acidbasesolutions.constants.ABSConstants;
import edu.colorado.phet.acidbasesolutions.constants.ABSStrings;
import edu.colorado.phet.acidbasesolutions.model.ABSModel;
import edu.colorado.phet.acidbasesolutions.model.PureWaterSolution;
import edu.colorado.phet.acidbasesolutions.model.Solute;
import edu.colorado.phet.acidbasesolutions.model.ABSModel.ModelListener;
import edu.colorado.phet.acidbasesolutions.model.Acid.StrongAcid;
import edu.colorado.phet.acidbasesolutions.model.Acid.WeakAcid;
import edu.colorado.phet.acidbasesolutions.model.Base.TestStrongBase;
import edu.colorado.phet.acidbasesolutions.model.Base.TestWeakBase;
import edu.colorado.phet.acidbasesolutions.model.StrongAcidSolution.TestStrongAcidSolution;
import edu.colorado.phet.acidbasesolutions.model.StrongBaseSolution.TestStrongBaseSolution;
import edu.colorado.phet.acidbasesolutions.model.WeakAcidSolution.TestWeakAcidSolution;
import edu.colorado.phet.acidbasesolutions.model.WeakBaseSolution.TestWeakBaseSolution;
import edu.colorado.phet.acidbasesolutions.view.ABSRadioButton;
import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;

/**
 * Control used to set the properties of a "test" solution.
 * The solute can be changed.
 * The properties of the solute, and the solute's concentration in solution, are fixed.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class TestSolutionControl extends JPanel {
    
    private final ABSModel model;
    private final JRadioButton waterRadioButton;
    private final JRadioButton strongAcidRadioButton, weakAcidRadioButton;
    private final JRadioButton strongBaseRadioButton, weakBaseRadioButton;

    public TestSolutionControl( ABSModel model ) {
        
        this.model = model;
        this.model.addModelListener( new ModelListener()  {
            public void solutionChanged() {
                updateControls();
            }
        } );
        
        // border
        TitledBorder titledBorder = new TitledBorder( ABSStrings.SOLUTION );
        titledBorder.setTitleFont( ABSConstants.TITLED_BORDER_FONT );
        setBorder( titledBorder );
        
        // radio buttons
        ButtonGroup buttonGroup = new ButtonGroup();
        ActionListener actionListener = new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
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
        updateControls();
    }
    
    private void updateControls() {
        Solute solute = model.getSolution().getSolute();
        if ( solute == null ) {
            waterRadioButton.setSelected( true );
        }
        else if ( solute instanceof StrongAcid ) {
            strongAcidRadioButton.setSelected( true );
        }
        else if ( solute instanceof WeakAcid ) {
            weakAcidRadioButton.setSelected( true );
        }
        else if ( solute instanceof TestStrongBase ) {
            strongBaseRadioButton.setSelected( true );
        }
        else if ( solute instanceof TestWeakBase ) {
            weakBaseRadioButton.setSelected( true );
        }
        else {
            throw new UnsupportedOperationException( "solute type not supported: " + solute.getClass().getName() );
        }
    }
    
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
    }
}
