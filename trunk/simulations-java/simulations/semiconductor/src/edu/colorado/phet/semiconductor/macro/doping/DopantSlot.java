package edu.colorado.phet.semiconductor.macro.doping;

import java.awt.*;
import java.io.IOException;

import edu.colorado.phet.semiconductor.util.math.PhetVector;
import edu.colorado.phet.semiconductor.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.semiconductor.phetcommon.view.graphics.transforms.TransformListener;
import edu.colorado.phet.semiconductor.util.RectangleUtils;

public class DopantSlot implements TransformListener {
    DopantType dopant;
    Shape modelShape;
    Shape viewShape;
    ModelViewTransform2D transform;
    private Color defaultColor;
    DopantSlotGraphic graphic;

    public DopantSlot( DopantType dopant, final Shape modelShape, final ModelViewTransform2D transform, Color color ) throws IOException {
        this.dopant = dopant;
        this.modelShape = modelShape;
        this.transform = transform;
        this.defaultColor = color;
        graphic = new DopantSlotGraphic( viewShape, getDopantType() );
        transform.addTransformListener( this );
        transformChanged( transform );
    }

    public void setDopantType( DopantType dopantType ) {
        this.dopant = dopantType;
        graphic.setDopantType( dopantType );
    }

    public Shape getViewShape() {
        return viewShape;
    }

    public DopantType getDopantType() {
        return dopant;
    }

    public PhetVector getModelCenter() {
        PhetVector center = RectangleUtils.getCenter( modelShape.getBounds2D() );
        return center;
    }

    public void paint( Graphics2D g ) {
        g.setColor( defaultColor );
        g.fill( viewShape );
        graphic.paint( g );
    }

    public void transformChanged( ModelViewTransform2D mvt ) {
        viewShape = transform.toAffineTransform().createTransformedShape( modelShape );
        graphic.setShape( viewShape );
    }
}
