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

import java.util.List;
import java.util.ArrayList;
import javax.swing.*;
import java.awt.*;

/**
 * MoleculeInstanceControlPanel
 * <p/>
 * Panel that shows the number of each type of molecule
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class MoleculeInstanceControlPanel extends JPanel {
    private List counters = new ArrayList( );

    public MoleculeInstanceControlPanel( MRModel model ) {
        super( new GridBagLayout() );

        Insets insets = new Insets( 2, 2, 2, 2 );

        JLabel aLabel = new JLabel( "A" );
        JLabel bLabel = new JLabel( "B" );
        JLabel cLabel = new JLabel( "C" );
        JLabel abLabel = new JLabel( "AB" );
        JLabel bcLabel = new JLabel( "BC" );

        MoleculeCounter aMC = new MoleculeCounter( MoleculeA.class, model );
        counters.add( aMC );
        MoleculeCounter cMC = new MoleculeCounter( MoleculeC.class, model );
        counters.add( cMC );
        MoleculeCounter abMC = new MoleculeCounter( MoleculeAB.class, model );
        counters.add( abMC );
        MoleculeCounter bcMC = new MoleculeCounter( MoleculeBC.class, model );
        counters.add( bcMC );

        GridBagConstraints gbc = new GridBagConstraints( 0,
                                                         GridBagConstraints.RELATIVE,
                                                         1, 1, 1, 1,
                                                         GridBagConstraints.EAST,
                                                         GridBagConstraints.NONE,
                                                         insets,
                                                         0, 0 );
        add( aLabel, gbc );
        add( abLabel, gbc );
        add( bcLabel, gbc );
        add( cLabel, gbc );

        gbc.gridx = 1;
        gbc.gridy = GridBagConstraints.RELATIVE;
        gbc.anchor = GridBagConstraints.WEST;
        add( aMC, gbc );
        add( abMC, gbc );
        add( bcMC, gbc );
        add( cMC, gbc );
    }

    public void setCountersEditable( boolean editable ) {
        for( int i = 0; i < counters.size(); i++ ) {
            MoleculeCounter moleculeCounter = (MoleculeCounter)counters.get( i );
            moleculeCounter.setEditable( editable );
        }
    }
}
