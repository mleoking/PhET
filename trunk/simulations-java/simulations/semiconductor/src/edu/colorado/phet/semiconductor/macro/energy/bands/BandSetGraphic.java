/*, 2003.*/
package edu.colorado.phet.semiconductor.macro.energy.bands;

import java.awt.*;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.TransformListener;
import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.semiconductor.common.ClipGraphic;
import edu.colorado.phet.semiconductor.common.TransformGraphic;
import edu.colorado.phet.semiconductor.macro.energy.EnergySection;

import edu.colorado.phet.semiconductor.phetcommon.view.CompositeInteractiveGraphic;
import edu.colorado.phet.semiconductor.oldphetgraphics.graphics.ShapeGraphic;


import edu.colorado.phet.semiconductor.util.RectangleUtils;

/**
 * User: Sam Reid
 * Date: Jan 18, 2004
 * Time: 6:21:26 PM
 */
public class BandSetGraphic extends TransformGraphic implements BandParticleObserver {
    CompositeInteractiveGraphic graphic = new CompositeInteractiveGraphic();
    ClipGraphic clipGraphic;
    //    private boolean showPlusses = true;
    ShapeGraphic backgroundWhite;
    ShapeGraphic backgroundBorder;
    private ChargeCountGraphic chargeCountGraphic;
    protected SemiconductorBandSet bandSet;
    private Rectangle2D.Double viewport;
    private boolean paintLevelIDs = false;
//    private boolean paintLevelIDs = true;

    public BandSetGraphic( EnergySection diodeSection, ModelViewTransform2D transform, final SemiconductorBandSet bandSet, final Rectangle2D.Double viewport ) {
        super( transform );
        this.bandSet = bandSet;
        this.viewport = viewport;
        backgroundWhite = new ShapeGraphic( viewport, Color.white );
        backgroundBorder = new ShapeGraphic( viewport, Color.blue, new BasicStroke( 3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND ) );
        transform.addTransformListener( new TransformListener() {
            public void transformChanged( ModelViewTransform2D modelViewTransform2D ) {
                Shape shape = getTransform().getAffineTransform().createTransformedShape( viewport );
                backgroundWhite.setShape( shape );
                backgroundBorder.setShape( shape );
            }
        } );

        clipGraphic = new ClipGraphic( getTransform(), graphic, viewport );

        BandGraphic upperGraphic = new BandGraphic( diodeSection, bandSet.getConductionBand(), transform );
        graphic.addGraphic( upperGraphic, 1 );
        BandGraphic lowerGraphic = new BandGraphic( diodeSection, bandSet.getValenceBand(), transform );
        graphic.addGraphic( lowerGraphic, 1 );
        BandGraphic botGraphic = new BandGraphic( diodeSection, bandSet.getBottomBand(), transform );
        graphic.addGraphic( botGraphic, 1 );
        BandGraphic topGraphic = new BandGraphic( diodeSection, bandSet.getTopBand(), transform );
        graphic.addGraphic( topGraphic, 1 );
//        graphic.addGraphic(plusCGraphic, 2);
        graphic.addGraphic( backgroundWhite, -1 );
        graphic.addGraphic( backgroundBorder, 3 );

        chargeCountGraphic = new ChargeCountGraphic( diodeSection, this, transform );
    }

    public void update() {
    }

    public void paint( Graphics2D graphics2D ) {
        clipGraphic.paint( graphics2D );//use this clip
//        graphic.paint(graphics2D);

        chargeCountGraphic.paint( graphics2D );
        if ( paintLevelIDs ) {
            SemiconductorBandSet.EnergyLevelIterator it = bandSet.energyLevelIterator();
            Font font = new PhetFont( 10 );
            Color color = Color.black;
            graphics2D.setFont( font );
            graphics2D.setColor( color );
            while ( it.hasNext() ) {
                EnergyLevel energyLevel = (EnergyLevel) it.next();
                Rectangle2D rect = energyLevel.getRegion().toRectangle();
                Vector2D.Double ctr = RectangleUtils.getCenter( rect );
                Point viewpt = getTransform().modelToView( ctr );

                graphics2D.drawString( energyLevel.getID() + "", viewpt.x, viewpt.y );
            }
        }
    }

    public Rectangle2D.Double getViewport() {
        return viewport;
    }

    public Vector2D.Double getViewportBottomCenter() {
        return new Vector2D.Double( getViewport().getX() + getViewport().getWidth() / 2, getViewport().getY() );
    }

}
