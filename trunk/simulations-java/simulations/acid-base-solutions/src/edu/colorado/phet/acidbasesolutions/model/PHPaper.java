/* Copyright 2010, University of Colorado */

package edu.colorado.phet.acidbasesolutions.model;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.EventListener;

import javax.swing.event.EventListenerList;

import edu.colorado.phet.acidbasesolutions.constants.ABSColors;
import edu.colorado.phet.acidbasesolutions.constants.ABSConstants;
import edu.colorado.phet.common.phetcommon.math.Function;
import edu.colorado.phet.common.phetcommon.math.Function.LinearFunction;
import edu.colorado.phet.common.phetcommon.view.util.ColorUtils;
import edu.colorado.phet.common.phetcommon.view.util.VisibleColor;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * Model for pH paper.
 * Origin is at top center.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class PHPaper extends SolutionRepresentation {
    
    private static final double UNDIPPED_PH = 7; // pH of undipped paper
    
    private final PDimension size;
    private final Beaker beaker;
    private Color dippedColor;
    private double dippedHeight;
    private final EventListenerList listeners;
    private final PHColorStrategy colorStrategy;

    public PHPaper( AqueousSolution solution, Point2D location, boolean visible, PDimension size, Beaker beaker ) {
        super( solution, location, visible );
        this.size = new PDimension( size );
        this.beaker = beaker;
        this.listeners = new EventListenerList();
        this.colorStrategy = new CustomColorStrategy();
        this.dippedHeight = getSubmergedHeight();
        this.dippedColor = createColor( solution.getPH() );
        
        addSolutionRepresentationChangeListener( new SolutionRepresentationChangeAdapter() {
            
            @Override
            public void solutionChanged() {
                setDippedHeight( getSubmergedHeight() ); // Clear any dipped color on the paper above the solution.
                setDippedColor( createColor( getSolution().getPH() ) );
            }
            
            @Override
            public void concentrationChanged() {
                setDippedHeight( getSubmergedHeight() ); // Clear any dipped color on the paper above the solution.
                setDippedColor( createColor( getSolution().getPH() ) );
            }
            
            @Override
            public void strengthChanged() {
                setDippedHeight( getSubmergedHeight() ); // Clear any dipped color on the paper above the solution.
                setDippedColor( createColor( getSolution().getPH() ) );
            }
            
            @Override
            public void locationChanged() {
                setDippedHeight( Math.max( dippedHeight, getSubmergedHeight() ) ); // dipped height can only increase
            }
        });
    }
    
    public PDimension getSizeReference() {
        return size;
    }

    public double getWidth() {
        return size.getWidth();
    }

    public double getHeight() {
        return size.getHeight();
    }
    
    /**
     * Constraint location for dragging.
     */
    @Override
    public void setLocation( double x, double y ) {
        super.setLocation( constrainX( x ), constrainY( y ) );
    }
    
    /*
     * Constrains an x coordinate to be between the walls of the beaker.
     */
    private double constrainX( double requestedX ) {
        double min = beaker.getLocationReference().getX() - ( beaker.getWidth() / 2 ) + ( this.getWidth() / 2 );
        double max = beaker.getLocationReference().getX() + ( beaker.getWidth() / 2 ) - ( this.getWidth() / 2 );
        double x = requestedX;
        if ( x < min ) {
            x = min;
        }
        else if ( x > max ) {
            x = max;
        }
        return x;
    }
    
    /*
     * Constraints a y coordinate to be in or slightly above the solution.
     */
    private double constrainY( double requestedY ) {
        double min = beaker.getLocationReference().getY() - beaker.getHeight() - this.getHeight() - 20;
        double max = beaker.getLocationReference().getY() - this.getHeight() - 20;
        double y = requestedY;
        if ( y < min ) {
            y = min;
        }
        else if ( y > max ) {
            y = max;
        }
        return y;
    }
    
    /**
     * Gets the original color of the paper, before it's dipped in solution.
     * @return
     */
    public Color getPaperColor() {
        return createColor( UNDIPPED_PH );
    }
    
    private void setDippedColor( Color dippedColor ) {
        if ( !dippedColor.equals( this.dippedColor ) ) {
            this.dippedColor = dippedColor;
            fireDippedColorChanged();
        }
    }
    /**
     * Gets the color of the paper when it's dipped in solution.
     * @return
     */
    public Color getDippedColor() {
        return dippedColor;
    }
    
    /**
     * Creates a color that corresponds to a specific pH.
     * @param pH
     * @return
     */
    public Color createColor( double pH ) {
        return colorStrategy.createColor( pH );
    }
    
    private void setDippedHeight( double dippedHeight ) {
        if ( dippedHeight != this.dippedHeight ) {
            this.dippedHeight = dippedHeight;
            fireDippedHeightChanged();
        }
    }
    
    /**
     * Gets the height of the portion of the paper that has been dipped in the solution.
     */
    public double getDippedHeight() {
        return dippedHeight;
    }

    /*
     * Gets the height of the portion of the paper that's submerged in the solution.
     */
    private double getSubmergedHeight() {
        double by = beaker.getY();
        double py = getY();
        double bh = beaker.getHeight();
        double ph = getHeight();
        double h = -Math.abs( by - py ) + bh + ph;
        if ( h < 0 ) {
            h = 0;
        }
        else if ( h > ph ) {
            h = ph;
        }
        return h;
    }

    public interface PHPaperChangeListener extends EventListener {
        public void dippedColorChanged();
        public void dippedHeightChanged();
    }
    
    public void addPHPaperChangeListener( PHPaperChangeListener listener ) {
        listeners.add( PHPaperChangeListener.class, listener );
    }
    
    public void removePHPaperChangeListener( PHPaperChangeListener listener ) {
        listeners.remove( PHPaperChangeListener.class, listener );
    }
    
    private void fireDippedColorChanged() {
        for ( PHPaperChangeListener listener : listeners.getListeners( PHPaperChangeListener.class ) ) {
            listener.dippedColorChanged();
        }
    }
    
    private void fireDippedHeightChanged() {
        for ( PHPaperChangeListener listener : listeners.getListeners( PHPaperChangeListener.class ) ) {
            listener.dippedHeightChanged();
        }
    }
    
    private interface PHColorStrategy {
        public Color createColor( double pH );
    }
    
    /*
     * Maps a pH value to a color in the visible light spectrum.
     * We use visible wavelength as an intermediate representation,
     * then map the wavelength to a color using VisibleColor from phetcommon.
     */
    private static class VisibleSpectrumStrategy implements PHColorStrategy {
        
        private static final LinearFunction MAPPING_FUNCTION = new Function.LinearFunction( ABSConstants.MIN_PH, ABSConstants.MAX_PH, VisibleColor.MAX_WAVELENGTH, VisibleColor.MIN_WAVELENGTH );
        
        public Color createColor( double pH ) {
            double wavelength = MAPPING_FUNCTION.evaluate( pH );
            return new VisibleColor( wavelength ); 
        }
    }
    
    /*
     * Maps a pH value to a color, using a set of custom colors and interpolation.
     * Custom colors are provided for each integer pH value.
     * For an actual pH value, we select the 2 closest colors, then interpolate between them.
     * The interpolating is a linear interpolation of individual RGB components.
     */
    private static class CustomColorStrategy implements PHColorStrategy {
        
        // colors as shown in acid-base-solutions/doc/pH-colors.png
        protected static final Color[] COLORS = { 
            ABSColors.PH_0,
            ABSColors.PH_1,
            ABSColors.PH_2,
            ABSColors.PH_3,
            ABSColors.PH_4,
            ABSColors.PH_5,
            ABSColors.PH_6,
            ABSColors.PH_7,
            ABSColors.PH_8,
            ABSColors.PH_9,
            ABSColors.PH_10,
            ABSColors.PH_11,
            ABSColors.PH_12,
            ABSColors.PH_13,
            ABSColors.PH_14,
        };
        
        public Color createColor( double pH ) {
            assert( COLORS.length == ( ABSConstants.MAX_PH - ABSConstants.MIN_PH + 1 ) );
            Color color = null;
            int lowerPH = (int)pH;
            int upperPH = Math.round( (float)pH );
            if ( lowerPH == upperPH ) {
                color = COLORS[ lowerPH ];
            }
            else {
                double distance = ( pH - lowerPH ) / ( upperPH - lowerPH );
                color = ColorUtils.interpolateRBGA( COLORS[lowerPH], COLORS[upperPH], distance );
            }
            return color;
        }
    }
}
