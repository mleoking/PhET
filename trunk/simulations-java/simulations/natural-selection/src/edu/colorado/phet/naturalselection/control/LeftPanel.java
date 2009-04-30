package edu.colorado.phet.naturalselection.control;

import java.awt.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import edu.colorado.phet.naturalselection.NaturalSelectionConstants;
import edu.colorado.phet.naturalselection.NaturalSelectionResources;
import edu.colorado.phet.naturalselection.module.naturalselection.NaturalSelectionModel;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;

public class LeftPanel extends JPanel {
    private MutationPanel mutationPanel;
    private GenePanel genePanel;

    public LeftPanel( NaturalSelectionModel model ) {

        setLayout( new BoxLayout( this, BoxLayout.Y_AXIS ) );


        JLabel label = new JLabel( "Add Mutation", new ImageIcon( NaturalSelectionResources.getImage( NaturalSelectionConstants.IMAGE_MUTATION_PANEL_LARGE ) ), SwingConstants.LEFT );
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

        setBackground( NaturalSelectionConstants.COLOR_CONTROL_PANEL );
        labelPanel.setBackground( NaturalSelectionConstants.COLOR_CONTROL_PANEL );

        setBorder( new EmptyBorder( new Insets( 0, 10, 10, 0 ) ) );
    }

    public void reset() {
        mutationPanel.reset();
        genePanel.reset();
    }
}
