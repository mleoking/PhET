// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.water.model;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Random;

import javax.swing.JFrame;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.World;
import org.jbox2d.testbed.framework.DebugDrawJ2D;
import org.jbox2d.testbed.framework.TestPanel;
import org.jbox2d.testbed.framework.TestbedSettings;

import edu.colorado.phet.common.phetcommon.math.ImmutableRectangle2D;
import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.Pair;
import edu.colorado.phet.common.phetcommon.util.function.Function1;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.sugarandsaltsolutions.common.model.AbstractSugarAndSaltSolutionsModel;
import edu.colorado.phet.sugarandsaltsolutions.common.model.Compound;
import edu.colorado.phet.sugarandsaltsolutions.common.model.ItemList;
import edu.colorado.phet.sugarandsaltsolutions.common.model.SphericalParticle;
import edu.colorado.phet.sugarandsaltsolutions.common.model.sucrose.Sucrose;

import static edu.colorado.phet.common.phetcommon.math.Vector2D.ZERO;

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
    private final ArrayList<VoidFunction0> frameListeners = new ArrayList<VoidFunction0>();

    //Box2d world which updates the physics
    public final World world;

    //Randomness for laying out and propagating the particles
    private final Random random = new Random();

    //Dimensions of the particle window in meters, determines the zoom level in the view as well since it fits to the model particle window
    private final double particleWindowWidth = 1.84E-9; //Manually tuned until particles were big enough to see but small enough that you could see several but big enough that there wouldn't have to be too many of them
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

    //Thresholds and settings for artificial force on waters to split up salt or sucrose components that are too close to each other
    private static final double SALT_ION_DISTANCE_THRESHOLD = new SaltIon.ChlorideIon().getShape().getBounds2D().getWidth() * 1.3;
    private static final double SUCROSE_DISTANCE_THRESHOLD = new Sucrose().getShape().getBounds2D().getWidth();
    public final double COULOMB_FORCE_SCALE_FACTOR = 1.0E-36; //Tuned with discussions with the chemistry team to make interactions strong enough but not too strong

    //Flag to indicate debugging of removal of water when sucrose added, to keep water density constant
    private final boolean debugWaterRemoval = false;

    //Expand a rectangle by the specified size in all 4 directions
    private static ImmutableRectangle2D expand( ImmutableRectangle2D r, double size ) {
        return new ImmutableRectangle2D( r.x - size, r.y - size, r.width + size * 2, r.height + size * 2 );
    }

    //Determine the length from one corner to the center of the rectangle, this is used to determine how far to move the periodic boundary condition from the visible model rectangle
    //So that particles don't disappear when they wrap from one side to the other
    private static double getHalfDiagonal( Rectangle2D bounds2D ) {
        return new Vector2D( new Point2D.Double( bounds2D.getX(), bounds2D.getY() ), new Point2D.Double( bounds2D.getCenterX(), bounds2D.getCenterY() ) ).getMagnitude();
    }

    //Width of the box2D model.  Box2D is a physics engine used to drive the dynamics for this tab, see implementation-notes.txt and Box2DAdapter
    private final double box2DWidth = 20;

    //units for water molecules are in SI
    //Beaker floor should be about 40 angstroms, to accommodate about 20 water molecules side-to-side
    //But keep box2d within -10..10 (i.e. 20 boxes wide)
    final double scaleFactor = box2DWidth / particleWindow.width;
    public final ModelViewTransform modelToBox2D = ModelViewTransform.createSinglePointScaleMapping( new Point(), new Point(), scaleFactor );

    private static final int DEFAULT_NUM_WATERS = 130;

    //Coulomb's constant in SI, see http://en.wikipedia.org/wiki/Coulomb's_law
    private static final double k = 8.987E9;

    //User settings
    public final Property<Boolean> showWaterCharges = new Property<Boolean>( false );
    public final Property<Boolean> showSugarPartialCharge = new Property<Boolean>( false );
    public final Property<Boolean> showSugarAtoms = new Property<Boolean>( false );
    public final ObservableProperty<Boolean> showChargeColor = new Property<Boolean>( false );

    //Developer settings
    public final Property<Double> coulombStrengthMultiplier = new Property<Double>( 100.0 );//Scale factor that increases the strength of coulomb repulsion/attraction
    public final Property<Double> pow = new Property<Double>( 2.0 );//Power in the coulomb force radius term
    public final Property<Integer> randomness = new Property<Integer>( 5 );//How much randomness to add to the system
    public final Property<Double> probabilityOfInteraction = new Property<Double>( 0.6 );//Some interactions are ignored on each time step to improve performance.  But don't ignore too many or it will destroy the dynamics.
    public final Property<Double> timeScale = new Property<Double>( 0.06 );//How fast the clock should run
    public final Property<Integer> iterations = new Property<Integer>( 100 );//How many numerical iterations to run: more means more accurate but more processor used
    public final Property<Integer> overlaps = new Property<Integer>( 10 );//Only remove water molecules that intersected this many sucrose atoms, so that the density of water remains about the same

    //if the particles are too close, the coulomb force gets too big--a good way to limit the coulomb force is to limit the inter-particle distance used in the coulomb calculation
    public final double MIN_COULOMB_DISTANCE = new WaterMolecule.Hydrogen().radius * 2;

    //List of adapters that manage both the box2D and actual model data
    private final ItemList<Box2DAdapter> box2DAdapters = new ItemList<Box2DAdapter>();

    //Panel that allows us to see jbox2d model and computations
    protected TestPanel testPanel;

    //Flag to enable/disable the jbox2D DebugDraw mode, which shows the box2d model and computations
    private final boolean useDebugDraw = false;

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

    //Use the specified random number generator to get a number between [-1,1].
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
        final WaterMolecule molecule = new WaterMolecule( new Vector2D( x, y ), angle );
        waterList.add( molecule );

        //Add the adapter for box2D
        box2DAdapters.add( new Box2DAdapter( world, molecule, modelToBox2D ) );
    }

    protected double updateModel( double dt ) {

        //Iterate over all pairs of particles and apply the coulomb force, but only consider particles from different molecules (no intramolecular forces)
        for ( Box2DAdapter box2DAdapter : box2DAdapters ) {
            if ( random.nextDouble() < probabilityOfInteraction.get() ) {
                for ( SphericalParticle target : box2DAdapter.compound ) {
                    for ( SphericalParticle source : getAllParticles() ) {
                        if ( !box2DAdapter.compound.containsParticle( source ) ) {
                            final Vector2D coulombForce = getCoulombForce( source, target ).times( COULOMB_FORCE_SCALE_FACTOR );
                            box2DAdapter.applyModelForce( coulombForce, target.getPosition() );
                        }
                    }
                }
            }
        }

        //Apply a force to keep the Na+ and Cl- from staying too close together, and likewise with Sucrose/Sucrose
        //First find the ions that are too close together
        final ItemList<Pair<SaltIon, SaltIon>> saltTooClose = getSaltIonPairs().filter( new Function1<Pair<SaltIon, SaltIon>, Boolean>() {
            public Boolean apply( Pair<SaltIon, SaltIon> pair ) {
                return pair._1.getDistance( pair._2 ) < SALT_ION_DISTANCE_THRESHOLD;
            }
        } );
        final ItemList<Pair<Sucrose, Sucrose>> sucroseTooClose = getSucrosePairs().filter( new Function1<Pair<Sucrose, Sucrose>, Boolean>() {
            public Boolean apply( Pair<Sucrose, Sucrose> pair ) {
                return pair._1.getDistance( pair._2 ) < SUCROSE_DISTANCE_THRESHOLD;
            }
        } );

        ArrayList<Pair<? extends Compound<SphericalParticle>, ? extends Compound<SphericalParticle>>> all = new ArrayList<Pair<? extends Compound<SphericalParticle>, ? extends Compound<SphericalParticle>>>() {{
            addAll( saltTooClose );
            addAll( sucroseTooClose );
        }};

        //Apply an attractive coulomb force to guide water molecules to the center of mass of Na+ Cl- pairs that were too close, to split them up
        //Coulomb force is useful here so that it affects close-by particles more that particles that are distant
        for ( Box2DAdapter box2DAdapter : box2DAdapters ) {
            if ( box2DAdapter.compound instanceof WaterMolecule ) {
                for ( Pair<? extends Compound<SphericalParticle>, ? extends Compound<SphericalParticle>> pair : all ) {

                    //Find the centroid
                    Vector2D p1 = pair._1.getPosition();
                    Vector2D p2 = pair._2.getPosition();
                    Vector2D center = p1.plus( p2 ).times( 0.5 );

                    //Compute and apply an attractive coulomb force from the water molecules to the centroids
                    //The scale has to be strong enough to overcome other forces and dissolve the salt, but if it is too high then the water system will be too volatile
                    final Vector2D modelPosition = box2DAdapter.getModelPosition();
                    double scale = 1;
                    Vector2D coulombForce = getCoulombForce( center, modelPosition, scale, -scale ).times( COULOMB_FORCE_SCALE_FACTOR );
                    box2DAdapter.applyModelForce( coulombForce, modelPosition );
                }
            }
        }

        //Factor out center of mass motion so no large scale drifts can occur
        subtractOutCenterOfMomentum();

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

        for ( Box2DAdapter box2DAdapter : box2DAdapters ) {
            box2DAdapter.worldStepped();
        }

        //Notify listeners that the model changed
        for ( VoidFunction0 frameListener : frameListeners ) {
            frameListener.apply();
        }

        //No water can be drained from the water module
        return 0;
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

    //Get all pairs of sucrose molecules so that the water can make sure they dissolve and move far enough away
    private ItemList<Pair<Sucrose, Sucrose>> getSucrosePairs() {
        return new ItemList<Pair<Sucrose, Sucrose>>() {{
            for ( Sucrose a : sucroseList ) {
                for ( Sucrose b : sucroseList ) {
                    if ( a != b ) {
                        add( new Pair<Sucrose, Sucrose>( a, b ) );
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
    private Vector2D getCoulombForce( SphericalParticle source, SphericalParticle target ) {
        return getCoulombForce( source.getPosition(), target.getPosition(), source.getCharge(), target.getCharge() );
    }

    //Get the coulomb force between two points with the specified charges
    private Vector2D getCoulombForce( Vector2D sourcePosition, Vector2D targetPosition, double q1, double q2 ) {
        if ( sourcePosition.equals( targetPosition ) ) {
            return ZERO;
        }
        double distance = sourcePosition.distance( targetPosition );
        if ( distance < MIN_COULOMB_DISTANCE ) {
            distance = MIN_COULOMB_DISTANCE;
        }
        double scale = k * q1 * q2 / Math.pow( distance, pow.get() ) / distance * coulombStrengthMultiplier.get();
        return new Vector2D( ( targetPosition.getX() - sourcePosition.getX() ) * scale, ( targetPosition.getY() - sourcePosition.getY() ) * scale );
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

            //Move the particle away from the edge a little bit to prevent exact collisions, may not help that much but left here
            final double delta = boundary.width / 100;
            if ( particle.getPosition().getX() > boundary.getMaxX() ) {
                adapter.setModelPosition( boundary.x + delta, y );
            }
            else if ( particle.getPosition().getX() < boundary.x ) {
                adapter.setModelPosition( boundary.getMaxX() - delta, y );
            }
            else if ( particle.getPosition().getY() > boundary.getMaxY() ) {
                adapter.setModelPosition( x, boundary.y + delta );
            }
            else if ( particle.getPosition().getY() < boundary.y ) {
                adapter.setModelPosition( x, boundary.getMaxY() - delta );
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
        showSugarPartialCharge.reset();
        clockRunning.reset();
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
        return (float) ( random.nextFloat() * particleWindow.width - particleWindow.width / 2 );
    }

    //Gets a random number within the vertical range of the beaker
    public double getRandomY() {
        return (float) ( random.nextFloat() * particleWindow.height );
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
            IdentityHashMap<WaterMolecule, Integer> counts = new IdentityHashMap<WaterMolecule, Integer>();

            //Iterate over all water atoms
            for ( WaterMolecule waterMolecule : waterList ) {

                //Make sure we aren't checking for the collisions with a water molecule itself
                if ( waterMolecule != compound ) {

                    //Check the hydrogen atoms and oxygen atoms in the water
                    for ( SphericalParticle waterAtom : waterMolecule ) {

                        //Iterate over all sucrose atoms
                        for ( SphericalParticle atom : compound ) {

                            //add if they are overlapping
                            if ( waterAtom.getPosition().distance( atom.getPosition() ) < waterAtom.radius + atom.radius ) {
                                counts.put( waterMolecule, counts.get( waterMolecule ) == null ? 1 : counts.get( waterMolecule ) + 1 );
                            }
                        }
                    }
                }
            }

            //Only remove water molecules that intersected several sucrose atoms, so that the density of water remains about the same
            for ( WaterMolecule waterMolecule : counts.keySet() ) {
                if ( counts.get( waterMolecule ) >= overlaps.get() ) {
                    add( waterMolecule );
                }
            }

            //Diagnostic for determining how many waters would have been removed with our previous algorithm of intersecting only one particle
            if ( debugWaterRemoval ) {
                HashSet<WaterMolecule> oneSet = new HashSet<WaterMolecule>();
                for ( WaterMolecule waterMolecule : counts.keySet() ) {
                    if ( counts.get( waterMolecule ) >= 1 ) {
                        oneSet.add( waterMolecule );
                    }
                }

                System.out.println( "waterList.size = " + waterList.size() + ", oneSet.size() = " + oneSet.size() + ", useSet.size = " + size() );
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

    //Add the specified ion crystal to the model, no need to remove overlapping water molecules in this case since the salt ions are small enough
    public void addSaltIon( SaltIon ion ) {

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
        Vector2D force = model.getCoulombForce( ZERO, new Vector2D( 1, 0 ), 1, 1 );
        System.out.println( "force = " + force );
        System.out.println( "5E-36 / 10 * 2 = " + 5E-36 / 10 * 2 );
    }
}