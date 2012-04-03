// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fluidpressureandflow.pressure.model;

import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.property.CompositeProperty;
import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.ObservableList;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.Function0;

/**
 * Pool with separate chambers where the fluid can flow.  Weights can be added to either side.
 *
 * @author Sam Reid
 */
public class ChamberPool implements IPool {

    //Units in meters, describes the leftmost chamber and is used to create both
    public final double centerAtLeftChamberOpening = -2.9;
    private final double separation = 3.9;//Between centers

    public final double height = 3;

    private final CompositeProperty<Shape> waterShape;
    public final Property<Double> waterVolume = new Property<Double>( 2.0 );

    private final double passageHeight = 0.5;
    private double rightOpeningWidth = 3;

    public final Property<ObservableList<Mass>> masses = new Property<ObservableList<Mass>>( new ObservableList<Mass>() {{
        double massOffset = -4.9;
        add( new Mass( new Rectangle2D.Double( massOffset + 0, 0, passageHeight, passageHeight ), false, 0.0, 1000 ) );
        add( new Mass( new Rectangle2D.Double( massOffset + passageHeight, 0, passageHeight, passageHeight / 2 ), false, 0.0, 500 ) );
        add( new Mass( new Rectangle2D.Double( massOffset + passageHeight * 2, 0, passageHeight, passageHeight / 2 ), false, 0.0, 500 ) );
    }} );
    private final Property<Double> gravity;
    private final Property<Double> fluidDensity;
    public final Property<Double> leftWaterHeightAboveChamber = new Property<Double>( 1.0 );
    private final CompositeProperty<Double> rightWaterHeightAboveChamber = new CompositeProperty<Double>( new Function0<Double>() {
        @Override public Double apply() {
            double leftDisplacement = Math.abs( 1.0 - leftWaterHeightAboveChamber.get() );
            return 1.0 + leftDisplacement / 5.0;
        }
    }, leftWaterHeightAboveChamber );
    private final double CHAMBER_HEIGHT = 1.5;

    public ChamberPool( Property<Double> gravity, Property<Double> fluidDensity ) {
        this.gravity = gravity;
        this.fluidDensity = fluidDensity;

        //just keep the bottom part that is occupied by water
        this.waterShape = new CompositeProperty<Shape>( new Function0<Shape>() {
            @Override public Shape apply() {
                return createWaterShape();
            }
        }, waterVolume, masses, leftWaterHeightAboveChamber, rightWaterHeightAboveChamber );
    }

    private Shape createWaterShape() {
        return new Area( getLeftOpeningWaterShape() ) {{
            add( new Area( leftChamber() ) );
            add( new Area( horizontalPassage() ) );
            add( new Area( rightChamber() ) );
            add( new Area( getRightOpeningWaterShape() ) );
        }};
    }

    @Override public Shape getContainerShape() {
        return new Area( leftOpening() ) {{
            add( new Area( leftChamber() ) );
            add( new Area( horizontalPassage() ) );
            add( new Area( rightChamber() ) );
            add( new Area( rightOpening() ) );
        }};
    }

    private Shape rightOpening() {
        return new Rectangle2D.Double( rightChamber().getBounds2D().getCenterX() - rightOpeningWidth / 2, -height / 2, rightOpeningWidth, height / 2 );
    }

    private Shape leftOpening() {
        return new Rectangle2D.Double( leftChamber().getBounds2D().getCenterX() - passageHeight / 2, -height, passageHeight, height );
    }

    private Shape horizontalPassage() {
        return new Rectangle2D.Double( centerAtLeftChamberOpening, -height + passageHeight, separation, passageHeight );
    }

    private Shape leftChamber() {
        return new Rectangle2D.Double( -4.5, -3, 3, CHAMBER_HEIGHT );
    }

    private Shape rightChamber() {
        return new Rectangle2D.Double( 0, -3, CHAMBER_HEIGHT, CHAMBER_HEIGHT );
    }

    @Override public double getHeight() {
        return getContainerShape().getBounds2D().getHeight();
    }

    @Override public ObservableProperty<Shape> getWaterShape() {
        return waterShape;
    }

    @Override public double getPressure( final double x, final double y, final boolean atmosphere, final double standardAirPressure, final double liquidDensity, final double gravity ) {
        if ( y >= 0 ) {
            return Pool.getPressureAboveGround( y, atmosphere, standardAirPressure, gravity );
        }
        else {
            //Under the ground
            final Shape containerShape = getContainerShape();
            final Shape waterShape = getWaterShape().get();

            //In the ground, return 0.0 (no reading)
            if ( !containerShape.contains( x, y ) ) {
                return 0.0;
            }

            //in the container but not the water
            else if ( containerShape.contains( x, y ) && !waterShape.contains( x, y ) ) {
                return Pool.getPressureAboveGround( y, atmosphere, standardAirPressure, gravity );
            }

            //In the water, but the container may not be completely full
            else {// if ( containerShape.contains( x, y ) && waterShape.contains( x, y ) ) {

                //Y value at the top of the water to compute the air pressure there
                double y0 = getWaterShape().get().getBounds2D().getMaxY();
                double p0 = Pool.getPressureAboveGround( y0, atmosphere, standardAirPressure, gravity );
                double distanceBelowWater = Math.abs( -y + y0 );
                return p0 + liquidDensity * gravity * distanceBelowWater;
            }
        }
    }

    public void stepInTime( final double dt ) {
        int nsteps = 10;
        for ( int i = 0; i < nsteps; i++ ) {
            masses.set( updateMasses( masses.get(), dt / nsteps ) );
        }

        //how far have the masses pushed down the water
        //Find the minimum y of masses that are not being dragged

        Double minY = null;
        for ( Mass mass : masses.get() ) {
            if ( !mass.dragging && mass.getMinY() < 0 ) {
                minY = mass.getMinY();
            }
        }
        if ( minY != null ) {
            double equilibriumY = -height + CHAMBER_HEIGHT + 1.0;
            double leftDisplacement = Math.abs( equilibriumY - minY );
            leftWaterHeightAboveChamber.set( 1.0 - leftDisplacement );

            this.waterShape.notifyIfChanged();
        }

        //Water should equalize after mass removed
        else {
            double leftDisplacement = Math.abs( 1.0 - leftWaterHeightAboveChamber.get() );
            //move back toward zero displacement
            double delta = leftDisplacement;
            leftWaterHeightAboveChamber.set( leftWaterHeightAboveChamber.get() + delta / 10 );
        }
    }

    @Override public void addPressureChangeObserver( final SimpleObserver updatePressure ) {
        waterShape.addObserver( updatePressure );
        masses.addObserver( updatePressure );
        leftWaterHeightAboveChamber.addObserver( updatePressure );
        rightWaterHeightAboveChamber.addObserver( updatePressure );
    }

    @Override public Point2D clampSensorPosition( final Point2D pt ) {
        return pt;
    }

    @Override public boolean isAbbreviatedUnits( final ImmutableVector2D sensorPosition, final double value ) {
        return getWaterShape().get().contains( sensorPosition.getX(), sensorPosition.getY() );
    }

    private ObservableList<Mass> updateMasses( final ObservableList<Mass> masses, final double dt ) {
        ObservableList<Mass> newList = new ObservableList<Mass>();

        final Double g = gravity.get();
        ArrayList<Mass> stacked = getStackedMasses();
        for ( Mass mass : masses ) {
            if ( !stacked.contains( mass ) ) {
                final double m = mass.mass;
                if ( mass.getMinY() > 0.0 && !mass.dragging ) {
                    double force = -m * g;
                    double acceleration = force / m;
                    double newVelocity = mass.velocity + acceleration * dt;
                    double newPosition = mass.getMinY() + newVelocity * dt;
                    newList.add( mass.withMinY( Math.max( newPosition, 0.0 ) ).withVelocity( newVelocity ) );
                }
                else {
                    newList.add( mass );
                }
            }
        }

        //Account for the stacked masses together since their masses should add up.
        if ( stacked.size() > 0 ) {
            double stackedTotalMass = 0.0;
            for ( Mass mass : stacked ) {
                stackedTotalMass += mass.mass;
            }

            Collections.sort( stacked, new Comparator<Mass>() {
                @Override public int compare( final Mass o1, final Mass o2 ) {
                    return Double.compare( o1.getMinY(), o2.getMinY() );
                }
            } );
            Mass mass = stacked.get( 0 );

            //Update the bottom mass, then update the ones on top of it

            final double m = stackedTotalMass;
            //use newton’s laws to equalize pressure/force at interface
            final double h = getRightOpeningWaterShape().getBounds2D().getMaxY() - mass.getMinY();
            final Double rho = fluidDensity.get();
            final double gravityForce = -m * g;
            final double pressureForce = Math.abs( rho * g * h );
//                    System.out.println( "rightWaterHeightAboveChamber = " + rightWaterHeightAboveChamber + ", h = " + h + ", pressure force = " + pressureForce );
            double force = gravityForce + pressureForce;
            double acceleration = force / m;
            final double frictionCoefficient = 0.98;
            double newVelocity = ( mass.velocity + acceleration * dt ) * frictionCoefficient;
            double newPosition = mass.getMinY() + newVelocity * dt;
            newList.add( mass.withMinY( newPosition ).withVelocity( newVelocity ) );

            //Stack the others on this one
            for ( int i = 1; i < stacked.size(); i++ ) {
                newList.add( stacked.get( i ).withMinY( stacked.get( i - 1 ).getMaxY() ) );
            }
        }

        return newList;
    }

    public void reset() {
        waterVolume.reset();
        masses.reset();
        leftWaterHeightAboveChamber.reset();
    }

    public Shape getLeftOpeningWaterShape() {
        double openingY = 0 - height + CHAMBER_HEIGHT;
        return new Rectangle2D.Double( leftChamber().getBounds2D().getCenterX() - passageHeight / 2, openingY, passageHeight, leftWaterHeightAboveChamber.get() );
    }

    public Shape getRightOpeningWaterShape() {
        double openingY = 0 - height + CHAMBER_HEIGHT;
        return new Rectangle2D.Double( rightChamber().getBounds2D().getCenterX() - rightOpeningWidth / 2, openingY, rightOpeningWidth, rightWaterHeightAboveChamber.get() );
    }

    public ArrayList<Mass> getStackedMasses() {
        ArrayList<Mass> m = new ArrayList<Mass>();
        for ( Mass mass : masses.get() ) {
            if ( mass.getMinY() < 0 && !mass.dragging ) {
                m.add( mass );
            }
        }
        return m;
    }

    public boolean showDropRegion() {
        double leftDisplacement = Math.abs( 1.0 - leftWaterHeightAboveChamber.get() );
        return getStackedMasses().size() > 0 || Math.abs( leftDisplacement ) < 1E-2;
    }

    public boolean isOverOpening( final Mass m ) {
        return isOverOpening( m, leftOpening() ) || isOverOpening( m, rightOpening() );
    }

    private boolean isOverOpening( final Mass m, final Shape opening ) {
        final Rectangle2D bounds2D = opening.getBounds2D();
        return new Rectangle2D.Double( bounds2D.getX(), m.shape.getBounds2D().getY(), bounds2D.getWidth(), m.shape.getBounds2D().getHeight() ).intersects( m.shape.getBounds2D() );
    }
}