package edu.colorado.phet.acidbasesolutions.model.solutions;

import java.util.ArrayList;
import java.util.Iterator;

import edu.colorado.phet.acidbasesolutions.model.bases.IBase;


public abstract class AbstractBaseSolution extends AbstractAqueousSolution implements IBaseSolution {

    private final IBase _base;
    private double _c; // initial base concentration, named as in model
    private final ArrayList _listeners;
    
    public AbstractBaseSolution( IBase base, double c ) {
        _base = base;
        _c = c;
        _listeners = new ArrayList();
    }
    
    public IBase getBase() {
        return _base;
    }
    
    // c
    public void setInitialBaseConcentration( double c ) {
        if ( c != _c ) {
            _c = c;
            notifyStateChanged();
        }
    }
    
    // c
    public double getInitialBaseConcentration() {
        return _c;
    }
    
    // count of base molecules
    public int getBaseMoleculeCount() {
        return (int) ( getAvogadrosNumber() * getBaseConcentration() );
    }

    // count of conjugate acid molecules
    public int getConjugateAcidMoleculeCount() {
        return (int) ( getAvogadrosNumber() * getConjugateAcidConcentration() );
    }
    
    public interface BaseSolutionListener {
        public void stateChanged();
    }
    
    public void addBaseSolutionListener( BaseSolutionListener listener ) {
        _listeners.add( listener );
    }
    
    public void removeBaseSolutionListener( BaseSolutionListener listener ) {
        _listeners.remove( listener );
    }
    
    protected void notifyStateChanged() {
        Iterator i = _listeners.iterator();
        while ( i.hasNext() ) {
            ( (BaseSolutionListener) i.next() ).stateChanged();
        }
    }
}
