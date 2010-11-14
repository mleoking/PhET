package edu.colorado.phet.fluidpressureandflow.modules.fluidflow;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.fluidpressureandflow.model.Pipe;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * @author Sam Reid
 */
public class DropperNode extends PNode {
    public DropperNode( final ModelViewTransform2D transform, final Pipe pipe, final Property<Boolean> dropperOnProperty ) {
        JPanel panel = new JPanel() {{
            setLayout( new BoxLayout( this, BoxLayout.Y_AXIS ) );
            add( new MyRadioButton( "On", dropperOnProperty ) );
            add( new MyRadioButton( "Off", new Not( dropperOnProperty ) ) );
        }};
        addChild( new PSwing( panel ) );
        pipe.addShapeChangeListener( new SimpleObserver() {
            public void update() {
                final Point2D pipeTopLeft = transform.modelToView( pipe.getTopLeft() );
                System.out.println( "pipeTopLeft = " + pipeTopLeft );
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

    private class Not extends Property<Boolean> {
        public Not( final Property<Boolean> p ) {
            super( !p.getValue() );
            p.addObserver( new SimpleObserver() {
                public void update() {
                    setValue( !p.getValue() );
                }
            } );
            addObserver( new SimpleObserver() {
                public void update() {
                    p.setValue( !getValue() );
                }
            } );
        }

    }
}
