package edu.colorado.phet.acidbasesolutions.model;

import java.util.ArrayList;
import java.util.Iterator;

import edu.colorado.phet.acidbasesolutions.ABSConstants;
import edu.colorado.phet.common.phetcommon.view.util.HTMLUtils;

/**
 * Base class for all aqueous solutions.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class AqueousSolution {

    private final Solute solute;
    private final Water water;
    private double initialConcentration;
    private final ArrayList<SolutionListener> listeners;
    
    protected AqueousSolution( Solute solute ) {
        this.solute = solute;
        this.water = new Water();
        this.initialConcentration = ABSConstants.CONCENTRATION_RANGE.getMin();
        this.listeners = new ArrayList<SolutionListener>();
    }
    
    public Solute getSolute() {
        return solute;
    }
    
    public Water getWater() {
        return water;
    }
    
    // c
    public void setInitialConcentration( double initialConcentration ) {
        if ( initialConcentration != this.initialConcentration ) {
            this.initialConcentration = initialConcentration;
            notifyConcentrationChanged();
        }
    }
    
    // c
    public double getInitialConcentration() {
        return initialConcentration;
    }
    
    public void setStrength( double strength ) {
        solute.setStrength( strength );
    }
    
    public double getStrength() {
        return solute.getStrength();
    }
    
    public String toString() {
        return HTMLUtils.toHTMLString( solute.getName() + " (" + solute.getSymbol() + ")" );
    }
    
    public interface SolutionListener {
        public void concentrationChanged();
    }
    
    public void addSolutionListener( SolutionListener listener ) {
        listeners.add( listener );
    }
    
    public void removeSolutionListener( SolutionListener listener ) {
        listeners.remove( listener );
    }
    
    private void notifyConcentrationChanged() {
        Iterator<SolutionListener> i = listeners.iterator();
        while ( i.hasNext() ) {
            i.next().concentrationChanged();
        }
    }
}
