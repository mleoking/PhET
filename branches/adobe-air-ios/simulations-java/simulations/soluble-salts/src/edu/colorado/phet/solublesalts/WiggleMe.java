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
public class WiggleMe extends PNode implements ModelElement {

    PText text;
    Point2D finalLocation;

    public WiggleMe( String message, Point2D location, double shakeDist, Color color ) {
        text = new PText( message );
        Font font = UIManager.getFont( "Label.font" );
        text.setFont( new Font( font.getName(), font.getStyle(), 24 ) );
        text.setTextPaint( color );
        addChild( text );
        Point2D startLocation = new Point2D.Double( 50, -20 );
        finalLocation = location;
        setOffset( startLocation );
    }

    public void stepInTime( double dt ) {
        double f = 20;
        double dx = finalLocation.getX() - getOffset().getX();
        double dy = finalLocation.getY() - getOffset().getY();

        if ( dx > 1 && dy > 1 ) {
            setOffset( getOffset().getX() + ( dx / f ), getOffset().getY() + ( dy / f ) );
        }
    }
}
