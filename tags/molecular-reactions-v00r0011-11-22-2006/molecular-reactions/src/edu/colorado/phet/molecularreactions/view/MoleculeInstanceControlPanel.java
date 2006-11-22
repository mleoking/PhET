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
 * Panel that has spinners for the number of each type of molecule
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class MoleculeInstanceControlPanel extends JPanel {
    private List counters = new ArrayList();
    private int maxMoleculeCnt = MRConfig.MAX_MOLECULE_CNT;
    private JLabel aLabel = new JLabel();
    private JLabel cLabel = new JLabel();
    private JLabel abLabel = new JLabel();
    private JLabel bcLabel = new JLabel();

    public MoleculeInstanceControlPanel( final MRModel model ) {

        // Add a listener to the model that will update the icons if the energy profile changes
        model.addListener( new MRModel.ModelListener() {
            public void energyProfileChanged( EnergyProfile profile ) {
                updateIcons( model.getEnergyProfile() );
            }
        } );
        updateIcons( model.getEnergyProfile() );

        MoleculeCountSpinner aMC = new MoleculeCountSpinner( MoleculeA.class, model, maxMoleculeCnt );
        counters.add( aMC );
        MoleculeCountSpinner cMC = new MoleculeCountSpinner( MoleculeC.class, model, maxMoleculeCnt );
        counters.add( cMC );
        MoleculeCountSpinner abMC = new MoleculeCountSpinner( MoleculeAB.class, model, maxMoleculeCnt );
        counters.add( abMC );
        MoleculeCountSpinner bcMC = new MoleculeCountSpinner( MoleculeBC.class, model, maxMoleculeCnt );
        counters.add( bcMC );

        // Lay out the controls
        setBorder( ControlBorderFactory.createPrimaryBorder( SimStrings.get( "Control.numMolecules" ) ) );
        setLayout( new GridBagLayout() );
        Insets insets = new Insets( 2, 0, 2, 0 );
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

    private void updateIcons( EnergyProfile profile ) {
        aLabel.setIcon( new MoleculeIcon( MoleculeA.class, profile ) );
        cLabel.setIcon( new MoleculeIcon( MoleculeC.class, profile ) );
        abLabel.setIcon( new MoleculeIcon( MoleculeAB.class, profile ) );
        bcLabel.setIcon( new MoleculeIcon( MoleculeBC.class, profile ) );
    }

    public void setCountersEditable( boolean editable ) {
        for( int i = 0; i < counters.size(); i++ ) {
            MoleculeCountSpinner moleculeCounter = (MoleculeCountSpinner)counters.get( i );
            moleculeCounter.setEditable( editable );
        }
    }
}
