package edu.colorado.phet.semiconductor.macro.energy.bands;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.phetcommon.view.util.SimStrings;
import edu.colorado.phet.semiconductor.common.EnergySpaceRegion;
import edu.colorado.phet.semiconductor.macro.energy.EnergySection;
import edu.colorado.phet.semiconductor.phetcommon.view.graphics.Graphic;
import edu.colorado.phet.semiconductor.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.semiconductor.phetcommon.view.graphics.transforms.TransformListener;

/**
 * User: Sam Reid
 * Date: Jan 16, 2004
 * Time: 12:19:30 AM
 */
public class BandGraphic implements Graphic {
    private EnergySection diodeSection;
    Band band;
    private ModelViewTransform2D transform;
    Stroke stroke = new BasicStroke( 2 );

    public BandGraphic( EnergySection diodeSection, Band band, ModelViewTransform2D transform ) {
        this.diodeSection = diodeSection;
        this.band = band;
        this.transform = transform;
        transform.addTransformListener( new TransformListener() {
            public void transformChanged( ModelViewTransform2D modelViewTransform2D ) {
                update();
            }
        } );
    }

    private void update() {
        //To change body of created methods use Options | File Templates.
    }

    public void paint( Graphics2D graphics2D ) {
        //To change body of implemented methods use Options | File Templates.
        AffineTransform tr = transform.toAffineTransform();

        EnergySpaceRegion esr = band.getRegion();
        Rectangle2D.Double rect = new Rectangle2D.Double( esr.getMinX(), esr.getMinEnergy(), esr.getSpatialWidth(), esr.getEnergyRange() );
//        Rectangle2D.Double rect = band.getViewport();
//        System.out.println("rect = " + rect);
        graphics2D.setColor( Color.yellow );
        graphics2D.fill( tr.createTransformedShape( rect ) );

        graphics2D.setStroke( stroke );
        graphics2D.setColor( Color.black );
        for ( int i = 0; i < band.numEnergyLevels(); i++ ) {
            EnergyLevel el = band.energyLevelAt( i );
            Line2D.Double line = new Line2D.Double( el.getRegion().getMinX(), el.getRegion().getMinEnergy(), el.getRegion().getMinX() + el.getRegion().getSpatialWidth(), el.getRegion().getMinEnergy() );
//            Line2D.Double line = el.getLine();
            Shape sh = tr.createTransformedShape( line );
            graphics2D.draw( sh );

            boolean debugCells = false;
            //Paint cell owners as debug
            if ( debugCells ) {
                graphics2D.setColor( Color.black );
                graphics2D.setFont( new PhetFont( 12 ) );
                for ( int k = 0; k < el.numCells(); k++ ) {
                    EnergyCell cell = el.cellAt( k );
                    Point pt = transform.modelToView( cell.getPosition() );
                    String str = SimStrings.get( "BandGraphic.NullText" );
                    BandParticle bp = diodeSection.getBandParticle( cell );
                    if ( bp != null ) {
                        str = bp.toString();
                    }
                    graphics2D.drawString( str, pt.x + 20, pt.y - 10 );
                }
            }
        }
//        Line2D.Double line=band.getTopLine();
//        graphics2D.draw(tr.createTransformedShape(line));
    }
}
