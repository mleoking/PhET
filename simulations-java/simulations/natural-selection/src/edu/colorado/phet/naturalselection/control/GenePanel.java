package edu.colorado.phet.naturalselection.control;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import edu.colorado.phet.naturalselection.NaturalSelectionConstants;
import edu.colorado.phet.naturalselection.model.ColorGene;
import edu.colorado.phet.naturalselection.model.Gene;
import edu.colorado.phet.naturalselection.model.TailGene;
import edu.colorado.phet.naturalselection.model.TeethGene;
import edu.colorado.phet.naturalselection.module.naturalselection.NaturalSelectionModel;

public class GenePanel extends JPanel {

    // TODO: handle reset, possibly set up listening to gene changes

    //private ArrayList listeners;

    public GenePanel( NaturalSelectionModel model ) {
        setLayout( new BoxLayout( this, BoxLayout.Y_AXIS ) );

        add( new JLabel( "Edit Genes" ) );

        add( createGeneSubpanel( ColorGene.getInstance() ) );
        add( createGeneSubpanel( TailGene.getInstance() ) );
        add( createGeneSubpanel( TeethGene.getInstance() ) );

        setBackground( NaturalSelectionConstants.COLOR_CONTROL_PANEL );
    }

    public JPanel createGeneSubpanel( final Gene gene ) {
        JPanel panel = new JPanel();
        panel.add( new JLabel( gene.getName() ) );

        panel.setBackground( NaturalSelectionConstants.COLOR_CONTROL_PANEL );

        JPanel optionPanel = new JPanel( new GridLayout( 2, 2, 0, 0 ) );

        optionPanel.setBackground( NaturalSelectionConstants.COLOR_CONTROL_PANEL );

        final JRadioButton primaryDominant = new JRadioButton();
        final JRadioButton primaryRecessive = new JRadioButton();
        final JRadioButton secondaryDominant = new JRadioButton();
        final JRadioButton secondaryRecessive = new JRadioButton();

        primaryDominant.setSelected( true );
        secondaryRecessive.setSelected( true );

        ButtonGroup primaryGroup = new ButtonGroup();
        ButtonGroup secondaryGroup = new ButtonGroup();

        primaryGroup.add( primaryDominant );
        primaryGroup.add( primaryRecessive );

        secondaryGroup.add( secondaryDominant );
        secondaryGroup.add( secondaryRecessive );

        primaryDominant.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent actionEvent ) {
                notifyChangeDominance( gene, true );
                if ( !secondaryRecessive.isSelected() ) {
                    secondaryRecessive.setSelected( true );
                }
            }
        } );

        primaryRecessive.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent actionEvent ) {
                if ( !secondaryDominant.isSelected() ) {
                    secondaryDominant.setSelected( true );
                }
            }
        } );

        secondaryDominant.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent actionEvent ) {
                notifyChangeDominance( gene, false );
                if ( !primaryRecessive.isSelected() ) {
                    primaryRecessive.setSelected( true );
                }
            }
        } );

        secondaryRecessive.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent actionEvent ) {
                if ( !primaryDominant.isSelected() ) {
                    primaryDominant.setSelected( true );
                }
            }
        } );

        optionPanel.add( primaryDominant );
        optionPanel.add( primaryRecessive );
        optionPanel.add( secondaryDominant );
        optionPanel.add( secondaryRecessive );

        panel.add( optionPanel );

        return panel;
    }

    private void notifyChangeDominance( Gene gene, boolean primary ) {
        // temporary hack, will refactor to permanent if listeners not necessary
        if ( primary ) {
            gene.setDominantAllele( gene.getPrimaryAllele() );
        }
        else {
            gene.setDominantAllele( gene.getSecondaryAllele() );
        }

        /*
        Iterator iter = listeners.iterator();
        while ( iter.hasNext() ) {
            ( (Listener) iter.next() ).onChangeDominance( gene, primary );
        }
        */
    }

    public void reset() {

    }

    /*
    public static interface Listener {
        void onChangeDominance( Gene gene, boolean primary );
    }
    */

}
