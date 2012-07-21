// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.semiconductor.macro.energyprobe;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.math.vector.MutableVector2D;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.semiconductor.common.TransformGraphic;
import edu.colorado.phet.semiconductor.oldphetgraphics.graphics.ShapeGraphic;


/**
 * User: Sam Reid
 * Date: Feb 2, 2004
 * Time: 8:57:33 PM
 */
public class CableGraphic extends TransformGraphic implements SimpleObserver {
    Cable cable;
    LeadGraphic leadGraphic;
    ShapeGraphic shapeGraphic;

    public CableGraphic( ModelViewTransform2D transform, Cable cable, LeadGraphic leadGraphic ) {
        super( transform );
        this.cable = cable;
        this.leadGraphic = leadGraphic;
        shapeGraphic = new ShapeGraphic( new Rectangle(), Color.black, new BasicStroke( 4.0f ) );
        cable.getLead().addObserver( this );
        update();
    }

    public void update() {
        Point tailView = leadGraphic.getTail();
        MutableVector2D attach = cable.getAttachmentPoint();
        Point2D plugModel = attach.toPoint2D();
        Point plugView = getTransform().modelToView( plugModel );

        GeneralPath path = new GeneralPath();
        path.moveTo( plugView.x, plugView.y );
        Point ctrl1 = new Point( plugView.x + 10, plugView.y + 80 );
        Point ctrl2 = new Point( tailView.x, tailView.y + 200 );
        path.curveTo( ctrl1.x, ctrl1.y, ctrl2.x, ctrl2.y, tailView.x, tailView.y );
        shapeGraphic.setShape( path );
    }

    public void paint( Graphics2D graphics2D ) {
        shapeGraphic.paint( graphics2D );
    }
}
