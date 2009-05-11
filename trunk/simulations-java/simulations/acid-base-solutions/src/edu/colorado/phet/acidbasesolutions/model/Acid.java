package edu.colorado.phet.acidbasesolutions.model;

import java.util.ArrayList;
import java.util.Iterator;

import edu.colorado.phet.acidbasesolutions.ABSConstants;
import edu.colorado.phet.acidbasesolutions.ABSStrings;
import edu.colorado.phet.acidbasesolutions.ABSSymbols;


public abstract class Acid {
    
    // specific strong acids
    public static final StrongAcid HYDROCHLORIC_ACID = new StrongAcid( ABSStrings.HYDORCHLORIC_ACID, ABSSymbols.HCl, ABSSymbols.Cl_MINUS, 10E7 );
    
    // specific weak acids
    public static final WeakAcid HYPOCHLORUS_ACID = new WeakAcid( ABSStrings.HYPOCHLOROUS_ACID, ABSSymbols.HClO, ABSSymbols.ClO_MINUS, 2.9E-8 );
    
    private final String name;
    private final String symbol;
    private final String conjugateSymbol;
    private double strength;
    private final ArrayList<AcidListener> listeners;
    
    protected Acid( String name, String symbol, String conjugateSymbol, double strength ) {
        this.name = name;
        this.symbol = symbol;
        this.conjugateSymbol = conjugateSymbol;
        this.strength = strength;
        this.listeners = new ArrayList<AcidListener>();
    }
    
    public String getName() {
        return name;
    }
    
    public String getSymbol() {
        return symbol;
    }
    
    public String getConjugateSymbol() {
        return conjugateSymbol;
    }
    
    protected void setStrength( double strength ) {
        if ( strength != this.strength ) {
            this.strength = strength;
            notifyStrengthChanged();
        }
    }
    
    public double getStrength() {
        return strength;
    }
    
    public interface AcidListener {
        public void strengthChanged();
    }
    
    public void addAcidListener( AcidListener listener ) {
        listeners.add( listener );
    }
    
    public void removeAcidListener( AcidListener listener ) {
        listeners.remove( listener );
    }
    
    private void notifyStrengthChanged() {
        Iterator<AcidListener> i = listeners.iterator();
        while ( i.hasNext() ) {
            i.next().strengthChanged();
        }
    }
    
    public static class StrongAcid extends Acid {
        
        protected StrongAcid( String name, String symbol, String conjugateSymbol, double strength ) {
            super( name, symbol, conjugateSymbol, strength );
        }
        
        protected void setStrength( double strength ) {
            if ( !( ABSConstants.STRONG_STRENGTH_RANGE.contains( strength ) ) ) {
                throw new IllegalArgumentException( "strength out of range: " + strength );
            }
            super.setStrength( strength );
        }
    }

    public static class CustomStrongAcid extends StrongAcid {

        private static final double DEFAULT_STRENGTH = ABSConstants.STRONG_STRENGTH_RANGE.getMin();

        public CustomStrongAcid() {
            super( ABSStrings.CUSTOM_STRONG_ACID, ABSSymbols.HA, ABSSymbols.A_MINUS, DEFAULT_STRENGTH );
        }

        public void setStrength( double strength ) {
            super.setStrength( strength );
        }
    }
    
    public static class WeakAcid extends Acid {
        
        protected WeakAcid( String name, String symbol, String conjugateSymbol, double strength ) {
            super( name, symbol, conjugateSymbol, strength );
        }
        
        protected void setStrength( double strength ) {
            if ( !( ABSConstants.WEAK_STRENGTH_RANGE.contains( strength ) ) ) {
                throw new IllegalArgumentException( "strength out of range: " + strength );
            }
            super.setStrength( strength );
        }
    }
    
    public static class CustomWeakAcid extends WeakAcid {
        
        private static final double DEFAULT_STRENGTH = ABSConstants.WEAK_STRENGTH_RANGE.getMin();
        
        public CustomWeakAcid() {
            super( ABSStrings.CUSTOM_WEAK_ACID, ABSSymbols.HA, ABSSymbols.A_MINUS, DEFAULT_STRENGTH );
        }
        
        public void setStrength( double strength ) {
            super.setStrength( strength );
        }
    }
    
    public static class IntermediateAcid extends Acid {
        
        protected IntermediateAcid( String name, String symbol, String conjugateSymbol, double strength ) {
            super( name, symbol, conjugateSymbol, strength );
        }
        
        protected void setStrength( double strength ) {
            if ( !( strength > ABSConstants.WEAK_STRENGTH_RANGE.getMax() && strength < ABSConstants.STRONG_STRENGTH_RANGE.getMin() ) ) {
                throw new IllegalArgumentException( "strength out of range: " + strength );
            }
            super.setStrength( strength );
        }
    }

    public static class CustomIntermediateAcid extends IntermediateAcid {

        private static final double DEFAULT_STRENGTH = ABSConstants.WEAK_STRENGTH_RANGE.getMin();

        public CustomIntermediateAcid() {
            super( ABSStrings.CUSTOM_INTERMEDIATE_ACID, ABSSymbols.HA, ABSSymbols.A_MINUS, DEFAULT_STRENGTH );
        }

        public void setStrength( double strength ) {
            super.setStrength( strength );
        }
    }

}
