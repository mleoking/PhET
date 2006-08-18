/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.molecularreactions.model;

import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.common.util.EventChannel;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.EventListener;

/**
 * SimpleMolecule
 * <p>
 * A molecule that is a single sphere.
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class SimpleMolecule extends Molecule implements Selectable {

    private double radius;
    private Rectangle2D boundingBox = new Rectangle2D.Double();

    /**
     * A molecule that has no component molecules, and whose mass is the square of
     * its radius
     *
     * @param radius
     */
    public SimpleMolecule( double radius ) {
        super();
        this.radius = radius;
        setMass( radius * radius );
    }

    public SimpleMolecule( double radius, Point2D location, Vector2D velocity, Vector2D acceleration, double mass, double charge ) {
        super( location, velocity, acceleration, mass, charge );
        this.radius = radius;
    }

    public Molecule[] getComponentMolecules() {
        return new Molecule[]{ this };
    }

    public Rectangle2D getBoundingBox() {
        boundingBox.setRect( getPosition().getX() - radius,
                             getPosition().getY() - radius,
                             radius * 2,
                             radius * 2);
        return boundingBox;
    }

    public double getRadius() {
        return radius;
    }

    public Point2D getCM() {
        return getPosition();
    }

    public double getMomentOfInertia() {
        return radius * radius * getMass() / 2;
    }

    //--------------------------------------------------------------------------------------------------
    // Implementation of Selectable
    //--------------------------------------------------------------------------------------------------
    private Selectable.Selection selectionStatus;

    public void setSelectionStatus( Selection selection ) {
        selectionStatus = selection;
        listenerProxy.selectionStatusChanged( this );
    }

    public Selection getSelectionStatus() {
        return selectionStatus;
    }


    //--------------------------------------------------------------------------------------------------
    // Events
    //--------------------------------------------------------------------------------------------------
    private EventChannel eventChannel = new EventChannel( Listener.class );
    private Listener listenerProxy = (Listener)eventChannel.getListenerProxy();

    public interface Listener extends EventListener {
        void selectionStatusChanged( SimpleMolecule molecule );
    }

    public void addListener( Listener listener ) {
        eventChannel.addListener( listener );
    }

    public void removeListener( Listener listener ) {
        eventChannel.removeListener( listener );
    }
}
