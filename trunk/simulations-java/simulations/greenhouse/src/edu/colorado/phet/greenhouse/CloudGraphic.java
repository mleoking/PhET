/**
 * Class: CloudGraphic
 * Class: edu.colorado.phet.greenhouse
 * User: Ron LeMaster
 * Date: Oct 12, 2003
 * Time: 4:59:46 PM
 */
package edu.colorado.phet.greenhouse;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.util.Random;

import edu.colorado.phet.greenhouse.common_greenhouse.view.graphics.Graphic;
import edu.colorado.phet.greenhouse.coreadditions.graphics.ShapeGraphicType;

public class CloudGraphic implements Graphic, ShapeGraphicType {

    static Random random = new Random();

    private Ellipse2D.Double baseOval = new Ellipse2D.Double();
    private Ellipse2D[] auxillaryOvals = new Ellipse2D[8];
    private Cloud cloud;
    private Paint cloudPaint = new Color( 230, 230, 230 );
//    private Paint cloudPaint = Color.gray;

    public CloudGraphic( Cloud cloud ) {
        this.cloud = cloud;
        for ( int i = 0; i < auxillaryOvals.length; i++ ) {
            auxillaryOvals[i] = new Ellipse2D.Double();
        }
        this.update();
    }

    public void paint( Graphics2D g ) {
        RenderingHints orgRenderingHints = g.getRenderingHints();
        g.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
        g.setPaint( cloudPaint );
        g.fill( baseOval );

//        g.setPaint( Color.white );
        for ( int i = 0; i < auxillaryOvals.length; i++ ) {
            Ellipse2D auxillaryOval = auxillaryOvals[i];
            g.fill( auxillaryOval );
        }
        g.setRenderingHints( orgRenderingHints );
    }

    public void update() {
        baseOval = cloud.getBounds();
        for ( int i = 0; i < auxillaryOvals.length; i++ ) {
            double height = Math.max( random.nextDouble() * baseOval.getHeight(), baseOval.getHeight() / 4 );
            double width = Math.max( random.nextDouble() * ( baseOval.getWidth() / 3 ), height * 8 );
            double dx = random.nextDouble() * ( baseOval.getWidth() / 3 ) * ( random.nextBoolean() ? 1 : -1 );
            double x = baseOval.getX() + baseOval.getWidth() / 2 + dx;
            double y = baseOval.getY() + ( random.nextBoolean() ? 1 : 0 ) * baseOval.getHeight() - height / 2;
            auxillaryOvals[i].setFrame( x, y, width, height );
        }

    }
}
