package edu.colorado.phet.acidbasesolutions.model;

import java.util.ArrayList;
import java.util.Iterator;

import edu.colorado.phet.acidbasesolutions.ABSConstants;
import edu.colorado.phet.acidbasesolutions.ABSStrings;
import edu.colorado.phet.acidbasesolutions.ABSSymbols;


public abstract class Base {
    
    // specific strong bases
    public static final StrongBase SODIUM_HYDROXIDE = new StrongBase( ABSStrings.SODIUM_HYDROXIDE, ABSSymbols.NaOH, ABSSymbols.Na_PLUS, 1.8E-5 );
    
    // specific weak bases
    public static final WeakBase AMMONIA = new WeakBase( ABSStrings.AMMONIA, ABSSymbols.NH3, ABSSymbols.NH4_PLUS, 1.8E-5 );
    public static final WeakBase PYRIDINE = new WeakBase( ABSStrings.PYRIDINE, ABSSymbols.C5H5N, ABSSymbols.C5H5NH_PLUS, 1.7E-9 );

    private final String name;
    private final String symbol;
    private double strength;
    private final ArrayList<BaseListener> listeners;
    
    private Base( String name, String symbol, double strength ) {
        this.name = name;
        this.symbol = symbol;
        if ( !isValidStrength( strength ) ) {
            throw new IllegalArgumentException( "strength is invalid: " + strength );
        }
        this.strength = strength;
        this.listeners = new ArrayList<BaseListener>();
    }
    
    public String getName() {
        return name;
    }
    
    public String getSymbol() {
        return symbol;
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
    
    protected abstract boolean isValidStrength( double strength );
    
    public interface BaseListener {
        public void strengthChanged();
    }
    
    public void addBaseListener( BaseListener listener ) {
        listeners.add( listener );
    }
    
    public void removeBaseListener( BaseListener listener ) {
        listeners.remove( listener );
    }
    
    private void notifyStrengthChanged() {
        Iterator<BaseListener> i = listeners.iterator();
        while ( i.hasNext() ) {
            i.next().strengthChanged();
        }
    }
    
    public static class StrongBase extends Base {
        
        private final String metalSymbol;
        
        private StrongBase( String name, String symbol, String metalSymbol, double strength ) {
            super( name, symbol, strength );
            this.metalSymbol = metalSymbol;
        }
        
        public String getMetalSymbol() {
            return metalSymbol;
        }
        
        protected boolean isValidStrength( double strength ) {
            return ABSConstants.STRONG_STRENGTH_RANGE.contains( strength );
        }
    }

    public static class CustomStrongBase extends StrongBase {

        private static final double DEFAULT_STRENGTH = ABSConstants.STRONG_STRENGTH_RANGE.getMin();

        public CustomStrongBase() {
            super( ABSStrings.CUSTOM_STRONG_BASE, ABSSymbols.MOH, ABSSymbols.M_PLUS, DEFAULT_STRENGTH );
        }

        public void setStrength( double strength ) {
            super.setStrength( strength );
        }
    }
    
    public static class WeakBase extends Base {
        
        private final String conjugateSymbol;
        
        private WeakBase( String name, String symbol, String conjugateSymbol, double strength ) {
            super( name, symbol, strength );
            this.conjugateSymbol = conjugateSymbol;
        }
        
        public String getConjugateSymbol() {
            return conjugateSymbol;
        }
        
        protected boolean isValidStrength( double strength ) {
            return ABSConstants.WEAK_STRENGTH_RANGE.contains( strength );
        }
    }

    public static class CustomWeakBase extends WeakBase {

        private static final double DEFAULT_STRENGTH = ABSConstants.WEAK_STRENGTH_RANGE.getMin();

        public CustomWeakBase() {
            super( ABSStrings.CUSTOM_WEAK_BASE, ABSSymbols.B, ABSSymbols.BH_PLUS, DEFAULT_STRENGTH );
        }

        public void setStrength( double strength ) {
            super.setStrength( strength );
        }
    }
    
    public static class IntermediateBase extends Base {

        private final String conjugateSymbol;

        private IntermediateBase( String name, String symbol, String conjugateSymbol, double strength ) {
            super( name, symbol, strength );
            this.conjugateSymbol = conjugateSymbol;
        }

        public String getConjugateSymbolSymbol() {
            return conjugateSymbol;
        }

        protected boolean isValidStrength( double strength ) {
            // exclusive of intermediate range bounds!
            return ( strength > ABSConstants.INTERMEDIATE_STRENGTH_RANGE.getMin() && strength < ABSConstants.INTERMEDIATE_STRENGTH_RANGE.getMax() );
        }
    }
    
    public static class CustomIntermediateBase extends IntermediateBase {
        
        private static final double DEFAULT_STRENGTH = ABSConstants.WEAK_STRENGTH_RANGE.getMax() + 1;

        public CustomIntermediateBase() {
            super( ABSStrings.CUSTOM_INTERMEDIATE_BASE, ABSSymbols.B, ABSSymbols.BH_PLUS, DEFAULT_STRENGTH );
        }

        public void setStrength( double strength ) {
            super.setStrength( strength );
        }
    }
}
