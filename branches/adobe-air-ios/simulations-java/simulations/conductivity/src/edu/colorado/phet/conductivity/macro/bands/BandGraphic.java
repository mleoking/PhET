// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.conductivity.macro.bands;

import java.awt.*;
import java.awt.geom.AffineTransform;

import edu.colorado.phet.conductivity.oldphetgraphics.Graphic;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.TransformListener;

// Referenced classes of package edu.colorado.phet.semiconductor.macro.bands:
//            Band, EnergyLevel

public class BandGraphic
        implements Graphic {

    public BandGraphic( Band band1, ModelViewTransform2D modelviewtransform2d ) {
        stroke = new BasicStroke( 2.0F );
        band = band1;
        transform = modelviewtransform2d;
        modelviewtransform2d.addTransformListener( new TransformListener() {

            public void transformChanged( ModelViewTransform2D modelviewtransform2d1 ) {
                update();
            }

        } );
    }

    private void update() {
    }

    public void paint( Graphics2D graphics2d ) {
        Stroke origStroke = graphics2d.getStroke();
        AffineTransform affinetransform = transform.getAffineTransform();
        java.awt.geom.Rectangle2D.Double double1 = band.getBounds();
        graphics2d.setColor( Color.yellow );
        graphics2d.fill( affinetransform.createTransformedShape( double1 ) );
        graphics2d.setStroke( stroke );
        graphics2d.setColor( Color.black );
        for ( int i = 0; i < band.numEnergyLevels(); i++ ) {
            EnergyLevel energylevel = band.energyLevelAt( i );
            java.awt.geom.Line2D.Double double3 = energylevel.getLine();
            java.awt.Shape shape = affinetransform.createTransformedShape( double3 );
            graphics2d.draw( shape );
        }

        java.awt.geom.Line2D.Double double2 = band.getTopLine();
        graphics2d.draw( affinetransform.createTransformedShape( double2 ) );
        graphics2d.setStroke( origStroke );
    }

    Band band;
    private ModelViewTransform2D transform;
    Stroke stroke;

}
