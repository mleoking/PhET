// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.solublesalts.view;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;

import edu.colorado.phet.solublesalts.model.crystal.Bond;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;

/**
 * BondGraphic
 *
 * @author Ron LeMaster
 */
public class BondGraphic extends PNode implements Bond.ChangeListener {

    static Color openColor = Color.green;
    static Color closedColor = Color.black;

    private PPath bondLine;

    public BondGraphic( Bond bond ) {
        createGraphic( bond );
        bond.addChangeListener( this );
    }

    private void createGraphic( Bond bond ) {
        if ( bond == null ) {
            throw new IllegalArgumentException();
        }
        if ( bondLine != null ) {
            removeChild( bondLine );
            bondLine = null;
        }
        if ( bond.getOrigin() != null ) {
            Stroke stroke = new BasicStroke( 2 );
            Line2D line;
            Color color;
            if ( bond.isOpen() ) {
                line = new Line2D.Double( bond.getOrigin().getPosition(), bond.getOpenPosition() );
                color = openColor;
            }
            else {
                line = new Line2D.Double( bond.getOrigin().getPosition(), bond.getDestination().getPosition() );
                color = closedColor;
            }

            if ( bond.isDebugEnabled() ) {
                color = Color.red;
            }

            bondLine = new PPath( line, stroke );
            bondLine.setPaint( color );
            bondLine.setStrokePaint( color );
            addChild( bondLine );

            if ( bond.isOpen() ) {
                Ellipse2D e = new Ellipse2D.Double( bond.getOpenPosition().getX(), bond.getOpenPosition().getY(), 2, 2 );
                PPath pp = new PPath( e );
                pp.setStrokePaint( Color.blue );
            }
        }
    }

    public void stateChanged( Bond.ChangeEvent event ) {
        if ( event.getBond() == null || !( event.getBond() instanceof Bond ) ) {
            throw new IllegalArgumentException();
        }
        createGraphic( event.getBond() );
    }
}
