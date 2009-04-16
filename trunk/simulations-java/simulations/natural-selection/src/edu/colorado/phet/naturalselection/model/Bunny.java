package edu.colorado.phet.naturalselection.model;

import java.util.ArrayList;

public class Bunny {

    private Bunny father;
    private Bunny mother;

    private boolean alive;

    private ArrayList children;

    public Bunny( Bunny _father, Bunny _mother ) {

        father = _father;
        mother = _mother;

        alive = true;
        children = new ArrayList();

    }

    public boolean isAlive() {
        return alive;
    }

}
