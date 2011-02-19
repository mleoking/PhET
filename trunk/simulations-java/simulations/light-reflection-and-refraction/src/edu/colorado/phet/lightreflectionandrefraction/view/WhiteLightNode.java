// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.lightreflectionandrefraction.view;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;

import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;

/**
 * In order to support white light, we need to perform additive color mixing (not subtractive,
 * as is the default when drawing transparent colors on top of each other in Java)
 *
 * @author Sam Reid
 */
public class WhiteLightNode extends PImage {
    //Sampled from actual stage: width=1008.0,height=705.5999999999999
    private BufferedImage bufferedImage = new BufferedImage( 1008, 705, BufferedImage.TYPE_INT_ARGB_PRE );
    private final PNode rayLayer;

    public WhiteLightNode( PNode rayLayer ) {
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
                    final double intensity = child.getLightRay().getPowerFraction();
                    addToMap( x0, y0, color, intensity, map );

                    //Some additional points makes it look a lot better (less sparse) without slowing it down too much
                    addToMap( x0 + 1, y0, color, intensity, map );
                    addToMap( x0, y0 + 1, color, intensity, map );

                    //This code makes a thicker ray
//                    for ( int dx = -3; dx <= 3; dx++ ) {
//                        for ( int dy = -3; dy <= 3; dy++ ) {
//                            addToMap( x0 + dx, y0 + dy, color, map );
//                        }
//                    }
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
//            System.out.println( "samples = " + samples[3] );
            float intensity = samples[3];

            //move excess samples value into the intensity column
            float max = samples[0];
            if ( samples[1] > max ) { max = samples[1]; }
            if ( samples[2] > max ) { max = samples[2]; }
            if ( max > 1 ) {
                final float scale = 2f;//extra factor to make it white instead of cream/orange
                samples[0] = Math.min( samples[0] / max * scale, 1 );
                samples[1] = Math.min( samples[1] / max * scale, 1 );
                samples[2] = Math.min( samples[2] / max * scale, 1 );
                intensity = intensity * max;
            }
            float alpha = (float) Math.sqrt( intensity / 3 );
            alpha = (float) MathUtil.clamp( 0, alpha, 1 );
            mainBufferGraphics.setPaint( new Color( samples[0], samples[1], samples[2], alpha ) );
            mainBufferGraphics.fillRect( point.x, point.y, 1, 1 );
        }
        mainBufferGraphics.dispose();

        setImage( bufferedImage );
        moveToFront();
    }

    private void addToMap( int x0, int y0, Color color, double intensity, HashMap<Point, float[]> map ) {
        float brightnessFactor = 0.2f;//so that rays don't start fully saturated
        final Point point = new Point( x0, y0 );
        if ( map.containsKey( point ) ) {
            float[] current = map.get( point );
            float[] newOne = color.getComponents( null );
//            System.out.println( "Got components: " + newOne.length );
            for ( int a = 0; a <= 3; a++ ) {
                current[a] = current[a] + newOne[a] * brightnessFactor;
            }
            //add intensities, then convert to alpha later
            current[3] = current[3] + (float) intensity;
//            current[3] = Math.max( 0, current[3] - newOne[3] );
//            current[3] = 1f;
        }
        else {
            final float[] components = color.getComponents( null );
            for ( int i = 0; i < components.length; i++ ) {
                components[i] = components[i] * brightnessFactor;
            }
//            components[3] = 1f;
            components[3] = (float) intensity;
            map.put( point, components );
        }
    }
}
