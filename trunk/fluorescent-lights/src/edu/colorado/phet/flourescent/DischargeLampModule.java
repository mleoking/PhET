/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.flourescent;

import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.common.view.ApparatusPanel2;
import edu.colorado.phet.common.view.ControlPanel;
import edu.colorado.phet.common.view.components.ModelSlider;
import edu.colorado.phet.common.view.phetgraphics.PhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetImageGraphic;
import edu.colorado.phet.flourescent.model.*;
import edu.colorado.phet.flourescent.view.ElectronGraphic;
import edu.colorado.phet.lasers.controller.module.BaseLaserModule;
import edu.colorado.phet.lasers.model.LaserModel;
import edu.colorado.phet.lasers.model.ResonatingCavity;
import edu.colorado.phet.lasers.view.ResonatingCavityGraphic;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

/**
 * DischargeLampModule
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class DischargeLampModule extends BaseLaserModule implements ElectronSource.ElectronProductionListener {
    private ElectronSink anode;
    private ElectronSource cathode;
    private double s_maxSpeed = 0.1;

    protected DischargeLampModule( AbstractClock clock ) {
        super( "Module A", clock );

        ApparatusPanel apparatusPanel = new ApparatusPanel2( clock );
        apparatusPanel.setBackground( Color.white );
        setApparatusPanel( apparatusPanel );

        FluorescentLightModel model = new FluorescentLightModel();
        setModel( model );

        setControlPanel( new ControlPanel( this ) );
        PhetGraphic circuitGraphic = new PhetImageGraphic( getApparatusPanel(), "images/battery-w-wires.png" );
        circuitGraphic.setLocation( FluorescentLightsConfig.CIRCUIT_ULC );
        apparatusPanel.addGraphic( circuitGraphic, FluorescentLightsConfig.CIRCUIT_LAYER );

        // Add the cathode to the model
        addCathode( model, apparatusPanel );

        // Add the anode to the model
        addAnode( model, apparatusPanel, cathode );

        // Set the cathode to listen for potential changes relative to the anode
        hookCathodeToAnode();

        // Add the tube
        ResonatingCavity tube = addTube( model, apparatusPanel );

        // Add some atoms
        addAtoms( tube );

        // Set up the control panel
        addControls();
    }

    /**
     * Sets up the control panel
     */
    private void addControls() {
        final ModelSlider batterySlider = new ModelSlider( "Battery Voltage", "V", 0, .1, 0 );
        batterySlider.setPreferredSize( new Dimension( 200, 70 ) );
        ControlPanel controlPanel = (ControlPanel)getControlPanel();
        controlPanel.add( batterySlider );

        batterySlider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                cathode.setPotential( batterySlider.getValue() );
                anode.setPotential( 0 );
            }
        } );
    }

    /**
     * Creates the tube, adds it to the model and creates a graphic for it
     * @param model
     * @param apparatusPanel
     * @return
     */
    private ResonatingCavity addTube( FluorescentLightModel model, ApparatusPanel apparatusPanel ) {
        ResonatingCavity tube = new ResonatingCavity( FluorescentLightsConfig.TUBE_ULC,
                                                      FluorescentLightsConfig.TUBE_LENGTH,
                                                      FluorescentLightsConfig.TUBE_HEIGHT );
        model.addModelElement( tube );
        ResonatingCavityGraphic tubeGraphic = new ResonatingCavityGraphic( getApparatusPanel(), tube );
        apparatusPanel.addGraphic( tubeGraphic, FluorescentLightsConfig.TUBE_LAYER );
        return tube;
    }

    /**
     * Adds some atoms and their graphics
     * @param tube
     */
    private void addAtoms( ResonatingCavity tube ) {
        DischargeLampAtom atom = null;
        ArrayList atoms = new ArrayList();
        Rectangle2D tubeBounds = tube.getBounds();
        int numAtoms = 30;
        int numEnergyLevels = 3;
        for( int i = 0; i < numAtoms; i++ ) {
            atom = new DischargeLampAtom( (LaserModel)getModel(), numEnergyLevels );
            atom.setPosition( ( tubeBounds.getX() + ( Math.random() ) * ( tubeBounds.getWidth() - atom.getRadius() * 4 ) + atom.getRadius() * 2 ),
                              ( tubeBounds.getY() + ( Math.random() ) * ( tubeBounds.getHeight() - atom.getRadius() * 4 ) ) + atom.getRadius() * 2 );
            atom.setVelocity( (float)( Math.random() - 0.5 ) * s_maxSpeed,
                              (float)( Math.random() - 0.5 ) * s_maxSpeed );
            atoms.add( atom );
            addAtom( atom );
        }
    }

    /**
     * Adds some atoms and their graphics
     * @param tube
     */
    private void addDebugAtoms( ResonatingCavity tube ) {


        Point2D p0 = new Point2D.Double( FluorescentLightsConfig.CATHODE_LINE.getP1().getX(),
                                         (FluorescentLightsConfig.CATHODE_LINE.getP1().getY() +
                                       FluorescentLightsConfig.CATHODE_LINE.getP2().getY() ) / 2  - 20);

        cathode = new ElectronSource( getModel(), p0, p0 );

        DischargeLampAtom atom = null;
        ArrayList atoms = new ArrayList();
        Rectangle2D tubeBounds = tube.getBounds();
        int numAtoms = 1;
//        int numAtoms = 30;
        int numEnergyLevels = 3;
        for( int i = 0; i < numAtoms; i++ ) {
            atom = new DischargeLampAtom( (LaserModel)getModel(), numEnergyLevels );
            atom.setPosition( ( tubeBounds.getX() + 150 ),
                              ( tubeBounds.getY() + tubeBounds.getHeight() / 2 - atom.getRadius()));
            atom.setVelocity( 0,0 );
            atoms.add( atom );
            addAtom( atom );
        }
    }

    /**
     * Creates a listener that manages the production rate of the cathode based on its potential
     * relative to the anode
     */
    private void hookCathodeToAnode() {
        anode.addStateChangeListener( new Electrode.StateChangeListener() {
            public void stateChanged( Electrode.StateChangeEvent event ) {
                double anodePotential = event.getElectrode().getPotential();
                cathode.setSinkPotential( anodePotential );
            }
        } );
    }

    /**
     * @param model
     * @param apparatusPanel
     * @param cathode
     */
    private void addAnode( FluorescentLightModel model, ApparatusPanel apparatusPanel, ElectronSource cathode ) {
        ElectronSink anode = new ElectronSink( model,
                                               FluorescentLightsConfig.ANODE_LINE.getP1(),
                                               FluorescentLightsConfig.ANODE_LINE.getP2() );
        model.addModelElement( anode );
        this.anode = anode;
        this.anode.setPosition( FluorescentLightsConfig.ANODE_LOCATION );
        PhetGraphic anodeGraphic = new PhetImageGraphic( getApparatusPanel(), "images/electrode.png" );
        anodeGraphic.setRegistrationPoint( 0, (int)anodeGraphic.getBounds().getHeight() / 2 );
        anodeGraphic.setLocation( FluorescentLightsConfig.ANODE_LOCATION );
        apparatusPanel.addGraphic( anodeGraphic, FluorescentLightsConfig.CIRCUIT_LAYER );
        cathode.addListener( anode );
    }

    /**
     * @param model
     * @param apparatusPanel
     * @return
     */
    private ElectronSource addCathode( FluorescentLightModel model, ApparatusPanel apparatusPanel ) {
        cathode = new ElectronSource( model,
                                       FluorescentLightsConfig.CATHODE_LINE.getP1(),
                                       FluorescentLightsConfig.CATHODE_LINE.getP2() );
        model.addModelElement( cathode );
        cathode.addListener( this );
        cathode.setElectronsPerSecond( 0 );
        cathode.setPosition( FluorescentLightsConfig.CATHODE_LOCATION );
        PhetGraphic cathodeGraphic = new PhetImageGraphic( getApparatusPanel(), "images/electrode.png" );
        cathodeGraphic.setRegistrationPoint( (int)cathodeGraphic.getBounds().getWidth(),
                                             (int)cathodeGraphic.getBounds().getHeight() / 2 );
        cathodeGraphic.setLocation( FluorescentLightsConfig.CATHODE_LOCATION );
        apparatusPanel.addGraphic( cathodeGraphic, FluorescentLightsConfig.CIRCUIT_LAYER );
        return cathode;
    }


    //----------------------------------------------------------------
    // Interface implementations
    //----------------------------------------------------------------

    public void electronProduced( ElectronSource.ElectronProductionEvent event ) {
        Electron electron = event.getElectron();

        // Create a graphic for the electron
        ElectronGraphic graphic = new ElectronGraphic( getApparatusPanel(), electron );
        getApparatusPanel().addGraphic( graphic, FluorescentLightsConfig.ELECTRON_LAYER );
        anode.addListener( new AbsorptionElectronAbsorptionListener( electron, graphic ) );
    }

    //-----------------------------------------------------------------
    // Inner classes
    //-----------------------------------------------------------------

    /**
     * Listens for the absorption of an electron. When such an event happens,
     * its graphic is taken off the apparatus panel
     */
    private class AbsorptionElectronAbsorptionListener implements ElectronSink.ElectronAbsorptionListener {
        private Electron electron;
        private ElectronGraphic graphic;

        AbsorptionElectronAbsorptionListener( Electron electron, ElectronGraphic graphic ) {
            this.electron = electron;
            this.graphic = graphic;
        }

        public void electronAbsorbed( ElectronSink.ElectronAbsorptionEvent event ) {
            if( event.getElectron() == electron ) {
                getApparatusPanel().removeGraphic( graphic );
                anode.removeListener( this );
            }
        }
    }
}
