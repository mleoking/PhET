/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.boundstates.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * BSTotalEnergy is the model of total energy.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class BSTotalEnergy extends BSObservable {

    ArrayList _eigenstates;
    
    public BSTotalEnergy() {
        super();
        _eigenstates = new ArrayList();
    }
    
    public BSTotalEnergy( BSEigenstate[] eigenstates ) {
        this();
        setEigenstates( eigenstates );
    }
    
    public void addEigenstate( BSEigenstate eigenstate ) {
        _eigenstates.add( eigenstate );
        sort( _eigenstates );
        notifyObservers();
    }
    
    public void removeEigenstate( BSEigenstate eigenstate ) {
        _eigenstates.remove( eigenstate );
        notifyObservers();
    }
    
    public void clearEigenstates() {
        _eigenstates.clear();
        notifyObservers();
    }
    
    public void setEigenstates( BSEigenstate[] eigenstates ) {
        clearEigenstates();
        for ( int i = 0; i < eigenstates.length; i++ ) {
            addEigenstate( eigenstates[i] );
        }
        sort( _eigenstates );
    }
    
    public BSEigenstate[] getEigenstates() {
        return (BSEigenstate[]) _eigenstates.toArray( new BSEigenstate[ _eigenstates.size() ] );
    }
    
    public int getNumberOfEigenstates() {
        return _eigenstates.size();
    }
    
    private void sort( ArrayList list ) {
        Collections.sort( list );
    }
}
