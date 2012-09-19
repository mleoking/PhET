// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.buildafraction.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Stroke;

import edu.umd.cs.piccolo.PNode;

/**
 * Base class for shapes and number collection boxes.  It just serves as a marker parent class (like marker interface)
 * and static fields for look and feel common to all shapes and number collection boxes.
 *
 * @author Sam Reid
 */
public class CollectionBoxNode extends PNode {

    //Look and feel common to all shapes and number collection boxes.
    protected static final Color BACKGROUND = new Color( 250, 249, 220 );
    protected static final Stroke STROKE = new BasicStroke( 2 );
    protected static final Color DISABLED_STROKE_PAINT = Color.DARK_GRAY;
    protected static final Color ENABLED_STROKE_PAINT = Color.blue;
    protected static final double ARC = 30;
    protected static final float FADED_OUT = 0.2f;
    protected static final long FADE_TIME = 500;
}