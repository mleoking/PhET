/**
 * Class: TestModule
 * Package: edu.colorado.phet.nuclearphysics.modules
 * Author: Another Guy
 * Date: Feb 26, 2004
 */
package edu.colorado.phet.nuclearphysics.controller;

import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.nuclearphysics.model.FissionListener;
import edu.colorado.phet.nuclearphysics.model.FissionProducts;
import edu.colorado.phet.nuclearphysics.model.Neutron;
import edu.colorado.phet.nuclearphysics.model.Uranium235;
import edu.colorado.phet.nuclearphysics.view.Kaboom;
import edu.colorado.phet.nuclearphysics.view.NeutronGraphic;
import edu.colorado.phet.nuclearphysics.view.NucleusGraphic;
import edu.colorado.phet.nuclearphysics.view.PotentialProfilePanel;

import java.awt.geom.Point2D;
import java.util.List;
import java.util.Random;

public class SingleNucleusFissionModule extends ProfiledNucleusModule
        implements NeutronGun, FissionListener {
    private static Random random = new Random();
    private Neutron neutronToAdd;
    private AbstractClock clock;
    private double orgDt;

    public SingleNucleusFissionModule( AbstractClock clock ) {
        super( "Fission: One Nucleus", clock );
        super.addControlPanelElement( new SingleNucleusFissionControlPanel( this ) );
        this.clock = clock;

        setNucleus( new Uranium235( new Point2D.Double( 0, 0 ) ) );
        setUraniumNucleus( getNucleus() );
        getNucleus().addFissionListener( this );
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
        orgDt = clock.getDt();
        clock.setDt( orgDt / 4 );
    }

    public void deactivate( PhetApplication app ) {
        clock.setDt( orgDt );
    }

    public void fireNeutron() {
        double bounds = 600;
        double gamma = random.nextDouble() * Math.PI * 2;
        double x = bounds * Math.cos( gamma );
        double y = bounds * Math.sin( gamma );
        Neutron neutron = new Neutron( new Point2D.Double( x, y ), gamma + Math.PI );
        super.addNeutron( neutron );
    }

    private void checkForFission() {
        for( int i = 0; i < getModel().numModelElements(); i++ ) {
            ModelElement me = getModel().modelElementAt( i );
            if( me instanceof Neutron ) {
                Neutron neutron = (Neutron)me;
                for( int j = 0; j < getModel().numModelElements(); j++ ) {
                    ModelElement me2 = getModel().modelElementAt( j );
                    if( me2 instanceof Uranium235 ) {
                        Uranium235 nucleus = (Uranium235)me2;
                        if( neutron.getLocation().distanceSq( nucleus.getLocation() )
                            < nucleus.getRadius() * nucleus.getRadius() ) {
                            nucleus.fission( neutron );
                        }
                    }
                }
            }
        }
    }

    public void fission( FissionProducts products ) {

        PotentialProfilePanel potentialProfilePanel = this.getPotentialProfilePanel();

        // Remove the neutron and old nucleus
        getModel().removeModelElement( products.getInstigatingNeutron() );
        getModel().removeModelElement( products.getParent() );
        List graphics = (List)NucleusGraphic.getGraphicForNucleus( products.getParent() );
        for( int i = 0; i < graphics.size(); i++ ) {
            NucleusGraphic ng = (NucleusGraphic)graphics.get( i );
            potentialProfilePanel.removeGraphic( ng );
            this.getPhysicalPanel().removeGraphic( ng );
        }
        NeutronGraphic ng = (NeutronGraphic)NeutronGraphic.getGraphicForNeutron( products.getInstigatingNeutron() );
        this.getPhysicalPanel().removeGraphic( ng );

        // Remove the potential profile for the old nucleus
        potentialProfilePanel.removePotentialProfile( products.getParent().getPotentialProfile() );

        // Add fission products
        super.addNeucleus( products.getDaughter1() );
        super.addNeucleus( products.getDaughter2() );
        Neutron[] neutronProducts = products.getNeutronProducts();
        for( int i = 0; i < neutronProducts.length; i++ ) {
            NeutronGraphic npg = new NeutronGraphic( neutronProducts[i] );
            getModel().addModelElement( neutronProducts[i] );
            getPhysicalPanel().addGraphic( npg );
        }

        // Add some pizzazz
        Kaboom kaboom = new Kaboom( new Point2D.Double( 0, 0 ),
                                    25, 300, getApparatusPanel() );
//                                    25, 300, getPotentialProfilePanel() );
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
