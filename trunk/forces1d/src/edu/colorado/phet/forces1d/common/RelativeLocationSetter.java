package edu.colorado.phet.forces1d.common;

import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.common.view.phetgraphics.PhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetGraphicListener;
import edu.colorado.phet.common.view.util.RectangleUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

/**
 * User: Sam Reid
 * Date: Mar 6, 2005
 * Time: 3:37:42 PM
 * Copyright (c) Mar 6, 2005 by Sam Reid
 */
public abstract class RelativeLocationSetter {
    public static interface Target {
        public Rectangle getBounds();

        public void addBoundsObserver( BoundsObserver boundsObserver );
    }

    public static interface BoundsObserver {
        void boundsChanged();
    }

    public static interface Movable extends Target {
        public void setLocation( int x, int y );
    }

    public static class PhetGraphicTarget implements Target {
        PhetGraphic phetGraphic;

        public PhetGraphicTarget( PhetGraphic phetGraphic ) {
            this.phetGraphic = phetGraphic;
        }

        public Rectangle getBounds() {
            return phetGraphic.getBounds();
        }

        public void addBoundsObserver( final BoundsObserver boundsObserver ) {
            phetGraphic.addPhetGraphicListener( new PhetGraphicListener() {
                public void phetGraphicChanged( PhetGraphic phetGraphic ) {
                    boundsObserver.boundsChanged();
                }

                public void phetGraphicVisibilityChanged( PhetGraphic phetGraphic ) {
                }
            } );
        }
    }

    public static class MovablePhetGraphic extends PhetGraphicTarget implements Movable {
        public MovablePhetGraphic( PhetGraphic phetGraphic ) {
            super( phetGraphic );
        }

        /**
         * Accounts for the difference between registration point and location.
         *
         * @param x
         * @param y
         */
        public void setLocation( int x, int y ) {
            int origX = getBounds().x;
            int origY = getBounds().y;
            int dx = x - origX;
            int dy = y - origY;

            Point location = phetGraphic.getLocation();
            phetGraphic.setLocation( location.x + dx, location.y + dy );
        }

    }

    public abstract void layout( Target fixed, Movable tomove );

    public void layout( PhetGraphic fixed, PhetGraphic tomove ) {
        layout( new PhetGraphicTarget( fixed ), new MovablePhetGraphic( tomove ) );
    }

    public static class Top extends RelativeLocationSetter {
        int separation;

        public Top() {
            this( 0 );
        }

        public Top( int separation ) {
            this.separation = separation;
        }

        public void layout( Target fixed, Movable tomove ) {
            Rectangle targetBounds = fixed.getBounds();
            Point connector = RectangleUtils.getTopCenter( targetBounds );
            Rectangle movableBounds = tomove.getBounds();
            int x = connector.x - movableBounds.width / 2;
            int y = connector.y - movableBounds.height - separation;
            tomove.setLocation( x, y );
        }
    }

    public static class Right extends RelativeLocationSetter {
        int separation;

        public Right() {
            this( 0 );
        }

        public Right( int separation ) {
            this.separation = separation;
        }

        public void layout( Target fixed, Movable tomove ) {
            Rectangle targetBounds = fixed.getBounds();
            Point connector = RectangleUtils.getRightCenter( targetBounds );
            Rectangle movableBounds = tomove.getBounds();
            int x = connector.x + separation;
            int y = connector.y - movableBounds.height / 2;
            tomove.setLocation( x, y );
        }

    }

    public static class Bottom extends RelativeLocationSetter {
        int separation;

        public Bottom() {
            this( 0 );
        }

        public Bottom( int separation ) {
            this.separation = separation;
        }

        public void layout( Target fixed, Movable tomove ) {
            Rectangle targetBounds = fixed.getBounds();
            Point connector = RectangleUtils.getBottomCenter( targetBounds );
            Rectangle movableBounds = tomove.getBounds();
            int x = connector.x - movableBounds.width / 2;
            int y = connector.y + separation;
            tomove.setLocation( x, y );
        }

    }

    public static class Left extends RelativeLocationSetter {
        int separation;

        public Left() {
            this( 0 );
        }

        public Left( int separation ) {
            this.separation = separation;
        }

        public void layout( Target fixed, Movable tomove ) {
            Rectangle targetBounds = fixed.getBounds();
            Point connector = RectangleUtils.getLeftCenter( targetBounds );
            Rectangle movableBounds = tomove.getBounds();
            int x = connector.x - movableBounds.width - separation;
            int y = connector.y - movableBounds.height / 2;
            tomove.setLocation( x, y );
        }

    }

    public static void follow( final Target target, final Movable tomove, final RelativeLocationSetter rel ) {
        target.addBoundsObserver( new BoundsObserver() {
            public void boundsChanged() {
                rel.layout( target, tomove );
            }
        } );
        rel.layout( target, tomove );
    }

    public static void follow( final PhetGraphic target, final PhetGraphic tomove, final RelativeLocationSetter rel ) {
        follow( new PhetGraphicTarget( target ), new MovablePhetGraphic( tomove ), rel );
    }

    public static class RectangleTarget implements Target {
        private Rectangle bounds;

        public RectangleTarget( Rectangle bounds ) {
            this.bounds = bounds;
        }

        public Rectangle getBounds() {
            return new Rectangle( bounds );
        }

        public void addBoundsObserver( BoundsObserver boundsObserver ) {
            //no-op for regular rectangles.
        }
    }

    public static class JComponentTarget implements Target {
        JComponent jComponent;
        private ApparatusPanel panel;

        public JComponentTarget( JComponent jComponent ) {
            this( jComponent, null );
        }

        public JComponentTarget( JComponent jComponent, ApparatusPanel panel ) {
            this.jComponent = jComponent;
            this.panel = panel;
        }

        public Rectangle getBounds() {
            if( panel != null && jComponent.getParent() != panel ) {
                Rectangle r = SwingUtilities.convertRectangle( jComponent, jComponent.getBounds(), panel );
                return r;
            }
            else {
                return jComponent.getBounds();
            }
        }

        public void addBoundsObserver( final BoundsObserver boundsObserver ) {
            jComponent.addComponentListener( new ComponentAdapter() {
                public void componentMoved( ComponentEvent e ) {
                    boundsObserver.boundsChanged();
                }

                public void componentResized( ComponentEvent e ) {
                    boundsObserver.boundsChanged();
                }
            } );
            if( panel != null ) {
                panel.addComponentListener( new ComponentAdapter() {
                    public void componentResized( ComponentEvent e ) {
                        boundsObserver.boundsChanged();
                    }
                } );
            }
        }
    }
}