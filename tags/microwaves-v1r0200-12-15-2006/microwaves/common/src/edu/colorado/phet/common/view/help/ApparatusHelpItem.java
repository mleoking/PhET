/**
 * Class: ApparatusHelpItem
 * Package: edu.colorado.phet.common.view.help
 * Author: Another Guy
 * Date: Oct 7, 2003
 */
package edu.colorado.phet.common.view.help;

import edu.colorado.phet.common.view.graphics.Graphic;
import edu.colorado.phet.common.view.graphics.TransformListener;
import edu.colorado.phet.common.view.graphics.ModelViewTransform2D;

import java.awt.geom.Point2D;
import java.awt.geom.AffineTransform;
import java.awt.*;

public class ApparatusHelpItem extends HelpItem implements Graphic, TransformListener {

    private Point2D.Double modelLoc;
    private ModelViewTransform2D tx;

    public void paint( Graphics2D g ) {
        super.paint( g );
    }

    public ApparatusHelpItem(String text, Point2D.Double location, ModelViewTransform2D tx ) {
        this(text, location, Color.BLACK, tx );
    }

    public ApparatusHelpItem(String text, Point2D.Double location, Color color, ModelViewTransform2D tx ) {
        this( text, (float)location.getX(), (float)location.getY(), tx );
    }

    public ApparatusHelpItem( String text, double x, double y, ModelViewTransform2D tx ) {
        super( text,  new Point2D.Float( (float)x, (float)y ));
        modelLoc = new Point2D.Double(x, y);
        this.tx = tx;
        transformChanged( tx );
        tx.addTransformListener( this );
    }

    public Point2D.Double getModelLoc() {
        return modelLoc;
    }

    public void setModelLoc( Point2D.Double location ) {
        this.modelLoc = location;
    }

    public void transformChanged( ModelViewTransform2D mvt ) {
        Point2D.Double upperLeft = new Point2D.Double( this.getModelLoc().getX(), this.getModelLoc().getY() );
        tx.toAffineTransform().transform( upperLeft, upperLeft );
        super.setLocation( (int)upperLeft.getX(), (int)upperLeft.getY() );
    }
}
