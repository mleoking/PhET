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
    //Sampled from actual stage: width=1008.0,height=705.5999999999999
    private BufferedImage bufferedImage = new BufferedImage( 1008, 705, BufferedImage.TYPE_INT_ARGB_PRE );
    private final PNode rayLayer;

    public RayLayer( PNode rayLayer ) {
        this.rayLayer = rayLayer;
        setImage( bufferedImage );
        setPickable( false );
        setChildrenPickable( false );
    }

    public void updateImage() {
        final Graphics2D mainBufferGraphics = bufferedImage.createGraphics();
        mainBufferGraphics.setBackground( new Color( 0, 0, 0, 0 ) );
        mainBufferGraphics.clearRect( 0, 0, bufferedImage.getWidth(), bufferedImage.getHeight() );
        final HashMap<Point, float[]> map = new HashMap<Point, float[]>();
        for ( int i = 0; i < rayLayer.getChildrenCount(); i++ ) {
            final LightRayNode child = (LightRayNode) rayLayer.getChild( i );
            final TestBresenham testBresenham = new TestBresenham() {
                public void setPixel( int x0, int y0 ) {
                    Color color = child.getColor();
                    addToMap( x0, y0, color, map );

                    //Some additional points makes it look a lot better (less sparse) without slowing it down too much
                    addToMap( x0 + 1, y0, color, map );
                    addToMap( x0, y0 + 1, color, map );
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
            final float[] samples = map.get( point );
            System.out.println( "samples = " + samples[3] );
            mainBufferGraphics.setPaint( new Color( samples[0], samples[1], samples[2], samples[3] ) );
            mainBufferGraphics.fillRect( point.x, point.y, 1, 1 );
        }
        mainBufferGraphics.dispose();

        setImage( bufferedImage );
        moveToFront();
    }

    private void addToMap( int x0, int y0, Color color, HashMap<Point, float[]> map ) {
        final Point point = new Point( x0, y0 );
        if ( map.containsKey( point ) ) {
            float[] current = map.get( point );
            float[] newOne = color.getComponents( null );
//            System.out.println( "Got components: " + newOne.length );
            for ( int a = 0; a <= 2; a++ ) {
                current[a] = Math.min( 1, current[a] + newOne[a] );
            }
//            current[3] = Math.max( 0, current[3] - newOne[3] );
            current[3] = 0.1f;
        }
        else {
            final float[] components = color.getComponents( null );
            components[3] = 0.1f;
            map.put( point, components );
        }
    }
}
