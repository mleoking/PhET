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
import edu.colorado.phet.nuclearphysics.model.Neutron;
import edu.colorado.phet.nuclearphysics.model.Nucleus;
import edu.colorado.phet.nuclearphysics.model.PotentialProfile;
import edu.colorado.phet.nuclearphysics.model.Uranium235;
import edu.colorado.phet.nuclearphysics.view.Kaboom;
import edu.colorado.phet.nuclearphysics.view.NeutronGraphic;
import edu.colorado.phet.nuclearphysics.view.NucleusGraphic;
import edu.colorado.phet.nuclearphysics.view.PotentialProfilePanel;

import java.awt.geom.Point2D;
import java.util.List;
import java.util.Random;

public class SingleNucleusFissionModule extends ProfiledNucleusModule implements NeutronGun {
    private static Random random = new Random();
    private static float neutronSpeed = 1f;
    private static double fissionDisplacementVelocity = 2;
    private Neutron neutronToAdd;

    public SingleNucleusFissionModule( AbstractClock clock ) {
        super( "Fission: One Nucleus", clock );
        super.addControlPanelElement( new SingleNucleusFissionControlPanel( this ) );
        getModel().addModelElement( new ModelElement() {
            public void stepInTime( double dt ) {
                checkForFission();
                if( SingleNucleusFissionModule.this.neutronToAdd != null ) {
                    SingleNucleusFissionModule.this.addNeutron( neutronToAdd );
                    SingleNucleusFissionModule.this.neutronToAdd = null;
                }
            }
        } );
    }

    public void activate( PhetApplication app ) {
//        neutronGun = new NeutronGun();
//        Thread neutronGunThread = new Thread( neutronGun );
//        neutronGunThread.start();
    }

    public void deactivate( PhetApplication app ) {
//        neutronGun.kill();
    }

    public void fireNeutron() {
        double theta = random.nextDouble() * Math.PI * 2;
        double r = 200;
        double x = r * Math.cos( theta );
        double y = r * Math.sin( theta );

        Neutron neutron = new Neutron( new Point2D.Double( x, y ) );
        neutron.setVelocity( ( new Vector2D( (float)-x, (float)-y ) ).normalize().multiply( 5f ) );
        super.addNeutron( neutron );
    }

    private void checkForFission() {
        for( int i = 0; i < getModel().numModelElements(); i++ ) {
            ModelElement me = getModel().modelElementAt( i );
            if( me instanceof Neutron ) {
                for( int j = 0; j < getModel().numModelElements(); j++ ) {
                    ModelElement me2 = getModel().modelElementAt( j );
                    if( me2 instanceof Uranium235 ) {
                        if( ( (Neutron)me ).getLocation().distanceSq( ( (Nucleus)me2 ).getLocation() )
                            < ( (Nucleus)me2 ).getRadius() * ( (Nucleus)me2 ).getRadius() ) {
                            fission( (Nucleus)me2, (Neutron)me );
                        }
                    }
                }
            }
        }
    }

    private void fission( Nucleus nucleus, Neutron neutron ) {
        PotentialProfilePanel potentialProfilePanel = this.getPotentialProfilePanel();

        // Remove the neutron and old nucleus
        getModel().removeModelElement( neutron );
        getModel().removeModelElement( nucleus );
        List graphics = (List)NucleusGraphic.getGraphicForNucleus( nucleus );
        for( int i = 0; i < graphics.size(); i++ ) {
            NucleusGraphic ng = (NucleusGraphic)graphics.get( i );
            potentialProfilePanel.removeGraphic( ng );
            this.getPhysicalPanel().removeGraphic( ng );
        }
        NeutronGraphic ng = (NeutronGraphic)NeutronGraphic.getGraphicForNeutron( neutron );
        this.getPhysicalPanel().removeGraphic( ng );

        // Remove the potential profile for the old nucleus
        potentialProfilePanel.removePotentialProfile( nucleus.getPotentialProfile() );

        // Add an intermediate profile
        nucleus.getPotentialProfile().setWellDepth( -10 );
//        nucleus.getPotentialProfile().setWellDepth( -nucleus.getPotentialProfile().getMaxPotential() - nucleus.getPotentialProfile().getWellPotential() );
        potentialProfilePanel.addPotentialProfile( nucleus );
        potentialProfilePanel.repaint();
//        try {
//            Thread.sleep( 2000 );
//        }
//        catch( InterruptedException e ) {
//            e.printStackTrace();
//        }

        // create fission products
        PotentialProfile fissionProductProfile = new PotentialProfile( nucleus.getPotentialProfile().getWidth() / 2,
                                                                       nucleus.getPotentialProfile().getMaxPotential() / 2,
                                                                       nucleus.getPotentialProfile().getWellPotential() );
        double theta = random.nextDouble() * Math.PI;
        double vx = fissionDisplacementVelocity * Math.cos( theta );
        double vy = fissionDisplacementVelocity * Math.sin( theta );
        Nucleus n1 = new Nucleus( new Point2D.Double( nucleus.getLocation().getX(), nucleus.getLocation().getX() ),
                                  nucleus.getNumProtons() / 2, nucleus.getNumNeutrons() / 2, fissionProductProfile );
        n1.setVelocity( (float)( -vx ), (float)( -vy ) );
        Nucleus n2 = new Nucleus( new Point2D.Double( nucleus.getLocation().getX(), nucleus.getLocation().getX() ),
                                  nucleus.getNumProtons() / 2, nucleus.getNumNeutrons() / 2, fissionProductProfile );
        n2.setVelocity( (float)( vx ), (float)( vy ) );
        super.addNeucleus( n1 );
        super.addNeucleus( n2 );

        for( int i = 0; i < 3; i++ ) {
            Neutron np = new Neutron( nucleus.getLocation() );
            np.setVelocity( random.nextFloat() * 10 * ( random.nextBoolean() ? 1 : -1 ),
                            random.nextFloat() * 10 * ( random.nextBoolean() ? 1 : -1 ) );
            NeutronGraphic npg = new NeutronGraphic( np );
            getModel().addModelElement( np );
            getPhysicalPanel().addGraphic( npg );
        }

        // Add some pizzazz
        Kaboom kaboom = new Kaboom( new Point2D.Double( 0, 0 ),
                                    25, 300, getPotentialProfilePanel() );
        getPhysicalPanel().addGraphic( kaboom );

        // Add one energy profile for each of the daughter nuclei

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
            double theta = gamma
                           + Math.PI
                           + ( random.nextDouble() * Math.PI / 4 ) * ( random.nextBoolean() ? 1 : -1 );

            Neutron neutron = new Neutron( new Point2D.Double( x, y ) );
            neutron.setVelocity( ( new Vector2D( (float)Math.cos( theta ),
                                                 (float)Math.sin( theta ) ) ).normalize().multiply( neutronSpeed ) );
            SingleNucleusFissionModule.this.neutronToAdd = neutron;
        }

    }
}
