/** Sam Reid*/
package edu.colorado.phet.cck3.circuit.components;

import edu.colorado.phet.cck3.circuit.Junction;
import edu.colorado.phet.cck3.circuit.KirkhoffListener;
import edu.colorado.phet.common.math.AbstractVector2D;
import net.n3.nanoxml.IXMLElement;

import java.awt.geom.Point2D;

/**
 * User: Sam Reid
 * Date: May 28, 2004
 * Time: 1:11:17 PM
 * Copyright (c) May 28, 2004 by Sam Reid
 */
public class Resistor extends CircuitComponent {
    public Resistor( Point2D start, AbstractVector2D dir, double length, double height, KirkhoffListener kl ) {
        super( kl, start, dir, length, height );
        setResistance( 10 );
    }

    public Resistor( KirkhoffListener kl, Junction startJunction, Junction endjJunction,double length,double height ) {
        super( kl, startJunction, endjJunction,length, height );
    }

    public void addAttributes( IXMLElement xml ) {
        xml.setAttribute( "resistance", getResistance() + "" );
    }

//    public static Resistor parseXML( IXMLElement xml, Junction startJunction, Junction endJunction, KirkhoffListener kl ) {
//        Resistor r = new Resistor( kl, startJunction, endJunction );
//        String res = xml.getAttribute( "resistance", "0" );
//        double rx = Double.parseDouble( res );
//        r.setResistance( rx );
//        return r;
//    }

}
