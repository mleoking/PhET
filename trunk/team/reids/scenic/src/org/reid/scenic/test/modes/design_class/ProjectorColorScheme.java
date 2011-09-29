// Copyright 2002-2011, University of Colorado
package org.reid.scenic.test.modes.design_class;

import java.awt.Color;

/**
 * @author Sam Reid
 */
public class ProjectorColorScheme extends MonitorColorScheme {
    @Override public Color getBackground() {
        return Color.white;
    }

    @Override public Color getSaltColor() {
        return Color.blue;
    }
}
