/*
 * Class: ApparatusPanel
 * Package: edu.colorado.phet.common.view.graphics
 *
 * Created by: Ron LeMaster
 * Date: Nov 6, 2002
 */
package edu.colorado.phet.common_1200.view;

import java.awt.*;
import java.util.ArrayList;

import javax.swing.JPanel;

import edu.colorado.phet.common.phetgraphics.view.util.GraphicsState;
import edu.colorado.phet.common_1200.view.graphics.Graphic;
import edu.colorado.phet.common_1200.view.phetgraphics.PhetGraphic;

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
 * @see edu.colorado.phet.common_1200.view.graphics.Graphic
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
    CompositeGraphic graphic = new CompositeGraphic();
    CompositeInteractiveGraphicMouseDelegator mouseDelegator = new CompositeInteractiveGraphicMouseDelegator( this.graphic );

    ArrayList graphicsSetups = new ArrayList();

    protected ApparatusPanel( Object dummy ) {
        super( null );
    }

    public ApparatusPanel() {
        // Call superclass constructor with null so that we
        // don't get the default layout manager. This allows us
        // to lay out components with absolute coordinates
        super( null );
        this.addMouseListener( mouseDelegator );
        this.addMouseMotionListener( mouseDelegator );
        //        BevelBorder border = (BevelBorder)BorderFactory.createLoweredBevelBorder();

        //        Border border = BorderFactory.createLineBorder( Color.black );
        //        this.setBorder( border );
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

    protected void superPaint( Graphics g ) {
        super.paintComponent( g );
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
        state.restoreGraphics();
    }

    public void addGraphic( Graphic graphic, double level ) {
        this.graphic.addGraphic( graphic, level );
        if( graphic instanceof PhetGraphic ) {
            PhetGraphic phetGraphic = (PhetGraphic)graphic;
            phetGraphic.repaint();
        }
    }

    /**
     * Adds a graphic to the default layer 0.
     */
    public void addGraphic( Graphic graphic ) {
        this.addGraphic( graphic, 0 );
    }

    public void removeGraphic( Graphic graphic ) {
        this.graphic.removeGraphic( graphic );
    }

    public CompositeGraphic getGraphic() {
        return graphic;
    }

}
