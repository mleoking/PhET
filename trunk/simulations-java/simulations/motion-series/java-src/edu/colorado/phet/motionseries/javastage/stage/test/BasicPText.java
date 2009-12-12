/* Copyright 2004-2010, University of Colorado */
package edu.colorado.phet.motionseries.javastage.stage.test;

import edu.umd.cs.piccolo.nodes.PText;

/**
 * This text node provides convenience constructors that include offset and scale.
 *
 * @author Sam Reid
 */
public class BasicPText extends PText {
    public BasicPText(String text, double x, double y, double scale) {
        setText(text);
        setOffset(x, y);
        setScale(scale);
    }

    public BasicPText(String text, double x, double y) {
        this(text, x, y, 1.0);
    }
}
