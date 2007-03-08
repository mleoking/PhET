/* Copyright 2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.common.view.components;

import javax.swing.*;
import java.awt.*;

/**
 * This is a decorator panel.
 * Aspect ratio = width/height.
 * 
 * @author Ron LeMaster
 * @version $Revision$
 */
public class AspectRatioPanel extends JPanel {
    private Component target;
    private AspectRatioLayout layout;

    public AspectRatioPanel( Component target, int insetX, int insetY, double aspectRatio ) {
        this.target = target;
        layout = new AspectRatioLayout( target, insetX, insetY, aspectRatio );
        this.setLayout( layout );
        add( target );
    }

    public void setAspectRatio( double aspectRatio ) {
        layout.setAspectRatio( aspectRatio );
        this.doLayout();
        repaint();
    }
}
