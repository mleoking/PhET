package edu.colorado.phet.naturalselection.control;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.naturalselection.NaturalSelectionApplication;
import edu.colorado.phet.naturalselection.NaturalSelectionConstants;
import edu.colorado.phet.naturalselection.NaturalSelectionStrings;
import edu.colorado.phet.naturalselection.model.*;
import edu.colorado.phet.naturalselection.persistence.NaturalSelectionConfig;
import edu.colorado.phet.naturalselection.util.ImagePanel;

/**
 * A control panel that allows the user to change what traits are dominant and recessive. Genes that have not been
 * mutated yet are grayed out
 *
 * @author Jonathan Olson
 */
public class GenePanel extends JPanel {

    // elements for the color gene
    private JLabel colorLabel;
    private ImagePanel colorWhite;
    private ImagePanel colorBrown;
    private JRadioButton colorPD;
    private JRadioButton colorPR;
    private JRadioButton colorSD;
    private JRadioButton colorSR;

    // elements for the tail gene
    private JLabel tailLabel;
    private ImagePanel tailRegular;
    private ImagePanel tailBig;
    private JRadioButton tailPD;
    private JRadioButton tailPR;
    private JRadioButton tailSD;
    private JRadioButton tailSR;

    // elements for the teeth gene
    private JLabel teethLabel;
    private ImagePanel teethRegular;
    private ImagePanel teethLong;
    private JRadioButton teethPD;
    private JRadioButton teethPR;
    private JRadioButton teethSD;
    private JRadioButton teethSR;

    private boolean colorEnabled;
    private boolean teethEnabled;
    private boolean tailEnabled;

    private static final Insets GENE_LABEL_INSETS = new Insets( 0, 0, 0, 10 );

    // row and column constants for layout

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

        initLabels();
        initGeneArea( ROW_COLOR_TOP, ROW_COLOR_BOTTOM, colorLabel, colorWhite, colorBrown, colorPD, colorPR, colorSD, colorSR );
        initGeneArea( ROW_TAIL_TOP, ROW_TAIL_BOTTOM, tailLabel, tailRegular, tailBig, tailPD, tailPR, tailSD, tailSR );
        initGeneArea( ROW_TEETH_TOP, ROW_TEETH_BOTTOM, teethLabel, teethRegular, teethLong, teethPD, teethPR, teethSD, teethSR );
        initSeparators();

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


        setBackground( NaturalSelectionApplication.accessibleColor( NaturalSelectionConstants.COLOR_CONTROL_PANEL ) );

    }

    private void initGeneArea( int rowTop, int rowBottom, JLabel label, ImagePanel primarySwatch, ImagePanel secondarySwatch,
                               JRadioButton primaryDominant, JRadioButton primaryRecessive,
                               JRadioButton secondaryDominant, JRadioButton secondaryRecessive ) {
        GridBagConstraints c = new GridBagConstraints();

        c.gridx = COLUMN_SWATCHES;
        c.gridy = rowTop;
        add( primarySwatch, c );

        c.gridx = COLUMN_SWATCHES;
        c.gridy = rowBottom;
        add( secondarySwatch, c );

        c.gridx = COLUMN_DOMINANT;
        c.gridy = rowTop;
        add( primaryDominant, c );

        c.gridx = COLUMN_RECESSIVE;
        c.gridy = rowTop;
        add( primaryRecessive, c );

        c.gridx = COLUMN_DOMINANT;
        c.gridy = rowBottom;
        add( secondaryDominant, c );

        c.gridx = COLUMN_RECESSIVE;
        c.gridy = rowBottom;
        add( secondaryRecessive, c );

        c.gridx = COLUMN_LABELS;
        c.gridy = rowTop;
        c.gridheight = 2;
        label.setBorder( new EmptyBorder( GENE_LABEL_INSETS ) );
        add( label, c );
    }

    private void initSeparators() {
        GridBagConstraints c = new GridBagConstraints();

        c.gridwidth = 4;
        c.gridheight = 1;
        c.fill = GridBagConstraints.HORIZONTAL;

        c.gridx = COLUMN_FIRST;
        c.gridy = ROW_HIGH_SEPARATOR;
        add( new JSeparator(), c );

        c.gridx = COLUMN_FIRST;
        c.gridy = ROW_LOW_SEPARATOR;
        add( new JSeparator(), c );
    }

    private void initLabels() {
        GridBagConstraints c = new GridBagConstraints();

        c.gridx = COLUMN_DOMINANT;
        c.gridy = ROW_FIELDS;
        JLabel dominantLabel = new JLabel( NaturalSelectionStrings.GENE_PANEL_DOMINANT );
        dominantLabel.setBorder( new EmptyBorder( new Insets( 0, 10, 0, 10 ) ) );
        add( dominantLabel, c );

        c.gridx = COLUMN_RECESSIVE;
        c.gridy = ROW_FIELDS;
        add( new JLabel( NaturalSelectionStrings.GENE_PANEL_RECESSIVE ), c );

        c.gridx = COLUMN_FIRST;
        c.gridy = ROW_TITLE;
        c.gridwidth = 4;
        JLabel label = new JLabel( NaturalSelectionStrings.GENE_PANEL_EDIT_GENES );
        label.setBorder( new EmptyBorder( new Insets( 0, 0, 5, 0 ) ) );
        label.setFont( new PhetFont( 18 ) );
        add( label, c );
    }

    private JRadioButton createButton( boolean selected ) {
        JRadioButton ret = new JRadioButton();
        ret.setIconTextGap( 0 );
        ret.setSelected( selected );
        ret.setBackground( NaturalSelectionApplication.accessibleColor( NaturalSelectionConstants.COLOR_CONTROL_PANEL ) );
        return ret;
    }

    /**
     * Wires together radio buttons with a gene so that radio buttons are grouped, and send the correct events to the gene
     *
     * @param gene               The gene to control
     * @param primaryDominant    Radio button for primary dominant
     * @param primaryRecessive   Radio button for primary recessive
     * @param secondaryDominant  Radio button for secondary dominant
     * @param secondaryRecessive Radio button for secondary recessive
     */
    private void createFourGroup( final Gene gene,
                                  final JRadioButton primaryDominant, final JRadioButton primaryRecessive,
                                  final JRadioButton secondaryDominant, final JRadioButton secondaryRecessive ) {

        ButtonGroup primaryGroup = new ButtonGroup();
        ButtonGroup secondaryGroup = new ButtonGroup();

        primaryGroup.add( primaryDominant );
        primaryGroup.add( primaryRecessive );

        secondaryGroup.add( secondaryDominant );
        secondaryGroup.add( secondaryRecessive );

        primaryDominant.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent actionEvent ) {
                changeDominance( gene, true );
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
                changeDominance( gene, false );
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
        colorEnabled = enabled;
        colorLabel.setEnabled( enabled );
        colorWhite.setEnabled( enabled );
        colorBrown.setEnabled( enabled );
        colorPD.setEnabled( enabled );
        colorPR.setEnabled( enabled );
        colorSD.setEnabled( enabled );
        colorSR.setEnabled( enabled );
    }

    public void setTailEnabled( boolean enabled ) {
        tailEnabled = enabled;
        tailLabel.setEnabled( enabled );
        tailRegular.setEnabled( enabled );
        tailBig.setEnabled( enabled );
        tailPD.setEnabled( enabled );
        tailPR.setEnabled( enabled );
        tailSD.setEnabled( enabled );
        tailSR.setEnabled( enabled );
    }

    public void setTeethEnabled( boolean enabled ) {
        teethEnabled = enabled;
        teethLabel.setEnabled( enabled );
        teethRegular.setEnabled( enabled );
        teethLong.setEnabled( enabled );
        teethPD.setEnabled( enabled );
        teethPR.setEnabled( enabled );
        teethSD.setEnabled( enabled );
        teethSR.setEnabled( enabled );
    }

    private void changeDominance( Gene gene, boolean primary ) {
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

    public void load( NaturalSelectionConfig config ) {
        setColorEnabled( config.isColorMutated() );
        setTeethEnabled( config.isTeethMutated() );
        setTailEnabled( config.isTailMutated() );
        if ( config.isColorRegularDominant() ) {
            colorPD.setSelected( true );
            colorSR.setSelected( true );
        }
        else {
            colorPR.setSelected( true );
            colorSD.setSelected( true );
        }
        if ( config.isTeethRegularDominant() ) {
            teethPD.setSelected( true );
            teethSR.setSelected( true );
        }
        else {
            teethPR.setSelected( true );
            teethSD.setSelected( true );
        }
        if ( config.isTailRegularDominant() ) {
            tailPD.setSelected( true );
            tailSR.setSelected( true );
        }
        else {
            tailPR.setSelected( true );
            tailSD.setSelected( true );
        }
    }

    public void save( NaturalSelectionConfig config ) {
        config.setColorMutated( colorEnabled );
        config.setTeethMutated( teethEnabled );
        config.setTailMutated( tailEnabled );
    }
}
