// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.lwjglphet.math;

import edu.colorado.phet.common.phetcommon.math.vector.Vector2F;

// copied somewhat from the Arrow class TODO cleanup
public class Arrow2F {
    private Vector2F tailLocation;
    private Vector2F tipLocation;
    private float headHeight;
    private float headWidth;
    private float tailWidth;
    private float fractionalHeadHeight;
    private boolean scaleTailToo;
    private float headScale;
    private Vector2F direction;
    private Vector2F norm;
    boolean isHeadDynamic = false;

    private Vector2F rightFlap;
    private Vector2F leftFlap;
    private Vector2F rightPin;
    private Vector2F leftPin;
    private Vector2F rightTail;
    private Vector2F leftTail;

    public Arrow2F( Vector2F tailLocation, Vector2F tipLocation, float headHeight, float headWidth, float tailWidth ) {
        init( tailLocation, tipLocation, headHeight, headWidth, tailWidth );
        computeArrow();
    }

    /**
     * @param fractionalHeadHeight When the head size is less than fractionalHeadHeight * arrow length,
     *                             the head will be scaled.
     */
    public Arrow2F( Vector2F tailLocation, Vector2F tipLocation,
                    float headHeight, float headWidth, float tailWidth,
                    float fractionalHeadHeight, boolean scaleTailToo ) {
        this.fractionalHeadHeight = fractionalHeadHeight;
        this.scaleTailToo = scaleTailToo;
        this.isHeadDynamic = true;
        init( tailLocation, tipLocation, headHeight, headWidth, tailWidth );
        computeArrow();
    }


    void init( Vector2F tailLocation, Vector2F tipLocation, float headHeight, float headWidth, float tailWidth ) {
        this.tailLocation = tailLocation;
        this.tipLocation = tipLocation;
        this.headHeight = headHeight;
        this.headWidth = headWidth;
        this.tailWidth = tailWidth;
    }

    private void computeArrow() {
        if ( tailLocation.distance( tipLocation ) != 0 ) {
            Vector2F distanceVector = tipLocation.minus( tailLocation );
            direction = distanceVector.getNormalizedInstance();
            float length = tipLocation.distance( tailLocation );
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
    private Vector2F getPoint( float parallel, float normal ) {
        // do scaling and addition of vector components inline to improve performance
        float x = ( direction.getX() * parallel ) + ( norm.getX() * normal ) + tipLocation.getX();
        float y = ( direction.getY() * parallel ) + ( norm.getY() * normal ) + tipLocation.getY();
        return new Vector2F( x, y );
    }

    public Vector2F getTailLocation() {
        return tailLocation;
    }

    public Vector2F getTipLocation() {
        return tipLocation;
    }

    public void setTailLocation( Vector2F tailLocation ) {
        this.tailLocation = tailLocation;
        computeArrow();
    }

    public void setTipLocation( Vector2F tipLocation ) {
        this.tipLocation = tipLocation;
        computeArrow();
    }

    public void setTipAndTailLocations( Vector2F tipLocation, Vector2F tailLocation ) {
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

    public Vector2F getLeftFlap() {
        return leftFlap;
    }

    public Vector2F getLeftPin() {
        return leftPin;
    }

    public Vector2F getLeftTail() {
        return leftTail;
    }

    public Vector2F getRightFlap() {
        return rightFlap;
    }

    public Vector2F getRightPin() {
        return rightPin;
    }

    public Vector2F getRightTail() {
        return rightTail;
    }
}
