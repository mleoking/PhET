// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.advancedacidbasesolutions.model;

import java.util.ArrayList;
import java.util.Iterator;

import edu.colorado.phet.advancedacidbasesolutions.model.Solute.SoluteListener;
import edu.colorado.phet.advancedacidbasesolutions.model.equilibrium.AbstractEquilibriumModel;
import edu.colorado.phet.advancedacidbasesolutions.model.equilibrium.EquilibriumModelFactory;
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
    private AbstractEquilibriumModel equilibriumModel;
    private final ArrayList<SolutionListener> listeners;
    private final SoluteListener soluteListener;
    
    public AqueousSolution() {
        this( new NoSolute() );
    }
    
    public AqueousSolution( Solute solute ) {
        this.listeners = new ArrayList<SolutionListener>();
        this.soluteListener = new SoluteListener() {
            public void concentrationChanged() {
                notifyConcentrationChanged();
            }

            public void strengthChanged() {
                updateEquilibriumModel();
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
        updateEquilibriumModel();
        notifySoluteChanged();
    }
    
    public Solute getSolute() {
        return solute;
    }
    
    public boolean isPureWater() {
        return solute instanceof NoSolute;
    }
    
    public boolean isAcidic() {
        return solute instanceof Acid;
    }
    
    public boolean isBasic() {
        return solute instanceof Base;
    }
    
    public PHValue getPH() {
        return equilibriumModel.getPH();
    }
    
    public double getReactantConcentration() {
        return equilibriumModel.getReactantConcentration();
    }

    public double getReactantMoleculeCount() {
        return equilibriumModel.getReactantMoleculeCount();
    }
    
    public double getProductConcentration() {
        return equilibriumModel.getProductConcentration();
    }

    public double getProductMoleculeCount() {
        return equilibriumModel.getProductMoleculeCount();
    }

    public double getH3OConcentration() {
        return equilibriumModel.getH3OConcentration();
    }

    public double getH3OMoleculeCount() {
        return equilibriumModel.getH3OMoleculeCount();
    }

    public double getOHConcentration() {
        return equilibriumModel.getOHConcentration();
    }

    public double getOHMoleculeCount() {
        return equilibriumModel.getOHMoleculeCount();
    }
    
    public double getH2OConcentration() {
        return equilibriumModel.getH2OConcentration();
    }

    public double getH2OMoleculeCount() {
        return equilibriumModel.getH2OMoleculeCount();
    }
    
    public double getH3OConcentration( double pH ) {
        return equilibriumModel.getH3OConcentration( pH );
    }
    
    public double getOHConcentration( double pH ) {
        return equilibriumModel.getOHConcentration( pH );
    }
    
    private void updateEquilibriumModel() {
        equilibriumModel = EquilibriumModelFactory.getModel( solute );
    }
    
    public String toString() {
        return HTMLUtils.toHTMLString( solute.getName() + " (" + solute.getSymbol() + ")" );
    }
    
    public interface SolutionListener {
        public void soluteChanged();
        public void concentrationChanged();
        public void strengthChanged();
    }
    
    public static class SolutionAdapter implements SolutionListener {
        public void soluteChanged() {}
        public void concentrationChanged() {}
        public void strengthChanged() {}
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
