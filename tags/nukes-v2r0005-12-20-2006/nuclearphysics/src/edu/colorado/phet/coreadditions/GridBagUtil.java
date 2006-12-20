/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.coreadditions;

import javax.swing.*;
import java.awt.*;

/**
 * GridBagUtil
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class GridBagUtil {

    public static void addGridBagComponent( JComponent container,
                                            JComponent component,
                                            int gridx, int gridy, int colSpan, int rowSpan, int fill, int anchor )
            throws AWTException {

        if( !( container.getLayout() instanceof GridBagLayout ) ) {
            throw new AWTException( "LayoutManager is not GridBagLayout" );
        }

        GridBagConstraints gbc = new GridBagConstraints( gridx, gridy,
                                                         colSpan, rowSpan,
                                                         1, 1,
                                                         anchor, fill,
                                                         new Insets( 0, 0, 0, 0 ), 0, 0 );
        container.add( component, gbc );
    }
}
