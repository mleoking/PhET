/**
 * Class: EmfPanel
 * Package: edu.colorado.phet.waves.view
 * Author: Another Guy
 * Date: May 23, 2003
 */
package edu.colorado.phet.emf.view;

import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.common.view.graphics.Graphic;
import edu.colorado.phet.common.view.graphics.shapes.Arrow;
import edu.colorado.phet.common.view.util.GraphicsUtil;
import edu.colorado.phet.common.view.util.ImageLoader;
import edu.colorado.phet.emf.model.Electron;
import edu.colorado.phet.emf.model.EmfModel;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class EmfPanel extends ApparatusPanel {

    private FieldLatticeView fieldLatticeView;
    private Dimension size = new Dimension();
    private BufferedImage bi;
    private boolean useBufferedImage = false;

    public void setUseBufferedImage( boolean useBufferedImage ) {
        this.useBufferedImage = useBufferedImage;
    }

    public EmfPanel( EmfModel model, Electron electron, final Point origin, int fieldWidth, int fieldHeight ) {

        EmfPanel.setInstance( this );

        // Add the field lattice
//        int latticeSpacingX = 20;
//        int latticeSpacingY = 20;
//                int latticeSpacingX = 2;
//                int latticeSpacingY = 2;

//        int latticeSpacingX = 10;
//        int latticeSpacingY = 10;
//        int latticeSpacingX = 25;
//        int latticeSpacingY = 25;
        int latticeSpacingX = 50;
        int latticeSpacingY = 50;
        fieldLatticeView = new FieldLatticeView( electron,
                                                 origin,
                                                 fieldWidth - latticeSpacingX, fieldHeight,
                                                 latticeSpacingX,
                                                 latticeSpacingY );
        addGraphic( fieldLatticeView, 4 );

        // Add the background
        final BufferedImage im;
        try {
            im = GraphicsUtil.toBufferedImage( ImageLoader.loadBufferedImage( "images/background.gif" ) );
            fieldLatticeView.paintDots( im.getGraphics() );
            addGraphic( new Graphic() {
                public void paint( Graphics2D g ) {
                    g.drawImage( im, 0, 0, EmfPanel.this );
                }
            }, 0 );
            this.setPreferredSize( new Dimension( im.getWidth(), im.getHeight() ) );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
    }

    public void setFieldCurvesVisible( boolean enabled ) {
        fieldLatticeView.setFieldCurvesEnabled( enabled );
    }

    protected void paintComponent( Graphics graphics ) {
        if( useBufferedImage ) {
            if( size.getWidth() != this.getSize().getWidth()
                    || size.getHeight() != this.getSize().getHeight() ) {
                size.setSize( this.getSize() );
                bi = new BufferedImage( (int)size.getWidth(), (int)size.getHeight(), BufferedImage.TYPE_INT_RGB );
            }
            Graphics gBI = bi.getGraphics();
            super.paintComponent( gBI );
            graphics.drawImage( bi, 0, 0, this );
            gBI.dispose();
        }
        else {
            super.paintComponent( graphics );
        }
    }

    //
    // Static fields and methods
    //
    private static EmfPanel s_instance;
    public static int NO_FIELD = 1;
    public static int FULL_FIELD = 2;
    public static int CURVE = 3;
    public static int CURVE_WITH_VECTORS = 4;
    private static int s_latticePtDiam = 5;
    private static BufferedImage s_latticePtImg = new BufferedImage( s_latticePtDiam,
                                                                     s_latticePtDiam,
                                                                     BufferedImage.TYPE_INT_ARGB );

    private static void setInstance( EmfPanel panel ) {
        s_instance = panel;
    }

    public static EmfPanel instance() {
        return s_instance;
    }

    public void setAutoscaleEnabled( boolean enabled ) {
        fieldLatticeView.setAutoscaleEnabled( enabled );
    }

    public void displayStaticField( boolean display ) {
        fieldLatticeView.setDisplayStaticField( display );
    }

    public void displayDynamicField( boolean display ) {
        fieldLatticeView.setDisplayDynamicField( display );
    }

    public void setFieldSense( int fieldSense ) {
        fieldLatticeView.setFieldSense( fieldSense );
    }

    public void setFieldDisplay( int display ) {
        fieldLatticeView.setDisplay( display );
    }

    static {     // Create a graphics context on the buffered image
        Graphics2D g2d = s_latticePtImg.createGraphics();

        // Draw on the image
        g2d.setColor( Color.blue );
        g2d.drawArc( 0, 0,
                     2, 2,
                     0, 360 );
        g2d.dispose();
    }

}
