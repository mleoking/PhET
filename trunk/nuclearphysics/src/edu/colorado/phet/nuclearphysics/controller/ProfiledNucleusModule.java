/**
 * Class: ProfiledNucleusModule
 * Package: edu.colorado.phet.nuclearphysics.view
 * Author: Another Guy
 * Date: Mar 7, 2004
 */
package edu.colorado.phet.nuclearphysics.controller;

import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.nuclearphysics.model.Nucleus;
import edu.colorado.phet.nuclearphysics.view.PotentialProfilePanel;

import java.awt.*;

public class ProfiledNucleusModule extends NuclearPhysicsModule {

    private Nucleus nucleus;
    private PotentialProfilePanel potentialProfilePanel;

    public ProfiledNucleusModule( String name, AbstractClock clock ) {
        super( name, clock );

        getApparatusPanel().setLayout( new GridLayout( 2, 1 ) );
        potentialProfilePanel = new PotentialProfilePanel();
        getApparatusPanel().add( potentialProfilePanel, 1 );
    }

    public void setNucleus( Nucleus nucleus ) {
        this.nucleus = nucleus;
    }

    protected Nucleus getNucleus() {
        return nucleus;
    }

    protected void addNucleus( Nucleus nucleus ) {
        super.addNucleus( nucleus );
        potentialProfilePanel.addNucleus( nucleus );
    }

    protected void addNucleus( Nucleus nucleus, Color color ) {
        super.addNucleus( nucleus );
        potentialProfilePanel.addNucleus( nucleus, color );
    }

    protected PotentialProfilePanel getPotentialProfilePanel() {
        return potentialProfilePanel;
    }

    public void clear() {
        getPhysicalPanel().clear();
        getPotentialProfilePanel().clear();
        this.getModel().removeModelElement( nucleus );
    }
}
