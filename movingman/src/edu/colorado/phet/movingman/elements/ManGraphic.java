/*PhET, 2004.*/
package edu.colorado.phet.movingman.elements;

import edu.colorado.phet.common.math.CircularBuffer;
import edu.colorado.phet.common.math.transforms.functions.RangeToRange;
import edu.colorado.phet.common.model.command.Command;
import edu.colorado.phet.common.view.graphics.DragHandler;
import edu.colorado.phet.common.view.graphics.InteractiveGraphic;
import edu.colorado.phet.common.view.graphics.ObservingGraphic;
import edu.colorado.phet.common.view.graphics.arrows.ArrowWithFixedSizeArrowhead;
import edu.colorado.phet.common.view.util.graphics.ImageLoader;
import edu.colorado.phet.movingman.application.MovingManModule;
import edu.colorado.phet.movingman.common.ImageFlip3;
import edu.colorado.phet.movingman.common.RescaleOp3;
import edu.colorado.phet.movingman.common.tests.IdeaGraphic2;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Observable;

/**
 * User: Sam Reid
 * Date: Jun 30, 2003
 * Time: 12:25:37 AM
 * Copyright (c) Jun 30, 2003 by Sam Reid
 */
public class ManGraphic implements ObservingGraphic, InteractiveGraphic {
    private BufferedImage standingMan;
    private BufferedImage leftMan;
    private BufferedImage rightMan;
    private int x;
    private int y;
    private RangeToRange transform;//from man to graphics device.
    private MovingManModule module;
    private Man m;
    private DragHandler dragHandler;
    private BufferedImage currentImage;
    private RangeToRange inversion;
    private CircularBuffer cb = new CircularBuffer( 10 );
    private IdeaGraphic2 ideaGraphic;
    private boolean showIdea = true;
    private ArrowWithFixedSizeArrowhead arrow;
    private IdeaGraphic2 motionIdea;
    private ArrowWithFixedSizeArrowhead motionArrow;
    double lastx = 0;

    public ManGraphic( MovingManModule module, Man m, int y, RangeToRange transform ) throws IOException {
        this.module = module;
        this.m = m;
        this.y = y;
        this.transform = transform;
        standingMan = ImageLoader.loadBufferedImage( "images/stand-ii.gif" );
        leftMan = ImageLoader.loadBufferedImage( "images/left-ii.gif" );
        int height = 120;
//        JFrame frame = new JFrame();
//        frame.setContentPane( module.getApparatusPanel() );
//        module.getApparatusPanel().setVisible( true );
//        frame.setVisible( true );
        standingMan = RescaleOp3.rescaleYMaintainAspectRatio( module.getApparatusPanel(), standingMan, height );
        leftMan = RescaleOp3.rescaleYMaintainAspectRatio( module.getApparatusPanel(), leftMan, height );
        rightMan = ImageFlip3.flipX( leftMan );

        currentImage = standingMan;
        m.addObserver( this );
//        m.updateObservers();
        inversion = transform.invert();
        update();
    }

    public void update() {
        update( null, null );
    }

    public void setShowIdea( boolean showIdea ) {
        if( this.showIdea != showIdea ) {
            this.showIdea = showIdea;
            module.getApparatusPanel().repaint();
        }
    }

    public void paint( Graphics2D g ) {
        g.drawImage( currentImage, x - currentImage.getWidth() / 2, y, null );
        if( showIdea ) {
            paintIdea( g );
        }
    }

    private void paintIdea( Graphics2D g ) {
        if( ideaGraphic == null ) {
            g.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
            Color lightBlue = module.getPurple();
            Font ideaFont = new Font( "Lucida", Font.ITALIC, 18 );

            BufferedImage ideaImage = null;
            try {
                ideaImage = ImageLoader.loadBufferedImage( "images/icons/TipOfTheDay24.gif" );
            }
            catch( IOException e ) {
                e.printStackTrace();
            }
            int ideaX = module.getApparatusPanel().getWidth() / 8;
            ideaGraphic = new IdeaGraphic2( true, ideaX, y + 250, new String[]{"Drag the man"},
                                            g.getFontRenderContext(), ideaFont, Color.black, ideaImage, lightBlue );
            int motionIdeaX = (int)( module.getApparatusPanel().getWidth() * .6 );
            arrow = new ArrowWithFixedSizeArrowhead( Color.black, 10 );
            motionIdea = new IdeaGraphic2( true, motionIdeaX, y + 270, new String[]{"Or choose a premade motion."}, g.getFontRenderContext(),
                                           ideaFont, Color.black, ideaImage, lightBlue );
            motionArrow = new ArrowWithFixedSizeArrowhead( Color.black, 10 );
        }
        ideaGraphic.paint( g );
        motionIdea.paint( g );
        Point ideaCenter = ideaGraphic.getImageCenter();
        arrow.drawLine( g, ideaCenter.x, ideaCenter.y, x - currentImage.getWidth() / 2, y + currentImage.getHeight() / 2 );

        Point motionCenter = motionIdea.getImageCenter();
        motionArrow.drawLine( g, motionCenter.x, motionCenter.y, module.getApparatusPanel().getWidth(), 85 );
    }

    public void update( Observable o, Object arg ) {
        Rectangle origRectangle = getRectangle();

        double output = transform.evaluate( m.getX() );
        int oldX = x;
        this.x = (int)output;

        cb.addPoint( x - lastx );
        lastx = x;
        double velocity = cb.average();
        BufferedImage origImage = currentImage;
        if( velocity == 0 && currentImage != this.standingMan ) {
            currentImage = this.standingMan;
        }
        else if( velocity < 0 && currentImage != this.leftMan ) {
            currentImage = this.leftMan;
        }
        else if( velocity > 0 && currentImage != this.rightMan ) {
            currentImage = this.rightMan;
        }
        if( oldX != x || origImage != currentImage ) {
            paintImmediately( origRectangle, getRectangle() );
        }
    }

    private void paintImmediately( Rectangle r1, Rectangle r2 ) {
        Rectangle union = r1.union( r2 );
//        module.getApparatusPanel().paintImmediately( union );
        module.getApparatusPanel().paintSoon( union );
    }

    public boolean canHandleMousePress( MouseEvent event ) {
        if( true ) {
            BufferedImage im = currentImage;
            Rectangle r = new Rectangle( x - im.getWidth() / 2, y, im.getWidth(), im.getHeight() );
            return r.contains( event.getPoint() );
        }
        else {
            return false;
        }
    }

    public void mousePressed( MouseEvent event ) {
        dragHandler = new DragHandler( event.getPoint(), new Point( x, y ) );
        module.getModel().execute( new Command() {
            public void doIt() {
                m.setGrabbed( true );
            }
        } );
    }

    public void mouseDragged( MouseEvent event ) {
        if( !module.isDragMode() || module.isPaused() ) {
            module.getMovingManControlPanel().startRecordingManual();
        }
        final Point newPt = dragHandler.getNewLocation( event.getPoint() );
//        Rectangle curRect = getRectangle();
        int graphicsPt = newPt.x;
        double manPoint = inversion.evaluate( graphicsPt );
        m.setX( manPoint );
        setShowIdea( false );
    }

    public Rectangle getRectangle() {
        return new Rectangle( x - currentImage.getWidth() / 2, y, currentImage.getWidth(), currentImage.getHeight() );
    }

    public void mouseReleased( MouseEvent event ) {
        module.getModel().execute( new Command() {
            public void doIt() {
                m.setGrabbed( false );
            }
        } );
        dragHandler = null;
    }

    public void mouseEntered( MouseEvent event ) {
        event.getComponent().setCursor( Cursor.getPredefinedCursor( Cursor.HAND_CURSOR ) );
    }

    public void mouseExited( MouseEvent event ) {
        event.getComponent().setCursor( Cursor.getPredefinedCursor( Cursor.DEFAULT_CURSOR ) );
    }

    public void setTransform( RangeToRange transform ) {
        this.transform = transform;
        this.inversion = transform.invert();
        update( null, null );
    }

}
