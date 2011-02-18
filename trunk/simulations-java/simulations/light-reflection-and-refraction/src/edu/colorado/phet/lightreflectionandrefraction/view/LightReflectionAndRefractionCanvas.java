// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.lightreflectionandrefraction.view;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.*;
import java.util.HashMap;

import edu.colorado.phet.common.phetcommon.model.BooleanProperty;
import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.common.phetcommon.util.Function1;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.VoidFunction0;
import edu.colorado.phet.common.phetcommon.util.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.mediabuttons.FloatingClockControlNode;
import edu.colorado.phet.lightreflectionandrefraction.model.LRRModel;
import edu.colorado.phet.lightreflectionandrefraction.model.LightRay;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PDimension;
import edu.umd.cs.piccolo.util.PPaintContext;

/**
 * @author Sam Reid
 */
public class LightReflectionAndRefractionCanvas<T extends LRRModel> extends PhetPCanvas {
    public static final PhetFont labelFont = new PhetFont( 16 );
    private PNode rootNode;
    public final BooleanProperty showNormal = new BooleanProperty( true );
    public final BooleanProperty showProtractor = new BooleanProperty( false );
    public final Property<LaserView> laserView = new Property<LaserView>( LaserView.RAY );
    protected final PNode mediumNode;
    protected final T model;
    protected final ModelViewTransform transform;
    protected final PDimension stageSize;
    BufferedImage bufferedImage = new BufferedImage( 1000, 1000, BufferedImage.TYPE_4BYTE_ABGR_PRE );
    protected final PNode rayLayer = new PNode() {
        @Override
        public void fullPaint( PPaintContext paintContext ) {
            super.fullPaint( paintContext );

//            Composite c = paintContext.getGraphics().getComposite();
//            paintContext.getGraphics().setComposite( new Composite() {
//                public CompositeContext createContext( ColorModel srcColorModel, ColorModel dstColorModel, RenderingHints hints ) {
//                    return new CompositeContext() {
//                        public void dispose() {
//                        }
//
//                        //See http://www.java-gaming.org/index.php?topic=22467.0
//                        public void compose( Raster src, Raster dstIn, WritableRaster dstOut ) {
//                            int chan1 = src.getNumBands();
//                            int chan2 = dstIn.getNumBands();
//
//                            int minCh = Math.min( chan1, chan2 );
//
//                            int[] pxSrc = null;
//                            int[] pxDst = null;
//
////                            System.out.println( "dstIn.getWidth() = " + dstIn.getWidth() + ", h = " + dstIn.getHeight() );
//                            //This bit is horribly inefficient,
//                            //getting individual pixels rather than all at once.
//                            for ( int x = 0; x < dstIn.getWidth(); x++ ) {
//                                for ( int y = 0; y < dstIn.getHeight(); y++ ) {
//
//                                    pxSrc = src.getPixel( x, y, pxSrc );
//                                    pxDst = dstIn.getPixel( x, y, pxDst );
//
//                                    for ( int i = 0; i < 3 && i < minCh; i++ ) {
//                                        pxDst[i] = Math.min( 255, pxSrc[i] + pxDst[i] );
//                                    }
//                                    dstOut.setPixel( x, y, pxDst );
//                                }
//                            }
//                        }
//                    };
//                }
//            } );
//            super.fullPaint( paintContext );
//            paintContext.getGraphics().setComposite( c );
        }
    };

//    BufferedImage tmpImage = new BufferedImage( 1000, 1000, BufferedImage.TYPE_4BYTE_ABGR_PRE );

    @Override
    public void paintComponent( Graphics canvasGraphics1D ) {
        super.paintComponent( canvasGraphics1D );
        Graphics2D canvasGraphics = (Graphics2D) canvasGraphics1D;

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

//        for ( int i = 0; i < rayLayer.getChildrenCount(); i++ ) {
//            PNode child = rayLayer.getChild( i );
//            final Graphics2D tmpGraphics = tmpImage.createGraphics();
//            tmpGraphics.setBackground( new Color( 0, 0, 0, 0 ) );
//            tmpGraphics.clearRect( 0, 0, 1000, 1000 );
//            child.fullPaint( new PPaintContext( tmpGraphics ) );
//
//            BufferedImageOp imageOp = new AdditiveOp( bufferedImage );
//            bufferedImage = imageOp.filter( tmpImage, bufferedImage );
//            tmpGraphics.dispose();
////            images.add(im);
//        }
//        mainBufferGraphics.setPaint( Color.red );
//        float[][] r = new float[bufferedImage.getWidth()][bufferedImage.getHeight()];
//        float[][] g = new float[bufferedImage.getWidth()][bufferedImage.getHeight()];
//        float[][] b = new float[bufferedImage.getWidth()][bufferedImage.getHeight()];
//        float[][] a = new float[bufferedImage.getWidth()][bufferedImage.getHeight()];
//        for ( int i = 0; i < bufferedImage.getWidth(); i++ ) {
//            for ( int k = 0; k < bufferedImage.getHeight(); k++ ) {
//                Color color = new Color( r[i][k], g[i][k], b[i][k], a[i][k] );
//                mainBufferGraphics.setPaint( color );
//                mainBufferGraphics.fillRect( i, k, 1, 1 );
//            }
//        }
//        System.out.println( "rayLayer.getChildrenCount() = " + rayLayer.getChildrenCount() );
        mainBufferGraphics.dispose();

        canvasGraphics.drawRenderedImage( bufferedImage, new AffineTransform() );
    }

    public LightReflectionAndRefractionCanvas( final T model, final Function1<Double, Double> clampDragAngle, final Function1<Double, Boolean> clockwiseArrowNotAtMax, final Function1<Double, Boolean> ccwArrowNotAtMax ) {
        this.model = model;
        model.addRayAddedListener( new VoidFunction1<LightRay>() {
            public void apply( LightRay lightRay ) {
                repaint();//redraw the rays with bresenham
            }
        } );
        // Root of our scene graph
        rootNode = new PNode();
        addWorldChild( rootNode );

        setBackground( Color.black );
        final int stageWidth = 1008;
        stageSize = new PDimension( stageWidth, stageWidth * model.getHeight() / model.getWidth() );

        setWorldTransformStrategy( new PhetPCanvas.CenteredStage( this, stageSize ) );

        final double scale = stageSize.getHeight() / model.getHeight();
        transform = ModelViewTransform.createSinglePointScaleInvertedYMapping( new Point2D.Double( 0, 0 ),
                                                                               new Point2D.Double( stageSize.getWidth() / 2 - 150, stageSize.getHeight() / 2 ),
                                                                               scale );
        mediumNode = new PNode();
        addChild( mediumNode );

        final BooleanProperty showDragHandles = new BooleanProperty( false );
        addChild( new LaserNodeDragHandle( transform, model.getLaser(), 10, showDragHandles, clockwiseArrowNotAtMax ) );
        addChild( new LaserNodeDragHandle( transform, model.getLaser(), -10, showDragHandles, ccwArrowNotAtMax ) );
        addChild( new LaserNode( transform, model.getLaser(), showDragHandles, clampDragAngle ) );

        laserView.addObserver( new SimpleObserver() {
            public void update() {
                model.updateModel();//TODO: Maybe it would be better just to regenerate view, but now we just do this by telling the model to recompute and repopulate
            }
        } );

        final VoidFunction1<LightRay> addLightRayNode = new VoidFunction1<LightRay>() {
            public void apply( LightRay lightRay ) {
                final PNode node = laserView.getValue().createNode( transform, lightRay );
                rayLayer.addChild( node );
                lightRay.addMoveToFrontListener( new VoidFunction0() {
                    public void apply() {
                        node.moveToFront();
                    }
                } );//TODO: memory leak
                lightRay.addRemovalListener( new VoidFunction0() {
                    public void apply() {
                        rayLayer.removeChild( node );
                    }
                } );
            }
        };
        for ( LightRay lightRay : model.getRays() ) {
            addLightRayNode.apply( lightRay );
        }
        model.addRayAddedListener( new VoidFunction1<LightRay>() {
            public void apply( final LightRay lightRay ) {
                addLightRayNode.apply( lightRay );
            }
        } );

        //No time readout
        addChild( new FloatingClockControlNode( new BooleanProperty( true ) {{
            addObserver( new SimpleObserver() {
                public void update() {
                    if ( getValue() ) { model.getClock().start(); }
                    else { model.getClock().pause(); }
                }
            } );
        }}, null, model.getClock(), "Reset", new Property<Color>( Color.white ) ) {{
            setOffset( stageSize.width * 3 / 4 - getFullBounds().getWidth() / 2, stageSize.getHeight() - getFullBounds().getHeight() );
            laserView.addObserver( new SimpleObserver() {
                public void update() {
                    setVisible( laserView.getValue().equals( LaserView.WAVE ) );
                }
            } );
        }} );
//        addChild( rayLayer );

        //Debug for showing stage
//        addChild( new PhetPPath( new Rectangle2D.Double( 0, 0, STAGE_SIZE.getWidth(), STAGE_SIZE.getHeight() ), new BasicStroke( 2 ), Color.red ) );
    }

    public void addChild( PNode node ) {
        rootNode.addChild( node );
    }

    public void removeChild( PNode node ) {
        rootNode.removeChild( node );
    }

    private class AdditiveOp implements BufferedImageOp {
        private BufferedImage other;

        public AdditiveOp( BufferedImage other ) {
            this.other = other;
        }

        public BufferedImage filter( BufferedImage src, BufferedImage dest ) {
            Raster a = src.getData();
            Raster b = other.getData();
            int[] aPixels = a.getPixels( 0, 0, a.getWidth(), a.getHeight(), (int[]) null );
            int[] bPixels = b.getPixels( 0, 0, a.getWidth(), a.getHeight(), (int[]) null );
            WritableRaster c = dest.getRaster();
            int[] cPixels = new int[aPixels.length];
            for ( int i = 0; i < aPixels.length; i++ ) {
                cPixels[i] = aPixels[i] + bPixels[i];
            }
            c.setPixels( 0, 0, c.getWidth(), c.getHeight(), cPixels );

//            for ( int i = 0; i < src.getWidth(); i++ ) {
//                for ( int k = 0; k < src.getHeight(); k++ ) {
//                    int srcRGB = src.getRGB( i, k );
//                    int otherRGB = other.getRGB( i, k );
//                    Color x = new Color( srcRGB );
//                    Color y = new Color( otherRGB );
//                    Color z = new Color( Math.min( 255, x.getRed() + y.getRed() ), Math.min( 255, x.getGreen() + y.getGreen() ), Math.min( 255, x.getBlue() + y.getBlue() ), Math.min( 255, x.getAlpha() + y.getAlpha() ) );
//                    dest.setRGB( i, k, z.getRGB() );
//                }
//            }
            return dest;
        }

        public Rectangle2D getBounds2D( BufferedImage src ) {
            return new Rectangle2D.Double( 0, 0, src.getWidth(), src.getHeight() );
        }

        public BufferedImage createCompatibleDestImage( BufferedImage src, ColorModel destCM ) {
            return new AffineTransformOp( new AffineTransform(), AffineTransformOp.TYPE_NEAREST_NEIGHBOR ).createCompatibleDestImage( src, destCM );
        }

        public Point2D getPoint2D( Point2D srcPt, Point2D dstPt ) {
            if ( dstPt == null ) {
                dstPt = new Point2D.Double();
            }
            dstPt.setLocation( srcPt.getX(), srcPt.getY() );
            return dstPt;
        }

        public RenderingHints getRenderingHints() {
            return null;
        }
    }
}
