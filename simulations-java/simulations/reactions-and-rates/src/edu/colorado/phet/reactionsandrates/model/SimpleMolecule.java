/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.reactionsandrates.model;

import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.common.phetcommon.util.EventChannel;

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
public abstract class SimpleMolecule extends AbstractMolecule implements Selectable {

    private static double areaToMassFactor = .2;

    private double radius;
    private Rectangle2D boundingBox = new Rectangle2D.Double();
    private Selectable.Selection selectionStatus;
    private EventChannel eventChannel = new EventChannel( ChangeListener.class );
    private ChangeListener listenerProxy = (ChangeListener)eventChannel.getListenerProxy();

    /**
     * A molecule that has no component molecules, and whose mass is the square of
     * its radius
     *
     * @param radius
     */
    public SimpleMolecule( double radius ) {
        super();
        this.radius = radius;
        setMass( radius * radius * areaToMassFactor );
    }

    public SimpleMolecule( double radius, Point2D location, Vector2D velocity, Vector2D acceleration, double mass, double charge ) {
        super( location, velocity, acceleration, mass, charge );
        this.radius = radius;
    }


    public Object clone() {
        SimpleMolecule clone = (SimpleMolecule)super.clone();

        clone.boundingBox = new Rectangle2D.Double( boundingBox.getX(), boundingBox.getY(), boundingBox.getWidth(), boundingBox.getHeight() );
        clone.eventChannel = new EventChannel( ChangeListener.class );
        clone.listenerProxy = (ChangeListener)eventChannel.getListenerProxy();
        clone.selectionStatus = null;

        return clone;
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
