/**
 * Class: ProfiledNucleusModule
 * Package: edu.colorado.phet.nuclearphysics.view
 * Author: Another Guy
 * Date: Mar 7, 2004
 */
package edu.colorado.phet.nuclearphysics.controller;

import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.nuclearphysics.model.Nucleus;
import edu.colorado.phet.nuclearphysics.model.Uranium235;
import edu.colorado.phet.nuclearphysics.view.PotentialProfilePanel;

import java.awt.*;
import java.awt.geom.Point2D;

public class ProfiledNucleusModule extends NuclearPhysicsModule {

    private Uranium235 nucleus;
    private PotentialProfilePanel potentialProfilePanel;

    public ProfiledNucleusModule( String name, AbstractClock clock ) {
        super( name, clock );

        getApparatusPanel().setLayout( new GridLayout( 2, 1 ) );
        potentialProfilePanel = new PotentialProfilePanel();
        getApparatusPanel().add( potentialProfilePanel, 1 );
        nucleus = new Uranium235( new Point2D.Double( 0, 0 ) );
        setUraniumNucleus( getNucleus() );
    }

    public void setNucleus( Uranium235 nucleus ) {
        this.nucleus = nucleus;
    }

    protected Uranium235 getNucleus() {
        return nucleus;
    }

    protected void addNeucleus( Nucleus nucleus ) {
        super.addNeucleus( nucleus );
        potentialProfilePanel.addNucleus( nucleus );
    }

    protected PotentialProfilePanel getPotentialProfilePanel() {
        return potentialProfilePanel;
    }

    public void clear() {
        getPhysicalPanel().clear();
        getPotentialProfilePanel().clear();
        this.getModel().removeModelElement( nucleus );
    }

    public void run() {
        clear();
        nucleus = new Uranium235( new Point2D.Double( 0, 0 ) );
        setUraniumNucleus( nucleus );
    }
}
