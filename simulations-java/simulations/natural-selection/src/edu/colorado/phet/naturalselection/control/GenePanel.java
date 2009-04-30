package edu.colorado.phet.naturalselection.control;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.naturalselection.NaturalSelectionConstants;
import edu.colorado.phet.naturalselection.model.ColorGene;
import edu.colorado.phet.naturalselection.model.Gene;
import edu.colorado.phet.naturalselection.model.TailGene;
import edu.colorado.phet.naturalselection.model.TeethGene;
import edu.colorado.phet.naturalselection.module.naturalselection.NaturalSelectionModel;
import edu.colorado.phet.naturalselection.util.ImagePanel;

public class GenePanel extends JPanel {

    // TODO: handle reset, possibly set up listening to gene changes

    public GenePanel( NaturalSelectionModel model ) {

        setLayout( new GridBagLayout() );
        GridBagConstraints c = new GridBagConstraints();

        ImagePanel colorWhite = new ImagePanel( NaturalSelectionConstants.IMAGE_BUNNY_COLOR );
        ImagePanel colorBrown = new ImagePanel( NaturalSelectionConstants.IMAGE_BUNNY_COLOR_BROWN );
        JRadioButton colorPD = createButton( true );
        JRadioButton colorPR = createButton( false );
        JRadioButton colorSD = createButton( false );
        JRadioButton colorSR = createButton( true );
        createFourGroup( ColorGene.getInstance(), colorPD, colorPR, colorSD, colorSR );

        ImagePanel tailRegular = new ImagePanel( NaturalSelectionConstants.IMAGE_BUNNY_TAIL );
        ImagePanel tailBig = new ImagePanel( NaturalSelectionConstants.IMAGE_BUNNY_TAIL_BIG );
        JRadioButton tailPD = createButton( true );
        JRadioButton tailPR = createButton( false );
        JRadioButton tailSD = createButton( false );
        JRadioButton tailSR = createButton( true );
        createFourGroup( TailGene.getInstance(), tailPD, tailPR, tailSD, tailSR );

        ImagePanel teethRegular = new ImagePanel( NaturalSelectionConstants.IMAGE_BUNNY_TEETH );
        ImagePanel teethLong = new ImagePanel( NaturalSelectionConstants.IMAGE_BUNNY_TEETH_LONG );
        JRadioButton teethPD = createButton( true );
        JRadioButton teethPR = createButton( false );
        JRadioButton teethSD = createButton( false );
        JRadioButton teethSR = createButton( true );
        createFourGroup( TeethGene.getInstance(), teethPD, teethPR, teethSD, teethSR );


        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 4;
        c.gridheight = 1;
        JLabel label = new JLabel( "Edit Genes" );
        label.setBorder( new EmptyBorder( new Insets( 0, 0, 5, 0 ) ) );
        label.setFont( new PhetFont( 18 ) );
        add( label, c );

        //----------------------------------------------------------------------------
        // Labels
        //----------------------------------------------------------------------------

        c.gridx = 0;
        c.gridy = 2;
        c.gridwidth = 1;
        c.gridheight = 2;
        JLabel colorLabel = new JLabel( "Fur" );
        colorLabel.setBorder( new EmptyBorder( new Insets( 0, 0, 0, 10 ) ) );
        add( colorLabel, c );

        c.gridx = 0;
        c.gridy = 4;
        c.gridwidth = 1;
        c.gridheight = 2;
        JLabel tailLabel = new JLabel( "Tail" );
        tailLabel.setBorder( new EmptyBorder( new Insets( 0, 0, 0, 10 ) ) );
        add( tailLabel, c );

        c.gridx = 0;
        c.gridy = 6;
        c.gridwidth = 1;
        c.gridheight = 2;
        JLabel teethLabel = new JLabel( "Teeth" );
        teethLabel.setBorder( new EmptyBorder( new Insets( 0, 0, 0, 10 ) ) );
        add( teethLabel, c );

        c.gridx = 2;
        c.gridy = 1;
        c.gridwidth = 1;
        c.gridheight = 1;
        JLabel dominantLabel = new JLabel( "Dominant" );
        dominantLabel.setBorder( new EmptyBorder( new Insets( 0, 0, 0, 10 ) ) );
        add( dominantLabel, c );

        c.gridx = 3;
        c.gridy = 1;
        c.gridwidth = 1;
        c.gridheight = 1;
        add( new JLabel( "Recessive" ), c );

        //----------------------------------------------------------------------------
        // Fur Color
        //----------------------------------------------------------------------------

        c.gridx = 1;
        c.gridy = 2;
        c.gridwidth = 1;
        c.gridheight = 1;

        add( colorWhite, c );

        c.gridx = 1;
        c.gridy = 3;
        c.gridwidth = 1;
        c.gridheight = 1;

        add( colorBrown, c );

        c.gridx = 2;
        c.gridy = 2;
        c.gridwidth = 1;
        c.gridheight = 1;

        add( colorPD, c );

        c.gridx = 3;
        c.gridy = 2;
        c.gridwidth = 1;
        c.gridheight = 1;

        add( colorPR, c );

        c.gridx = 2;
        c.gridy = 3;
        c.gridwidth = 1;
        c.gridheight = 1;

        add( colorSD, c );

        c.gridx = 3;
        c.gridy = 3;
        c.gridwidth = 1;
        c.gridheight = 1;

        add( colorSR, c );

        //----------------------------------------------------------------------------
        // Tail
        //----------------------------------------------------------------------------

        c.gridx = 1;
        c.gridy = 4;
        c.gridwidth = 1;
        c.gridheight = 1;

        add( tailRegular, c );

        c.gridx = 1;
        c.gridy = 5;
        c.gridwidth = 1;
        c.gridheight = 1;

        add( tailBig, c );

        c.gridx = 2;
        c.gridy = 4;
        c.gridwidth = 1;
        c.gridheight = 1;

        add( tailPD, c );

        c.gridx = 3;
        c.gridy = 4;
        c.gridwidth = 1;
        c.gridheight = 1;

        add( tailPR, c );

        c.gridx = 2;
        c.gridy = 5;
        c.gridwidth = 1;
        c.gridheight = 1;

        add( tailSD, c );

        c.gridx = 3;
        c.gridy = 5;
        c.gridwidth = 1;
        c.gridheight = 1;

        add( tailSR, c );

        //----------------------------------------------------------------------------
        // Teeth
        //----------------------------------------------------------------------------

        c.gridx = 1;
        c.gridy = 6;
        c.gridwidth = 1;
        c.gridheight = 1;

        add( teethRegular, c );

        c.gridx = 1;
        c.gridy = 7;
        c.gridwidth = 1;
        c.gridheight = 1;

        add( teethLong, c );

        c.gridx = 2;
        c.gridy = 6;
        c.gridwidth = 1;
        c.gridheight = 1;

        add( teethPD, c );

        c.gridx = 3;
        c.gridy = 6;
        c.gridwidth = 1;
        c.gridheight = 1;

        add( teethPR, c );

        c.gridx = 2;
        c.gridy = 7;
        c.gridwidth = 1;
        c.gridheight = 1;

        add( teethSD, c );

        c.gridx = 3;
        c.gridy = 7;
        c.gridwidth = 1;
        c.gridheight = 1;

        add( teethSR, c );


        setBackground( NaturalSelectionConstants.COLOR_CONTROL_PANEL );

    }

    public JRadioButton createButton( boolean selected ) {
        JRadioButton ret = new JRadioButton();
        ret.setIconTextGap( 0 );
        ret.setSelected( selected );
        return ret;
    }

    public void createFourGroup( final Gene gene, final JRadioButton primaryDominant, final JRadioButton primaryRecessive, final JRadioButton secondaryDominant, final JRadioButton secondaryRecessive ) {
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

    }

    private void notifyChangeDominance( Gene gene, boolean primary ) {
        // temporary hack, will refactor to permanent if listeners not necessary
        if ( primary ) {
            gene.setDominantAllele( gene.getPrimaryAllele() );
        }
        else {
            gene.setDominantAllele( gene.getSecondaryAllele() );
        }
    }

    public void reset() {

    }

}
