import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.piccolo.PhetPCanvas;
import edu.colorado.phet.piccolo.event.BoundedDragHandler;
import edu.colorado.phet.piccolo.event.CursorHandler;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PDimension;


public class TestDynamicResize extends JFrame {

    private static final Dimension FRAME_SIZE = new Dimension( 1024, 768 ); // screen coordinates
    private static final Dimension WORLD_SIZE = new Dimension( 750, 750 ); // world coordinates
    private static final Point2D HORIZONTAL_BAR_POSITION = new Point2D.Double( 400, 100 ); // world coordinates
    
    public static void main( String[] args ) {
        TestDynamicResize frame = new TestDynamicResize();
        frame.show();
    }
    
    public TestDynamicResize() {
        super( "TestRulerResize" );
        
        /*--------------------------- Canvas ---------------------------*/
        
        PPath dragBoundsNode = new PPath();
        dragBoundsNode.setStrokePaint( Color.RED );
        
        final HorizontalBarNode horizontalBarNode = new HorizontalBarNode( dragBoundsNode );
        horizontalBarNode.setPosition( HORIZONTAL_BAR_POSITION );

        final PhetPCanvas canvas = new PhetPCanvas( WORLD_SIZE );
        canvas.addWorldChild( horizontalBarNode );
        canvas.addWorldChild( dragBoundsNode );
        
        // when the canvas size changes, update the horizontal bar
        canvas.addComponentListener( new ComponentAdapter() {
            public void componentResized( ComponentEvent e ) {
                Dimension2D worldSize = canvas.getWorldSize();
                if ( worldSize.getWidth() > 0 && worldSize.getHeight() > 0 ) {
                    horizontalBarNode.setWorldSize( worldSize );
                }
            }
        } );
        
        /*--------------------------- Control Panel ---------------------------*/
        
        final JLabel positionLabel = new JLabel( String.valueOf( HORIZONTAL_BAR_POSITION.getX() ) );
        
        final JSlider positionSlider = new JSlider();
        positionSlider.setMinimum( 0 );
        positionSlider.setMaximum( 750 );
        positionSlider.setValue( (int) HORIZONTAL_BAR_POSITION.getX() );
        positionSlider.addChangeListener( new ChangeListener() {
            // when the slider is changed, update the horizontal bar's position
            public void stateChanged( ChangeEvent event ) {
                double x = positionSlider.getValue();
                double y = horizontalBarNode.getFullBounds().getY();
                positionLabel.setText( String.valueOf( x ) );
                horizontalBarNode.setPosition( new Point2D.Double( x, y ) );
            }
        } );
        
        JPanel controlPanel = new JPanel();
        controlPanel.setBorder( BorderFactory.createEmptyBorder( 15, 15, 15, 15 ) );
        controlPanel.setLayout( new BoxLayout( controlPanel, BoxLayout.X_AXIS ) );
        controlPanel.add( positionSlider );
        controlPanel.add( positionLabel );
        
        /*--------------------------- Frame ---------------------------*/
        
        JPanel panel = new JPanel();
        panel.setLayout( new BoxLayout( panel, BoxLayout.Y_AXIS ) );
        panel.add( canvas );
        panel.add( controlPanel );
        
        setContentPane( panel );
        setSize( FRAME_SIZE );
        setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
    }
    
    /*
     * HorizontalBarNode is a horizontal bar with a dynamic width and fixed height.
     * The word "CENTER" is printed at the center of the bar.
     * The width is adjusted to match the width of the canvas.
     * The bar can be vertically dragged, but dragging cannot change it's horizontal position.
     * Horizontal position can be changed only via the slider in the control panel.
     */
    private class HorizontalBarNode extends PNode {
        
        private static final double HEIGHT = 20;

        private Point2D _position;
        private Dimension2D _worldSize;
        private PPath _dragBoundsNode;
        
        public HorizontalBarNode( PPath dragBoundsNode ) {
            super();
            _position = new Point2D.Double();
            _worldSize = new PDimension();
            _dragBoundsNode = dragBoundsNode;
            addInputEventListener( new BoundedDragHandler( this, _dragBoundsNode ) );
            addInputEventListener( new CursorHandler() );
        }
        
        public void setPosition( Point2D position ) {
            _position.setLocation( position );
            updatePosition();
        }
        
        public void setWorldSize( Dimension2D worldSize ) {
            _worldSize.setSize( worldSize );
            updateWidth();
        }
        
        /* Adjusts the horizontal offset so that the horizontal center is at x=0 */
        private void updatePosition() {
            final double x = _position.getX() - ( getFullBounds().getWidth() / 2 );
            final double y = getOffset().getY();
            setOffset( x, y );
            updateDragBounds();
        }
        
        /* Adjusts the drag bound for the position and world size */
        private void updateDragBounds() {
            Rectangle2D dragBounds = new Rectangle2D.Double( getFullBounds().getX(), 0, getFullBounds().getWidth(), _worldSize.getHeight() );
            _dragBoundsNode.setPathTo( dragBounds );
        }
        
        /* Adjusts the width to match the world width */
        private void updateWidth() {
            removeAllChildren();
            
            // gray rectangle that is as wide as the canvas
            Rectangle2D rect = new Rectangle2D.Double( 0, 0, _worldSize.getWidth(), HEIGHT );
            PPath pathNode = new PPath( rect );
            pathNode.setStroke( new BasicStroke( 1f ) );
            pathNode.setStrokePaint( Color.BLACK );
            pathNode.setPaint( Color.GRAY );
            pathNode.setOffset( 0, 0 );
            
            // center "CENTER" in the rectangle
            PText textNode = new PText( "CENTER" );
            textNode.setTextPaint( Color.BLACK );
            double x = ( pathNode.getFullBounds().getWidth() / 2 ) - ( textNode.getFullBounds().getWidth() / 2 );
            double y = ( pathNode.getFullBounds().getHeight() / 2 ) - ( textNode.getFullBounds().getHeight() / 2 );
            textNode.setOffset( x, y );
            
            addChild( pathNode );
            addChild( textNode );
            
            updatePosition();
        }
    }
    
}
