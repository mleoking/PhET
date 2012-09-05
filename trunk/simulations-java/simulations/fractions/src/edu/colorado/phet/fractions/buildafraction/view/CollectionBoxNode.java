// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.buildafraction.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Stroke;

import edu.umd.cs.piccolo.PNode;

/**
 * Base class for shapes and number collection boxes.
 *
 * @author Sam Reid
 */
public class CollectionBoxNode extends PNode {

    //Look and feel common to all shapes and number collection boxes.
    public static final Color BACKGROUND = Color.white;
    public static final Stroke STROKE = new BasicStroke( 2 );
    public static final Color DISABLED_STROKE_PAINT = Color.DARK_GRAY;
    public static final Color ENABLED_STROKE_PAINT = Color.blue;
    public static final double ARC = 30;
    public static final float FADED_OUT = 0.2f;
    public static final long FADE_IN_TIME = 500;
}