package edu.colorado.phet.fluidpressureandflow.modules.fluidflow;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.fluidpressureandflow.model.Pipe;
import edu.colorado.phet.fluidpressureandflow.view.FluidPressureAndFlowCanvas;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolox.pswing.PSwing;

import static edu.colorado.phet.fluidpressureandflow.FluidPressureAndFlowResources.RESOURCES;

/**
 * @author Sam Reid
 */
public class DropperNode extends PNode {
    public static final PhetFont font = new PhetFont( 16, true );

    public DropperNode( final ModelViewTransform transform, final Pipe pipe, final SimpleObserver squirt ) {
        final PImage image = new PImage( RESOURCES.getImage( "dropper.png" ) );
        image.scale( 0.8 );
        addChild( image );
        final PSwing pSwing = new PSwing( new VerticalLayoutPanel() {{
            setFillNone();
            FluidPressureAndFlowCanvas.makeTransparent( this );
            add( new JButton( "Squirt" ) {{
                setFont( font );
                addActionListener( new ActionListener() {
                    public void actionPerformed( ActionEvent e ) {
                        squirt.update();
                    }
                } );
            }} );
        }} );
        addChild( pSwing );
        pipe.addShapeChangeListener( new SimpleObserver() {
            public void update() {
                final Point2D pipeTopLeft = transform.modelToView( pipe.getTopLeft() );
                setOffset( pipeTopLeft.getX() - image.getFullBounds().getWidth() / 2 + 10, pipeTopLeft.getY() - image.getFullBounds().getHeight() );
                final Point2D.Double g = new Point2D.Double( 0, 0 );//keep everything in the stage
                globalToLocal( g );
                pSwing.setOffset( Math.max( g.getX(), image.getFullBounds().getCenterX() - pSwing.getFullBounds().getWidth() / 2 ), Math.max( g.getY(), image.getFullBounds().getY() - pSwing.getFullBounds().getHeight() ) );
            }
        } );
    }
}