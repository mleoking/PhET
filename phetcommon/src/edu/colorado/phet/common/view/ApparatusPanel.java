/*
 * Class: ApparatusPanel
 * Package: edu.colorado.phet.common.view.graphics
 *
 * Created by: Ron LeMaster
 * Date: Nov 6, 2002
 */
package edu.colorado.phet.common.view;

import edu.colorado.phet.common.view.graphics.Graphic;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;

/**
 * This is a base class for panels that contain graphic representations
 * of elements in the PhysicalSystem.
 * <p/>
 * The graphic objects to be displayed are maintained in "layers". Each layer can
 * contain any number of Graphic objects, and each layer has an integer "level"
 * associated with it. Layers are drawn in ascending order of their levels. The order
 * in which objects in a given level are drawn in undefined.
 * <p/>
 *
 * @see edu.colorado.phet.common.view.graphics.Graphic
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
    private BasicStroke borderStroke = new BasicStroke( 4, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND );
    CompositeInteractiveGraphic graphic = new CompositeInteractiveGraphic();
    ArrayList graphicsSetups = new ArrayList();

    public ApparatusPanel() {
        // Call superclass constructor with null so that we
        // don't get the default layout manager. This allows us
        // to lay out components with absolute coordinates
        super( null );
        this.addMouseListener( graphic );
        this.addMouseMotionListener( graphic );

        Graphic borderGraphic = new Graphic() {
            public void paint( Graphics2D g ) {
                Rectangle boundingRect = getBounds();
                g.setStroke( borderStroke );
                g.setColor( Color.black );
                g.drawRect( 2, 2,
                            (int)boundingRect.getWidth() - 4,
                            (int)boundingRect.getHeight() - 4 );
            }
        };
        addGraphic( borderGraphic, Double.POSITIVE_INFINITY );
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
    }

    public void addGraphic( Graphic graphic, double level ) {
        this.graphic.addGraphic( graphic, level );
    }

    /**
     * Adds a graphic to the default layer 0.
     */
    public void addGraphic( Graphic graphic ) {
        this.graphic.addGraphic( graphic, 0 );
    }

    public void addGraphic( Graphic graphic, AffineTransform atx ) {
        this.graphic.addGraphic( graphic, 0, atx, null );
    }

    public void addGraphic( Graphic graphic, double level, AffineTransform atx ) {
        this.graphic.addGraphic( graphic, level, atx, null );
    }

    public void addGraphic( Graphic graphic, AffineTransform atx, RevertableGraphicsSetup graphicSetup ) {
        this.graphic.addGraphic( graphic, 0, atx, graphicSetup );
    }

    public void addGraphic( Graphic graphic, double level, AffineTransform atx, RevertableGraphicsSetup graphicSetup ) {
        this.graphic.addGraphic( graphic, level, atx, graphicSetup );
    }

    public void removeGraphic( Graphic graphic ) {
        this.graphic.remove( graphic );
    }

    public CompositeInteractiveGraphic getGraphic() {
        return graphic;
    }

}
