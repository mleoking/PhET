/** Sam Reid*/
package edu.colorado.phet.cck3.circuit.components;

import edu.colorado.phet.common.view.graphics.Graphic;
import edu.colorado.phet.common.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.view.graphics.transforms.TransformListener;

import java.awt.*;
import java.awt.geom.Area;

/**
 * User: Sam Reid
 * Date: Jun 13, 2004
 * Time: 8:34:45 PM
 * Copyright (c) Jun 13, 2004 by Sam Reid
 */
public class FilamentGraphic implements Graphic {
    private Filament fil;
    private ModelViewTransform2D transform;
    private BulbComponentGraphic bcg;
    private Color color = Color.black;
    private Stroke stroke = new BasicStroke( 2 );

    public FilamentGraphic( Filament fil, ModelViewTransform2D transform, BulbComponentGraphic bcg ) {
        this.fil = fil;
        this.transform = transform;
        this.bcg = bcg;
        transform.addTransformListener( new TransformListener() {
            public void transformChanged( ModelViewTransform2D mvt ) {
                changed();
            }
        } );
        changed();
        bcg.addIntensityChangeListener( new BulbComponentGraphic.IntensityChangeListener() {
            public void intensityChanged( BulbComponentGraphic bulbComponentGraphic, double intensity ) {
                color = new Color( (float)( intensity ), (float)(intensity*.4), (float)(intensity*.5) );
            }
        } );
    }

    private void changed() {
    }

    public Filament getFilament() {
        return fil;
    }

    public void paint( Graphics2D g ) {
        g.setColor( color );
        g.setStroke( stroke );
        Shape shape = transform.createTransformedShape( fil.getPath() );
//        Area area = new Area( shape );
//        area.subtract( new Area( bcg.getCoverShape() ) );
        Shape origClip = g.getClip();

        Area newClip = new Area( shape.getBounds2D() );
        newClip.subtract( new Area( bcg.getCoverShape() ) );
        g.clip( newClip );
        g.draw( shape );

        g.setClip( origClip );
    }
}
