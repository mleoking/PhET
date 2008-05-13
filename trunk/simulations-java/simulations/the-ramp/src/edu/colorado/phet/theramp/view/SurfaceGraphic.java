/*  */
package edu.colorado.phet.theramp.view;

import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;
import edu.colorado.phet.common.phetcommon.view.util.ImageLoader;
import edu.colorado.phet.common.phetcommon.view.util.MakeDuotoneImageOp;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.theramp.TheRampStrings;
import edu.colorado.phet.theramp.model.Surface;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PBounds;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.MessageFormat;

/**
 * User: Sam Reid
 * Date: Feb 11, 2005
 * Time: 10:17:00 AM
 */

public class SurfaceGraphic extends PNode {
    private RampPanel rampPanel;
    private Surface ramp;
    private ModelViewTransform2D screenTransform;
    private double viewAngle;
    private PImage surfaceGraphic;
    private PPath floorGraphic;
    private PPath bookStackGraphic;
    private int surfaceStrokeWidth = 12;
    private PPath filledShapeGraphic;
    private RampTickSetGraphic rampTickSetGraphic;
    private PText heightReadoutGraphic;
    private PPath heightExtentGraphic;
    private AngleGraphic angleGraphic;
    private BufferedImage texture;
    private Rectangle lastJackShape = null;

    public SurfaceGraphic( final RampPanel rampPanel, final Surface ramp ) {
        super();
        this.rampPanel = rampPanel;
        this.ramp = ramp;
        screenTransform = new ModelViewTransform2D( new Rectangle2D.Double( -10, 0, 20, 10 ), new Rectangle( -50, -50, 800, 400 ) );

        Stroke stroke = new BasicStroke( 6.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND );
        try {
            surfaceGraphic = new PImage( loadRampImage() );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
//        floorGraphic = new PhetShapeGraphic( getComponent(), null, stroke, Color.black );
        floorGraphic = new PPath( null, stroke );
        floorGraphic.setStrokePaint( Color.black );

        Paint bookFill = createBookFill();
//        bookStackGraphic = new PhetShapeGraphic( getComponent(), null, bookFill );
        bookStackGraphic = new PPath( null );
        bookStackGraphic.setStrokePaint( null );
        bookStackGraphic.setStroke( null );
        bookStackGraphic.setPaint( bookFill );

//        filledShapeGraphic = new PhetShapeGraphic( getComponent(), null, Color.lightGray );
        filledShapeGraphic = new PPath();
        filledShapeGraphic.setPaint( Color.lightGray );
        filledShapeGraphic.setVisible( false );

        addChild( filledShapeGraphic );
        addChild( floorGraphic );
        addChild( bookStackGraphic );
        addChild( surfaceGraphic );


        heightExtentGraphic = new PPath();
        heightExtentGraphic.setStroke( new BasicStroke( 2 ) );
        heightExtentGraphic.setStrokePaint( Color.black );
        heightExtentGraphic.setPaint( null );
        addChild( heightExtentGraphic );

//        heightReadoutGraphic = new PText( rampPanel, new Font( PhetDefaultFont.LUCIDA_SANS, 0, 14 ), "h=0.0 m", Color.black, 1, 1, Color.gray );
        heightReadoutGraphic = new PText( TheRampStrings.getString( "indicator.height-zero" ) );
        heightReadoutGraphic.setFont( new PhetFont( 18, true ) );
        heightReadoutGraphic.setPaint( SkyGraphic.lightBlue );
        addChild( heightReadoutGraphic );

        surfaceGraphic.addInputEventListener( new PBasicInputEventHandler() {
            public void mouseDragged( PInputEvent event ) {
                SurfaceGraphic.this.mouseDragged( event );
            }
        } );
        bookStackGraphic.addInputEventListener( new PBasicInputEventHandler() {
            public void mouseDragged( PInputEvent event ) {
                SurfaceGraphic.this.mouseDragged( event );
            }
        } );
        surfaceGraphic.addInputEventListener( new CursorHandler( Cursor.HAND_CURSOR ) );
        bookStackGraphic.addInputEventListener( new CursorHandler( Cursor.HAND_CURSOR ) );

        rampTickSetGraphic = new RampTickSetGraphic( this );
        addChild( rampTickSetGraphic );

        angleGraphic = new AngleGraphic( this );
        addChild( angleGraphic );

        updateRamp();
        ramp.addObserver( new SimpleObserver() {
            public void update() {
                updateRamp();
            }
        } );
    }

    private BufferedImage loadRampImage() throws IOException {
        return ImageLoader.loadBufferedImage( "the-ramp/images/wood5.png" );
    }

    public PText getHeightReadoutGraphic() {
        return heightReadoutGraphic;
    }

    private void mouseDragged( PInputEvent pInputEvent ) {
        Point2D pt = pInputEvent.getCanvasPosition();
//        Point2D o=getViewOrigin();
        Point2D o = getViewOrigin();
        localToGlobal( o );
        rampPanel.getCamera().globalToLocal( o );
//        Point2D pt = pInputEvent.getPositionRelativeTo( this );
////        localToGlobal( pt );
//        Point2D o = ramp.getOrigin();
////        localToGlobal( o );
////        pt = getRampWorld().convertToWorld( pt );
//        System.out.println( "o = " + o );
//        System.out.println( "pt = " + pt );
        Vector2D.Double vec = new Vector2D.Double( o, pt );

        System.out.println( "vec = " + vec );
        double angle = -vec.getAngle();
        System.out.println( "angle = " + angle );
        angle = MathUtil.clamp( 0, angle, Math.PI / 2.0 );
        ramp.setAngle( angle );
        rampPanel.getRampModule().record();
    }

    private RampWorld getRampWorld() {
        return rampPanel.getRampWorld();
    }

    private Point getEndLocation() {
        return getViewLocation( ramp.getLocation( ramp.getLength() ) );
    }

    private Paint createBookFill() {
        try {
//            texture = ImageLoader.loadBufferedImage( "the-ramp/images/bookstack3.png" );
            texture = ImageLoader.loadBufferedImage( "the-ramp/images/bookstack3.gif" );
//            Point rampEnd = getViewLocation( ramp.getLocation( ramp.getLength() * 0.8 ) );
            Point rampEnd = getEndLocation();
//            System.out.println( "texture = " + texture );
            return new TexturePaint( texture, new Rectangle2D.Double( rampEnd.x - texture.getWidth() / 2, rampEnd.y, texture.getWidth(), texture.getHeight() ) );
        }
        catch( IOException e ) {
            e.printStackTrace();
            throw new RuntimeException( e );
        }

    }

    public PImage getSurfaceGraphic() {
        return surfaceGraphic;
    }

    public AngleGraphic getAngleGraphic() {
        return angleGraphic;
    }

    public void paintRed() {
        if( RampPanel.redRampEnabled ) {
            surfaceGraphic.setImage( new MakeDuotoneImageOp( new Color( 255, 0, 0, 32 ) ).filter( (BufferedImage)surfaceGraphic.getImage(), null ) );
        }
    }

    public void restoreOriginalImage() {
        try {
            surfaceGraphic.setImage( loadRampImage() );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
    }

    public static class ImageDebugFrame extends JFrame {
        public JLabel label;

        public ImageDebugFrame( Image im ) {
            label = new JLabel( new ImageIcon( im ) );
            setContentPane( label );
            setImage( im );
            pack();
        }

        public void setImage( Image image ) {
            label.setIcon( new ImageIcon( image ) );
        }
    }

    private Point getViewOrigin() {
        Point2D modelOrigin = ramp.getOrigin();
        return screenTransform.modelToView( modelOrigin );
    }

    private void updateRamp() {

        Point viewOrigin = getViewOrigin();
        Point2D modelDst = ramp.getEndPoint();
        Point viewDst = screenTransform.modelToView( modelDst );
        viewAngle = Math.atan2( viewDst.y - viewOrigin.y, viewDst.x - viewOrigin.x );
        surfaceGraphic.setOffset( getViewOrigin() );
        surfaceGraphic.setRotation( viewAngle );

        //todo scale the graphic to fit the length.
        double cur_im_width_model = screenTransform.viewToModelDifferentialX( surfaceGraphic.getImage().getWidth( null ) );

        surfaceGraphic.setScale( ramp.getLength() / cur_im_width_model );
        Point p2 = new Point( viewDst.x, viewOrigin.y );
        Line2D.Double floor = new Line2D.Double( viewOrigin, p2 );
        Rectangle jackShape = createJackArea();
        if( lastJackShape == null || !jackShape.equals( lastJackShape ) ) {
            bookStackGraphic.setPathTo( jackShape );
            bookStackGraphic.setPaint( createBookFill() );
            bookStackGraphic.setVisible( ramp.getAngle() * 360 / 2 / Math.PI < 85 );
            lastJackShape = jackShape;
        }

        DoubleGeneralPath path = new DoubleGeneralPath( viewOrigin );
        path.lineTo( floor.getP2() );
        path.lineTo( viewDst );
        path.closePath();
        filledShapeGraphic.setPathTo( path.getGeneralPath() );

        heightReadoutGraphic.setOffset( (int)( jackShape.getBounds().getMaxX() + 5 ), jackShape.getBounds().y + jackShape.getBounds().height / 2 );

        PBounds bounds = heightReadoutGraphic.getGlobalFullBounds();
        globalToLocal( bounds );

        heightExtentGraphic.setPathToPolyline( new Point2D[]{
                new Point2D.Double( bounds.getCenterX() - 5, floor.getP2().getY() ),
                new Point2D.Double( bounds.getCenterX() + 5, floor.getP2().getY() ),
                new Point2D.Double( bounds.getCenterX(), floor.getP2().getY() ),
                new Point2D.Double( bounds.getCenterX(), jackShape.getY() ),
                new Point2D.Double( bounds.getCenterX() + 5, jackShape.getY() ),
                new Point2D.Double( bounds.getCenterX() - 5, jackShape.getY() )
        } );
        heightExtentGraphic.setVisible( ramp.getHeight() > 0.4 );
        double height = ramp.getHeight();
        String heightStr = new DecimalFormat( "0.0" ).format( height );
        heightReadoutGraphic.setText( MessageFormat.format( TheRampStrings.getString( "indicator.height-meters" ), new Object[]{heightStr} ) );

        rampTickSetGraphic.update();
        angleGraphic.update();

    }

    private Rectangle createJackArea() {
        Point rampStart = getViewLocation( ramp.getLocation( 0 ) );
        Point rampEnd = getViewLocation( ramp.getLocation( ramp.getLength() ) );

        return new Rectangle( rampEnd.x - texture.getWidth() / 2, (int)( rampEnd.y + surfaceGraphic.getImage().getHeight( null ) * 0.75 ), texture.getWidth(), rampStart.y - rampEnd.y );
    }

    GeneralPath createJackLine() {
        Point rampStart = getViewLocation( ramp.getLocation( 0 ) );
        Point rampEnd = getViewLocation( ramp.getLocation( ramp.getLength() ) );

        DoubleGeneralPath path = new DoubleGeneralPath( new Point( rampEnd.x, rampStart.y ) );
        path.lineTo( new Point( rampEnd.x, rampEnd.y ) );
        return path.getGeneralPath();
    }

    public double getViewAngle() {
        return viewAngle;
    }

    public ModelViewTransform2D getScreenTransform() {
        return screenTransform;
    }

    public Surface getSurface() {
        return ramp;
    }

    public int getSurfaceStrokeWidth() {
        return surfaceStrokeWidth;
    }

    public Point getViewLocation( Point2D location ) {
//        return getRampWorld().convertToWorld( viewLoc );
        return getScreenTransform().modelToView( location );
    }

    public Point getViewLocation( double rampDist ) {
        return getViewLocation( ramp.getLocation( rampDist ) );
    }

    /**
     * Create the AffineTransform that will put an object of size: dim centered along the ramp at position dist
     */
    public AffineTransform createTransform( double dist, Dimension dim ) {
        Point viewLoc = getViewLocation( ramp.getLocation( dist ) );
        AffineTransform transform = new AffineTransform();
        transform.translate( viewLoc.x, viewLoc.y );
//        transform.rotate( getViewAngle(), dim.width / 2, dim.height / 2 );
        transform.rotate( getViewAngle() );
//        transform.translate( 0, -dim.height );
        int onRamp = 7;
        transform.translate( -dim.width / 2, -dim.height + onRamp );
//        transform.translate( 0, -dim.height + onRamp );
        return transform;
    }

    public int getImageHeight() {
        return surfaceGraphic.getImage().getHeight( null );
    }
}
