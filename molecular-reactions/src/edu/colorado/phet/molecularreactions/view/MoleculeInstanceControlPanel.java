/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.molecularreactions.view;

import edu.colorado.phet.molecularreactions.model.*;
import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.common.math.Vector2D;

import java.util.List;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/**
 * MoleculeInstanceControlPanel
 * <p>
 * Panel that shows the number of each type of molecule
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class MoleculeInstanceControlPanel extends JPanel {

    public MoleculeInstanceControlPanel( MRModel model ) {
        super( new GridBagLayout() );

        Insets insets = new Insets( 2, 2, 2, 2 );

        JLabel aLabel = new JLabel( "A" );
        JLabel bLabel = new JLabel( "B" );
        JLabel cLabel = new JLabel( "C" );
        JLabel abLabel = new JLabel( "AB" );
        JLabel bcLabel = new JLabel( "BC" );

        JTextField aTF = new MoleculeCounter( 2, MoleculeA.class, model );
        JTextField bTF = new MoleculeCounter( 2, MoleculeB.class, model );
        bTF.setEditable( false );
        JTextField cTF = new MoleculeCounter( 2, MoleculeC.class, model );
        JTextField abTF = new MoleculeCounter( 2, MoleculeAB.class, model );
        JTextField bcTF = new MoleculeCounter( 2, MoleculeBC.class, model );

        GridBagConstraints gbc = new GridBagConstraints( 0,
                                                         GridBagConstraints.RELATIVE,
                                                         1, 1, 1, 1,
                                                         GridBagConstraints.EAST,
                                                         GridBagConstraints.NONE,
                                                         insets,
                                                         0, 0 );
        add( aLabel, gbc );
        add( bLabel, gbc );
        add( cLabel, gbc );
        add( abLabel, gbc );
        add( bcLabel, gbc );

        gbc.gridx = 1;
        gbc.gridy = GridBagConstraints.RELATIVE;
        gbc.anchor = GridBagConstraints.WEST;
        add( aTF, gbc );
        add( bTF, gbc );
        add( cTF, gbc );
        add(abTF, gbc );
        add(bcTF, gbc );
    }
}
