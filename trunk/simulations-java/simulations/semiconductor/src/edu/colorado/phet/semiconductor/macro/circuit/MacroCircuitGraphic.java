package edu.colorado.phet.semiconductor.macro.circuit;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

import edu.colorado.phet.semiconductor.common.StretchedBufferedImage;
import edu.colorado.phet.semiconductor.macro.circuit.battery.Battery;
import edu.colorado.phet.semiconductor.macro.circuit.battery.BatteryListener;
import edu.colorado.phet.semiconductor.phetcommon.math.PhetVector;
import edu.colorado.phet.semiconductor.phetcommon.view.graphics.Graphic;
import edu.colorado.phet.semiconductor.phetcommon.view.graphics.ShapeGraphic;
import edu.colorado.phet.semiconductor.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.semiconductor.phetcommon.view.graphics.transforms.TransformListener;
import edu.colorado.phet.semiconductor.phetcommon.view.util.graphics.ImageLoader;

/**
 * User: Sam Reid
 * Date: Jan 16, 2004
 * Time: 5:42:34 PM
 */
public class MacroCircuitGraphic implements Graphic, BatteryListener {
    public static final Color COPPER = new Color( Integer.parseInt( "D98719", 16 ) );//new Color(214, 18, 34);
    private MacroCircuit circuit;
    private ModelViewTransform2D transform;
    ArrayList wireGraphics = new ArrayList();
    ShapeGraphic resistorGraphic;
    StretchedBufferedImage battGraphic;
    private BufferedImage batteryImage;
    private static BufferedImage particleImage;
    private static ImageLoader loader = new ImageLoader();

    public MacroCircuitGraphic( MacroCircuit circuit, ModelViewTransform2D transform ) throws IOException {
        this.circuit = circuit;
        this.transform = transform;
        init();
        circuit.getBattery().addBatteryListener( this );
    }

    void init() throws IOException {
        for ( int i = 0; i < circuit.numBranches(); i++ ) {
            final LinearBranch b = circuit.wireAt( i );
            if ( b instanceof Wire ) {
                Shape sh = new Line2D.Double( b.getStartPosition().toPoint2D(), b.getEndPosition().toPoint2D() );
//            sh=transform.toAffineTransform().createTransformedShape(sh);
                Color wireColor = MacroCircuitGraphic.COPPER;
                final ShapeGraphic sg = new ShapeGraphic( sh, wireColor, new BasicStroke( getParticleImage().getWidth() + 4 ) );
                wireGraphics.add( sg );
                final Shape sh1 = sh;
                transform.addTransformListener( new TransformListener() {
                    public void transformChanged( ModelViewTransform2D modelViewTransform2D ) {
                        //To change body of implemented methods use Options | File Templates.
//                    System.out.println("sh1 = " + sh1);
                        Shape trf = transform.toAffineTransform().createTransformedShape( sh1 );
                        sg.setShape( trf );
                    }
                } );
            }
            else if ( b instanceof Resistor ) {

                final Shape sh = new Line2D.Double( b.getStartPosition().toPoint2D(), b.getEndPosition().toPoint2D() );
                int viewWidth = transform.modelToViewY( circuit.getResistor().getHeight() );
                Stroke stroke = new BasicStroke( viewWidth );
//                final Shape square=stroke.createStrokedShape(sh);
//            sh=transform.toAffineTransform().createTransformedShape(sh);
                this.resistorGraphic = new ShapeGraphic( sh, Color.yellow, stroke );

//                final Shape sh1 = sh;
                transform.addTransformListener( new TransformListener() {
                    public void transformChanged( ModelViewTransform2D modelViewTransform2D ) {
                        //To change body of implemented methods use Options | File Templates.
//                    System.out.println("sh1 = " + sh1);
                        Shape trf = transform.toAffineTransform().createTransformedShape( sh );
                        resistorGraphic.setShape( trf );
                    }
                } );
            }
            else if ( b instanceof Battery ) {
                BufferedImage battIm = getBatteryImage();
                final StretchedBufferedImage sbi = new StretchedBufferedImage( battIm, new Rectangle( 0, 0, 100, 100 ) );
                this.battGraphic = sbi;
                transform.addTransformListener( new TransformListener() {
                    public void transformChanged( ModelViewTransform2D modelViewTransform2D ) {
                        //To change body of implemented methods use Options | File Templates.
                        PhetVector start = b.getEndPosition();
                        PhetVector end = b.getStartPosition();
                        int height = batteryImage.getHeight();
                        int x = transform.modelToViewX( start.getX() );
                        int width = ( transform.modelToViewX( end.getX() ) - x );
                        int y = transform.modelToViewY( start.getY() );
                        Rectangle outputRect = new Rectangle( x, y - height / 2, width, height );
                        sbi.setOutputRect( outputRect );
                    }
                } );
            }
        }
    }

    private BufferedImage getBatteryImage() throws IOException {
        if ( batteryImage != null ) {
            return batteryImage;
        }
        else {
            loader.setPhetLoader();
            batteryImage = ImageLoader.loadBufferedImage( "semiconductor/images/AA-battery.gif" );
            return batteryImage;
        }
    }

    public static BufferedImage getParticleImage() throws IOException {
        if ( particleImage != null ) {
            return particleImage;
        }
        else {
            loader.setPhetLoader();
            particleImage = loader.loadBufferedImage( "semiconductor/images/particle-blue-sml.gif" );
            return particleImage;
        }
    }

    public int numWireGraphics() {
        return wireGraphics.size();
    }

    public Graphic wireGraphicAt( int i ) {
        return (Graphic) wireGraphics.get( i );
    }

    public void paint( Graphics2D graphics2D ) {
        for ( int i = 0; i < numWireGraphics(); i++ ) {
            wireGraphicAt( i ).paint( graphics2D );
        }
        battGraphic.paint( graphics2D );
        resistorGraphic.paint( graphics2D );
    }

    public void voltageChanged( Battery source ) {
        if ( source.getVoltage() < 0 ) {
            battGraphic.setFlipX( false );
        }
        else {
            battGraphic.setFlipX( true );
        }
    }

}
