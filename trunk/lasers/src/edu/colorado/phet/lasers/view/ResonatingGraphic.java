/**
 * Class: ResonatingGraphic
 * Package: edu.colorado.phet.lasers.view
 * Author: Another Guy
 * Date: Mar 26, 2003
 */
package edu.colorado.phet.lasers.view;

import edu.colorado.phet.common.view.phetgraphics.PhetGraphic;
import edu.colorado.phet.common.view.util.GraphicsState;
import edu.colorado.phet.common.view.util.GraphicsUtil;
import edu.colorado.phet.lasers.controller.LaserConfig;
import edu.colorado.phet.lasers.model.ResonatingCavity;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

public class ResonatingGraphic extends PhetGraphic implements ResonatingCavity.ChangeListener {

    private Rectangle2D rep = new Rectangle2D.Double();
    private Ellipse2D end1 = new Ellipse2D.Double();
    private Ellipse2D end2 = new Ellipse2D.Double();
    private ResonatingCavity cavity;
    private Stroke stroke = new BasicStroke( 1.5f );

    public ResonatingGraphic( Component component, ResonatingCavity cavity ) {
        super( component );
        this.cavity = cavity;
        cavity.addListener( this );
        update();
    }

    protected Rectangle determineBounds() {
        return rep.getBounds();
    }

    public void paint( Graphics2D g ) {
        GraphicsState gs = new GraphicsState( g );

        GraphicsUtil.setAntiAliasingOn( g );
        g.setColor( Color.black );
        g.setStroke( stroke );
        g.draw( rep );
        g.setColor( Color.white );
        g.fill( end1 );
        g.fill( end2 );
        g.setColor( Color.black );
        g.draw( end1 );
        g.draw( end2 );

        gs.restoreGraphics();
    }

    public void update() {
        double mirrorPerspecitveWidth = LaserConfig.MIRROR_THICKNESS;
        rep.setRect( cavity.getMinX(), cavity.getMinY(), cavity.getWidth(), cavity.getHeight() );
        end1.setFrame( cavity.getMinX() - mirrorPerspecitveWidth / 2, cavity.getMinY(),
                       mirrorPerspecitveWidth, cavity.getHeight() );
        end2.setFrame( cavity.getMinX() + cavity.getWidth() - mirrorPerspecitveWidth / 2, cavity.getMinY(),
                       mirrorPerspecitveWidth, cavity.getHeight() );
    }

    public void CavityChanged( ResonatingCavity.ChangeEvent event ) {
        update();
    }
}
