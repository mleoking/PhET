/**
 * Class: TestModule
 * Package: edu.colorado.phet.nuclearphysics.modules
 * Author: Another Guy
 * Date: Feb 26, 2004
 */
package edu.colorado.phet.nuclearphysics.controller;

import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.nuclearphysics.model.*;
import edu.colorado.phet.nuclearphysics.view.Kaboom;
import edu.colorado.phet.nuclearphysics.view.NeutronGraphic;
import edu.colorado.phet.nuclearphysics.view.NucleusGraphic;

import java.awt.geom.Point2D;
import java.util.List;
import java.util.Random;

public class SingleNucleusFissionModule extends ProfiledNucleusModule
        implements NeutronGun, FissionListener {
    private static Random random = new Random();
    private Neutron neutronToAdd;
    private AbstractClock clock;
    private double orgDt;
    private Uranium235 nucleus;
    private Neutron neutron;

    public SingleNucleusFissionModule( AbstractClock clock ) {

        super( "Fission: One Nucleus", clock );
        this.clock = clock;
        super.addControlPanelElement( new SingleNucleusFissionControlPanel( this ) );
        getModel().addModelElement( new ModelElement() {
            public void stepInTime( double dt ) {
                if( SingleNucleusFissionModule.this.neutronToAdd != null ) {
                    SingleNucleusFissionModule.this.addNeutron( neutronToAdd );
                    SingleNucleusFissionModule.this.neutronToAdd = null;
                }
            }
        } );

        // Add a model element that will watch for collisions between the
        // nucleus and neutron
        getModel().addModelElement( new ModelElement() {
            public void stepInTime( double dt ) {
                if( neutron != null
                    && neutron.getLocation().distanceSq( nucleus.getLocation() )
                       <= nucleus.getRadius() * nucleus.getRadius() ) {
                    nucleus.fission( neutron );
                }
            }
        } );


    }

    public void stop() {
        getPhysicalPanel().clear();
        getPotentialProfilePanel().removeAllGraphics();
        getModel().removeModelElement( nucleus );
//        setNucleus( null );
//        setUraniumNucleus( null );
    }

    public void start() {
        nucleus = new Uranium235( new Point2D.Double( 0, 0 ), getModel() );
        nucleus.setPotential( nucleus.getPotentialProfile().getWellPotential() );
        setNucleus( nucleus );
        setUraniumNucleus( nucleus );
        getPotentialProfilePanel().addNucleusGraphic( nucleus );
        nucleus.addFissionListener( this );
        nucleus.setDoMorph( true );
        nucleus.addObserver( getNucleus().getPotentialProfile() );
    }

    public void activate( PhetApplication app ) {
        orgDt = clock.getDt();
        clock.setDt( orgDt / 4 );
        this.start();
    }

    public void deactivate( PhetApplication app ) {
        clock.setDt( orgDt );
        this.stop();
    }

    public void fireNeutron() {
        double bounds = 600;
        double gamma = random.nextDouble() * Math.PI * 2;
        double x = bounds * Math.cos( gamma );
        double y = bounds * Math.sin( gamma );
        neutron = new Neutron( new Point2D.Double( x, y ), gamma + Math.PI );
        super.addNeutron( neutron );
    }

    public void fission( final FissionProducts products ) {

        // Constrain velocity of the daughter nuclei to be more or less horizontal
        double theta = ( random.nextDouble() * Math.PI / 2 ) - ( Math.PI / 4 );
        Nucleus daughter1 = products.getDaughter1();
        double v1 = daughter1.getVelocity().getLength();
        daughter1.setVelocity( (float)( v1 * Math.cos( theta ) ), (float)( v1 * Math.sin( theta ) ) );
        Nucleus daughter2 = products.getDaughter2();
        double v2 = daughter2.getVelocity().getLength();
        daughter2.setVelocity( (float)( v2 * Math.cos( theta + Math.PI ) ), (float)( v2 * Math.sin( theta + Math.PI ) ) );

// Remove the neutron and old nucleus
        getModel().removeModelElement( products.getInstigatingNeutron() );
        getModel().removeModelElement( products.getParent() );
        List graphics = (List)NucleusGraphic.getGraphicForNucleus( products.getParent() );
        for( int i = 0; i < graphics.size(); i++ ) {
            NucleusGraphic ng = (NucleusGraphic)graphics.get( i );
            getPotentialProfilePanel().removeGraphic( ng );
            this.getPhysicalPanel().removeGraphic( ng );
        }
        NeutronGraphic ng = (NeutronGraphic)NeutronGraphic.getGraphicForNeutron( products.getInstigatingNeutron() );
        this.getPhysicalPanel().removeGraphic( ng );
        getPotentialProfilePanel().removeNucleusGraphic( products.getParent() );

// Remove the potential profile for the old nucleus and replace it with a gray one
//        potentialProfilePanel.removePotentialProfile( products.getParent().getPotentialProfile() );
//        potentialProfilePanel.addNucleus( products.getParent(), Color.gray );

// Add fission products
        Neutron[] neutronProducts = products.getNeutronProducts();
        for( int i = 0; i < neutronProducts.length; i++ ) {
            NeutronGraphic npg = new NeutronGraphic( neutronProducts[i] );
            getModel().addModelElement( neutronProducts[i] );
            getPhysicalPanel().addGraphic( npg );
        }

// Add a model element that will make the daughter nuclei slide down the
// profile
        getModel().addModelElement( new ModelElement() {
            private double forceScale = 0.05;
//            private double forceScale = 0.0005;

            public void stepInTime( double dt ) {
                stepDaughterNucleus( products.getParent(), products.getDaughter1() );
                stepDaughterNucleus( products.getParent(), products.getDaughter2() );
            }

            private void stepDaughterNucleus( Nucleus parent, Nucleus daughter ) {
                double d = daughter.getLocation().distance( parent.getLocation() );
                Vector2D a = null;
                PotentialProfile profile = parent.getPotentialProfile();
                double force = Math.abs( profile.getHillY( -d ) ) * forceScale;
                force = Double.isNaN( force ) ? 0 : force;
                force = -profile.getDyDx( -d ) * forceScale;
                if( daughter.getVelocity().getX() == 0 && daughter.getVelocity().getY() == 0 ) {
                    double dx = daughter.getLocation().getX() - parent.getLocation().getX();
                    double dy = daughter.getLocation().getY() - parent.getLocation().getY();
                    a = new Vector2D( (float)dx, (float)dy ).normalize().multiply( (float)force );
                }
                else {
                    a = new Vector2D( daughter.getVelocity() ).normalize().multiply( (float)force );
                }
                daughter.setAcceleration( a );

// Set the nucleus' potential energy. If the nucles isn't outside the peaks of the
// profile, it's potential keeps it at the top of the profile. Otherwise, it slides
// down the profile
                double potential = 0;
// I don't know why the -10 is needed here, but it is. I don't have time to figure out why
                if( Math.abs( d ) <= Math.abs( profile.getProfilePeakX() - 7 ) ) {
                    potential = profile.getMaxPotential();
                }
                else {
                    potential = Double.isNaN( -profile.getHillY( -d ) ) ? 0 : -profile.getHillY( -d );
                }
                daughter.setPotential( potential );
            }
        } );

        Nucleus dn1 = products.getDaughter1();
        Nucleus dn2 = products.getDaughter2();
        dn2.setLocation( 0, 0 );
        super.addNucleus( dn1, null );
        super.addNucleus( dn2, null );
        getPotentialProfilePanel().addNucleusGraphic( dn1 );
        getPotentialProfilePanel().addNucleusGraphic( dn2 );

// Add some pizzazz
        Kaboom kaboom = new Kaboom( new Point2D.Double( 0, 0 ),
                                    25, 300, getPhysicalPanel() );
        getPhysicalPanel().addGraphic( kaboom );
    }


//
// Inner classes
//
    private class InternalNeutronGun implements Runnable {
        private long waitTime = 1000;
        private boolean kill = false;

        public void run() {
            while( kill == false ) {
                try {
                    Thread.sleep( waitTime );
                    this.fireNeutron();
                }
                catch( InterruptedException e ) {
                    e.printStackTrace();
                }
            }
        }

        public synchronized void kill() {
            this.kill = true;
        }

        public void fireNeutron() {
            double bounds = 600;
            double gamma = random.nextDouble() * Math.PI * 2;
            double x = bounds * Math.cos( gamma );
            double y = bounds * Math.sin( gamma );
            Neutron neutron = new Neutron( new Point2D.Double( x, y ), gamma + Math.PI * 2 );
            SingleNucleusFissionModule.this.neutronToAdd = neutron;
        }
    }

}
