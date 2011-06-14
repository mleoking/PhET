// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.naturalselection.control;

import java.awt.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.naturalselection.NaturalSelectionConstants;
import edu.colorado.phet.naturalselection.NaturalSelectionResources;
import edu.colorado.phet.naturalselection.NaturalSelectionStrings;
import edu.colorado.phet.naturalselection.persistence.NaturalSelectionConfig;

/**
 * Panel on the left side of the control panel that contains the mutation and gene panels
 *
 * @author Jonathan Olson
 */
public class LeftPanel extends JPanel {
    private MutationPanel mutationPanel;
    private GenePanel genePanel;

    public LeftPanel() {
        setLayout( new BoxLayout( this, BoxLayout.Y_AXIS ) );

        JLabel label = new JLabel( NaturalSelectionStrings.MUTATION_PANEL_ADD_MUTATION,
                                   new ImageIcon( NaturalSelectionResources.getImage( NaturalSelectionConstants.IMAGE_MUTATION_PANEL_LARGE ) ),
                                   SwingConstants.LEFT );
        label.setFont( new PhetFont( 18 ) );
        JPanel labelPanel = new JPanel( new FlowLayout( FlowLayout.CENTER ) );
        labelPanel.add( label );

        add( labelPanel );

        mutationPanel = new MutationPanel();
        add( mutationPanel );

        add( Box.createRigidArea( new Dimension( 0, 10 ) ) );
        add( new JSeparator() );
        add( Box.createRigidArea( new Dimension( 0, 10 ) ) );

        genePanel = new GenePanel();
        add( genePanel );

        setBackground( NaturalSelectionConstants.COLOR_ACCESSIBLE_CONTROL_PANEL );
        labelPanel.setBackground( NaturalSelectionConstants.COLOR_ACCESSIBLE_CONTROL_PANEL );

        setBorder( new EmptyBorder( new Insets( 0, 10, 10, 10 ) ) );
    }

    public void reset() {
        mutationPanel.reset();
        genePanel.reset();
    }

    public void load( NaturalSelectionConfig config ) {
        genePanel.load( config );
    }

    public void save( NaturalSelectionConfig config ) {
        genePanel.save( config );
    }

    public MutationPanel getMutationPanel() {
        return mutationPanel;
    }

    public GenePanel getGenePanel() {
        return genePanel;
    }
}
