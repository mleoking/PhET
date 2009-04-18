package edu.colorado.phet.naturalselection.model;

import java.util.ArrayList;
import java.util.Iterator;

import edu.colorado.phet.naturalselection.NaturalSelectionConstants;

public class Bunny {

    private Bunny father;
    private Bunny mother;

    private Genotype colorGenotype;
    private Genotype teethGenotype;
    private Genotype tailGenotype;

    private Bunny potentialMate;
    private boolean mated = false;

    private boolean alive;

    private ArrayList children;

    private ArrayList listeners;

    private int age; // +1 each generation

    public static int bunnyCount = 0;

    public int bunnyId;

    public Bunny( Bunny _father, Bunny _mother ) {

        bunnyId = bunnyCount++;

        father = _father;
        mother = _mother;

        alive = true;
        children = new ArrayList();
        listeners = new ArrayList();

        setAge( 0 );

        if ( father == null || mother == null ) {
            colorGenotype = new Genotype( ColorGene.getInstance(), ColorGene.WHITE_ALLELE, ColorGene.WHITE_ALLELE );
            teethGenotype = new Genotype( TeethGene.getInstance(), TeethGene.TEETH_REGULAR_ALLELE, TeethGene.TEETH_REGULAR_ALLELE );
            tailGenotype = new Genotype( TailGene.getInstance(), TailGene.TAIL_SHORT_ALLELE, TailGene.TAIL_SHORT_ALLELE );
        }
        else {
            colorGenotype = combineGenotypes( father.getColorGenotype(), mother.getColorGenotype() );
            teethGenotype = combineGenotypes( father.getTeethGenotype(), mother.getTeethGenotype() );
            tailGenotype = combineGenotypes( father.getTailGenotype(), mother.getTailGenotype() );
        }

        addListener( ColorGene.getInstance() );
        addListener( TeethGene.getInstance() );
        addListener( TailGene.getInstance() );

        notifyInit();

    }

    public boolean isAlive() {
        return alive;
    }

    public void die() {
        if ( isAlive() ) {
            return;
        }

        alive = false;

        notifyDeath();
    }

    public int getAge() {
        return age;
    }

    public void setAge( int _age ) {
        age = _age;
        if ( isAlive() && age >= NaturalSelectionConstants.BUNNIES_DIE_WHEN_THEY_ARE_THIS_OLD ) {
            die();
        }
    }

    public void ageMe() {
        if ( !isAlive() ) {
            return;
        }

        setAge( getAge() + 1 );

        if ( isAlive() ) {
            notifyAging();
        }
    }

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

    public boolean canMate() {
        if ( potentialMate == null ) {
            System.out.println( "WARNING: potentialMate == null on " + this );
            return false;
        }

        if ( !isAlive() || !potentialMate.isAlive() ) {
            // just no
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


    // static methods

    public static Genotype combineGenotypes( Genotype fatherGenotype, Genotype motherGenotype ) {

        if ( fatherGenotype.getGene() != motherGenotype.getGene() ) {
            throw new RuntimeException( "Genotype mismatch" );
        }

        return new Genotype( fatherGenotype.getGene(), fatherGenotype.getRandomAllele(), motherGenotype.getRandomAllele() );
    }

    public static Bunny[] mateBunnies( Bunny father, Bunny mother ) {
        System.out.println( "Mating " + father + " and " + mother );
        Bunny a = new Bunny( father, mother );
        Bunny b = new Bunny( father, mother );
        Bunny c = new Bunny( father, mother );
        Bunny d = new Bunny( father, mother );
        a.setPotentialMate( b );
        b.setPotentialMate( a );
        c.setPotentialMate( d );
        d.setPotentialMate( c );
        Bunny[] bunnyArray = new Bunny[]{a, b, c, d};
        father.reproduce( bunnyArray );
        mother.reproduce( bunnyArray );
        return bunnyArray;
    }

    public void reproduce( Bunny[] bunnyArray ) {
        for ( int i = 0; i < bunnyArray.length; i++ ) {
            children.add( bunnyArray[i] );
        }
        mated = true;
        notifyReproduces();
    }


    // notifications

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

    // listeners

    public void addListener( BunnyListener listener ) {
        listeners.add( listener );
    }

    public void removeListener( BunnyListener listener ) {
        listeners.remove( listener );
    }

    public interface BunnyListener {
        public void onBunnyInit( Bunny bunny );

        public void onBunnyDeath( Bunny bunny );

        public void onBunnyReproduces( Bunny bunny );

        public void onBunnyAging( Bunny bunny );
    }


    // toString

    public String toString() {
        String ret = "#" + String.valueOf( bunnyId ) + "[ " + colorGenotype + " " + teethGenotype + " " + tailGenotype + " (";
        ret += colorGenotype.getPhenotype().toString();
        ret += teethGenotype.getPhenotype().toString();
        ret += tailGenotype.getPhenotype().toString();
        ret += ")]";

        return ret;
    }

}
