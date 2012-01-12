// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.acidbasesolutions.controls;

import java.awt.BorderLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.MessageFormat;

import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import edu.colorado.phet.acidbasesolutions.constants.ABSImages;
import edu.colorado.phet.acidbasesolutions.constants.ABSSimSharing.UserComponents;
import edu.colorado.phet.acidbasesolutions.constants.ABSStrings;
import edu.colorado.phet.acidbasesolutions.model.ABSModel;
import edu.colorado.phet.acidbasesolutions.model.ABSModel.ModelChangeListener;
import edu.colorado.phet.acidbasesolutions.model.AqueousSolution;
import edu.colorado.phet.acidbasesolutions.model.Molecule;
import edu.colorado.phet.acidbasesolutions.model.Molecule.GenericAcidMolecule;
import edu.colorado.phet.acidbasesolutions.model.Molecule.GenericStrongBaseMolecule;
import edu.colorado.phet.acidbasesolutions.model.Molecule.GenericWeakBaseMolecule;
import edu.colorado.phet.acidbasesolutions.model.Molecule.WaterMolecule;
import edu.colorado.phet.acidbasesolutions.model.PureWaterSolution;
import edu.colorado.phet.acidbasesolutions.model.StrongAcidSolution.TestStrongAcidSolution;
import edu.colorado.phet.acidbasesolutions.model.StrongBaseSolution.TestStrongBaseSolution;
import edu.colorado.phet.acidbasesolutions.model.WeakAcidSolution.TestWeakAcidSolution;
import edu.colorado.phet.acidbasesolutions.model.WeakBaseSolution.TestWeakBaseSolution;
import edu.colorado.phet.common.phetcommon.simsharing.components.SimSharingIcon;
import edu.colorado.phet.common.phetcommon.simsharing.components.SimSharingJRadioButton;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
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

        // radio buttons
        {
            ActionListener actionListener = new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    // when a radio button is pressed, update the model
                    updateModel();
                }
            };

            waterRadioButton = new SolutionRadioButton( UserComponents.waterRadioButton, ABSStrings.WATER, new WaterMolecule() );
            waterRadioButton.addActionListener( actionListener );

            strongAcidRadioButton = new SolutionRadioButton( UserComponents.strongAcidRadioButton, ABSStrings.STRONG_ACID, new GenericAcidMolecule() );
            strongAcidRadioButton.addActionListener( actionListener );

            weakAcidRadioButton = new SolutionRadioButton( UserComponents.weakAcidRadioButton, ABSStrings.WEAK_ACID, new GenericAcidMolecule() );
            weakAcidRadioButton.addActionListener( actionListener );

            strongBaseRadioButton = new SolutionRadioButton( UserComponents.strongBaseRadioButton, ABSStrings.STRONG_BASE, new GenericStrongBaseMolecule() );
            strongBaseRadioButton.addActionListener( actionListener );

            weakBaseRadioButton = new SolutionRadioButton( UserComponents.weakBaseRadioButton, ABSStrings.WEAK_BASE, new GenericWeakBaseMolecule() );
            weakBaseRadioButton.addActionListener( actionListener );

            ButtonGroup group = new ButtonGroup();
            group.add( waterRadioButton );
            group.add( strongAcidRadioButton );
            group.add( weakAcidRadioButton );
            group.add( strongBaseRadioButton );
            group.add( weakBaseRadioButton );
        }

        // icons - clicking on these selects associated radio buttons
        JLabel waterIcon = new MoleculeIcon( UserComponents.waterIcon, new WaterMolecule(), new VoidFunction0() {
            public void apply() {
                waterRadioButton.setSelected( true );
                updateModel();
            }
        } );
        JLabel strongAcidIcon = new MoleculeIcon( UserComponents.strongAcidIcon, new GenericAcidMolecule(), new VoidFunction0() {
            public void apply() {
                strongAcidRadioButton.setSelected( true );
                updateModel();
            }
        } );
        JLabel weakAcidIcon = new MoleculeIcon( UserComponents.weakAcidIcon, new GenericAcidMolecule(), new VoidFunction0() {
            public void apply() {
                weakAcidRadioButton.setSelected( true );
                updateModel();
            }
        } );
        JLabel strongBaseIcon = new MoleculeIcon( UserComponents.strongBaseIcon, new GenericStrongBaseMolecule(), new VoidFunction0() {
            public void apply() {
                strongBaseRadioButton.setSelected( true );
                updateModel();
            }
        } );
        JLabel weakBaseIcon = new MoleculeIcon( UserComponents.weakBaseIcon, new GenericWeakBaseMolecule(), new VoidFunction0() {
            public void apply() {
                weakBaseRadioButton.setSelected( true );
                updateModel();
            }
        } );

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

    // Radio button with a label, symbol and molecule icon.
    private static class SolutionRadioButton extends SimSharingJRadioButton {
        public SolutionRadioButton( IUserComponent object, String label, Molecule molecule ) {
            super( object );
            String s = MessageFormat.format( ABSStrings.PATTERN_SOLUTION_SYMBOL, label, molecule.getSymbol() );
            String html = HTMLUtils.toHTMLString( s );
            setText( html );
        }
    }

    // Molecule icon
    private static final class MoleculeIcon extends SimSharingIcon {
        public MoleculeIcon( IUserComponent object, final Molecule molecule, final VoidFunction0 function ) {
            super( object, ABSImages.createIcon( molecule.getImage(), 0.75 /* scale */ ), function );
        }
    }
}
