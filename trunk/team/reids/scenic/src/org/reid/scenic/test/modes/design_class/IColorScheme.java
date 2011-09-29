// Copyright 2002-2011, University of Colorado
package org.reid.scenic.test.modes.design_class;

import java.awt.Color;
import java.awt.Font;

/**
 * @author Sam Reid
 */
public interface IColorScheme {
    Color getBackground();

    Color getSaltColor();

    Color getSugarColor();

    Font getTitleFont();
}