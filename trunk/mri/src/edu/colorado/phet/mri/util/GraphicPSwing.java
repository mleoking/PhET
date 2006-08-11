/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.mri.util;

import edu.umd.cs.piccolox.pswing.PSwing;
import edu.umd.cs.piccolox.pswing.PSwingCanvas;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.PNode;
import edu.colorado.phet.piccolo.util.PImageFactory;

import javax.swing.*;
import java.awt.*;

/**
 * GraphicPSwing
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class GraphicPSwing extends PNode {

    public GraphicPSwing( PSwingCanvas canvas, JComponent component, String backgroundImageFile ) {
        this( canvas, component, PImageFactory.create( backgroundImageFile ) );
    }

    public GraphicPSwing( PSwingCanvas canvas, JComponent component, Image backgroundImage ) {
        this( canvas, component, new PImage( backgroundImage ) );
    }

    public GraphicPSwing( PSwingCanvas canvas, JComponent component, PImage pImage ) {
        PSwing pSwing = new PSwing( canvas, component );
        setComponentTransparent( component );
        component.setBorder( null );
        addChild( pImage );
        addChild( pSwing );
    }

    private void setComponentTransparent( JComponent component ) {
        component.setOpaque( false );
        Component[] components = component.getComponents();
        for( int i = 0; i < components.length; i++ ) {
            Component comp = components[i];
            if( comp instanceof JComponent ) {
                JComponent jComp = (JComponent)comp;
                setComponentTransparent( jComp );
            }
        }
    }
}
