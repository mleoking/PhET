package edu.colorado.phet.functionalscenegraph;

import fj.Effect;
import fj.data.List;
import lombok.Data;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Ellipse2D;

import javax.swing.JFrame;
import javax.swing.Timer;
import javax.swing.WindowConstants;

import edu.colorado.phet.common.phetcommon.math.ImmutableRectangle2D;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.fractions.util.immutable.Vector2D;

import static edu.colorado.phet.functionalscenegraph.DrawText.textNode;

/**
 * @author Sam Reid
 */
public class TestSCanvas {
    private static List<ImmutableRectangle2D> repaintRegion;

    public static void main( String[] args ) {
        new JFrame( "Test" ) {{
            final Property<State> model = new Property<State>( new State( new Vector2D( 50, 200 ), new Vector2D( 50, 50 ) ) );
//            model.trace( "model changed" );
            final SNode root = createRoot( model );
            setContentPane( new SCanvas( root ) {
                {
                    setPreferredSize( new Dimension( 800, 600 ) );

                    //When the model updates, create a new node
                    model.addObserver( new VoidFunction1<State>() {
                        @Override public void apply( final State state ) {
                            final SNode oldRoot = child.get();
                            final SNode newRoot = createRoot( model );
                            List<ImmutableRectangle2D> result = CompareTrees.compare( oldRoot, newRoot );
                            child.set( newRoot );
                            repaintRegion = result;
                            for ( ImmutableRectangle2D dirty : result ) {
                                final Rectangle bounds = dirty.toRectangle2D().getBounds();

                                //Swing does the union in repaintmanager
                                repaint( bounds.x - 2, bounds.y - 2, bounds.width + 4, bounds.height + 4 );
                            }
                        }
                    } );
                }

                @Override protected void paintComponent( final Graphics g ) {
                    super.paintComponent( g );
//                    System.out.println( "repaintRegion = " + repaintRegion );
                    for ( ImmutableRectangle2D rectangle2D : repaintRegion ) {
                        Graphics2D g2 = (Graphics2D) g;
                        g2.setColor( Color.red );
                        g2.setStroke( new BasicStroke( 1 ) );
                        g2.draw( rectangle2D.toRectangle2D() );
                    }
                }
            } );
            pack();
            setDefaultCloseOperation( WindowConstants.EXIT_ON_CLOSE );
            new Timer( 30, new ActionListener() {
                @Override public void actionPerformed( final ActionEvent e ) {
                    model.set( model.get().withTextPosition( model.get().getTextPosition().plus( 1f, 1f ) ) );
                }
            } ).start();
        }}.setVisible( true );
    }

    //look up corresponding model element by ID?
    //Better to provide different methods to dispatch to.  Should be attached to the node.
    public static @Data class State {
        public final Vector2D circlePosition;
        public final Vector2D textPosition;

        public State withCirclePosition( final Vector2D circlePosition ) { return new State( circlePosition, textPosition ); }

        public State withTextPosition( final Vector2D textPosition ) { return new State( circlePosition, textPosition ); }
    }

    private static SNode createRoot( final Property<State> model ) {
        final SNode text = textNode( "Hello", new PhetFont( 30, true ), Color.blue ).translate( model.get().getTextPosition() );
        ImmutableRectangle2D bounds = text.getBounds();
//        System.out.println( "bounds = " + bounds );

        DrawShape shape = new DrawShape( bounds );
        SNode fillShape = new FillShape( bounds ).withPaint( Color.white );

        final SNode ellipse = new FillShape( new Ellipse2D.Double( 0, 0, 100, 100 ) ).translate( model.get().circlePosition ).
                withDragEvent( new Effect<Vector2D>() {
                    @Override public void e( final Vector2D vector2D ) {
                        model.set( model.get().withCirclePosition( model.get().circlePosition.plus( vector2D ) ) );
                    }
                } );

//        int additional = 4;
        int numCircles = 100;
//        final int max = numCircles + additional;
//        ArrayList<SNode> nodes = new ArrayList<SNode>( max );
        List<SNode> list = List.nil();
        for ( int i = 0; i < numCircles; i++ ) {
            list = list.cons( new FillShape( new Ellipse2D.Double( 0, 0, 10, 10 ) ).withPaint( new Color( i % 255, 0, 0 ) ).translate( i / 10.0, i / 10.0 ) );
        }

//        fj.data.List<SNode> elements = fj.data.List.range( 0, 10000 ).map( new F<Integer, SNode>() {
//            @Override public SNode f( final Integer i ) {
//                return new FillShape( new Ellipse2D.Double( 0, 0, 10, 10 ) ).withPaint( new Color( i % 255, 0, 0 ) ).translate( i/10, i/10 );
//            }
//        } );
        list = list.cons( text );
        list = list.cons( fillShape );
        list = list.cons( shape );
        list = list.cons( ellipse );
//        nodes.addAll( Arrays.asList( fillShape, shape, text, ellipse ) );
        return new SList( list );

        //Deltas not getting transformed yet
//        .scale( 2 );
    }
}