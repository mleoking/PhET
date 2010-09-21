/* Copyright 2010, University of Colorado */

package edu.colorado.phet.capacitorlab.model;

import java.awt.Color;
import java.util.EventListener;

import javax.swing.event.EventListenerList;

import edu.colorado.phet.capacitorlab.CLConstants;
import edu.colorado.phet.capacitorlab.CLPaints;
import edu.colorado.phet.capacitorlab.CLStrings;

/**
 * Base class and subclasses for dielectric materials.
 * All subclasses for "real" materials are immutable.
 * The subclass for a "custom" material has a mutable dielectric constant.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class DielectricMaterial {
    
    private final String name;
    private double dielectricConstant;
    private final Color color;
    
    public DielectricMaterial( String name, double dielectricConstant, Color color ) {
        this.name = name;
        this.dielectricConstant = dielectricConstant;
        this.color = color;
    }
    
    public String getName() {
        return name;
    }

    public double getDielectricConstant() {
        return dielectricConstant;
    }
    
    public Color getColor() {
        return color;
    }
    
    public static class Air extends DielectricMaterial {
        public Air() {
            super( CLStrings.MATERIAL_AIR, CLConstants.EPSILON_AIR, CLPaints.INVISIBLE );
        }
    }
    
    public static class Teflon extends DielectricMaterial {
        public Teflon() {
            super( CLStrings.MATERIAL_TEFLON, 2.1, Color.GREEN );
        }
    }
    
    public static class Polystyrene extends DielectricMaterial {
        public Polystyrene() {
            super( CLStrings.MATERIAL_POLYSTYRENE, 2.5, new Color( 189, 132, 141 ) /* pink */ );
        }
    }
    
    public static class Paper extends DielectricMaterial {
        public Paper() {
            super( CLStrings.MATERIAL_PAPER, 3.5, new Color( 226, 255, 155 ) /* light yellow */ );
        }
    }
    
    /**
     * A custom dielectric material with mutable dielectric constant.
     * Listeners are notified when this material changes.
     */
    public static class CustomDielectricMaterial extends DielectricMaterial {
        
        private final EventListenerList listeners;
        
        public CustomDielectricMaterial() {
            this( CLConstants.DIELECTRIC_CONSTANT_RANGE.getDefault() );
        }
      
        public CustomDielectricMaterial( double dielectricConstant ) {
            super( CLStrings.MATERIAL_CUSTOM, dielectricConstant, new Color( 255, 161, 23 ) /* orange */ );
            listeners = new EventListenerList();
        }
        
        
        /**
         * Dielectric constant is mutable for our custom material.
         * @param dielectricConstant
         */
        public void setDielectricConstant( double dielectricConstant ) {
            if ( dielectricConstant != super.dielectricConstant ) {
                super.dielectricConstant = dielectricConstant;
                fireDielectricConstantChanged();
            }
        }
        
        /**
         * Show only the custom material's name, not its mutable constant.
         */
        @Override
        public String toString() {
            return getName();
        }
        
        public interface CustomDielectricChangeListener extends EventListener {
            public void dielectricConstantChanged();
        }
        
        public void addCustomDielectricChangeListener( CustomDielectricChangeListener listener ) {
            listeners.add( CustomDielectricChangeListener.class, listener );
        }
        
        public void removeCustomDielectricChangeListener( CustomDielectricChangeListener listener ) {
            listeners.remove( CustomDielectricChangeListener.class, listener );
        }
        
        private void fireDielectricConstantChanged() {
            for ( CustomDielectricChangeListener listener : listeners.getListeners( CustomDielectricChangeListener.class ) ) {
                listener.dielectricConstantChanged();
            }
        }
    }
}
