// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fluidpressureandflow.pressure.model;

import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.property.CompositeProperty;
import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.ObservableList;
import edu.colorado.phet.common.phetcommon.util.Pair;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.Function0;
import edu.colorado.phet.fluidpressureandflow.FPAFSimSharing.UserComponents;
import edu.colorado.phet.fluidpressureandflow.FluidPressureAndFlowResources.Images;

/**
 * Pool with separate chambers where the fluid can flow.  Weights can be added to either side.
 *
 * @author Sam Reid
 */
public class ChamberPool implements IPool {

    //Units in meters, describes the leftmost chamber and is used to create both
    public final double centerAtLeftChamberOpening = -2.9;

    //The entire apparatus is this tall
    public final double height = 3;

    private final CompositeProperty<Shape> waterShape;

    //The size of the passage between the chambers
    private final double passageSize = 0.5;

    //Width of the right opening to the air
    private double rightOpeningWidth = 2.5;

    //Width of the left opening to the air
    private double leftOpeningWidth = 0.5;

    //Use the length ratio instead of area ratio because the quadratic factor makes it too hard to see the water move on the right, and decreases the pressure effect too much to see it
    private double lengthRatio = rightOpeningWidth / leftOpeningWidth;

    public final Property<ObservableList<Mass>> masses = new Property<ObservableList<Mass>>( new ObservableList<Mass>() {{
        double massOffset = -4.9;
        double separation = 0.05;
        add( new Mass( UserComponents.mass1, new Rectangle2D.Double( massOffset + 0, 0, passageSize, passageSize ), false, 0.0, 500, Images.MASS ) );
        add( new Mass( UserComponents.mass2, new Rectangle2D.Double( massOffset + passageSize + separation, 0, passageSize, passageSize / 2 ), false, 0.0, 250, Images.MASS ) );
        add( new Mass( UserComponents.mass3, new Rectangle2D.Double( massOffset + passageSize * 2 + separation * 2, 0, passageSize, passageSize / 2 ), false, 0.0, 250, Images.MASS ) );
    }} );
    private final Property<Double> gravity;
    private final Property<Double> fluidDensity;

    //These heights are as measured above the lower chamber
    public final Property<Double> leftWaterHeight = new Property<Double>( 1.0 );
    private final CompositeProperty<Double> rightWaterHeight = new CompositeProperty<Double>( new Function0<Double>() {
        public Double apply() {
            return 1.0 + getLeftDisplacement() / lengthRatio;
        }
    }, leftWaterHeight );

    //Height of each chamber, physics not working properly to vary these independently
    private final double CHAMBER_HEIGHT = 1.25;

    //Keep track of the last value for purposes of determining when at equilibrium, for showing the dotted drag line
    private double lastLeftDisplacement;

    public ChamberPool( Property<Double> gravity, Property<Double> fluidDensity ) {
        this.gravity = gravity;
        this.fluidDensity = fluidDensity;

        //just keep the bottom part that is occupied by water
        this.waterShape = new CompositeProperty<Shape>( new Function0<Shape>() {
            public Shape apply() {
                return createWaterShape();
            }
        }, masses, leftWaterHeight, rightWaterHeight );
    }

    private double getLeftDisplacement() {return Math.abs( 1.0 - leftWaterHeight.get() );}

    private Shape createWaterShape() {
        return new Area( getLeftOpeningWaterShape() ) {{
            add( new Area( leftChamber() ) );
            add( new Area( horizontalPassage() ) );
            add( new Area( rightChamber() ) );
            add( new Area( getRightOpeningWaterShape() ) );
        }};
    }

    public Shape getContainerShape() {
        return new Area( leftOpening() ) {{
            add( new Area( leftChamber() ) );
            add( new Area( horizontalPassage() ) );
            add( new Area( rightChamber() ) );
            add( new Area( rightOpening() ) );
        }};
    }

    private Rectangle2D.Double rightOpening() {
        final double openingY = -height + CHAMBER_HEIGHT;
        final double openingHeight = height - CHAMBER_HEIGHT;
        return new Rectangle2D.Double( rightChamber().getBounds2D().getCenterX() - rightOpeningWidth / 2, openingY, rightOpeningWidth, openingHeight );
    }

    private Rectangle2D.Double leftOpening() {
        final double openingY = -height + CHAMBER_HEIGHT;
        final double openingHeight = height - CHAMBER_HEIGHT;
        return new Rectangle2D.Double( leftChamber().getBounds2D().getCenterX() - leftOpeningWidth / 2, openingY, leftOpeningWidth, openingHeight );
    }

    private Rectangle2D.Double horizontalPassage() {
        return new Rectangle2D.Double( leftChamber().getMaxX(), -height + CHAMBER_HEIGHT / 2 - passageSize / 2, rightChamber().getMinX() - leftChamber().getMaxX(), passageSize );
    }

    private Rectangle2D.Double leftChamber() { return new Rectangle2D.Double( -4.5, -3, 3, CHAMBER_HEIGHT ); }

    private Rectangle2D.Double rightChamber() { return new Rectangle2D.Double( 0, -height, CHAMBER_HEIGHT, CHAMBER_HEIGHT ); }

    public double getHeight() { return height; }

    public ObservableProperty<Shape> getWaterShape() { return waterShape; }

    //Similar to code in TrapezoidPool, maybe some could be factored out
    public double getPressure( final double x, final double y, final boolean atmosphere, final double standardAirPressure, final double liquidDensity, final double gravity ) {
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
        lastLeftDisplacement = getLeftDisplacement();
        int numberSteps = 10;
        for ( int i = 0; i < numberSteps; i++ ) {
            masses.set( updateMasses( masses.get(), dt / numberSteps ) );
        }

        //how far have the masses pushed down the water
        //Find the minimum y of masses that are not being dragged

        Double minY = null;
        for ( Mass mass : masses.get() ) {
            if ( !mass.dragging && mass.getMinY() < 0 ) {
                if ( minY == null ) {
                    minY = mass.getMinY();
                }
                else {
                    minY = Math.min( minY, mass.getMinY() );
                }
            }
        }
        if ( minY != null ) {
            double leftDisplacement = Math.abs( -height + CHAMBER_HEIGHT + 1.0 - minY );
            leftWaterHeight.set( 1.0 - leftDisplacement );

            this.waterShape.notifyIfChanged();
        }

        //Water should equalize after mass removed
        else {
            //move back toward zero displacement.  Note, this does not use correct newtonian dynamics, just a simple heuristic
            double delta = getLeftDisplacement() / 10;
            leftWaterHeight.set( leftWaterHeight.get() + delta );
        }
    }

    public void addPressureChangeObserver( final SimpleObserver updatePressure ) {
        waterShape.addObserver( updatePressure );
        masses.addObserver( updatePressure );
        leftWaterHeight.addObserver( updatePressure );
        rightWaterHeight.addObserver( updatePressure );
    }

    //Sensor can travel anywhere in this scene
    public Point2D clampSensorPosition( final Point2D pt ) { return pt; }

    public boolean isAbbreviatedUnits( final ImmutableVector2D sensorPosition, final double value ) {
        return getWaterShape().get().contains( sensorPosition.getX(), sensorPosition.getY() );
    }

    public ArrayList<Pair<Double, Double>> getGrassSegments() {
        return new ArrayList<Pair<Double, Double>>() {{
            add( new Pair<Double, Double>( leftOpening().getBounds2D().getX() - 100, leftOpening().getBounds2D().getX() ) );
            add( new Pair<Double, Double>( leftOpening().getBounds2D().getMaxX(), rightOpening().getBounds2D().getMinX() ) );
            add( new Pair<Double, Double>( rightOpening().getBounds2D().getMaxX(), rightOpening().getBounds2D().getMaxX() + 100 ) );
        }};
    }

    public ArrayList<ArrayList<ImmutableVector2D>> getEdges() {
        return new ArrayList<ArrayList<ImmutableVector2D>>() {{
            add( new ArrayList<ImmutableVector2D>( Arrays.asList( topLeft( leftOpening() ),
                                                                  bottomLeft( leftOpening() ),
                                                                  topLeft( leftChamber() ),
                                                                  bottomLeft( leftChamber() ),
                                                                  bottomRight( leftChamber() ),
                                                                  bottomLeft( horizontalPassage() ),
                                                                  bottomRight( horizontalPassage() ),
                                                                  bottomLeft( rightChamber() ),
                                                                  bottomRight( rightChamber() ),
                                                                  topRight( rightChamber() ),
                                                                  bottomRight( rightOpening() ),
                                                                  topRight( rightOpening() ) ) ) );
            add( new ArrayList<ImmutableVector2D>( Arrays.asList( topRight( leftOpening() ),
                                                                  bottomRight( leftOpening() ),
                                                                  topRight( leftChamber() ),
                                                                  topLeft( horizontalPassage() ),
                                                                  topRight( horizontalPassage() ),
                                                                  topLeft( rightChamber() ),
                                                                  bottomLeft( rightOpening() ),
                                                                  topLeft( rightOpening() ) ) ) );
        }};
    }

    private ImmutableVector2D topLeft( final Rectangle2D rectangle2D ) { return new ImmutableVector2D( rectangle2D.getMinX(), rectangle2D.getMaxY() ); }

    private ImmutableVector2D bottomLeft( final Rectangle2D rectangle2D ) { return new ImmutableVector2D( rectangle2D.getMinX(), rectangle2D.getMinY() ); }

    private ImmutableVector2D topRight( final Rectangle2D rectangle2D ) { return new ImmutableVector2D( rectangle2D.getMaxX(), rectangle2D.getMaxY() ); }

    private ImmutableVector2D bottomRight( final Rectangle2D rectangle2D ) { return new ImmutableVector2D( rectangle2D.getMaxX(), rectangle2D.getMinY() ); }

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
                public int compare( final Mass o1, final Mass o2 ) {
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
        masses.reset();
        leftWaterHeight.reset();
    }

    public Rectangle2D.Double getLeftOpeningWaterShape() {
        double openingY = 0 - height + CHAMBER_HEIGHT;
        return new Rectangle2D.Double( leftChamber().getBounds2D().getCenterX() - passageSize / 2, openingY, passageSize, leftWaterHeight.get() );
    }

    public Rectangle2D.Double getRightOpeningWaterShape() {
        double openingY = 0 - height + CHAMBER_HEIGHT;
        return new Rectangle2D.Double( rightChamber().getBounds2D().getCenterX() - rightOpeningWidth / 2, openingY, rightOpeningWidth, rightWaterHeight.get() );
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

    public boolean showDropRegion() { return Math.abs( getLeftDisplacement() - lastLeftDisplacement ) < 1E-3; }

    public boolean isOverOpening( final Mass m ) { return isOverOpening( m, leftOpening() ) || isOverOpening( m, rightOpening() ); }

    private boolean isOverOpening( final Mass m, final Shape opening ) {
        final Rectangle2D bounds2D = opening.getBounds2D();
        return new Rectangle2D.Double( bounds2D.getX(), m.shape.getBounds2D().getY(), bounds2D.getWidth(), m.shape.getBounds2D().getHeight() ).intersects( m.shape.getBounds2D() );
    }
}