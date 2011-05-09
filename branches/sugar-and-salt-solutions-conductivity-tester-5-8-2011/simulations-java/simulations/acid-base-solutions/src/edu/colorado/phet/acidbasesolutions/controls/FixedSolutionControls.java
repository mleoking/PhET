// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.acidbasesolutions.controls;

import java.awt.BorderLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.MessageFormat;

import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import edu.colorado.phet.acidbasesolutions.constants.ABSImages;
import edu.colorado.phet.acidbasesolutions.constants.ABSStrings;
import edu.colorado.phet.acidbasesolutions.model.*;
import edu.colorado.phet.acidbasesolutions.model.ABSModel.ModelChangeListener;
import edu.colorado.phet.acidbasesolutions.model.Molecule.GenericAcidMolecule;
import edu.colorado.phet.acidbasesolutions.model.Molecule.GenericStrongBaseMolecule;
import edu.colorado.phet.acidbasesolutions.model.Molecule.GenericWeakBaseMolecule;
import edu.colorado.phet.acidbasesolutions.model.Molecule.WaterMolecule;
import edu.colorado.phet.acidbasesolutions.model.StrongAcidSolution.TestStrongAcidSolution;
import edu.colorado.phet.acidbasesolutions.model.StrongBaseSolution.TestStrongBaseSolution;
import edu.colorado.phet.acidbasesolutions.model.WeakAcidSolution.TestWeakAcidSolution;
import edu.colorado.phet.acidbasesolutions.model.WeakBaseSolution.TestWeakBaseSolution;
import edu.colorado.phet.common.phetcommon.view.HorizontalLayoutPanel;
import edu.colorado.phet.common.phetcommon.view.PhetTitledPanel;
import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.phetcommon.view.util.HTMLUtils;

/**
 * Controls used to select a between several "fixed" (immutable) solution.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class FixedSolutionControls extends PhetTitledPanel {
    
    private final ABSModel model;
    private final JRadioButton waterRadioButton;
    private final JRadioButton strongAcidRadioButton, weakAcidRadioButton;
    private final JRadioButton strongBaseRadioButton, weakBaseRadioButton;
    
    private static final class MoleculeLabel extends JLabel {
        public MoleculeLabel( Molecule molecule ) {
            super( ABSImages.createIcon( molecule.getImage(), 0.75 /* scale */ ) );
        }
    }

    public FixedSolutionControls( ABSModel model ) {
        super( ABSStrings.SOLUTIONS );

        // model
        {
            this.model = model;
            
            this.model.addModelChangeListener( new ModelChangeListener() {
                public void solutionChanged() {
                    // when the model changes, update this control
                    updateControls();
                }
            } );
        }
        
        // icons - clicking on these selects associated radio buttons
        JLabel waterIcon = new MoleculeLabel( new WaterMolecule() );
        waterIcon.addMouseListener( new MouseAdapter() {
            @Override 
            public void mousePressed( MouseEvent event ) {
                waterRadioButton.setSelected( true );
                updateModel();
            }
        } );
        
        JLabel strongAcidIcon = new MoleculeLabel( new GenericAcidMolecule() );
        strongAcidIcon.addMouseListener( new MouseAdapter() {
            @Override 
            public void mousePressed( MouseEvent event ) {
                strongAcidRadioButton.setSelected( true );
                updateModel();
            }
        } );
        
        JLabel weakAcidIcon = new MoleculeLabel( new GenericAcidMolecule() );
        weakAcidIcon.addMouseListener( new MouseAdapter() {
            @Override 
            public void mousePressed( MouseEvent event ) {
                weakAcidRadioButton.setSelected( true );
                updateModel();
            }
        } );
        
        JLabel strongBaseIcon = new MoleculeLabel( new GenericStrongBaseMolecule() );
        strongBaseIcon.addMouseListener( new MouseAdapter() {
            @Override 
            public void mousePressed( MouseEvent event ) {
                strongBaseRadioButton.setSelected( true );
                updateModel();
            }
        } );
        
        JLabel weakBaseIcon = new MoleculeLabel( new GenericWeakBaseMolecule() );
        weakBaseIcon.addMouseListener( new MouseAdapter() {
            @Override 
            public void mousePressed( MouseEvent event ) {
                weakBaseRadioButton.setSelected( true );
                updateModel();
            }
        } );
        
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
            final int labelIconSpacing = 5;
            
            HorizontalLayoutPanel waterPanel = new HorizontalLayoutPanel();
            waterPanel.setInsets( new Insets( 0, labelIconSpacing, 0, 0 ) );
            waterPanel.add( waterRadioButton );
            waterPanel.add( waterIcon );
            
            HorizontalLayoutPanel strongAcidPanel = new HorizontalLayoutPanel();
            strongAcidPanel.setInsets( new Insets( 0, labelIconSpacing, 0, 0 ) );
            strongAcidPanel.add( strongAcidRadioButton );
            strongAcidPanel.add( strongAcidIcon );
            
            HorizontalLayoutPanel weakAcidPanel = new HorizontalLayoutPanel();
            weakAcidPanel.setInsets( new Insets( 0, labelIconSpacing, 0, 0 ) );
            weakAcidPanel.add( weakAcidRadioButton );
            weakAcidPanel.add( weakAcidIcon );
            
            HorizontalLayoutPanel strongBasePanel = new HorizontalLayoutPanel();
            strongBasePanel.setInsets( new Insets( 0, labelIconSpacing, 0, 0 ) );
            strongBasePanel.add( strongBaseRadioButton );
            strongBasePanel.add( strongBaseIcon );
            
            HorizontalLayoutPanel weakBasePanel = new HorizontalLayoutPanel();
            weakBasePanel.setInsets( new Insets( 0, labelIconSpacing, 0, 0 ) );
            weakBasePanel.add( weakBaseRadioButton );
            weakBasePanel.add( weakBaseIcon );
            
            JPanel innerPanel = new JPanel();
            EasyGridBagLayout layout = new EasyGridBagLayout( innerPanel );
            innerPanel.setLayout( layout );
            layout.setInsets( new Insets( 4, 0, 4, 0 ) );
            int row = 0;
            int column = 0;
            layout.addComponent( waterPanel, row++, column );
            layout.addComponent( strongAcidPanel, row++, column );
            layout.addComponent( weakAcidPanel, row++, column );
            layout.addComponent( strongBasePanel, row++, column );
            layout.addComponent( weakBasePanel, row++, column );
            
            // ensure left justification
            this.setLayout( new BorderLayout() );
            this.add( innerPanel, BorderLayout.WEST );
        }
        
        // default state
        {
            updateControls();
        }
    }
    
    /*
     * Updates this control to match the model.
     */
    private void updateControls() {
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
