package edu.colorado.phet.fluidpressureandflow.modules.fluidflow;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.util.Hashtable;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.model.Property;
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
    PhetFont font = new PhetFont( 16, true );
    PhetFont tickFont = new PhetFont( 16, false );

    public DropperNode( final ModelViewTransform transform, final Pipe pipe, final Property<Double> dropperRateProperty, final SimpleObserver waterDropped, final SimpleObserver pour ) {
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
                        pour.update();
                    }
                } );
            }} );
            add( new JButton( "Drop" ) {{
                setFont( font );
                addActionListener( new ActionListener() {
                    public void actionPerformed( ActionEvent e ) {
                        waterDropped.update();
                    }
                } );
                dropperRateProperty.addObserver( new SimpleObserver() {
                    public void update() {
                        setEnabled( dropperRateProperty.getValue() == 0 );
                    }
                } );
            }} );
            add(
                    new JSlider( JSlider.HORIZONTAL, 0, 100, 4 ) {{
                        setFont( font );
                        FluidPressureAndFlowCanvas.makeTransparent( this );
                        setPaintTicks( true );
                        setPaintLabels( true );
                        setLabelTable( new Hashtable() {{
                            put( 0, new JLabel( "None" ) {{setFont( tickFont );}} );
                            put( 100, new JLabel( "Lots" ) {{setFont( tickFont );}} );
                        }} );
                        addChangeListener( new ChangeListener() {
                            public void stateChanged( ChangeEvent e ) {
                                dropperRateProperty.setValue( (double) getValue() );
                            }
                        } );
                        dropperRateProperty.addObserver( new SimpleObserver() {
                            public void update() {
                                setValue( dropperRateProperty.getValue().intValue() );
                            }
                        } );
                    }}
//            }}
            );
        }} );
//        pSwing.setOffset( image.getFullBounds().getWidth(), image.getFullBounds().getHeight() - pSwing.getFullBounds().getHeight() );
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
