package edu.colorado.phet.fluidpressureandflow.modules.fluidflow;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.model.NotProperty;
import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.fluidpressureandflow.model.Pipe;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolox.pswing.PSwing;

import static edu.colorado.phet.fluidpressureandflow.FluidPressureAndFlowResources.RESOURCES;

/**
 * @author Sam Reid
 */
public class DropperNode extends PNode {
    public DropperNode( final ModelViewTransform2D transform, final Pipe pipe, final Property<Boolean> dropperOnProperty, final SimpleObserver waterDropped ) {
        final PImage image = new PImage( RESOURCES.getImage( "dropper.png" ) );
        addChild( image );
        final PSwing pSwing = new PSwing( new VerticalLayoutPanel() {{
            add( new JButton( "Drop" ) {{
                addActionListener( new ActionListener() {
                    public void actionPerformed( ActionEvent e ) {
                        waterDropped.update();
                    }
                } );
            }} );
            add( new VerticalLayoutPanel() {{
                setBorder( BorderFactory.createTitledBorder( "Auto" ) );
                add( new MyRadioButton( "On", dropperOnProperty ) );
                add( new MyRadioButton( "Off", new NotProperty( dropperOnProperty ) ) );
            }} );
        }} );
        pSwing.setOffset( image.getFullBounds().getWidth(), image.getFullBounds().getHeight() - pSwing.getFullBounds().getHeight() );
        addChild( pSwing );
        pipe.addShapeChangeListener( new SimpleObserver() {
            public void update() {
                final Point2D pipeTopLeft = transform.modelToView( pipe.getTopLeft() );
                setOffset( Math.max( pipeTopLeft.getX(), 0 ), pipeTopLeft.getY() - getFullBounds().getHeight() );
            }
        } );
    }

    public static class MyRadioButton extends JRadioButton {
        public MyRadioButton( String name, final Property<Boolean> property ) {
            super( name, property.getValue() );
            property.addObserver( new SimpleObserver() {
                public void update() {
                    setSelected( property.getValue() );
                }
            } );
            addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    property.setValue( isSelected() );
                }
            } );
        }
    }

}
