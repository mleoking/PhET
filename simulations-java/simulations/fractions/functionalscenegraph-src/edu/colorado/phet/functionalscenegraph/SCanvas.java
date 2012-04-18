package edu.colorado.phet.functionalscenegraph;

import fj.Effect;
import fj.data.Option;
import lombok.Data;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Ellipse2D;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.WindowConstants;

import edu.colorado.phet.common.phetcommon.math.ImmutableRectangle2D;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.fractions.util.immutable.Vector2D;

import static edu.colorado.phet.functionalscenegraph.DrawText.textNode;

/**
 * @author Sam Reid
 */
public class SCanvas extends JComponent {
    public final Property<SNode> child;

    //Make sure mock and Graphics2D use same font, so bounds will be right
    public static final Font DEFAULT_FONT = new PhetFont();
    private Option<PickResult> pick;
    private Vector2D lastMousePoint = null;

    public SCanvas( final SNode _child ) {
        this.child = new Property<SNode>( _child );
        addMouseListener( new MouseAdapter() {
            @Override public void mousePressed( final MouseEvent e ) {
                pick = child.get().pick( new Vector2D( e.getPoint() ) );
                lastMousePoint = new Vector2D( e.getPoint() );
            }

            @Override public void mouseReleased( final MouseEvent e ) {
                //when dragging something, create a new scene graph

            }
        } );
        addMouseMotionListener( new MouseMotionListener() {
            @Override public void mouseDragged( final MouseEvent e ) {
                if ( pick.isSome() ) {
                    //Return the same tree, but where the given node has a withTranslate.
                    //Eh, this will cause problems (failure to detect identical states, and stack overflows), we should collapse adjacent withTranslates.

                    //On second thought just send this to the model, and wait for them to send back a new tree.
                    //In the most abstract sense, for purposes of this class, a model is a function that receives events and produces new SNodes.

                    pick.some().dragHandler.e( new Vector2D( e.getPoint() ).minus( lastMousePoint ) );
                }
                lastMousePoint = new Vector2D( e.getPoint() );
            }

            @Override public void mouseMoved( final MouseEvent e ) {
                //hit detection and show cursor hand
                //should mouse location be in the model?
                final Option<PickResult> picked = child.get().pick( new Vector2D( e.getPoint() ) );
                if ( picked.isSome() ) {
                    setCursor( picked.some().node.getCursor() );
                }
                else {
                    setCursor( Cursor.getPredefinedCursor( Cursor.DEFAULT_CURSOR ) );
                }
            }
        } );

        this.child.addObserver( new SimpleObserver() {
            @Override public void update() {
                repaint();
            }
        } );
    }

    @Override protected void paintComponent( final Graphics g ) {
        super.paintComponent( g );
        Font orig = g.getFont();
        g.setFont( DEFAULT_FONT );
        final Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
        g2.setRenderingHint( RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON );
        this.child.get().render( new Graphics2DContext( g2 ) );
        g.setFont( orig );
    }

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

        final SNode ellipse = new FillShape( new Ellipse2D.Double( 0, 0, 100, 100 ) ).translate( model.get().circlePosition.x, model.get().circlePosition.y ).
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