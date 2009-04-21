/* Copyright 2009, University of Colorado */

package edu.colorado.phet.naturalselection.model;

import java.util.ArrayList;
import java.util.Iterator;

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
     * Whether the bunny is alive or not. Instances should not disappear after bunnies die, but should stay around
     * so that the family tree can be viewed or analyzed.
     */
    private boolean alive;

    /**
     * A list of the bunny's children. Currently only 0 or 4 children.
     */
    private ArrayList children;

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

    // old genetic data. If the new data is different, listeners will be notified of the changes
    private Allele cachedColorPhenotype;
    private Allele cachedTeethPhenotype;
    private Allele cachedTailPhenotype;

    private ArrayList listeners;

    /**
     * Constructor
     *
     * @param father     The father of the bunny (or null if there is none)
     * @param mother     The mother of the bunny (or null if there is none)
     * @param generation The generation the bunny is being born into
     */
    public Bunny( Bunny father, Bunny mother, int generation ) {

        bunnyId = bunnyCount++;

        this.father = father;
        this.mother = mother;
        this.generation = generation;

        alive = true;
        children = new ArrayList();
        listeners = new ArrayList();

        setAge( 0 );

        if ( this.father == null || this.mother == null ) {
            // one parent is null, so this bunny must be one of the "root" generation 0 bunnies.
            // in general, just make this a vanilla bunny
            colorGenotype = new Genotype( ColorGene.getInstance(), ColorGene.WHITE_ALLELE, ColorGene.WHITE_ALLELE );
            teethGenotype = new Genotype( TeethGene.getInstance(), TeethGene.TEETH_REGULAR_ALLELE, TeethGene.TEETH_REGULAR_ALLELE );
            tailGenotype = new Genotype( TailGene.getInstance(), TailGene.TAIL_SHORT_ALLELE, TailGene.TAIL_SHORT_ALLELE );
        }
        else {
            // we have both parents. do the usual genetic process (including possible mutations)
            colorGenotype = combineGenotypes( this.father.getColorGenotype(), this.mother.getColorGenotype() );
            teethGenotype = combineGenotypes( this.father.getTeethGenotype(), this.mother.getTeethGenotype() );
            tailGenotype = combineGenotypes( this.father.getTailGenotype(), this.mother.getTailGenotype() );
        }

        // set the "cached" phenotypes so that if they change we can notify listeners
        cachedColorPhenotype = colorGenotype.getPhenotype();
        cachedTeethPhenotype = teethGenotype.getPhenotype();
        cachedTailPhenotype = tailGenotype.getPhenotype();

        // let each Gene keep track of this bunny for trait distribution purposes
        addListener( ColorGene.getInstance() );
        addListener( TeethGene.getInstance() );
        addListener( TailGene.getInstance() );

        // bunny is set up, notify various things that the bunny has been created and is ready to use
        notifyInit();

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
            die();
        }
    }

    public int getGeneration() {
        return generation;
    }

    public ArrayList getChildren() {
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

        if ( getAge() >= NaturalSelectionConstants.BUNNIES_STERILE_WHEN_THIS_OLD || potentialMate.getAge() >= NaturalSelectionConstants.BUNNIES_STERILE_WHEN_THIS_OLD ) {
            // too old
            return false;
        }

        return true;
    }

    public int getId() {
        return bunnyId;
    }

    /**
     * Helper function that signals that this bunny has reproduced, and adds children at the same time.
     *
     * @param bunnyArray
     */
    public void reproduce( Bunny[] bunnyArray ) {
        for ( int i = 0; i < bunnyArray.length; i++ ) {
            children.add( bunnyArray[i] );
        }
        mated = true;
        notifyReproduces();
    }

    /**
     * When a trait is changed to be dominant, the bunny's phenotype (whether the trait appears) can change.
     * Call this when trait dominance is changed so that if the bunny needs to change, it can.
     */
    public void checkPhenotypes() {
        if ( cachedColorPhenotype != getColorGenotype().getPhenotype() ) {
            cachedColorPhenotype = getColorGenotype().getPhenotype();
            notifyChangeColor();
        }

        // TODO: add for other traits
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

        return new Genotype( fatherGenotype.getGene(), fatherGenotype.getRandomAllele(), motherGenotype.getRandomAllele() );
    }

    /**
     * Causes two bunnies to have children, with the correct genetics.
     *
     * @param father The father
     * @param mother The mother
     * @return An array of the children
     */
    public static Bunny[] mateBunnies( Bunny father, Bunny mother ) {
        System.out.println( "Mating " + father + " and " + mother );

        // create the bunnies
        Bunny a = new Bunny( father, mother, father.getGeneration() + 1 );
        Bunny b = new Bunny( father, mother, father.getGeneration() + 1 );
        Bunny c = new Bunny( father, mother, father.getGeneration() + 1 );
        Bunny d = new Bunny( father, mother, father.getGeneration() + 1 );

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

    private void notifyInit() {
        System.out.println( "Bunny Born: " + this );
        Iterator iter = listeners.iterator();
        while ( iter.hasNext() ) {
            ( (BunnyListener) iter.next() ).onBunnyInit( this );
        }
    }

    private void notifyDeath() {
        System.out.println( "Bunny Died: " + this );
        Iterator iter = listeners.iterator();
        while ( iter.hasNext() ) {
            ( (BunnyListener) iter.next() ).onBunnyDeath( this );
        }
    }

    private void notifyReproduces() {
        System.out.println( "Bunny Reproduced: " + this );
        Iterator iter = listeners.iterator();
        while ( iter.hasNext() ) {
            ( (BunnyListener) iter.next() ).onBunnyReproduces( this );
        }
    }

    private void notifyAging() {
        Iterator iter = listeners.iterator();
        while ( iter.hasNext() ) {
            ( (BunnyListener) iter.next() ).onBunnyAging( this );
        }
    }

    private void notifyChangeColor() {
        Iterator iter = listeners.iterator();
        while ( iter.hasNext() ) {
            ( (BunnyListener) iter.next() ).onBunnyChangeColor( getColorGenotype().getPhenotype() );
        }
    }

    //----------------------------------------------------------------------------
    // Listeners
    //----------------------------------------------------------------------------

    public void addListener( BunnyListener listener ) {
        listeners.add( listener );
    }

    public void removeListener( BunnyListener listener ) {
        listeners.remove( listener );
    }

    /**
     * Interface for objects that want to get events from a bunny
     */
    public interface BunnyListener {
        /**
         * Called when a bunny initialized
         *
         * @param bunny The initialized bunny
         */
        public void onBunnyInit( Bunny bunny );

        /**
         * Called when the bunny dies
         *
         * @param bunny The bunny
         */
        public void onBunnyDeath( Bunny bunny );

        /**
         * Called when the bunny reproduces
         *
         * @param bunny The bunny
         */
        public void onBunnyReproduces( Bunny bunny );

        /**
         * Called when the bunny ages
         *
         * @param bunny The bunny
         */
        public void onBunnyAging( Bunny bunny );

        /**
         * Called when the bunny changes color
         *
         * @param allele The color (Either ColorGene.WHITE_ALLELE or ColorGene.BROWN_ALLELE)
         */
        public void onBunnyChangeColor( Allele allele );
    }


    /**
     * Show the genetics of the bunny
     *
     * @return A string containing: the bunny ID, in brackets the color, teeth and tail genotypes, and in parenthesis the
     *         phenotypes in the same order
     */
    public String toString() {
        String ret = "#" + String.valueOf( bunnyId ) + "[ " + colorGenotype + " " + teethGenotype + " " + tailGenotype + " (";
        ret += colorGenotype.getPhenotype().toString();
        ret += teethGenotype.getPhenotype().toString();
        ret += tailGenotype.getPhenotype().toString();
        ret += ")]";

        return ret;
    }

}
