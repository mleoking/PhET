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

import edu.colorado.phet.common.view.ControlPanel;
import edu.colorado.phet.lasers.model.atom.ElementProperties;
import edu.colorado.phet.lasers.model.atom.EnergyAbsorptionStrategy;
import edu.colorado.phet.photoelectric.model.MetalEnergyAbsorptionStrategy;
import edu.colorado.phet.photoelectric.model.PhotoelectricModel;
import edu.colorado.phet.photoelectric.model.PhotoelectricTarget;
import edu.colorado.phet.photoelectric.model.SimpleEnergyAbsorptionStrategy;
import edu.colorado.phet.photoelectric.module.PhotoelectricModule;
import edu.colorado.phet.photoelectric.view.CurrentVsIntensityGraph;
import edu.colorado.phet.photoelectric.view.CurrentVsVoltageGraph;
import edu.colorado.phet.photoelectric.view.EnergyVsFrequencyGraph;
import edu.colorado.phet.photoelectric.view.GraphPanel;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

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

        final ControlPanel controlPanel = (ControlPanel)module.getControlPanel();

        //----------------------------------------------------------------
        // Target controls
        //----------------------------------------------------------------
        {
            JPanel targetControlPnl = new JPanel( new GridLayout( 1, 1 ) );
            targetControlPnl.setBorder( new TitledBorder( "Target" ) );
            controlPanel.addFullWidth( targetControlPnl );

            // Put the targetMaterials in the desired order. Sodium should be at the top, and the "mystery material",
            // magnesium, should be at the end
            ArrayList selectionList = new ArrayList();
            selectionList.add( PhotoelectricTarget.SODIUM );
            targetMaterials = PhotoelectricTarget.TARGET_MATERIALS;
            for( Iterator iterator = targetMaterials.iterator(); iterator.hasNext(); ) {
                Object obj = (Object)iterator.next();
                if( obj != PhotoelectricTarget.SODIUM && obj != PhotoelectricTarget.MAGNESIUM ) {
                    selectionList.add( obj );
                }
            }
            selectionList.add( PhotoelectricTarget.MAGNESIUM );

            final JComboBox targetMaterial = new JComboBox( selectionList.toArray() );
            targetControlPnl.add( targetMaterial );
            final PhotoelectricTarget target = model.getTarget();
            targetMaterial.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    target.setTargetMaterial( (ElementProperties)targetMaterial.getSelectedItem() );
                }
            } );
            target.setTargetMaterial( (ElementProperties)targetMaterial.getSelectedItem() );
        }

        //----------------------------------------------------------------
        // Selection of electron model
        //----------------------------------------------------------------
        {
            JPanel electronModelPanel = new JPanel( new GridBagLayout() );
//            electronModelPanel.setBorder( new LineBorder( Color.gray ) );
            electronModelPanel.setBorder( new TitledBorder( "" ) );
//            electronModelPanel.setBorder( new TitledBorder( "Electron model" ) );
            GridBagConstraints gbc = new GridBagConstraints( GridBagConstraints.RELATIVE, 0,
                                                             1, 1, 1, 1,
                                                             GridBagConstraints.CENTER,
                                                             GridBagConstraints.NONE,
                                                             new Insets( 0, 0, 0, 0 ), 0, 0 );

            final JCheckBox electronModelCB = new JCheckBox( "Show only highest energy electrons" );
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

            // Radio buttons to select the electron model
//            JRadioButton simpleRB = new JRadioButton( "Simple" );
//            simpleRB.addActionListener( new ActionListener() {
//                public void actionPerformed( ActionEvent e ) {
//                    setElectronModel( PhotoelectricModel.ELECTRON_MODEL_SIMPLE );
//                }
//            } );
//            JRadioButton realisticRB = new JRadioButton( "Realistic", true );
//            realisticRB.addActionListener( new ActionListener() {
//                public void actionPerformed( ActionEvent e ) {
//                    setElectronModel( PhotoelectricModel.ELECTRON_MODEL_REALISTIC );
//                }
//            } );
//            ButtonGroup rbg = new ButtonGroup();
//            rbg.add( simpleRB );
//            rbg.add( realisticRB );
//            JPanel rbPanel = new JPanel( new GridLayout( 1, 2 ) );
//            rbPanel.add( simpleRB, gbc );
//            rbPanel.add( realisticRB, gbc );
//            electronModelPanel.add( rbPanel, gbc );
            controlPanel.addFullWidth( electronModelPanel );
        }

        //----------------------------------------------------------------
        // Graph options
        //----------------------------------------------------------------
        {
            Insets graphInsets = new Insets( 5, 20, 20, 15 );
            final JCheckBox currentVsVoltageCB = new JCheckBox( "<html>Current vs battery voltage</html>" );
            final GraphPanel cvgPanel = new GraphPanel( module.getClock() );
            CurrentVsVoltageGraph currentVsVoltageGraph = new CurrentVsVoltageGraph( cvgPanel, model );
            cvgPanel.setGraph( currentVsVoltageGraph, graphInsets );
            final JCheckBox currentVsIntensityCB = new JCheckBox( "<html>Current vs light intensity</html>" );
            final GraphPanel cviPanel = new GraphPanel( module.getClock() );
            CurrentVsIntensityGraph currentVsIntensityGraph = new CurrentVsIntensityGraph( cviPanel, model );
            cviPanel.setGraph( currentVsIntensityGraph, graphInsets );
            final JCheckBox energyVsFrequencyCB = new JCheckBox( "<html>Electron energy vs light frequency</html>" );
            final GraphPanel evfPanel = new GraphPanel( module.getClock() );
            EnergyVsFrequencyGraph energyVsFrequencyGraph = new EnergyVsFrequencyGraph( evfPanel, model );
            evfPanel.setGraph( energyVsFrequencyGraph, graphInsets );
            currentVsVoltageCB.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    cvgPanel.setVisible( currentVsVoltageCB.isSelected() );
                    controlPanel.setLogoVisible( !currentVsVoltageCB.isSelected()
                                                 && !currentVsIntensityCB.isSelected()
                                                 && !energyVsFrequencyCB.isSelected() );
                }
            } );
            cvgPanel.setVisible( currentVsVoltageCB.isSelected() );

            currentVsIntensityCB.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    cviPanel.setVisible( currentVsIntensityCB.isSelected() );
                    controlPanel.setLogoVisible( !currentVsVoltageCB.isSelected()
                                                 && !currentVsIntensityCB.isSelected()
                                                 && !energyVsFrequencyCB.isSelected() );
                }
            } );
            cviPanel.setVisible( currentVsIntensityCB.isSelected() );

            energyVsFrequencyCB.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    evfPanel.setVisible( energyVsFrequencyCB.isSelected() );
                    controlPanel.setLogoVisible( !currentVsVoltageCB.isSelected()
                                                 && !currentVsIntensityCB.isSelected()
                                                 && !energyVsFrequencyCB.isSelected() );
                }
            } );
            evfPanel.setVisible( energyVsFrequencyCB.isSelected() );

            JPanel graphOptionsPanel = new JPanel();
            graphOptionsPanel.setLayout( new GridBagLayout() );
            GridBagConstraints gbc = new GridBagConstraints( 0, GridBagConstraints.RELATIVE,
                                                             1, 1, 0, 0,
                                                             GridBagConstraints.NORTHWEST,
                                                             GridBagConstraints.NONE,
                                                             new Insets( 0, 0, 0, 0 ), 0, 0 );
            graphOptionsPanel.setBorder( new TitledBorder( "Graphs" ) );
            graphOptionsPanel.add( currentVsVoltageCB, gbc );
            graphOptionsPanel.add( cvgPanel, gbc );
            graphOptionsPanel.add( currentVsIntensityCB, gbc );
            graphOptionsPanel.add( cviPanel, gbc );
            graphOptionsPanel.add( energyVsFrequencyCB, gbc );
            graphOptionsPanel.add( evfPanel, gbc );
            controlPanel.addControlFullWidth( graphOptionsPanel );
        }
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
                    ElementProperties targetProperties = (ElementProperties)iterator.next();
                    energyAbsorptionStrategy = new SimpleEnergyAbsorptionStrategy( targetProperties.getWorkFunction() );
                    targetProperties.setEnergyAbsorptionStrategy( energyAbsorptionStrategy );
                }
                break;
            case ELECTRON_MODEL_REALISTIC:
                for( Iterator iterator = targetMaterials.iterator(); iterator.hasNext(); ) {
                    ElementProperties targetProperties = (ElementProperties)iterator.next();
                    energyAbsorptionStrategy = new MetalEnergyAbsorptionStrategy( targetProperties.getWorkFunction() );
                    targetProperties.setEnergyAbsorptionStrategy( energyAbsorptionStrategy );
                }
                break;
            default:
                throw new RuntimeException( "invalid parameter" );
        }
    }
}

