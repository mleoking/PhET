/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.nuclearphysics.controller;

import edu.colorado.phet.common.model.clock.IClock;
import edu.colorado.phet.nuclearphysics.model.Nucleus;
import edu.colorado.phet.nuclearphysics.view.PotentialProfilePanel;
import edu.colorado.phet.nuclearphysics.view.EnergyProfilePanel;

import java.awt.*;

/**
 * A module that presents a PhysicalPanel at the top and PotentialProfilePanel below.
 */
public abstract class ProfiledNucleusModule extends NuclearPhysicsModule {

    private Nucleus nucleus;
    private EnergyProfilePanel energyProfilePanel;
    private GridBagConstraints physicalPanelGBC;

    public ProfiledNucleusModule( String name, IClock clock ) {
        super( name, clock );
    }

    protected void init() {
        getApparatusPanel().setLayout( new GridBagLayout() );
        physicalPanelGBC = new GridBagConstraints( 0,0,1,1,1,1,
                                                   GridBagConstraints.CENTER,
                                                   GridBagConstraints.BOTH,
                                                   new Insets( 0,0,0,0), 0,0 );
        GridBagConstraints profilePanelGBC = new GridBagConstraints( 0,1,1,1,1,.75,
                                                                     GridBagConstraints.CENTER,
                                                                     GridBagConstraints.BOTH,
                                                                     new Insets( 0,0,0,0), 0,0 );

        energyProfilePanel = new EnergyProfilePanel( getClock() );
        getApparatusPanel().add( energyProfilePanel, profilePanelGBC );
    }

    protected void addPhysicalPanel( Component component ) {
        physicalPanelGBC.gridy = 0;
        getApparatusPanel().add( component, physicalPanelGBC );
    }

    public void setNucleus( Nucleus nucleus ) {
        this.nucleus = nucleus;
    }

    protected Nucleus getNucleus() {
        return nucleus;
    }

    protected void addNucleus( Nucleus nucleus ) {
        super.addNucleus( nucleus );
        energyProfilePanel.addEnergyProfile( nucleus );
    }

    protected void addNucleus( Nucleus nucleus, Color color ) {
        super.addNucleus( nucleus );
        energyProfilePanel.addNucleus( nucleus, color );
    }

    protected EnergyProfilePanel getEnergyProfilePanel() {
        return energyProfilePanel;
    }

    public void clear() {
        getEnergyProfilePanel().clear();
        this.getModel().removeModelElement( nucleus );
    }
}
