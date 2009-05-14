package edu.colorado.phet.acidbasesolutions.model;

import java.util.ArrayList;
import java.util.Iterator;

import edu.colorado.phet.acidbasesolutions.model.Solute.SoluteListener;
import edu.colorado.phet.acidbasesolutions.model.concentration.ConcentrationModel;
import edu.colorado.phet.acidbasesolutions.model.concentration.ConcentrationModelFactory;
import edu.colorado.phet.common.phetcommon.view.util.HTMLUtils;

/**
 * In chemistry, a solution is a homogeneous mixture composed of two or more substances.
 * In such a mixture, a solute is dissolved in another substance, known as a solvent.
 * In an aqueous solution, the solvent is water. 
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class AqueousSolution {

    private Solute solute;
    private ConcentrationModel concentrationModel;
    private final ArrayList<SolutionListener> listeners;
    private final SoluteListener soluteListener;
    
    public AqueousSolution( Solute solute ) {
        this.listeners = new ArrayList<SolutionListener>();
        this.soluteListener = new SoluteListener() {
            public void concentrationChanged() {
                notifyConcentrationChanged();
            }

            public void strengthChanged() {
                updateConcentrationModel();
                notifyStrengthChanged();
            }
        };
        setSolute( solute );
    }
    
    public void setSolute( Solute solute ) {
        if ( this.solute != null ) {
            this.solute.removeSoluteListener( soluteListener );
        }
        this.solute = solute;
        this.solute.addSoluteListener( soluteListener );
        updateConcentrationModel();
        notifySoluteChanged();
    }
    
    public Solute getSolute() {
        return solute;
    }
    
    public ConcentrationModel getConcentrationModel() {
        return concentrationModel;
    }
    
    private void updateConcentrationModel() {
        concentrationModel = ConcentrationModelFactory.getModel( solute );
    }
    
    public PHValue getPH() {
        return concentrationModel.getPH();
    }
    
    public String toString() {
        return HTMLUtils.toHTMLString( solute.getName() + " (" + solute.getSymbol() + ")" );
    }
    
    public interface SolutionListener {
        public void soluteChanged();
        public void concentrationChanged();
        public void strengthChanged();
    }
    
    public void addSolutionListener( SolutionListener listener ) {
        listeners.add( listener );
    }
    
    public void removeSolutionListener( SolutionListener listener ) {
        listeners.remove( listener );
    }
    
    private void notifySoluteChanged() {
        Iterator<SolutionListener> i = listeners.iterator();
        while ( i.hasNext() ) {
            i.next().soluteChanged();
        }
    }
    
    private void notifyConcentrationChanged() {
        Iterator<SolutionListener> i = listeners.iterator();
        while ( i.hasNext() ) {
            i.next().concentrationChanged();
        }
    }
    
    private void notifyStrengthChanged() {
        Iterator<SolutionListener> i = listeners.iterator();
        while ( i.hasNext() ) {
            i.next().strengthChanged();
        }
    }
}
