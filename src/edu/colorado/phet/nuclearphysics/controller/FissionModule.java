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
import edu.colorado.phet.nuclearphysics.model.Uranium235;
import edu.colorado.phet.nuclearphysics.view.*;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;
import java.util.List;
import java.util.Random;

public class FissionModule extends OneNucleusModule {
    private static Random random = new Random();
    private static float neutronSpeed = 1f;
    private static double fissionDisplacement = 50;
    private NeutronGun neutronGun;
    private Neutron neutronToAdd;

    public FissionModule( AbstractClock clock ) {
        super( "Fission", clock );
        super.addControlPanelElement( new FissionControlPanel( this ) );
        getModel().addModelElement( new ModelElement() {
            public void stepInTime( double dt ) {
                checkForFission();
                if( FissionModule.this.neutronToAdd != null ) {
                    FissionModule.this.addNeutron( neutronToAdd );
                    FissionModule.this.neutronToAdd = null;
                }
            }
        } );
    }

    public void activate( PhetApplication app ) {
        neutronGun = new NeutronGun();
        Thread neutronGunThread = new Thread( neutronGun );
        neutronGunThread.start();
    }

    public void deactivate( PhetApplication app ) {
        neutronGun.kill();
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
        // Remove the neutron and old nucleus
        getModel().removeModelElement( neutron );
        getModel().removeModelElement( nucleus );
        List graphics = (List)NucleusGraphic.getGraphicForNucleus( nucleus );
        for( int i = 0; i < graphics.size(); i++ ) {
            NucleusGraphic ng = (NucleusGraphic)graphics.get( i );
            this.getPotentialProfilePanel().removeGraphic( ng );
            this.getPhysicalPanel().removeGraphic( ng );
        }
        NeutronGraphic ng = (NeutronGraphic)NeutronGraphic.getGraphicForNeutron( neutron );
        this.getPotentialProfilePanel().removeGraphic( ng );
        this.getPhysicalPanel().removeGraphic( ng );

        // create fission products
        double theta = random.nextDouble() * Math.PI;
        double dx = fissionDisplacement * Math.cos( theta );
        double dy = fissionDisplacement * Math.sin( theta );
        Nucleus n1 = new Nucleus( new Point2D.Double( nucleus.getLocation().getX() - dx, nucleus.getLocation().getX() - dy ),
                                  nucleus.getNumProtons() / 2, nucleus.getNumNeutrons() / 2, nucleus.getPotentialProfile() );
        Nucleus n2 = new Nucleus( new Point2D.Double( nucleus.getLocation().getX() + dx, nucleus.getLocation().getX() + dy ),
                                  nucleus.getNumProtons() / 2, nucleus.getNumNeutrons() / 2, nucleus.getPotentialProfile() );
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

        // Remove the old potential panel and add one for each of the new nuclei
        getApparatusPanel().remove( getPotentialProfilePanel() );
        JPanel newPotentialProfilePanel = new JPanel( new GridLayout( 2, 1 ) );
        PotentialProfilePanel ppp1 = new PotentialProfilePanel( n1.getPotentialProfile() );
        PotentialProfilePanel ppp2 = new PotentialProfilePanel( n2.getPotentialProfile() );
        newPotentialProfilePanel.add( ppp1 );
        newPotentialProfilePanel.add( ppp2 );
        getApparatusPanel().add( newPotentialProfilePanel, 0 );
        getApparatusPanel().validate();

    }

    //
    // Inner classes
    //
    private class NeutronGun implements Runnable {
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
            FissionModule.this.neutronToAdd = neutron;
        }

    }
}
