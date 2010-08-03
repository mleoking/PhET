/* Copyright 2010, University of Colorado */

package edu.colorado.phet.acidbasesolutions.model;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.EventListener;

import javax.swing.event.EventListenerList;

import edu.colorado.phet.common.phetcommon.math.Function;
import edu.colorado.phet.common.phetcommon.math.Function.LinearFunction;
import edu.colorado.phet.common.phetcommon.view.util.VisibleColor;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * Model for pH paper.
 * Origin is at top center.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class PHPaper extends SolutionRepresentation {
    
    private static final Color PAPER_COLOR = Color.WHITE;
    
    private final PDimension size;
    private final Beaker beaker;
    private double dippedHeight=0;
    private double phValue;//current ph value from dipping in the solution
    private final EventListenerList listeners;

    public PHPaper( AqueousSolution solution, Point2D location, boolean visible, PDimension size, Beaker beaker ) {
        super( solution, location, visible );
        this.size = new PDimension( size );
        this.beaker = beaker;
        this.listeners = new EventListenerList();
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
    
    @Override
    public void setLocation( double x, double y ) {
        super.setLocation( constrainX( x ), constrainY( y ) );
        this.dippedHeight= Math.max(dippedHeight,getSubmergedHeight());
    }

    @Override
    public void setSolution(AqueousSolution solution) {
        this.dippedHeight=getSubmergedHeight();//Clear any residue above the solution
        super.setSolution(solution);
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
        return PAPER_COLOR;
    }
    
    /**
     * Gets the color of the paper when it's dipped in solution.
     * @return
     */
    public Color getDippedColor() {
        return createColor( phValue );
    }
    
    private Color createColor( double pH ) {
        LinearFunction f = new Function.LinearFunction( 0, 14, VisibleColor.MIN_WAVELENGTH, VisibleColor.MAX_WAVELENGTH );
        double wavelength = f.evaluate( pH );
        return new VisibleColor( wavelength ); 
    }
    
    public double getDippedHeight() {
        return dippedHeight;
    }

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

    public void clockTicked(double simulationTimeChange) {
        if (phValue != getSolution().getPH()) {
            double deltaPH = (getSolution().getPH() - phValue) > 0 ? +1 : -1; //unit step towards target
            if (Math.abs(phValue - getSolution().getPH()) < deltaPH) { //close enough, go directly to the target value
                phValue = getSolution().getPH();
            } else {
                phValue = phValue + simulationTimeChange * deltaPH * 0.1;
            }
            fireDippedColorChanged();
        }
    }
    
    public interface PHPaperChangeListener extends EventListener {
        public void dippedColorChanged();
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
}
