/*
 * Class: ApparatusPanel
 * Package: edu.colorado.phet.common.view.graphics
 *
 * Created by: Ron LeMaster
 * Date: Nov 6, 2002
 */
package edu.colorado.phet.coreadditions;

import edu.colorado.phet.common.model.BaseModel;
import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.common.view.CompositeGraphic;
import edu.colorado.phet.common.view.CompositeInteractiveGraphicMouseDelegator;
import edu.colorado.phet.common.view.GraphicsSetup;
import edu.colorado.phet.common.view.graphics.Graphic;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.LinkedList;

/**
 * This is a base class for panels that contain graphic representations
 * of elements in the PhysicalSystem.
 * <p/>
 * The graphic objects to be displayed are maintained in "layers". Each layer can
 * contain any number of Graphic objects, and each layer has an integer "level"
 * associated with it. Layers are drawn in ascending order of their levels. The order
 * in which objects in a given level are drawn is undefined.
 * Test Comment.
 * <p/>
 *
 * @see edu.colorado.phet.common.view.graphics.Graphic
 */
public class TestApparatusPanel extends ApparatusPanel {

    //
    // Statics
    //
    public static final double LAYER_TOP = Double.POSITIVE_INFINITY;
    public static final double LAYER_BOTTOM = Double.NEGATIVE_INFINITY;
    public static final double LAYER_DEFAULT = 0;


    //
    // Instance fields and methods
    //
    private BasicStroke borderStroke = new BasicStroke( 1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND );
    CompositeGraphic graphic = new CompositeGraphic();
    CompositeInteractiveGraphicMouseDelegator mouseDelegator = new CompositeInteractiveGraphicMouseDelegator( this.graphic );
    BufferedImage bImg;

    ArrayList graphicsSetups = new ArrayList();
    private boolean paintEnabled;

    public TestApparatusPanel( BaseModel model ) {
        super( null );
        MouseProcessor mouseProcessor = new MouseProcessor( mouseDelegator );
        model.addModelElement( mouseProcessor );
        this.addMouseListener( mouseProcessor );
        this.addMouseMotionListener( mouseProcessor );

        model.addModelElement( new ModelElement() {
            public void stepInTime( double dt ) {
                //                Graphics g = PhetApplication.instance().getApplicationView().getPhetFrame().getGraphics();
                //                myPaintComponent( g );
                //                myPaintComponents( g );
                updateBuffer();
                paintImmediately( 0, 0, getWidth(), getHeight() );
            }
        } );
        //        setOpaque( true );
        //        setDoubleBuffered( false );
    }

    public CompositeInteractiveGraphicMouseDelegator getMouseDelegator() {
        return mouseDelegator;
    }

    public void addGraphicsSetup( GraphicsSetup setup ) {
        graphicsSetups.add( setup );
    }

    /**
     * Clears objects in the graphical context that are experiment-specific
     */
    public void removeAllGraphics() {
        graphic.clear();
    }

    public void repaint( long tm, int x, int y, int width, int height ) {
        //super.repaint( tm, x, y, width, height );
    }

    public void repaint( Rectangle r ) {
        //        super.repaint( r );
    }

    public void repaint() {
        //        super.repaint();
    }

    public void repaint( int x, int y, int width, int height ) {
        //        super.repaint( x, y, width, height );
    }

    public void repaint( long tm ) {
        //        super.repaint( tm );
    }

    AffineTransform IDENTITY = AffineTransform.getTranslateInstance( 0, 0 );

    /**
     * Draws all the Graphic objects in the ApparatusPanel
     *
     * @param graphics
     */
    protected void paintComponent( Graphics graphics ) {
        if( bImg == null || bImg.getWidth() != this.getWidth() || bImg.getHeight() != this.getHeight() ) {
            updateBuffer();
        }
        else {
            ( (Graphics2D)graphics ).drawRenderedImage( bImg, IDENTITY );
        }
    }

    private void updateBuffer() {
        if( bImg == null || bImg.getWidth() != this.getWidth() || bImg.getHeight() != this.getHeight() ) {
            if( getWidth() == 0 || getHeight() == 0 ) {
                return;
            }
            bImg = new BufferedImage( this.getWidth(), this.getHeight(), BufferedImage.TYPE_INT_RGB );
        }
        Graphics2D g2 = (Graphics2D)bImg.getGraphics();

        g2.setBackground( super.getBackground() );
        g2.clearRect( 0, 0, getWidth(), getHeight() );
        for( int i = 0; i < graphicsSetups.size(); i++ ) {
            GraphicsSetup graphicsSetup = (GraphicsSetup)graphicsSetups.get( i );
            graphicsSetup.setup( g2 );
        }
        graphic.paint( g2 );
        Color origColor = g2.getColor();
        Stroke origStroke = g2.getStroke();

        g2.setColor( Color.black );
        g2.setStroke( borderStroke );
        Rectangle border = new Rectangle( 0, 0, (int)this.getBounds().getWidth() - 1, (int)this.getBounds().getHeight() - 1 );
        g2.draw( border );

        g2.setColor( origColor );
        g2.setStroke( origStroke );
        //        g2.draw( this.getBounds() );
    }

    public void addGraphic( Graphic graphic, double level ) {
        this.graphic.addGraphic( graphic, level );
    }

    /**
     * Adds a graphic to the default layer 0.
     */
    public void addGraphic( Graphic graphic ) {
        this.addGraphic( graphic, 0 );
        //        this.graphic.addGraphic( graphic, 0 );
    }

    public void removeGraphic( Graphic graphic ) {
        this.graphic.removeGraphic( graphic );
    }

    public CompositeGraphic getGraphic() {
        return graphic;
    }

    class MouseProcessor implements ModelElement, MouseListener, MouseMotionListener {
        LinkedList mouseEventList;
        LinkedList mouseMotionEventList;
        private CompositeInteractiveGraphicMouseDelegator handler;

        public MouseProcessor( CompositeInteractiveGraphicMouseDelegator mouseDelegator ) {
            mouseEventList = new LinkedList();
            mouseMotionEventList = new LinkedList();
            this.handler = mouseDelegator;
        }

        int cnt = 0;

        public void stepInTime( double dt ) {
            cnt = 0;
            processMouseEventList();
            processMouseMotionEventList();
        }

        public void addMouseEvent( MouseEvent event ) {
            synchronized( mouseEventList ) {
                mouseEventList.add( event );
            }
        }

        public void addMouseMotionEvent( MouseEvent event ) {
            synchronized( mouseMotionEventList ) {
                mouseMotionEventList.add( event );
            }
        }

        public void processMouseEventList() {
            MouseEvent event;
            while( mouseEventList.size() > 0 ) {
                synchronized( mouseEventList ) {
                    event = (MouseEvent)mouseEventList.removeFirst();
                }
                handleMouseEvent( event );
            }
        }

        public void processMouseMotionEventList() {
            MouseEvent event;
            while( mouseMotionEventList.size() > 0 ) {
                synchronized( mouseMotionEventList ) {
                    event = (MouseEvent)mouseMotionEventList.removeFirst();
                }
                handleMouseEvent( event );
            }
        }

        private void handleMouseEvent( MouseEvent event ) {
            //            if( true) return;
            switch( event.getID() ) {
                case MouseEvent.MOUSE_CLICKED:
                    handler.mouseClicked( event );
                    break;
                case MouseEvent.MOUSE_DRAGGED:
                    handler.mouseDragged( event );
                    break;
                case MouseEvent.MOUSE_ENTERED:
                    handler.mouseEntered( event );
                    break;
                case MouseEvent.MOUSE_EXITED:
                    handler.mouseExited( event );
                    break;
                case MouseEvent.MOUSE_MOVED:
                    handler.mouseMoved( event );
                    break;
                case MouseEvent.MOUSE_PRESSED:
                    handler.mousePressed( event );
                    break;
                case MouseEvent.MOUSE_RELEASED:
                    handler.mouseReleased( event );
                    break;
            }
        }

        public void mouseClicked( MouseEvent e ) {
            this.addMouseEvent( e );
        }

        public void mouseEntered( MouseEvent e ) {
            this.addMouseEvent( e );
        }

        public void mouseExited( MouseEvent e ) {
            this.addMouseEvent( e );
        }

        public void mousePressed( MouseEvent e ) {
            this.addMouseEvent( e );
        }

        public void mouseReleased( MouseEvent e ) {
            this.addMouseEvent( e );
        }

        public void mouseDragged( MouseEvent e ) {
            this.addMouseMotionEvent( e );
        }

        public void mouseMoved( MouseEvent e ) {
            this.addMouseMotionEvent( e );
        }
    }
}
