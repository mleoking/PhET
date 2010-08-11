/* Copyright 2010, University of Colorado */

package edu.colorado.phet.acidbasesolutions.controls;

import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.MessageFormat;

import javax.swing.*;
import javax.swing.border.TitledBorder;

import edu.colorado.phet.acidbasesolutions.constants.ABSConstants;
import edu.colorado.phet.acidbasesolutions.constants.ABSImages;
import edu.colorado.phet.acidbasesolutions.constants.ABSStrings;
import edu.colorado.phet.acidbasesolutions.model.ABSModel;
import edu.colorado.phet.acidbasesolutions.model.AqueousSolution;
import edu.colorado.phet.acidbasesolutions.model.Molecule;
import edu.colorado.phet.acidbasesolutions.model.PureWaterSolution;
import edu.colorado.phet.acidbasesolutions.model.ABSModel.ModelChangeListener;
import edu.colorado.phet.acidbasesolutions.model.Molecule.GenericAcidMolecule;
import edu.colorado.phet.acidbasesolutions.model.Molecule.GenericStrongBaseMolecule;
import edu.colorado.phet.acidbasesolutions.model.Molecule.GenericWeakBaseMolecule;
import edu.colorado.phet.acidbasesolutions.model.Molecule.WaterMolecule;
import edu.colorado.phet.acidbasesolutions.model.StrongAcidSolution.TestStrongAcidSolution;
import edu.colorado.phet.acidbasesolutions.model.StrongBaseSolution.TestStrongBaseSolution;
import edu.colorado.phet.acidbasesolutions.model.WeakAcidSolution.TestWeakAcidSolution;
import edu.colorado.phet.acidbasesolutions.model.WeakBaseSolution.TestWeakBaseSolution;
import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.phetcommon.view.util.HTMLUtils;
import edu.umd.cs.piccolo.nodes.PImage;

/**
 * Control used to select a "test" solution.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class TestSolutionControl extends JPanel {
    
    private static final double MOLECULE_ICON_SCALE = 0.75;
    
    private final ABSModel model;
    private final JRadioButton waterRadioButton;
    private final JRadioButton strongAcidRadioButton, weakAcidRadioButton;
    private final JRadioButton strongBaseRadioButton, weakBaseRadioButton;

    public TestSolutionControl( ABSModel model ) {
        
        // border
        {
            TitledBorder titledBorder = new TitledBorder( ABSStrings.SOLUTIONS );
            titledBorder.setTitleFont( ABSConstants.TITLED_BORDER_FONT );
            titledBorder.setBorder( ABSConstants.TITLE_BORDER_BORDER );
            setBorder( titledBorder );
        }
        
        // model
        {
            this.model = model;
            
            this.model.addModelChangeListener( new ModelChangeListener() {
                public void solutionChanged() {
                    // when the model changes, update this control
                    updateControl();
                }
            } );
        }
        
        // radio buttons
        {
            ActionListener actionListener = new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    // when a radio button is pressed, update the model
                    updateModel();
                }
            };
            
            waterRadioButton = new SolutionRadioButton( ABSStrings.WATER, new WaterMolecule() );
            waterRadioButton.addActionListener( actionListener );
            
            strongAcidRadioButton = new SolutionRadioButton( ABSStrings.STRONG_ACID, new GenericAcidMolecule() );
            strongAcidRadioButton.addActionListener( actionListener );
            
            weakAcidRadioButton = new SolutionRadioButton( ABSStrings.WEAK_ACID, new GenericAcidMolecule() );
            weakAcidRadioButton.addActionListener( actionListener );
            
            strongBaseRadioButton = new SolutionRadioButton( ABSStrings.STRONG_BASE, new GenericStrongBaseMolecule() );
            strongBaseRadioButton.addActionListener( actionListener );
            
            weakBaseRadioButton = new SolutionRadioButton( ABSStrings.WEAK_BASE, new GenericWeakBaseMolecule() );
            weakBaseRadioButton.addActionListener( actionListener );
            
            ButtonGroup group = new ButtonGroup();
            group.add( waterRadioButton );
            group.add( strongAcidRadioButton );
            group.add( weakAcidRadioButton );
            group.add( strongBaseRadioButton );
            group.add( weakBaseRadioButton );
        }
        
        // layout
        {
            EasyGridBagLayout layout = new EasyGridBagLayout( this );
            setLayout( layout );
            layout.setInsets( new Insets( 4, 0, 4, 0 ) );
            int row = 0;
            int column = 0;
            layout.addComponent( waterRadioButton, row, column++ );
            layout.addComponent( getMoleculeIconLabel( ABSImages.H2O_MOLECULE ), row++, column );
            column = 0;
            layout.addComponent( strongAcidRadioButton, row, column++ );
            layout.addComponent( getMoleculeIconLabel( ABSImages.HA_MOLECULE ), row++, column );
            column = 0;
            layout.addComponent( weakAcidRadioButton, row, column++ );
            layout.addComponent( getMoleculeIconLabel( ABSImages.HA_MOLECULE ), row++, column );
            column = 0;
            layout.addComponent( strongBaseRadioButton, row, column++ );
            layout.addComponent( getMoleculeIconLabel( ABSImages.MOH_MOLECULE ), row++, column );
            column = 0;
            layout.addComponent( weakBaseRadioButton, row, column++ );
            layout.addComponent( getMoleculeIconLabel( ABSImages.B_MOLECULE ), row++, column );
        }
        
        // default state
        {
            updateControl();
        }
    }
    
    private JLabel getMoleculeIconLabel( Image image ) {
        PImage imageNode = new PImage( image );
        imageNode.scale( MOLECULE_ICON_SCALE );
        Image scaledImage = imageNode.toImage();
        return new JLabel( new ImageIcon( scaledImage ) );
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
    
    /*
     * Radio button with a label, symbol and molecule icon.
     */
    private static class SolutionRadioButton extends JRadioButton {
        public SolutionRadioButton( String label, Molecule molecule ) {
            String s = MessageFormat.format( ABSStrings.PATTERN_SOLUTION_SYMBOL, label, molecule.getSymbol() );
            String html = HTMLUtils.toHTMLString( s );
            setText( html );
        }
    }
}
