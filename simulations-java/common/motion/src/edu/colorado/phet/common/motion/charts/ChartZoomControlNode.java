package edu.colorado.phet.common.motion.charts;

import edu.colorado.phet.common.motion.MotionResources;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils;
import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;
import edu.colorado.phet.common.piccolophet.event.ButtonEventHandler;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.mediabuttons.AbstractMediaButton;
import edu.umd.cs.piccolo.PNode;

import java.awt.*;
import java.awt.geom.GeneralPath;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * @author Sam Reid
 */
public class ChartZoomControlNode extends PNode {
    private VerticalZoomControl verticalZoomControl;
    private HorizontalZoomControl horizontalZoomControl;

    public ChartZoomControlNode(TemporalChart chart) {
        horizontalZoomControl = new HorizontalZoomControl(chart);
        addChild(horizontalZoomControl);
        
        verticalZoomControl = new VerticalZoomControl(chart);
        addChild(verticalZoomControl);
        
        verticalZoomControl.setOffset(0,horizontalZoomControl.getFullBounds().getMaxY());
    }

    public double getZoomControlWidth() {
        return horizontalZoomControl.getFullBounds().getWidth() + verticalZoomControl.getFullBounds().getWidth();
    }

    public void setHorizontalZoomButtonsVisible(boolean visible) {
        horizontalZoomControl.setVisible(visible);
        horizontalZoomControl.setPickable(visible);
        horizontalZoomControl.setChildrenPickable(visible);
    }

    public static interface Listener {
        void actionPerformed();
    }

    private static class ZoomButton extends PNode {
        protected AbstractMediaButton button;

        private ZoomButton(final String imageName, final Listener zoomListener) {
            button = new AbstractMediaButton(30) {
                protected BufferedImage createImage() {
                    try {
                        return BufferedImageUtils.multiScaleToHeight(MotionResources.loadBufferedImage(imageName), 30);
                    } catch (IOException e) {
                        e.printStackTrace();
                        throw new RuntimeException(e);
                    }
                }
            };

            // this handler ensures that the button won't fire unless the mouse is released while inside the button
            //see DefaultIconButton
            ButtonEventHandler verticalZoomInListener = new ButtonEventHandler();
            button.addInputEventListener(verticalZoomInListener);
            verticalZoomInListener.addButtonEventListener(new ButtonEventHandler.ButtonEventAdapter() {
                public void fire() {
                    if (button.isEnabled())
                        zoomListener.actionPerformed();
                }
            });

            addChild(button);
        }

        public void setEnabled(boolean enabled) {
            button.setEnabled(enabled);
        }
    }

    private static class VerticalZoomControl extends PNode {
        private VerticalZoomControl(final TemporalChart chart) {
            final ZoomButton zoomInButton = new ZoomButton("magnify-plus.png", new Listener() {
                public void actionPerformed() {
                    chart.zoomInVertical();
                }
            });
            chart.getDataModelBounds().addObserver(new SimpleObserver() {
                public void update() {
                    boolean enabled = chart.getDataModelBounds().getHeight() > 10.0 + 1E-6;
                    zoomInButton.setEnabled(enabled);
                    //TODO: cursor hand never goes away
                }
            });
            addChild(zoomInButton);

            ZoomButton zoomOutButton = new ZoomButton("magnify-minus.png", new Listener() {
                public void actionPerformed() {
                    chart.zoomOutVertical();
                }
            });
            addChild(zoomOutButton);

            zoomOutButton.setOffset(0, zoomInButton.getFullBounds().getHeight());
        }
    }

    private static class HorizontalZoomControl extends PNode {
        private double triangleHeight = 6;
        private double triangleWidth = 6;

        private HorizontalZoomControl(final TemporalChart chart) {
            final ZoomButton zoomInButton = new ZoomButton("magnify-plus.png", new Listener() {
                public void actionPerformed() {
                    chart.zoomInHorizontal();
                }
            });
            addChild(zoomInButton);

            final ZoomButton zoomOutButton = new ZoomButton("magnify-minus.png", new Listener() {
                public void actionPerformed() {
                    chart.zoomOutHorizontal();
                }
            });
            addChild(zoomOutButton);
            SimpleObserver updateButtonsEnabled = new SimpleObserver() {
                public void update() {
                    zoomInButton.setEnabled(chart.getDataModelBounds().getWidth() > 5.0 + 1E-6);
                    zoomOutButton.setEnabled(chart.getDataModelBounds().getWidth() < 20.0 - 1E-6);
                    //TODO: cursor hand never goes away
                }
            };
            chart.getDataModelBounds().addObserver(updateButtonsEnabled);
            updateButtonsEnabled.update();

            zoomOutButton.setOffset(0, 5);
            zoomInButton.setOffset(zoomOutButton.getFullBounds().getMaxX(), 5);

            //These small triangles look terrible without good rendering hints.
            PhetPPath pathLeft = new HighQualityPhetPPath(getTrianglePathLeft(), Color.black);
            addChild(pathLeft);
            PhetPPath pathRight = new HighQualityPhetPPath(getTrianglePathRight(), Color.black);
            addChild(pathRight);
            pathLeft.setOffset(zoomOutButton.getFullBounds().getMaxX() - pathLeft.getFullBounds().getWidth() - 0.5, 0);
            pathRight.setOffset(pathLeft.getFullBounds().getMaxX() + 1.5, 0);
        }

        private GeneralPath getTrianglePathLeft() {
            DoubleGeneralPath trianglePath = new DoubleGeneralPath(0, triangleHeight / 2);
            trianglePath.lineToRelative(triangleWidth, -triangleHeight / 2);
            trianglePath.lineToRelative(0, triangleHeight);
            trianglePath.lineTo(0, triangleHeight / 2);
            GeneralPath path1 = trianglePath.getGeneralPath();
            return path1;
        }

        private GeneralPath getTrianglePathRight() {
            DoubleGeneralPath trianglePath = new DoubleGeneralPath(triangleWidth, triangleHeight / 2);
            trianglePath.lineToRelative(-triangleWidth, -triangleHeight / 2);
            trianglePath.lineToRelative(0, triangleHeight);
            trianglePath.lineTo(triangleWidth, triangleHeight / 2);
            GeneralPath path1 = trianglePath.getGeneralPath();
            return path1;
        }
    }
}