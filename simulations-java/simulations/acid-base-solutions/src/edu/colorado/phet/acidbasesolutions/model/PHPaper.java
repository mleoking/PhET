/* Copyright 2010, University of Colorado */

package edu.colorado.phet.acidbasesolutions.model;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.EventListener;

import javax.swing.event.EventListenerList;

import edu.umd.cs.piccolo.util.PDimension;

/**
 * Model for pH paper.
 * Origin is at top center.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class PHPaper extends SolutionRepresentation {
    
    private static final double UNDIPPED_PH = 7; // pH of undipped paper
    private static final double BLEED_HEIGHT = 15; // how much the dipped color bleeds above the top of the solution
    
    private final PDimension size;
    private final Beaker beaker;
    private Color dippedColor;
    private double dippedHeight;
    private final EventListenerList listeners;

    public PHPaper( AqueousSolution solution, Point2D location, boolean visible, PDimension size, Beaker beaker ) {
        super( solution, location, visible );
        this.size = new PDimension( size );
        this.beaker = beaker;
        this.listeners = new EventListenerList();
        this.dippedHeight = computeDippedHeight();
        this.dippedColor = createColor( solution.getPH() );
        
        addSolutionRepresentationChangeListener( new SolutionRepresentationChangeAdapter() {
            
            @Override
            public void solutionChanged() {
                setDippedHeight( computeDippedHeight() );
                updateDippedColor();
            }
            
            @Override
            public void concentrationChanged() {
                setDippedHeight( computeDippedHeight() );
                updateDippedColor();
            }
            
            @Override
            public void strengthChanged() {
                setDippedHeight( computeDippedHeight() );
                updateDippedColor();
            }
            
            @Override
            public void locationChanged() {
                setDippedHeight( Math.max( dippedHeight, computeDippedHeight() ) ); // dipped height can only increase
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
     * Constraints x coordinate to be inside the beaker.
     */
    private double constrainX( double requestedX ) {
        double min = beaker.getLocationReference().getX() - ( beaker.getWidth() / 2 ) + ( this.getWidth() / 2 ) + 20;
        double max = beaker.getLocationReference().getX() + ( beaker.getWidth() / 2 ) - ( this.getWidth() / 2 ) - 20;
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
     * Constraints y coordinate to be in or slightly above the solution.
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
    
    private void updateDippedColor() {
        setDippedColor( createColor( getSolution().getPH() ) );
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
        return PHColorFactory.createColor( pH );
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
     * Gets the height of the portion of the paper that displays the dipped color.
     * This is the portion of the paper that is submerged, plus a small "bleed" 
     * above the surface of the solution.
     */
    private double computeDippedHeight() {
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
        if ( h > 0 ) {
            h += BLEED_HEIGHT;
        }
        if ( h > size.getHeight() ) {
            h = size.getHeight();
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
}
