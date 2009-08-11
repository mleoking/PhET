package edu.colorado.phet.acidbasesolutions.control;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Stroke;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.acidbasesolutions.ABSImages;
import edu.colorado.phet.acidbasesolutions.util.PNodeUtils;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PDimension;



public class MaximizeControlNode extends PhetPNode {
    
    private static final int X_SPACING = 6;
    private static final int X_MARGIN = 15;
    private static final int Y_MARGIN = 6;
    
    private static final Stroke BACKGROUND_STROKE = new BasicStroke( 1f );
    private static final Color BACKGROUND_STROKE_COLOR = Color.BLACK;
    private static final Color BACKGROUND_FILL_COLOR = Color.WHITE;
    private static final Color LABEL_PAINT = Color.BLACK;
    private static final Font LABEL_FONT = new PhetFont( 16 );

    private final PDimension maximizedSize, minimizedSize;
    private final PNode managedNode;
    private final PPath backgroundNode;
    private final PImage buttonNode;
    private final PImage tearOffNode;
    private final ArrayList<ChangeListener> listeners;
    private boolean isMaximized;
    
    public MaximizeControlNode( String label, PDimension maximizedSize, PNode managedNode ) {
        super();
        
        this.managedNode = managedNode;
        
        listeners = new ArrayList<ChangeListener>();
        
        // background
        backgroundNode = new PPath();
        backgroundNode.setStroke( BACKGROUND_STROKE );
        backgroundNode.setStrokePaint( BACKGROUND_STROKE_COLOR );
        backgroundNode.setPaint( BACKGROUND_FILL_COLOR );
        addChild( backgroundNode );
        
        // button
        isMaximized = false;
        buttonNode = new PImage( ABSImages.MAXIMIZE_BUTTON );
        buttonNode.scale( 1.5 );//XXX
        buttonNode.addInputEventListener( new CursorHandler() );
        buttonNode.addInputEventListener( new PBasicInputEventHandler() {
            public void mouseReleased( PInputEvent event ) {
                setMaximized( !isMaximized ); // toggle the button state
            }
        } );
        addChild( buttonNode );
        
        // label
        PText labelNode = new PText( label );
        labelNode.setTextPaint( LABEL_PAINT );
        labelNode.setFont( LABEL_FONT );
        addChild( labelNode );
        
        // tear-off icon
        tearOffNode = new PImage( ABSImages.TEAR_OFF_BUTTON );
        tearOffNode.addInputEventListener( new CursorHandler() );
        tearOffNode.addInputEventListener( new PBasicInputEventHandler() {
            public void mouseReleased( PInputEvent event ) {
                handleTearOff();
            }
        } );
        addChild( tearOffNode );
        
        // layout
        double xOffset = X_MARGIN;
        double yOffset = Y_MARGIN;
        buttonNode.setOffset( xOffset, yOffset );
        xOffset = buttonNode.getFullBoundsReference().getMaxX() + X_SPACING;
        yOffset = buttonNode.getFullBoundsReference().getCenterY() - ( labelNode.getFullBoundsReference().getHeight() / 2 );
        labelNode.setOffset( xOffset, yOffset );
        xOffset = maximizedSize.getWidth() - tearOffNode.getFullBoundsReference().getWidth() - X_MARGIN;
        yOffset = buttonNode.getFullBoundsReference().getCenterY() - ( tearOffNode.getFullBoundsReference().getHeight() / 2 );
        tearOffNode.setOffset( xOffset, yOffset );
        xOffset = X_MARGIN - PNodeUtils.getOriginXOffset( managedNode );
        yOffset = buttonNode.getFullBoundsReference().getMaxY() - PNodeUtils.getOriginYOffset( managedNode ) + Y_MARGIN;
        managedNode.setOffset( xOffset, yOffset );
        
        // sizes
        this.maximizedSize = new PDimension( maximizedSize.getWidth(), maximizedSize.getHeight() );
        double minimizedHeight = Math.max( buttonNode.getFullBoundsReference().getHeight(), labelNode.getFullBoundsReference().getHeight() ) + ( 2 * Y_MARGIN );
        this.minimizedSize = new PDimension( maximizedSize.getWidth(), minimizedHeight );
        
        // default state
        isMaximized = true; // force an update
        setMaximized( false );
    }

    public boolean isMaximized() {
        return isMaximized;
    }
    
    public void setMaximized( boolean b ) {
        if ( b != isMaximized ) {
            isMaximized = b;
            if ( isMaximized ) {
                buttonNode.setImage( ABSImages.MINIMIZE_BUTTON );
                backgroundNode.setPathTo( new Rectangle2D.Double( 0, 0, maximizedSize.getWidth(), maximizedSize.getHeight() ) );
                addChild( managedNode );
            }
            else {
                buttonNode.setImage( ABSImages.MAXIMIZE_BUTTON );
                backgroundNode.setPathTo( new Rectangle2D.Double( 0, 0, minimizedSize.getWidth(), minimizedSize.getHeight() ) );
                removeChild( managedNode );
            }
            fireActionPerformed();
        }
    }
    
    public void addChangeListener( ChangeListener listener ) {
        listeners.add( listener );
    }

    private void fireActionPerformed() {
        ChangeEvent event = new ChangeEvent( this );
        for ( ChangeListener listener : listeners ) {
            listener.stateChanged( event );
        }
    }
    
    private void handleTearOff() {
        //XXX
    }
}
