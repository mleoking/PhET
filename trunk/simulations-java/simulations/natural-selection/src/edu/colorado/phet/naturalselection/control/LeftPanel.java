package edu.colorado.phet.naturalselection.control;

import javax.swing.*;

import edu.colorado.phet.naturalselection.NaturalSelectionConstants;
import edu.colorado.phet.naturalselection.module.naturalselection.NaturalSelectionModel;

public class LeftPanel extends JPanel {

    public LeftPanel( NaturalSelectionModel model ) {

        setLayout( new BoxLayout( this, BoxLayout.Y_AXIS ) );

        add( new MutationPanel( model ) );

        add( new JSeparator() );

        add( new GenePanel( model ) );

        setBackground( NaturalSelectionConstants.COLOR_CONTROL_PANEL );
    }

    public void reset() {

    }
}
