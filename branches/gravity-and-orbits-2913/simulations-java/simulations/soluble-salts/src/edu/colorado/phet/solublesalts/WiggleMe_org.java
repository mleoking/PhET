// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.solublesalts;

import java.awt.*;
import java.awt.geom.Point2D;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.model.ModelElement;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * WiggleMe
 *
 * @author Ron LeMaster
 */
public class WiggleMe_org extends PNode implements ModelElement {

    static final int DOWN = 1;
    static final int UP = -1;
    static final int TOTAL_NUM_CYCLES = 4;

    PText text;
    Point2D startLocation;
    double maxY;
    double x, y;
    int direction = 1;
    int numCycles;

    public WiggleMe_org( String message, Point2D location, double shakeDist, Color color ) {
        text = new PText( message );
        Font font = UIManager.getFont( "Label.font" );
        text.setFont( new Font( font.getName(), font.getStyle(), 24 ) );
        text.setTextPaint( color );
        addChild( text );
        startLocation = location;
        x = startLocation.getX();
        y = startLocation.getY();
        maxY = startLocation.getY() + shakeDist;
        setOffset( location );
    }

    public void stepInTime( double dt ) {
        if ( numCycles < TOTAL_NUM_CYCLES ) {

            double dy = 0;
            if ( direction == WiggleMe_org.DOWN ) {
                dy = 15;
            }
            else if ( direction == WiggleMe_org.UP ) {
                dy = -5;
            }

            y += dy;
            setOffset( x, y );

            if ( y < startLocation.getY() ) {
                direction = WiggleMe_org.DOWN;
            }
            else if ( y > maxY ) {
                direction = WiggleMe_org.UP;
                numCycles++;
            }
        }
    }
}
