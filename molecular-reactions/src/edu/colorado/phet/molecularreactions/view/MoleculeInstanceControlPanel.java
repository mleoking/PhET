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

import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.molecularreactions.model.*;
import edu.colorado.phet.molecularreactions.util.ControlBorderFactory;
import edu.colorado.phet.molecularreactions.MRConfig;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

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
    private int maxMoleculeCnt = MRConfig.MAX_MOLECULE_CNT;

    public MoleculeInstanceControlPanel( MRModel model ) {

        JLabel aLabel = new JLabel( new MoleculeIcon( MoleculeA.class ) );
        JLabel cLabel = new JLabel(  new MoleculeIcon( MoleculeC.class )  );
        JLabel abLabel = new JLabel( new MoleculeIcon( MoleculeAB.class ) );
        JLabel bcLabel = new JLabel( new MoleculeIcon( MoleculeBC.class ) );

        MoleculeCountSpinner aMC = new MoleculeCountSpinner( MoleculeA.class, model, maxMoleculeCnt );
        counters.add( aMC );
        MoleculeCountSpinner cMC = new MoleculeCountSpinner( MoleculeC.class, model, maxMoleculeCnt );
        counters.add( cMC );
        MoleculeCountSpinner abMC = new MoleculeCountSpinner( MoleculeAB.class, model, maxMoleculeCnt );
        counters.add( abMC );
        MoleculeCountSpinner bcMC = new MoleculeCountSpinner( MoleculeBC.class, model, maxMoleculeCnt );
        counters.add( bcMC );

        // Lay out the controls
        setBorder( ControlBorderFactory.createPrimaryBorder( SimStrings.get("Control.numMolecules")) );
        setLayout( new GridBagLayout() );
        Insets insets = new Insets( 2, 0, 2, 0 );
//        Insets insets = new Insets( 2, 2, 2, 2 );
        GridBagConstraints gbc = new GridBagConstraints( 0,
                                                         GridBagConstraints.RELATIVE,
                                                         1, 1, 1, 1,
                                                         GridBagConstraints.CENTER,
                                                         GridBagConstraints.NONE,
                                                         insets,
                                                         0, 0 );
        add( aLabel, gbc );
        add( bcLabel, gbc );
        add( abLabel, gbc );
        add( cLabel, gbc );

        gbc.gridx = 1;
        gbc.gridy = GridBagConstraints.RELATIVE;
        gbc.anchor = GridBagConstraints.WEST;
        add( aMC, gbc );
        add( bcMC, gbc );
        add( abMC, gbc );
        add( cMC, gbc );
    }

    public void setCountersEditable( boolean editable ) {
        for( int i = 0; i < counters.size(); i++ ) {
            MoleculeCountSpinner moleculeCounter = (MoleculeCountSpinner)counters.get( i );
            moleculeCounter.setEditable( editable );
        }
    }
}
