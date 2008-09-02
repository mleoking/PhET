/*
 * Class: ApparatusPanel
 * Package: edu.colorado.phet.common.view.graphics
 *
 * Created by: Ron LeMaster
 * Date: Nov 6, 2002
 */
package edu.colorado.phet.greenhouse.phetcommon.view;

import edu.colorado.phet.greenhouse.coreadditions.graphics.AffineTransformFactory;
import edu.colorado.phet.greenhouse.phetcommon.view.graphics.Graphic;

import javax.swing.*;
import javax.swing.event.MouseInputAdapter;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.util.Observable;
import java.util.Observer;

/**
 * This is a base class for panels that contain graphic representations
 * of elements in the PhysicalSystem.
 * <p>
 * The graphic objects to be displayed are maintained in "layers". Each layer can
 * contain any number of Graphic objects, and each layer has an integer "level"
 * associated with it. Layers are drawn in ascending order of their levels. The order
 * in which objects in a given level are drawn in undefined.
 * <p>
 * Levels less than 0 are reserved for items that are always to be displayed. This
 * could, for example, be used for a fixture or instrument that is always to appear as
 * part of the apparatus, such as a table or meter. When this class' removeAllModelElements() method is
 * executed these objects are not destroyed.
 * <p>
 * Levels 1 and higher are used for objects that can be created and destroyed as the
 * application runs. All objects in these layers are destroyed when the removeAllModelElements() method
 * is executed.
 * <p>
 * Instances of this class are Observers of the application's PhysicalSystem
 *
 * @see edu.colorado.phet.greenhouse.phetcommon.view.graphics.Graphic
 */
public class ApparatusPanel extends JPanel implements Observer {

    //
    // Statics
    //
    public static final double LAYER_TOP = Double.POSITIVE_INFINITY;
    public static final double LAYER_BOTTOM = Double.NEGATIVE_INFINITY;
    public static final double LAYER_DEFAULT = 0;

    // The map of graphic objects to be drawn in the panel
    private MouseHandler mh;
    private CompositeGraphic compositeGraphic = new CompositeGraphic();
    private AffineTransformFactory mvTx;
    private AffineTransform affineTx;
    private AffineTransform inverseAffineTx;
    private BasicStroke borderStroke = new BasicStroke( 1 );


    /**
     *
     */
    public ApparatusPanel() {
        this( new AffineTransformFactory() {
            public AffineTransform getTx( Rectangle rectangle ) {
                return AffineTransform.getScaleInstance( 1, 1 );
            }
        } );
    }

    /**
     *
     */
    public ApparatusPanel( final AffineTransformFactory tx ) {
        // Call superclass constructor with null so that we
        // don't get the default layout manager. This allows us
        // to lay out components with absolute coordinates
        super( null );
        this.mvTx = tx;


        this.mh = new MouseHandler();
        addMouseListener( mh );
        addMouseMotionListener( mh );

        addComponentListener( new ComponentAdapter() {
            public void componentResized( ComponentEvent e ) {
                updateTransform();
            }
        } );
    }

    /**
     *
     * @param tx
     */
    public void setAffineTransformFactory( AffineTransformFactory tx ) {
        this.mvTx = tx;
    }

    /**
     * Clears objects in the graphical context that are experiment-specific
     */
    public void removeAllGraphics() {
        compositeGraphic.removeAllGraphics();
    }

    /**
     * Draws all the Graphic objects in the ApparatusPanel
     * @param graphics
     */
    protected void paintComponent( Graphics graphics ) {

        Graphics2D g2 = (Graphics2D)graphics;
        super.paintComponent( g2 );

        AffineTransform orgATx = g2.getTransform();
        if (affineTx!=null){
        g2.setTransform( affineTx );
        }
        compositeGraphic.paint( g2 );
        g2.setTransform( orgATx );

        // Draw a bounding rectangle
        Rectangle boundingRect = this.getBounds();
        graphics.setColor( Color.black );
        g2.setStroke( borderStroke );
        g2.drawRect( 0, 0,
                     (int)boundingRect.getWidth() - 2,
                     (int)boundingRect.getHeight() - 2 );
    }

    /**
     *
     * @param graphic
     * @param level
     */
    public void addGraphic( Graphic graphic, double level ) {
        compositeGraphic.addGraphic( graphic, level );
    }

    /**
     * Removes the specified paintable from the specified level.
     */
    public void removeGraphic( Graphic graphic ) {
        compositeGraphic.removeGraphic( graphic );
    }

    public void update( Observable o, Object arg ) {
        repaint();
    }

    /**
     * Causes this panel to update its model-to-view affine transform
     * based on its current view bounds.
     */
    public void updateTransform() {
        Rectangle bounds = this.getBounds();
        affineTx = mvTx.getTx( bounds );
        try {
            inverseAffineTx = affineTx.createInverse();
        }
        catch( NoninvertibleTransformException e ) {
//            e.printStackTrace();
        }
    }

    public CompositeGraphic getCompositeGraphic() {
        return compositeGraphic;
    }

    //
    // Inner classes
    //

    private class MouseHandler
            extends MouseInputAdapter {

        public void mousePressed( MouseEvent e ) {
            compositeGraphic.mousePressed( e, getModelLoc( e.getPoint() ) );
        }

        public void mouseReleased( MouseEvent e ) {
            compositeGraphic.mouseReleased( e, getModelLoc( e.getPoint() ) );
        }

        public void mouseDragged( MouseEvent e ) {
            compositeGraphic.mouseDragged( e, getModelLoc( e.getPoint() ) );
        }

        public void mouseMoved( MouseEvent e ) {
            compositeGraphic.mouseMoved( e, getModelLoc( e.getPoint() ) );
        }

        private Point2D.Double getModelLoc( Point viewLoc ) {
            Point2D.Double modelLoc = new Point2D.Double();
            inverseAffineTx.transform( viewLoc, modelLoc );
            return modelLoc;
        }
    }
}
