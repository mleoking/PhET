package edu.colorado.phet.naturalselection.control;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import edu.colorado.phet.naturalselection.NaturalSelectionConstants;
import edu.colorado.phet.naturalselection.NaturalSelectionResources;
import edu.colorado.phet.naturalselection.NaturalSelectionStrings;
import edu.colorado.phet.naturalselection.NaturalSelectionApplication;
import edu.colorado.phet.naturalselection.model.*;
import edu.colorado.phet.naturalselection.util.ImagePanel;

public class MutationPanel extends JPanel {
    public JButton colorButton;
    public JButton tailButton;
    public JButton teethButton;
    private ImagePanel colorMutationIndicator;
    private ImagePanel tailMutationIndicator;
    private ImagePanel teethMutationIndicator;

    public MutationPanel( NaturalSelectionModel model ) {
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

        GridBagConstraints c = new GridBagConstraints();

        c.gridx = 0;
        c.gridy = 0;
        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets( 2, 0, 2, 10 );
        add( colorButton, c );

        c.gridx = 1;
        c.gridy = 0;
        c.fill = GridBagConstraints.NONE;
        c.insets = new Insets( 0, 0, 0, 0 );
        add( colorMutationIndicator, c );

        c.gridx = 0;
        c.gridy = 1;
        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets( 2, 0, 2, 10 );
        add( tailButton, c );

        c.gridx = 1;
        c.gridy = 1;
        c.fill = GridBagConstraints.NONE;
        c.insets = new Insets( 0, 0, 0, 0 );
        add( tailMutationIndicator, c );

        c.gridx = 0;
        c.gridy = 2;
        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets( 2, 0, 2, 10 );
        add( teethButton, c );

        c.gridx = 1;
        c.gridy = 2;
        c.fill = GridBagConstraints.NONE;
        c.insets = new Insets( 0, 0, 0, 0 );
        add( teethMutationIndicator, c );

        c.gridx = 1;
        c.gridy = 3;
        c.fill = GridBagConstraints.NONE;
        c.insets = new Insets( 0, colorMutationIndicator.getPreferredSize().width, 0, 0 );
        add( Box.createRigidArea( new Dimension( 0, 0 ) ), c );

        colorButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent actionEvent ) {
                ColorGene.getInstance().setMutatable( true );
                colorMutationIndicator.setVisible( true );

                colorButton.setEnabled( false );
                tailButton.setEnabled( false );
                teethButton.setEnabled( false );
            }
        } );

        tailButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent actionEvent ) {
                TailGene.getInstance().setMutatable( true );
                tailMutationIndicator.setVisible( true );

                colorButton.setEnabled( false );
                tailButton.setEnabled( false );
                teethButton.setEnabled( false );
            }
        } );

        teethButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent actionEvent ) {
                TeethGene.getInstance().setMutatable( true );
                teethMutationIndicator.setVisible( true );

                colorButton.setEnabled( false );
                tailButton.setEnabled( false );
                teethButton.setEnabled( false );
            }
        } );

        ColorGene.getInstance().addListener( new GeneListener() {
            public void onChangeDominantAllele( Gene gene, boolean primary ) {

            }

            public void onChangeDistribution( Gene gene, int primary, int secondary ) {

            }

            public void onChangeMutatable( Gene gene, boolean mutatable ) {
                if ( !mutatable ) {
                    colorButton.setEnabled( true );
                    tailButton.setEnabled( true );
                    teethButton.setEnabled( true );

                    colorMutationIndicator.setVisible( false );
                    tailMutationIndicator.setVisible( false ); // unneeded, TODO: refactor all of these to one listener
                    teethMutationIndicator.setVisible( false ); // unneeded
                }
            }
        } );

        TailGene.getInstance().addListener( new GeneListener() {
            public void onChangeDominantAllele( Gene gene, boolean primary ) {

            }

            public void onChangeDistribution( Gene gene, int primary, int secondary ) {

            }

            public void onChangeMutatable( Gene gene, boolean mutatable ) {
                if ( !mutatable ) {
                    colorButton.setEnabled( true );
                    tailButton.setEnabled( true );
                    teethButton.setEnabled( true );

                    colorMutationIndicator.setVisible( false ); // unneeded
                    tailMutationIndicator.setVisible( false );
                    teethMutationIndicator.setVisible( false ); // unneeded
                }
            }
        } );

        TeethGene.getInstance().addListener( new GeneListener() {
            public void onChangeDominantAllele( Gene gene, boolean primary ) {

            }

            public void onChangeDistribution( Gene gene, int primary, int secondary ) {

            }

            public void onChangeMutatable( Gene gene, boolean mutatable ) {
                if ( !mutatable ) {
                    colorButton.setEnabled( true );
                    tailButton.setEnabled( true );
                    teethButton.setEnabled( true );

                    colorMutationIndicator.setVisible( false ); // unneeded
                    tailMutationIndicator.setVisible( false ); // unneeded
                    teethMutationIndicator.setVisible( false );
                }
            }
        } );


        setBackground( NaturalSelectionApplication.accessibleColor( NaturalSelectionConstants.COLOR_CONTROL_PANEL ) );
        colorMutationIndicator.setBackground( NaturalSelectionApplication.accessibleColor( NaturalSelectionConstants.COLOR_CONTROL_PANEL ) );
        tailMutationIndicator.setBackground( NaturalSelectionApplication.accessibleColor( NaturalSelectionConstants.COLOR_CONTROL_PANEL ) );
        teethMutationIndicator.setBackground( NaturalSelectionApplication.accessibleColor( NaturalSelectionConstants.COLOR_CONTROL_PANEL ) );

    }

    public void reset() {
        colorButton.setEnabled( true );
        tailButton.setEnabled( true );
        teethButton.setEnabled( true );

        colorMutationIndicator.setVisible( false );
        tailMutationIndicator.setVisible( false );
        teethMutationIndicator.setVisible( false );
    }

}
