// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.reactionsandrates.view;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.reactionsandrates.MRConfig;
import edu.colorado.phet.reactionsandrates.model.*;
import edu.colorado.phet.reactionsandrates.util.ControlBorderFactory;
import edu.colorado.phet.reactionsandrates.view.icons.MoleculeIcon;

/**
 * MoleculeInstanceControlPanel
 * <p/>
 * Panel that has spinners for the number of each type of molecule
 *
 * @author Ron LeMaster
 */
public class MoleculeInstanceControlPanel extends JPanel {
    
    private List counters = new ArrayList();
    private JLabel aLabel = new JLabel();
    private JLabel cLabel = new JLabel();
    private JLabel abLabel = new JLabel();
    private JLabel bcLabel = new JLabel();
    private final ClearContainerButton clearContainerButton;

    public MoleculeInstanceControlPanel( final MRModel model, IClock clock ) {

        // Add a listener to the model that will update the icons if the energy profile changes
        model.addListener( new MRModel.ModelListenerAdapter() {
            public void notifyEnergyProfileChanged( EnergyProfile profile ) {
                updateIcons( model.getEnergyProfile() );
            }
        } );
        updateIcons( model.getEnergyProfile() );

        MoleculeCountSpinner aMC = new MoleculeCountSpinner( MoleculeA.class, model, 0, MRConfig.MAX_MOLECULES );
        counters.add( aMC );
        MoleculeCountSpinner cMC = new MoleculeCountSpinner( MoleculeC.class, model, 0, MRConfig.MAX_MOLECULES );
        counters.add( cMC );
        MoleculeCountSpinner abMC = new MoleculeCountSpinner( MoleculeAB.class, model, 0, MRConfig.MAX_MOLECULES );
        counters.add( abMC );
        MoleculeCountSpinner bcMC = new MoleculeCountSpinner( MoleculeBC.class, model, 0, MRConfig.MAX_MOLECULES );
        counters.add( bcMC );
        
        // Lay out the controls
        setBorder( ControlBorderFactory.createPrimaryBorder( MRConfig.RESOURCES.getLocalizedString( "Control.numMolecules" ) ) );
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
        
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        clearContainerButton = new ClearContainerButton( model, clock );
        add( clearContainerButton, gbc );
    }

    private void updateIcons( EnergyProfile profile ) {
        aLabel.setIcon( new MoleculeIcon( MoleculeA.class, profile ) );
        cLabel.setIcon( new MoleculeIcon( MoleculeC.class, profile ) );
        abLabel.setIcon( new MoleculeIcon( MoleculeAB.class, profile ) );
        bcLabel.setIcon( new MoleculeIcon( MoleculeBC.class, profile ) );
    }
    
    public void setClearContainerButtonVisible( boolean visible ) {
        clearContainerButton.setVisible( visible );
    }

    public void setCountersEditable( boolean editable ) {
        for( int i = 0; i < counters.size(); i++ ) {
            MoleculeCountSpinner moleculeCounter = (MoleculeCountSpinner)counters.get( i );
            moleculeCounter.setEnabled( editable );
        }
    }
    
    private static class ClearContainerButton extends JButton {
        public ClearContainerButton( final MRModel model, final IClock clock ) {
            super( MRConfig.RESOURCES.getLocalizedString( "Control.clearContainer" ) );
            addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    model.removeAllMolecules();
                    clock.resetSimulationTime();
                }
            });
        }
    }
}
