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
            super( CLStrings.DIELECTRIC_AIR, CLConstants.EPSILON_AIR, CLPaints.AIR );
        }
    }
    
    public static class Teflon extends DielectricMaterial {
        public Teflon() {
            super( CLStrings.DIELECTRIC_TEFLON, CLConstants.EPSILON_TEFLON, CLPaints.TEFLON );
        }
    }
    
    public static class Glass extends DielectricMaterial {
        public Glass() {
            super( CLStrings.DIELECTRIC_GLASS, CLConstants.EPSILON_GLASS, CLPaints.GLASS );
        }
    }
    
    public static class Paper extends DielectricMaterial {
        public Paper() {
            super( CLStrings.DIELECTRIC_PAPER, CLConstants.EPSILON_PAPER, CLPaints.PAPER );
        }
    }
    
    /**
     * A custom dielectric material with mutable dielectric constant.
     * Listeners are notified when this material changes.
     */
    public static class CustomDielectricMaterial extends DielectricMaterial {
        
        private final EventListenerList listeners;
        
        public CustomDielectricMaterial( double dielectricConstant ) {
            super( CLStrings.DIELECTRIC_CUSTOM, dielectricConstant, CLPaints.CUSTOM_DIELECTRIC );
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
