/* Copyright 2010, University of Colorado */

package edu.colorado.phet.acidbasesolutions.model;

import java.awt.Color;
import java.util.EventListener;

import javax.swing.event.EventListenerList;

import edu.colorado.phet.acidbasesolutions.constants.ABSColors;
import edu.colorado.phet.acidbasesolutions.constants.ABSConstants;
import edu.colorado.phet.acidbasesolutions.model.Molecule.H3OMolecule;
import edu.colorado.phet.acidbasesolutions.model.Molecule.OHMolecule;
import edu.colorado.phet.acidbasesolutions.model.Molecule.WaterMolecule;
import edu.colorado.phet.common.phetcommon.math.MathUtil;

/** 
 * A solution is a homogeneous mixture composed of two or more substances.
 * In such a mixture, a solute is dissolved in another substance, known as a solvent.
 * In an aqueous solution, the solvent is water.  The substance that is produced as 
 * the result of the solute dissolving is called the product.
 * 
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class AqueousSolution {
    
    private static final WaterMolecule WATER_MOLECULE = new WaterMolecule(); 
    private static final H3OMolecule H3O_MOLECULE = new H3OMolecule(); 
    private static final OHMolecule OH_MOLECULE = new OHMolecule(); 
    
    private final Molecule solute; // the substance that is dissolved in a solution
    private final Molecule product; // the substance that is produced as the result of the solute dissolving 
    private double strength; // strength of the solute
    private double concentration; // initial concentration of the solute, at the start of the reaction
    private final EventListenerList listeners;
    
    /**
     * In real solutions, solute strength and initial concentration of the solute are immuatable. 
     * For make-believe "custom" solutions, strength and concentration may be made mutable by 
     * implementing this interface.
     */
    public interface ICustomSolution {
        public void setStrength( double strength );
        public void setConcentration( double concentration );
    }
    
    /**
     * Constructor.
     * 
     * @param solute the substance dissolved in water
     * @param product the substance produced by dissolving the solute
     * @param strength the strength of the solute
     * @param concentration the initial concentration of the solute, at the start of the reaction
     */
    public AqueousSolution( Molecule solute, Molecule product, double strength, double concentration ) {
        this.solute = solute;
        this.product = product;
        this.strength = strength;
        this.concentration = concentration;
        listeners = new EventListenerList();
    }
    
    public Color getColor() {
        return ABSColors.AQUEOUS_SOLUTION;
    }
    
    public WaterMolecule getWaterMolecule() {
        return WATER_MOLECULE;
    }
    
    public H3OMolecule getH3OMolecule() {
        return H3O_MOLECULE;
    }
    
    public OHMolecule getOHMolecule() {
        return OH_MOLECULE;
    }

    public  Molecule getSolute() {
        return solute;
    }
    
    public Molecule getProduct() {
        return product;
    }
    
    /**
     * Sets the solute strength.
     * Strength is immutable for real solutions.
     * This method is provided by use by "custom" solutions whose solute strength is mutable.
     * 
     * @param concentration
     */
    protected void setStrength( double strength ) {
        if ( !isValidStrength( strength ) ) {
            throw new IllegalArgumentException( "invalid strength: " + strength );
        }
        if ( strength != this.strength ) {
            this.strength = strength;
            fireStrengthChanged();
        }
    }
    
    public double getStrength() {
        return strength;
    }
    
    /**
     * For solutions with mutable strength, subclasses must implement this method
     * to enforce range constraints on the value of strength. This method is called 
     * to valid the argument provided to setStrength. 
     * 
     * @param strength
     * @return
     */
    protected abstract boolean isValidStrength( double strength );
    
    /**
     * Sets the initial concentration of the solute, at the start of the reaction.
     * Initial concentration is immutable for real solutions.
     * This method is provided by use by "custom" solutions whose concentration is mutable.
     * 
     * @param concentration
     */
    protected void setConcentration( double concentration ) {
        if ( !isValidConcentration( concentration ) ) {
            throw new IllegalArgumentException( "invalid concentration: " + concentration );
        }
        if ( concentration != this.concentration ) {
            this.concentration = concentration;
            fireConcentrationChanged();
        }
    }
    
    /**
     * Gets the initial concentration of the solute, at the start of the reaction.
     * @param concentration
     */
    public double getConcentration() {
        return concentration;
    }

    /**
     * For solutions with mutable concentration, this method enforces range constrains 
     * on the concentration value. This method is called to valid the argument provided
     * to setConcentration. 
     * 
     * @param concentration
     * @return
     */
    protected boolean isValidConcentration( double concentration ) {
        return ABSConstants.CONCENTRATION_RANGE.contains( concentration );
    }
    
    /**
     * Gets the pH of the solution at equilibrium.
     * @return
     */
    public double getPH() {
        return -MathUtil.log10( getH3OConcentration() );
    }

    /**
     * Gets the solute concentration at equilibrium.
     * @return
     */
    public abstract double getSoluteConcentration();

    /**
     * Gets the product concentration at equilibrium.
     * @return
     */
    public abstract double getProductConcentration();

    /**
     * Gets the H3O+ (hydronium) concentration at equilibrium.
     * @return
     */
    public abstract double getH3OConcentration();

    /**
     * Gets the OH- (hydroxide) concentration at equilibrium.
     * @return
     */
    public abstract double getOHConcentration();

    /**
     * Gets the H2O (water) concentration at equilibrium.
     * @return
     */
    public abstract double getH2OConcentration();

    /**
     * Gets the solute molecule count at equilibrium.
     * @return
     */
    public double getSoluteMoleculeCount() {
        return getMoleculeCount( getSoluteConcentration() );
    }

    /**
     * Gets the product molecule count at equilibrium.
     * @return
     */
    public double getProductMoleculeCount() {
        return getMoleculeCount( getProductConcentration() );
    }

    /**
     * Gets the H3O+ (hydronium) molecule count at equilibrium.
     * @return
     */
    public double getH3OMoleculeCount() {
        return getMoleculeCount( getH3OConcentration() );
    }

    /**
     * Gets the OH- (hydroxide) molecule count at equilibrium.
     * @return
     */
    public double getOHMoleculeCount() {
        return getMoleculeCount( getOHConcentration() );
    }

    /**
     * Gets the H2O (water) molecule count at equilibrium.
     * @return
     */
    public double getH2OMoleculeCount() {
        return getMoleculeCount( getH2OConcentration() );
    }

    /*
     * Gets the molecule count for some specified concentration.
     */
    private static double getMoleculeCount( double concentration ) {
        return concentration * ABSConstants.AVOGADROS_NUMBER;
    }
    
    protected static double getWaterConcentration() {
        return ABSConstants.WATER_CONCENTRATION;
    }
    
    protected static double getWaterEquilibriumConstant() {
        return ABSConstants.WATER_EQUILIBRIUM_CONSTANT;
    }
    
    public interface AqueousSolutionChangeListener extends EventListener {
        
        /**
         * The strength of the solute has changed.
         */
        public void strengthChanged();
        
        /** 
         * The initial concentration of the solute has changed.
         */
        public void concentrationChanged();
    }
    
    public void addAqueousSolutionChangeListener( AqueousSolutionChangeListener listener ) {
        listeners.add( AqueousSolutionChangeListener.class, listener );
    }
    
    public void removeAqueousSolutionChangeListener( AqueousSolutionChangeListener listener ) {
        listeners.remove( AqueousSolutionChangeListener.class, listener );
    }
    
    private void fireStrengthChanged() {
        for ( AqueousSolutionChangeListener listener : listeners.getListeners( AqueousSolutionChangeListener.class ) ) {
            listener.strengthChanged();
        }
    }
    
    private void fireConcentrationChanged() {
        for ( AqueousSolutionChangeListener listener : listeners.getListeners( AqueousSolutionChangeListener.class ) ) {
            listener.concentrationChanged();
        }
    }
}
