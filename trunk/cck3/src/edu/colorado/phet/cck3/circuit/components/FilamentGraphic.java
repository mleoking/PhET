/** Sam Reid*/
package edu.colorado.phet.cck3.circuit.components;

import edu.colorado.phet.cck3.circuit.components.Filament;
import edu.colorado.phet.common.view.graphics.Graphic;
import edu.colorado.phet.common.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.view.graphics.transforms.TransformListener;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: Jun 13, 2004
 * Time: 8:34:45 PM
 * Copyright (c) Jun 13, 2004 by Sam Reid
 */
public class FilamentGraphic implements Graphic {
    private Filament fil;
    private ModelViewTransform2D transform;
    private Color color = Color.black;
    private Stroke stroke = new BasicStroke( 2 );

    public FilamentGraphic( Filament fil, ModelViewTransform2D transform ) {
        this.fil = fil;
        this.transform = transform;
        transform.addTransformListener( new TransformListener() {
            public void transformChanged( ModelViewTransform2D mvt ) {
                changed();
            }
        } );
        changed();
    }

    private void changed() {
    }

    public Filament getFilament() {
        return fil;
    }

    public void paint( Graphics2D g ) {
        g.setColor( color );
        g.setStroke( stroke );
        g.draw( transform.createTransformedShape( fil.getPath() ) );
    }
}
