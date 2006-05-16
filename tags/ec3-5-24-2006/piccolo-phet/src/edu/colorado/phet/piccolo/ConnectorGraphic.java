/* Copyright 2003-2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.piccolo;

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * Shows a Line connection between two PNodes.
 */

public class ConnectorGraphic extends PPath {

    private PNode source;
    private PNode destination;

    public ConnectorGraphic( PNode src, PNode dst ) {
        this.source = src;
        this.destination = dst;
        PropertyChangeListener changeHandler = new PropertyChangeListener() {
            public void propertyChange( PropertyChangeEvent evt ) {
                update();
            }
        };
        src.addPropertyChangeListener( PNode.PROPERTY_FULL_BOUNDS, changeHandler );
        dst.addPropertyChangeListener( PNode.PROPERTY_FULL_BOUNDS, changeHandler );
        update();
        setStrokePaint( Color.black );
    }

    private void update() {
        connectRectsWithLine();
    }

    public void connectRectsWithLine() {
        if( source == null || source.getFullBounds() == null ||
            destination == null || destination.getFullBounds() == null ||
            source.getParent() == null || destination.getParent() == null ) {
            return;
        }

        Point2D r1c = source.getGlobalFullBounds().getCenter2D();
        Point2D r2c = destination.getGlobalFullBounds().getCenter2D();
        globalToLocal( r1c );
        globalToLocal( r2c );
        updateShape( r1c, r2c );
        repaint();
    }

    protected void updateShape( Point2D r1c, Point2D r2c ) {
        setPathTo( new Line2D.Double( r1c, r2c ) );
    }

    public PNode getSource() {
        return source;
    }

    public PNode getDestination() {
        return destination;
    }
}