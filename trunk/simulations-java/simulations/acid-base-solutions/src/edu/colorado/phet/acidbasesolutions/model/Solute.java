package edu.colorado.phet.acidbasesolutions.model;

import java.awt.Color;
import java.awt.Image;
import java.util.ArrayList;
import java.util.Iterator;

import edu.colorado.phet.acidbasesolutions.ABSConstants;

/**
 * A solute is a substance that is dissolved in a solution.
 * It's conjugate is the thing that is produced as the result of dissolving.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class Solute extends Molecule {
    
    public interface ICustomSolute {
        public void setStrength( double strength );
    }
    
    private String conjugateSymbol;
    private Image conjugateIcon;
    private Image conjugateStructure;
    private Color conjugateColor;
    private double strength;
    private double concentration; // initial concentration!
    private final ArrayList<SoluteListener> listeners;
    
    protected Solute( String name, String symbol, Image icon, Image structure, Color color, 
                      String conjugateSymbol, Image conjugateIcon, Image conjugateStructure, Color conjugateColor, double strength ) {
        super( name, symbol, icon, structure, color );
        
        if ( !isValidStrength( strength ) ) {
            throw new IllegalArgumentException( "strength is invalid: " + strength );
        }
        
        this.conjugateSymbol = conjugateSymbol;
        this.conjugateIcon = conjugateIcon;
        this.conjugateStructure = conjugateStructure;
        this.conjugateColor = conjugateColor;
        this.strength = strength;
        this.concentration = ABSConstants.CONCENTRATION_RANGE.getMin();
        listeners = new ArrayList<SoluteListener>();
    }
    
    protected abstract boolean isValidStrength( double strength );
    
    public boolean isZeroNegligible() {
        return isStrong();
    }
    
    public boolean isReactionBidirectional() {
        return !isStrong();
    }
    
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
    
    protected void setConjugateStructure( Image conjugateStructure ) {
        this.conjugateStructure = conjugateStructure;
    }
    
    public Image getConjugateStructure() {
        return conjugateStructure;
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
    
    public abstract String getStrengthSymbol();
    
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
