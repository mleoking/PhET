/**
 * Class: CmLines
 * Package: edu.colorado.phet.idealgas.view.monitors
 * Author: Another Guy
 * Date: Sep 17, 2004
 */
package edu.colorado.phet.idealgas.view.monitors;

import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.common.view.phetgraphics.PhetGraphic;
import edu.colorado.phet.idealgas.model.Box2D;
import edu.colorado.phet.idealgas.model.HeavySpecies;
import edu.colorado.phet.idealgas.model.IdealGasModel;
import edu.colorado.phet.idealgas.model.LightSpecies;

import java.awt.*;
import java.awt.geom.Point2D;

public class CmLines extends PhetGraphic implements SimpleObserver {
    private Point2D heavyCm;
    private Point2D lightCm;
    private Box2D box;
    private double boxLeftEdge;
    private double boxLowerEdge;
    private Stroke cmStroke = new BasicStroke( 3.0f );

    public CmLines( Component component, IdealGasModel model ) {
        super( component );
        this.box = model.getBox();
        model.addObserver( this );
    }

    protected Rectangle determineBounds() {
        return null;
    }

    public void update() {
        heavyCm = HeavySpecies.getCm();
        lightCm = LightSpecies.getCm();
        boxLeftEdge = box.getMinX();
        boxLowerEdge = box.getMaxY();
    }

    public void paint( Graphics2D g2 ) {
        Stroke orgStroke = g2.getStroke();
        g2.setStroke( cmStroke );
        if( lightCm.getY() != 0 ) {
            Color oldColor = g2.getColor();
            g2.setColor( Color.red );
            g2.drawLine( (int)boxLeftEdge - 20, (int)( lightCm.getY() ),
                         (int)boxLeftEdge + 18, (int)( lightCm.getY() ) );

            g2.drawLine( (int)( lightCm.getX() ), (int)boxLowerEdge - 20,
                         (int)( lightCm.getX() ), (int)boxLowerEdge + 18 );

            g2.setColor( oldColor );
        }
        if( heavyCm.getY() != 0 ) {
            Color oldColor = g2.getColor();
            g2.setColor( Color.blue );
            g2.drawLine( (int)boxLeftEdge - 20, (int)( heavyCm.getY() ),
                         (int)boxLeftEdge + 18, (int)( heavyCm.getY() ) );

            g2.drawLine( (int)( heavyCm.getX() ), (int)boxLowerEdge - 20,
                         (int)( heavyCm.getX() ), (int)boxLowerEdge + 18 );

            g2.setColor( oldColor );
        }
        g2.setStroke( orgStroke );
    }
}
