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

import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.common.view.ControlPanel;
import edu.colorado.phet.common.view.ApparatusPanel2;
import edu.colorado.phet.common.view.phetgraphics.PhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetImageGraphic;
import edu.colorado.phet.flourescent.model.*;
import edu.colorado.phet.flourescent.view.ElectronGraphic;
import edu.colorado.phet.lasers.model.ResonatingCavity;
import edu.colorado.phet.lasers.model.atom.Atom;
import edu.colorado.phet.lasers.model.atom.GroundState;
import edu.colorado.phet.lasers.model.atom.MiddleEnergyState;
import edu.colorado.phet.lasers.model.atom.HighEnergyState;
import edu.colorado.phet.lasers.view.ResonatingGraphic;
import edu.colorado.phet.lasers.view.AtomGraphic;
import edu.colorado.phet.lasers.controller.LaserConfig;
import edu.colorado.phet.lasers.controller.module.BaseLaserModule;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

/**
 * ModuleA
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class ModuleA extends BaseLaserModule implements ElectronSource.Listener {
//public class ModuleA extends Module implements ElectronSource.Listener {
    private ElectronSink anode;
    private Cathode cathode;
    private double s_maxSpeed = 0.1;

    protected ModuleA( AbstractClock clock ) {
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
        Cathode cathode = addCathode( model, apparatusPanel );

        // Add the anode to the model
        addAnode( model, apparatusPanel, cathode );

        // Add the tube
        ResonatingCavity tube = new ResonatingCavity( FluorescentLightsConfig.TUBE_ULC,
                                                      FluorescentLightsConfig.TUBE_LENGTH,
                                                      FluorescentLightsConfig.TUBE_HEIGHT );
        model.addModelElement( tube );
        ResonatingGraphic tubeGraphic = new ResonatingGraphic( getApparatusPanel(), tube );
        apparatusPanel.addGraphic( tubeGraphic, FluorescentLightsConfig.TUBE_LAYER );

        // Add some atoms
        Atom atom = null;
        ArrayList atoms = new ArrayList();
        Rectangle2D tubeBounds = tube.getBounds();
        int numAtoms = 30;
        int numEnergyLevels = 3;
        for( int i = 0; i < numAtoms; i++ ) {
            atom = new Atom( getModel(), numEnergyLevels );
            atom.setPosition( ( tubeBounds.getX() + ( Math.random() ) * ( tubeBounds.getWidth() - atom.getRadius() * 4 ) + atom.getRadius() * 2 ),
                              ( tubeBounds.getY() + ( Math.random() ) * ( tubeBounds.getHeight() - atom.getRadius() * 4 ) ) + atom.getRadius() * 2 );
            atom.setVelocity( (float)( Math.random() - 0.5 ) * s_maxSpeed,
                              (float)( Math.random() - 0.5 ) * s_maxSpeed );
            atoms.add( atom );
            addAtom( atom );
        }
    }


    /**
     * @param model
     * @param apparatusPanel
     * @param cathode
     */
    private void addAnode( FluorescentLightModel model, ApparatusPanel apparatusPanel, Cathode cathode ) {
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
    private Cathode addCathode( FluorescentLightModel model, ApparatusPanel apparatusPanel ) {
        Cathode cathode = new Cathode( model,
                                       FluorescentLightsConfig.CATHODE_LINE.getP1(),
                                       FluorescentLightsConfig.CATHODE_LINE.getP2() );
        model.addModelElement( cathode );
        cathode.addListener( this );
        this.cathode = cathode;
        this.cathode.setElectronsPerSecond( 0.01 );
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

    public void electronProduced( ElectronSource.ElectronSourceEvent event ) {
        ElectronGraphic graphic = new ElectronGraphic( getApparatusPanel(), event.getElectron() );
        getApparatusPanel().addGraphic( graphic, FluorescentLightsConfig.ELECTRON_LAYER );
        anode.addListener( new AbsorptionListener( event.getElectron(), graphic ) );
    }

    //-----------------------------------------------------------------
    // Inner classes
    //-----------------------------------------------------------------

    /**
     * Listens for the absorption of an electron. When such an event happens,
     * its graphic is taken off the apparatus panel
     */
    private class AbsorptionListener implements ElectronSink.Listener {
        private Electron electron;
        private ElectronGraphic graphic;

        AbsorptionListener( Electron electron, ElectronGraphic graphic ) {
            this.electron = electron;
            this.graphic = graphic;
        }

        public void electronAbsorbed( ElectronSink.ElectronSinkEvent event ) {
            if( event.getElectron() == electron ) {
                getApparatusPanel().removeGraphic( graphic );
                anode.removeListener( this );
            }
        }
    }
}
