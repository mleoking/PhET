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
import edu.colorado.phet.common.view.graphics.Graphic;
import edu.colorado.phet.nuclearphysics.model.*;
import edu.colorado.phet.nuclearphysics.view.Kaboom;
import edu.colorado.phet.nuclearphysics.view.NeutronGraphic;
import edu.colorado.phet.nuclearphysics.view.NucleusGraphic;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MultipleNucleusFissionModule extends NuclearPhysicsModule
        implements NeutronGun, FissionListener {
    private static Random random = new Random();
    private static float neutronSpeed = 1f;

//    private InternalNeutronGun neutronGun;
    private Neutron neutronToAdd;
    private ArrayList nuclei = new ArrayList();
    private ArrayList u235Nuclei = new ArrayList();
    private ArrayList u238Nuclei = new ArrayList();
    private AbstractClock clock;
    private long orgDelay;
    private double orgDt;
    private double neutronLaunchGamma;
    private Point2D.Double neutronLaunchPoint;
    private Line2D.Double neutronPath;
    private ArrayList daughterNuclei = new ArrayList();

    public MultipleNucleusFissionModule( AbstractClock clock ) {
        super( "Chain Reaction", clock );
        this.clock = clock;

        // set the scale of the physical panel so we can fit more nuclei in it
        getPhysicalPanel().setScale( 0.5 );
        super.addControlPanelElement( new MultipleNucleusFissionControlPanel( this ) );

        getModel().addModelElement( new ModelElement() {
            public void stepInTime( double dt ) {
                if( MultipleNucleusFissionModule.this.neutronToAdd != null ) {
                    MultipleNucleusFissionModule.this.addNeutron( neutronToAdd );
                    MultipleNucleusFissionModule.this.neutronToAdd = null;
                }
            }
        } );
    }

    public void start() {

        // Add a bunch of nuclei, including one in the middle that we can fire a
        // neutron at
        Uranium235 centralNucleus = new Uranium235( new Point2D.Double(), getModel() );
        centralNucleus.addFissionListener( this );
        getPhysicalPanel().addNucleus( centralNucleus );
        getModel().addModelElement( centralNucleus );
        addU235Nucleus( centralNucleus );

        // Clear out any leftover nuclei from previous runs
        for( int i = 0; i < daughterNuclei.size(); i++ ) {
            Nucleus nucleus = (Nucleus)daughterNuclei.get( i );
            NucleusGraphic.removeGraphicForNucleus( nucleus );
        }

        computeNeutronLaunchParams();
    }

    public void stop() {
        for( int i = 0; i < nuclei.size(); i++ ) {
            Nucleus nucleus = (Nucleus)nuclei.get( i );
            getPhysicalPanel().removeNucleus( nucleus );
            getModel().removeModelElement( nucleus );
        }
        for( int i = 0; i < u235Nuclei.size(); i++ ) {
            Nucleus nucleus = (Nucleus)u235Nuclei.get( i );
            getPhysicalPanel().removeNucleus( nucleus );
            // Pretty ugly, but it works
            getPhysicalPanel().removeGraphic( ( (Graphic)( (ArrayList)NucleusGraphic.getGraphicForNucleus( nucleus ) ).get( 0 ) ) );
            getModel().removeModelElement( nucleus );
        }
        for( int i = 0; i < u238Nuclei.size(); i++ ) {
            Nucleus nucleus = (Nucleus)u238Nuclei.get( i );
            getPhysicalPanel().removeNucleus( nucleus );
            // Pretty ugly, but it works
            getPhysicalPanel().removeGraphic( ( (Graphic)( (ArrayList)NucleusGraphic.getGraphicForNucleus( nucleus ) ).get( 0 ) ) );
            getModel().removeModelElement( nucleus );
        }
        nuclei.clear();
        u235Nuclei.clear();
        u238Nuclei.clear();
    }

    private void computeNeutronLaunchParams() {
        // Compute how we'll fire the neutron
        double bounds = 600 / getPhysicalPanel().getScale();
        neutronLaunchGamma = random.nextDouble() * Math.PI * 2;
        double x = bounds * Math.cos( neutronLaunchGamma );
        double y = bounds * Math.sin( neutronLaunchGamma );
        neutronLaunchPoint = new Point2D.Double( x, y );
        neutronPath = new Line2D.Double( neutronLaunchPoint, new Point2D.Double( 0, 0 ) );
    }

    public Line2D.Double getNeutronPath() {
        return neutronPath;
    }

    public void activate( PhetApplication app ) {
        orgDelay = this.clock.getDelay();
        orgDt = this.clock.getDt();
        this.clock.setDelay( 10 );
        this.clock.setDt( orgDt * 0.6 );
//        neutronGun = new NeutronGun();
//        Thread neutronGunThread = new Thread( neutronGun );
//        neutronGunThread.start();
        this.start();
    }

    public void deactivate( PhetApplication app ) {
        this.clock.setDelay( (int)( orgDelay ) );
        this.clock.setDt( orgDt );
        this.stop();
//        neutronGun.kill();
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

    public void addU235Nucleus( Uranium235 nucleus ) {
        u235Nuclei.add( nucleus );
        addNucleus( nucleus );
    }

    public void addU238Nucleus( Uranium238 nucleus ) {
        u238Nuclei.add( nucleus );
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
        ArrayList ngList = NucleusGraphic.getGraphicForNucleus( nucleus );
        for( int i = 0; i < ngList.size(); i++ ) {
            NucleusGraphic ng = (NucleusGraphic)ngList.get( i );
            getPhysicalPanel().removeGraphic( ng );
            super.remove( nucleus, ng );
        }
    }

    public void fireNeutron() {
//        double bounds = 600;
//        neutronLaunchGamma = random.nextDouble() * Math.PI * 2;
//        double x = bounds * Math.cos( neutronLaunchGamma );
//        double y = bounds * Math.sin( neutronLaunchGamma );
//        neutronLaunchPoint = new Point2D.Double( x, y );
        Neutron neutron = new Neutron( neutronLaunchPoint, neutronLaunchGamma + Math.PI );
        super.addNeutron( neutron );
    }

    public void fission( FissionProducts products ) {

        // Remove the neutron and old nucleus
        getModel().removeModelElement( products.getInstigatingNeutron() );
        getModel().removeModelElement( products.getParent() );
        nuclei.remove( products.getParent() );

        // We know this must be a U235 nucleus
        u235Nuclei.remove( products.getParent() );
        List graphics = (List)NucleusGraphic.getGraphicForNucleus( products.getParent() );
        for( int i = 0; i < graphics.size(); i++ ) {
            NucleusGraphic ng = (NucleusGraphic)graphics.get( i );
            this.getPhysicalPanel().removeGraphic( ng );
        }
        NeutronGraphic ng = (NeutronGraphic)NeutronGraphic.getGraphicForNeutron( products.getInstigatingNeutron() );
        this.getPhysicalPanel().removeGraphic( ng );

        // Add fission products
        super.addNucleus( products.getDaughter1() );
        super.addNucleus( products.getDaughter2() );
        daughterNuclei.add( products.getDaughter1() );
        daughterNuclei.add( products.getDaughter2() );
        Neutron[] neutronProducts = products.getNeutronProducts();
        for( int i = 0; i < neutronProducts.length; i++ ) {
            NeutronGraphic npg = new NeutronGraphic( neutronProducts[i] );
            getModel().addModelElement( neutronProducts[i] );
            getPhysicalPanel().addGraphic( npg );
        }

        // Add some pizzazz
        Kaboom kaboom = new Kaboom( products.getParent().getLocation(),
                                    25, 300, getPhysicalPanel() );
//                                    25, 300, getApparatusPanel() );
        getPhysicalPanel().addGraphic( kaboom );
    }

    //
    // Inner classes
    //
//    private class InternalNeutronGun implements Runnable {
//        private long waitTime = 1000;
//        private boolean kill = false;
//
//        public void run() {
//            while( kill == false ) {
//                try {
//                    Thread.sleep( waitTime );
//                    this.fireNeutron();
//                }
//                catch( InterruptedException e ) {
//                    e.printStackTrace();
//                }
//            }
//        }
//
//        public synchronized void kill() {
//            this.kill = true;
//        }
//
//        public void fireNeutron() {
//            double bounds = 600;
//            double gamma = random.nextDouble() * Math.PI * 2;
//            double x = bounds * Math.cos( gamma );
//            double y = bounds * Math.sin( gamma );
//            double theta = gamma
//                           + Math.PI
//                           + ( random.nextDouble() * Math.PI / 4 ) * ( random.nextBoolean() ? 1 : -1 );
//
//            Neutron neutron = new Neutron( new Point2D.Double( x, y ), theta );
//            MultipleNucleusFissionModule.this.neutronToAdd = neutron;
//        }
//
//    }
}
