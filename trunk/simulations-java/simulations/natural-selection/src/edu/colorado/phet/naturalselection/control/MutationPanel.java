package edu.colorado.phet.naturalselection.control;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import edu.colorado.phet.naturalselection.NaturalSelectionConstants;
import edu.colorado.phet.naturalselection.model.ColorGene;
import edu.colorado.phet.naturalselection.model.TailGene;
import edu.colorado.phet.naturalselection.model.TeethGene;
import edu.colorado.phet.naturalselection.model.GeneListener;
import edu.colorado.phet.naturalselection.module.naturalselection.NaturalSelectionModel;

public class MutationPanel extends JPanel {
    private JButton colorButton;
    private JButton tailButton;
    private JButton teethButton;

    public MutationPanel( NaturalSelectionModel model ) {
        //setLayout( new GridBagLayout() );
        setLayout( new GridLayout( 4, 1 ) );

        //GridBagConstraints c = new GridBagConstraints();

        //c.gridx = 0;
        //c.gridy = 0;
        JLabel label = new JLabel( "Add Mutation" );
        JPanel labelPanel = new JPanel( new FlowLayout( FlowLayout.CENTER ) );
        labelPanel.add( label );
        //add( label, c );

        //c.gridy = 1;
        colorButton = new JButton( "Brown Fur" );
        //add( colorButton, c );

        //c.gridy = 2;
        tailButton = new JButton( "Fuzzy Tail" );
        //add( tailButton, c );

        //c.gridy = 3;
        teethButton = new JButton( "Long Teeth" );
        //add( teethButton, c );

        add( labelPanel );
        add( colorButton );
        add( tailButton );
        add( teethButton );


        colorButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent actionEvent ) {
                ColorGene.getInstance().setMutatable( true );
                colorButton.setEnabled( false );
            }
        } );

        tailButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent actionEvent ) {
                TailGene.getInstance().setMutatable( true );
                tailButton.setEnabled( false );
            }
        } );

        teethButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent actionEvent ) {
                TeethGene.getInstance().setMutatable( true );
                teethButton.setEnabled( false );
            }
        } );

        ColorGene.getInstance().addListener( new GeneListener() {
            public void onChangeDominantAllele( boolean primary ) {

            }

            public void onChangeDistribution( int primary, int secondary ) {

            }

            public void onChangeMutatable( boolean mutatable ) {
                if( !mutatable ) {
                    colorButton.setEnabled( true );
                }
            }
        } );

        TailGene.getInstance().addListener( new GeneListener() {
            public void onChangeDominantAllele( boolean primary ) {

            }

            public void onChangeDistribution( int primary, int secondary ) {

            }

            public void onChangeMutatable( boolean mutatable ) {
                if( !mutatable ) {
                    tailButton.setEnabled( true );
                }
            }
        } );

        TeethGene.getInstance().addListener( new GeneListener() {
            public void onChangeDominantAllele( boolean primary ) {

            }

            public void onChangeDistribution( int primary, int secondary ) {

            }

            public void onChangeMutatable( boolean mutatable ) {
                if( !mutatable ) {
                    teethButton.setEnabled( true );
                }
            }
        } );


        setBackground( NaturalSelectionConstants.COLOR_CONTROL_PANEL );
        labelPanel.setBackground( NaturalSelectionConstants.COLOR_CONTROL_PANEL );
    }
}
