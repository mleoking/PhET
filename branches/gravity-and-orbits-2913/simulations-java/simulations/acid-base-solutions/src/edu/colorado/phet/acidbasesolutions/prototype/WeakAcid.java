// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.acidbasesolutions.prototype;

import java.awt.Color;

import edu.colorado.phet.common.phetcommon.math.MathUtil;

/**
 * Model of a weak acid solution at equilibrium.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
class WeakAcid extends Changeable {
    
    private double concentration, strength;
    private Color color;
    
    public WeakAcid() {
        this( MGPConstants.WEAK_ACID_CONCENTRATION_RANGE.getDefault(), MGPConstants.WEAK_ACID_STRENGTH_RANGE.getDefault(), MGPConstants.COLOR_WEAK_ACID );
    }
    
    public WeakAcid( double concentration, double strength, Color color ) {
        this.concentration = concentration;
        this.strength = strength;
        this.color = color;
    }
    
    public void setConcentration( double concentration ) {
        if ( concentration != this.concentration ) {
            this.concentration = concentration;
            fireStateChanged();
        }
    }
    
    public double getConcentration() {
        return concentration;
    }
    
    public void setStrength( double strength ) {
        if ( strength != this.strength ) {
            this.strength = strength;
            fireStateChanged();
        }
    }
    
    public double getStrength() {
        return strength;
    }
    
    public void setColor( Color color ) {
        if ( !color.equals( this.color ) ) {
            this.color = color;
            fireStateChanged();
        }
    }
    
    public Color getColor() {
        return color;
    }
    
    // pH = -log10( [H3O] )
    public double getPH() {
        return -MathUtil.log10( getConcentrationH3O() );
    }
    
    // [HA] = c - [A-]
    public double getConcentrationHA() {
        return getConcentration() - getConcentrationA();
    }
    
    // [A-] = ( -Ka + sqrt( Ka*Ka + 4*Ka*c ) ) / 2 
    public double getConcentrationA() {
        final double Ka = getStrength();
        final double c = getConcentration();
        return ( -Ka + Math.sqrt( ( Ka * Ka ) + ( 4 * Ka * c ) ) ) / 2;
    }
    
    // [H3O+] = [A-]
    public double getConcentrationH3O() {
        return getConcentrationA();
    }
    
    // [OH-] = Kw / [H3O+]
    public double getConcentrationOH() {
        return MGPConstants.Kw / getConcentrationH3O();
    }
    
    // [H2O] = W - [A-]
    public double getConcentrationH2O() {
        return MGPConstants.W - getConcentrationA();
    }
    
    public double getConcentrationH3O( double pH ) {
        return Math.pow( 10, -pH );
    }
    
    public double getConcentrationOH( double pH ) {
        return Math.pow( 10, -( 14 - pH ) );
    }
    
    public double getMoleculeCountHA() {
        return getMoleculeCount( getConcentrationHA() ); 
    }
    
    public double getMoleculeCountA() {
        return getMoleculeCount( getConcentrationA() ); 
    }
    
    public double getMoleculeCountH3O() {
        return getMoleculeCount( getConcentrationH3O() ); 
    }
    
    public double getMoleculeCountOH() {
        return getMoleculeCount( getConcentrationOH() ); 
    }
    
    public double getMoleculeCountH2O() {
        return getMoleculeCount( getConcentrationH2O() ); 
    }
    
    protected static double getMoleculeCount( double concentration ) {
        return concentration * MGPConstants.AVOGADROS_NUMBER;
    }
}
