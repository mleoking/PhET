/**
 * Copyright (C) 2007 - University of Colorado
 *
 * User: New Admin
 * Date: Feb 7, 2007
 * Time: 11:46:52 AM
 */

package edu.colorado.phet.common.phetcommon.view.util;

import java.awt.*;

public interface ColorFilter {
    public static final ColorFilter NULL = new ColorFilter() {
        public Color filter( Color in ) {
            return in;
        }
    };

    Color filter( Color in );
}
