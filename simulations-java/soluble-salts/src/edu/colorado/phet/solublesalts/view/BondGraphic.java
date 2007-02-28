/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.solublesalts.view;

import edu.colorado.phet.solublesalts.model.crystal.Bond;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

/**
 * BondGraphic
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class BondGraphic extends PNode implements Bond.ChangeListener {

    static Color openColor = Color.green;
    static Color closedColor = Color.black;

    private PPath bondLine;
    private Bond bond;

    public BondGraphic(Bond bond) {
        this.bond = bond;
        createGraphic(bond);
        bond.addChangeListener(this);
    }

    private void createGraphic(Bond bond) {
        if (bond == null || !(bond instanceof Bond)) {
            throw new IllegalArgumentException();
        }
        if (bondLine != null) {
            removeChild(bondLine);
            bondLine = null;
        }
        if (bond.getOrigin() != null) {
            Stroke stroke = new BasicStroke(2);
            Line2D line = null;
            Color color = null;
            if (bond.isOpen()) {
                line = new Line2D.Double(bond.getOrigin().getPosition(), bond.getOpenPosition());
                color = openColor;
            } else {
                line = new Line2D.Double(bond.getOrigin().getPosition(), bond.getDestination().getPosition());
                color = closedColor;
            }

            if (bond.isDebugEnabled()) {
                color = Color.red;
            }

            bondLine = new PPath(line, stroke);
            bondLine.setPaint(color);
            bondLine.setStrokePaint(color);
            addChild(bondLine);

            if (bond.isOpen()) {
                Point2D p = new Point2D.Double(bond.getOpenPosition().getX() - (bond.getOpenPosition().getX() - bond.getOrigin().getPosition().getX()) / 2,
                        bond.getOpenPosition().getY() - (bond.getOpenPosition().getY() - bond.getOrigin().getPosition().getY()) / 2);
                Line2D line2 = new Line2D.Double(p, bond.getOpenPosition());
                Ellipse2D e = new Ellipse2D.Double(bond.getOpenPosition().getX(), bond.getOpenPosition().getY(), 2, 2);
                PPath pp = new PPath(e);
                pp.setStrokePaint(Color.blue);
//                addChild(pp);
            }
        }
    }

    public void stateChanged(Bond.ChangeEvent event) {
        if (event.getBond() == null || !(event.getBond() instanceof Bond)) {
            throw new IllegalArgumentException();
        }
        createGraphic(event.getBond());
    }
}
