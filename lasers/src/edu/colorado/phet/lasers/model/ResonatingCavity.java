/**
 * Class: ResonatingCavity
 * Package: edu.colorado.phet.lasers.model
 * Author: Another Guy
 * Date: Mar 26, 2003
 *      $Author$
 *      $Date$
 *      $Name$
 *      $Revision$
 */
package edu.colorado.phet.lasers.model;

import edu.colorado.phet.collision.Box2D;
import edu.colorado.phet.common.util.EventRegistry;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.EventListener;
import java.util.EventObject;

public class ResonatingCavity extends Box2D {
    //public class ResonatingCavity extends Body {

    private EventRegistry eventRegistry = new EventRegistry();
    private Point2D origin;
    private double width;
    private double height;
    private Rectangle2D bounds;

    public ResonatingCavity( Point2D origin, double width, double height ) {
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

        eventRegistry.fireEvent( new ChangeEvent( this ) );
    }


    public void addCavityChangeListener( ChangeListener listener ) {
        eventRegistry.addListener( listener );
    }

    public void removeListener( ChangeListener listener ) {
        eventRegistry.removeListener( listener );
    }


    ////////////////////////////////////////////////////////////////////////////////////////
    // Inner classes
    //
    public interface ChangeListener extends EventListener {
        void CavityChanged( ResonatingCavity.ChangeEvent event );
    }

    public class ChangeEvent extends EventObject {
        public ChangeEvent( Object source ) {
            super( source );
        }

        public Rectangle2D getBounds() {
            return ( (ResonatingCavity)getSource() ).getBounds();
        }
    }
}
