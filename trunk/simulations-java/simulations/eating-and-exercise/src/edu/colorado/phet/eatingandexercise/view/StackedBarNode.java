package edu.colorado.phet.eatingandexercise.view;

import java.awt.*;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.math.Function;
import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.LinearValueControl;
import edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils;
import edu.colorado.phet.common.phetcommon.view.util.ColorChooserFactory;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.BufferedPhetPCanvas;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.HTMLNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.eatingandexercise.EatingAndExerciseResources;
import edu.colorado.phet.eatingandexercise.EatingAndExerciseStrings;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.nodes.PClip;

/**
 * Created by: Sam
 * Apr 17, 2008 at 6:19:17 PM
 */
public class StackedBarNode extends PNode {
    private Function function;
    private int barWidth;
    private PNode barChartElementNodeLayer = new PNode();
    private ReadoutNode readoutNode;
    private boolean showColorChooser = false;

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

        BarChartElementNode node = new BarChartElementNode( barChartElement, thumbLocation );
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

    private double modelToView( double model ) {
        return function.evaluate( model );
    }

    private double viewToModel( double view ) {
        return function.createInverse().evaluate( view );
    }

    private double viewToModelDelta( double deltaView ) {//assumes linear
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

    private class BarChartElementNode extends PNode {
        private BarChartElement barChartElement;
        private PClip clip;
        private PhetPPath barNode;
        private PhetPPath barThumb;
        private Thumb thumbLocation;

        private PNode labelNode = new PNode();//contains image + label + readout
        private PNode imageNode;
        private HTMLNode labelTextNode;
        private HTMLNode readoutValueNode;

        private BarChartElementNode( final BarChartElement barChartElement, Thumb thumbLocation ) {
            this.thumbLocation = thumbLocation;
            this.barChartElement = barChartElement;
            barNode = new PhetPPath( createShape(), barChartElement.getPaint() );
            addChild( barNode );

            if ( showColorChooser ) {
                showColorChooser();
            }

            barChartElement.addListener( new BarChartElement.Listener() {
                public void valueChanged() {
                    updateShape();
                }

                public void paintChanged() {
                    barNode.setPaint( barChartElement.getPaint() );
                    System.out.println( barChartElement.getName() + " = " + barChartElement.getPaint() );
                }
            } );
            clip = new PClip();

            if ( barChartElement.getImage() != null ) {
                imageNode = new PImage( BufferedImageUtils.multiScaleToHeight( barChartElement.getImage(), 25 ) );
            }
            else {
                imageNode = new PNode();
            }

            labelTextNode = new HTMLNode( barChartElement.getName(), new PhetFont( 18, true ), barChartElement.getTextColor() );
            readoutValueNode = new HTMLNode( "", new PhetFont( 12, true ), barChartElement.getTextColor() );
            clip.addChild( labelNode );
            labelNode.addChild( imageNode );
            labelNode.addChild( labelTextNode );
//            clip.addChild( readoutNode );
            labelNode.addChild( readoutValueNode );

            addChild( clip );

            //todo: delegate to subclass
            barThumb = new PhetPPath( thumbLocation.getThumbShape( barWidth ), barChartElement.getPaint(), new BasicStroke( 1 ), Color.black );
            addChild( barThumb );
            barThumb.addInputEventListener( new CursorHandler() );
            barThumb.addInputEventListener( new PBasicInputEventHandler() {
                public void mouseDragged( PInputEvent event ) {
                    double modelDX = viewToModelDelta( event.getCanvasDelta().getHeight() );
                    barChartElement.setValue( Math.max( 0, barChartElement.getValue() - modelDX ) );
                }
            } );
            updateShape();
        }

        private void showColorChooser() {
            barNode.addInputEventListener( new PBasicInputEventHandler() {
                public void mousePressed( PInputEvent event ) {
                    ColorChooserFactory.showDialog( "Color Picker", null, (Color) barChartElement.getPaint(), new ColorChooserFactory.Listener() {
                        public void colorChanged( Color color ) {
                            barChartElement.setPaint( color );
                        }

                        public void ok( Color color ) {
                        }

                        public void cancelled( Color originalColor ) {
                        }
                    }, true );
                }
            } );
        }

        private void updateShape() {
            double value = barChartElement.getValue();
            readoutValueNode.setHTML( EatingAndExerciseStrings.KCAL_PER_DAY_FORMAT.format( value ) );
            barNode.setPathTo( createShape() );
            clip.setPathTo( createShape() );
            double availHeight = clip.getFullBounds().getHeight();
            labelNode.setScale( 1 );
            labelNode.setOffset( 0, 0 );
            labelTextNode.setOffset( 0, 0 );
            imageNode.setOffset( clip.getFullBounds().getWidth() / 2 - imageNode.getFullBounds().getWidth() / 2, 0 );

            labelTextNode.setOffset( clip.getFullBounds().getWidth() / 2 - labelTextNode.getFullBounds().getWidth() / 2, imageNode.getFullBounds().getHeight() - 3 );
            readoutValueNode.setOffset( clip.getFullBounds().getWidth() / 2 - readoutValueNode.getFullBounds().getWidth() / 2 + 2, labelTextNode.getFullBounds().getMaxY() - 2 );

            if ( availHeight < labelNode.getFullBounds().getHeight() ) {
                double sy = availHeight / labelNode.getFullBounds().getHeight();
                if ( sy > 0 && sy < 1 ) {
                    double MIN_SCALE = 0.6;
                    sy = Math.max( MIN_SCALE, sy );
                    labelNode.setScale( sy );

                    //if the font is pretty small, then move the text to the top right so it's not obscured
                    if ( sy == MIN_SCALE ) {
                        labelTextNode.setOffset( imageNode.getFullBounds().getMaxX(), imageNode.getFullBounds().getY() );
                        readoutValueNode.setOffset( clip.getFullBounds().getWidth() / 2 - readoutValueNode.getFullBounds().getWidth() / 2 + 2, labelTextNode.getFullBounds().getMaxY() - 2 );
                    }
                }
            }

            barThumb.setPathTo( thumbLocation.getThumbShape( barWidth ) );
        }

        private Rectangle2D.Double createShape() {
            return new Rectangle2D.Double( 0, 0, barWidth, modelToView( barChartElement.getValue() ) );
        }

        public BarChartElement getBarChartElement() {
            return barChartElement;
        }
    }

    public static class BarChartElement {
        private String name;
        private Paint paint;
        private double value;
        private BufferedImage image;
        private Color textColor;

        public BarChartElement( String name, Paint paint, double value ) {
            this( name, paint, value, null );
        }

        public BarChartElement( String name, Paint paint, double value, BufferedImage image, Color textColor ) {
            this.name = name;
            this.paint = paint;
            this.value = value;
            this.image = image;
            this.textColor = textColor;
        }

        public BarChartElement( String name, Paint paint, double value, BufferedImage image ) {
            this( name, paint, value, image, Color.black );
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

        public BufferedImage getImage() {
            return image;
        }

        public Color getTextColor() {
            return textColor;
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
