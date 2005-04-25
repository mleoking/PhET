package edu.colorado.phet.movingman.view;

import edu.colorado.phet.common.math.Function;
import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.common.view.phetgraphics.CompositePhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetTextGraphic;
import edu.colorado.phet.common.view.util.BufferedImageUtils;
import edu.colorado.phet.common.view.util.ImageLoader;
import edu.colorado.phet.movingman.MMFontManager;
import edu.colorado.phet.movingman.MovingManModule;
import edu.colorado.phet.movingman.common.DefaultDecimalFormat;
import edu.colorado.phet.movingman.common.LinearTransform1d;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: Sam Reid
 * Date: Jul 5, 2003
 * Time: 5:34:02 PM
 * To change this template use Options | File Templates.
 */
public class WalkWayGraphic extends CompositePhetGraphic {

    private WalkwayObjectGraphic treeObject;
    private WalkwayObjectGraphic houseObject;
    private WalkwayObjectGraphic leftWall;
    private WalkwayObjectGraphic rightWall;

    private Function.LinearFunction transform;
    private PhetShapeGraphic backgroundGraphic;
    private PhetShapeGraphic floorGraphic;
    private TickSetGraphic tickSetGraphic;
    private int floorHeight = 6;
    private Dimension size;
    private Paint backgroundColor = Color.yellow;
//    private PhetShapeGraphic tickSetBoundsGraphic;

    public WalkWayGraphic( MovingManModule module, ApparatusPanel apparatusPanel, int numTicks ) throws IOException {
        this( apparatusPanel, module, numTicks, -8, 8, new Function.LinearFunction( -10, 10, 0, 500 ) );
    }

    public WalkWayGraphic( ApparatusPanel panel, MovingManModule module, int numTickMarks, Function.LinearFunction transform ) throws IOException {
        this( panel, module, numTickMarks, -10, 10, transform );
    }

    public WalkWayGraphic( ApparatusPanel panel, final MovingManModule module,
                           int numTickMarks, double treex, double housex, Function.LinearFunction transform ) throws IOException {
        super( panel );
        this.transform = transform;
        this.treeObject = new WalkwayObjectGraphic( this, treex, "images/tree.gif" );
        this.houseObject = new WalkwayObjectGraphic( this, housex, "images/cottage.gif" );


        backgroundGraphic = new PhetShapeGraphic( panel, null, Color.white, new BasicStroke( 1.0f ), Color.black );
        floorGraphic = new PhetShapeGraphic( panel, null, Color.white );

        tickSetGraphic = new TickSetGraphic( panel, transform );

        BufferedImage barrierImage = ImageLoader.loadBufferedImage( "images/barrier.jpg" );
        barrierImage = BufferedImageUtils.rescaleYMaintainAspectRatio( panel, barrierImage, 130 );
        rightWall = new LeftEdgeWalkwayObjectGraphic( this, 10, barrierImage );
        leftWall = new RightEdgeWalkwayObjectGraphic( this, -10, barrierImage );

//        Rectangle bounds = new Rectangle( tickSetGraphic.getBounds() );
//        tickSetBoundsGraphic = new PhetShapeGraphic( panel, bounds, Color.green );
//        addGraphic( tickSetBoundsGraphic,10 );

        addGraphic( backgroundGraphic );
        addGraphic( floorGraphic );

        addGraphic( treeObject );
        addGraphic( houseObject );

        addGraphic( leftWall );
        addGraphic( rightWall );

        addGraphic( tickSetGraphic );
        setSize( 900, 150 );
        update();
    }

    public int getFloorY() {
        return size.height - floorHeight;
    }

    public void setSize( int width, int height ) {
        this.size = new Dimension( width, height );
        transform.setOutput( 0, width );
        update();
    }

    public void setTreeX( double treeX ) {
        treeObject.setModelLocation( treeX );
    }

    public void setHouseX( double houseX ) {
        this.houseObject.setModelLocation( houseX );
    }

    public int getViewCoordinate( double modelCoord ) {
        return (int)transform.evaluate( modelCoord );
    }

    public int getDistCeilToFloor() {
        return getHeight() - floorHeight;
    }

    public Paint getBackgroundColor() {
        return backgroundColor;
    }

    public void setTransform( LinearTransform1d transform ) {
        this.transform = transform;
        update();
    }

    public static class TickSetGraphic extends CompositePhetGraphic {

        private int numTickMarks = 11;
        private DefaultDecimalFormat format = new DefaultDecimalFormat( "##" );
        ArrayList graphicList = new ArrayList();

        public TickSetGraphic( Component component, Function.LinearFunction transform ) {
            super( component );

            double minValue = -10;
            double maxValue = -minValue;
            double modelRange = maxValue - minValue;
            double modelDX = modelRange / ( numTickMarks - 1 );

            for( int i = 0; i < numTickMarks; i++ ) {

                double modelx = minValue + i * modelDX;
                String str = format.format( modelx );
                if( str.equals( "0" ) ) {
                    str = "0 meters";
                }
                TickGraphic tickGraphic = new TickGraphic( component, modelx, transform, str );
                graphicList.add( tickGraphic );
                addGraphic( tickGraphic );
            }
        }

        public void update() {
            for( int i = 0; i < graphicList.size(); i++ ) {
                TickGraphic tickGraphic = (TickGraphic)graphicList.get( i );
                tickGraphic.update();
            }
        }

        public void setY( int y ) {
            for( int i = 0; i < graphicList.size(); i++ ) {
                TickGraphic tickGraphic = (TickGraphic)graphicList.get( i );
                tickGraphic.setY( y );
            }
        }
    }

    public static class TickGraphic extends CompositePhetGraphic {
        private double modelx;
        private Function.LinearFunction transform;
        private String text;
        private int y = 134;
        private PhetShapeGraphic shapeGraphic;
//        private Font font = new Font( "Lucida Sans", Font.BOLD, 16 );
        private Font font = MMFontManager.getFontSet().getWalkwayFont();
        private PhetTextGraphic textGraphic;

        public TickGraphic( Component component ) {
            super( component );
        }

        public TickGraphic( Component component, double modelx, Function.LinearFunction transform, String text ) {
            super( component );

            this.modelx = modelx;
            this.transform = transform;
            this.text = text;
            shapeGraphic = new PhetShapeGraphic( component, null, new BasicStroke( 1.0f ), Color.black );
            addGraphic( shapeGraphic );
            textGraphic = new PhetTextGraphic( component, font, text, Color.black, 0, 0 );
            addGraphic( textGraphic );
        }

        public void update() {
            int x = (int)transform.evaluate( modelx );
            int dy = 5;
            Line2D.Double line = new Line2D.Double( x, y, x, y + dy );
            shapeGraphic.setShape( line );
            textGraphic.setLocation( x - textGraphic.getWidth() / 2, y + dy );
        }

        public void setY( int y ) {
            this.y = y;
        }
    }

    private void update() {

        Rectangle skyRect = getSkyRect();
        backgroundGraphic.setShape( skyRect );
        Color lightBLue = new Color( 150, 120, 255 );
        backgroundGraphic.setPaint( new GradientPaint( skyRect.x, skyRect.y, lightBLue, skyRect.x, skyRect.y + skyRect.height, Color.white ) );

        Color root = new Color( 100, 100, 255 );
        Rectangle floor = getFloor();
        floorGraphic.setPaint( new GradientPaint( floor.x, floor.y, root, floor.x, floor.y + floor.height, Color.white ) );
        floorGraphic.setShape( floor );
        tickSetGraphic.setY( floor.y );
        tickSetGraphic.update();

        treeObject.update();
        houseObject.update();

        leftWall.update();
        rightWall.update();

//        Rectangle bounds = getTickSetBounds();
//        tickSetBoundsGraphic.setShape( new Ellipse2D.Double(50,50,50,50) );
//        tickSetBoundsGraphic.setShape( new Rectangle(0,100,getComponent().getWidth(), 10));
//        tickSetBoundsGraphic.autorepaint();

        setBoundsDirty();
        repaint();
    }

//    private Rectangle getTickSetBounds() {
//        Rectangle bounds = tickSetGraphic.getBounds();
//        bounds.x = -30;
//        bounds.width = getComponent().getWidth();
//        System.out.println( "bounds = " + bounds );
//        return bounds;
//    }

    private Rectangle getFloor() {
        int y = size.height - floorHeight;
        return new Rectangle( 0, y, size.width, size.height - y );
    }

    private Rectangle getSkyRect() {
        return new Rectangle( 0, 0, size.width, size.height );
    }

}
