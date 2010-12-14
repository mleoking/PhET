package edu.colorado.phet.gravityandorbits.model;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.common.phetcommon.util.Function2;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.gravityandorbits.view.BodyRenderer;
import edu.colorado.phet.gravityandorbits.view.Scale;

/**
 * @author Sam Reid
 */
public class Body {
    private final Property<ImmutableVector2D> positionProperty;//physical position
    private final Property<ImmutableVector2D> scaledPositionProperty;//position accounting for scale (i.e. cartoon or real)
    private final Property<ImmutableVector2D> velocityProperty;
    private final Property<ImmutableVector2D> accelerationProperty;
    private final Property<ImmutableVector2D> forceProperty;
    private final PublicProperty<Double> massProperty;
    private final Property<Double> diameterProperty;
    private final String name;
    private final Color color;
    private final Color highlight;
    private final double density;
    private boolean userControlled;

    private final ArrayList<PathListener> pathListeners = new ArrayList<PathListener>();
    private final ArrayList<PathPoint> path = new ArrayList<PathPoint>();
    private final Property<Scale> scaleProperty;
    private final boolean massSettable;
    private final double cartoonDiameterScaleFactor;
    private final Body parent;
    private final double cartoonOffsetScale;
    private final Function2<Body, Double, BodyRenderer> renderer;
    private final double labelAngle;
    private final int maxPathLength;
    private final double cartoonForceScale;
    private boolean massReadoutBelow;
    private Property<Boolean> collidedProperty = new Property<Boolean>( false );

    public Body( Body parent,//the parent body that this body is in orbit around, used in cartoon mode to exaggerate locations
                 String name, double x, double y, double diameter, double vx, double vy, double mass, Color color, Color highlight,
                 double cartoonDiameterScaleFactor, double cartoonOffsetScale,
                 Function2<Body, Double, BodyRenderer> renderer,// way to associate the graphical representation directly instead of later with conditional logic or map
                 final Property<Scale> scaleProperty, double labelAngle, boolean massSettable,
                 int maxPathLength,
                 double cartoonForceScale, boolean massReadoutBelow ) {
        this.scaleProperty = scaleProperty;//Multiplied with mode scale to arrive at total scale for forces for this body, provides body-specific force scaling that is independent of cartoon/real modes
        this.massSettable = massSettable;
        this.maxPathLength = maxPathLength;
        this.cartoonForceScale = cartoonForceScale;
        this.massReadoutBelow = massReadoutBelow;
        assert renderer != null;
        this.parent = parent;
        this.name = name;
        this.color = color;
        this.highlight = highlight;
        this.cartoonDiameterScaleFactor = cartoonDiameterScaleFactor;
        this.cartoonOffsetScale = cartoonOffsetScale;
        this.renderer = renderer;
        this.labelAngle = labelAngle;
        positionProperty = new Property<ImmutableVector2D>( new ImmutableVector2D( x, y ) );
        velocityProperty = new Property<ImmutableVector2D>( new ImmutableVector2D( vx, vy ) );
        accelerationProperty = new Property<ImmutableVector2D>( new ImmutableVector2D( 0, 0 ) );
        forceProperty = new Property<ImmutableVector2D>( new ImmutableVector2D( 0, 0 ) );
        massProperty = new PublicProperty<Double>( mass );
        diameterProperty = new Property<Double>( diameter );
        density = mass / getVolume();
        scaledPositionProperty = new Property<ImmutableVector2D>( getPosition() );

        //Synchronize the scaled position, which accounts for the scale
        final SimpleObserver updateScaledPosition = new SimpleObserver() {
            public void update() {
                scaledPositionProperty.setValue( getPosition( scaleProperty.getValue() ) );
            }
        };
        scaleProperty.addObserver( updateScaledPosition );
        positionProperty.addObserver( updateScaledPosition );
        if ( parent != null ) {
            parent.positionProperty.addObserver( updateScaledPosition );
        }
    }

    public Property<ImmutableVector2D> getScaledPositionProperty() {
        return scaledPositionProperty;
    }

    private double getVolume() {
        return 4.0 / 3.0 * Math.PI * Math.pow( getRadius(), 3 );
    }

    public double getRadius() {
        return getDiameter() / 2;
    }

    public Color getColor() {
        return color;
    }

    public Color getHighlight() {
        return highlight;
    }

    public Property<ImmutableVector2D> getPositionProperty() {
        return positionProperty;
    }

    public ImmutableVector2D getPosition() {
        return positionProperty.getValue();
    }

    public Property<Double> getDiameterProperty() {
        return diameterProperty;
    }

    public double getDiameter() {
        return diameterProperty.getValue();
    }

    public void translate( Point2D delta ) {
        translate( delta.getX(), delta.getY() );
        addPathPoint();
    }

    public void translate( double dx, double dy ) {
        positionProperty.setValue( new ImmutableVector2D( getX() + dx, getY() + dy ) );
    }

    public double getY() {
        return positionProperty.getValue().getY();
    }

    public double getX() {
        return positionProperty.getValue().getX();
    }

    public String getName() {
        return name;
    }

    public void setDiameter( double value ) {
        diameterProperty.setValue( value );
    }

    public BodyState toBodyState() {
        return new BodyState( getPosition(), getVelocity(), getAcceleration(), getMass() );
    }

    public double getMass() {
        return massProperty.getValue();
    }

    public ImmutableVector2D getAcceleration() {
        return accelerationProperty.getValue();
    }

    public ImmutableVector2D getVelocity() {
        return velocityProperty.getValue();
    }

    public void updateBodyStateFromModel( BodyState bodyState ) {
        if ( !isUserControlled() ) {
            positionProperty.setValue( bodyState.position );
            velocityProperty.setValue( bodyState.velocity );
        }
        accelerationProperty.setValue( bodyState.acceleration );
        forceProperty.setValue( bodyState.acceleration.getScaledInstance( bodyState.mass ) );
    }

    public void allBodiesUpdated() {
        addPathPoint();
    }

    private void addPathPoint() {
        PathPoint pathPoint = new PathPoint( getPosition(), getCartoonPosition() );
        path.add( pathPoint );
        while ( path.size() > maxPathLength ) {//start removing data after 2 orbits of the default system
            path.remove( 0 );
            for ( PathListener listener : pathListeners ) {
                listener.pointRemoved();
            }
        }
        for ( PathListener listener : pathListeners ) {
            listener.pointAdded( pathPoint );
        }
    }

    public void clearPath() {
        path.clear();
        for ( PathListener listener : pathListeners ) {
            listener.cleared();
        }
    }

    public Property<ImmutableVector2D> getForceProperty() {
        return forceProperty;
    }

    public void setMass( double mass ) {
        massProperty.setValue( mass );
        double radius = Math.pow( 3 * mass / 4 / Math.PI / density, 1.0 / 3.0 );
        diameterProperty.setValue( radius * 2 );
    }

    public void resetAll() {
        positionProperty.reset();
        velocityProperty.reset();
        accelerationProperty.reset();
        forceProperty.reset();
        massProperty.reset();
        diameterProperty.reset();
        collidedProperty.reset();
        clearPath();
    }

    public Property<ImmutableVector2D> getVelocityProperty() {
        return velocityProperty;
    }

    public PublicProperty<Double> getMassProperty() {
        return massProperty;
    }

    public boolean isUserControlled() {
        return userControlled;
    }

    public void setUserControlled( boolean b ) {
        this.userControlled = b;
    }

    public void addPathListener( PathListener listener ) {
        pathListeners.add( listener );
    }

    public void removePathListener( PathListener listener ) {
        pathListeners.remove( listener );
    }

    public void setVelocity( ImmutableVector2D velocity ) {
        velocityProperty.setValue( velocity );
    }

    public void setPosition( double x, double y ) {
        positionProperty.setValue( new ImmutableVector2D( x, y ) );
    }

    public double getDensity() {
        return density;
    }

    public void setAcceleration( ImmutableVector2D acceleration ) {
        this.accelerationProperty.setValue( acceleration );
    }

    public void setForce( ImmutableVector2D force ) {
        this.forceProperty.setValue( force );
    }

    public ArrayList<PathPoint> getPath() {
        return path;
    }

    public boolean isMassSettable() {
        return massSettable;
    }

    public BodyRenderer createRenderer( double viewDiameter ) {
        return renderer.apply( this, viewDiameter );
    }

    public double getCartoonDiameterScaleFactor() {
        return cartoonDiameterScaleFactor;
    }

    public Body getParent() {
        return parent;
    }

    public double getCartoonOffsetScale() {
        return cartoonOffsetScale;
    }

    public ImmutableVector2D getCartoonPosition() {
        if ( getParent() != null ) {
            ImmutableVector2D offset = getPosition().getSubtractedInstance( getParent().getPosition() );
            final ImmutableVector2D cartoonOffset = offset.getScaledInstance( getCartoonOffsetScale() );
            final ImmutableVector2D cartoonPosition = getParent().getPosition().getAddedInstance( cartoonOffset );
            return cartoonPosition;
        }
        else {
            return getPosition();//those without parents have a cartoon position equal to their physical position
        }
    }

    public ImmutableVector2D getPosition( Scale scale ) {
        if ( scale == Scale.CARTOON && parent != null ) {
            return getCartoonPosition();
        }
        else {
            return getPosition();
        }
    }

    public double getDiameter( Scale scale ) {
        if ( scale == Scale.CARTOON ) {
            return getCartoonDiameterScaleFactor() * getDiameter();
        }
        else {
            return getDiameter();
        }
    }

    public double getLabelAngle() {
        return labelAngle;
    }

    public boolean isDraggable() {
        return true;
    }

    public int getMaxPathLength() {
        return maxPathLength;
    }

    public Property<Scale> getScaleProperty() {
        return scaleProperty;
    }

    public boolean isMassReadoutBelow() {
        return massReadoutBelow;
    }

    public Property<Boolean> getCollidedProperty() {
        return collidedProperty;
    }

    public boolean collidesWidth( Body body ) {
        final ImmutableVector2D myPosition = getPosition( scaleProperty.getValue() );
        final ImmutableVector2D yourPosition = body.getPosition( body.scaleProperty.getValue() );
        double distance = myPosition.getSubtractedInstance( yourPosition ).getMagnitude();
        double radiiSum = getDiameter( scaleProperty.getValue() ) / 2 + body.getDiameter( body.scaleProperty.getValue() ) / 2;
        return distance < radiiSum;
    }

    public void setCollided( boolean b ) {
        collidedProperty.setValue( b );
    }

    public static class PathPoint {
        public final ImmutableVector2D point;
        public final ImmutableVector2D cartoonPoint;

        public PathPoint( ImmutableVector2D point, ImmutableVector2D cartoonPoint ) {
            this.point = point;
            this.cartoonPoint = cartoonPoint;
        }
    }

    public static interface PathListener {
        public void pointAdded( PathPoint point );

        public void pointRemoved();

        public void cleared();
    }

    @Override
    public String toString() {
        return "name = " + getName() + ", mass = " + getMass();
    }

    public double getCartoonForceScale() {
        return cartoonForceScale;
    }
}
