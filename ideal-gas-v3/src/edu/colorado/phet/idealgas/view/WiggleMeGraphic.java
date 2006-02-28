/**
 * Class: WiggleMeGraphic
 * Package: edu.colorado.phet.idealgas.view
 * Author: Another Guy
 * Date: Sep 27, 2004
 */
package edu.colorado.phet.idealgas.view;

import edu.colorado.phet.common.model.BaseModel;
import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.common.view.phetgraphics.CompositePhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetMultiLineTextGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.common.view.graphics.Arrow;
import edu.colorado.phet.idealgas.IdealGasConfig;

import java.awt.*;
import java.awt.geom.Point2D;

public class WiggleMeGraphic extends CompositePhetGraphic {

    private BaseModel model;
    String family = "Sans Serif";
    int style = Font.BOLD;
    int size = 16;
    Font font = new Font( family, style, size );
    private Color color = IdealGasConfig.HELP_COLOR;
    private ModelElement wiggleMeModelElement;

    public WiggleMeGraphic( final Component component, final Point2D.Double startLocation, BaseModel model ) {
        super( component );
        this.model = model;

        PhetMultiLineTextGraphic textGraphic = new PhetMultiLineTextGraphic( component, font,
                                                                             new String[]{SimStrings.get( "WiggleMe.Pump_the" ),
                                                                                          SimStrings.get( "WiggleMe.handle!" )}, color );
        addGraphic( textGraphic, 0 );
        Arrow arrow = new Arrow( new Point2D.Double( 0,0 ),
                                 new Point2D.Double( 15, 12 ), 6, 6, 2, 100, false );
        PhetShapeGraphic arrowGraphic = new PhetShapeGraphic( component, arrow.getShape(), color );
        arrowGraphic.setLocation(  80, 20 );
        addGraphic( arrowGraphic, 1 );


        wiggleMeModelElement = new ModelElement() {
            double cnt = 0;

            public void stepInTime( double dt ) {
                cnt += 0.1;
                setLocation( (int)(startLocation.getX() + 30 * Math.cos( cnt )),
                             (int)(startLocation.getY() + 15 * Math.sin( cnt ))  );
                setBoundsDirty();
                repaint();
            }
        };
    }

    public void start() {
        model.addModelElement( wiggleMeModelElement );
    }

    public void kill() {
        model.removeModelElement( wiggleMeModelElement );
    }
}
