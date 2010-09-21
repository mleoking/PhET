package edu.colorado.phet.eatingandexercise.view;

import java.awt.*;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.text.DecimalFormat;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.math.Function;
import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.LinearValueControl;
import edu.colorado.phet.common.piccolophet.BufferedPhetPCanvas;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.eatingandexercise.EatingAndExerciseResources;
import edu.colorado.phet.eatingandexercise.EatingAndExerciseStrings;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * Created by: Sam
 * Apr 17, 2008 at 6:19:17 PM
 */
public class StackedBarNode extends PNode {
    private Function function;
    private int barWidth;
    private PNode barChartElementNodeLayer = new PNode();
    private ReadoutNode readoutNode;
    public static final boolean showColorChooser = false;

    public StackedBarNode( int barWidth ) {
        this( new Function.IdentityFunction(), barWidth );
    }

    public StackedBarNode( Function function, int barWidth ) {
        this.function = function;
        this.barWidth = barWidth;
        addChild( barChartElementNodeLayer );
        readoutNode = new ReadoutNode();
        addChild( readoutNode );
    }

    public void addElement( final BarChartElement barChartElement ) {
        addElement( barChartElement, NONE );
    }

    public void addElement( final BarChartElement barChartElement, Thumb thumbLocation ) {
        barChartElement.addListener( new BarChartElement.Listener() {
            public void valueChanged() {
                relayout();
            }

            public void paintChanged() {
            }
        } );

        BarChartElementNode node = new BarChartElementNode( this, barChartElement, thumbLocation );
        barChartElementNodeLayer.addChild( node );

        relayout();
    }

    public double getTotal() {
        double sum = 0;
        for ( int i = barChartElementNodeLayer.getChildrenCount() - 1; i >= 0; i-- ) {
            BarChartElementNode node = (BarChartElementNode) barChartElementNodeLayer.getChild( i );
            sum += node.getBarChartElement().getValue();
        }
        return sum;
    }

    private void relayout() {
        DecimalFormat decimalFormat = EatingAndExerciseStrings.KCAL_PER_DAY_FORMAT;
        readoutNode.setText( decimalFormat.format( getTotal() ) + " " + EatingAndExerciseStrings.KCAL_PER_DAY );
        double viewHeight = modelToView( getTotalModelValue() );
        double offsetY = 0;
        for ( int i = barChartElementNodeLayer.getChildrenCount() - 1; i >= 0; i-- ) {
            BarChartElementNode node = (BarChartElementNode) barChartElementNodeLayer.getChild( i );
            node.setOffset( 0, offsetY - viewHeight );
            offsetY += modelToView( node.getBarChartElement().getValue() );
        }
//        BarChartElementNode node = (BarChartElementNode) barChartElementNodeLayer.getChild( 0 );
        readoutNode.setOffset( barWidth / 2 - readoutNode.getFullBounds().getWidth() / 2, -offsetY - readoutNode.getFullBounds().getHeight() );
    }

    double modelToView( double model ) {
        return function.evaluate( model );
    }

    private double viewToModel( double view ) {
        return function.createInverse().evaluate( view );
    }

    double viewToModelDelta( double deltaView ) {//assumes linear
        double x0 = viewToModel( 0 );
        double x1 = viewToModel( deltaView );
        return x1 - x0;
    }

    private double getTotalModelValue() {
        double sum = 0;
        for ( int i = 0; i < barChartElementNodeLayer.getChildrenCount(); i++ ) {
            sum += ( (BarChartElementNode) barChartElementNodeLayer.getChild( i ) ).getBarChartElement().getValue();
        }
        return sum;
    }

    public double getBarWidth() {
        return barWidth;
    }

    public void setFunction( Function function ) {
        this.function = function;

        for ( int i = 0; i < barChartElementNodeLayer.getChildrenCount(); i++ ) {
            ( (BarChartElementNode) barChartElementNodeLayer.getChild( i ) ).updateShape();
        }
        relayout();
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

    public static abstract class Thumb {
        private String name;

        public Thumb( String name ) {
            this.name = name;
        }

        public abstract Shape getThumbShape( double barWidth );
    }

    private static final float DEFAULT_TRIANGLE_WIDTH = 10;
    public static final Thumb LEFT = new Thumb( "left" ) {

        public Shape getThumbShape( double barWidth ) {
            float triangleWidth = DEFAULT_TRIANGLE_WIDTH;
            float triangleHeight = DEFAULT_TRIANGLE_WIDTH;
            GeneralPath path = new GeneralPath();
            path.moveTo( 0, 0 );
            path.lineTo( -triangleWidth, triangleHeight );
            path.lineTo( -triangleWidth, -triangleHeight );
            path.lineTo( 0, 0 );
            return path;
        }
    };
    public static final Thumb RIGHT = new Thumb( "right" ) {
        public Shape getThumbShape( double barWidth ) {
            float triangleWidth = DEFAULT_TRIANGLE_WIDTH;
            float triangleHeight = DEFAULT_TRIANGLE_WIDTH;
            GeneralPath path = new GeneralPath();
            path.moveTo( (float) barWidth, 0 );
            path.lineTo( (float) ( triangleWidth + barWidth ), triangleHeight );
            path.lineTo( (float) ( triangleWidth + barWidth ), -triangleHeight );
            path.lineTo( (float) barWidth, 0 );
            return path;
        }
    };
    public static final Thumb NONE = new Thumb( "none" ) {
        public Shape getThumbShape( double barWidth ) {
            return new Line2D.Double();
        }
    };

    public static void main( String[] args ) {
        JFrame frame = new JFrame( "Test Frame" );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setSize( 800, 600 );
        PhetPCanvas contentPane = new BufferedPhetPCanvas();
        frame.setContentPane( contentPane );

//        StackedBarNode barNode = new StackedBarNode( new Function.IdentityFunction(), 100 );
        StackedBarNode barNode = new StackedBarNode( new Function.LinearFunction( 0, 1, 0, 0.5 ), 100 );
        barNode.setOffset( 100, 360 );
        final BarChartElement bmr = new BarChartElement( "BMR", Color.red, 100, EatingAndExerciseResources.getImage( "eye.png" ) );
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

    private class ReadoutNode extends PNode {
        private PText child;

        private ReadoutNode() {
            child = new EatingAndExercisePText( "Text" );
            addChild( child );
        }

        public void setText( String s ) {
            child.setText( s );
        }
    }
}
