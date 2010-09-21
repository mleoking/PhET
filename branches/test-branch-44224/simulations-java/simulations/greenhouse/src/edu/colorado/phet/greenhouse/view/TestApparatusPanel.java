/**
 * Class: TestApparatusPanel
 * Package: edu.colorado.phet.greenhouse
 * Author: Another Guy
 * Date: Dec 8, 2003
 */
package edu.colorado.phet.greenhouse.view;

import java.awt.geom.Rectangle2D;

import edu.colorado.phet.greenhouse.common.graphics.ApparatusPanel;

public class TestApparatusPanel extends ApparatusPanel {

    public TestApparatusPanel( double aspectRatio, FlipperAffineTransformFactory flipperAffineTransformFactory ) {
        super( flipperAffineTransformFactory );
    }

    public void setModelBounds( Rectangle2D.Double bounds ) {
        super.setAffineTransformFactory( new FlipperAffineTransformFactory( bounds ) );
        super.updateTransform();
    }
}
