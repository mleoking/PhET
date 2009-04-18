package edu.colorado.phet.naturalselection.view;

import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.pswing.PSwing;
import edu.colorado.phet.naturalselection.NaturalSelectionConstants;
import edu.colorado.phet.naturalselection.util.ImagePanel;

public abstract class MutationControlNode extends PNode implements ActionListener {

    private JButton addMutationButton;
    private PSwing addMutationButtonHolder;
    private JPanel mutationOptionsPanel;
    private PSwing mutationOptionsHolder;
    private BufferedImage image;
    private BufferedImage alternateImage;
    private JLabel percentOne;
    private JLabel percentTwo;
    private JRadioButton radioOne;
    private JRadioButton radioTwo;
    private boolean mutated;

    public MutationControlNode( BufferedImage iconImage ) {
        this( iconImage, iconImage );
    }

    public MutationControlNode( BufferedImage iconImage, BufferedImage alternateIconImage ) {
        image = iconImage;
        alternateImage = alternateIconImage;
        mutated = false;
        showAddButton();
    }

    private void showAddButton() {
        if( mutated ) {
            removeChild( mutationOptionsHolder );
            mutated = false;
        }
        addMutationButton = new JButton( "Add Mutation", new ImageIcon( image ) );
        addMutationButton.addActionListener( this );
        addMutationButtonHolder = new PSwing( addMutationButton );
        addChild( addMutationButtonHolder );
    }

    public void reset() {
        showAddButton();
    }

    public void showMutationDialog() {
        if( !mutated ) {
            addMutationButton.setVisible( false );
            removeChild( addMutationButtonHolder );
            mutated = true;
        }

        JLabel dominantLabel = new JLabel( "<html><center>Dominant<br>trait</center></html>" );
        JLabel percentLabel = new JLabel( "<html><center>% with<br>trait</center></html>" );

        JPanel optionOne = new JPanel();
        optionOne.setBackground( NaturalSelectionConstants.COLOR_MUTATION_PANEL );
        ImagePanel imageOne = new ImagePanel( image );
        imageOne.setBackground( NaturalSelectionConstants.COLOR_MUTATION_PANEL );
        radioOne = new JRadioButton( "" );
        radioOne.setSelected( true );
        radioOne.setBackground( NaturalSelectionConstants.COLOR_MUTATION_PANEL );
        optionOne.add( imageOne );
        optionOne.add( radioOne );

        percentOne = new JLabel( "100%" );

        JPanel optionTwo = new JPanel();
        optionTwo.setBackground( NaturalSelectionConstants.COLOR_MUTATION_PANEL );
        ImagePanel imageTwo = new ImagePanel( alternateImage );
        imageTwo.setBackground( NaturalSelectionConstants.COLOR_MUTATION_PANEL );
        radioTwo = new JRadioButton( "" );        
        radioTwo.setBackground( NaturalSelectionConstants.COLOR_MUTATION_PANEL );
        optionTwo.add( imageTwo );
        optionTwo.add( radioTwo );

        percentTwo = new JLabel( "0%" );

        ButtonGroup group = new ButtonGroup();
        group.add( radioOne );
        group.add( radioTwo );

        mutationOptionsPanel = new JPanel();
        GridBagLayout layout = new GridBagLayout();
        mutationOptionsPanel.setLayout( layout );

        GridBagConstraints constraints = new GridBagConstraints();
        
        constraints.gridx = 0; constraints.gridy = 0;
        constraints.insets = new Insets( 0, 0, 0, 15 );
        mutationOptionsPanel.add( dominantLabel, constraints );
        constraints.insets = new Insets( 0, 0, 0, 0 );
        constraints.gridx = 1; constraints.gridy = 0;
        mutationOptionsPanel.add( percentLabel, constraints );
        constraints.gridx = 0; constraints.gridy = 1;
        mutationOptionsPanel.add( optionOne, constraints );
        constraints.gridx = 1; constraints.gridy = 1;
        mutationOptionsPanel.add( percentOne, constraints );
        constraints.gridx = 0; constraints.gridy = 2;
        mutationOptionsPanel.add( optionTwo, constraints );
        constraints.gridx = 1; constraints.gridy = 2;
        mutationOptionsPanel.add( percentTwo, constraints );

        mutationOptionsPanel.setBorder( new CompoundBorder( new LineBorder( Color.BLACK, 1 ), new EmptyBorder( new Insets( 5, 5, 5, 5 ) ) ) );
        mutationOptionsPanel.setBackground( NaturalSelectionConstants.COLOR_MUTATION_PANEL );

        mutationOptionsHolder = new PSwing( mutationOptionsPanel );
        mutationOptionsHolder.translate( 0, -30 );
        addChild( mutationOptionsHolder );
    }

    private Component centerFit( Component comp ) {
        JPanel ret = new JPanel( new FlowLayout( FlowLayout.CENTER, 0, 0 ) );
        ret.setBackground( NaturalSelectionConstants.COLOR_MUTATION_PANEL );
        ret.add( comp );
        return ret;
    }

    public Point2D getCenter() {
        return new Point2D.Double( getOffset().getX() + addMutationButton.getWidth() / 2, getOffset().getY() + addMutationButton.getHeight() / 2 );
    }

    public abstract Point2D getBunnyLocation( BigVanillaBunny bunny );

    public void actionPerformed( ActionEvent e ) {
        if( e.getSource() == addMutationButton ) {
            showMutationDialog();
        }
    }

}
