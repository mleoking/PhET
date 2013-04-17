// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.motion.charts;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.piccolophet.nodes.MinimizeMaximizeNode;
import edu.umd.cs.piccolo.PNode;

/**
 * @author Sam Reid
 */
public class MinimizeMaximizeButton extends PNode {
    private MinimizeMaximizeNode node;
    private BooleanProperty maximized;
    private final boolean defaultMaximizedValue;

    public MinimizeMaximizeButton( IUserComponent minimizeButtonComponent, IUserComponent maximizeButtonComponent, String title ) {
        this( minimizeButtonComponent, maximizeButtonComponent, title, true );
    }

    public MinimizeMaximizeButton( IUserComponent minimizeButtonComponent, IUserComponent maximizeButtonComponent, String title, boolean maximizedValue ) {

        node = new MinimizeMaximizeNode( minimizeButtonComponent, maximizeButtonComponent, title, MinimizeMaximizeNode.BUTTON_RIGHT );
        addChild( node );

        SimpleObserver maximizedObserver = new SimpleObserver() {
            public void update() {
                node.setMaximized( maximized.get() );
            }
        };

        this.maximized = new BooleanProperty( maximizedValue );
        this.maximized.addObserver( maximizedObserver );

        this.defaultMaximizedValue = maximizedValue;
        node.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                maximized.set( node.isMaximized() );
            }
        } );
    }

    public BooleanProperty getMaximized() {
        return maximized;
    }

    public void reset() {
        maximized.set( defaultMaximizedValue );
    }
}
