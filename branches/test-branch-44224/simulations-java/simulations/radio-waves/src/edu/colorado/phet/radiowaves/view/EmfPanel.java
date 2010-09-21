/**
 * Class: EmfPanel Package: edu.colorado.phet.waves.view Author: Another Guy
 * Date: May 23, 2003
 */

package edu.colorado.phet.radiowaves.view;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.TransformListener;
import edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils;
import edu.colorado.phet.common.phetcommon.view.util.ImageLoader;
import edu.colorado.phet.radiowaves.common_1200.graphics.ApparatusPanel;
import edu.colorado.phet.radiowaves.common_1200.graphics.BufferedImageGraphic;
import edu.colorado.phet.radiowaves.model.Electron;

public class EmfPanel extends ApparatusPanel implements TransformListener {

    //----------------------------------------------------------------
    // Static fields and methods
    //----------------------------------------------------------------
    private static EmfPanel s_instance;
    public static final int NO_FIELD = 1;
    public static final int FULL_FIELD = 2;
    public static final int CURVE = 3;
    public static final int CURVE_WITH_VECTORS = 4;
    public static final int ALPHA_FOR_MAGNITUDE = 5;
    public static final int VECTORS_CENTERED_ON_X_AXIS = 5;

    private static void setInstance( EmfPanel panel ) {
        s_instance = panel;
    }

    public static EmfPanel instance() {
        return s_instance;
    }

    //----------------------------------------------------------------
    // Instance fields and methods
    //----------------------------------------------------------------

    private FieldLatticeView fieldLatticeView;
    private Dimension size = new Dimension();
    private BufferedImage bi;
    private boolean useBufferedImage = false;
    private AffineTransform atx;
    private BufferedImageGraphic backgroundImg;

    public void setUseBufferedImage( boolean useBufferedImage ) {
        this.useBufferedImage = useBufferedImage;
    }

    public EmfPanel( Electron electron, final Point origin, int fieldWidth, int fieldHeight ) {

        EmfPanel.setInstance( this );

        // Add the field lattice
        int latticeSpacingX = 50;
        int latticeSpacingY = 50;
        fieldLatticeView = new FieldLatticeView( electron, origin, fieldWidth - latticeSpacingX, fieldHeight, latticeSpacingX, latticeSpacingY, this );
        addGraphic( fieldLatticeView, 4 );

        // Add the background
        final BufferedImage im;
        try {
            im = BufferedImageUtils.toBufferedImage( ImageLoader.loadBufferedImage( "radio-waves/images/background.gif" ) );
            this.setPreferredSize( new Dimension( im.getWidth(), im.getHeight() ) );
            backgroundImg = new BufferedImageGraphic( im);
            addGraphic( backgroundImg, 0 );
        }
        catch ( IOException e ) {
            e.printStackTrace();
        }
    }

    protected void paintComponent( Graphics graphics ) {
        if ( useBufferedImage ) {
            if ( size.getWidth() != this.getSize().getWidth() || size.getHeight() != this.getSize().getHeight() ) {
                size.setSize( this.getSize() );
                bi = new BufferedImage( (int) size.getWidth(), (int) size.getHeight(), BufferedImage.TYPE_INT_RGB );
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

    public void transformChanged( ModelViewTransform2D mvt ) {
        atx = mvt.getAffineTransform();
        backgroundImg.setTransform( atx );
        fieldLatticeView.setTransform( atx );
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

    public void setCurveVisible( boolean visible ) {
        fieldLatticeView.setCurveVisible( visible );
    }
}
