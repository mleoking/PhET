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
    }

    public void stop() {
        getPhysicalPanel().clear();
        getPotentialProfilePanel().removeAllGraphics();
    }

    public void start() {
        Uranium235 nucleus = new Uranium235( new Point2D.Double( 0, 0 ), getModel() );
        setNucleus( nucleus );
        setUraniumNucleus( nucleus );
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
        Neutron neutron = new Neutron( new Point2D.Double( x, y ), gamma + Math.PI );
        super.addNeutron( neutron );
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
        super.addNucleus( products.getDaughter1() );
        super.addNucleus( products.getDaughter2() );
        Neutron[] neutronProducts = products.getNeutronProducts();
        for( int i = 0; i < neutronProducts.length; i++ ) {
            NeutronGraphic npg = new NeutronGraphic( neutronProducts[i] );
            getModel().addModelElement( neutronProducts[i] );
            getPhysicalPanel().addGraphic( npg );
        }

        // Add some pizzazz
        Kaboom kaboom = new Kaboom( new Point2D.Double( 0, 0 ),
                                    25, 300, getPhysicalPanel() );
//                                    25, 300, getApparatusPanel() );
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
