package edu.colorado.phet.acidbasesolutions.model.solutions;

import java.util.ArrayList;
import java.util.Iterator;

import edu.colorado.phet.acidbasesolutions.model.acids.IAcid;


public abstract class AbstractAcidSolution extends AbstractAqueousSolution implements IAcidSolution {

    private final IAcid _acid;
    private double _c; // initial acid concentration, named as in model
    private final ArrayList _listeners;
    
    public AbstractAcidSolution( IAcid acid, double c ) {
        _acid = acid;
        _c = c;
        _listeners = new ArrayList();
    }
    
    public IAcid getAcid() {
        return _acid;
    }
    
    // c
    public void setInitialAcidConcentration( double c ) {
        if ( c != _c ) {
            _c = c;
            notifyStateChanged();
        }
    }
    
    // c
    public double getInitialAcidConcentration() {
        return _c;
    }
    
    // count of acid molecules
    public int getAcidMoleculeCount() {
        return (int) ( getAvogadrosNumber() * getAcidConcentration() );
    }

    // count of conjugate base molecules
    public int getConjugateBaseMoleculeCount() {
        return (int) ( getAvogadrosNumber() * getConjugateBaseConcentration() );
    }
    
    public interface AcidSolutionListener {
        public void stateChanged();
    }
    
    public void addAcidSolutionListener( AcidSolutionListener listener ) {
        _listeners.add( listener );
    }
    
    public void removeAcidSolutionListener( AcidSolutionListener listener ) {
        _listeners.remove( listener );
    }
    
    protected void notifyStateChanged() {
        Iterator i = _listeners.iterator();
        while ( i.hasNext() ) {
            ( (AcidSolutionListener) i.next() ).stateChanged();
        }
    }
}
