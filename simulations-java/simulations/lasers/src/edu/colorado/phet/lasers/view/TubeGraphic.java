/**
 * Class: ResonatingGraphic
 * Package: edu.colorado.phet.lasers.view
 * Author: Another Guy
 * Date: Mar 26, 2003
 */
package edu.colorado.phet.lasers.view;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;

import edu.colorado.phet.common.phetgraphics.view.phetgraphics.CompositePhetGraphic;
import edu.colorado.phet.common.phetgraphics.view.phetgraphics.PhetShapeGraphic;
import edu.colorado.phet.common.quantum.model.Tube;
import edu.colorado.phet.lasers.controller.LasersConfig;

public class TubeGraphic extends CompositePhetGraphic implements Tube.ChangeListener {

    private Ellipse2D end1 = new Ellipse2D.Double();
    private Ellipse2D end2 = new Ellipse2D.Double();
    private Line2D top = new Line2D.Double();
    private Line2D bottom = new Line2D.Double();
    private Rectangle bounds = new Rectangle();
    private Tube cavity;
    private Stroke stroke = new BasicStroke( 1.5f );
    private Color color = Color.black;

    public TubeGraphic( Component component, Tube cavity ) {
        super( component );
        this.cavity = cavity;
        addGraphic( new PhetShapeGraphic( component, end1, stroke, color ) );
        addGraphic( new PhetShapeGraphic( component, end2, stroke, color ) );
        addGraphic( new PhetShapeGraphic( component, top, stroke, color ) );
        addGraphic( new PhetShapeGraphic( component, bottom, stroke, color ) );
        setRenderingHints( new RenderingHints( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON ) );

        cavity.addListener( this );
        update();
    }

    protected Rectangle determineBounds() {
        return bounds;
    }

    public void update() {
        double mirrorPerspecitveWidth = LasersConfig.MIRROR_THICKNESS;
        top.setLine( cavity.getMinX(), cavity.getMinY(), cavity.getMinX() + cavity.getWidth(), cavity.getMinY() );
        bottom.setLine( cavity.getMinX(), cavity.getMaxY(), cavity.getMinX() + cavity.getWidth(), cavity.getMaxY() );
        end1.setFrame( cavity.getMinX() - mirrorPerspecitveWidth / 2, cavity.getMinY(),
                       mirrorPerspecitveWidth, cavity.getHeight() );
        end2.setFrame( cavity.getMinX() + cavity.getWidth() - mirrorPerspecitveWidth / 2, cavity.getMinY(),
                       mirrorPerspecitveWidth, cavity.getHeight() );
        bounds.setRect( end1.getMinX(), end1.getMinY(), end2.getMaxX() - end1.getMinX(), end2.getMaxY() - end1.getMinY() );
    }

    public void CavityChanged( Tube.ChangeEvent event ) {
        update();
    }
}
