package edu.colorado.phet.functionalscenegraph;

import fj.Effect;
import lombok.Data;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.geom.Ellipse2D;

import javax.swing.JFrame;
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
    public static void main( String[] args ) {
        new JFrame( "Test" ) {{
            final Property<State> model = new Property<State>( new State( new Vector2D( 50, 200 ) ) );
            model.trace( "model changed" );
            final SNode root = createRoot( model );
            setContentPane( new SCanvas( root ) {{
                setPreferredSize( new Dimension( 800, 600 ) );

                //When the model updates, create a new node
                model.addObserver( new VoidFunction1<State>() {
                    @Override public void apply( final State state ) {
                        child.set( createRoot( model ) );
                        repaint();
                    }
                } );

            }} );
            pack();
            setDefaultCloseOperation( WindowConstants.EXIT_ON_CLOSE );
        }}.setVisible( true );
    }

    //look up corresponding model element by ID?
    //Better to provide different methods to dispatch to.  Should be attached to the node.
    public static @Data class State {
        public final Vector2D circlePosition;

        public State withCirclePosition( final Vector2D circlePosition ) { return new State( circlePosition ); }
    }

    private static SNode createRoot( final Property<State> model ) {
        final SNode text = textNode( "Hello", new PhetFont( 30, true ), Color.blue ).translate( 100, 100 );
        ImmutableRectangle2D bounds = text.getBounds();
        System.out.println( "bounds = " + bounds );

        DrawShape shape = new DrawShape( bounds );
        SNode fillShape = new FillShape( bounds ).withPaint( Color.white );

        final SNode ellipse = new FillShape( new Ellipse2D.Double( 0, 0, 100, 100 ) ).translate( model.get().circlePosition ).
                withDragEvent( new Effect<Vector2D>() {
                    @Override public void e( final Vector2D vector2D ) {
                        model.set( model.get().withCirclePosition( model.get().circlePosition.plus( vector2D ) ) );
                    }
                } );
        return new SList( fillShape, shape, text, ellipse );

        //Deltas not getting transformed yet
//        .scale( 2 );
    }
}