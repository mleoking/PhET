// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.intro.model;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.common.phetcommon.util.ObservableList;

/**
 * Model element that represents a burner in the simulation.  The burner can
 * heat and also cool other model elements.
 *
 * @author John Blanco
 */
public class Burner extends ModelElement implements EnergyContainer {

    //-------------------------------------------------------------------------
    // Class Data
    //-------------------------------------------------------------------------

    private static final double WIDTH = 0.075; // In meters.
    private static final double HEIGHT = WIDTH;
    private static final double MAX_ENERGY_GENERATION_RATE = 1; // joules/sec TODO: Needs tweaking.

    //-------------------------------------------------------------------------
    // Instance Data
    //-------------------------------------------------------------------------

    private final Point2D position = new Point2D.Double( 0, 0 );

    // Property that is used to control the amount of heating or cooling that
    // is being done.
    public final Property<Double> heatCoolLevel = new Property<Double>( 0.0 );
    private Property<HorizontalSurface> topSurface;

    private double energy = 0; // TODO: Need to determine initial value.

    //-------------------------------------------------------------------------
    // Constructor(s)
    //-------------------------------------------------------------------------

    /**
     * Constructor.
     *
     * @param position The position in model space where this burner exists.
     *                 By convention for this simulation, the position is
     *                 defined as the bottom center of the model element.
     */
    public Burner( Point2D position ) {
        this.position.setLocation( position );
        topSurface = new Property<HorizontalSurface>( new HorizontalSurface( new DoubleRange( getOutlineRect().getMinX(), getOutlineRect().getMaxX() ), getOutlineRect().getMaxY(), this ) );
    }

    //-------------------------------------------------------------------------
    // Methods
    //-------------------------------------------------------------------------

    /**
     * Get a rectangle that defines the outline of the burner.  In the model,
     * the burner is essentially a 2D rectangle.
     *
     * @return
     */
    public Rectangle2D getOutlineRect() {
        return new Rectangle2D.Double( position.getX() - WIDTH / 2,
                                       position.getY(),
                                       WIDTH,
                                       HEIGHT );
    }

    public void updateInternallyProducedEnergy( double dt ) {
        energy += heatCoolLevel.get() * MAX_ENERGY_GENERATION_RATE * dt;
    }

    @Override public Property<HorizontalSurface> getTopSurfaceProperty() {
        return topSurface;
    }

    public void changeEnergy( double deltaEnergy ) {
        energy += deltaEnergy;
    }

    public double getEnergy() {
        return energy;
    }

    public void exchangeEnergyWith( EnergyContainer energyContainer ) {
        double thermalContactLength = getThermalContactArea().getThermalContactLength( energyContainer.getThermalContactArea() );
        if ( thermalContactLength > 0 ) {
            System.out.println( "Non-zero thermalContactLength = " + thermalContactLength );
        }
    }

    public ThermalContactArea getThermalContactArea() {
        // The thermal contact area for the burner a rectangular space that is
        // intended to be just above the top of the burner.  This has to be
        // coordinated a bit with the view.
        Rectangle2D burnerRect = getOutlineRect();
        Rectangle2D thermalContactAreaRect = new Rectangle2D.Double( burnerRect.getCenterX() - burnerRect.getWidth() / 4,
                                                                     burnerRect.getCenterY(),
                                                                     burnerRect.getWidth() / 2,
                                                                     burnerRect.getHeight() / 2 );
        return new ThermalContactArea( thermalContactAreaRect, true );
    }

    public ObservableList<EnergyChunk> getEnergyChunkList() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
