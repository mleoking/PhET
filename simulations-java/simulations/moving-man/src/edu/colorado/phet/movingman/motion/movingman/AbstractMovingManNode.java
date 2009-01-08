package edu.colorado.phet.movingman.motion.movingman;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.util.DefaultDecimalFormat;
import edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.util.PImageFactory;
import edu.colorado.phet.movingman.MovingManResources;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * Created by: Sam
 * Dec 4, 2007 at 1:34:58 PM
 */
public class AbstractMovingManNode extends PNode {
    private PImage manImage;
    private double modelWidth;
    private int screenWidth;
    private double sign = +1.0;
    private ArrayList tickTextList = new ArrayList();

    private BufferedImage left = MovingManResources.loadBufferedImage( "left-ii.gif" );
    private BufferedImage right = BufferedImageUtils.flipX( MovingManResources.loadBufferedImage( "left-ii.gif" ) );
    private BufferedImage standing = BufferedImageUtils.rescaleYMaintainAspectRatio( MovingManResources.loadBufferedImage( "stand-ii.gif" ), left.getHeight() );
    private PImage leftWall;
    private PImage rightWall;

    public AbstractMovingManNode() throws IOException {
        Rectangle2D.Float skyRect = new Rectangle2D.Float( -20, 0, 40, 2 );
        GradientPaint skyPaint = new GradientPaint( skyRect.x, skyRect.y, new Color( 150, 120, 255 ), skyRect.x, skyRect.y + skyRect.height, Color.white );
        PhetPPath skyNode = new PhetPPath( skyRect, skyPaint );
        addChild( skyNode );

        PhetPPath yellowBackground = new PhetPPath( new Rectangle2D.Double( -20, 2, 40, 0.6 ), Color.yellow );
        addChild( yellowBackground );

        Rectangle2D.Float floorRect = new Rectangle2D.Float( -20, 2, 40, 0.1f );
        Paint floorPaint = new GradientPaint( floorRect.x, floorRect.y, new Color( 100, 100, 255 ), floorRect.x, floorRect.y + floorRect.height, Color.white );
        PhetPPath floorNode = new PhetPPath( floorRect, floorPaint );
        addChild( floorNode );

        DecimalFormat format = new DefaultDecimalFormat( "0" );
        for ( int i = -10; i <= 10; i += 2 ) {
            TickLabel tickLabel = new TickLabel( format, i );
            tickTextList.add( tickLabel );

            PPath tickNode = new PhetPPath( new Line2D.Double( 0, 0, 0, -0.2 ), new BasicStroke( 0.1f / 2 ), Color.black );
            tickNode.setOffset( i, 2 + 0.1 );


            addChild( tickLabel );
            addChild( tickNode );
        }
        PImage tree = PImageFactory.create( "moving-man/images/tree.gif" );
        tree.translate( -8, 2 - 2.1 );
        tree.scale( 2.1 / tree.getHeight() );
        tree.translate( -tree.getFullBounds().getWidth() / 2.0 / tree.getScale(), 0 );
        addChild( tree );

        PImage house = PImageFactory.create( "moving-man/images/cottage.gif" );
        house.translate( 8, 2 - 1.8 );
        house.scale( 1.8 / house.getHeight() );
        house.translate( -house.getFullBounds().getWidth() / 2.0 / house.getScale(), 0 );
        addChild( house );


        leftWall = PImageFactory.create( "moving-man/images/barrier.jpg" );
        leftWall.translate( -10, 0 );
        leftWall.scale( 2.0 / leftWall.getHeight() );
        leftWall.translate( -leftWall.getFullBounds().getWidth() / leftWall.getScale(), 0 );
        addChild( leftWall );

        rightWall = PImageFactory.create( "moving-man/images/barrier.jpg" );
        rightWall.translate( 10, 0 );
        rightWall.scale( 2.0 / rightWall.getHeight() );
        addChild( rightWall );

        manImage = new PImage( standing );
        manImage.scale( 2.0 / manImage.getHeight() );
        addChild( manImage );
        setDirection( 0.0 );
    }

    public void setDirection( double value ) {
        Image origImage = manImage.getImage();

        Image newImage;
        double tolerance = 1E-2;
        if ( value < -tolerance ) {
            newImage = left;
        }
        else if ( value > tolerance ) {
            newImage = right;
        }
        else {
            newImage = standing;
        }
        if ( newImage != origImage ) {
            manImage.setImage( newImage );
        }
    }

    public void setWallsVisible( boolean vis ) {
        leftWall.setVisible( vis );
        rightWall.setVisible( vis );
    }

    public PImage getManImage() {
        return manImage;
    }

    public void setTransform( double modelWidth, int screenWidth ) {
        this.modelWidth = modelWidth;
        this.screenWidth = screenWidth;
        updateTransform();
    }

    private void updateTransform() {
        setTransform( new AffineTransform() );
        translate( screenWidth / 2, 0 );

        transformBy( AffineTransform.getScaleInstance( sign * screenWidth / modelWidth, screenWidth / modelWidth ) );
    }

    public void setRightDirPositive( boolean rightPositive, JComponent component ) {
        double newSign = rightPositive ? +1 : -1;
        double oldSign = sign > 0 ? +1 : -1;
        if ( newSign != sign ) {
            for ( double s = 1 * oldSign; newSign == -1 ? s >= -1 * oldSign : s <= -1 * oldSign; s -= 0.05 * oldSign ) {//new sign =-1 old sign=+1
                sign = s;
                updateTransform();
                for ( int i = 0; i < tickTextList.size(); i++ ) {
                    TickLabel pText = (TickLabel) tickTextList.get( i );
                    pText.updateTransform();
                }
                component.paintImmediately( 0, 0, component.getWidth(), component.getHeight() );
                try {
                    Thread.sleep( 5 );
                }
                catch( InterruptedException e ) {
                    e.printStackTrace();
                }
            }
            sign = newSign;
            updateTransform();
            notifyDirectionChanged();
        }
    }

    public static interface Listener {
        void directionChanged();
    }

    private ArrayList listeners = new ArrayList();

    public void addListener( Listener listener ) {
        listeners.add( listener );
    }

    public void notifyDirectionChanged() {
        for ( int i = 0; i < listeners.size(); i++ ) {
            ( (Listener) listeners.get( i ) ).directionChanged();
        }
    }

    public double getScaleX() {
        return sign;
    }

    class TickLabel extends PText {
        private double x;

        public TickLabel( DecimalFormat decimalFormat, double x ) {
            super( "" + decimalFormat.format( x ) + ( x == 0 ? " " + MovingManResources.getString( "units.meters" ) : "" ) );
            this.x = x;
            setFont( new PhetFont( 14, true ) );
            updateTransform();
        }

        public void updateTransform() {
            setTransform( new AffineTransform() );
            transformBy( AffineTransform.getScaleInstance( 0.025 * sign, 0.025 ) );
            setOffset( x - getFullBounds().getWidth() / 2 * sign, 2 + 0.1 );
        }
    }
}
