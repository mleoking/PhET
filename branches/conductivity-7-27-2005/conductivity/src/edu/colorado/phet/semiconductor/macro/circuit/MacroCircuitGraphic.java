// Decompiled by Jad v1.5.8f. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package edu.colorado.phet.semiconductor.macro.circuit;

import edu.colorado.phet.common.math.PhetVector;
import edu.colorado.phet.common.view.graphics.Graphic;
import edu.colorado.phet.common.view.graphics.ShapeGraphic;
import edu.colorado.phet.common.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.view.graphics.transforms.TransformListener;
import edu.colorado.phet.common.view.util.graphics.ImageLoader;
import edu.colorado.phet.semiconductor.common.StretchedBufferedImage;
import edu.colorado.phet.semiconductor.macro.battery.Battery;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

// Referenced classes of package edu.colorado.phet.semiconductor.macro.circuit:
//            Wire, Resistor, MacroCircuit, LinearBranch

public class MacroCircuitGraphic {

    public MacroCircuitGraphic( MacroCircuit macrocircuit, ModelViewTransform2D modelviewtransform2d )
            throws IOException {
        wireGraphics = new ArrayList();
        circuit = macrocircuit;
        transform = modelviewtransform2d;
        init();
    }

    void init()
            throws IOException {
        for( int i = 0; i < circuit.numBranches(); i++ ) {
            final LinearBranch b = circuit.wireAt( i );
            if( b instanceof Wire ) {
                java.awt.geom.Line2D.Double double1 = new java.awt.geom.Line2D.Double( b.getStartPosition().toPoint2D(), b.getEndPosition().toPoint2D() );
                Color color = new Color( 240, 130, 150 );
                final ShapeGraphic sg = new ShapeGraphic( double1, color, new BasicStroke( getParticleImage().getWidth() + 4 ) );
                wireGraphics.add( sg );
                final java.awt.geom.Line2D.Double sh1 = double1;
                transform.addTransformListener( new TransformListener() {

                    public void transformChanged( ModelViewTransform2D modelviewtransform2d ) {
                        Shape shape = transform.toAffineTransform().createTransformedShape( sh1 );
                        sg.setShape( shape );
                    }

                } );
            }
            else if( b instanceof Resistor ) {
                java.awt.geom.Line2D.Double double2 = new java.awt.geom.Line2D.Double( b.getStartPosition().toPoint2D(), b.getEndPosition().toPoint2D() );
                resistorGraphic = new ShapeGraphic( double2, Color.yellow, new BasicStroke( getParticleImage().getWidth() * 4 ) );
                final java.awt.geom.Line2D.Double sh1 = double2;
                transform.addTransformListener( new TransformListener() {

                    public void transformChanged( ModelViewTransform2D modelviewtransform2d ) {
                        Shape shape = transform.toAffineTransform().createTransformedShape( sh1 );
                        resistorGraphic.setShape( shape );
                    }

                } );
            }
            else if( b instanceof Battery ) {
                BufferedImage bufferedimage = getBatteryImage();
                final StretchedBufferedImage sbi = new StretchedBufferedImage( bufferedimage, new Rectangle( 0, 0, 100, 100 ) );
                battGraphic = sbi;
                transform.addTransformListener( new TransformListener() {

                    public void transformChanged( ModelViewTransform2D modelviewtransform2d ) {
                        PhetVector phetvector = b.getEndPosition();
                        PhetVector phetvector1 = b.getStartPosition();
                        int j = batteryImage.getHeight();
                        int k = transform.modelToViewX( phetvector.getX() );
                        int l = transform.modelToViewX( phetvector1.getX() ) - k;
                        int i1 = transform.modelToViewY( phetvector.getY() );
                        Rectangle rectangle = new Rectangle( k, i1 - j / 2, l, j );
                        sbi.setOutputRect( rectangle );
                    }

                } );
            }
        }

    }

    private BufferedImage getBatteryImage()
            throws IOException {
        if( batteryImage != null ) {
            return batteryImage;
        }
        else {
            loader.setPhetLoader();
            batteryImage = loader.loadBufferedImage( "images/AA-battery.gif" );
            return batteryImage;
        }
    }

    public static BufferedImage getParticleImage()
            throws IOException {
        if( particleImage != null ) {
            return particleImage;
        }
        else {
            loader.setPhetLoader();
            ImageLoader _tmp = loader;
            particleImage = ImageLoader.loadBufferedImage( "images/particle-blue-sml.gif" );
            return particleImage;
        }
    }

    public int numWireGraphics() {
        return wireGraphics.size();
    }

    public Graphic wireGraphicAt( int i ) {
        return (Graphic)wireGraphics.get( i );
    }

    public ShapeGraphic getResistorGraphic() {
        return resistorGraphic;
    }

    public StretchedBufferedImage getBatteryGraphic() {
        return battGraphic;
    }

    private MacroCircuit circuit;
    private ModelViewTransform2D transform;
    ArrayList wireGraphics;
    ShapeGraphic resistorGraphic;
    StretchedBufferedImage battGraphic;
    private BufferedImage batteryImage;
    private static BufferedImage particleImage;
    private static ImageLoader loader = new ImageLoader();


}
