package edu.colorado.phet.acidbasesolutions.model;

import java.awt.Color;
import java.awt.Image;
import java.util.ArrayList;
import java.util.Iterator;

import edu.colorado.phet.acidbasesolutions.ABSConstants;


public abstract class Solute extends Molecule {
    
    public interface ICustomSolute {
        public void setStrength( double strength );
    }
    
    private String conjugateSymbol;
    private Image conjugateIcon;
    private Color conjugateColor;
    private double strength;
    private double concentration; // initial concentration!
    private final ArrayList<SoluteListener> listeners;
    
    protected Solute( String name, String symbol, Image icon, Color color, String conjugateSymbol, Image conjugateIcon, Color conjugateColor, double strength ) {
        super( name, symbol, icon, color );
        this.conjugateSymbol = conjugateSymbol;
        this.conjugateIcon = conjugateIcon;
        this.conjugateColor = conjugateColor;
        if ( !isValidStrength( strength ) ) {
            throw new IllegalArgumentException( "strength is invalid: " + strength );
        }
        this.strength = strength;
        this.concentration = ABSConstants.CONCENTRATION_RANGE.getMin();
        listeners = new ArrayList<SoluteListener>();
    }
    
    protected abstract boolean isValidStrength( double strength );
    
    public abstract boolean isZeroNegligible();
    
    public boolean isStrong() {
        return ABSConstants.STRONG_STRENGTH_RANGE.contains( getStrength() );
    }
    
    public boolean isWeak() {
        return ABSConstants.WEAK_STRENGTH_RANGE.contains( getStrength() );
    }
    
    public boolean isIntermediate() {
        return ABSConstants.INTERMEDIATE_STRENGTH_RANGE.containsExclusive( getStrength() );
    }
    
    protected void setConjugateSymbol( String conjugateSymbol ) {
        this.conjugateSymbol = conjugateSymbol;
    }
    
    public String getConjugateSymbol() {
        return conjugateSymbol;
    }
    
    protected void setConjugateIcon( Image conjugateIcon ) {
        this.conjugateIcon = conjugateIcon;
    }
    
    public Image getConjugateIcon() {
        return conjugateIcon;
    }
    
    protected void setConjugateColor( Color conjugateColor ) {
        this.conjugateColor = conjugateColor;
    }
    
    public Color getConjugateColor() {
        return conjugateColor;
    }
    
    protected void setStrength( double strength ) {
        if ( !isValidStrength( strength ) ) {
            throw new IllegalArgumentException( "strength is invalid: " + strength );
        }
        if ( strength != this.strength ) {
            this.strength = strength;
            notifyStrengthChanged();
        }
    }
    
    public double getStrength() {
        return strength;
    }
    
    // c
    public void setConcentration( double concentration ) {
        if ( concentration != this.concentration ) {
            this.concentration = concentration;
            notifyConcentrationChanged();
        }
    }
    
    // c
    public double getConcentration() {
        return concentration;
    }
    
    public interface SoluteListener {
        public void strengthChanged();
        public void concentrationChanged();
    }
    
    public void addSoluteListener( SoluteListener listener ) {
        listeners.add( listener );
    }
    
    public void removeSoluteListener( SoluteListener listener ) {
        listeners.remove( listener );
    }
    
    private void notifyStrengthChanged() {
        Iterator<SoluteListener> i = listeners.iterator();
        while ( i.hasNext() ) {
            i.next().strengthChanged();
        }
    }
    
    private void notifyConcentrationChanged() {
        Iterator<SoluteListener> i = listeners.iterator();
        while ( i.hasNext() ) {
            i.next().concentrationChanged();
        }
    }
}
