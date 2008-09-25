/*, 2003.*/
package edu.colorado.phet.semiconductor.macro.energy;

import java.awt.*;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.semiconductor.common.TransformGraphic;
import edu.colorado.phet.semiconductor.util.math.PhetVector;
import edu.colorado.phet.semiconductor.phetcommon.view.graphics.shapes.ArrowShape;
import edu.colorado.phet.semiconductor.phetcommon.view.graphics.transforms.ModelViewTransform2D;

/**
 * User: Sam Reid
 * Date: Mar 15, 2004
 * Time: 9:59:23 AM
 */
public class ElectricFieldGraphic extends TransformGraphic {
    private String name;
    private ElectricField field;
    private Font font = new PhetFont( Font.BOLD, 14 );

    public ElectricFieldGraphic( String name, ElectricField field, ModelViewTransform2D transform ) {
        super( transform );
        this.name = name;
        this.field = field;
    }

    public void update() {
    }

    public void paint( Graphics2D g ) {
        g.setColor( Color.black );
        int h = 0;
        if ( field.getStrength() != 0 ) {
            try {
                PhetVector dest = field.getCenter().getAddedInstance( field.getStrength(), 0 );
                PhetVector start = field.getCenter();

                PhetVector dir = dest.getSubtractedInstance( start );
                PhetVector mid = start.getAddedInstance( dir.getScaledInstance( .5 ) );
                PhetVector dx = start.getSubtractedInstance( mid );

                start = start.getAddedInstance( dx );
                dest = dest.getAddedInstance( dx );

                double tailWidth = .1;
                double headWidth = .2;
                double headHeight = .1;
                double dist = start.getSubtractedInstance( dest ).getMagnitude();
                if ( dist < headHeight ) {
                    headHeight = dist * .9;
//                    headWidth=headHeight;
//                    tailWidth=headHeight/2;
                }
                ArrowShape as = new ArrowShape( start, dest, headHeight, headWidth, tailWidth );

                Shape sh = as.getArrowShape();
                Shape viewShape = super.createTransformedShape( sh );
                h = viewShape.getBounds().height;
                g.fill( viewShape );
//        double strength = field.getStrength();
//        int viewdx = super.getTransform().modelToViewDifferentialX(strength);
            }
            catch( RuntimeException re ) {
                re.printStackTrace();
            }
        }
        Point ctr = super.getTransform().modelToView( field.getCenter() );
//        g.fillRect(ctr.x, ctr.y, viewdx, 2);
        g.setColor( Color.blue );
        g.setFont( font );
        String text = "" + name;
        if ( field.getStrength() == 0 ) {
            text += "=0";
            h = -getTransform().modelToViewDifferentialY( .2 );
        }
        Rectangle2D textBounds = font.getStringBounds( text, g.getFontRenderContext() );
        double dx = textBounds.getWidth() / 2;
        int dxint = (int) dx;
        g.drawString( text, ctr.x - dxint, ctr.y - h );
    }
}
