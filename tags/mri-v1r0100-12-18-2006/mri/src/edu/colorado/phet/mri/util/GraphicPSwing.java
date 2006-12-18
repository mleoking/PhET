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

import edu.colorado.phet.piccolo.util.PImageFactory;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolox.pswing.PSwing;

import javax.swing.*;
import java.awt.*;

/**
 * GraphicPSwing
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class GraphicPSwing extends PNode {
    private PImage pImage;
    private PSwing pSwing;

    public GraphicPSwing( PSwing pSwing, String backgroundImageFile ) {
        this( pSwing, PImageFactory.create( backgroundImageFile,
                                            new Dimension( (int)pSwing.getWidth(),
                                                           (int)pSwing.getHeight() ) ) );
    }

    public GraphicPSwing( PSwing pSwing, Image backgroundImage ) {
        this( pSwing, new PImage( backgroundImage ) );
    }

    public GraphicPSwing( PSwing pSwing, PImage pImage ) {
        this.pImage = pImage;
        this.pSwing = pSwing;
        setComponentTransparent( pSwing.getComponent() );
        pSwing.getComponent().setBorder( null );
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

    public void repaint() {
        pSwing.setVisible( false );
        JComponent component = pSwing.getComponent();
        component.invalidate();
//        component.validateTree();
        component.revalidate();
        component.doLayout();
        component.repaint();
        GraphicPSwing.this.invalidateFullBounds();
        GraphicPSwing.this.invalidatePaint();
        GraphicPSwing.super.repaint();


        pSwing.setVisible( true );
    }

    public PBounds getBounds() {
        return pImage.getBounds();
    }

    public double getWidth() {
        return pImage.getWidth();
    }

    public double getHeight() {
        return pImage.getHeight();
    }
}
