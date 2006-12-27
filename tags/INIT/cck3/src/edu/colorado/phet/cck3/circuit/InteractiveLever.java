/** Sam Reid*/
package edu.colorado.phet.cck3.circuit;

import edu.colorado.phet.cck3.circuit.components.CircuitComponent;
import edu.colorado.phet.cck3.circuit.components.CircuitComponentImageGraphic;
import edu.colorado.phet.cck3.common.CursorControl;
import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.common.view.graphics.DefaultInteractiveGraphic;
import edu.colorado.phet.common.view.graphics.bounds.Boundary;
import edu.colorado.phet.common.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.view.util.ImageLoader;

import javax.swing.event.MouseInputAdapter;
import javax.swing.event.MouseInputListener;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * User: Sam Reid
 * Date: Jun 10, 2004
 * Time: 12:50:23 PM
 * Copyright (c) Jun 10, 2004 by Sam Reid
 */
public class InteractiveLever extends DefaultInteractiveGraphic {
    private ModelViewTransform2D transform;
    private ApparatusPanel apparatusPanel;
    private LeverGraphic leverGraphic;
    private CircuitComponent component;
    private CircuitComponentImageGraphic switchGraphic;

    private static Cursor rotate;

    static {
        BufferedImage image = null;
        try {
//            image = ImageLoader.loadBufferedImage( "images/rot11.gif" );
            image = ImageLoader.loadBufferedImage( "images/hand40.gif" );
            rotate = Toolkit.getDefaultToolkit().createCustomCursor( image, new Point(), "rotator2" );
        }
        catch( IOException e ) {
            throw new RuntimeException( e );
        }
    }

    public CircuitComponent getComponent() {
        return component;
    }

    public InteractiveLever( final ModelViewTransform2D transform, final ApparatusPanel apparatusPanel,
                             final LeverGraphic leverGraphic ) {
        super( leverGraphic );
        this.leverGraphic = leverGraphic;
        this.switchGraphic = leverGraphic.getBaseGraphic();
        this.transform = transform;
        this.apparatusPanel = apparatusPanel;
        this.component = switchGraphic.getComponent();
        Boundary mybounds = new Boundary() {
            public boolean contains( int x, int y ) {
                //only allowed to move the handle.  This will make it easier to move the base when the switch is closed.
                //let's assume this is the leftmost 30% of this lever
                Shape sh = getHandleShape();
                boolean cont = sh.contains( x, y );
                return cont;
            }
        };
        setBoundary( mybounds );
        MouseInputListener mil = new MouseInputAdapter() {
            public void mouseDragged( MouseEvent e ) {
                Point2D pivot = leverGraphic.getPivotViewLocation();
                double totalAngle = new Vector2D.Double( pivot, e.getPoint() ).getAngle();
                double baseAngle = leverGraphic.getBaseGraphic().getComponent().getAngle();
                double desiredAngle = -( totalAngle + baseAngle );
                leverGraphic.setRelativeAngle( desiredAngle );
            }
        };
        CursorControl cc = new CursorControl( rotate );
        addMouseInputListener( cc );
        addMouseInputListener( mil );
    }

    private Shape getHandleShape() {
        double x = 0;
        double y = 0;
        double w = leverGraphic.getBufferedImage().getWidth() * .4;
        double h = leverGraphic.getBufferedImage().getHeight();

        Rectangle2D r = new Rectangle2D.Double( x, y, w, h );
        Shape sh = leverGraphic.getTransform().createTransformedShape( r );
        return sh;
    }

}
