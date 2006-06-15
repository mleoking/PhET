/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.nuclearphysics.model;

import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.coreadditions.EventChannel;
import edu.colorado.phet.nuclearphysics.controller.ControlledFissionModule;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RectangularShape;
import java.awt.geom.Line2D;
import java.util.EventListener;
import java.util.EventObject;
import java.util.List;
import java.util.Random;

/**
 * ControlRod
 * <p/>
 * A control rod's primary behavior is to absorb neutrons that pass through it.
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class ControlRod implements ModelElement {

    private static Random random = new Random();

    private Rectangle2D.Double rep = new Rectangle2D.Double();
    private int orientation;
    private double thickness;
    private double absoprtionProbability;
    private NuclearPhysicsModel model;

    /**
     *
     * @param p1 The top point of the mid-line of the control rod
     * @param p2 The bottom point of the mid-line of the control rod
     * @param thickness
     * @param model
     * @param absoprtionProbability
     */
    public ControlRod( Point2D p1, Point2D p2, double thickness, NuclearPhysicsModel model, double absoprtionProbability ) {

        this.model = model;
        this.thickness = thickness;
        this.absoprtionProbability = absoprtionProbability;
        // Is the rod horizontal?
        if( p1.getY() == p2.getY() ) {
            orientation = ControlledFissionModule.HORIZONTAL;
            rep.setRect( p1.getX(), p1.getY() - thickness / 2, p2.getX() - p1.getX(), thickness );
        }
        // Is the rod vertical?
        if( p1.getX() == p2.getX() ) {
            orientation = ControlledFissionModule.VERTICAL;
            rep.setRect( p1.getX() - thickness / 2, p1.getY(), thickness, p2.getY() - p1.getY() );
        }
    }

    //----------------------------------------------------------------
    // Getters and setters
    //----------------------------------------------------------------

    public double getThickness() {
        return thickness;
    }

    public Shape getShape() {
        return rep;
    }

    public int getOrientation() {
        return orientation;
    }

    public Rectangle2D getBounds() {
        return getShape().getBounds2D();
    }

    public void translate( double dx, double dy ) {
        rep.setRect( rep.getMinX() + dx, rep.getMinY() + dy, rep.getWidth(), rep.getHeight() );
        changeListenerProxy.changed( new ChangeEvent( this ) );
    }

    //----------------------------------------------------------------
    // Implementation of Body
    //----------------------------------------------------------------
    public Point2D getCM() {
        return null;
    }

    public double getMomentOfInertia() {
        return 0;
    }

    //----------------------------------------------------------------
    // Event and listener definitions
    //----------------------------------------------------------------
    private EventChannel changeEventChannel = new EventChannel( ChangeListener.class );
    private ChangeListener changeListenerProxy = (ChangeListener)changeEventChannel.getListenerProxy();

    public void addChangeListener( ChangeListener listener ) {
        changeEventChannel.addListener( listener );
    }

    public void removeChangeListener( ChangeListener listener ) {
        changeEventChannel.removeListener( listener );
    }

    public double getLength() {
        double result = 0;
        if( orientation == ControlledFissionModule.VERTICAL ) {
            result = getBounds().getHeight();
        }
        if( orientation == ControlledFissionModule.HORIZONTAL ) {
            result = getBounds().getWidth();
        }
        return result;
    }

    public void setAbsorptionProbability( double probability ) {
        this.absoprtionProbability = probability;
    }

    public Point2D getLocation() {
        return new Point2D.Double( rep.getBounds2D().getMinX() + rep.getBounds2D().getWidth() / 2,
                                   rep.getBounds2D().getMinY() );
    }

    public class ChangeEvent extends EventObject {
        public ChangeEvent( Object source ) {
            super( source );
        }

        public ControlRod getControlRod() {
            return (ControlRod)getSource();
        }
    }

    public interface ChangeListener extends EventListener {
        void changed( ChangeEvent event );
    }

    //----------------------------------------------------------------
    // Implementation of ModelElement
    //----------------------------------------------------------------

    /**
     * Looks for neutrons that it should absorb
     *
     * @param v
     */
    public void stepInTime( double v ) {
        List modelElements = model.getNuclearModelElements();
        for( int i = 0; i < modelElements.size(); i++ ) {
            Object obj = modelElements.get( i );
            if( obj instanceof Neutron ) {
                Neutron neutron = (Neutron)obj;
                // Is the neutron in the control rod now?
                if( this.getBounds().contains( neutron.getPosition() ) && random.nextDouble() < absoprtionProbability ) {
                    model.removeModelElement( neutron );
                }
                else if( passedThroughInLastTimeStep( neutron ) && random.nextDouble() < absoprtionProbability ) {
                    model.removeModelElement( neutron );
                }
            }
        }
    }

    /**
     * Tells if a body passed through the rod during the last time step
     *
     * @param body
     * @return
     */
    private boolean passedThroughInLastTimeStep( NuclearParticle body ) {
        return body.getPositionPrev().getX() < this.getBounds().getMaxX()
               && body.getPosition().getX() > this.getBounds().getMinX()
               && isWithinYBounds( body.getPosition() )
               ||
               body.getPositionPrev().getX() > this.getBounds().getMinX()
               && body.getPosition().getX() < this.getBounds().getMaxX()
               && isWithinYBounds( body.getPosition() );
    }

    /**
     * Tells if a point is between the minimum and maximum y coordinates of the rod
     *
     * @param p
     * @return
     */
    private boolean isWithinYBounds( Point2D p ) {
        return p.getY() >= this.getBounds().getMinY() && p.getY() <= this.getBounds().getMaxY();
    }
}
