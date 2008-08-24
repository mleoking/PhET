package edu.colorado.phet.forces1d.view;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import javax.swing.event.MouseInputAdapter;

import edu.colorado.phet.forces1d.model.Block;
import edu.colorado.phet.forces1d.model.Force1DModel;
import edu.colorado.phet.forces1d.model.Force1dObject;
import edu.colorado.phet.forces1d.phetcommon.math.Function;
import edu.colorado.phet.forces1d.phetcommon.view.phetgraphics.CompositePhetGraphic;
import edu.colorado.phet.forces1d.phetcommon.view.phetgraphics.PhetImageGraphic;

/**
 * User: Sam Reid
 * Date: Nov 13, 2004
 * Time: 10:27:14 AM
 */
public class BlockGraphic extends CompositePhetGraphic {
    private Block block;
    private Function.LinearFunction transform1d;
    private PhetImageGraphic graphic;
    private Force1DPanel panel;
    private Point lastCenter;
    private OffsetManager offsetManager;

    public BlockGraphic( Force1DPanel panel, final Block block, final Force1DModel model,
                         final Function.LinearFunction transform1d, Force1dObject force1dObject, OffsetManager offsetManager ) {
        super( panel );
        offsetManager.addListener( new OffsetManager.Listener() {
            public void offsetChanged() {
                update();
            }
        } );
        this.offsetManager = offsetManager;
        this.panel = panel;
        this.block = block;
        this.transform1d = transform1d;

        graphic = new PhetImageGraphic( panel, force1dObject.getLocation() );
        addGraphic( graphic );

        update();

        MouseInputAdapter mia = new MouseInputAdapter() {
            public void mouseDragged( MouseEvent e ) {
                Point ctr = getCenter();
                double dx = e.getPoint().x - ctr.x;
                double appliedForce = dx / ArrowSetGraphic.forceLengthScale;
                model.setAppliedForce( appliedForce );
            }

            // implements java.awt.event.MouseListener
            public void mouseReleased( MouseEvent e ) {
                model.setAppliedForce( 0.0 );
            }
        };

        ThresholdedDragAdapter mouseListener = new ThresholdedDragAdapter( mia, 10, 0, 1000 );
        addMouseInputListener( mouseListener );
        setCursorHand();
    }

    public void setImage( BufferedImage image ) {
        graphic.setImage( image );
    }

    public Dimension computeDimension() {
        return new Dimension( graphic.getWidth(), graphic.getHeight() );
    }

    protected Rectangle determineBounds() {
        return super.determineBounds();
    }

    public Block getBlock() {
        return block;
    }

    public void update() {
        double mass = block.getMass();
        double defaultScale = 0.35;
        double fracSize = mass / 1000.0 / 2.0 + defaultScale;
        setAutoRepaint( false );
        setTransform( new AffineTransform() );
        scale( defaultScale, fracSize );

        Point center = getCenter();
        setLocation( center.x - graphic.getWidth() / 2, (int) ( center.y - graphic.getHeight() / 2 + 5 + offsetManager.getOffset() ) );
        setBoundsDirty();//so that the bounds gets recalculated when needed
        setAutoRepaint( true );
        if ( lastCenter == null || lastCenter.equals( center ) ) {

        }
        else {
            autorepaint();
            notifyChanged();
        }
        lastCenter = center;
    }

    public Point getCenter() {
        Dimension dim = computeDimension();
        int x = (int) transform1d.evaluate( block.getPosition() );
        int y = panel.getWalkwayGraphic().getFloorY() - dim.height / 2;
        return new Point( x, y );
    }
}
