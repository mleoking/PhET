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

import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.nuclearphysics.model.*;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Random;

/**
 * ChainReactionModule
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public abstract class ChainReactionModule extends NuclearPhysicsModule implements NeutronGun, FissionListener {
    protected static Random random = new Random();
    protected static int s_maxPlacementAttempts = 100;
    protected Neutron neutronToAdd;
    protected ArrayList nuclei = new ArrayList();
    protected ArrayList u235Nuclei = new ArrayList();
    protected ArrayList u238Nuclei = new ArrayList();
    protected ArrayList u239Nuclei = new ArrayList();
    protected ArrayList neutrons = new ArrayList();
    private long orgDelay;
    private double orgDt;
    protected double neutronLaunchGamma;
    protected Point2D.Double neutronLaunchPoint;
    protected Line2D.Double neutronPath;

    public ChainReactionModule( String name, AbstractClock clock ) {
        super( name, clock );
    }

    public abstract void start();

    public void stop() {

        // The class should be re-written so that everything is taken care
        // of by the nuclei list, and the others don't need to be iterated
        // here
        for( int i = 0; i < nuclei.size(); i++ ) {
            removeNucleus( (Nucleus)nuclei.get( i ) );
        }
        for( int i = 0; i < u235Nuclei.size(); i++ ) {
            removeNucleus( (Nucleus)u235Nuclei.get( i ) );
        }
        for( int i = 0; i < u238Nuclei.size(); i++ ) {
            removeNucleus( (Nucleus)u238Nuclei.get( i ) );
        }
        for( int i = 0; i < u239Nuclei.size(); i++ ) {
            removeNucleus( (Nucleus)u239Nuclei.get( i ) );
        }
        for( int i = 0; i < neutrons.size(); i++ ) {
            Neutron neutron = (Neutron)neutrons.get( i );
            getModel().removeModelElement( neutron );
        }
        nuclei.clear();
        neutrons.clear();
        u239Nuclei.clear();
        u235Nuclei.clear();
        u238Nuclei.clear();
    }

    protected abstract void computeNeutronLaunchParams();

    protected Line2D.Double getNeutronPath() {
        return neutronPath;
    }

    public void activate( PhetApplication app ) {
        super.activate( app );
        orgDelay = getClock().getDelay();
        orgDt = getClock().getDt();
        getClock().setDelay( 10 );
        getClock().setDt( orgDt * 0.6 );
        this.start();
    }

    public void deactivate( PhetApplication app ) {
        super.deactivate( app );
        getClock().setDelay( (int)( orgDelay ) );
        getClock().setDt( orgDt );
        this.stop();
    }

    public ArrayList getNuclei() {
        return nuclei;
    }

    public ArrayList getU235Nuclei() {
        return u235Nuclei;
    }

    public ArrayList getU238Nuclei() {
        return u238Nuclei;
    }

    public Nucleus addU235Nucleus() {
        Nucleus nucleus = null;
        Point2D.Double location = findLocationForNewNucleus();
        if( location != null ) {
            nucleus = new Uranium235( location, getModel() );
            u235Nuclei.add( nucleus );
            addNucleus( nucleus );
        }
        return nucleus;
    }

    public void addU238Nucleus() {
        Point2D.Double location = findLocationForNewNucleus();
        if( location != null ) {
            Nucleus nucleus = new Uranium238( location, getModel() );
            u238Nuclei.add( nucleus );
            addNucleus( nucleus );
        }
    }

    public void addU239Nucleus( Uranium239 nucleus ) {
        u239Nuclei.add( nucleus );
        addNucleus( nucleus );
    }

    protected void addNucleus( Nucleus nucleus ) {
        nuclei.add( nucleus );
        nucleus.addFissionListener( this );
        getPhysicalPanel().addNucleus( nucleus );
        getModel().addModelElement( nucleus );
    }

    public void removeU235Nucleus( Uranium235 nucleus ) {
        u235Nuclei.remove( nucleus );
        removeNucleus( nucleus );
    }

    public void removeU238Nucleus( Uranium238 nucleus ) {
        u238Nuclei.remove( nucleus );
        removeNucleus( nucleus );
    }

    public void removeNucleus( Nucleus nucleus ) {
        nuclei.remove( nucleus );
        //        getPhysicalPanel().removeNucleus( nucleus );
        getModel().removeModelElement( nucleus );
    }

    public void fireNeutron() {
        Neutron neutron = new Neutron( neutronLaunchPoint, neutronLaunchGamma + Math.PI );
        neutrons.add( neutron );
        addNeutron( neutron );
    }

    public abstract void fission( FissionProducts products );

    protected abstract Point2D.Double findLocationForNewNucleus();
}
