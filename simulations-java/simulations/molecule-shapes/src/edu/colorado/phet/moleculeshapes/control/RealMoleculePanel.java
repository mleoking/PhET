// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculeshapes.control;

import java.awt.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import edu.colorado.phet.common.jmolphet.JmolPanel;
import edu.colorado.phet.moleculeshapes.MoleculeShapesConstants;
import edu.colorado.phet.moleculeshapes.model.RealMolecule;

public class RealMoleculePanel extends JPanel {
    public RealMoleculePanel( RealMolecule molecule ) {
        super( new BorderLayout() );

        // TODO: i18n
        add( new JmolPanel( molecule, "Loading..." ) {{
                 setPreferredSize( new Dimension( (int) MoleculeShapesConstants.CONTROL_PANEL_INNER_WIDTH, (int) MoleculeShapesConstants.CONTROL_PANEL_INNER_WIDTH ) );
             }}, BorderLayout.CENTER );

        setBorder( new EmptyBorder( 0, 0, 0, 0 ) );
    }
}
