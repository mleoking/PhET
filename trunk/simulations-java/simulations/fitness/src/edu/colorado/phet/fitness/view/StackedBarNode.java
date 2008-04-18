package edu.colorado.phet.fitness.view;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.LinearValueControl;
import edu.colorado.phet.common.phetcommon.view.util.ColorChooserFactory;
import edu.colorado.phet.common.phetcommon.view.util.PhetDefaultFont;
import edu.colorado.phet.common.piccolophet.BufferedPhetPCanvas;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.HTMLNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolox.nodes.PClip;

/**
 * Created by: Sam
 * Apr 17, 2008 at 6:19:17 PM
 */
public class StackedBarNode extends PNode {
    private int barWidth;
    private PNode barChartElementNodeLayer = new PNode();

    public StackedBarNode( int barWidth ) {
        this.barWidth = barWidth;
        addChild( barChartElementNodeLayer );
    }

    public void addElement( final BarChartElement barChartElement ) {
        barChartElement.addListener( new BarChartElement.Listener() {
            public void valueChanged() {
                relayout();
            }

            public void paintChanged() {
            }
        } );
        BarChartElementNode node = new BarChartElementNode( barChartElement );
        node.addInputEventListener( new PBasicInputEventHandler() {
            public void mousePressed( PInputEvent event ) {
                ColorChooserFactory.showDialog( "Color Picker", null, (Color) barChartElement.getPaint(), new ColorChooserFactory.Listener() {
                    public void colorChanged( Color color ) {
                        barChartElement.setPaint( color );
                    }

                    public void ok( Color color ) {
                    }

                    public void cancelled( Color originalColor ) {
                    }
                } );
            }
        } );
        barChartElementNodeLayer.addChild( node );

        relayout();
    }

    private void relayout() {
        double totalHeight = getTotalBarHeight();
        double offsetY = 0;
        for ( int i = barChartElementNodeLayer.getChildrenCount() - 1; i >= 0; i-- ) {
            BarChartElementNode node = (BarChartElementNode) barChartElementNodeLayer.getChild( i );
            node.setOffset( 0, offsetY - totalHeight );
            offsetY += node.getBarChartElement().getValue();
        }
    }

    private double getTotalBarHeight() {
        double sum = 0;
        for ( int i = 0; i < barChartElementNodeLayer.getChildrenCount(); i++ ) {
            sum += ( (BarChartElementNode) barChartElementNodeLayer.getChild( i ) ).getBarChartElement().getValue();
        }
        return sum;
    }

    public double getBarWidth() {
        return barWidth;
    }

    private class BarChartElementNode extends PNode {
        private BarChartElement barChartElement;
        private PClip clip;
        private PhetPPath barNode;
        private HTMLNode htmlNode;

        private BarChartElementNode( final BarChartElement barChartElement ) {
            this.barChartElement = barChartElement;
            barNode = new PhetPPath( createShape(), barChartElement.getPaint() );
            addChild( barNode );
            barChartElement.addListener( new BarChartElement.Listener() {
                public void valueChanged() {
                    updateShape();
                }

                public void paintChanged() {
                    barNode.setPaint( barChartElement.getPaint() );
                    System.out.println( barChartElement.getName()+" = "+barChartElement.getPaint() );
                }
            } );
            clip = new PClip();
            htmlNode = new HTMLNode( barChartElement.getName(), new PhetDefaultFont( 20, true ), Color.black );
            clip.addChild( htmlNode );
            addChild( clip );
            updateShape();
        }

        private void updateShape() {
            barNode.setPathTo( createShape() );
            clip.setPathTo( createShape() );
            htmlNode.setOffset( clip.getFullBounds().getWidth() / 2 - htmlNode.getFullBounds().getWidth() / 2, 0 );
        }

        private Rectangle2D.Double createShape() {
            return new Rectangle2D.Double( 0, 0, barWidth, barChartElement.getValue() );
        }

        public BarChartElement getBarChartElement() {
            return barChartElement;
        }
    }

    public static class BarChartElement {
        private String name;
        private Paint paint;
        private double value;

        public BarChartElement( String name, Paint paint, double value ) {
            this.name = name;
            this.paint = paint;
            this.value = value;
        }

        public Paint getPaint() {
            return paint;
        }

        public double getValue() {
            return value;
        }

        public void setValue( double value ) {
            this.value = value;
            notifyListener();
        }

        public String getName() {
            return name;
        }

        public void setPaint( Paint color ) {
            this.paint = color;
            for ( int i = 0; i < listeners.size(); i++ ) {
                Listener listener = (Listener) listeners.get( i );
                listener.paintChanged();
            }
        }

        public static interface Listener {
            void valueChanged();

            void paintChanged();
        }

        private ArrayList listeners = new ArrayList();

        public void addListener( Listener listener ) {
            listeners.add( listener );
        }

        public void notifyListener() {
            for ( int i = 0; i < listeners.size(); i++ ) {
                ( (Listener) listeners.get( i ) ).valueChanged();
            }
        }
    }

    public static void main( String[] args ) {
        JFrame frame = new JFrame( "Test Frame" );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setSize( 800, 600 );
        PhetPCanvas contentPane = new BufferedPhetPCanvas();
        frame.setContentPane( contentPane );


        StackedBarNode barNode = new StackedBarNode( 100 );
        barNode.setOffset( 100, 360 );
        final BarChartElement bmr = new BarChartElement( "BMR", Color.red, 100 );
        barNode.addElement( bmr );
        BarChartElement activity = new BarChartElement( "Activity", Color.green, 200 );
        barNode.addElement( activity );
        BarChartElement exercise = new BarChartElement( "Exercise", Color.blue, 50 );
        barNode.addElement( exercise );


        contentPane.addScreenChild( barNode );


        frame.setVisible( true );

        JFrame controlPanel = new JFrame();
        JPanel cp = new VerticalLayoutPanel();
        cp.add( new BarChartElementControl( exercise ) );
        cp.add( new BarChartElementControl( activity ) );
        cp.add( new BarChartElementControl( bmr ) );


        controlPanel.setContentPane( cp );
        controlPanel.setVisible( true );
        controlPanel.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        controlPanel.pack();
    }

    public static class BarChartElementControl extends LinearValueControl {
        public BarChartElementControl( final BarChartElement elm ) {
            super( 0, 200, elm.getName(), "0.00", "Calories" );
            setValue( elm.getValue() );
            addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    elm.setValue( getValue() );
                }
            } );
        }
    }
}
