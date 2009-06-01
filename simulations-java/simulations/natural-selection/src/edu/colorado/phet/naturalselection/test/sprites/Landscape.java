package edu.colorado.phet.naturalselection.test.sprites;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.LinkedList;
import java.util.List;

import edu.colorado.phet.common.phetcommon.math.Point3D;
import edu.umd.cs.piccolo.PNode;

public class Landscape extends PNode implements Sprite.Listener {

    public static final Dimension LANDSCAPE_SIZE = new Dimension( 640, 480 );
    public static final double LANDSCAPE_HORIZON = 100;

    public static final double LANDSCAPE_NEARPLANE = 150;
    public static final double LANDSCAPE_FARPLANE = 300;
    public static final double LANDSCAPE_VERTICAL_RISE = 100;

    private List<Sprite> sprites;

    private double landscapeWidth;
    private double landscapeHeight;

    public Landscape() {
        sprites = new LinkedList<Sprite>();

        double nearX = getMaximumX( LANDSCAPE_NEARPLANE );
        double farX = getMaximumX( LANDSCAPE_FARPLANE );

        Point3D topLeft = getGroundPoint( -farX, LANDSCAPE_FARPLANE );
        Point3D topRight = getGroundPoint( farX, LANDSCAPE_FARPLANE );
        Point3D bottomLeft = getGroundPoint( -nearX, LANDSCAPE_NEARPLANE );
        Point3D bottomRight = getGroundPoint( nearX, LANDSCAPE_NEARPLANE );


        addSprite( new StaticSprite( this, topLeft, Color.RED ) );
        addSprite( new StaticSprite( this, topRight, Color.GREEN ) );
        addSprite( new StaticSprite( this, bottomLeft, Color.BLUE ) );
        addSprite( new StaticSprite( this, bottomRight, Color.ORANGE ) );

        for ( int i = 0; i < 100; i++ ) {
            addSprite( new GroundSprite( this, getRandomGroundPosition() ) );
        }

        for ( int i = 0; i < 300; i++ ) {
            addSprite( new ActiveSprite( this, getRandomGroundPosition() ) );
        }

        //addSprite( new StaticSprite( this, new Point3D.Double( 0, 0, 0 ) ) );
        //addSprite( new StaticSprite( this, new Point3D.Double( 80, 0, 20 ) ) );
    }

    public Point3D getRandomGroundPosition() {
        // randomly sample the trapezoid in the z direction
        double z = Math.sqrt( LANDSCAPE_NEARPLANE * LANDSCAPE_NEARPLANE + Math.random() * ( LANDSCAPE_FARPLANE * LANDSCAPE_FARPLANE - LANDSCAPE_NEARPLANE * LANDSCAPE_NEARPLANE ) );

        double x = getMaximumX( z ) * ( Math.random() * 2 - 1 );
        double y = getGroundY( x, z );

        return new Point3D.Double( x, y, z );
    }

    public void addSprite( Sprite sprite ) {
        sprites.add( sprite );
        addChild( sprite );
        sprite.addSpriteListener( this );

        repositionSprite( sprite );
        spriteDepthCheck( sprite );
    }

    public void removeSprite( Sprite sprite ) {
        sprites.remove( sprite );
        removeChild( sprite );
        sprite.removeSpriteListener( this );
    }


    public void spriteMoved( Sprite sprite, boolean zChanged ) {
        repositionSprite( sprite );
        if ( zChanged ) {
            //spriteDepthCheck( sprite );
        }
        sprite.repaint();
    }

    private void spriteDepthCheck( Sprite sprite ) {
        List children = getChildrenReference();
        int idx = children.indexOf( sprite );
        if ( idx == -1 ) {
            return;
        }

        boolean ok = true;
        if ( idx > 0 && ( (Sprite) getChild( idx - 1 ) ).getPosition().getZ() < sprite.getPosition().getZ() ) {
            ok = false;
        }
        if ( idx < children.size() - 1 && ( (Sprite) getChild( idx + 1 ) ).getPosition().getZ() > sprite.getPosition().getZ() ) {
            ok = false;
        }

        if ( !ok ) {
            replaceSprite( sprite );
        }
    }

    private void replaceSprite( Sprite sprite ) {
        removeChild( sprite );
        List children = getChildrenReference();
        for ( int i = 0; i < children.size(); i++ ) {
            Sprite testSprite = (Sprite) children.get( i );
            if ( sprite.getPosition().getZ() >= testSprite.getPosition().getZ() ) {
                addChild( i, sprite );
                return;
            }
        }
        addChild( children.size(), sprite );
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

    public Point2D spriteToScreen( Point3D position ) {
        double landscapeX = LANDSCAPE_SIZE.getWidth() / 2 + ( position.getX() / position.getZ() ) * getFactor();
        double landscapeY = LANDSCAPE_HORIZON - ( position.getY() / position.getZ() ) * getFactor();
        return new Point2D.Double( landscapeX * landscapeWidth / LANDSCAPE_SIZE.getWidth(), landscapeY * landscapeHeight / LANDSCAPE_SIZE.getHeight() );
    }

    private void repositionSprite( Sprite sprite ) {
        Point3D position = sprite.getPosition();

        Point2D screenPosition = spriteToScreen( sprite.getPosition() );
        sprite.setOffset( screenPosition.getX(), screenPosition.getY() );
    }

    public void updateLayout( double width, double height ) {
        landscapeWidth = width;
        landscapeHeight = height;
        for ( Sprite sprite : sprites ) {
            repositionSprite( sprite );
        }
    }

    public List<Sprite> getSprites() {
        return sprites;
    }
}
