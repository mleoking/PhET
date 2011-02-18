// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.lightreflectionandrefraction.view;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;

/**
 * In order to support white light, we need to perform additive color mixing (not subtractive,
 * as is the default when drawing transparent colors on top of each other in Java)
 *
 * @author Sam Reid
 */
public class RayLayer extends PImage {
    private BufferedImage bufferedImage = new BufferedImage( 1000, 1000, BufferedImage.TYPE_4BYTE_ABGR_PRE );
    private final PNode rayLayer;

    public RayLayer( PNode rayLayer ) {
        this.rayLayer = rayLayer;
        setImage( bufferedImage );
    }

    public void updateImage() {
        final Graphics2D mainBufferGraphics = bufferedImage.createGraphics();
        mainBufferGraphics.setBackground( new Color( 0, 0, 0, 0 ) );
        mainBufferGraphics.clearRect( 0, 0, 1000, 1000 );
        mainBufferGraphics.setPaint( Color.blue );
        final HashMap<Point, float[]> map = new HashMap<Point, float[]>();
        for ( int i = 0; i < rayLayer.getChildrenCount(); i++ ) {
            final LightRayNode child = (LightRayNode) rayLayer.getChild( i );
            final TestBresenham testBresenham = new TestBresenham() {
                public void setPixel( int x0, int y0 ) {
                    Color color = child.getColor();
                    final Point point = new Point( x0, y0 );
                    if ( map.containsKey( point ) ) {
                        float[] current = map.get( point );
                        float[] newOne = color.getComponents( null );
                        for ( int a = 0; a <= 3; a++ ) {
                            current[a] = Math.min( 1, current[a] + newOne[a] );
                        }
                    }
                    else {
                        map.put( point, color.getComponents( null ) );
                    }
                }

                @Override
                public boolean isOutOfBounds( int x0, int y0 ) {
                    return x0 < 0 || y0 < 0 || x0 > bufferedImage.getWidth() || y0 > bufferedImage.getHeight();
                }
            };
            final int x1 = (int) child.getLine().x1;
            final int y1 = (int) child.getLine().y1;
            final int x2 = (int) child.getLine().x2;
            final int y2 = (int) child.getLine().y2;
            if ( testBresenham.isOutOfBounds( x1, y1 ) ) {
                testBresenham.draw( x2, y2, x1, y1 );
            }
            else {
                testBresenham.draw( x1, y1, x2, y2 );
            }
        }
        for ( Point point : map.keySet() ) {
            final float[] doubles = map.get( point );
            mainBufferGraphics.setPaint( new Color( doubles[0], doubles[1], doubles[2], doubles[3] ) );
            mainBufferGraphics.fillRect( point.x, point.y, 1, 1 );
        }
        mainBufferGraphics.dispose();

        setImage( bufferedImage );
    }
}
