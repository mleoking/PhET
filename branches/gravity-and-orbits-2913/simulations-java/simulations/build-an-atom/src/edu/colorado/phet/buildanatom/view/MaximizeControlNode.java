// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.buildanatom.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Stroke;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;
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

/**
 * Node that contains other PNodes and that can be minimized/maximized to
 * hide/show the PNodes contained within it.  Note that there is a class in
 * phetcommon that is similar to this, but that didn't serve the purposes
 * needed.  If you need similar functionality, this should be moved to
 * phetcommon.
 *
 * @author Chris Malley
 * @author John Blanco
 * @author Sam Reid
 */
public class MaximizeControlNode extends PhetPNode {
    private static final int X_MARGIN = 15;
    private static final int Y_MARGIN = 6;

    private static final Stroke BACKGROUND_STROKE = new BasicStroke( 1f );
    private static final Color BACKGROUND_STROKE_COLOR = Color.BLACK;
    private static final Color BACKGROUND_FILL_COLOR = null;
    private static final Color LABEL_PAINT = Color.BLACK;
    private static final Font LABEL_FONT = new PhetFont( 18, true );

    private final PDimension maximizedSize, minimizedSize;
    private final PNode managedNode;
    private final PPath backgroundNode;
    private final PImage buttonNode;
    private final ArrayList<ChangeListener> listeners;
    private boolean isMaximized;

    public MaximizeControlNode( String label, PDimension maximizedSize, PNode managedNode, boolean isMaximized ) {
        super();

        this.managedNode = managedNode;
        this.isMaximized = isMaximized;

        listeners = new ArrayList<ChangeListener>();

        // background
        backgroundNode = new PPath();
        backgroundNode.setStroke( BACKGROUND_STROKE );
        backgroundNode.setStrokePaint( BACKGROUND_STROKE_COLOR );
        backgroundNode.setPaint( BACKGROUND_FILL_COLOR );
        addChild( backgroundNode );

        // button
        buttonNode = new PImage( PhetCommonResources.getImage( "buttons/maximizeButton.png" ) );
        buttonNode.scale( 1.25 );
        buttonNode.addInputEventListener( new CursorHandler() );
        buttonNode.addInputEventListener( new PBasicInputEventHandler() {
            @Override
            public void mouseReleased( PInputEvent event ) {
                setMaximized( !MaximizeControlNode.this.isMaximized ); // toggle the button state
            }
        } );
        addChild( buttonNode );

        // label
        PText labelNode = new PText( label );
        labelNode.setTextPaint( LABEL_PAINT );
        labelNode.setFont( LABEL_FONT );
        addChild( labelNode );

        // layout
        double xOffset = X_MARGIN;//these variables get reassigned and reused
        double yOffset = Y_MARGIN;
        buttonNode.setOffset( maximizedSize.getWidth() - buttonNode.getFullBoundsReference().width - xOffset, yOffset );
        yOffset = buttonNode.getFullBoundsReference().getCenterY() - ( labelNode.getFullBoundsReference().getHeight() / 2 );
        xOffset = maximizedSize.width / 2 - labelNode.getFullBoundsReference().width / 2;
        labelNode.setOffset( xOffset, yOffset );

        // sizes
        this.maximizedSize = new PDimension( maximizedSize.getWidth(), maximizedSize.getHeight() );
        double minimizedHeight = Math.max( buttonNode.getFullBoundsReference().getHeight(), labelNode.getFullBoundsReference().getHeight() ) + ( 2 * Y_MARGIN );
        this.minimizedSize = new PDimension( maximizedSize.getWidth(), minimizedHeight );

        // default state
        this.isMaximized = !isMaximized; // force an update
        setMaximized( isMaximized );
    }

    public boolean isMaximized() {
        return isMaximized;
    }

    public void setMaximized( boolean b ) {
        if ( b != isMaximized ) {
            isMaximized = b;
            if ( isMaximized ) {
                buttonNode.setImage( PhetCommonResources.getImage( "buttons/minimizeButton.png" ) );
                backgroundNode.setPathTo( new Rectangle2D.Double( 0, 0, maximizedSize.getWidth(), maximizedSize.getHeight() ) );
                addChild( managedNode );
            }
            else {
                buttonNode.setImage( PhetCommonResources.getImage( "buttons/maximizeButton.png" ) );
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

    public void reset() {

    }
}
