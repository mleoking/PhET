// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.lwjglphet.math;

// copied somewhat from the Arrow class TODO cleanup
public class Arrow2F {
    private ImmutableVector2F tailLocation;
    private ImmutableVector2F tipLocation;
    private float headHeight;
    private float headWidth;
    private float tailWidth;
    private float fractionalHeadHeight;
    private boolean scaleTailToo;
    private float headScale;
    private ImmutableVector2F direction;
    private ImmutableVector2F norm;
    boolean isHeadDynamic = false;

    private ImmutableVector2F rightFlap;
    private ImmutableVector2F leftFlap;
    private ImmutableVector2F rightPin;
    private ImmutableVector2F leftPin;
    private ImmutableVector2F rightTail;
    private ImmutableVector2F leftTail;

    public Arrow2F( ImmutableVector2F tailLocation, ImmutableVector2F tipLocation, float headHeight, float headWidth, float tailWidth ) {
        init( tailLocation, tipLocation, headHeight, headWidth, tailWidth );
        computeArrow();
    }

    /**
     * @param fractionalHeadHeight When the head size is less than fractionalHeadHeight * arrow length,
     *                             the head will be scaled.
     */
    public Arrow2F( ImmutableVector2F tailLocation, ImmutableVector2F tipLocation,
                    float headHeight, float headWidth, float tailWidth,
                    float fractionalHeadHeight, boolean scaleTailToo ) {
        this.fractionalHeadHeight = fractionalHeadHeight;
        this.scaleTailToo = scaleTailToo;
        this.isHeadDynamic = true;
        init( tailLocation, tipLocation, headHeight, headWidth, tailWidth );
        computeArrow();
    }


    void init( ImmutableVector2F tailLocation, ImmutableVector2F tipLocation, float headHeight, float headWidth, float tailWidth ) {
        this.tailLocation = tailLocation;
        this.tipLocation = tipLocation;
        this.headHeight = headHeight;
        this.headWidth = headWidth;
        this.tailWidth = tailWidth;
    }

    private void computeArrow() {
        if ( tailLocation.getDistance( tipLocation ) != 0 ) {
            ImmutableVector2F distanceVector = tipLocation.getSubtractedInstance( tailLocation );
            direction = distanceVector.getNormalizedInstance();
            float length = tipLocation.getDistance( tailLocation );
            float tempHeadHeight = headHeight;
            float tempHeadWidth = headWidth;
            float tempTailWidth = tailWidth;
            if ( isHeadDynamic ) {
                if ( length < headHeight / fractionalHeadHeight ) {
                    tempHeadHeight = length * fractionalHeadHeight;
                    if ( scaleTailToo ) {
                        tempTailWidth = tailWidth * tempHeadHeight / headHeight;
                        tempHeadWidth = headWidth * tempHeadHeight / headHeight;
                    }
                }
                else {
                    //nothing needs to be done, since the headHeight is already large enough, and previously computed values will be correct.
                }
            }
            else if ( length < headHeight ) {
                throw new RuntimeException( "headHeight is bigger than arrow length: length=" + length + " headHeight=" + headHeight );
            }
            headScale = tempHeadHeight / headHeight;
            norm = direction.getNormalVector();

            rightFlap = getPoint( -1 * tempHeadHeight, -tempHeadWidth / 2 );
            leftFlap = getPoint( -1 * tempHeadHeight, tempHeadWidth / 2 );
            rightPin = getPoint( -1 * tempHeadHeight, -tempTailWidth / 2 );
            leftPin = getPoint( -1 * tempHeadHeight, tempTailWidth / 2 );
            rightTail = getPoint( -1 * length, -tempTailWidth / 2 );
            leftTail = getPoint( -1 * length, tempTailWidth / 2 );

//            arrowPath.moveTo( (float) tipLocation.getX(), (float) tipLocation.getY() );
//            lineTo( arrowPath, rightFlap );
//            lineTo( arrowPath, rightPin );
//            lineTo( arrowPath, rightTail );
//            lineTo( arrowPath, leftTail );
//            lineTo( arrowPath, leftPin );
//            lineTo( arrowPath, leftFlap );
//            arrowPath.closePath();
        }
    }


    //parallel and normal are from the tip
    private ImmutableVector2F getPoint( float parallel, float normal ) {
        // do scaling and addition of vector components inline to improve performance
        float x = ( direction.getX() * parallel ) + ( norm.getX() * normal ) + tipLocation.getX();
        float y = ( direction.getY() * parallel ) + ( norm.getY() * normal ) + tipLocation.getY();
        return new ImmutableVector2F( x, y );
    }

    public ImmutableVector2F getTailLocation() {
        return tailLocation;
    }

    public ImmutableVector2F getTipLocation() {
        return tipLocation;
    }

    public void setTailLocation( ImmutableVector2F tailLocation ) {
        this.tailLocation = tailLocation;
        computeArrow();
    }

    public void setTipLocation( ImmutableVector2F tipLocation ) {
        this.tipLocation = tipLocation;
        computeArrow();
    }

    public void setTipAndTailLocations( ImmutableVector2F tipLocation, ImmutableVector2F tailLocation ) {
        this.tipLocation = tipLocation;
        this.tailLocation = tailLocation;
        computeArrow();
    }

    public float getTailWidth() {
        return tailWidth;
    }

    public void setTailWidth( float tailWidth ) {
        this.tailWidth = tailWidth;
        computeArrow();
    }

    public void setHeadWidth( float headWidth ) {
        this.headWidth = headWidth;
        computeArrow();
    }

    public float getHeadWidth() {
        return headWidth;
    }

    public void setHeadHeight( float headHeight ) {
        this.headHeight = headHeight;
        computeArrow();
    }

    public float getHeadHeight() {
        return headHeight;
    }

    public void setFractionalHeadHeight( float fractionalHeadHeight ) {
        this.fractionalHeadHeight = fractionalHeadHeight;
        computeArrow();
    }

    public float getFractionalHeadHeight() {
        return fractionalHeadHeight;
    }

    public void setScaleTailToo( boolean scaleTailToo ) {
        this.scaleTailToo = scaleTailToo;
        computeArrow();
    }

    public boolean getScaleTailToo() {
        return scaleTailToo;
    }

    public ImmutableVector2F getLeftFlap() {
        return leftFlap;
    }

    public ImmutableVector2F getLeftPin() {
        return leftPin;
    }

    public ImmutableVector2F getLeftTail() {
        return leftTail;
    }

    public ImmutableVector2F getRightFlap() {
        return rightFlap;
    }

    public ImmutableVector2F getRightPin() {
        return rightPin;
    }

    public ImmutableVector2F getRightTail() {
        return rightTail;
    }
}
