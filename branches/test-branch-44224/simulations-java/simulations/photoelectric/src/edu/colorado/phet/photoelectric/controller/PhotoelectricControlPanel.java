/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.photoelectric.controller;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import edu.colorado.phet.common.phetcommon.view.ControlPanel;
import edu.colorado.phet.dischargelamps.model.DischargeLampElementProperties;
import edu.colorado.phet.dischargelamps.model.EnergyAbsorptionStrategy;
import edu.colorado.phet.photoelectric.PhotoelectricResources;
import edu.colorado.phet.photoelectric.model.MetalEnergyAbsorptionStrategy;
import edu.colorado.phet.photoelectric.model.PhotoelectricModel;
import edu.colorado.phet.photoelectric.model.PhotoelectricTarget;
import edu.colorado.phet.photoelectric.model.SimpleEnergyAbsorptionStrategy;
import edu.colorado.phet.photoelectric.module.PhotoelectricModule;
import edu.colorado.phet.photoelectric.view.CompositeGraphPanel;

/**
 * PhotoelectricControlPanel
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class PhotoelectricControlPanel {
    private static final int ELECTRON_MODEL_SIMPLE = 1;
    private static final int ELECTRON_MODEL_REALISTIC = 2;

    private Collection targetMaterials;

    public PhotoelectricControlPanel( final PhotoelectricModule module ) {
        final PhotoelectricModel model = (PhotoelectricModel)module.getModel();

        final ControlPanel controlPanel = module.getControlPanel();

        //----------------------------------------------------------------
        // Target controls
        //----------------------------------------------------------------
        {
            JPanel targetControlPnl = new JPanel( new GridBagLayout() );
            targetControlPnl.setBorder( new TitledBorder( PhotoelectricResources.getString( "Target" ) ) );
            controlPanel.addFullWidth( targetControlPnl );

            // Put the targetMaterials in the desired order. Sodium should be at the top, and the "mystery material",
            // magnesium, should be at the end
            ArrayList selectionList = new ArrayList();
            selectionList.add( PhotoelectricTarget.SODIUM );
            targetMaterials = PhotoelectricTarget.TARGET_MATERIALS;
            for( Iterator iterator = targetMaterials.iterator(); iterator.hasNext(); ) {
                Object obj = iterator.next();
                if( obj != PhotoelectricTarget.SODIUM && obj != PhotoelectricTarget.MAGNESIUM ) {
                    selectionList.add( obj );
                }
            }
            selectionList.add( PhotoelectricTarget.MAGNESIUM );

            final JComboBox targetMaterial = new JComboBox( selectionList.toArray() );
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.anchor = GridBagConstraints.CENTER;
            targetControlPnl.add( targetMaterial, gbc );
            final PhotoelectricTarget target = model.getTarget();
            targetMaterial.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    target.setTargetMaterial( (DischargeLampElementProperties)targetMaterial.getSelectedItem() );
                }
            } );
            target.setTargetMaterial( (DischargeLampElementProperties)targetMaterial.getSelectedItem() );
        }

        //----------------------------------------------------------------
        // Selection of electron model
        //----------------------------------------------------------------
        {
            JPanel electronModelPanel = new JPanel( new GridBagLayout() );
            electronModelPanel.setBorder( new TitledBorder( "" ) );
            GridBagConstraints gbc = new GridBagConstraints( GridBagConstraints.RELATIVE, 0,
                                                             1, 1, 1, 1,
                                                             GridBagConstraints.CENTER,
                                                             GridBagConstraints.NONE,
                                                             new Insets( 0, 0, 0, 0 ), 0, 0 );

            final JCheckBox electronModelCB = new JCheckBox( PhotoelectricResources.getString( "ControlPanel.SimpleMode" ) );
            electronModelCB.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    if( electronModelCB.isSelected() ) {
                        setElectronModel( PhotoelectricModel.ELECTRON_MODEL_SIMPLE );
                    }
                    else {
                        setElectronModel( PhotoelectricModel.ELECTRON_MODEL_REALISTIC );
                    }
                }
            } );
            electronModelPanel.add( electronModelCB, gbc );
            controlPanel.addFullWidth( electronModelPanel );
        }

        //----------------------------------------------------------------
        // Graph options
        //----------------------------------------------------------------
        controlPanel.addControlFullWidth( new CompositeGraphPanel( module ) );
    }

    /**
     * Sets the energy absorption strategy for all target materials.
     *
     * @param electronModelType
     */
    private void setElectronModel( int electronModelType ) {
        EnergyAbsorptionStrategy energyAbsorptionStrategy = null;
        switch( electronModelType ) {
            case ELECTRON_MODEL_SIMPLE:
//                energyAbsorptionStrategy = new FiftyPercentAbsorptionStrategy();
                for( Iterator iterator = targetMaterials.iterator(); iterator.hasNext(); ) {
                    DischargeLampElementProperties targetProperties = (DischargeLampElementProperties)iterator.next();
                    energyAbsorptionStrategy = new SimpleEnergyAbsorptionStrategy( targetProperties.getWorkFunction() );
                    targetProperties.setEnergyAbsorptionStrategy( energyAbsorptionStrategy );
                }
                break;
            case ELECTRON_MODEL_REALISTIC:
                for( Iterator iterator = targetMaterials.iterator(); iterator.hasNext(); ) {
                    DischargeLampElementProperties targetProperties = (DischargeLampElementProperties)iterator.next();
                    energyAbsorptionStrategy = new MetalEnergyAbsorptionStrategy( targetProperties.getWorkFunction() );
                    targetProperties.setEnergyAbsorptionStrategy( energyAbsorptionStrategy );
                }
                break;
            default:
                throw new RuntimeException( "invalid parameter" );
        }
    }
}

