/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.solublesalts.model;

import edu.colorado.phet.mechanics.Body;

import java.awt.geom.Point2D;
import java.util.ArrayList;

/**
 * Molecule
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class Molecule extends Body implements Binder {

    private Point2D cm = new Point2D.Double();
    private ArrayList atoms = new ArrayList();


    public Molecule() {
    }

    public Molecule( ArrayList atoms ) {
        this.atoms = atoms;
        updateCm();
    }

    public void addAtom( Atom atom ) {
        atoms.add( atom );
        atom.bindTo( this );
//        atom.setIsBound( true );
        updateCm();
    }

    public Point2D getCM() {
        return cm;
    }

    public double getMomentOfInertia() {
        throw new RuntimeException( "not implemented " );
    }

    private void updateCm() {
        cm.setLocation( 0, 0 );
        for( int i = 0; i < atoms.size(); i++ ) {
            Atom atom = (Atom)atoms.get( i );
            cm.setLocation( cm.getX() + atom.getPosition().getX(),
                            cm.getY() + atom.getPosition().getY() );
        }
        cm.setLocation( cm.getX() / atoms.size(),
                        cm.getY() / atoms.size() );
    }

    protected ArrayList getAtoms() {
        return atoms;
    }
}
