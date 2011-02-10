// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.lightreflectionandrefraction.modules.intro;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.lightreflectionandrefraction.model.LightRay;
import edu.umd.cs.piccolo.PNode;

/**
 * @author Sam Reid
 */
public class LightWaveNode extends PNode {
    public LightWaveNode( final ModelViewTransform transform, final LightRay lightRay ) {
//        float powerFraction = (float) lightRay.getPowerFraction();
//        Color color = lightRay.getColor();
        addChild( new PhetPPath( new BasicStroke( 80, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER ), Color.blue ) {{
            lightRay.addObserver( new SimpleObserver() {
                public void update() {
                    setPathTo( transform.modelToView( new Line2D.Double( lightRay.tip.getValue().toPoint2D(), lightRay.tail.getValue().toPoint2D() ) ) );
                }
            } );
        }} );
        setPickable( false );
        setChildrenPickable( false );
    }

    public static void main( String[] args ) {
        new JFrame() {{
            setContentPane( new JLabel( new ImageIcon( new BufferedImage( 200, 100, BufferedImage.TYPE_INT_ARGB_PRE ) {{
                Graphics2D g2 = createGraphics();
                g2.setPaint( new GradientPaint( 0, 0, Color.red, 100, 0, Color.black, true ) );
                g2.fillRect( 0, 0, 200, 100 );
                g2.dispose();
            }} ) ) );
            pack();
        }}.setVisible( true );
    }
}
