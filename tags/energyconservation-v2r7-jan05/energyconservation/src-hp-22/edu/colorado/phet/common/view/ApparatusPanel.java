/*
 * Class: ApparatusPanel
 * Package: edu.colorado.phet.common.view.graphics
 *
 * Created by: Ron LeMaster
 * Date: Nov 6, 2002
 */
package edu.colorado.phet.common.view;

import edu.colorado.phet.common.view.graphics.Graphic;
import edu.colorado.phet.common.view.graphics.InteractiveGraphic;

import javax.swing.*;
import javax.swing.event.MouseInputAdapter;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.Observable;
import java.util.Observer;

/**
 * This is a base class for panels that contain graphic representations
 * of elements in the PhysicalSystem.
 * <p/>
 * The graphic objects to be displayed are maintained in "layers". Each layer can
 * contain any number of Graphic objects, and each layer has an integer "level"
 * associated with it. Layers are drawn in ascending order of their levels. The order
 * in which objects in a given level are drawn in undefined.
 * <p/>
 * Levels less than 0 are reserved for items that are always to be displayed. This
 * could, for example, be used for a fixture or instrument that is always to appear as
 * part of the apparatus, such as a table or meter. When this class' removeAllModelElements() method is
 * executed these objects are not destroyed.
 * <p/>
 * Levels 1 and higher are used for objects that can be created and destroyed as the
 * application runs. All objects in these layers are destroyed when the removeAllModelElements() method
 * is executed.
 * <p/>
 * Instances of this class are Observers of the application's PhysicalSystem
 *
 * @see edu.colorado.phet.common.view.graphics.Graphic
 */
public class ApparatusPanel extends JPanel implements Observer {

    // The map of graphic objects to be drawn in the panel
    private MouseHandler mh;
    private CompositeGraphic compositeGraphic = new CompositeGraphic();

    /**
     * @param observable
     */
    public ApparatusPanel( Observable observable ) {
        this();
        observable.addObserver( this );
    }

    /**
     *
     */
    public ApparatusPanel() {
        // Call superclass constructor with null so that we
        // don't get the default layout manager. This allows us
        // to lay out components with absolute coordinates
        super( null );
        this.setBackground( Color.WHITE );
        // Set the size of the panel
        this.setPreferredSize( new Dimension( 600, 520 ) );
        this.mh = new MouseHandler();
        addMouseListener( mh );
        addMouseMotionListener( mh );
    }


    /**
     * Clears objects in the graphical context that are experiment-specific
     */
    public void removeAllGraphics() {
        compositeGraphic.removeAllGraphics();
//        mh.currentIG = null;
    }

    BasicStroke borderStroke = new BasicStroke( 1 );

    /**
     * Draws all the Graphic objects in the ApparatusPanel
     *
     * @param graphics
     */
    protected void paintComponent( Graphics graphics ) {
        Graphics2D g2 = (Graphics2D)graphics;
        super.paintComponent( graphics );
        compositeGraphic.paint( (Graphics2D)graphics );
        // Draw a bounding rectangle
        Rectangle boundingRect = this.getBounds();
        graphics.setColor( Color.black );
        g2.setStroke( borderStroke );
        graphics.drawRect( 0, 0,
                           (int)boundingRect.getWidth() - 2,
                           (int)boundingRect.getHeight() - 2 );

    }

    /**
     * @param graphic
     * @param level
     */
    public void addGraphic( Graphic graphic, int level ) {
        compositeGraphic.addGraphic( graphic, level );
    }

    /**
     * Removes the specified paintable from the specified level.
     */
    public void removeGraphic( Graphic graphic ) {
        compositeGraphic.removeGraphic( graphic );
    }

    /**
     * Returns the InteractiveGraphic in the ApparatusPanel that should
     * handle a specified mouse event
     *
     * @param e
     * @return
     */
    private InteractiveGraphic determineMouseHandler( MouseEvent e ) {
        return compositeGraphic.determineMouseHandler( e );
    }

    public void update( Observable o, Object arg ) {
        repaint();
    }


    //
    // Inner classes
    //

    private class MouseHandler
            extends MouseInputAdapter {

        public void mousePressed( MouseEvent e ) {
            compositeGraphic.mousePressed( e );
        }

        public void mouseReleased( MouseEvent e ) {
            compositeGraphic.mouseReleased( e );
        }

        public void mouseDragged( MouseEvent e ) {
            compositeGraphic.mouseDragged( e );
        }

        public void mouseMoved( MouseEvent e ) {
            compositeGraphic.mouseMoved( e );
        }
    }

    //TODO Added by Sam Reid for testing purposes in Energy Conservation Applet.
    public CompositeGraphic getCompositeGraphic() {
        return compositeGraphic;
    }

}
