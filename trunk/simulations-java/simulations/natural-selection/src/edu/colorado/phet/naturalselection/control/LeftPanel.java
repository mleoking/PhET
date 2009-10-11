package edu.colorado.phet.naturalselection.control;

import java.awt.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.naturalselection.NaturalSelectionApplication;
import edu.colorado.phet.naturalselection.NaturalSelectionConstants;
import edu.colorado.phet.naturalselection.NaturalSelectionResources;
import edu.colorado.phet.naturalselection.NaturalSelectionStrings;
import edu.colorado.phet.naturalselection.model.NaturalSelectionModel;
import edu.colorado.phet.naturalselection.persistence.NaturalSelectionConfig;

/**
 * Panel on the left side of the control panel that contains the mutation and gene panels
 *
 * @author Jonathan Olson
 */
public class LeftPanel extends JPanel {
    private MutationPanel mutationPanel;
    private GenePanel genePanel;

    public LeftPanel( NaturalSelectionModel model ) {
        setLayout( new BoxLayout( this, BoxLayout.Y_AXIS ) );

        JLabel label = new JLabel( NaturalSelectionStrings.MUTATION_PANEL_ADD_MUTATION,
                                   new ImageIcon( NaturalSelectionResources.getImage( NaturalSelectionConstants.IMAGE_MUTATION_PANEL_LARGE ) ),
                                   SwingConstants.LEFT );
        label.setFont( new PhetFont( 18 ) );
        JPanel labelPanel = new JPanel( new FlowLayout( FlowLayout.CENTER ) );
        labelPanel.add( label );

        add( labelPanel );

        mutationPanel = new MutationPanel( model );
        add( mutationPanel );

        add( Box.createRigidArea( new Dimension( 0, 10 ) ) );
        add( new JSeparator() );
        add( Box.createRigidArea( new Dimension( 0, 10 ) ) );

        genePanel = new GenePanel( model );
        add( genePanel );

        setBackground( NaturalSelectionApplication.accessibleColor( NaturalSelectionConstants.COLOR_CONTROL_PANEL ) );
        labelPanel.setBackground( NaturalSelectionApplication.accessibleColor( NaturalSelectionConstants.COLOR_CONTROL_PANEL ) );

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
