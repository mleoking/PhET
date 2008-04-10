package edu.colorado.phet.fitness.control;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import edu.colorado.phet.common.motion.graphs.ControlGraph;
import edu.colorado.phet.common.motion.graphs.ControlGraphSeries;
import edu.colorado.phet.common.motion.graphs.GraphSuiteSet;
import edu.colorado.phet.common.motion.graphs.MinimizableControlGraph;
import edu.colorado.phet.common.motion.model.DefaultTemporalVariable;
import edu.colorado.phet.common.motion.model.MotionTimeSeriesModel;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.view.graphics.Arrow;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.barchart.BarChartNode;
import edu.colorado.phet.common.timeseries.model.TestTimeSeries;
import edu.colorado.phet.common.timeseries.model.TimeSeriesModel;
import edu.colorado.phet.fitness.FitnessResources;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PDragEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PDimension;
import edu.umd.cs.piccolo.util.PPaintContext;

/**
 * Created by: Sam
 * Apr 9, 2008 at 8:59:34 PM
 */
public class CaloriePanel extends PNode {
    private CalorieSlider calorieSlider;
    private CalorieSlider burnSlider;

    public CaloriePanel( PhetPCanvas phetPCanvas ) {
        PNode foodStrip = new IconStrip( new IconStrip.IconItem[]{
                new IconStrip.IconItem( "burger.png" ),
                new IconStrip.IconItem( "strawberry.png" ),
                new IconStrip.IconItem( "bananasplit.png" ),
                new IconStrip.IconItem( "grapefruit.png" ),
                new IconStrip.IconItem( "burger.png" ),
                new IconStrip.IconItem( "strawberry.png" ),
                new IconStrip.IconItem( "bananasplit.png" ),
                new IconStrip.IconItem( "grapefruit.png" ),
        } );
        calorieSlider = new CalorieSlider( "Caloric Intake", Color.green, true );
        addChild( calorieSlider );

        burnSlider = new CalorieSlider( "Caloric Burn", Color.red, false );
        burnSlider.addRegion( 0, 50, new Color( 225, 138, 138 ) );
        addChild( burnSlider );

        PImage plateNode = new PImage( FitnessResources.getImage( "plate.png" ) );
//            im = new PImage( ImageLoader.loadBufferedIm
// age( "fitness/images/plate.png" ) );

        plateNode.setOffset( 0, 70 );
        addChild( plateNode );
        addChild( foodStrip );
        calorieSlider.setOffset( 0, 175 );
        burnSlider.setOffset( 0, calorieSlider.getFullBounds().getMaxY() + 5 );

        BarChartNode foodCompositionBarChart = new BarChartNode( "Food Composition", 200, Color.white, 200 );
        foodCompositionBarChart.setVariables( new BarChartNode.Variable[]{
                new BarChartNode.Variable( "Carbs", 0.7, Color.green ),
                new BarChartNode.Variable( "Proteins", 0.3, Color.red ),
                new BarChartNode.Variable( "Lipids", 0.5, Color.blue ),
        } );
        foodCompositionBarChart.setOffset( calorieSlider.getFullBounds().getMaxX() + 30, foodStrip.getFullBounds().getMaxY() + 5 );
        addChild( foodCompositionBarChart );


        BarChartNode exerciseChart = new BarChartNode( "Exercise", 200 / 24, Color.white, 200 );//hours
        exerciseChart.setVariables( new BarChartNode.Variable[]{
                new BarChartNode.Variable( "Activity", 10, Color.red ),
                new BarChartNode.Variable( "Relaxing", 12, Color.blue ),
                new BarChartNode.Variable( "Sleep", 8, Color.green ),
        } );
//        exerciseChart.setOffset( foodCompositionBarChart.getFullBounds().getX(), burnSlider.getFullBounds().getY() );
        exerciseChart.setOffset( foodCompositionBarChart.getFullBounds().getMaxX() + 50, foodCompositionBarChart.getFullBounds().getY() );
        addChild( exerciseChart );

        PhetPPath activityAcceptor = new PhetPPath( new Rectangle2D.Double( 0, 0, 50, 50 ), new BasicStroke( 2 ), Color.black );
        PText activityText = new PText( "Activity" );
        activityText.setOffset( 3, 1 );
        PNode activityAcceptorNode = new PNode();
        activityAcceptorNode.addChild( activityAcceptor );
        activityAcceptorNode.addChild( activityText );
        activityAcceptorNode.setOffset( burnSlider.getFullBounds().getCenterX() - activityAcceptorNode.getFullBounds().getWidth() / 2, burnSlider.getFullBounds().getMaxY() + 5 );
        addChild( activityAcceptorNode );

        PNode exerciseStrip = new IconStrip( new IconStrip.IconItem[]{
                new IconStrip.IconItem( "bike.png" ),
                new IconStrip.IconItem( "swim.png" ),
                new IconStrip.IconItem( "bike.png" ),
                new IconStrip.IconItem( "swim.png" ),
                new IconStrip.IconItem( "bike.png" ),
                new IconStrip.IconItem( "swim.png" ),
        } );
        exerciseStrip.setOffset( 0, activityAcceptorNode.getFullBounds().getMaxY() + 10 );
        addChild( exerciseStrip );

        GraphSuiteSet graphSuiteSet = new GraphSuiteSet();
        TimeSeriesModel tsm = new MotionTimeSeriesModel( new TestTimeSeries.MyRecordableModel(), new ConstantDtClock( 30, 1 ) );
        ControlGraph controlGraph = new ControlGraph( phetPCanvas, new ControlGraphSeries( "Weight", Color.blue, "weight", "lbs", "human", new DefaultTemporalVariable() ), "Weight", 0, 100, tsm );
        controlGraph.setEditable( false );
        MinimizableControlGraph a = new MinimizableControlGraph( "Weight", controlGraph );
        ControlGraph controlGraph1 = new ControlGraph( phetPCanvas, new ControlGraphSeries( "Calories", Color.blue, "Cal", "Cal", "human", new DefaultTemporalVariable() ), "Calories", 0, 100, tsm );
        controlGraph1.setEditable( false );
        MinimizableControlGraph b = new MinimizableControlGraph( "Calories", controlGraph1 );
        a.setAvailableBounds( 600, 125 );
        b.setAvailableBounds( 600, 125 );
        MinimizableControlGraph[] graphs = {a, b};
        a.setAlignedLayout( graphs );
        b.setAlignedLayout( graphs );
        graphSuiteSet.addGraphSuite( graphs );
        a.setOffset( 0, exerciseStrip.getFullBounds().getMaxY() );
        b.setOffset( 0, a.getFullBounds().getMaxY() );
        addChild( a );

        addChild( b );
    }

    private static class CalorieSlider extends PNode {
        public static double WIDTH = 200;
        public static double HEIGHT = 30;
        private PhetPPath arrowNode;
        private PText titleNode;
        private PhetPPath borderNode;

        public CalorieSlider( String title, Color color, boolean arrowDown ) {
            titleNode = new PText( title );
            addChild( titleNode );
            borderNode = new PhetPPath( new Rectangle2D.Double( 0, 0, WIDTH, HEIGHT ), new BasicStroke( 1.5f ), Color.black );
            addChild( borderNode );
            arrowNode = new PhetPPath( new Arrow( new Point2D.Double( 0, arrowDown ? 0 : HEIGHT ), new Point2D.Double( 0, arrowDown ? HEIGHT : 0 ), 12, 12, 7 ).getShape(), color );

            arrowNode.addInputEventListener( new CursorHandler() );
            arrowNode.addInputEventListener( new PDragEventHandler() {
                protected void drag( PInputEvent event ) {
//                    super.drag( event );
                    PDimension d = event.getDeltaRelativeTo( getDraggedNode() );
                    getDraggedNode().localToParent( d );
                    getDraggedNode().offset( d.getWidth(), 0 );
                    if ( getDraggedNode().getOffset().getX() < 0 ) {
                        getDraggedNode().setOffset( 0, getDraggedNode().getOffset().getY() );
                    }
                    else if ( getDraggedNode().getOffset().getX() > WIDTH ) {
                        getDraggedNode().setOffset( WIDTH, getDraggedNode().getOffset().getY() );
                    }
                }
            } );
//            titleNode.setOffset( 0, -titleNode.getHeight() );
            borderNode.setOffset( 0, titleNode.getHeight() );
            arrowNode.setOffset( 0, titleNode.getHeight() );

            PText low = new PText( "None" );
            addChild( low );
            low.setOffset( -low.getFullBounds().getWidth(), borderNode.getFullBounds().getCenterY() - low.getFullBounds().getHeight() / 2 );
            PText lots = new PText( "Lots" );
            addChild( lots );
            lots.setOffset( borderNode.getFullBounds().getWidth(), borderNode.getFullBounds().getCenterY() - low.getFullBounds().getHeight() / 2 );

            addChild( arrowNode );
        }

        public void addRegion( int x0, int x1, Color color ) {
            PhetPPath path = new PhetPPath( new Rectangle2D.Double( x0, 0, x1, HEIGHT ), color );
            path.setOffset( 0, titleNode.getHeight() );
            addChild( path );
            borderNode.moveToFront();
            arrowNode.moveToFront();
        }
    }

    static class BoxedImage extends PImage {
        private boolean drawBorder = true;

        public BoxedImage( BufferedImage image ) {
            super( image );
        }

        public boolean isDrawBorder() {
            return drawBorder;
        }

        public void setDrawBorder( boolean drawBorder ) {
            this.drawBorder = drawBorder;
        }

        protected void paint( PPaintContext paintContext ) {
            if ( drawBorder ) {
                Graphics2D g2 = paintContext.getGraphics();
                g2.setPaint( Color.black );
                g2.setStroke( new BasicStroke( 1 ) );
                g2.draw( getBoundsReference() );
            }
            super.paint( paintContext );
        }
    }

    private static class IconStrip extends PNode {
        double height = 30;


        private IconStrip( IconItem[] iconItems ) {
            final BoxedImage[] im = new BoxedImage[iconItems.length];
            for ( int i = 0; i < iconItems.length; i++ ) {
                final IconItem foodItem = iconItems[i];
                im[i] = createNode( foodItem, i, im );
                final int i1 = i;
                im[i].addInputEventListener( new PDragEventHandler() {
                    BoxedImage createdNode = null;

                    protected void startDrag( PInputEvent event ) {
                        super.startDrag( event );
                        createdNode = createNode( foodItem, i1, im );
                        createdNode.addInputEventListener( new PDragEventHandler() {
                            protected void startDrag( PInputEvent event ) {
                                super.startDrag( event );
                                createdNode.moveToFront();
                            }
                        } );
                        createdNode.setDrawBorder( false );
                        IconStrip.this.addChild( createdNode );
                        createdNode.moveToFront();
                    }

                    protected void drag( PInputEvent event ) {
                        PDimension d = event.getDeltaRelativeTo( im[i1] );
                        createdNode.localToParent( d );
                        createdNode.offset( d.getWidth(), d.getHeight() );
                    }
                } );
            }
        }

        private BoxedImage createNode( IconItem foodItem, int index, BoxedImage[] others ) {
            BufferedImage image = FitnessResources.getImage( foodItem.getImageName() );
            double sy = height / image.getHeight();
            int width = (int) ( sy * image.getWidth() );
            BoxedImage imx = new BoxedImage( getScaledInstance( image, width, (int) height, RenderingHints.VALUE_INTERPOLATION_BILINEAR, true ) );

//            imx.scale( height / imx.getFullBounds().getHeight() );
            addChild( imx );
            imx.addInputEventListener( new CursorHandler() );
            if ( index > 0 ) {
                imx.setOffset( others[index - 1].getFullBounds().getMaxX(), others[index - 1].getFullBounds().getY() );
            }

            return imx;
        }

        private static class IconItem {
            private String image;

            public IconItem( String image ) {
                this.image = image;
            }

            public String getImageName() {
                return image;
            }
        }
    }

    /**
     * 
     * Convenience method that returns a scaled instance of the
     * provided {@code BufferedImage}.
     *
     * @param img           the original image to be scaled
     * @param targetWidth   the desired width of the scaled instance,
     *                      in pixels
     * @param targetHeight  the desired height of the scaled instance,
     *                      in pixels
     * @param hint          one of the rendering hints that corresponds to
     *                      {@code RenderingHints.KEY_INTERPOLATION} (e.g.
     *                      {@code RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR},
     *                      {@code RenderingHints.VALUE_INTERPOLATION_BILINEAR},
     *                      {@code RenderingHints.VALUE_INTERPOLATION_BICUBIC})
     * @param higherQuality if true, this method will use a multi-step
     *                      scaling technique that provides higher quality than the usual
     *                      one-step technique (only useful in downscaling cases, where
     *                      {@code targetWidth} or {@code targetHeight} is
     *                      smaller than the original dimensions, and generally only when
     *                      the {@code BILINEAR} hint is specified)
     * @return a scaled version of the original {@code BufferedImage}
     *         <p/>
     *
     * see http://today.java.net/pub/a/today/2007/04/03/perils-of-image-getscaledinstance.html
     * see also SwingLabs GraphicsUtilities
     */
    public static BufferedImage getScaledInstance( BufferedImage img,
                                                   int targetWidth,
                                                   int targetHeight,
                                                   Object hint,
                                                   boolean higherQuality ) {
        int type = BufferedImage.TYPE_INT_ARGB;
        BufferedImage ret = (BufferedImage) img;
        int w, h;
        if ( higherQuality ) {
            // Use multi-step technique: start with original size, then
            // scale down in multiple passes with drawImage()
            // until the target size is reached
            w = img.getWidth();
            h = img.getHeight();
        }
        else {
            // Use one-step technique: scale directly from original
            // size to target size with a single drawImage() call
            w = targetWidth;
            h = targetHeight;
        }

        do {
            if ( higherQuality && w > targetWidth ) {
                w /= 2;
                if ( w < targetWidth ) {
                    w = targetWidth;
                }
            }

            if ( higherQuality && h > targetHeight ) {
                h /= 2;
                if ( h < targetHeight ) {
                    h = targetHeight;
                }
            }

            BufferedImage tmp = new BufferedImage( w, h, type );
            Graphics2D g2 = tmp.createGraphics();
            g2.setRenderingHint( RenderingHints.KEY_INTERPOLATION, hint );
            g2.drawImage( ret, 0, 0, w, h, null );
            g2.dispose();

            ret = tmp;
        } while ( w != targetWidth || h != targetHeight );

        return ret;
    }


}
