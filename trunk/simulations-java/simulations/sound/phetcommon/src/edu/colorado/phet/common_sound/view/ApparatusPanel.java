/* Copyright 2002-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.common_sound.view;

import java.awt.*;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JPanel;
import javax.swing.event.MouseInputListener;

import edu.colorado.phet.common.phetgraphics.view.phetgraphics.GraphicLayerSet;
import edu.colorado.phet.common.phetgraphics.view.phetgraphics.PhetGraphic;
import edu.colorado.phet.common.phetgraphics.view.util.GraphicsSetup;
import edu.colorado.phet.common.phetgraphics.view.util.GraphicsState;

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
 * @author Ron LeMaster
 * @version $Revision$
 * @see edu.colorado.phet.common_sound.view.phetgraphics.PhetGraphic
 */
public class ApparatusPanel extends JPanel {

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
    private GraphicLayerSet graphic;

    private ArrayList graphicsSetups = new ArrayList();
    private boolean displayBorder = true;

    protected ApparatusPanel( Object obj ) {
        super( null );
        setGraphic( new GraphicLayerSet( this ) );
    }

    public ApparatusPanel() {
        // Call superclass constructor with null so that we don't get the default layout manager. This allows us
        // to lay out components with absolute coordinates
        super( null );
        setGraphic( new GraphicLayerSet( this ) );
        setMouseAndKeyListeners( getGraphic().getMouseHandler(), getGraphic().getKeyAdapter() );
    }

    /**
     * Sets the GraphicLayerSet for the ApparatusPanel, and attaches its listeners to the panel, after
     * getting rid of the old ones. Also tells all the PhetGraphics in the new GraphicLayerSet that their
     * containing component is this AppratusPanel.
     *
     * @param newGraphic
     */
    public void setGraphic( GraphicLayerSet newGraphic ) {

        this.graphic = newGraphic;

        // Hook up all the graphics to the apparatus panel
        graphic.setComponent( this );
        Iterator gIt = graphic.getGraphicMap().iterator();
        while( gIt.hasNext() ) {
            Object obj = gIt.next();
            if( obj instanceof PhetGraphic ) {
                PhetGraphic phetGraphic = (PhetGraphic)obj;
                phetGraphic.setComponent( this );
            }
        }
    }

    /**
     * Sets up mouse and key listeners
     */
    protected void setMouseAndKeyListeners( MouseInputListener mouseHandler, KeyListener keyAdapter ) {
        // Clear the old handlers
        MouseListener[] mouseListeners = this.getMouseListeners();
        for( int i = 0; i < mouseListeners.length; i++ ) {
            MouseListener mouseListener = mouseListeners[i];
            this.removeMouseListener( mouseListener );
        }
        MouseMotionListener[] mouseMostionListeners = this.getMouseMotionListeners();
        for( int i = 0; i < mouseMostionListeners.length; i++ ) {
            MouseMotionListener mouseMostionListener = mouseMostionListeners[i];
            this.removeMouseMotionListener( mouseMostionListener );
        }
        KeyListener[] keyListeners = this.getKeyListeners();
        for( int i = 0; i < keyListeners.length; i++ ) {
            KeyListener keyListener = keyListeners[i];
            this.removeKeyListener( keyListener );
        }

        // Add the new handlers
        this.addMouseListener( mouseHandler );
        this.addMouseMotionListener( getGraphic().getMouseHandler() );
        this.addKeyListener( keyAdapter );
    }

    protected void setHandlers( GraphicLayerSet.SwingAdapter mouseHandler, KeyListener keyAdapter ) {
        this.addMouseListener( mouseHandler );
        this.addMouseMotionListener( getGraphic().getMouseHandler() );
        this.addKeyListener( keyAdapter );
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

    protected void superPaint( Graphics g ) {
        super.paintComponent( g );
    }

    protected ArrayList getGraphicsSetups() {
        return graphicsSetups;
    }

    protected void setup( Graphics2D g2 ) {
        for( int i = 0; i < graphicsSetups.size(); i++ ) {
            GraphicsSetup graphicsSetup = (GraphicsSetup)graphicsSetups.get( i );
            graphicsSetup.setup( g2 );
        }
    }

    /**
     * Draws all the Graphic objects in the ApparatusPanel
     *
     * @param graphics
     */
    protected void paintComponent( Graphics graphics ) {
        Graphics2D g2 = (Graphics2D)graphics;
        super.paintComponent( g2 );
        GraphicsState state = new GraphicsState( g2 );
        setup( g2 );
        graphic.paint( g2 );
        drawBorder( g2 );
        state.restoreGraphics();
    }

    /**
     * Provided so that subclasses (e.g. ApparatusPanel2) can get Java components to
     * paint without them having to call super.paintComponent() and incurring a redundant
     * painting of the graphic instance.
     *
     * @param graphics
     */
    protected void doPaintSuper( Graphics graphics ) {
        super.paintComponent( graphics );
    }

    /**
     * Causes the panel to be repainted in the normal Swing invocation loop
     */
    public void paint() {
        repaint();
    }

    public void addGraphic( PhetGraphic graphic, double level ) {
        this.graphic.addGraphic( graphic, level );
//        graphic.repaint();//Automatically repaint the added graphic.//This is/should be done in GraphicLayerSet
    }

    /**
     * Adds a graphic to the default layer 0.
     */
    public void addGraphic( PhetGraphic graphic ) {
        this.addGraphic( graphic, 0 );
    }

    public void removeGraphic( PhetGraphic graphic ) {
        this.graphic.removeGraphic( graphic );
    }

    public GraphicLayerSet getGraphic() {
        return graphic;
    }

    protected void drawBorder( Graphics2D g2 ) {
        if( displayBorder ) {
            GraphicsState gs = new GraphicsState( g2 );
            g2.setColor( Color.black );
            g2.setStroke( borderStroke );
            Rectangle border = new Rectangle( 0, 0, (int)this.getBounds().getWidth() - 1, (int)this.getBounds().getHeight() - 1 );
            g2.draw( border );
            gs.restoreGraphics();
        }
    }

    public void setDisplayBorder( boolean displayBorder ) {
        this.displayBorder = displayBorder;
    }

    public void handleUserInput() {
        //noop
    }

}
