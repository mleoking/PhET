/**
 * Class: TestModule
 * Package: edu.colorado.phet.nuclearphysics.modules
 * Author: Another Guy
 * Date: Feb 26, 2004
 */
package edu.colorado.phet.nuclearphysics.controller;

import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.common.model.clock.IClock;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.nuclearphysics.model.*;
import edu.colorado.phet.nuclearphysics.view.ContainmentGraphic;
import edu.colorado.phet.nuclearphysics.view.Kaboom;
import edu.colorado.phet.nuclearphysics.view.NeutronGraphic;

import java.awt.*;
import java.awt.geom.*;
import java.util.ArrayList;
import java.util.Random;

public class MultipleNucleusFissionModule extends NuclearPhysicsModule
        implements NeutronGun, FissionListener, Containment.ResizeListener {
    private static Random random = new Random();
    private static int s_maxPlacementAttempts = 100;

    private Neutron neutronToAdd;
    private ArrayList nuclei = new ArrayList();
    private ArrayList u235Nuclei = new ArrayList();
    private ArrayList u238Nuclei = new ArrayList();
    private ArrayList u239Nuclei = new ArrayList();
    private ArrayList neutrons = new ArrayList();
    private long orgDelay;
    private double orgDt;
    private double neutronLaunchGamma;
    private Point2D.Double neutronLaunchPoint;
    private Line2D.Double neutronPath;
    private Containment containment;
    private ContainmentGraphic containmentGraphic;

    public MultipleNucleusFissionModule( IClock clock ) {
        super( SimStrings.get( "ModuleTitle.MultipleNucleusFissionModule" ), clock );

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
        getModel().addModelElement( centralNucleus );
        u235Nuclei.add( centralNucleus );
        addNucleus( centralNucleus );

        // If the containment is enabled, recreate it, so it will be fully
        // displayed
        if( containment != null ) {
            setContainmentEnabled( false );
            setContainmentEnabled( true );
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

    private void computeNeutronLaunchParams() {
        // Compute how we'll fire the neutron
        if( containment != null ) {
            neutronLaunchPoint = containment.getNeutronLaunchPoint();
            neutronLaunchGamma = Math.PI;
            neutronPath = new Line2D.Double( neutronLaunchPoint, new Point2D.Double( 0, 0 ) );
        }
        else {
            double bounds = 600 / getPhysicalPanel().getScale();
            neutronLaunchGamma = random.nextDouble() * Math.PI * 2;
            double x = bounds * Math.cos( neutronLaunchGamma );
            double y = bounds * Math.sin( neutronLaunchGamma );
            neutronLaunchPoint = new Point2D.Double( x, y );
            neutronPath = new Line2D.Double( neutronLaunchPoint, new Point2D.Double( 0, 0 ) );
        }
    }

    private Line2D.Double getNeutronPath() {
        return neutronPath;
    }

//    public void activate( PhetApplication app ) {
//        super.activate( app );
//        orgDelay = getClock().getDelay();
//        orgDt = getClock().getDt();
//        getClock().setDelay( 10 );
//        getClock().setDt( orgDt * 0.6 );
//        this.start();
//    }

//    public void deactivate( PhetApplication app ) {
//        super.deactivate( app );
//        getClock().setDelay( (int)( orgDelay ) );
//        getClock().setDt( orgDt );
//        this.stop();
//    }

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
        super.addNeutron( neutron );
    }

    public void fission( FissionProducts products ) {
        // Remove the neutron and old nucleus
        getModel().removeModelElement( products.getInstigatingNeutron() );
        this.neutrons.remove( products.getInstigatingNeutron() );
        getModel().removeModelElement( products.getParent() );
        nuclei.remove( products.getParent() );

        // We know this must be a U235 nucleus
        u235Nuclei.remove( products.getParent() );

        // Add fission products
        super.addNucleus( products.getDaughter1() );
        super.addNucleus( products.getDaughter2() );
        Neutron[] neutronProducts = products.getNeutronProducts();

        for( int i = 0; i < neutronProducts.length; i++ ) {
            final NeutronGraphic npg = new NeutronGraphic( getApparatusPanel(), neutronProducts[i] );
            getModel().addModelElement( neutronProducts[i] );
            getPhysicalPanel().addGraphic( npg );
            neutrons.add( neutronProducts[i] );
            neutronProducts[i].addListener( new NuclearModelElement.Listener() {
                public void leavingSystem( NuclearModelElement nme ) {
                    getPhysicalPanel().removeGraphic( npg );
                }
            } );
        }

        // Add some pizzazz
        Kaboom kaboom = new Kaboom( getApparatusPanel(),
                                    products.getParent().getPosition(),
                                    25, 300, getPhysicalPanel() );
        getPhysicalPanel().addGraphic( kaboom );

        // If the conatinment vessel is being used, make it dissovle
        if( containment != null ) {
            containment.dissolve();
        }
    }

    private void addContainment() {
        containment = new Containment( new Point2D.Double( 0, 0 ), 400 );
        containment.addResizeListener( this );
        containmentGraphic = new ContainmentGraphic( containment, getPhysicalPanel(), getPhysicalPanel().getNucleonTx() );
        getPhysicalPanel().addGraphic( containmentGraphic, 10 );
    }

    private void removeContainment() {
        containment = null;
        getPhysicalPanel().removeGraphic( containmentGraphic );
    }

    public void setContainmentEnabled( boolean selected ) {
        if( selected ) {
            addContainment();
        }
        else {
            removeContainment();
        }
        computeNeutronLaunchParams();
        
        // This call will cause any nuclei that are outside the containment
        // to be removed
        containementResized( containment );
    }

    private Point2D.Double findLocationForNewNucleus() {
        // Determine the area in which the nucleus can be place. This depends on whether the
        // containment vessel is enabled or not.

        // Determine the model bounds represented by the current size of the apparatus panel
        Rectangle2D r = getPhysicalPanel().getBounds();
        AffineTransform atx = new AffineTransform( getPhysicalPanel().getNucleonTx() );
        AffineTransform gtx = getPhysicalPanel().getGraphicTx();
        atx.preConcatenate( gtx );
        Rectangle2D modelBounds = new Rectangle2D.Double();
        try {
            modelBounds.setFrameFromDiagonal( atx.inverseTransform( new Point2D.Double( r.getMinX(), r.getMinY() ), null ),
                                              atx.inverseTransform( new Point2D.Double( r.getMinX() + r.getWidth(), r.getMinY() + r.getHeight() ), null ) );
        }
        catch( NoninvertibleTransformException e ) {
            e.printStackTrace();
        }

        Shape bounds = null;
        if( containment != null ) {
            bounds = containment.getShape();
        }
        else {
            bounds = modelBounds;
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

            double x = centralNucleusExists ? random.nextDouble() * ( modelBounds.getWidth() - 50 ) + modelBounds.getMinX() + 25 : 0;
            double y = centralNucleusExists ? random.nextDouble() * ( modelBounds.getHeight() - 50 ) + modelBounds.getMinY() + 25 : 0;
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
                overlapping = overlapping
                              || getNeutronPath().ptSegDist( location ) < 50
                              || location.distance( 0, 0 ) + 50 > bounds.getBounds2D().getWidth() / 2;
            }
            attempts++;
        } while( overlapping && attempts < s_maxPlacementAttempts );

        if( overlapping ) {
            location = null;
        }
        return location;
    }

    public void containementResized( Containment containment ) {
        Shape bounds = containment.getShape();
        ArrayList removeList = new ArrayList();
        for( int i = 0; i < nuclei.size(); i++ ) {
            Nucleus nucleus = (Nucleus)nuclei.get( i );
            if( nucleus.getPosition().distance( 0, 0 ) + 50 > bounds.getBounds2D().getWidth() / 2 ) {
                removeList.add( nucleus );
            }
        }
        for( int i = 0; i < removeList.size(); i++ ) {
            Nucleus nucleus = (Nucleus)removeList.get( i );
            getModel().removeModelElement( nucleus );
            // This is lazy and crude, but let's just remove from all the lists, so
            // we don't have to check types, use logic, etc.
            nuclei.remove( nucleus );
            u235Nuclei.remove( nucleus );
            u238Nuclei.remove( nucleus );
            u239Nuclei.remove( nucleus );
        }
    }
}