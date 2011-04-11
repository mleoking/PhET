// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.bendinglight.modules.prisms;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;

import edu.colorado.phet.bendinglight.view.BresenhamLineAlgorithm;
import edu.colorado.phet.bendinglight.view.LightRayNode;
import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;

/**
 * In order to support white light, we need to perform additive color mixing (not subtractive,
 * as is the default when drawing transparent colors on top of each other in Java).
 * <p/>
 * This class uses the Bresenham line drawing algorithm (with a stroke thickness of 2) to determine which pixels each ray inhabits.
 * When multiple rays hit the same pixel, their RGB values are added.  If any of the RG or B values is greater than the maximum of 255,
 * then RGB values are scaled down and the leftover part is put into the "intensity" value (which is the sum of the ray intensities).
 * The intensity is converted to a transparency value according to alpha = sqrt(intensity/3), which is also clamped to be between 0 and 255.
 *
 * @author Sam Reid
 */
public class WhiteLightNode extends PImage {
    private BufferedImage image = new BufferedImage( 1008, 706, BufferedImage.TYPE_INT_ARGB_PRE );//Dimensions will need to change if the model aspect ratio changes or stage size changes
    private final PNode rayLayer;

    public WhiteLightNode( PNode rayLayer ) {
        this.rayLayer = rayLayer;
        setImage( image );
        setPickable( false );
        setChildrenPickable( false );
    }

    public void updateImage() {
        //Prepare and clear the Graphics2D to render the rays
        final Graphics2D graphics = image.createGraphics();
        graphics.setBackground( new Color( 0, 0, 0, 0 ) );
        graphics.clearRect( 0, 0, image.getWidth(), image.getHeight() );

        //Store samples of RGB + Intensity in a sparse HashMap
        final HashMap<Point, float[]> map = new HashMap<Point, float[]>();
        for ( int i = 0; i < rayLayer.getChildrenCount(); i++ ) {
            final LightRayNode child = (LightRayNode) rayLayer.getChild( i );
            final BresenhamLineAlgorithm bresenhamLineAlgorithm = new BresenhamLineAlgorithm() {
                public void setPixel( int x0, int y0 ) {
                    Color color = child.getColor();
                    final double intensity = child.getLightRay().getPowerFraction();
                    addToMap( x0, y0, color, intensity, map );

                    //Some additional points makes it look a lot better (less sparse) without slowing it down too much
                    addToMap( x0 + 1, y0, color, intensity, map );
                    addToMap( x0, y0 + 1, color, intensity, map );

                    //This code makes a thicker ray (stroke width of 3)
//                    for ( int dx = -3; dx <= 3; dx++ ) {
//                        for ( int dy = -3; dy <= 3; dy++ ) {
//                            addToMap( x0 + dx, y0 + dy, color, map );
//                        }
//                    }
                }

                @Override
                public boolean isOutOfBounds( int x0, int y0 ) {
                    return x0 < 0 || y0 < 0 || x0 > image.getWidth() || y0 > image.getHeight();
                }
            };
            final int x1 = (int) child.getLine().x1;
            final int y1 = (int) child.getLine().y1;
            final int x2 = (int) child.getLine().x2;
            final int y2 = (int) child.getLine().y2;
            //Some lines don't start in the play area; have to check and swap to make sure the line isn't pruned
            if ( bresenhamLineAlgorithm.isOutOfBounds( x1, y1 ) ) {
                bresenhamLineAlgorithm.draw( x2, y2, x1, y1 );
            }
            else {
                bresenhamLineAlgorithm.draw( x1, y1, x2, y2 );
            }
        }

        float whiteLimit = 0.2f;//Don't let things become completely white, since the background is white
        final float maxChannel = 1 - whiteLimit;
        final float scale = 2f;//extra factor to make it white instead of cream/orange

        //Iterate over the sample points and draw them in the BufferedImage
        //TODO: could maybe speed up by caching colors for individual points
        for ( Point point : map.keySet() ) {
            final float[] samples = map.get( point );
            float intensity = samples[3];

            //move excess samples value into the intensity column
            float max = samples[0];
            if ( samples[1] > max ) { max = samples[1]; }
            if ( samples[2] > max ) { max = samples[2]; }

            samples[0] = (float) MathUtil.clamp( 0, samples[0] / max * scale - whiteLimit, maxChannel );
            samples[1] = (float) MathUtil.clamp( 0, samples[1] / max * scale - whiteLimit, maxChannel );
            samples[2] = (float) MathUtil.clamp( 0, samples[2] / max * scale - whiteLimit, maxChannel );
            intensity = intensity * max;
            float alpha = (float) MathUtil.clamp( 0, Math.sqrt( intensity ), 1 );//don't let it become fully opaque or it looks too dark against white background
            graphics.setPaint( new Color( samples[0], samples[1], samples[2], alpha ) );
            graphics.fillRect( point.x, point.y, 1, 1 );
        }

        //Cleanup and show the image.
        graphics.dispose();
        setImage( image );
    }

    //Add the specified point to the HashMap (creating a new entry if necessary, otherwise adding it to existing values.
    //Take the intensity as the last component of the array
    private void addToMap( int x0, int y0, Color color, double intensity, HashMap<Point, float[]> map ) {
        float brightnessFactor = 0.007f;//so that rays don't start fully saturated: this makes it so that it is possible to see the decrease in intensity after a (nontotal) reflection
        final Point point = new Point( x0, y0 );
        if ( !map.containsKey( point ) ) {
            map.put( point, new float[4] );//seed with zeros so it can be summed
        }
        float[] current = map.get( point );
        float[] newOne = color.getComponents( null );
        for ( int a = 0; a <= 3; a++ ) {
            current[a] = current[a] + newOne[a] * brightnessFactor;
        }
        //add intensities, then convert to alpha later
        current[3] = current[3] + (float) intensity;
    }
}
