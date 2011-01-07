// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.naturalselection.control;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import edu.colorado.phet.naturalselection.NaturalSelectionConstants;
import edu.colorado.phet.naturalselection.NaturalSelectionResources;
import edu.colorado.phet.naturalselection.NaturalSelectionStrings;
import edu.colorado.phet.naturalselection.model.*;
import edu.colorado.phet.naturalselection.util.ImagePanel;

/**
 * Panel with buttons that start mutations for a particular gene. When one mutation is selected, no more can be
 * selected until the next generation
 *
 * @author Jonathan Olson
 */
public class MutationPanel extends JPanel {
    private JButton colorButton;
    private JButton tailButton;
    private JButton teethButton;

    private ImagePanel colorMutationIndicator;
    private ImagePanel tailMutationIndicator;
    private ImagePanel teethMutationIndicator;

    private static final Insets BUTTON_INSETS = new Insets( 2, 0, 2, 10 );
    private static final Insets INDICATOR_INSETS = new Insets( 0, 0, 0, 0 );

    public MutationPanel() {
        setLayout( new GridBagLayout() );

        colorMutationIndicator = new ImagePanel( NaturalSelectionConstants.IMAGE_MUTATION_PANEL_SMALL );
        tailMutationIndicator = new ImagePanel( NaturalSelectionConstants.IMAGE_MUTATION_PANEL_SMALL );
        teethMutationIndicator = new ImagePanel( NaturalSelectionConstants.IMAGE_MUTATION_PANEL_SMALL );

        colorMutationIndicator.setVisible( false );
        tailMutationIndicator.setVisible( false );
        teethMutationIndicator.setVisible( false );

        colorButton = new JButton( NaturalSelectionStrings.GENE_COLOR_BROWN, new ImageIcon( NaturalSelectionResources.getImage( NaturalSelectionConstants.IMAGE_BUNNY_COLOR_BROWN ) ) );
        colorButton.setHorizontalTextPosition( SwingConstants.LEFT );

        tailButton = new JButton( NaturalSelectionStrings.GENE_TAIL_LONG, new ImageIcon( NaturalSelectionResources.getImage( NaturalSelectionConstants.IMAGE_BUNNY_TAIL_SHORT ) ) );
        tailButton.setHorizontalTextPosition( SwingConstants.LEFT );

        teethButton = new JButton( NaturalSelectionStrings.GENE_TEETH_LONG, new ImageIcon( NaturalSelectionResources.getImage( NaturalSelectionConstants.IMAGE_BUNNY_TEETH_LONG ) ) );
        teethButton.setHorizontalTextPosition( SwingConstants.LEFT );

        initButtons();
        initIndicators();

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 1;
        c.gridy = 3;
        c.fill = GridBagConstraints.NONE;
        c.insets = new Insets( 0, colorMutationIndicator.getPreferredSize().width, 0, 0 );
        add( Box.createRigidArea( new Dimension( 0, 0 ) ), c );

        colorButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent actionEvent ) {
                ColorGene.getInstance().setMutatable( true );
                colorMutationIndicator.setVisible( true );
                setButtonsEnabled( false );
            }
        } );

        tailButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent actionEvent ) {
                TailGene.getInstance().setMutatable( true );
                tailMutationIndicator.setVisible( true );
                setButtonsEnabled( false );
            }
        } );

        teethButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent actionEvent ) {
                TeethGene.getInstance().setMutatable( true );
                teethMutationIndicator.setVisible( true );
                setButtonsEnabled( false );
            }
        } );

        GeneListener geneListener = new GeneListener() {
            public void onChangeDominantAllele( Gene gene, boolean primary ) {

            }

            public void onChangeDistribution( Gene gene, int primary, int secondary ) {

            }

            public void onChangeMutatable( Gene gene, boolean mutatable ) {
                if ( !mutatable ) {
                    setButtonsEnabled( true );

                    colorMutationIndicator.setVisible( false );
                    tailMutationIndicator.setVisible( false );
                    teethMutationIndicator.setVisible( false );
                }
            }
        };

        ColorGene.getInstance().addListener( geneListener );
        TailGene.getInstance().addListener( geneListener );
        TeethGene.getInstance().addListener( geneListener );

        setBackground( NaturalSelectionConstants.COLOR_ACCESSIBLE_CONTROL_PANEL );
        colorMutationIndicator.setBackground( NaturalSelectionConstants.COLOR_ACCESSIBLE_CONTROL_PANEL );
        tailMutationIndicator.setBackground( NaturalSelectionConstants.COLOR_ACCESSIBLE_CONTROL_PANEL );
        teethMutationIndicator.setBackground( NaturalSelectionConstants.COLOR_ACCESSIBLE_CONTROL_PANEL );

    }

    private void setButtonsEnabled( boolean enabled ) {
        colorButton.setEnabled( enabled );
        tailButton.setEnabled( enabled );
        teethButton.setEnabled( enabled );
    }

    private void initButtons() {
        GridBagConstraints c = new GridBagConstraints();

        c.gridx = 0;
        c.fill = GridBagConstraints.BOTH;
        c.insets = BUTTON_INSETS;

        c.gridy = 0;
        add( colorButton, c );

        c.gridy = 1;
        add( tailButton, c );

        c.gridy = 2;
        add( teethButton, c );
    }

    private void initIndicators() {
        GridBagConstraints c = new GridBagConstraints();

        c.gridx = 1;
        c.fill = GridBagConstraints.NONE;
        c.insets = INDICATOR_INSETS;

        c.gridy = 0;
        add( colorMutationIndicator, c );

        c.gridy = 1;
        add( tailMutationIndicator, c );

        c.gridy = 2;
        add( teethMutationIndicator, c );
    }

    public void reset() {
        colorButton.setEnabled( true );
        tailButton.setEnabled( true );
        teethButton.setEnabled( true );

        colorMutationIndicator.setVisible( false );
        tailMutationIndicator.setVisible( false );
        teethMutationIndicator.setVisible( false );
    }

    public JButton getColorButton() {
        return colorButton;
    }

    public JButton getTailButton() {
        return tailButton;
    }

    public JButton getTeethButton() {
        return teethButton;
    }
}
