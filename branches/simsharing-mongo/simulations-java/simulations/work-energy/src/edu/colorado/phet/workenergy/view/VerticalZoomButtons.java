// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.workenergy.view;

import java.awt.*;
import java.awt.geom.GeneralPath;
import java.awt.image.BufferedImage;

import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils;
import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;
import edu.colorado.phet.common.piccolophet.event.ButtonEventHandler;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.mediabuttons.AbstractMediaButton;
import edu.colorado.phet.workenergy.WorkEnergyResources;
import edu.umd.cs.piccolo.PNode;

//TODO: move to piccolo-phet
public class VerticalZoomButtons extends PNode {
    static interface Listener {
        void actionPerformed();
    }

    private static class ZoomButton extends PNode {
        protected AbstractMediaButton button;

        private ZoomButton( final String imageName, final Listener zoomListener ) {
            button = new AbstractMediaButton( 30 ) {
                protected BufferedImage createImage() {
                    return BufferedImageUtils.multiScaleToHeight( WorkEnergyResources.getImage( imageName ), 30 );
                }
            };

            // this handler ensures that the button won't fire unless the mouse is released while inside the button
            //see DefaultIconButton
            ButtonEventHandler verticalZoomInListener = new ButtonEventHandler();
            button.addInputEventListener( verticalZoomInListener );
            verticalZoomInListener.addButtonEventListener( new ButtonEventHandler.ButtonEventAdapter() {
                public void fire() {
                    if ( button.isEnabled() ) { zoomListener.actionPerformed(); }
                }
            } );

            addChild( button );
        }

        public void setEnabled( boolean enabled ) {
            button.setEnabled( enabled );
        }
    }

    public VerticalZoomButtons( final Zoomable zoomable ) {
        final ZoomButton zoomInButton = new ZoomButton( "magnify-plus.png", new Listener() {
            public void actionPerformed() {
                zoomable.zoomIn();
            }
        } );
        final ZoomButton zoomOutButton = new ZoomButton( "magnify-minus.png", new Listener() {
            public void actionPerformed() {
                zoomable.zoomOut();
            }
        } );
        addChild( zoomOutButton );
        zoomable.addObserver( new SimpleObserver() {
            public void update() {
                zoomInButton.setEnabled( zoomable.isZoomInEnabled() );
                zoomOutButton.setEnabled( zoomable.isZoomOutEnabled() );
            }
        } );
        addChild( zoomInButton );

        //Add small icons to indicate zoom dimension (i.e. horizontal or vertical)
        PNode upIcon = new TriangleIcon();
        {
            upIcon.setOffset( 1.5, zoomInButton.getFullBounds().getHeight() - upIcon.getFullBounds().getHeight() - 1.5 / 2.0 );
            upIcon.rotateInPlace( Math.PI * 3 / 2.0 );//point up
        }
        addChild( upIcon );

        PNode downIcon = new TriangleIcon();
        {
            downIcon.setOffset( 1.5, downIcon.getFullBounds().getHeight() + 1.5 + zoomInButton.getFullBounds().getHeight() - upIcon.getFullBounds().getHeight() - 1.5 / 2.0 );
            downIcon.rotateInPlace( Math.PI / 2 );//Point down
        }
        addChild( downIcon );

        zoomInButton.setOffset( downIcon.getFullBounds().getWidth() + 1.5 * 2, 0 );
        zoomOutButton.setOffset( downIcon.getFullBounds().getWidth() + 1.5 * 2, zoomInButton.getFullBounds().getHeight() );
    }

    public static class TriangleIcon extends PNode {
        private double triangleHeight = 6;
        private double triangleWidth = 6;

        public TriangleIcon() {
            DoubleGeneralPath trianglePath = new DoubleGeneralPath( triangleWidth, triangleHeight / 2 );
            trianglePath.lineToRelative( -triangleWidth, -triangleHeight / 2 );
            trianglePath.lineToRelative( 0, triangleHeight );
            trianglePath.lineTo( triangleWidth, triangleHeight / 2 );
            GeneralPath path1 = trianglePath.getGeneralPath();
            addChild( new PhetPPath( path1, Color.black ) );
        }
    }
}

