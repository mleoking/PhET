/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.common.view.basicgraphics.repaint;

import edu.colorado.phet.common.view.basicgraphics.RepaintDelegate;

import java.awt.*;

/**
 * Repaint
 *
 * @author Sam Reid
 * @version $Revision$
 */
public class Repaint implements RepaintDelegate {
    public void repaint( Component component, Rectangle rect ) {
        component.repaint();
    }
}
