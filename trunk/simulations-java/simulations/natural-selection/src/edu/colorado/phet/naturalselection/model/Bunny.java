package edu.colorado.phet.naturalselection.model;

import java.util.ArrayList;
import java.util.Iterator;

import edu.colorado.phet.naturalselection.NaturalSelectionConstants;

public class Bunny {

    private Bunny father;
    private Bunny mother;

    private boolean alive;

    private ArrayList children;

    private ArrayList listeners;

    private int age; // +1 each generation

    public Bunny( Bunny _father, Bunny _mother ) {

        father = _father;
        mother = _mother;

        alive = true;
        children = new ArrayList();
        listeners = new ArrayList();

        setAge( 0 );

    }

    public boolean isAlive() {
        return alive;
    }

    public void die() {
        if( isAlive() ) {
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
        if( isAlive() && age >= NaturalSelectionConstants.BUNNIES_DIE_WHEN_THEY_ARE_THIS_OLD ) {
            die();
        }
    }

    public void ageMe() {
        if( !isAlive() ) {
            return;
        }

        setAge( getAge() + 1 );

        if( isAlive() ) {
            notifyAging();
        }
    }



    public static Bunny[] mateBunnies( Bunny father, Bunny mother ) {
        father.notifyReproduces();
        mother.notifyReproduces();
        return new Bunny[] { new Bunny( father, mother ), new Bunny( father, mother ), new Bunny( father, mother ) ,new Bunny( father, mother ) };
    }



    // notifications

    private void notifyDeath() {
        Iterator iter = listeners.iterator();
        while( iter.hasNext() ) {
            ( ( BunnyListener ) iter.next() ).onBunnyDeath( this );
        }
    }

    private void notifyReproduces() {
        Iterator iter = listeners.iterator();
        while( iter.hasNext() ) {
            ( ( BunnyListener ) iter.next() ).onBunnyReproduces( this );
        }
    }

    private void notifyAging() {
        Iterator iter = listeners.iterator();
        while( iter.hasNext() ) {
            ( ( BunnyListener ) iter.next() ).onBunnyAging( this );
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
        public void onBunnyDeath( Bunny bunny );
        public void onBunnyReproduces( Bunny bunny );
        public void onBunnyAging( Bunny bunny );
    }

}
