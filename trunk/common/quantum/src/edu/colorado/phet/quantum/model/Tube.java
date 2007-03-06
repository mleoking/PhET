/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.quantum.model;

import edu.colorado.phet.collision.Box2D;
import edu.colorado.phet.common.util.EventChannel;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.EventListener;
import java.util.EventObject;

public class Tube extends Box2D {

    private Point2D origin;
    private double width;
    private double height;
    private Rectangle2D bounds;

    public Tube( Point2D origin, double width, double height ) {
        super( origin, new Point2D.Double( origin.getX() + width, origin.getY() + height ) );
        this.origin = origin;
        this.width = width;
        this.height = height;
        determineBounds();

        // Set the position of the cavity
        setPosition( origin );
    }

    public Rectangle2D getBounds() {
        determineBounds();
        return bounds;
    }

    private void determineBounds() {
        bounds = new Rectangle2D.Double( getMinX(), getMinY(), getWidth(), getHeight() );
    }

    /**
     * @param height
     */
    public void setHeight( double height ) {

        // Reposition the walls of the cavity
        double yMiddle = origin.getY() + this.height / 2;
        origin.setLocation( origin.getX(), yMiddle - height / 2 );
        this.height = height;
        super.setBounds( getMinX(), origin.getY() - height / 2, getWidth(), height );
        determineBounds();
        notifyObservers();
        changeListenerProxy.CavityChanged( new ChangeEvent( this ) );
    }

    public void setBounds( double minX, double minY, double maxX, double maxY ) {
        super.setBounds( minX, minY, maxX, maxY );
        changeListenerProxy.CavityChanged( new ChangeEvent( this ) );
    }

    //--------------------------------------------------------------------
    // LeftSystemEvent handling
    //--------------------------------------------------------------------
    private EventChannel changeEventChannel = new EventChannel( ChangeListener.class );
    private ChangeListener changeListenerProxy = (ChangeListener)changeEventChannel.getListenerProxy();

    public void addListener( ChangeListener listener ) {
        changeEventChannel.addListener( listener );
    }

    public void removeListener( ChangeListener listener ) {
        changeEventChannel.removeListener( listener );
    }

    public interface ChangeListener extends EventListener {
        void CavityChanged( Tube.ChangeEvent event );
    }

    public class ChangeEvent extends EventObject {
        public ChangeEvent( Object source ) {
            super( source );
        }

        public Rectangle2D getBounds() {
            return ( (Tube)getSource() ).getBounds();
        }
    }
}
