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
import edu.colorado.phet.nuclearphysics.view.ContainmentGraphic;
import edu.colorado.phet.nuclearphysics.view.Kaboom;
import edu.colorado.phet.nuclearphysics.view.NeutronGraphic;
import edu.colorado.phet.nuclearphysics.view.NucleusGraphic;

import java.awt.*;
import java.awt.geom.*;
import java.util.ArrayList;
import java.util.Random;

public class MultipleNucleusFissionModule extends NuclearPhysicsModule implements NeutronGun, FissionListener {
    private static Random random = new Random();

    private Neutron neutronToAdd;
    private ArrayList nuclei = new ArrayList();
    private ArrayList u235Nuclei = new ArrayList();
    private ArrayList u238Nuclei = new ArrayList();
    private ArrayList u239Nuclei = new ArrayList();
    private ArrayList neutrons = new ArrayList();
    private ArrayList daughterNuclei = new ArrayList();
    private ArrayList bodies = new ArrayList();
    //    private ArrayList neutronGraphics = new ArrayList();
    private AbstractClock clock;
    private long orgDelay;
    private double orgDt;
    private double neutronLaunchGamma;
    private Point2D.Double neutronLaunchPoint;
    private Line2D.Double neutronPath;
    private Containment containment;
    private ContainmentGraphic containmentGraphic;

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

        //        getModel().addModelElement( new ModelElement() {
        //            private CollisionExpert expert = new SphereBoxExpert();
        //            public void stepInTime( double dt ) {
        //                if( containment != null ) {
        //                    for( int i = 0; i < neutrons.size(); i++ ) {
        //                        Neutron neutron = (Neutron)neutrons.get( i );
        //                        boolean h = expert.detectAndDoCollision( containment, neutron );
        //                    }
        //                }
        //            }
        //        } );

        // Add a model element that watches for collisions between neutrons and
        // U235 nuclei
        getModel().addModelElement( new ModelElement() {
            public void stepInTime( double dt ) {
                for( int j = 0; j < u235Nuclei.size(); j++ ) {
                    Uranium235 u235 = (Uranium235)u235Nuclei.get( j );
                    for( int i = 0; i < neutrons.size(); i++ ) {
                        Neutron neutron = (Neutron)neutrons.get( i );
                        if( neutron.getPosition().distanceSq( u235.getPosition() )
                            <= u235.getRadius() * u235.getRadius() ) {
                            u235.fission( neutron );
                        }
                    }
                }
            }
        } );

        // Add model element that watches for collisions between neutrons and
        // U238 nuclei
        getModel().addModelElement( new ModelElement() {
            public void stepInTime( double dt ) {
                for( int j = 0; j < u238Nuclei.size(); j++ ) {
                    Uranium238 u238 = (Uranium238)u238Nuclei.get( j );
                    for( int i = 0; i < neutrons.size(); i++ ) {
                        Neutron neutron = (Neutron)neutrons.get( i );
                        if( neutron.getPosition().distanceSq( u238.getPosition() )
                            <= u238.getRadius() * u238.getRadius() ) {

                            // Create a new uranium 239 nucleus to replace the U238
                            Uranium239 u239 = new Uranium239( u238.getPosition(), getModel() );
                            addU239Nucleus( u239 );

                            // Remove the old U238 nucleus and the neutron
                            nuclei.remove( u238 );
                            neutrons.remove( neutron );
                            getModel().removeModelElement( u238 );
                            getModel().removeModelElement( neutron );
                            getPhysicalPanel().removeGraphic( (Graphic)NucleusGraphic.getGraphicForNucleus( u238 ).get( 0 ) );
                        }
                    }
                }
            }
        } );
    }

    public void start() {
        // Add a bunch of nuclei, including one in the middle that we can fire a
        // neutron at
        Uranium235 centralNucleus = new Uranium235( new Point2D.Double(), getModel() );
        //        centralNucleus.addFissionListener( this );
        getPhysicalPanel().addNucleus( centralNucleus );
        getModel().addModelElement( centralNucleus );
        u235Nuclei.add( centralNucleus );
        addNucleus( centralNucleus );

        // Clear out any leftover nuclei from previous runs
        for( int i = 0; i < daughterNuclei.size(); i++ ) {
            Nucleus nucleus = (Nucleus)daughterNuclei.get( i );
            NucleusGraphic.removeGraphicForNucleus( nucleus );
        }
        computeNeutronLaunchParams();
    }

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
        for( int i = 0; i < daughterNuclei.size(); i++ ) {
            removeNucleus( (Nucleus)daughterNuclei.get( i ) );
        }
        for( int i = 0; i < neutrons.size(); i++ ) {
            Neutron neutron = (Neutron)neutrons.get( i );
            getModel().removeModelElement( neutron );
            neutron.leaveSystem();
            bodies.remove( neutron );
        }
        //        for( int i = 0; i < neutronGraphics.size(); i++ ) {
        //            getPhysicalPanel().removeGraphic( (Graphic)neutronGraphics.get( i ) );
        //        }
        bodies.clear();
        nuclei.clear();
        neutrons.clear();
        u239Nuclei.clear();
        u235Nuclei.clear();
        u238Nuclei.clear();
        daughterNuclei.clear();
        //        neutronGraphics.clear();
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
        super.activate( app );
        orgDelay = this.clock.getDelay();
        orgDt = this.clock.getDt();
        this.clock.setDelay( 10 );
        this.clock.setDt( orgDt * 0.6 );
        this.start();
    }

    public void deactivate( PhetApplication app ) {
        super.deactivate( app );
        this.clock.setDelay( (int)( orgDelay ) );
        this.clock.setDt( orgDt );
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

    public void addU235Nucleus() {
        Point2D.Double location = findLocationForNewNucleus();
        Nucleus nucleus = new Uranium235( location, getModel() );
        u235Nuclei.add( nucleus );
        addNucleus( nucleus );
    }

    public void addU238Nucleus() {
        Point2D.Double location = findLocationForNewNucleus();
        Nucleus nucleus = new Uranium238( location, getModel() );
        u238Nuclei.add( nucleus );
        addNucleus( nucleus );
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
        bodies.add( nucleus );
    }

    public void removeU235Nucleus( Uranium235 nucleus ) {
        u235Nuclei.remove( nucleus );
        removeNucleus( nucleus );
        bodies.remove( nucleus );
    }

    public void removeU238Nucleus( Uranium238 nucleus ) {
        u238Nuclei.remove( nucleus );
        removeNucleus( nucleus );
    }

    public void removeNucleus( Nucleus nucleus ) {
        nuclei.remove( nucleus );
        nucleus.leaveSystem();

        //        ArrayList ngList = NucleusGraphic.getGraphicForNucleus( nucleus );
        //        for( int i = 0; i < ngList.size(); i++ ) {
        //            NucleusGraphic ng = (NucleusGraphic)ngList.get( i );
        //            getPhysicalPanel().removeGraphic( ng );
        //            super.remove( nucleus, ng );
        //        }
        getPhysicalPanel().removeNucleus( nucleus );
        getModel().removeModelElement( nucleus );
    }

    public void fireNeutron() {
        Neutron neutron = new Neutron( neutronLaunchPoint, neutronLaunchGamma + Math.PI );
        neutrons.add( neutron );
        super.addNeutron( neutron );
        bodies.add( neutron );
    }

    public void fission( FissionProducts products ) {
        // Remove the neutron and old nucleus
        getModel().removeModelElement( products.getInstigatingNeutron() );
        this.neutrons.remove( products.getInstigatingNeutron() );
        products.getInstigatingNeutron().leaveSystem();
        getModel().removeModelElement( products.getParent() );
        nuclei.remove( products.getParent() );
        products.getParent().leaveSystem();

        // We know this must be a U235 nucleus
        u235Nuclei.remove( products.getParent() );

        // Add fission products
        super.addNucleus( products.getDaughter1() );
        super.addNucleus( products.getDaughter2() );
        daughterNuclei.add( products.getDaughter1() );
        daughterNuclei.add( products.getDaughter2() );
        Neutron[] neutronProducts = products.getNeutronProducts();

        for( int i = 0; i < neutronProducts.length; i++ ) {
            final NeutronGraphic npg = new NeutronGraphic( neutronProducts[i] );
            getModel().addModelElement( neutronProducts[i] );
            getPhysicalPanel().addGraphic( npg );
            neutrons.add( neutronProducts[i] );
            //            neutronGraphics.add( npg );
            neutronProducts[i].addListener( new NuclearModelElement.Listener() {
                public void leavingSystem( NuclearModelElement nme ) {
                    getPhysicalPanel().removeGraphic( npg );
                }
            } );
        }

        // Add some pizzazz
        Kaboom kaboom = new Kaboom( products.getParent().getPosition(),
                                    25, 300, getPhysicalPanel() );
        getPhysicalPanel().addGraphic( kaboom );

        // Manage the list of collidable bodies
        bodies.remove( products.getParent() );
        bodies.add( products.getDaughter1() );
        bodies.add( products.getDaughter2() );
        for( int i = 0; i < neutronProducts.length; i++ ) {
            bodies.add( neutronProducts[i] );
        }
    }

    private void addContainment() {
        containment = new Containment( new Ellipse2D.Double( -300, -300, 600, 600 ) );
        //        containment = new Containment( new Rectangle2D.Double( -300, -300, 600, 600 ) );
        containmentGraphic = new ContainmentGraphic( containment, getPhysicalPanel(), getPhysicalPanel().getNucleonTx() );
        getPhysicalPanel().addGraphic( containmentGraphic, 100 );
    }

    private void removeContainment() {
        containment = null;
        getPhysicalPanel().removeGraphic( containmentGraphic );
    }

    public void enableContainment( boolean selected ) {
        if( selected ) {
            addContainment();
        }
        else {
            removeContainment();
        }
    }

    private Point2D.Double findLocationForNewNucleus() {
        // Determine the area in which the nucleus can be place. This depends on whether the
        // containment vessel is enabled or not.
        Rectangle2D r = getPhysicalPanel().getBounds();
        AffineTransform atx = getPhysicalPanel().getNucleonTx();
        Rectangle2D apparatusPanelBounds = new Rectangle2D.Double();
        try {
            apparatusPanelBounds.setFrameFromDiagonal( atx.inverseTransform( new Point2D.Double( r.getMinX(), r.getMinY() ), null ),
                                                       atx.inverseTransform( new Point2D.Double( r.getMinX() + r.getWidth(), r.getMinY() + r.getHeight() ), null ) );
        }
        catch( NoninvertibleTransformException e ) {
            e.printStackTrace();
        }
        Shape bounds = null;
        if( containment != null ) {
            bounds = containment.geShape();
        }
        else {
            //            Rectangle2D r = getPhysicalPanel().getBounds();
            //            AffineTransform atx = getPhysicalPanel().getNucleonTx();
            //            try {
            //                apparatusPanelBounds.setFrameFromDiagonal( atx.inverseTransform( new Point2D.Double( r.getMinX(), r.getMinY() ), null ),
            //                                           atx.inverseTransform( new Point2D.Double( r.getMinX() + r.getWidth(), r.getMinY() + r.getHeight() ), null ) );
            //            }
            //            catch( NoninvertibleTransformException e ) {
            //                e.printStackTrace();
            //            }
            bounds = apparatusPanelBounds;
        }
        boolean overlapping = false;
        Point2D.Double location = new Point2D.Double();
        int attempts = 0;
        do {
            // If there is already a nucleus at (0,0), then generate a random location
            boolean centralNucleusExists = false;
            for( int i = 0; i < getNuclei().size() && !centralNucleusExists; i++ ) {
                Nucleus testNucleus = (Nucleus)getNuclei().get( i );
                if( testNucleus.getPosition().getX() == 0 && testNucleus.getPosition().getY() == 0 ) {
                    centralNucleusExists = true;
                }
            }

            double x = centralNucleusExists ? random.nextDouble() * ( apparatusPanelBounds.getWidth() - 50 ) + apparatusPanelBounds.getMinX() + 25 : 0;
            double y = centralNucleusExists ? random.nextDouble() * ( apparatusPanelBounds.getHeight() - 50 ) + apparatusPanelBounds.getMinY() + 25 : 0;
            location.setLocation( x, y );

            overlapping = false;
            for( int j = 0; j < getNuclei().size() && !overlapping; j++ ) {
                Nucleus testNucleus = (Nucleus)getNuclei().get( j );
                if( testNucleus.getPosition().distance( location ) < testNucleus.getRadius() * 3 ) {
                    overlapping = true;
                }
            }

            // todo: the hard-coded 50 here should be replaced with the radius of a Uranium nucleus
            if( location.getX() != 0 && location.getY() != 0 ) {
                overlapping = overlapping || getNeutronPath().ptSegDist( location ) < 50 || !bounds.contains( location );
            }
            attempts++;
        } while( overlapping && attempts < 50 );

        if( overlapping ) {
            location = null;
        }
        return location;
    }
}
