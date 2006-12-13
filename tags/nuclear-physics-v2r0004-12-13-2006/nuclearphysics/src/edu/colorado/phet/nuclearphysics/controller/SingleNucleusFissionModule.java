/**
 * Class: TestModule
 * Package: edu.colorado.phet.nuclearphysics.modules
 * Author: Another Guy
 * Date: Feb 26, 2004
 */
package edu.colorado.phet.nuclearphysics.controller;

import edu.colorado.phet.common.math.MathUtil;
import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.common.model.clock.IClock;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetImageGraphic;
import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.nuclearphysics.model.*;
import edu.colorado.phet.nuclearphysics.view.*;

import java.awt.geom.Point2D;
import java.awt.geom.Line2D;
import java.awt.geom.AffineTransform;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

/**
 * Presents a single U235 nucleus and a potential profile graph in a panel below it. A single neutron is fired at
 * the U235 nucleus, which then rattles around for a bit while its potential energy increases until it gets to the
 * top of the profile, then breaks apart in a fission event.
 */
public class SingleNucleusFissionModule extends ProfiledNucleusModule implements NeutronGun, FissionListener {
    private static Random random = new Random();
    private Neutron neutronToAdd;
    private Uranium235 nucleus;
    private Neutron neutron;
    private ArrayList transientModelElements = new ArrayList();
    private Point gunMuzzelLocation = new Point( -240, 0);

    public SingleNucleusFissionModule( IClock clock ) {
        super( SimStrings.get( "ModuleTitle.SingleNucleusFissionModule" ), clock, EnergyProfileGraphic.POTENTIAL_ENERGY );
    }

    protected void init() {
        super.init();
        init( getClock() );
    }

    /**
     * A subclass of PhysicalPanel that sets the origin in the middle of the pane.
     */
    private class MyPhysicalPanel extends PhysicalPanel {
        public MyPhysicalPanel() {
            super( getClock(), (NuclearPhysicsModel)getModel() );
            setOrigin( new Point2D.Double( this.getWidth() / 2, this.getHeight() / 2 ) );
            getOriginTx().setToTranslation( getOrigin().getX(), getOrigin().getY() );
        }
    }

    protected void init( IClock clock ) {
        super.init();
        MyPhysicalPanel physicalPanel = new MyPhysicalPanel();
        setPhysicalPanel( physicalPanel );
        addPhysicalPanel( physicalPanel );

        physicalPanel.setOrigin( new Point2D.Double( 0, -150 ));
        
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
                    && neutron.getPosition().distanceSq( nucleus.getPosition() )
                       <= nucleus.getRadius() * nucleus.getRadius() ) {
                    nucleus.fission( neutron );
                }
            }
        } );

        // Add a listener that will add and remove energy profiles from the energy profile panel
        NuclearPhysicsModel model = (NuclearPhysicsModel)getModel();
        model.addNucleusListener( new NuclearPhysicsModel.NucleusListener() {
            public void nucleusAdded( NuclearPhysicsModel.ChangeEvent event ) {
                Nucleus nucleus = event.getNucleus();
                if( nucleus instanceof ProfileableNucleus ) {
                    getEnergyProfilePanel().addEnergyProfile( (ProfileableNucleus)event.getNucleus(),
                                                              EnergyProfileGraphic.POTENTIAL_ENERGY );

                    // Only add one profile. Otherwise, the fission products end up with profiles
                    ((NuclearPhysicsModel)getModel()).removeNucleusListener( this );
                }
            }

            public void nucleusRemoved( NuclearPhysicsModel.ChangeEvent event ) {
                // noop - don't remove the profile
            }
        } );

        // Ray gun
//        RayGunGraphic gunGraphic = new RayGunGraphic( getPhysicalPanel(), this );
//        gunGraphic.setLocation( gunMuzzelLocation );
//        getPhysicalPanel().addGraphic( gunGraphic );
        PhetImageGraphic gunGraphic;
        gunGraphic = new PhetImageGraphic( getPhysicalPanel(), "images/gun-8A.png");
        gunGraphic.setRegistrationPoint( gunGraphic.getWidth() - 15, 25 );
//        gunGraphic.setTransform( AffineTransform.getScaleInstance( 0.8, 0.8 ) );
        gunGraphic.setRenderingHints( new RenderingHints( RenderingHints.KEY_ALPHA_INTERPOLATION,
                                                          RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY) );
        gunGraphic.setRenderingHints( new RenderingHints( RenderingHints.KEY_ANTIALIASING,
                                                          RenderingHints.VALUE_ANTIALIAS_ON) );
        gunGraphic.setLocation( gunMuzzelLocation );
        getPhysicalPanel().addGraphic( gunGraphic );


        // Add a fire button to the play area
        FireButton fireButton = new FireButton( getPhysicalPanel());
        getPhysicalPanel().addGraphic( fireButton, 1E6 );
        fireButton.setLocation(  (int)gunMuzzelLocation.getX() + 285,
                                 (int)gunMuzzelLocation.getY() + 145 );
//        fireButton.setLocation(  (int)( 37), 152 );
//        fireButton.setTransform( AffineTransform.getScaleInstance( 0.85, 0.85));
        fireButton.addActionListener( new FireButton.ActionListener() {
            public void actionPerformed( FireButton.ActionEvent event ) {
                fireNeutron();
            }
        } );

        // Start things up
        this.start();
    }

    protected java.util.List getLegendClasses() {
        LegendPanel.LegendItem[] legendClasses = new LegendPanel.LegendItem[]{
                LegendPanel.NEUTRON,
                LegendPanel.PROTON,
                LegendPanel.U235,
                LegendPanel.DAUGHTER_NUCLEI
        };
        return Arrays.asList( legendClasses );
    }

    public void stop() {
        getModel().removeModelElement( nucleus );
        ( (NuclearPhysicsModel)getModel() ).removeNuclearParticles();

        for( int i = 0; i < transientModelElements.size(); i++ ) {
            ModelElement modelElement = (ModelElement)transientModelElements.get( i );
            getModel().removeModelElement( modelElement );
        }
    }

    public void start() {
        nucleus = new Uranium235( new Point2D.Double( 0, 0 ), (NuclearPhysicsModel)getModel() );
        nucleus.setPotential( nucleus.getPotentialProfile().getMinEnergy() );
        setNucleus( nucleus );
        setUraniumNucleus( nucleus );
        getEnergyProfilePanel().addNucleusGraphic( nucleus );
        nucleus.addFissionListener( this );
        nucleus.setDoMorph( true );
    }

    /**
     * Produces a neutron that comes into the PhysicalPanel from the muzzle of the ray gun
     */
    public void fireNeutron() {
        neutron = new Neutron( gunMuzzelLocation, 0 );
        super.addNeutron( neutron );
    }

    public void fission( final FissionProducts products ) {
        // Constrain velocity of the daughter nuclei to be more or less horizontal
        double theta = ( random.nextDouble() * Math.PI / 2 ) - ( Math.PI / 4 );
        Nucleus daughter1 = products.getDaughter1();
        double v1 = daughter1.getVelocity().getMagnitude();
        daughter1.setVelocity( (float)( v1 * Math.cos( theta ) ), (float)( v1 * Math.sin( theta ) ) );
//        double a = 10;
//        daughter1.setAcceleration( a * Math.cos( theta ), a * Math.sin( theta ));
        Nucleus daughter2 = products.getDaughter2();
        double v2 = daughter2.getVelocity().getMagnitude();
        daughter2.setVelocity( (float)( v2 * Math.cos( theta + Math.PI ) ), (float)( v2 * Math.sin( theta + Math.PI ) ) );
//        daughter2.setAcceleration( a * Math.cos( theta + Math.PI ), a * Math.sin( theta + Math.PI ));

        // Remove the neutron and old nucleus
        getModel().removeModelElement( products.getInstigatingNeutron() );
        getModel().removeModelElement( products.getParent() );

        // Add fission products
        final Neutron[] neutronProducts = products.getNeutronProducts();
        for( int i = 0; i < neutronProducts.length; i++ ) {
            final NeutronGraphic npg = new NeutronGraphic( neutronProducts[i] );
            getModel().addModelElement( neutronProducts[i] );
            transientModelElements.add( neutronProducts[i] );
            getPhysicalPanel().addGraphic( npg );
            final int i1 = i;
            neutronProducts[i].addListener( new NuclearModelElement.Listener() {
                public void leavingSystem( NuclearModelElement nme ) {
                    getPhysicalPanel().removeGraphic( npg );
                    neutronProducts[i1].removeListener( this );
                }
            } );
        }

        // Add a model element that will make the daughter nuclei slide down the
        // profile
        ModelElement daughterStepper = new FissionProductsStepper( products );
        getModel().addModelElement( daughterStepper );
        transientModelElements.add( daughterStepper );

        Nucleus dn1 = products.getDaughter1();
        Nucleus dn2 = products.getDaughter2();
        dn2.setPosition( 0, 0 );
        super.addNucleus( dn1, null );
        super.addNucleus( dn2, null );
        getEnergyProfilePanel().addNucleusGraphic( dn1 );
        getEnergyProfilePanel().addNucleusGraphic( dn2 );

        // Add some pizzazz
        Kaboom kaboom = new Kaboom( new Point2D.Double( 0, 0 ),
                                    25, 300, getPhysicalPanel(),
                                    getModel() );
        getPhysicalPanel().addGraphic( kaboom );
    }

    /**
     * A model element that steps the products of the fission
     */
    private static class FissionProductsStepper implements ModelElement {
        private double forceScale;
        private final FissionProducts products;

        public FissionProductsStepper( FissionProducts products ) {
            this.products = products;
            forceScale = 0.2;
//            forceScale = 0.1;
        }

        public void stepInTime( double dt ) {
            stepDaughterNucleus( products.getParent(), products.getDaughter1() );
            stepDaughterNucleus( products.getParent(), products.getDaughter2() );
        }

        private void stepDaughterNucleus( Nucleus parent, Nucleus daughter ) {
            double d = daughter.getPosition().distance( parent.getPosition() );
            Vector2D a = null;
            IEnergyProfile profile = parent.getPotentialProfile();
            double force = Math.abs( profile.getHillY( -d ) ) * forceScale;
            force = Double.isNaN( force ) ? 0 : force;
            force = -profile.getDyDx( -d ) * forceScale;
            if( daughter.getVelocity().getX() == 0 && daughter.getVelocity().getY() == 0 ) {
                double dx = daughter.getPosition().getX() - parent.getPosition().getX();
                double dy = daughter.getPosition().getY() - parent.getPosition().getY();
                a = new Vector2D.Double( (float)dx, (float)dy ).normalize().scale( force );
            }
            else {
                a = new Vector2D.Double( daughter.getVelocity() ).normalize().scale( force );
            }
            daughter.setAcceleration( a );


            daughter.setPotential( profile.getMaxEnergy() );
            // We're ignoring the code below that makes the daughter nucleui in the energy panel follow the
            // profile.
            // Set the nucleus' potential energy. If the nucles isn't outside the peaks of the
            // profile, it's potential keeps it at the top of the profile. Otherwise, it slides
            // down the profile
//            double potential = 0;
            // I don't know why the -10 is needed here, but it is. I don't have time to figure out why.
//            if( Math.abs( d ) <= Math.abs( profile.getMaxEnergy() - 10 ) ) {
//        //                if( Math.abs( d ) <= Math.abs( profile.getProfilePeakX() - 10 ) ) {
//                potential = profile.getMaxEnergy();
//            }
//            else {
//                potential = Double.isNaN( -profile.getHillY( -d ) ) ? 0 : -profile.getHillY( -d );
//            }
//            daughter.setPotential( potential );
        }
    }


    //--------------------------------------------------------------------------------------------------
    // Implementation of abstract methods
    //--------------------------------------------------------------------------------------------------
    protected String getPotentialEnergyLegend() {
        return SimStrings.get( "PotentialProfilePanel.legend.PotentialEnergy" );
    }
}
