/**
 * Class: ResonatingCavityGraphic
 * Package: edu.colorado.phet.lasers.view
 * Author: Another Guy
 * Date: Mar 26, 2003
 */
package edu.colorado.phet.lasers.view;

import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.common.view.phetgraphics.PhetGraphic;
import edu.colorado.phet.common.view.util.GraphicsState;
import edu.colorado.phet.lasers.model.ResonatingCavity;

import java.awt.*;
import java.awt.geom.Rectangle2D;

public class ResonatingCavityGraphic extends PhetGraphic implements SimpleObserver {

    private Rectangle2D rep = new Rectangle2D.Double();
    private ResonatingCavity cavity;

    public ResonatingCavityGraphic( Component component, ResonatingCavity cavity ) {
        super( component );
        this.cavity = cavity;
        cavity.addObserver( this );
        update();
//        rep = new Rectangle2D.Double( cavity.getOrigin().getX(),
//                                      cavity.getOrigin().getY(),
//                                      cavity.getWidth(),
//                                      cavity.getHeight() );
        //        this.setFill( new Color( 255, 240, 240 ) );
    }

    protected Rectangle determineBounds() {
        return rep.getBounds();
    }

    public void paint( Graphics2D g ) {
        GraphicsState gs = new GraphicsState( g );
        g.draw( rep );
        gs.restoreGraphics();
    }

    public void update() {
        rep.setRect( cavity.getMinX(), cavity.getMinY(), cavity.getWidth(), cavity.getHeight() );
    }
}
