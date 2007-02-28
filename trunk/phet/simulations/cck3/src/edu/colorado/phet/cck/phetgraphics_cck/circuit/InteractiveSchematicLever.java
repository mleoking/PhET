/** Sam Reid*/
package edu.colorado.phet.cck.phetgraphics_cck.circuit;

import edu.colorado.phet.cck.model.components.CircuitComponent;
import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.common_cck.view.ApparatusPanel;
import edu.colorado.phet.common_cck.view.graphics.Boundary;
import edu.colorado.phet.common_cck.view.graphics.DefaultInteractiveGraphic;
import edu.colorado.phet.common_cck.view.graphics.mousecontrols.CursorControl;
import edu.colorado.phet.common_cck.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common_cck.view.util.ImageLoader;

import javax.swing.event.MouseInputAdapter;
import javax.swing.event.MouseInputListener;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * User: Sam Reid
 * Date: Jun 10, 2004
 * Time: 12:50:23 PM
 * Copyright (c) Jun 10, 2004 by Sam Reid
 */
public class InteractiveSchematicLever extends DefaultInteractiveGraphic implements LeverInteraction {
    private ModelViewTransform2D transform;
    private ApparatusPanel apparatusPanel;
    private SchematicLeverGraphic leverGraphic;
    private CircuitComponent component;
    private static Cursor rotate;

    static {
        BufferedImage image = null;
        try {
            image = ImageLoader.loadBufferedImage( "images/hand40.gif" );
            rotate = Toolkit.getDefaultToolkit().createCustomCursor( image, new Point(), SimStrings.get( "InteractiveSchematicLever.CursorName" ) );
        }
        catch( IOException e ) {
            throw new RuntimeException( e );
        }
    }

    public InteractiveSchematicLever( final ModelViewTransform2D transform, final ApparatusPanel apparatusPanel,
                                      final SchematicLeverGraphic leverGraphic ) {
        super( leverGraphic );
        this.leverGraphic = leverGraphic;
        this.transform = transform;
        this.apparatusPanel = apparatusPanel;
        this.component = leverGraphic.getCircuitComponent();
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
                double baseAngle = leverGraphic.getBaseGraphic().getCircuitComponent().getAngle();
                double desiredAngle = -( totalAngle + baseAngle );
                leverGraphic.setRelativeAngle( desiredAngle );
            }
        };
        CursorControl cc = new CursorControl( rotate );
        addMouseInputListener( cc );
        addMouseInputListener( mil );
    }

    private Shape getHandleShape() {
        return leverGraphic.getShape();
    }

    public CircuitComponent getComponent() {
        return component;
    }

    public void delete() {
        leverGraphic.delete();
    }

}
