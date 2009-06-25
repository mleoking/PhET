/* Copyright 2009, University of Colorado */

package edu.colorado.phet.naturalselection.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import edu.colorado.phet.common.phetcommon.math.Point3D;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.naturalselection.NaturalSelectionConstants;

/**
 * Represents a bunny in the Natural Selection model
 *
 * @author Jonathan Olson
 */
public class Bunny {
    // parents (although currently the gender of bunnies is ignored)
    private Bunny father;
    private Bunny mother;

    // genetic data for each trait
    private Genotype colorGenotype;
    private Genotype teethGenotype;
    private Genotype tailGenotype;

    /**
     * Currently bunnies only mate with their siblings for a simple hierarchical layout. This is the only other
     * possible bunny that this one can mate with. If either dies, the other cannot reproduce
     */
    private Bunny potentialMate;

    /**
     * Whether or not this bunny has reproduced
     */
    private boolean mated = false;

    /**
     * Whether on not the bunny has mutated
     */
    private boolean mutated = false;

    /**
     * Whether the bunny is alive or not. Instances should not disappear after bunnies die, but should stay around
     * so that the family tree can be viewed or analyzed.
     */
    private boolean alive;

    /**
     * A list of the bunny's children. Currently only 0 or 4 children.
     */
    private ArrayList<Bunny> children;

    /**
     * The bunny's age (in years). starts at 0, counts up 1 each generation
     */
    private int age;

    /**
     * The generation of the bunny (will not change)
     */
    private final int generation;

    /**
     * Unique identifier for this bunny, used for ordering and simplifies handling couples (plus good for debugging)
     */
    public int bunnyId;
    public static int bunnyCount = 0;

    // The phenotypes of the bunny. These now do not change once the bunny has been created (and possibly mutated)
    private Allele colorPhenotype;
    private Allele teethPhenotype;
    private Allele tailPhenotype;

    // random number generator
    private static final Random random = new Random( System.currentTimeMillis() );

    // 3d coordinates
    private Point3D position;

    // time since the last hop
    private int sinceHopTime = 0;

    private boolean movingRight;

    public static final int BETWEEN_HOP_TIME = 50;
    public static final int HOP_TIME = 10;
    public static final int HOP_HEIGHT = 50;
    public static final double NORMAL_HOP_DISTANCE = 20.0;

    private static final int HUNGER_THRESHOLD = 250;
    private static final int MAX_HUNGER = 600;
    private int hunger = random.nextInt( MAX_HUNGER );

    private Point3D hopDirection;

    private NaturalSelectionModel model;

    private NaturalSelectionClock.Listener clockListener;

    private ArrayList<Listener> listeners;

    public static final double BUNNY_SIDE_SPACER = 10.0;

    /**
     * Constructor
     *
     * @param model      The natural selection model
     * @param father     The father of the bunny (or null if there is none)
     * @param mother     The mother of the bunny (or null if there is none)
     * @param generation The generation the bunny is being born into
     */
    public Bunny( NaturalSelectionModel model, Bunny father, Bunny mother, int generation ) {
        this.model = model;

        bunnyId = bunnyCount++;

        sinceHopTime = random.nextInt( BETWEEN_HOP_TIME + HOP_TIME );

        movingRight = true;
        if ( random.nextInt( 2 ) == 0 ) {
            movingRight = false;
        }

        this.father = father;
        this.mother = mother;
        this.generation = generation;

        alive = true;
        children = new ArrayList<Bunny>();
        listeners = new ArrayList<Listener>();

        setAge( 0 );

        if ( this.father == null || this.mother == null ) {
            // one parent is null, so this bunny must be one of the "root" generation 0 bunnies.
            // in general, just make this a vanilla bunny
            colorGenotype = new Genotype( ColorGene.getInstance(), ColorGene.WHITE_ALLELE, ColorGene.WHITE_ALLELE );
            teethGenotype = new Genotype( TeethGene.getInstance(), TeethGene.TEETH_SHORT_ALLELE, TeethGene.TEETH_SHORT_ALLELE );
            tailGenotype = new Genotype( TailGene.getInstance(), TailGene.TAIL_SHORT_ALLELE, TailGene.TAIL_SHORT_ALLELE );
        }
        else {
            // we have both parents. do the usual genetic process (including possible mutations)
            colorGenotype = combineGenotypes( this.father.getColorGenotype(), this.mother.getColorGenotype() );
            teethGenotype = combineGenotypes( this.father.getTeethGenotype(), this.mother.getTeethGenotype() );
            tailGenotype = combineGenotypes( this.father.getTailGenotype(), this.mother.getTailGenotype() );
        }

        // set the "cached" phenotypes so that if they change we can notify listeners
        colorPhenotype = colorGenotype.getPhenotype();
        teethPhenotype = teethGenotype.getPhenotype();
        tailPhenotype = tailGenotype.getPhenotype();

        // let each Gene keep track of this bunny for trait distribution purposes
        addListener( ColorGene.getInstance() );
        addListener( TeethGene.getInstance() );
        addListener( TailGene.getInstance() );

        setInitialPosition();

        clockListener = new NaturalSelectionClock.Listener() {
            public void onTick( ClockEvent event ) {
                Bunny.this.onPhysicalTick( event );
            }
        };

        model.getClock().addPhysicalListener( clockListener );

        // bunny is set up, notify various things that the bunny has been created and is ready to use
        //notifyInit();

    }

    public boolean isMovingRight() {
        return movingRight;
    }

    /**
     * Sets the initial bunny position (3d coordinates)
     */
    private void setInitialPosition() {
        position = model.getLandscape().getRandomGroundPosition();
        double mx = model.getLandscape().getMaximumX( position.getZ() );
        position.setLocation( position.getX() * ( mx - BUNNY_SIDE_SPACER ) / mx, position.getY(), position.getZ() );
    }

    public boolean isAlive() {
        return alive;
    }

    /**
     * Called to kill the bunny. Will notify listeners if the bunny goes from alive to dead.
     */
    public void die() {
        if ( !isAlive() ) {
            return;
        }

        alive = false;

        notifyDeath();
    }

    public int getAge() {
        return age;
    }

    /**
     * Sets the bunny's age. If too old, the bunny will die of old age.
     *
     * @param age The age you want the bunny to be!
     */
    public void setAge( int age ) {
        this.age = age;
        if ( isAlive() && this.age >= NaturalSelectionConstants.BUNNIES_DIE_WHEN_THEY_ARE_THIS_OLD ) {
            if ( model.isFriendAdded() && model.getGeneration() == 0 ) {
                // in this case, we don't want the bunny to die! just want them to get older
                return;
            }
            die();
        }
    }

    public int getGeneration() {
        return generation;
    }

    public ArrayList<Bunny> getChildren() {
        return children;
    }

    public boolean hasChildren() {
        return children.size() > 0;
    }

    /**
     * Age this bunny
     */
    public void ageMe() {
        setAge( getAge() + 1 );

        if ( isAlive() ) {
            notifyAging();
        }
    }

    // get genetic information

    public Genotype getColorGenotype() {
        return colorGenotype;
    }

    public Genotype getTeethGenotype() {
        return teethGenotype;
    }

    public Genotype getTailGenotype() {
        return tailGenotype;
    }

    public Allele getColorPhenotype() {
        return colorPhenotype;
    }

    public Allele getTeethPhenotype() {
        return teethPhenotype;
    }

    public Allele getTailPhenotype() {
        return tailPhenotype;
    }

    public Bunny getPotentialMate() {
        return potentialMate;
    }

    public void setPotentialMate( Bunny bunny ) {
        if ( bunny == null ) {
            System.out.println( "WARNING: setting potential mate to null on " + this );
        }
        potentialMate = bunny;
    }

    public boolean hasMated() {
        return mated;
    }

    public Point3D getPosition() {
        return position;
    }

    public boolean isMutated() {
        return mutated;
    }

    /**
     * Determines whether this bunny can reproduce or not.
     *
     * @return Whether or not this bunny can currently mate with its possible partner
     */
    public boolean canMate() {
        // potential mate not set yet, bad things!!!
        if ( potentialMate == null ) {
            System.out.println( "WARNING: potentialMate == null on " + this );
            return false;
        }

        if ( !isAlive() || !potentialMate.isAlive() ) {
            // just no, one or both are dead!
            return false;
        }

        if ( hasMated() || potentialMate.hasMated() ) {
            // for now, if we mated already, we can't again
            return false;
        }

        if ( this != model.getRootFather() && this != model.getRootMother() && model.getGeneration() != 0 ) {
            if ( getAge() >= NaturalSelectionConstants.BUNNIES_STERILE_WHEN_THIS_OLD || potentialMate.getAge() >= NaturalSelectionConstants.BUNNIES_STERILE_WHEN_THIS_OLD ) {
                // too old
                return false;
            }
        }

        return true;
    }

    public int getId() {
        return bunnyId;
    }

    public NaturalSelectionModel getModel() {
        return model;
    }

    /**
     * Helper function that signals that this bunny has reproduced, and adds children at the same time.
     *
     * @param bunnyArray The array of this bunny's new children
     */
    public void reproduce( Bunny[] bunnyArray ) {
        for ( int i = 0; i < bunnyArray.length; i++ ) {
            children.add( bunnyArray[i] );
        }
        mated = true;
        notifyReproduces();
    }

    private Point3D getNewHopDirection() {
        if ( hunger > HUNGER_THRESHOLD && model.getSelectionFactor() == NaturalSelectionModel.SELECTION_FOOD && teethPhenotype == TeethGene.TEETH_LONG_ALLELE ) {

            List<Shrub> shrubs = model.getShrubs();

            double bestDistance = Double.POSITIVE_INFINITY;
            Point3D bestShrubPosition = null;

            Point3D bunnyPosition = getPosition();

            for ( Shrub shrub : shrubs ) {
                Point3D shrubPosition = shrub.getPosition();
                double distance = Point3D.distance( bunnyPosition, shrubPosition );

                if ( distance < bestDistance ) {
                    bestDistance = distance;
                    bestShrubPosition = shrubPosition;
                }
            }

            if ( bestShrubPosition == null ) {
                throw new RuntimeException( "No shrubs?" );
            }

            double diffX = bestShrubPosition.getX() - bunnyPosition.getX();
            double diffZ = bestShrubPosition.getZ() - bunnyPosition.getZ();

            movingRight = diffX >= 0;

            double mag = Math.sqrt( diffX * diffX + diffZ * diffZ );
            if ( mag > NORMAL_HOP_DISTANCE ) {
                diffX *= NORMAL_HOP_DISTANCE / mag;
                diffZ *= NORMAL_HOP_DISTANCE / mag;
            }
            else {
                hunger = 0;
                sinceHopTime = 0;
                if ( teethPhenotype == TeethGene.TEETH_LONG_ALLELE ) {
                    sinceHopTime = -20;
                }
                movingRight = random.nextInt( 2 ) == 0;
            }

            diffX *= 0.7;
            diffZ *= 0.7;

            return new Point3D.Double( diffX, 0, diffZ );
        }
        else {
            //Point3D.Double ret = new Point3D.Double( NORMAL_HOP_DISTANCE, 0, Math.random() * 10 - 5 );
            double angle = Math.random() * Math.PI * 2;
            double a = NORMAL_HOP_DISTANCE * Math.cos( angle );
            double b = NORMAL_HOP_DISTANCE * Math.sin( angle );
            boolean swap = Math.abs( a ) < Math.abs( b );
            Point3D.Double ret = new Point3D.Double( Math.abs( swap ? b : a ), 0, swap ? a : b );

            double mx = getMaxX();
            if ( movingRight && position.getX() + ret.getX() > mx ) {
                movingRight = false;
            }
            if ( !movingRight && position.getX() - ret.getX() < -mx ) {
                movingRight = true;
            }

            if ( !movingRight ) {
                ret.setLocation( -ret.getX(), 0, ret.getZ() );
            }

            return ret;
        }
    }

    /**
     * Causes the bunny to move around physically
     */
    private void moveAround() {
        hunger += random.nextInt( 3 );
        if ( hunger > MAX_HUNGER ) {
            hunger = MAX_HUNGER;
        }

        // TODO: add randomness to inbetween-jumping time?
        sinceHopTime++;
        if ( sinceHopTime > BETWEEN_HOP_TIME + HOP_TIME ) {
            sinceHopTime = 0;
        }
        if ( sinceHopTime == BETWEEN_HOP_TIME ) {
            hopDirection = getNewHopDirection();
        }
        else if ( hopDirection == null ) {
            hopDirection = getNewHopDirection();
        }
        if ( sinceHopTime > BETWEEN_HOP_TIME ) {
            // move in the "hop"
            int hopProgress = sinceHopTime - BETWEEN_HOP_TIME;
            double hopFraction = ( (double) hopProgress ) / ( (double) HOP_TIME );

            double x = position.getX() + hopDirection.getX() / ( (double) HOP_TIME );
            double z = position.getZ() + hopDirection.getZ() / ( (double) HOP_TIME );
            double y = model.getLandscape().getGroundY( x, z ) + HOP_HEIGHT * 2 * ( -hopFraction * hopFraction + hopFraction );

            setPosition( new Point3D.Double( x, y, z ) );

            if ( movingRight ) {
                if ( position.getX() >= getMaxX() ) {
                    movingRight = false;
                    hopDirection.setLocation( -hopDirection.getX(), 0, hopDirection.getZ() );
                    sinceHopTime--;
                }
            }
            else {
                if ( position.getX() <= -getMaxX() ) {
                    movingRight = true;
                    hopDirection.setLocation( -hopDirection.getX(), 0, hopDirection.getZ() );
                    sinceHopTime--;
                }
            }

            if ( position.getZ() >= getMaxZ() || position.getZ() <= getMinZ() ) {
                hopDirection.setLocation( hopDirection.getX(), 0, -hopDirection.getZ() );
                sinceHopTime--;
            }

        }

    }

    private double getMaxX() {
        return model.getLandscape().getMaximumX( position.getZ() ) - BUNNY_SIDE_SPACER;
    }

    private double getMinZ() {
        return Landscape.NEARPLANE;
    }

    private double getMaxZ() {
        return Landscape.FARPLANE;
    }

    private void setPosition( Point3D position ) {
        this.position = position;
        notifyChangePosition();
    }


    /**
     * Cause a bunny to mutate for all mutatable genes
     */
    public void mutateMe() {
        if ( ColorGene.getInstance().getMutatable() ) {
            mutated = true;
            colorPhenotype = ColorGene.BROWN_ALLELE;
            colorGenotype = new Genotype( ColorGene.getInstance(), ColorGene.BROWN_ALLELE, ColorGene.BROWN_ALLELE );
        }
        if ( TailGene.getInstance().getMutatable() ) {
            mutated = true;
            tailPhenotype = TailGene.TAIL_LONG_ALLELE;
            tailGenotype = new Genotype( TailGene.getInstance(), TailGene.TAIL_LONG_ALLELE, TailGene.TAIL_LONG_ALLELE );
        }
        if ( TeethGene.getInstance().getMutatable() ) {
            mutated = true;
            teethPhenotype = TeethGene.TEETH_LONG_ALLELE;
            teethGenotype = new Genotype( TeethGene.getInstance(), TeethGene.TEETH_LONG_ALLELE, TeethGene.TEETH_LONG_ALLELE );
        }
    }

    /**
     * Cause a bunny to mutate with the specified gene and allele
     *
     * @param gene   The gene to mutate
     * @param allele The allele to mutate to
     */
    public void mutateGene( Gene gene, Allele allele ) {
        mutated = true;

        if ( gene == ColorGene.getInstance() ) {
            colorPhenotype = allele;
            colorGenotype = new Genotype( gene, allele, allele );
        }
        else if ( gene == TailGene.getInstance() ) {
            tailPhenotype = allele;
            tailGenotype = new Genotype( gene, allele, allele );
        }
        else if ( gene == TeethGene.getInstance() ) {
            teethPhenotype = allele;
            teethGenotype = new Genotype( gene, allele, allele );
        }


    }

    //----------------------------------------------------------------------------
    // Event handlers
    //----------------------------------------------------------------------------

    private void onPhysicalTick( ClockEvent clockEvent ) {
        if ( isAlive() ) {
            moveAround();
        }
        else {
            // stop listening
            model.getClock().removePhysicalListener( clockListener );
        }
    }

    //----------------------------------------------------------------------------
    // Static methods
    //----------------------------------------------------------------------------

    /**
     * Combines the DNA of each parent to get the DNA for the child. getRandomAllele should introduce mutations if
     * possible
     *
     * @param fatherGenotype The father's genotype
     * @param motherGenotype The mother's genotype
     * @return A new genotype for a child bunny
     */
    public static Genotype combineGenotypes( Genotype fatherGenotype, Genotype motherGenotype ) {

        if ( fatherGenotype.getGene() != motherGenotype.getGene() ) {
            throw new RuntimeException( "Genotype mismatch" );
        }

        return new Genotype( fatherGenotype.getGene(), fatherGenotype.getNextChildAllele(), motherGenotype.getNextChildAllele() );
    }

    /**
     * Causes two bunnies to have children, with the correct genetics.
     *
     * @param father The father
     * @param mother The mother
     * @return An array of the children
     */
    public static Bunny[] mateBunnies( Bunny father, Bunny mother, int generation ) {
        //System.out.println( "Mating " + father + " and " + mother );

        // create the bunnies
        Bunny a = new Bunny( father.getModel(), father, mother, generation );
        Bunny b = new Bunny( father.getModel(), father, mother, generation );
        Bunny c = new Bunny( father.getModel(), father, mother, generation );
        Bunny d = new Bunny( father.getModel(), father, mother, generation );

        // pair them up with their potential mates
        a.setPotentialMate( b );
        b.setPotentialMate( a );
        c.setPotentialMate( d );
        d.setPotentialMate( c );

        Bunny[] bunnyArray = new Bunny[]{a, b, c, d};
        father.reproduce( bunnyArray );
        mother.reproduce( bunnyArray );

        return bunnyArray;
    }


    //----------------------------------------------------------------------------
    // Notifications
    //----------------------------------------------------------------------------

    public void notifyInit() {
        notifyListenersOfEvent( new Event( this, Event.TYPE_INIT ) );
    }

    private void notifyDeath() {
        notifyListenersOfEvent( new Event( this, Event.TYPE_DIED ) );
    }

    private void notifyReproduces() {
        notifyListenersOfEvent( new Event( this, Event.TYPE_REPRODUCED ) );
    }

    private void notifyAging() {
        notifyListenersOfEvent( new Event( this, Event.TYPE_AGE_CHANGED ) );
    }

    private void notifyChangePosition() {
        Event event = new Event( this, Event.TYPE_POSITION_CHANGED );
        event.setPosition( position.getX(), position.getY(), position.getZ() );
        notifyListenersOfEvent( event );
    }

    private void notifyListenersOfEvent( Event event ) {
        Iterator<Listener> iter = listeners.iterator();
        while ( iter.hasNext() ) {
            ( iter.next() ).onEvent( event );
        }
    }

    //----------------------------------------------------------------------------
    // Listeners
    //----------------------------------------------------------------------------

    public void addListener( Listener listener ) {
        listeners.add( listener );
    }

    public void removeListener( Listener listener ) {
        listeners.remove( listener );
    }

    /**
     * Interface for objects that want to get events from a bunny
     */
    public interface Listener {
        public void onEvent( Event event );
    }

    /**
     * Events sent to bunny listeners
     */
    public class Event {
        public static final int TYPE_INIT = 0;
        public static final int TYPE_DIED = 1;
        public static final int TYPE_REPRODUCED = 2;
        public static final int TYPE_AGE_CHANGED = 3;
        public static final int TYPE_POSITION_CHANGED = 4;

        public final int type;
        public final Bunny bunny;
        private boolean targeted;
        private Point3D position;

        public Event( Bunny bunny, int type ) {
            this.bunny = bunny;
            this.type = type;
        }

        //----------------------------------------------------------------------------
        // Getters
        //----------------------------------------------------------------------------

        public int getType() {
            return type;
        }

        public Bunny getBunny() {
            return bunny;
        }

        public boolean isTargeted() {
            return targeted;
        }

        public Point3D getPosition() {
            return position;
        }

        //----------------------------------------------------------------------------
        // Setters
        //----------------------------------------------------------------------------

        private void setTargeted( boolean targeted ) {
            this.targeted = targeted;
        }

        private void setPosition( double x, double y, double z ) {
            position = new Point3D.Double( x, y, z );
        }
    }


    /**
     * Show the genetics of the bunny
     *
     * @return A string containing: the bunny ID, in brackets the color, teeth and tail genotypes, and in parenthesis the
     *         phenotypes in the same order
     */
    public String toString() {
        String ret = "#" + String.valueOf( bunnyId ) + "[ " + colorGenotype + " " + teethGenotype + " " + tailGenotype + " (";
        ret += colorPhenotype.toString();
        ret += teethPhenotype.toString();
        ret += tailPhenotype.toString();
        ret += ")]";

        return ret;
    }

}
