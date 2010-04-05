/* Copyright 2010, University of Colorado */

package edu.colorado.phet.acidbasesolutions.prototype;

import java.awt.Color;

import edu.colorado.phet.common.phetcommon.math.MathUtil;

/**
 * Model of a weak acid solution at equilibrium.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
class WeakAcid extends Changeable {
    
    private static final double Kw = 1E-14; // water equilibrium constant
    private static final double W = 55.6; // water concentration, mol/L
    private static final double AVOGADROS_NUMBER = 6.022E23;
    
    private double concentration, strength;
    private Color color;
    
    public WeakAcid() {
        this( ProtoConstants.WEAK_ACID_CONCENTRATION_RANGE.getDefault(), ProtoConstants.WEAK_ACID_STRENGTH_RANGE.getDefault(), ProtoConstants.WEAK_ACID_COLOR );
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
    
    public double getPH() {
        return -MathUtil.log10( getH3OConcentration() );
    }
    
    // [HA] = c - [A-]
    public double getReactantConcentration() {
        return getConcentration() - getProductConcentration();
    }
    
    // [A-] = ( -Ka + sqrt( Ka*Ka + 4*Ka*c ) ) / 2 
    public double getProductConcentration() {
        final double Ka = getStrength();
        final double c = getConcentration();
        return ( -Ka + Math.sqrt( ( Ka * Ka ) + ( 4 * Ka * c ) ) ) / 2;
    }
    
    // [H3O+] = [A-]
    public double getH3OConcentration() {
        return getProductConcentration();
    }
    
    // [OH-] = Kw / [H3O+]
    public double getOHConcentration() {
        return Kw / getH3OConcentration();
    }
    
    // [H2O] = W - [A-]
    public double getH2OConcentration() {
        return W - getProductConcentration();
    }
    
    public double getH3OConcentration( double pH ) {
        return Math.pow( 10, -pH );
    }
    
    public double getOHConcentration( double pH ) {
        return Math.pow( 10, -( 14 - pH ) );
    }
    
    public double getReactantMoleculeCount() {
        return getMoleculeCount( getReactantConcentration() ); 
    }
    
    public double getProductMoleculeCount() {
        return getMoleculeCount( getProductConcentration() ); 
    }
    
    public double getH3OMoleculeCount() {
        return getMoleculeCount( getH3OConcentration() ); 
    }
    
    public double getOHMoleculeCount() {
        return getMoleculeCount( getOHConcentration() ); 
    }
    
    public double getH2OMoleculeCount() {
        return getMoleculeCount( getH2OConcentration() ); 
    }
    
    protected static double getMoleculeCount( double concentration ) {
        return concentration * AVOGADROS_NUMBER;
    }
}
