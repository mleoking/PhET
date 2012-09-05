// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.buildafraction.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Stroke;

import edu.umd.cs.piccolo.PNode;

/**
 * @author Sam Reid
 */
public class CollectionBoxNode extends PNode {
    public static final Color BACKGROUND = BuildAFractionCanvas.CONTROL_PANEL_BACKGROUND;
    public static final Stroke STROKE = new BasicStroke( 2 );
    public static final Color STROKE_PAINT = Color.darkGray;
    public static final double ARC = 30;
    public static final float FADED_OUT = 0.2f;
    public static final long FADE_IN_TIME = 500;
}