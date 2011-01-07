// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.naturalselection.test.sprites;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.LinkedList;
import java.util.List;

import edu.colorado.phet.common.phetcommon.math.Point3D;
import edu.umd.cs.piccolox.nodes.PNodeCache;

public class TestLandscape extends PNodeCache implements TestSprite.Listener {

    public static final Dimension LANDSCAPE_SIZE = new Dimension( 640, 480 );
    public static final double LANDSCAPE_HORIZON = 100;

    public static final double LANDSCAPE_NEARPLANE = 150;
    public static final double LANDSCAPE_FARPLANE = 300;
    public static final double LANDSCAPE_VERTICAL_RISE = 100;

    private List<TestSprite> testSprites;

    private double landscapeWidth;
    private double landscapeHeight;

    public TestLandscape() {
        testSprites = new LinkedList<TestSprite>();

        double nearX = getMaximumX( LANDSCAPE_NEARPLANE );
        double farX = getMaximumX( LANDSCAPE_FARPLANE );

        Point3D topLeft = getGroundPoint( -farX, LANDSCAPE_FARPLANE );
        Point3D topRight = getGroundPoint( farX, LANDSCAPE_FARPLANE );
        Point3D bottomLeft = getGroundPoint( -nearX, LANDSCAPE_NEARPLANE );
        Point3D bottomRight = getGroundPoint( nearX, LANDSCAPE_NEARPLANE );


        addSprite( new TestStaticSprite( this, topLeft, Color.RED ) );
        addSprite( new TestStaticSprite( this, topRight, Color.GREEN ) );
        addSprite( new TestStaticSprite( this, bottomLeft, Color.BLUE ) );
        addSprite( new TestStaticSprite( this, bottomRight, Color.ORANGE ) );

        for ( int i = 0; i < 5; i++ ) {
            addSprite( new TestGroundSprite( this, getRandomGroundPosition() ) );
        }

        for ( int i = 0; i < 30; i++ ) {
            addSprite( new TestActiveSprite( this, getRandomGroundPosition() ) );
        }

        //addSprite( new TestStaticSprite( this, new Point3D.Double( 0, 0, 0 ) ) );
        //addSprite( new TestStaticSprite( this, new Point3D.Double( 80, 0, 20 ) ) );
    }

    public Point3D getRandomGroundPosition() {
        // randomly sample the trapezoid in the z direction
        double z = Math.sqrt( LANDSCAPE_NEARPLANE * LANDSCAPE_NEARPLANE + Math.random() * ( LANDSCAPE_FARPLANE * LANDSCAPE_FARPLANE - LANDSCAPE_NEARPLANE * LANDSCAPE_NEARPLANE ) );

        double x = getMaximumX( z ) * ( Math.random() * 2 - 1 );
        double y = getGroundY( x, z );

        return new Point3D.Double( x, y, z );
    }

    public void addSprite( TestSprite testSprite ) {
        testSprites.add( testSprite );
        addChild( testSprite );
        testSprite.addSpriteListener( this );

        repositionSprite( testSprite );
        spriteDepthCheck( testSprite );
    }

    public void removeSprite( TestSprite testSprite ) {
        testSprites.remove( testSprite );
        removeChild( testSprite );
        testSprite.removeSpriteListener( this );
    }


    public void spriteMoved( TestSprite testSprite, boolean zChanged ) {
        repositionSprite( testSprite );
        if ( zChanged ) {
            //spriteDepthCheck( testSprite );
        }
        testSprite.repaint();
    }

    private void spriteDepthCheck( TestSprite testSprite ) {
        List children = getChildrenReference();
        int idx = children.indexOf( testSprite );
        if ( idx == -1 ) {
            return;
        }

        boolean ok = true;
        if ( idx > 0 && ( (TestSprite) getChild( idx - 1 ) ).getPosition().getZ() < testSprite.getPosition().getZ() ) {
            ok = false;
        }
        if ( idx < children.size() - 1 && ( (TestSprite) getChild( idx + 1 ) ).getPosition().getZ() > testSprite.getPosition().getZ() ) {
            ok = false;
        }

        if ( !ok ) {
            replaceSprite( testSprite );
        }
    }

    private void replaceSprite( TestSprite testSprite ) {
        removeChild( testSprite );
        List children = getChildrenReference();
        for ( int i = 0; i < children.size(); i++ ) {
            TestSprite testTestSprite = (TestSprite) children.get( i );
            if ( testSprite.getPosition().getZ() >= testTestSprite.getPosition().getZ() ) {
                addChild( i, testSprite );
                return;
            }
        }
        addChild( children.size(), testSprite );
    }

    public Point3D getGroundPoint( double x, double z ) {
        return new Point3D.Double( x, getGroundY( x, z ), z );
    }

    public double getGroundY( double x, double z ) {
        return ( z - LANDSCAPE_FARPLANE ) * LANDSCAPE_VERTICAL_RISE / ( LANDSCAPE_FARPLANE - LANDSCAPE_NEARPLANE );
    }

    public double getMaximumX( double z ) {
        return z * LANDSCAPE_SIZE.getWidth() * 0.5 / getFactor();
    }

    private double getFactor() {
        return ( LANDSCAPE_SIZE.getHeight() - LANDSCAPE_HORIZON ) * LANDSCAPE_NEARPLANE / LANDSCAPE_VERTICAL_RISE;
    }

    public double landscapeYToZ( double yy ) {
        return ( LANDSCAPE_NEARPLANE * LANDSCAPE_FARPLANE * ( LANDSCAPE_HORIZON - LANDSCAPE_SIZE.getHeight() ) ) * ( LANDSCAPE_FARPLANE * ( LANDSCAPE_HORIZON - yy ) + LANDSCAPE_NEARPLANE * ( yy - LANDSCAPE_SIZE.getHeight() ) );
    }

    public Point2D spriteToScreen( Point3D position ) {
        double landscapeX = LANDSCAPE_SIZE.getWidth() / 2 + ( position.getX() / position.getZ() ) * getFactor();
        double landscapeY = LANDSCAPE_HORIZON - ( position.getY() / position.getZ() ) * getFactor();
        return new Point2D.Double( landscapeX * landscapeWidth / LANDSCAPE_SIZE.getWidth(), landscapeY * landscapeHeight / LANDSCAPE_SIZE.getHeight() );
    }

    private void repositionSprite( TestSprite testSprite ) {
        Point3D position = testSprite.getPosition();

        Point2D screenPosition = spriteToScreen( testSprite.getPosition() );
        testSprite.setOffset( screenPosition.getX(), screenPosition.getY() );
    }

    public void updateLayout( double width, double height ) {
        landscapeWidth = width;
        landscapeHeight = height;
        for ( TestSprite testSprite : testSprites ) {
            repositionSprite( testSprite );
        }
    }

    public List<TestSprite> getSprites() {
        return testSprites;
    }
}
