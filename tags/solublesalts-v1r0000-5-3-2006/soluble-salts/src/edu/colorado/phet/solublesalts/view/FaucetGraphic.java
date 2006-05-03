/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.solublesalts.view;

import edu.colorado.phet.common.view.util.ImageLoader;
import edu.colorado.phet.piccolo.PhetPCanvas;
import edu.colorado.phet.piccolo.nodes.RegisterablePNode;
import edu.colorado.phet.solublesalts.SolubleSaltsConfig;
import edu.colorado.phet.solublesalts.model.Spigot;
import edu.colorado.phet.solublesalts.model.WaterSource;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolox.pswing.PSwing;
import edu.umd.cs.piccolox.pswing.PSwingCanvas;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.IOException;


/**
 * An interactive graphic to represent the faucet
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class FaucetGraphic extends RegisterablePNode implements WaterSource.ChangeListener{

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------

    // Orientations
    public static final int LEFT_FACING = 1, RIGHT_FACING = 2;
    // Registrations
    public static final int WALL_ATTACHMENT = 1, SPOUT = 2;
    private static final double MAX_WATER_WIDTH = 20.0;
    private static final Color WATER_COLOR = SolubleSaltsConfig.WATER_COLOR;

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private Rectangle waterShape;
    private Spigot spigot;
    private double streamMaxY;
    private PPath waterGraphic;
    private JSlider flowSlider;

    //----------------------------------------------------------------------------
    // Constructors & finalizers
    //----------------------------------------------------------------------------

    /**
     * Sole constructor.
     */
    public FaucetGraphic( PSwingCanvas pSwingCanvas,
                          int orientation,
                          int registration,
                          Spigot spigot,
                          double streamMaxY ) {
        spigot.addChangeListener( this );
        this.spigot = spigot;
        this.streamMaxY = streamMaxY;

        // Faucet
        BufferedImage bImg = null;
        try {
            bImg = ImageLoader.loadBufferedImage( SolubleSaltsConfig.FAUCET_IMAGE );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }

        Point2D waterGraphicLocation = null;
        switch( registration ) {
            case SPOUT:
                this.setRegistrationPoint( 13, 77 );
                break;
            case WALL_ATTACHMENT:
                this.setRegistrationPoint( bImg.getWidth(), 40 );
                waterGraphicLocation = new Point2D.Double( 12, 77 );
                break;
            default:
                throw new RuntimeException( "Invalid registration" );
        }

        // If the faucet is facing right, flip the image and adjust the location of the water and the
        // registration pt.
        if( orientation == RIGHT_FACING ) {
            AffineTransform atx = AffineTransform.getScaleInstance( -1, 1 );
            atx.translate( -bImg.getWidth( null ), 0 );
            AffineTransformOp atxOp = new AffineTransformOp( atx, AffineTransformOp.TYPE_BILINEAR );
            waterGraphicLocation = new Point2D.Double( getRegistrationPoint().getX(),
                                                       getRegistrationPoint().getY() );
            waterGraphicLocation = atx.transform( waterGraphicLocation, null );
            bImg = atxOp.filter( bImg, null );
            setRegistrationPoint( bImg.getWidth() - getRegistrationPoint().getX(),
                                  getRegistrationPoint().getY() );
        }
        PImage faucetImage = new PImage( bImg );
        addChild( faucetImage );

        // Water
        waterShape = new Rectangle( 0, 0, 0, 0 );
        waterGraphic = new PPath( waterShape );
        waterGraphic.setOffset( waterGraphicLocation );
        waterGraphic.setPaint( WATER_COLOR );
        waterGraphic.setStrokePaint( null );
        addChild( waterGraphic );

        // Water Flow slider
        flowSlider = new JSlider( 0, (int)spigot.getMaxFlow(), 0 );
        flowSlider.setBackground( Color.black );
        flowSlider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                FaucetGraphic.this.spigot.setFlow( flowSlider.getValue() );
            }
        } );
        // Add a listener that will shut off the faucet when the mouse is released
        flowSlider.addMouseListener( new MouseAdapter() {
            public void mouseReleased( MouseEvent e ) {
                flowSlider.setValue( 0 );
            }
        } );

        flowSlider.setPreferredSize( new Dimension( (int)faucetImage.getWidth() / 2, 15 ) );
        PSwing pSwing = new PSwing( pSwingCanvas, flowSlider );
        pSwing.setOffset( 22, 35 );
        pSwing.addInputEventListener( new PBasicInputEventHandler() {
            public void mouseEntered( PInputEvent event ) {
                PhetPCanvas ppc = (PhetPCanvas)event.getComponent();
                ppc.setCursor( new Cursor( Cursor.W_RESIZE_CURSOR ) );
            }

            public void mouseExited( PInputEvent event ) {
                PhetPCanvas ppc = (PhetPCanvas)event.getComponent();
                ppc.setCursor( Cursor.getDefaultCursor() );
            }

        } );
        addChild( pSwing );

        update();
    }

    //----------------------------------------------------------------------------
    // Faucet.ChangeListener implementation
    //----------------------------------------------------------------------------

    public void stateChanged( WaterSource.ChangeEvent event ) {
        update();
    }

    /*
     * @see edu.colorado.phet.common.util.SimpleObserver#update()
     */
    public void update() {
        double waterWidth = Math.abs( spigot.getFlow() * ( MAX_WATER_WIDTH ) / spigot.getMaxFlow() );
        waterShape.setBounds( -( (int)waterWidth / 2 ), 0, (int)waterWidth,
                              (int)( ( streamMaxY - getYOffset() ) / getScale() ) );
        waterGraphic.setPathTo( waterShape );
        repaint();
    }
}