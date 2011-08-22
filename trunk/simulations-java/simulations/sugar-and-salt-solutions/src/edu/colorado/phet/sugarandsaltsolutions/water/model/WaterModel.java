// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.water.model;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

import javax.swing.JFrame;

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
import edu.colorado.phet.common.phetcommon.util.Pair;
import edu.colorado.phet.common.phetcommon.util.function.Function1;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.sugarandsaltsolutions.SugarAndSaltSolutionsApplication;
import edu.colorado.phet.sugarandsaltsolutions.common.model.AbstractSugarAndSaltSolutionsModel;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.Compound;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.ItemList;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.SphericalParticle;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.sucrose.Sucrose;

import static edu.colorado.phet.common.phetcommon.math.ImmutableVector2D.ZERO;

/**
 * Model for "water" tab for sugar and salt solutions.
 *
 * @author Sam Reid
 */
public class WaterModel extends AbstractSugarAndSaltSolutionsModel {

    //Lists of all water molecules
    public final ItemList<WaterMolecule> waterList = new ItemList<WaterMolecule>();

    //List of all sucrose crystals
    public final ItemList<Sucrose> sucroseList = new ItemList<Sucrose>();

    //List of all salt ions
    public final ItemList<SaltIon> saltIonList = new ItemList<SaltIon>();

    //Listeners who are called back when the physics updates
    private ArrayList<VoidFunction0> frameListeners = new ArrayList<VoidFunction0>();

    //Box2d world which updates the physics
    public final World world;

    private Random random = new Random();

    //Dimensions of the particle window in meters, determines the zoom level in the view as well since it fits to the model particle window
    private final double particleWindowWidth = 2.3E-9 * 0.9;
    private final double particleWindowHeight = particleWindowWidth * 0.65;

    //Dimensions of the particle window in meters, determines the zoom level in the view as well since it fits to the model particle window
    public final ImmutableRectangle2D particleWindow = new ImmutableRectangle2D( -particleWindowWidth / 2, -particleWindowHeight / 2, particleWindowWidth, particleWindowHeight );

    //Boundary for water's periodic boundary conditions, so that particles don't disappear when they wrap from one side to the other
    //Different boundaries are used for different molecule types so we can keep the number of particles low; the largest molecule is sucrose
    //But if we used the expanded sucrose boundary for water, then we would need lots of extra water out of the visible region
    //This also has the effect of keeping salt ions or sucrose molecules away from the play area once they exit,
    //but that seems preferable to (less confusing than) re-entering the screen from a different direction
    public final ImmutableRectangle2D waterBoundary = expand( particleWindow, getHalfDiagonal( new WaterMolecule().getShape().getBounds2D() ) );
    public final ImmutableRectangle2D sucroseBoundary = expand( particleWindow, getHalfDiagonal( new Sucrose().getShape().getBounds2D() ) );
    public final ImmutableRectangle2D chlorideBoundary = expand( particleWindow, getHalfDiagonal( new SaltIon.ChlorideIon().getShape().getBounds2D() ) );

    private static final double DISTANCE_THRESHOLD = new SaltIon.ChlorideIon().getShape().getBounds2D().getWidth() * 1.3;
    public final double COULOMB_FORCE_SCALE_FACTOR = 5E-36 / 10 * 2;

    //Expand a rectangle by the specified size in all 4 directions
    private static ImmutableRectangle2D expand( ImmutableRectangle2D r, double size ) {
        return new ImmutableRectangle2D( r.x - size, r.y - size, r.width + size * 2, r.height + size * 2 );
    }

    //Determine the length from one corner to the center of the rectangle, this is used to determine how far to move the periodic boundary condition from the visible model rectangle
    //So that particles don't disappear when they wrap from one side to the other
    private static double getHalfDiagonal( Rectangle2D bounds2D ) {
        return new ImmutableVector2D( new Point2D.Double( bounds2D.getX(), bounds2D.getY() ), new Point2D.Double( bounds2D.getCenterX(), bounds2D.getCenterY() ) ).getMagnitude();
    }

    //Width of the box2D model
    private final double box2DWidth = 20;

    //units for water molecules are in SI
    //Beaker floor should be about 40 angstroms, to accommodate about 20 water molecules side-to-side
    //But keep box2d within -10..10 (i.e. 20 boxes wide)
    double scaleFactor = box2DWidth / particleWindow.width;
    public final ModelViewTransform modelToBox2D = ModelViewTransform.createSinglePointScaleMapping( new Point(), new Point(), scaleFactor );

    private static final int DEFAULT_NUM_WATERS = 140;

    //Properties for developer controls
    public final Property<Integer> pow = new Property<Integer>( 2 );
    public final Property<Integer> randomness = new Property<Integer>( 5 );
    public final Property<Double> minInteractionDistance = new Property<Double>( 0.05 );
    public final Property<Double> maxInteractionDistance = new Property<Double>( 2.0 );
    public final Property<Double> probabilityOfInteraction = new Property<Double>( 0.6 );
    public final Property<Double> timeScale = new Property<Double>( 0.1 );
    public final Property<Integer> iterations = new Property<Integer>( 10 );

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
    //TODO: account for time since salt added
    private double timeSinceSaltAdded = 0;

    private static final boolean debugTime = false;

    //List of adapters that manage both the box2D and actual model data
    private ItemList<Box2DAdapter> box2DAdapters = new ItemList<Box2DAdapter>();

    //Panel that allows us to see jbox2d model and computations
    protected TestPanel testPanel;

    //Flag to enable/disable the jbox2D DebugDraw mode, which shows the box2d model and computations
    private boolean useDebugDraw = false;

    //Keep track of how many waters get deleted when sucrose molecule is dropped so they can be added back when the user grabs the sucrose molecule
    private final HashMap<Compound<SphericalParticle>, Integer> deletedWaterCount = new HashMap<Compound<SphericalParticle>, Integer>();

    //Convenience adapters for reuse with CompoundListNode for adding/removing crystals or molecules
    public final VoidFunction1<Sucrose> addSucrose = new VoidFunction1<Sucrose>() {
        public void apply( Sucrose sucrose ) {
            addSucroseMolecule( sucrose );
        }
    };
    public final VoidFunction1<Sucrose> removeSucrose = new VoidFunction1<Sucrose>() {
        public void apply( Sucrose sucrose ) {
            removeSucrose( sucrose );
        }
    };
    public final VoidFunction1<SaltIon> addSaltIon = new VoidFunction1<SaltIon>() {
        public void apply( SaltIon ion ) {
            addSaltIon( ion );
        }
    };
    public final VoidFunction1<SaltIon> removeSaltIon = new VoidFunction1<SaltIon>() {
        public void apply( SaltIon ion ) {
            removeSaltIon( ion );
        }
    };

    public WaterModel() {
        super( new ConstantDtClock( 30 ) );

        //Create the Box2D world with no gravity
        world = new World( new Vec2( 0, 0 ), true );

        //Set up initial state, same as reset() method would do, such as adding water particles to the model
        initModel();

        //Set up jbox2D debug draw so we can see the model and computations
        if ( useDebugDraw ) {
            initDebugDraw();
        }
    }

    private void addWaterParticles() {
        for ( int i = 0; i < DEFAULT_NUM_WATERS; i++ ) {
            addWaterMolecule( randomBetweenMinusOneAndOne() * particleWindow.width / 2, randomBetweenMinusOneAndOne() * particleWindow.height / 2, random.nextDouble() * Math.PI * 2 );
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

        world.setDebugDraw( new DebugDrawJ2D( testPanel ) {{

            //Show the shapes in the debugger
            setFlags( e_shapeBit );

            //Move the camera over so that the shapes will show up at a good size and location
            setCamera( -10, 10, 20 );
        }} );
    }

    //Adds a single water molecule
    public void addWaterMolecule( double x, double y, double angle ) {
        final WaterMolecule molecule = new WaterMolecule( new ImmutableVector2D( x, y ), angle );
        waterList.add( molecule );

        //Add the adapter for box2D
        box2DAdapters.add( new Box2DAdapter( world, molecule, modelToBox2D ) );
    }

    protected void updateModel( double dt ) {

        long t = System.currentTimeMillis();

        long t2 = System.currentTimeMillis();
        if ( debugTime ) {
            System.out.println( "delta = " + ( t2 - t ) );
        }

        //Iterate over all pairs of particles and apply the coulomb force, but only consider particles from different molecules (no intramolecular forces)
        for ( Box2DAdapter box2DAdapter : box2DAdapters ) {
            if ( random.nextDouble() < probabilityOfInteraction.get() ) {
                for ( SphericalParticle target : box2DAdapter.compound ) {
                    for ( SphericalParticle source : getAllParticles() ) {
                        if ( !box2DAdapter.compound.containsParticle( source ) ) {
                            final ImmutableVector2D coulombForce = getCoulombForce( source, target ).times( COULOMB_FORCE_SCALE_FACTOR );
                            box2DAdapter.applyModelForce( coulombForce, target.getPosition() );
                        }
                    }
                }
            }
        }

        //Apply a force to keep the Na+ and Cl- from staying too close together
        //First find the ions that are too close together
        ItemList<Pair<SaltIon, SaltIon>> tooClose = getSaltIonPairs().filter( new Function1<Pair<SaltIon, SaltIon>, Boolean>() {
            public Boolean apply( Pair<SaltIon, SaltIon> saltIonSaltIonPair ) {
                double distance = saltIonSaltIonPair._1.getPosition().getDistance( saltIonSaltIonPair._2.getPosition() );
                return distance < DISTANCE_THRESHOLD;
            }
        } );

        //Apply an attractive coulomb force to guide water molecules to the center of mass of Na+ Cl- pairs that were too close, to split them up
        //Coulomb force is useful here so that it affects close-by particles more that particles that are distant
        for ( Box2DAdapter box2DAdapter : box2DAdapters ) {
            if ( box2DAdapter.compound instanceof WaterMolecule ) {
                for ( Pair<SaltIon, SaltIon> saltIonSaltIonPair : tooClose ) {

                    //Find the centroid
                    ImmutableVector2D p1 = saltIonSaltIonPair._1.getPosition();
                    ImmutableVector2D p2 = saltIonSaltIonPair._2.getPosition();
                    ImmutableVector2D center = p1.plus( p2 ).times( 0.5 );

                    //Compute and apply an attractive coulomb force from the water molecules to the centroids
                    final ImmutableVector2D modelPosition = box2DAdapter.getModelPosition();
                    double scale = 3;
                    ImmutableVector2D coulombForce = getCoulombForce( center, modelPosition, scale, -scale ).times( COULOMB_FORCE_SCALE_FACTOR );
//                    System.out.println( "coulombForce = \t" + coulombForce.getMagnitude() );
                    if ( coulombForce.getMagnitude() > 1E-5 ) {
                        coulombForce = coulombForce.getInstanceOfMagnitude( 1E-5 );
                    }
                    box2DAdapter.applyModelForce( coulombForce, modelPosition );
                }
            }
        }

        //Box2D will exception unless values are within its sweet spot range.
        //if DT gets too low, it is hard to recover from assertion errors in box2D
        //It is supposed to run at 60Hz, with velocities not getting too large (300m/s is too large): http://www.box2d.org/forum/viewtopic.php?f=4&t=1205
        world.step( (float) ( dt * timeScale.get() ), iterations.get(), iterations.get() );

        if ( useDebugDraw ) {

            //Turn off the animation thread in the test panel, we are doing the animation ourselves
            testPanel.stop();

            //Make sure the debug draw paints on the screen
            testPanel.render();
            world.drawDebugData();
            testPanel.paintImmediately( 0, 0, testPanel.getWidth(), testPanel.getHeight() );
        }

        //Apply periodic boundary conditions
        applyPeriodicBoundaryConditions();

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

    //Get all pairs of salt ions, including Na+/Cl- and Na+/Na+ combinations so that the water can make sure they dissolve and move far enough away
    private ItemList<Pair<SaltIon, SaltIon>> getSaltIonPairs() {
        return new ItemList<Pair<SaltIon, SaltIon>>() {{
            for ( SaltIon a : saltIonList ) {
                for ( SaltIon b : saltIonList ) {
                    if ( a != b ) {
                        add( new Pair<SaltIon, SaltIon>( a, b ) );
                    }
                }
            }
        }};
    }

    //Get all interacting particles from the lists of water, sucrose, etc.
    private Iterable<? extends SphericalParticle> getAllParticles() {
        return new ArrayList<SphericalParticle>() {{
            for ( WaterMolecule waterMolecule : waterList ) {
                for ( SphericalParticle waterAtom : waterMolecule ) {
                    add( waterAtom );
                }
            }
            for ( Sucrose sucrose : sucroseList ) {
                for ( SphericalParticle sucroseAtom : sucrose ) {
                    add( sucroseAtom );
                }
            }
            for ( SaltIon saltIon : saltIonList ) {
                for ( SphericalParticle saltAtom : saltIon ) {
                    add( saltAtom );
                }
            }
        }};
    }

    //Get the coulomb force between two particles
    //The particles should be from different compounds since compounds shouldn't have intra-molecular forces
    private ImmutableVector2D getCoulombForce( SphericalParticle source, SphericalParticle target ) {
        return getCoulombForce( source.getPosition(), target.getPosition(), source.getCharge(), target.getCharge() );
    }

    //Get the coulomb force between two points with the specified charges
    private ImmutableVector2D getCoulombForce( ImmutableVector2D sourcePosition, ImmutableVector2D targetPosition, double q1, double q2 ) {
        if ( sourcePosition.equals( targetPosition ) ) {
            return ZERO;
        }
        double distance = sourcePosition.getDistance( targetPosition );
        double scale = k * q1 * q2 / distance / distance / distance;
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

    //Move particles from one side of the screen to the other if they went out of bounds
    //Use the extended boundary to prevent flickering when a particle wraps from one side to the other
    private void applyPeriodicBoundaryConditions() {
        for ( Box2DAdapter adapter : box2DAdapters ) {
            Compound<SphericalParticle> particle = adapter.compound;
            double x = particle.getPosition().getX();
            double y = particle.getPosition().getY();
            final ImmutableRectangle2D boundary = getBoundary( adapter.compound );
            if ( particle.getPosition().getX() > boundary.getMaxX() ) {
                x = boundary.x;
            }
            if ( particle.getPosition().getX() < boundary.x ) {
                x = boundary.getMaxX();
            }
            if ( particle.getPosition().getY() > boundary.getMaxY() ) {
                y = boundary.y;
            }
            if ( particle.getPosition().getY() < boundary.y ) {
                y = boundary.getMaxY();
            }
            if ( !new ImmutableVector2D( x, y ).equals( adapter.getModelPosition() ) ) {
                adapter.setModelPosition( new ImmutableVector2D( x, y ) );
            }
        }
    }

    //Lookup the boundary to use for the specified type, see docs at the declaration of the boundary instances for explanation
    private ImmutableRectangle2D getBoundary( Compound<SphericalParticle> compound ) {
        if ( compound instanceof Sucrose ) { return sucroseBoundary; }
        else if ( compound instanceof SaltIon.ChlorideIon ) { return chlorideBoundary; }
        else if ( compound instanceof SaltIon.SodiumIon ) { return chlorideBoundary; }
        else if ( compound instanceof WaterMolecule ) { return waterBoundary; }
        else { throw new IllegalArgumentException( "unknown type: " + compound.getClass() ); }
    }

    //Resets the model, clearing water molecules and starting over
    public void reset() {
        initModel();
        showSugarAtoms.reset();
        showWaterCharges.reset();
    }

    //Set up the initial model state, used on init and after reset
    protected void initModel() {

        //Clear out the box2D world
        for ( Box2DAdapter box2DAdapter : box2DAdapters ) {
            world.destroyBody( box2DAdapter.body );
        }
        box2DAdapters.clear();
        waterList.clear();
        sucroseList.clear();
        saltIonList.clear();

        //Add water particles
        addWaterParticles();
    }

    //Gets a random number within the horizontal range of the beaker
    public double getRandomX() {
        return (float) ( SugarAndSaltSolutionsApplication.random.nextFloat() * particleWindow.width - particleWindow.width / 2 );
    }

    //Gets a random number within the vertical range of the beaker
    public double getRandomY() {
        return (float) ( SugarAndSaltSolutionsApplication.random.nextFloat() * particleWindow.height );
    }

    //Remove the overlapping water so it doesn't overlap and cause box2d problems due to occupying the same space at the same time
    private void removeOverlappingWater( Compound<SphericalParticle> compound ) {
        HashSet<WaterMolecule> toRemove = getOverlappingWaterMolecules( compound );
        waterList.removeAll( toRemove );
        ArrayList<Box2DAdapter> box2DAdaptersToRemove = getBox2DAdapters( toRemove );
        for ( Box2DAdapter box2DAdapter : box2DAdaptersToRemove ) {
            world.destroyBody( box2DAdapter.body );
            box2DAdapters.remove( box2DAdapter );
        }

        //Store the number of deleted waters so they can be added back if/when the user grabs the sucrose molecule
        deletedWaterCount.put( compound, toRemove.size() );
    }

    //Find the Box2DAdapters for the specified water molecules, used for removing intersecting water when crystals are added by the user
    private ArrayList<Box2DAdapter> getBox2DAdapters( HashSet<WaterMolecule> set ) {
        ArrayList<Box2DAdapter> box2DAdaptersToRemove = new ArrayList<Box2DAdapter>();
        for ( Box2DAdapter box2DAdapter : box2DAdapters ) {
            if ( set.contains( box2DAdapter.compound ) ) {
                box2DAdaptersToRemove.add( box2DAdapter );
            }
        }
        return box2DAdaptersToRemove;
    }

    //Find which water molecules overlap with the specified crystal so they can be removed before the crystal is added, to prevent box2d body overlaps
    private HashSet<WaterMolecule> getOverlappingWaterMolecules( final Compound<SphericalParticle> compound ) {
        return new HashSet<WaterMolecule>() {{

            //Iterate over all water atoms
            for ( WaterMolecule waterMolecule : waterList ) {
                for ( SphericalParticle waterAtom : waterMolecule ) {

                    //Iterate over all sucrose atoms
                    for ( SphericalParticle atom : compound ) {

                        //add if they are overlapping
                        if ( waterAtom.getPosition().getDistance( atom.getPosition() ) < waterAtom.radius + atom.radius ) {
                            add( waterMolecule );
                        }
                    }
                }
            }
        }};
    }

    //Add the specified sucrose crystal to the model
    public void addSucroseMolecule( Sucrose sucrose ) {

        //Remove the overlapping water so it doesn't overlap and cause box2d problems due to occupying the same space at the same time
        removeOverlappingWater( sucrose );

        //Add the sucrose crystal and box2d adapters for all its molecules so they will propagate with box2d physics
        sucroseList.add( sucrose );
        box2DAdapters.add( new Box2DAdapter( world, sucrose, modelToBox2D ) );
    }

    //Remove a sucrose from the model.  This can be called when the user grabs a sucrose molecule in the play area, and is called so that box2D won't continue to move the sucrose while the user is moving it
    public void removeSucrose( final Sucrose sucrose ) {
        if ( sucroseList.contains( sucrose ) ) {

            //Find the box2D adapters to remove, hopefully there is only one!
            ArrayList<Box2DAdapter> toRemove = box2DAdapters.filterToArrayList( new Function1<Box2DAdapter, Boolean>() {
                public Boolean apply( Box2DAdapter box2DAdapter ) {
                    return box2DAdapter.compound == sucrose;
                }
            } );

            //Remove the box2D components
            for ( Box2DAdapter box2DAdapter : toRemove ) {
                world.destroyBody( box2DAdapter.body );
                box2DAdapters.remove( box2DAdapter );
            }

            //Remove the sucrose itself
            sucroseList.remove( sucrose );

            //Add back as many waters as were deleted when the sucrose was added to the model to conserve water molecule count
            addWaterWhereSucroseWas( sucrose );
        }
    }

    //Add back as many waters as were deleted when the sucrose was added to the model to conserve water molecule count
    private void addWaterWhereSucroseWas( Sucrose sucrose ) {

        //First make sure we only add back missing molecules once for each time they were removed to conserve water molecule count
        if ( deletedWaterCount.containsKey( sucrose ) ) {
            int numWatersToAdd = deletedWaterCount.get( sucrose );

            //Randomly distribute the new water molecules in the region of the sucrose that was removed
            final Rectangle2D bounds = sucrose.getShape().getBounds2D();
            for ( int i = 0; i < numWatersToAdd; i++ ) {
                addWaterMolecule( randomBetweenMinusOneAndOne() * bounds.getWidth() + bounds.getCenterX(),
                                  randomBetweenMinusOneAndOne() * bounds.getHeight() + bounds.getCenterY(), random.nextDouble() * 2 * Math.PI );
            }

            //Remove from the map to indicate we have accounted for the removed water particles
            deletedWaterCount.remove( sucrose );
        }
    }

    //Add the specified ion crystal to the model
    public void addSaltIon( SaltIon ion ) {

        //Remove the overlapping water so it doesn't overlap and cause box2d problems due to occupying the same space at the same time
        //TODO: should remove overlapping water?
//        removeOverlappingWater( ion );

        //Add the ion crystal and box2d adapters for all its molecules so they will propagate with box2d physics
        saltIonList.add( ion );
        box2DAdapters.add( new Box2DAdapter( world, ion, modelToBox2D ) );
    }

    //Remove a sucrose from the model.  This can be called when the user grabs a sucrose molecule in the play area, and is called so that box2D won't continue to move the sucrose while the user is moving it
    public void removeSaltIon( final SaltIon ion ) {
        if ( saltIonList.contains( ion ) ) {

            //Find the box2D adapters to remove, hopefully there is only one!
            ArrayList<Box2DAdapter> toRemove = box2DAdapters.filterToArrayList( new Function1<Box2DAdapter, Boolean>() {
                public Boolean apply( Box2DAdapter box2DAdapter ) {
                    return box2DAdapter.compound == ion;
                }
            } );

            //Remove the box2D components
            for ( Box2DAdapter box2DAdapter : toRemove ) {
                world.destroyBody( box2DAdapter.body );
                box2DAdapters.remove( box2DAdapter );
            }

            //Remove the sucrose itself
            saltIonList.remove( ion );
        }
    }

    public static void main( String[] args ) {
        final WaterModel model = new WaterModel();
        ImmutableVector2D force = model.getCoulombForce( ZERO, new ImmutableVector2D( 1, 0 ), 1, 1 );
        System.out.println( "force = " + force );
    }

    //Code taken from constructor
    //Attempted using collision boundaries, but they make the system come to rest too quickly, we will probably use periodic boundary conditions instead
//        Body body = world.createBody( new BodyDef() {{
//            type = BodyType.STATIC;
//        }} );
//        final ImmutableRectangle2D particleWindowBox2D = modelToBox2D.modelToView( particleWindow );
//        body.createFixture( new PolygonShape() {{ setAsBox( (float) particleWindowBox2D.width / 2, (float) ( particleWindowBox2D.height / 2 ), new Vec2( 0, (float) ( particleWindowBox2D.height ) ), 0 ); }}, 1 );
//        body.createFixture( new PolygonShape() {{ setAsBox( (float) particleWindowBox2D.width / 2, (float) ( particleWindowBox2D.height / 2 ), new Vec2( 0, (float) ( -particleWindowBox2D.height ) ), 0 ); }}, 1 );
//        body.createFixture( new PolygonShape() {{ setAsBox( (float) particleWindowBox2D.width / 2, (float) ( particleWindowBox2D.height / 2 ), new Vec2( (float) ( particleWindowBox2D.width ), 0 ), 0 ); }}, 1 );
//        body.createFixture( new PolygonShape() {{ setAsBox( (float) particleWindowBox2D.width / 2, (float) ( particleWindowBox2D.height / 2 ), new Vec2( (float) ( -particleWindowBox2D.width ), 0 ), 0 ); }}, 1 );

    //Move to a stable state on startup
    //Commented out because it takes too long
//        long startTime = System.currentTimeMillis();
//        for ( int i = 0; i < 10; i++ ) {
//            world.step( (float) ( clock.getDt() * 10 ), 1 );
//        }
//        System.out.println( "stable start time: " + ( System.currentTimeMillis() - startTime ) );

//Code taken from update
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

//    //Get the contribution to the total coulomb force from a single source
//    private Vec2 getCoulombForce( Particle source, Particle target, boolean repel ) {
//        //Precompute for performance reasons
//        final Vec2 sourceBox2DPosition = source.getBox2DPosition();
//        final Vec2 targetBox2DPosition = target.getBox2DPosition();
//
//        if ( source == target ||
//             ( sourceBox2DPosition.x == targetBox2DPosition.x && sourceBox2DPosition.y == targetBox2DPosition.y ) ) {
//            return zero;
//        }
//        Vec2 r = sourceBox2DPosition.sub( targetBox2DPosition );
//        double distance = r.length();
////        System.out.println( distance );
//
//        //Limit the max force or objects will get accelerated too much
//        //After particles get far enough apart, just ignore the force.  Otherwise Na+ and Cl- will seek each other out from far away.
//        //Units are box2d units
//        final double MIN = minInteractionDistance.get();
//        final double MAX = maxInteractionDistance.get();
//        if ( distance < MIN ) {
//            distance = MIN;
//        }
//        else if ( distance > MAX ) {
//            return zero;
//        }
//
//        double q1 = source.getCharge();
//        double q2 = target.getCharge();
//
//        double distanceFunction = 1 / Math.pow( distance, pow.get() );
//        double magnitude = -k * q1 * q2 * distanceFunction;
//        r.normalize();
//        if ( repel ) {
//            magnitude = Math.abs( magnitude ) * 2.5;//Overcome the true attractive force, and then some
//
//            //If the salt was just added, use full repulsive power, otherwise half the power
//            if ( timeSinceSaltAdded > 0.75 ) {
//                magnitude = magnitude / 2;
//            }
//        }
//        return r.mul( (float) magnitude );
//    }

}