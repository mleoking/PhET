/**
 * Class: AspectRatioPanel
 * Package: edu.colorado.phet.common.view.components
 * Author: Another Guy
 * Date: May 12, 2004
 */
package edu.colorado.phet.common_cck.view.components;

import javax.swing.*;
import java.awt.*;

/**
 * This is a decorator panel.
 * Aspect ratio = width/height.
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
