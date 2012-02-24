// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractionsintro.intro.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.Hashtable;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.property.SettableProperty;
import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.fractions.FractionsResources;
import edu.colorado.phet.fractionsintro.intro.model.CellPointer;
import edu.colorado.phet.fractionsintro.intro.model.ContainerSet;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;

/**
 * Shows just one cake with the specified pieces.
 *
 * @author Sam Reid
 */
public class CakeNode extends PNode {

    Point2D.Double center = new Point2D.Double( 86.12112259970462, 94.81388478581977 );

    Point2D.Double[] cakeFrame = new Point2D.Double[] {
            new Point2D.Double( 6.377963737796315, 104.99163179916316 ),
            new Point2D.Double( 86.04323570432352, 151.93723849372384 ),
            new Point2D.Double( 164.2859135285913, 105.70292887029287 ),
            new Point2D.Double( 84.62064156206411, 59.4686192468619 )
    };
    private final Ellipse2D.Double cakePanEllipse;

    //Create the point map table by clicking on the points where the lines radiating from the center intersect with the bounding ellipse
    //Sample manually since analytical expression is tricky.  TODO: Or is it?
    //Then use to create shapes for hit detection with empty cake.
    Hashtable<Integer, Point2D.Double[]> pointMap = new Hashtable<Integer, Point2D.Double[]>() {{
        put( 1, new Point2D.Double[] { new Point2D.Double( 86.12112259970462, 60.663220088626275 ) } );
        put( 2, new Point2D.Double[] {
                new Point2D.Double( 86.12112259970462, 60.663220088626275 ),
                new Point2D.Double( 86.12112259970462, 151.0620384047267 ) } );
        put( 3, new Point2D.Double[] {
                new Point2D.Double( 162.45790251107832, 94.81388478581977 ),
                new Point2D.Double( 53.97932053175779, 64.68094534711963 ),
                new Point2D.Double( 36.90398818316104, 140.01329394387 )
        } );
        put( 4, new Point2D.Double[] {
                new Point2D.Double( 86.12112259970462, 59.658788774002936 ),
                new Point2D.Double( 10.788774002954243, 93.80945347119643 ),
                new Point2D.Double( 86.12112259970462, 150.05760709010337 ),
                new Point2D.Double( 160.44903988183165, 94.81388478581977 )
        } );
        put( 5, new Point2D.Double[] {
                new Point2D.Double( 105.20531757754804, 61.667651403249614 ),
                new Point2D.Double( 31.881831610044344, 72.71639586410633 ),
                new Point2D.Double( 13.802067946824257, 122.93796159527325 ),
                new Point2D.Double( 117.2584933530281, 146.03988183161002 ),
                new Point2D.Double( 161.45347119645498, 93.80945347119643 )
        } );
        put( 6, new Point2D.Double[] {
                new Point2D.Double( 161.45347119645498, 93.80945347119643 ),
                new Point2D.Double( 116.25406203840477, 63.676514032496286 ),
                new Point2D.Double( 54.98375184638113, 63.676514032496286 ),
                new Point2D.Double( 9.784342688330904, 93.80945347119643 ),
                new Point2D.Double( 36.90398818316104, 141.01772525849333 ),
                new Point2D.Double( 134.33382570162485, 141.01772525849333 )
        } );
        put( 7, new Point2D.Double[] {
                new Point2D.Double( 161.45347119645498, 94.81388478581977 ),
                new Point2D.Double( 125.2939438700148, 65.68537666174296 ),
                new Point2D.Double( 70.0502215657312, 58.6543574593796 ),
                new Point2D.Double( 23.84638109305764, 76.73412112259969 ),
                new Point2D.Double( 9.784342688330904, 114.90251107828654 ),
                new Point2D.Double( 64.02363367799117, 148.0487444608567 ),
                new Point2D.Double( 144.37813884785822, 134.9911373707533 )
        } );
        put( 8, new Point2D.Double[] {
                new Point2D.Double( 161.45347119645498, 93.80945347119643 ),
                new Point2D.Double( 130.3161004431315, 68.69867060561297 ),
                new Point2D.Double( 85.11669128508127, 58.6543574593796 ),
                new Point2D.Double( 36.90398818316104, 68.69867060561297 ),
                new Point2D.Double( 6.77104874446089, 93.80945347119643 ),
                new Point2D.Double( 18.824224519940948, 130.97341211225995 ),
                new Point2D.Double( 85.11669128508127, 150.05760709010337 ),
                new Point2D.Double( 149.4002954209749, 129.96898079763662 ) } );
    }};
    private static final boolean debugPieceLocations = false;

    public CakeNode( final int denominator, final int[] pieces, final SettableProperty<ContainerSet> containerSetProperty, final int container, final int[] sliceOrder ) {
        Rectangle2D r = new Rectangle2D.Double( cakeFrame[0].getX(), cakeFrame[0].getY(), 0, 0 );
        r = r.createUnion( new Rectangle2D.Double( cakeFrame[1].getX(), cakeFrame[1].getY(), 0, 0 ) );
        r = r.createUnion( new Rectangle2D.Double( cakeFrame[2].getX(), cakeFrame[2].getY(), 0, 0 ) );
        r = r.createUnion( new Rectangle2D.Double( cakeFrame[3].getX(), cakeFrame[3].getY(), 0, 0 ) );
        cakePanEllipse = new Ellipse2D.Double( r.getX(), r.getY(), r.getWidth(), r.getHeight() );


        final BufferedImage[] images = new BufferedImage[pieces.length];
        for ( int i = 0; i < images.length; i++ ) {
            images[i] = cropSides( FractionsResources.RESOURCES.getImage( "cake/cake_" + denominator + "_" + pieces[i] + ".png" ) );
        }


        final PBasicInputEventHandler sharedMouseListener = new PBasicInputEventHandler() {
            @Override public void mousePressed( PInputEvent event ) {

                //Find the color of the pixel hit by the mouse.  If white or transparent in the image, subtract a piece because the user clicked the background
                //Otherwise add a piece
                Point2D position = event.getPositionRelativeTo( CakeNode.this );

                //Chain of responsibility sort of thing to see which cake slice gets to handle it.
                for ( int i = images.length - 1; i >= 0; i-- ) {
                    BufferedImage image = images[i];
                    int pixel = image.getRGB( (int) position.getX(), (int) position.getY() );
                    Color color = new Color( pixel, true );
                    if ( color.getRed() > 0 || color.getGreen() > 0 || color.getBlue() > 0 ) {
                        //Clicked on a piece of cake, turning it off...
                        //System.out.println( "//Clicked on a piece of cake, turning it off..." );
                        int piece = pieces[i] - 1;

                        //I do not know why pieces are backwards for the d=2 case, but they are.  So un-backwards them with the next line:
                        if ( denominator == 2 ) { piece = ( piece + 1 ) % denominator; }
                        containerSetProperty.set( containerSetProperty.get().toggle( new CellPointer( container, piece ) ) );
                        return;
                    }
                }

                //Clicked in a blank area, turning on an empty cell.
                //System.out.println( "//Clicked in a blank area, turning on an empty cell." );

                //See which blank cell they may have clicked in, if any
                for ( int i = 0; i < denominator; i++ ) {
                    final Area shape = createShape( i, denominator );
                    if ( shape.contains( position ) ) {
                        int piece = sliceOrder[i] - 1;

                        //For unknown reasons, the slice indices are off by 1 for denominator 4 and 5.  I determined this experimentally and do not know the cause.  Maybe a flaw in the original sliceOrder array?
                        if ( denominator == 5 || denominator == 4 ) {
                            piece = ( piece + 1 ) % denominator;
                        }
                        containerSetProperty.set( containerSetProperty.get().toggle( new CellPointer( container, piece ) ) );
                        return;
                    }
                }
            }
        };

        addChild( new PImage( cropSides( FractionsResources.RESOURCES.getImage( "cake/cake_grid_" + denominator + ".png" ) ) ) {{
            addInputEventListener( new CursorHandler() );
            addInputEventListener( sharedMouseListener );
        }} );

        for ( int i = 0; i < pieces.length; i++ ) {
            addChild( new PImage( images[i] ) {{
                addInputEventListener( new CursorHandler() );
                addInputEventListener( sharedMouseListener );
            }} );
        }

        if ( debugPieceLocations ) {
            Color[] colors = new Color[] {
                    Color.red, Color.green, Color.blue, Color.orange, Color.black, Color.magenta, Color.yellow, Color.gray
            };
            int numSlices = denominator;
            for ( int i = 0; i < numSlices; i++ ) {
                addChild( new PhetPPath( createShape( i, denominator ), colors[i] ) );
            }

            addChild( new PhetPPath( cakePanEllipse, new BasicStroke( 2 ), Color.red ) {{
                setPickable( false );
                setChildrenPickable( false );
            }} );
        }
    }

    public Area createShape( final int piece, final int denominator ) {
        return new Area( cakePanEllipse ) {{intersect( bigSlice( piece, denominator ) );}};
    }

    //Create a big slice (e.g. outside of the cake bounds) for intersecting with the cake pie tin
    private Area bigSlice( int piece, int denominator ) {
        if ( denominator == 1 ) { return new Area( cakePanEllipse ); }
        else if ( denominator == 2 ) {

            //Cannot triangulate since angle is 180
            if ( piece == 0 ) {
                return new Area( new Rectangle2D.Double( center.getX(), center.getY() - 1000, 2000, 2000 ) );
            }
            else if ( piece == 1 ) {
                return new Area( new Rectangle2D.Double( center.getX() - 2000, center.getY() - 1000, 2000, 2000 ) );
            }
        }
        Point2D start = pointMap.get( denominator )[piece];
        Point2D end = pointMap.get( denominator )[( piece + 1 ) % denominator];//wrap
        final ImmutableVector2D v = new ImmutableVector2D( center, start ).times( 1000 );
        final ImmutableVector2D v2 = new ImmutableVector2D( center, end ).times( 1000 );
        return new Area( new DoubleGeneralPath( center ) {{
            lineTo( v );
            lineTo( v2 );
            lineTo( center );
        }}.getGeneralPath() );
    }

    //trim whitespace on the sides of the image so cakes don't overlap (or else space out the cakes more)
    //Trim the sides since there is too much alpha left over in the images and it causes them to overlap so that mouse presses are caught by adjacent cakes instead of the desired cake
    public static BufferedImage cropSides( BufferedImage image ) {
        final int TRIM = 24;
        BufferedImage im = new BufferedImage( image.getWidth() - TRIM * 2, image.getHeight(), image.getType() );
        Graphics2D g2 = im.createGraphics();
        g2.drawRenderedImage( image, AffineTransform.getTranslateInstance( -TRIM, 0 ) );
        g2.dispose();
        return im;
    }
}