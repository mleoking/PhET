package edu.colorado.phet.forces1d.common_force1d.view.phetgraphics;

import java.awt.*;
import java.util.ArrayList;

import javax.swing.*;

/**
 * User: Sam Reid
 * Date: Dec 17, 2004
 * Time: 5:51:14 PM
 */
public interface RepaintStrategy {
    void repaint( int x, int y, int width, int height );

    void paintImmediately();

    public abstract static class Union implements RepaintStrategy {
        ArrayList rectangles = new ArrayList();
        Component component;

        public Union( Component component ) {
            this.component = component;
        }

        public void repaint( int x, int y, int width, int height ) {
            rectangles.add( new Rectangle( x, y, width, height ) );
        }

        public void paintImmediately() {
            Rectangle union = (Rectangle) rectangles.get( 0 );
            for ( int i = 0; i < rectangles.size(); i++ ) {
                Rectangle rectangle = (Rectangle) rectangles.get( i );
                union = union.union( rectangle );
            }
            doPaint( union );
            rectangles.clear();
        }

        abstract void doPaint( Rectangle union );
    }

    public static class RepaintUnion extends Union {
        public RepaintUnion( Component component ) {
            super( component );
        }

        void doPaint( Rectangle union ) {
            component.repaint( union.x, union.y, union.width, union.height );
        }
    }

    public static class ImmediateUnion extends Union {
        JComponent component;

        public ImmediateUnion( JComponent component ) {
            super( component );
            this.component = component;
        }

        void doPaint( Rectangle union ) {
            component.paintImmediately( union );
        }
    }

    /**
     * Adapter to go in a PhetGraphic.
     */
    public static class FalseComponent extends Component {
        private RepaintStrategy repaintStrategy;

        public FalseComponent( RepaintStrategy repaintStrategy ) {
            this.repaintStrategy = repaintStrategy;
        }

        public void repaint( int x, int y, int width, int height ) {
            repaintStrategy.repaint( x, y, width, height );
        }
    }
}
