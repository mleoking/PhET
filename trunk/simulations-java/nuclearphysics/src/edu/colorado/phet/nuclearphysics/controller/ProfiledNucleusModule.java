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
import edu.colorado.phet.coreadditions.TxGraphic;
import edu.colorado.phet.nuclearphysics.model.Nucleus;
import edu.colorado.phet.nuclearphysics.model.ProfileableNucleus;
import edu.colorado.phet.nuclearphysics.view.EnergyProfileGraphic;
import edu.colorado.phet.nuclearphysics.view.EnergyProfilePanelGraphic;

import java.awt.*;
import java.awt.geom.AffineTransform;

/**
 * A module that presents a PhysicalPanel at the top and PotentialProfilePanel below.
 * <p.
 * The apparatus panel has a physical representation, and overlaid on it is a graphic showing the
 * energy profile for the nucleus.
 * <p>
 * The code to get things to lay out in the right places is, unfortunately, spread out and obtuse. The basic origin
 * is in the middel of the apparatus panel, but calling setOrigin() on the PhysicalPanel translates the origin from
 * there.
 */
public abstract class ProfiledNucleusModule extends NuclearPhysicsModule {

    private ProfileableNucleus nucleus;
    private EnergyProfilePanelGraphic energyProfilePanel;
    private GridBagConstraints physicalPanelGBC;
    private EnergyProfileGraphic.ProfileType profileType;

    public ProfiledNucleusModule( String name, IClock clock, EnergyProfileGraphic.ProfileType profileType ) {
        super( name, clock );
        this.profileType = profileType;
    }

    /**
     *
     */
    protected void init() {
        getApparatusPanel().setLayout( new GridBagLayout() );
        physicalPanelGBC = new GridBagConstraints( 0, 0, 1, 1, 1, 1,
                                                   GridBagConstraints.CENTER,
                                                   GridBagConstraints.BOTH,
                                                   new Insets( 0, 0, 0, 0 ), 0, 0 );
        energyProfilePanel = new EnergyProfilePanelGraphic( getApparatusPanel(), 
                                                            profileType,
                                                            getEnergyLegendHeader(),
                                                            getPotentialEnergyLegend(),
                                                            getTotalEnergyLegend() );
        energyProfilePanel.setVisible( true );
    }

    /**
     *
     * @param component
     */
    protected void addPhysicalPanel( Component component ) {
        physicalPanelGBC.gridy = 0;

        // Add the energy profile panel
        AffineTransform atx = new AffineTransform( getPhysicalPanel().getNucleonTx() );
        atx.translate( -energyProfilePanel.getWidth() / 2, 130 );
        TxGraphic txg = new TxGraphic( energyProfilePanel, atx ) ;
        getPhysicalPanel().addOriginCenteredGraphic( txg, 1E12 );
        getApparatusPanel().add( component, physicalPanelGBC );
    }

    public void setNucleus( ProfileableNucleus nucleus ) {
        this.nucleus = nucleus;
    }

    protected ProfileableNucleus getNucleus() {
        return nucleus;
    }

    protected void addNucleus( Nucleus nucleus ) {
        getModel().addModelElement( nucleus );
    }

    protected void addNucleus( Nucleus nucleus, Color color ) {
        super.addNucleus( nucleus );
    }

    protected EnergyProfilePanelGraphic getEnergyProfilePanel() {
        return energyProfilePanel;
    }

    public void clear() {
        getEnergyProfilePanel().clear();
        this.getModel().removeModelElement( nucleus );
    }

    //--------------------------------------------------------------------------------------------------
    // Abstract methods
    //--------------------------------------------------------------------------------------------------
    abstract protected String getEnergyLegendHeader();

    abstract protected String getPotentialEnergyLegend();

    abstract protected String getTotalEnergyLegend();

}
