package edu.colorado.phet.naturalselection.control;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.naturalselection.NaturalSelectionConstants;
import edu.colorado.phet.naturalselection.NaturalSelectionStrings;
import edu.colorado.phet.naturalselection.model.*;
import edu.colorado.phet.naturalselection.util.ImagePanel;

// TODO: cleanup if possible

public class GenePanel extends JPanel {

    // TODO: handle reset, possibly set up listening to gene changes

    private static final int ROW_TITLE = 0;
    private static final int ROW_FIELDS = 1;
    private static final int ROW_COLOR_TOP = 2;
    private static final int ROW_COLOR_BOTTOM = 3;
    private static final int ROW_HIGH_SEPARATOR = 4;
    private static final int ROW_TAIL_TOP = 5;
    private static final int ROW_TAIL_BOTTOM = 6;
    private static final int ROW_LOW_SEPARATOR = 7;
    private static final int ROW_TEETH_TOP = 8;
    private static final int ROW_TEETH_BOTTOM = 9;

    private static final int COLUMN_FIRST = 0; // duplicate in case labels change
    private static final int COLUMN_LABELS = 0;
    private static final int COLUMN_SWATCHES = 1;
    private static final int COLUMN_DOMINANT = 2;
    private static final int COLUMN_RECESSIVE = 3;

    private JLabel colorLabel;
    private ImagePanel colorWhite;
    private ImagePanel colorBrown;
    private JRadioButton colorPD;
    private JRadioButton colorPR;
    private JRadioButton colorSD;
    private JRadioButton colorSR;

    private JLabel tailLabel;
    private ImagePanel tailRegular;
    private ImagePanel tailBig;
    private JRadioButton tailPD;
    private JRadioButton tailPR;
    private JRadioButton tailSD;
    private JRadioButton tailSR;

    private JLabel teethLabel;
    private ImagePanel teethRegular;
    private ImagePanel teethLong;
    private JRadioButton teethPD;
    private JRadioButton teethPR;
    private JRadioButton teethSD;
    private JRadioButton teethSR;


    public GenePanel( NaturalSelectionModel model, MutationPanel mutationPanel ) {

        setLayout( new GridBagLayout() );
        GridBagConstraints c = new GridBagConstraints();

        colorWhite = new ImagePanel( NaturalSelectionConstants.IMAGE_BUNNY_COLOR_WHITE );
        colorBrown = new ImagePanel( NaturalSelectionConstants.IMAGE_BUNNY_COLOR_BROWN );
        colorPD = createButton( true );
        colorPR = createButton( false );
        colorSD = createButton( false );
        colorSR = createButton( true );
        createFourGroup( ColorGene.getInstance(), colorPD, colorPR, colorSD, colorSR );

        tailRegular = new ImagePanel( NaturalSelectionConstants.IMAGE_BUNNY_TAIL_SHORT );
        tailBig = new ImagePanel( NaturalSelectionConstants.IMAGE_BUNNY_TAIL_LONG );
        tailPD = createButton( true );
        tailPR = createButton( false );
        tailSD = createButton( false );
        tailSR = createButton( true );
        createFourGroup( TailGene.getInstance(), tailPD, tailPR, tailSD, tailSR );

        teethRegular = new ImagePanel( NaturalSelectionConstants.IMAGE_BUNNY_TEETH_SHORT );
        teethLong = new ImagePanel( NaturalSelectionConstants.IMAGE_BUNNY_TEETH_LONG );
        teethPD = createButton( true );
        teethPR = createButton( false );
        teethSD = createButton( false );
        teethSR = createButton( true );
        createFourGroup( TeethGene.getInstance(), teethPD, teethPR, teethSD, teethSR );

        colorLabel = new JLabel( NaturalSelectionStrings.GENE_COLOR_NAME );
        tailLabel = new JLabel( NaturalSelectionStrings.GENE_TAIL_NAME );
        teethLabel = new JLabel( NaturalSelectionStrings.GENE_TEETH_NAME );

        setColorEnabled( false );
        setTailEnabled( false );
        setTeethEnabled( false );

        c.gridx = COLUMN_FIRST;
        c.gridy = ROW_TITLE;
        c.gridwidth = 4;
        c.gridheight = 1;
        JLabel label = new JLabel( NaturalSelectionStrings.GENE_PANEL_EDIT_GENES );
        label.setBorder( new EmptyBorder( new Insets( 0, 0, 5, 0 ) ) );
        label.setFont( new PhetFont( 18 ) );
        add( label, c );

        //----------------------------------------------------------------------------
        // Labels
        //----------------------------------------------------------------------------

        c.gridx = COLUMN_LABELS;
        c.gridy = ROW_COLOR_TOP;
        c.gridwidth = 1;
        c.gridheight = 2;
        colorLabel.setBorder( new EmptyBorder( new Insets( 0, 0, 0, 10 ) ) );
        add( colorLabel, c );

        c.gridx = COLUMN_LABELS;
        c.gridy = ROW_TAIL_TOP;
        c.gridwidth = 1;
        c.gridheight = 2;
        tailLabel.setBorder( new EmptyBorder( new Insets( 0, 0, 0, 10 ) ) );
        add( tailLabel, c );

        c.gridx = COLUMN_LABELS;
        c.gridy = ROW_TEETH_TOP;
        c.gridwidth = 1;
        c.gridheight = 2;
        teethLabel.setBorder( new EmptyBorder( new Insets( 0, 0, 0, 10 ) ) );
        add( teethLabel, c );

        c.gridx = COLUMN_DOMINANT;
        c.gridy = ROW_FIELDS;
        c.gridwidth = 1;
        c.gridheight = 1;
        JLabel dominantLabel = new JLabel( NaturalSelectionStrings.GENE_PANEL_DOMINANT );
        dominantLabel.setBorder( new EmptyBorder( new Insets( 0, 10, 0, 10 ) ) );
        add( dominantLabel, c );

        c.gridx = COLUMN_RECESSIVE;
        c.gridy = ROW_FIELDS;
        c.gridwidth = 1;
        c.gridheight = 1;
        add( new JLabel( NaturalSelectionStrings.GENE_PANEL_RECESSIVE ), c );

        //----------------------------------------------------------------------------
        // Fur Color
        //----------------------------------------------------------------------------

        c.gridx = COLUMN_SWATCHES;
        c.gridy = ROW_COLOR_TOP;
        c.gridwidth = 1;
        c.gridheight = 1;

        add( colorWhite, c );

        c.gridx = COLUMN_SWATCHES;
        c.gridy = ROW_COLOR_BOTTOM;
        c.gridwidth = 1;
        c.gridheight = 1;

        add( colorBrown, c );

        c.gridx = COLUMN_DOMINANT;
        c.gridy = ROW_COLOR_TOP;
        c.gridwidth = 1;
        c.gridheight = 1;

        add( colorPD, c );

        c.gridx = COLUMN_RECESSIVE;
        c.gridy = ROW_COLOR_TOP;
        c.gridwidth = 1;
        c.gridheight = 1;

        add( colorPR, c );

        c.gridx = COLUMN_DOMINANT;
        c.gridy = ROW_COLOR_BOTTOM;
        c.gridwidth = 1;
        c.gridheight = 1;

        add( colorSD, c );

        c.gridx = COLUMN_RECESSIVE;
        c.gridy = ROW_COLOR_BOTTOM;
        c.gridwidth = 1;
        c.gridheight = 1;

        add( colorSR, c );

        //----------------------------------------------------------------------------
        // Separator
        //----------------------------------------------------------------------------

        c.gridx = COLUMN_FIRST;
        c.gridy = ROW_HIGH_SEPARATOR;
        c.gridwidth = 4;
        c.gridheight = 1;
        c.fill = GridBagConstraints.HORIZONTAL;

        add( new JSeparator(), c );

        c.fill = GridBagConstraints.NONE;

        //----------------------------------------------------------------------------
        // Tail
        //----------------------------------------------------------------------------

        c.gridx = COLUMN_SWATCHES;
        c.gridy = ROW_TAIL_TOP;
        c.gridwidth = 1;
        c.gridheight = 1;

        add( tailRegular, c );

        c.gridx = COLUMN_SWATCHES;
        c.gridy = ROW_TAIL_BOTTOM;
        c.gridwidth = 1;
        c.gridheight = 1;

        add( tailBig, c );

        c.gridx = COLUMN_DOMINANT;
        c.gridy = ROW_TAIL_TOP;
        c.gridwidth = 1;
        c.gridheight = 1;

        add( tailPD, c );

        c.gridx = COLUMN_RECESSIVE;
        c.gridy = ROW_TAIL_TOP;
        c.gridwidth = 1;
        c.gridheight = 1;

        add( tailPR, c );

        c.gridx = COLUMN_DOMINANT;
        c.gridy = ROW_TAIL_BOTTOM;
        c.gridwidth = 1;
        c.gridheight = 1;

        add( tailSD, c );

        c.gridx = COLUMN_RECESSIVE;
        c.gridy = ROW_TAIL_BOTTOM;
        c.gridwidth = 1;
        c.gridheight = 1;

        add( tailSR, c );

        //----------------------------------------------------------------------------
        // Separator
        //----------------------------------------------------------------------------

        c.gridx = COLUMN_FIRST;
        c.gridy = ROW_LOW_SEPARATOR;
        c.gridwidth = 4;
        c.gridheight = 1;
        c.fill = GridBagConstraints.HORIZONTAL;

        add( new JSeparator(), c );

        c.fill = GridBagConstraints.NONE;

        //----------------------------------------------------------------------------
        // Teeth
        //----------------------------------------------------------------------------

        c.gridx = COLUMN_SWATCHES;
        c.gridy = ROW_TEETH_TOP;
        c.gridwidth = 1;
        c.gridheight = 1;

        add( teethRegular, c );

        c.gridx = COLUMN_SWATCHES;
        c.gridy = ROW_TEETH_BOTTOM;
        c.gridwidth = 1;
        c.gridheight = 1;

        add( teethLong, c );

        c.gridx = COLUMN_DOMINANT;
        c.gridy = ROW_TEETH_TOP;
        c.gridwidth = 1;
        c.gridheight = 1;

        add( teethPD, c );

        c.gridx = COLUMN_RECESSIVE;
        c.gridy = ROW_TEETH_TOP;
        c.gridwidth = 1;
        c.gridheight = 1;

        add( teethPR, c );

        c.gridx = COLUMN_DOMINANT;
        c.gridy = ROW_TEETH_BOTTOM;
        c.gridwidth = 1;
        c.gridheight = 1;

        add( teethSD, c );

        c.gridx = COLUMN_RECESSIVE;
        c.gridy = ROW_TEETH_BOTTOM;
        c.gridwidth = 1;
        c.gridheight = 1;

        add( teethSR, c );


        mutationPanel.colorButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent actionEvent ) {
                setColorEnabled( true );
            }
        } );

        mutationPanel.tailButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent actionEvent ) {
                setTailEnabled( true );
            }
        } );

        mutationPanel.teethButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent actionEvent ) {
                setTeethEnabled( true );
            }
        } );


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

    public void setColorEnabled( boolean enabled ) {
        colorLabel.setEnabled( enabled );
        colorWhite.setEnabled( enabled );
        colorBrown.setEnabled( enabled );
        colorPD.setEnabled( enabled );
        colorPR.setEnabled( enabled );
        colorSD.setEnabled( enabled );
        colorSR.setEnabled( enabled );
    }

    public void setTailEnabled( boolean enabled ) {
        tailLabel.setEnabled( enabled );
        tailRegular.setEnabled( enabled );
        tailBig.setEnabled( enabled );
        tailPD.setEnabled( enabled );
        tailPR.setEnabled( enabled );
        tailSD.setEnabled( enabled );
        tailSR.setEnabled( enabled );
    }

    public void setTeethEnabled( boolean enabled ) {
        teethLabel.setEnabled( enabled );
        teethRegular.setEnabled( enabled );
        teethLong.setEnabled( enabled );
        teethPD.setEnabled( enabled );
        teethPR.setEnabled( enabled );
        teethSD.setEnabled( enabled );
        teethSR.setEnabled( enabled );
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
        // set dominance back to the primary traits on the panel
        colorPD.setSelected( true );
        colorSR.setSelected( true );
        tailPD.setSelected( true );
        tailSR.setSelected( true );
        teethPD.setSelected( true );
        teethSR.setSelected( true );

        // disable (grey out) the gene panels
        setColorEnabled( false );
        setTailEnabled( false );
        setTeethEnabled( false );
    }

}
