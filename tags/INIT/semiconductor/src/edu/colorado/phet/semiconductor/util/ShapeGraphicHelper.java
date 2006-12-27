/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.semiconductor.util;

import edu.colorado.phet.common.view.graphics.Graphic;
import edu.colorado.phet.common.view.graphics.ShapeGraphic;
import edu.colorado.phet.common.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.view.graphics.transforms.TransformListener;

import java.awt.*;
import java.awt.geom.GeneralPath;

public class ShapeGraphicHelper implements Graphic {
    private GeneralPath originalPath;
    ShapeGraphic sg;
    ModelViewTransform2D transform;
    private Shape viewPath;

    public ShapeGraphicHelper( GeneralPath originalPath, ShapeGraphic sg, ModelViewTransform2D transform ) {
        this.originalPath = originalPath;
        this.sg = sg;
        this.transform = transform;
        transform.addTransformListener( new TransformListener() {
            public void transformChanged( ModelViewTransform2D modelViewTransform2D ) {
                doUpdate();
            }
        } );
        doUpdate();
    }

    private void doUpdate() {
        this.viewPath = transform.toAffineTransform().createTransformedShape( originalPath );
        sg.setShape( viewPath );
    }

    public void paint( Graphics2D graphics2D ) {
        sg.paint( graphics2D );
    }

}
