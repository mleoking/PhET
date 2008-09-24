/*, 2003.*/
package edu.colorado.phet.semiconductor.common;

import java.awt.*;

import edu.colorado.phet.semiconductor.phetcommon.view.graphics.Graphic;
import edu.colorado.phet.semiconductor.phetcommon.view.graphics.transforms.ModelViewTransform2D;

/**
 * User: Sam Reid
 * Date: Jan 18, 2004
 * Time: 2:32:38 PM
 */
public class ClipGraphic extends TransformGraphic {
    private Graphic graphic;
    private Shape modelClip;
    private Shape viewClip;
    static int numInstances = 0;

    public ClipGraphic( final ModelViewTransform2D transform, Graphic graphic, final Shape modelClip ) {
        super( transform );
        this.graphic = graphic;
        this.modelClip = modelClip;
        numInstances++;
        update();
    }

    public void update() {
        viewClip = transform.toAffineTransform().createTransformedShape( modelClip );
    }

    protected void finalize() throws Throwable {
        super.finalize();    //To change body of overridden methods use File | Settings | File Templates.
        numInstances--;
    }

    public void paint( Graphics2D graphics2D ) {
        Shape origClip = graphics2D.getClip();
        graphics2D.setClip( viewClip );
        graphic.paint( graphics2D );
        graphics2D.setClip( origClip );
    }

    public void setModelClip( Shape shape ) {
        this.modelClip = shape;
        update();
    }
}
