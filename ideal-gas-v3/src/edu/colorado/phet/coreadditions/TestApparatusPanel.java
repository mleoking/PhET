/*
 * Class: ApparatusPanel
 * Package: edu.colorado.phet.common.view.graphics
 *
 * Created by: Ron LeMaster
 * Date: Nov 6, 2002
 */
package edu.colorado.phet.common.view;

import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.model.BaseModel;
import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.common.view.graphics.Graphic;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
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

    ArrayList graphicsSetups = new ArrayList();
    private boolean paintEnabled;

    public TestApparatusPanel( BaseModel model ) {
        // Call superclass constructor with null so that we
        // don't get the default layout manager. This allows us
        // to lay out components with absolute coordinates
        //        super();
        MouseProcessor mouseProcessor = new MouseProcessor( mouseDelegator );
        model.addModelElement( mouseProcessor );
        this.addMouseListener( mouseProcessor );
        this.addMouseMotionListener( mouseProcessor );
        //        this.addMouseListener( mouseDelegator );
        //        this.addMouseMotionListener( mouseDelegator );
        //        BevelBorder border = (BevelBorder)BorderFactory.createLoweredBevelBorder();

        //        Border border = BorderFactory.createLineBorder( Color.black );
        //        this.setBorder( border );

        model.addModelElement( new ModelElement() {
            public void stepInTime( double dt ) {
                Graphics g = PhetApplication.instance().getApplicationView().getPhetFrame().getGraphics();
                myPaintComponents( g );
            }
        } );
    }

    public void myPaintComponents( Graphics g ) {
        super.paintComponents( g );
    }

    public void paintComponents( Graphics g ) {
        //        super.paintComponents( g );
        return;
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

    /**
     * Draws all the Graphic objects in the ApparatusPanel
     *
     * @param graphics
     */
    protected void paintComponent( Graphics graphics ) {
        Graphics2D g2 = (Graphics2D)graphics;
        super.paintComponent( g2 );
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
