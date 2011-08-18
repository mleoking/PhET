// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.water.model;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.JFrame;

import org.jbox2d.common.Color3f;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.World;
import org.jbox2d.testbed.framework.DebugDrawJ2D;
import org.jbox2d.testbed.framework.TestPanel;
import org.jbox2d.testbed.framework.TestbedSettings;

import edu.colorado.phet.common.phetcommon.math.ImmutableRectangle2D;
import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.model.property.doubleproperty.DoubleProperty;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.sugarandsaltsolutions.SugarAndSaltSolutionsApplication;
import edu.colorado.phet.sugarandsaltsolutions.common.model.AbstractSugarAndSaltSolutionsModel;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.Compound;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.Constituent;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.ItemList;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.SphericalParticle;

import static edu.colorado.phet.common.phetcommon.math.ImmutableVector2D.ZERO;

/**
 * Model for "water" tab for sugar and salt solutions.
 *
 * @author Sam Reid
 */
public class WaterModel extends AbstractSugarAndSaltSolutionsModel {

    //List of all spherical particles, the constituents in larger molecules or crystals, used for rendering on the screen
    public final ItemList<ChargedSphericalParticle> particles = new ItemList<ChargedSphericalParticle>();

    //Lists of all model objects
    public final ItemList<WaterMolecule> waterList = new ItemList<WaterMolecule>();
    public final ItemList<WaterMolecule2> waterList2 = new ItemList<WaterMolecule2>();
    public final ItemList<DefaultParticle> sodiumList = new ItemList<DefaultParticle>();
    public final ItemList<DefaultParticle> chlorineList = new ItemList<DefaultParticle>();
    public final ItemList<Sucrose> sugarMoleculeList = new ItemList<Sucrose>();

    //Listeners who are called back when the physics updates
    private ArrayList<VoidFunction0> frameListeners = new ArrayList<VoidFunction0>();

    //Box2d world which updates the physics
    public final World world;

    private Random random = new Random();

    //Dimensions of the particle window in meters
    private final double particleWindowWidth = 10E-10;
    private final double particleWindowHeight = particleWindowWidth * 0.6;
    public final ImmutableRectangle2D particleWindow = new ImmutableRectangle2D( -particleWindowWidth / 2, -particleWindowHeight / 2, particleWindowWidth, particleWindowHeight );

    //Width of the box2D model
    private final double box2DWidth = 20;

    //units for water molecules are in SI
    //Beaker floor should be about 40 angstroms, to accommodate about 20 water molecules side-to-side
    //But keep box2d within -10..10 (i.e. 20 boxes wide)
    double scaleFactor = box2DWidth / particleWindow.width;
    public final ModelViewTransform modelToBox2D = ModelViewTransform.createSinglePointScaleMapping( new Point(), new Point(), scaleFactor );

    private static final int DEFAULT_NUM_WATERS = 180;

    //So we don't have to reallocate zeros all the time
    private final Vec2 zero = new Vec2();

    //Properties for developer controls
    public final Property<Integer> pow = new Property<Integer>( 2 );
    public final Property<Integer> randomness = new Property<Integer>( 5 );
    public final Property<Double> minInteractionDistance = new Property<Double>( 0.05 );
    public final Property<Double> maxInteractionDistance = new Property<Double>( 2.0 );
    public final Property<Double> probabilityOfInteraction = new Property<Double>( 0.5 );
    public final Property<Double> timeScale = new Property<Double>( 0.005 );
    public final Property<Integer> iterations = new Property<Integer>( 10 );
    public final VoidFunction1<VoidFunction0> addFrameListener = new VoidFunction1<VoidFunction0>() {
        public void apply( VoidFunction0 waterMolecule ) {
            addFrameListener( waterMolecule );
        }
    };

    //Coulomb's constant in SI, see http://en.wikipedia.org/wiki/Coulomb's_law
    private static final double k = 8.987E9;

    //User settings
    public final Property<Boolean> showSugarAtoms = new Property<Boolean>( false );
    public final Property<Boolean> showWaterCharges = new Property<Boolean>( false );
    public final DoubleProperty oxygenCharge = new DoubleProperty( -0.8 );
    public final DoubleProperty hydrogenCharge = new DoubleProperty( 0.4 );
    public final DoubleProperty ionCharge = new DoubleProperty( 1.0 );
    public final BooleanProperty coulombForceOnAllMolecules = new BooleanProperty( true );
    public final ObservableProperty<Boolean> showChargeColor = new Property<Boolean>( false );

    //Turn down forces after salt disassociates
    private double timeSinceSaltAdded = 0;
    private static final boolean debugTime = false;

    //List of adapters that manage both the box2D and actual model data
    private ArrayList<Box2DAdapter> box2DAdapters = new ArrayList<Box2DAdapter>();

    //Panel that allows us to see jbox2d model and computations
    protected TestPanel testPanel;

    public WaterModel() {
        super( new ConstantDtClock( 30 ) );

        //Create the Box2D world with no gravity
        world = new World( new Vec2( 0, 0 ), true );

        //Move to a stable state on startup
        //Commented out because it takes too long
//        long startTime = System.currentTimeMillis();
//        for ( int i = 0; i < 10; i++ ) {
//            world.step( (float) ( clock.getDt() * 10 ), 1 );
//        }
//        System.out.println( "stable start time: " + ( System.currentTimeMillis() - startTime ) );

        //Set up initial state, same as reset() method would do, such as adding water particles to the model
        initModel();

        //Set up jbox2D debug draw so we can see the model and computations
        initDebugDraw();
    }

    private void addWaterParticles() {
        for ( int i = 0; i < DEFAULT_NUM_WATERS; i++ ) {
            addWaterMolecule2( randomBetweenMinusOneAndOne() * particleWindow.width / 2, randomBetweenMinusOneAndOne() * particleWindow.height / 2, random.nextDouble() * Math.PI * 2 );
        }
    }

    private double randomBetweenMinusOneAndOne() {
        return ( random.nextFloat() - 0.5 ) * 2;
    }

    //Set up jbox2D debug draw so we can see the model and computations
    private void initDebugDraw() {

        //We had to change code in TestPanel to make dbImage public, using jbox2D animation thread was glitchy and flickering
        //So instead we control the rendering ourselves
        testPanel = new TestPanel( new TestbedSettings() ) {
            @Override protected void paintComponent( Graphics g ) {
                super.paintComponent( g );    //To change body of overridden methods use File | Settings | File Templates.
                g.drawImage( dbImage, 0, 0, null );
            }
        };

        //Create a frame to show the debug draw in
        JFrame frame = new JFrame();
        frame.setContentPane( testPanel );
        frame.pack();
        frame.setVisible( true );

        world.setDebugDraw( new DebugDrawJ2D( testPanel ) {
            {
                //Show the shapes in the debugger
                setFlags( e_shapeBit );

                //Move the camera over so that the shapes will show up at a good size and location
                setCamera( -10, 10, 20 );
            }


            //Circles are drawn as segments, this override is here to facilitate debugging
            @Override public void drawSegment( Vec2 vec2, Vec2 vec21, Color3f color ) {
                super.drawSegment( vec2, vec21, color );
            }
        } );
    }

    //Add all constituents to the list of spherical particles so they will be drawn on the screen and can be iterated for coulomb repulsion
    private void addConstituents( WaterMolecule2 waterMolecule2 ) {
        for ( Constituent<ChargedSphericalParticle> constituent : waterMolecule2 ) {
            particles.add( constituent.particle );
        }
    }

    //Remove the SaltCrystal bodies from the box2d model so they won't collide.  This facilitates dragging from the bucket without causing interactions.
    public void unhook( SaltCrystal saltCrystal ) {
        world.destroyBody( saltCrystal.sodium.body );
        world.destroyBody( saltCrystal.sodium2.body );
        world.destroyBody( saltCrystal.chloride.body );
        world.destroyBody( saltCrystal.chloride2.body );
    }

    //Remove the sugar molecules bodies from the box2d model so they won't collide.  This facilitates dragging from the bucket without causing interactions.
    public void unhook( Sucrose sucrose ) {
        world.destroyBody( sucrose.body );
    }

    //Adds some NaCl molecules by adding nearby sodium and chlorine pairs, electrostatic forces are responsible for keeping them together until they are pulled apart by water
    public void addSalt( Point2D location ) {
        SaltCrystal saltCrystal = new SaltCrystal( this, location );

        //Move any waters away that these particles would overlap.  Otherwise the water can cause the Na to bump away from the Cl immediately instead of having them
        for ( WaterMolecule water : waterList ) {
            if ( water.intersects( saltCrystal.sodium ) || water.intersects( saltCrystal.chloride ) || water.intersects( saltCrystal.sodium2 ) || water.intersects( saltCrystal.chloride2 ) ) {
                water.setModelPosition( water.getModelPosition().plus( new ImmutableVector2D( 4 + Math.random(), 4 + Math.random() ) ) );
            }
        }

        sodiumList.add( saltCrystal.sodium );
        chlorineList.add( saltCrystal.chloride );

        sodiumList.add( saltCrystal.sodium2 );
        chlorineList.add( saltCrystal.chloride2 );

        timeSinceSaltAdded = 0;
    }

    //Adds a sugar crystal near the center of the screen
    public void addSugar( Point2D location ) {
        ArrayList<Sucrose> sugarCrystal = createSugarCrystal( location );
        for ( Sucrose sucrose : sugarCrystal ) {
            sugarMoleculeList.add( sucrose );
        }
    }

    public ArrayList<Sucrose> createSugarCrystal( Point2D location ) {
        final double x = location.getX();
        final double y = location.getY();
        final double delta = particleWindow.height / 4 * 0.87;
        return new ArrayList<Sucrose>() {{
            add( createSucrose( x, y - delta / 2 ) );
            add( createSucrose( x, y + delta / 2 ) );
        }};
    }

    private void addSugar( double x, double y ) {
        sugarMoleculeList.add( createSucrose( x, y ) );
    }

    public Sucrose createSucrose( double x, double y ) {
        return new Sucrose( world, modelToBox2D, x, y, 0, 0, 0, addFrameListener, oxygenCharge, hydrogenCharge );
    }

    //Adds a single water molecule
    public void addWaterMolecule( double x, double y, float angle ) {
        waterList.add( new WaterMolecule( world, modelToBox2D, x, y, 0, 0, angle, addFrameListener, oxygenCharge, hydrogenCharge ) );
    }

    //Adds a single water molecule
    public void addWaterMolecule2( double x, double y, double angle ) {
        final WaterMolecule2 molecule = new WaterMolecule2( new ImmutableVector2D( x, y ), angle );
        waterList2.add( molecule );
        addConstituents( molecule );

        //Add the adapter for box2D
        Box2DAdapter box2DAdapter = new Box2DAdapter( world, molecule, modelToBox2D );
        box2DAdapters.add( box2DAdapter );
    }

    public void addWaterAddedListener( VoidFunction1<WaterMolecule> waterAddedListener ) {
        waterList.addElementAddedObserver( waterAddedListener );
    }

    protected void updateModel( double dt ) {

        long t = System.currentTimeMillis();

//        //Apply a random force so the system doesn't settle down, setting random velocity looks funny
//        for ( WaterMolecule waterMolecule : waterList ) {
//            float rand1 = ( random.nextFloat() - 0.5f ) * 2;
//            float rand2 = ( random.nextFloat() - 0.5f ) * 2;
//            waterMolecule.body.applyForce( new Vec2( rand1 * randomness.get(), rand2 * randomness.get() ), waterMolecule.body.getPosition() );
//        }

//        //Apply coulomb forces between all pairs of particles
//        for ( Molecule molecule : coulombForceOnAllMolecules.get() ? getAllMolecules() : waterList ) {
//            for ( Atom atom : molecule.atoms ) {
//                //Only apply the force in some interactions, to improve performance and increase randomness
//                if ( Math.random() < probabilityOfInteraction.get() ) {
//                    molecule.body.applyForce( getCoulombForce( atom.particle, false ), atom.particle.getBox2DPosition() );
//                }
//            }
//        }
//
//        //Na+ and Cl- should repel each other so they disassociate quickly
//        for ( Molecule molecule : new ArrayList<Molecule>() {{
//            addAll( sodiumList );
//            addAll( chlorineList );
//        }} ) {
//            for ( Atom atom : molecule.atoms ) {
//                molecule.body.applyForce( getCoulombForce( atom.particle, true ), atom.particle.getBox2DPosition() );
//            }
//        }
//
        long t2 = System.currentTimeMillis();
        if ( debugTime ) {
            System.out.println( "delta = " + ( t2 - t ) );
        }

        for ( Box2DAdapter box2DAdapter : box2DAdapters ) {
            if ( random.nextDouble() > 0.5 ) {
                for ( Constituent<ChargedSphericalParticle> constituent : box2DAdapter.compound ) {
                    for ( ChargedSphericalParticle particle : particles ) {
                        if ( !box2DAdapter.compound.containsParticle( particle ) ) {
                            double q1 = constituent.particle.getCharge();
                            double q2 = particle.getCharge();
                            final ImmutableVector2D coulombForce = getCoulombForce( constituent.particle, particle, q1, q2 ).times( 1E-35 );
                            box2DAdapter.applyModelForce( coulombForce, constituent.particle.getPosition() );
                        }
                    }
                }
            }
        }

        //Box2D will exception unless values are within its sweet spot range.
        //if DT gets too low, it is hard to recover from assertion errors in box2D
        //It is supposed to run at 60Hz, with velocities not getting too large (300m/s is too large): http://www.box2d.org/forum/viewtopic.php?f=4&t=1205
        world.step( (float) ( dt * timeScale.get() ), iterations.get(), iterations.get() );

        //Turn off the animation thread in the test panel, we are doing the animation ourselves
        testPanel.stop();

        //Make sure the debug draw paints on the screen
        testPanel.render();
        world.drawDebugData();
        testPanel.paintImmediately( 0, 0, testPanel.getWidth(), testPanel.getHeight() );

        //Apply periodic boundary conditions
        applyPeriodicBoundaryConditions( box2DAdapters );

        //Factor out center of mass motion so no large scale drifts can occur
        subtractOutCenterOfMomentum();

        for ( Box2DAdapter box2DAdapter : box2DAdapters ) {
            box2DAdapter.worldStepped();
        }

        //Notify listeners that the model changed
        for ( VoidFunction0 frameListener : frameListeners ) {
            frameListener.apply();
        }
        timeSinceSaltAdded += dt;
    }

    //Get the coulomb force between two particles
    //The particles should be from different compounds since compounds shouldn't have intra-molecular forces
    private ImmutableVector2D getCoulombForce( SphericalParticle source, SphericalParticle target, double q1, double q2 ) {
        final ImmutableVector2D sourcePosition = source.getPosition();
        final ImmutableVector2D targetPosition = target.getPosition();
        if ( sourcePosition.equals( targetPosition ) ) {
            return ZERO;
        }
        double distance = sourcePosition.getDistance( targetPosition );
        double scale = -1 * k * q1 * q2 / distance / distance / distance;
        return new ImmutableVector2D( ( targetPosition.getX() - sourcePosition.getX() ) * scale, ( targetPosition.getY() - sourcePosition.getY() ) * scale );
    }

    //Factor out center of mass motion so no large scale drifts can occur
    private void subtractOutCenterOfMomentum() {
        Vec2 totalMomentum = getBox2DMomentum();
        for ( Box2DAdapter molecule : box2DAdapters ) {
            Vec2 v = molecule.body.getLinearVelocity();
            final Vec2 delta = totalMomentum.mul( (float) ( -1 / getBox2DMass() ) );
            molecule.body.setLinearVelocity( v.add( delta ) );
        }
        //        System.out.println( "init tot mom = " + totalMomentum + ", after = " + getTotalMomentum() );
    }

    private Vec2 getBox2DMomentum() {
        Vec2 totalMomentum = new Vec2();
        for ( Box2DAdapter adapter : box2DAdapters ) {
            Vec2 v = adapter.body.getLinearVelocity();
            totalMomentum.x += v.x * adapter.body.getMass();
            totalMomentum.y += v.y * adapter.body.getMass();
        }
        return totalMomentum;
    }

    private double getBox2DMass() {
        double m = 0.0;
        for ( Box2DAdapter molecule : box2DAdapters ) {
            m += molecule.body.getMass();
        }
        return m;
    }

    private ArrayList<Molecule> getAllMolecules() {
        return new ArrayList<Molecule>() {{
            addAll( waterList );
            addAll( sugarMoleculeList );
            addAll( sodiumList );
            addAll( chlorineList );
        }};
    }

    //Move particles from one side of the screen to the other if they went out of bounds
    //TODO: extend this boundary beyond the visible range
    private void applyPeriodicBoundaryConditions( ArrayList<Box2DAdapter> list ) {
        for ( Box2DAdapter adapter : list ) {
            Compound<ChargedSphericalParticle> particle = adapter.compound;
            double x = particle.getPosition().getX();
            double y = particle.getPosition().getY();
            if ( particle.getPosition().getX() > particleWindow.getMaxX() ) {
                x = particleWindow.x;
            }
            if ( particle.getPosition().getX() < particleWindow.x ) {
                x = particleWindow.getMaxX();
            }
            if ( particle.getPosition().getY() > particleWindow.getMaxY() ) {
                y = particleWindow.y;
            }
            if ( particle.getPosition().getY() < particleWindow.y ) {
                y = particleWindow.getMaxY();
            }
            if ( !new ImmutableVector2D( x, y ).equals( adapter.getModelPosition() ) ) {
                adapter.setModelPosition( new ImmutableVector2D( x, y ) );
            }
        }
    }

    //Gets the force on a single particle
    private Vec2 getCoulombForce( Particle target, boolean repel ) {
        Vec2 sumForces = new Vec2();
        for ( Molecule m : getAllMolecules() ) {
            for ( Atom atom : m.atoms ) {
                sumForces = sumForces.add( getCoulombForce( atom.particle, target, repel ) );
            }
        }
        return sumForces;
    }

    //Get the contribution to the total coulomb force from a single source
    private Vec2 getCoulombForce( Particle source, Particle target, boolean repel ) {
        //Precompute for performance reasons
        final Vec2 sourceBox2DPosition = source.getBox2DPosition();
        final Vec2 targetBox2DPosition = target.getBox2DPosition();

        if ( source == target ||
             ( sourceBox2DPosition.x == targetBox2DPosition.x && sourceBox2DPosition.y == targetBox2DPosition.y ) ) {
            return zero;
        }
        Vec2 r = sourceBox2DPosition.sub( targetBox2DPosition );
        double distance = r.length();
//        System.out.println( distance );

        //Limit the max force or objects will get accelerated too much
        //After particles get far enough apart, just ignore the force.  Otherwise Na+ and Cl- will seek each other out from far away.
        //Units are box2d units
        final double MIN = minInteractionDistance.get();
        final double MAX = maxInteractionDistance.get();
        if ( distance < MIN ) {
            distance = MIN;
        }
        else if ( distance > MAX ) {
            return zero;
        }

        double q1 = source.getCharge();
        double q2 = target.getCharge();

        double distanceFunction = 1 / Math.pow( distance, pow.get() );
        double magnitude = -k * q1 * q2 * distanceFunction;
        r.normalize();
        if ( repel ) {
            magnitude = Math.abs( magnitude ) * 2.5;//Overcome the true attractive force, and then some

            //If the salt was just added, use full repulsive power, otherwise half the power
            if ( timeSinceSaltAdded > 0.75 ) {
                magnitude = magnitude / 2;
            }
        }
        return r.mul( (float) magnitude );
    }

    //Get all bodies in the model
    public ArrayList<WaterMolecule> getWaterList() {
        return waterList.toList();
    }

    //Register for a callback when the model steps
    public void addFrameListener( VoidFunction0 listener ) {
        frameListeners.add( listener );
    }

    //Resets the model, clearing water molecules and starting over
    public void reset() {
        initModel();
        showSugarAtoms.reset();
        showWaterCharges.reset();
    }

    public void clear( ItemList<? extends Molecule> list, World world ) {
        for ( Molecule t : list ) {
            world.destroyBody( t.body );
            t.notifyRemoved();
        }
        list.clear();
    }

    //Set up the initial model state, used on init and after reset
    protected void initModel() {
        clear( waterList, world );
        clearSalt();
        clearSugar();

        //Add water particles
        addWaterParticles();
    }

    public void clearSalt() {
        clear( sodiumList, world );
        clear( chlorineList, world );
    }

    public ArrayList<DefaultParticle> getSodiumIonList() {
        return sodiumList.toList();
    }

    public void addSodiumIonAddedListener( VoidFunction1<DefaultParticle> listener ) {
        sodiumList.addElementAddedObserver( listener );
    }

    public ArrayList<DefaultParticle> getChlorineIonList() {
        return chlorineList.toList();
    }

    public void addChlorineIonAddedListener( VoidFunction1<DefaultParticle> createNode ) {
        chlorineList.addElementAddedObserver( createNode );
    }

    //Gets a random number within the horizontal range of the beaker
    public double getRandomX() {
        return (float) ( SugarAndSaltSolutionsApplication.random.nextFloat() * particleWindow.width - particleWindow.width / 2 );
    }

    //Gets a random number within the vertical range of the beaker
    public double getRandomY() {
        return (float) ( SugarAndSaltSolutionsApplication.random.nextFloat() * particleWindow.height );
    }

    public void addSugarAddedListener( VoidFunction1<Sucrose> createNode ) {
        sugarMoleculeList.addElementAddedObserver( createNode );
    }

    //Called when the user presses a button to clear the sugar, removes all sugar (dissolved and crystals) from the sim
    public void removeSugar() {
        clearSugar();
    }

    private void clearSugar() {
        clear( sugarMoleculeList, world );
    }
}