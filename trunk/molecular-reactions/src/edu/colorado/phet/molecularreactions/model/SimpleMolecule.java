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
 * <p/>
 * A molecule that is a single sphere.
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class SimpleMolecule extends AbstractMolecule implements Selectable {

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

    public SimpleMolecule[] getComponentMolecules() {
        return new SimpleMolecule[]{this};
    }

    public Rectangle2D getBoundingBox() {
        boundingBox.setRect( getPosition().getX() - radius,
                             getPosition().getY() - radius,
                             radius * 2,
                             radius * 2 );
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

    // Mass of molecule, or parent composite
    public double getFullMass() {
        if( isPartOfComposite() ) {
            return getParentComposite().getMass();
        }
        else {
            return getMass();
        }
    }

    // Kinetic energy of molecule or parent composite
    public double getFullKineticEnergy() {
        if( isPartOfComposite() ) {
            return getParentComposite().getKineticEnergy();
        }
        else {
            return getKineticEnergy();
        }
    }

    // CM of molecule or parent composite
    public Point2D getFullCM() {
        if( isPartOfComposite() ) {
            return getParentComposite().getCM();
        }
        else {
            return getCM();
        }
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
    private EventChannel eventChannel = new EventChannel( ChangeListener.class );
    private ChangeListener listenerProxy = (ChangeListener)eventChannel.getListenerProxy();

    public interface ChangeListener extends EventListener {
        void selectionStatusChanged( SimpleMolecule molecule );
    }

    public void addListener( ChangeListener listener ) {
        eventChannel.addListener( listener );
    }

    public void removeListener( ChangeListener listener ) {
        eventChannel.removeListener( listener );
    }
}
