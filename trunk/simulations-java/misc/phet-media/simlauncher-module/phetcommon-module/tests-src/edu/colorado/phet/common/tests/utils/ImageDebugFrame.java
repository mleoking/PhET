/* Copyright 2003-2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source: /cvsroot/phet/phetcommon/tests-src/edu/colorado/phet/common/tests/utils/ImageDebugFrame.java,v $
 * Branch : $Name:  $
 * Modified by : $Author: samreid $
 * Revision : $Revision: 1.6 $
 * Date modified : $Date: 2005/12/09 21:17:35 $
 */
package edu.colorado.phet.common.tests.utils;

import javax.swing.*;
import java.awt.*;

/**
 * User: Sam Reid
 * Date: Apr 11, 2005
 * Time: 6:56:12 PM
 * Copyright (c) Apr 11, 2005 by Sam Reid
 */

public class ImageDebugFrame extends JFrame {
    public ImageDebugFrame( Image im ) {
        setContentPane( new JLabel( new ImageIcon( im ) ) );
        pack();
    }
}