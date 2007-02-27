/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
*        Liquid Look and Feel                                                   *
*                                                                              *
*  Author, Miroslav Lazarevic                                                  *
*                                                                              *
*   For licensing information and credits, please refer to the                 *
*   comment in file com.birosoft.liquid.LiquidLookAndFeel                      *
*                                                                              *
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package com.birosoft.liquid;

import com.birosoft.liquid.skin.Skin;
import com.birosoft.liquid.skin.SkinSimpleButtonIndexModel;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;

import javax.swing.JComponent;
import javax.swing.JSlider;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicSliderUI;


public class LiquidSliderUI extends BasicSliderUI {
    /** the skin for the horizontal thumb*/
    private static Skin skinThumbHoriz;

    /** the skin for the vertical thumb*/
    private static Skin skinThumbVert;

    /** the skin for the horizontal slider*/
    private static Skin skinHorizSlider;

    /** the skin for the vertical slider*/
    private static Skin skinVertSlider;
    private Skin skinSlider;
    private SkinSimpleButtonIndexModel skinIndexModel = new SkinSimpleButtonIndexModel();
    protected boolean isRollover = false;
    protected boolean wasRollover = false;
    protected boolean isDragging = false;
    protected TrackListener trackListener;

    public LiquidSliderUI() {
        super(null);
    }

    protected TrackListener createTrackListener(JSlider slider) {
        return new MyTrackListener();
    }

    protected Dimension getThumbSize() {
        Dimension size = getSkinThumb().getSize();

        return size;
    }

    public void paintThumb(Graphics g) {
        Rectangle knobBounds = thumbRect;

        int index = skinIndexModel.getIndexForState(slider.isEnabled(),
                isRollover, isDragging);
        getSkinThumb().drawCentered(g, index, knobBounds.x, knobBounds.y,
            knobBounds.width, knobBounds.height);
    }

    public static ComponentUI createUI(JComponent c) {
        return new LiquidSliderUI();
    }

    /**
     * Returns the shorter dimension of the track.
     */
    protected int getTrackWidth() {
        // This strange calculation is here to keep the
        // track in proportion to the thumb.
        final double kIdealTrackWidth = 7.0;
        final double kIdealThumbHeight = 16.0;
        final double kWidthScalar = kIdealTrackWidth / kIdealThumbHeight;

        if (slider.getOrientation() == JSlider.HORIZONTAL) {
            return (int) (kWidthScalar * thumbRect.height);
        } else {
            return (int) (kWidthScalar * thumbRect.width);
        }
    }

    protected int getThumbOverhang() {
        if (slider.getOrientation() == JSlider.VERTICAL) {
            return (int) (getThumbSize().getWidth() - getTrackWidth()) / 2;
        } else {
            return (int) (getThumbSize().getHeight() - getTrackWidth()) / 2;
        }
    }

    public void paint(Graphics g, JComponent c) {
        c.setOpaque(false);
        g.setColor(new Color(0, 0, 0, 0));
        g.fillRect(0, 0, c.getWidth(), c.getHeight());
        super.paint(g, c);
    }

    public void paintTicks(Graphics g) {
        Rectangle tickBounds = tickRect;
        int i;
        int maj;
        int min;
        int max;
        int w = tickBounds.width;
        int h = tickBounds.height;
        int centerEffect;
        int tickHeight;
        boolean leftToRight = slider.getComponentOrientation().isLeftToRight();

        g.setColor(new Color(0, 0, 0, 0));
        g.fillRect(tickBounds.x, tickBounds.y, tickBounds.width,
            tickBounds.height);
        g.setColor(Color.black);

        maj = slider.getMajorTickSpacing();
        min = slider.getMinorTickSpacing();

        if (slider.getOrientation() == JSlider.HORIZONTAL) {
            g.translate(0, tickBounds.y);

            int value = slider.getMinimum();
            int xPos = 0;

            if (slider.getMinorTickSpacing() > 0) {
                while (value <= slider.getMaximum()) {
                    xPos = xPositionForValue(value);
                    paintMinorTickForHorizSlider(g, tickBounds, xPos);
                    value += slider.getMinorTickSpacing();
                }
            }

            if (slider.getMajorTickSpacing() > 0) {
                value = slider.getMinimum();

                while (value <= slider.getMaximum()) {
                    xPos = xPositionForValue(value);
                    paintMajorTickForHorizSlider(g, tickBounds, xPos);
                    value += slider.getMajorTickSpacing();
                }
            }

            g.translate(0, -tickBounds.y);
        } else {
            g.translate(tickBounds.x, 0);

            int value = slider.getMinimum();
            int yPos = 0;

            if (slider.getMinorTickSpacing() > 0) {
                int offset = 0;

                if (!leftToRight) {
                    offset = tickBounds.width - (tickBounds.width / 2);
                    g.translate(offset, 0);
                }

                while (value <= slider.getMaximum()) {
                    yPos = yPositionForValue(value);
                    paintMinorTickForVertSlider(g, tickBounds, yPos);
                    value += slider.getMinorTickSpacing();
                }

                if (!leftToRight) {
                    g.translate(-offset, 0);
                }
            }

            if (slider.getMajorTickSpacing() > 0) {
                value = slider.getMinimum();

                if (!leftToRight) {
                    g.translate(2, 0);
                }

                while (value <= slider.getMaximum()) {
                    yPos = yPositionForValue(value);
                    paintMajorTickForVertSlider(g, tickBounds, yPos);
                    value += slider.getMajorTickSpacing();
                }

                if (!leftToRight) {
                    g.translate(-2, 0);
                }
            }

            g.translate(-tickBounds.x, 0);
        }
    }

    public void paintTrack(Graphics g) {
        Color trackColor = Color.red;

        boolean leftToRight = slider.getComponentOrientation().isLeftToRight();

        g.translate(trackRect.x, trackRect.y);

        int trackLeft = 0;
        int trackTop = 0;
        int trackRight = 0;
        int trackBottom = 0;

        // Draw the track
        if (slider.getOrientation() == JSlider.HORIZONTAL) {
            trackBottom = (trackRect.height - 1) - getThumbOverhang();
            trackTop = trackBottom - (getTrackWidth() - 1);
            trackRight = trackRect.width - 1;

            int h = (trackBottom - trackTop - getSkinHorizSlider().getVsize()) / 2;
            getSkinHorizSlider().draw(g, 0, trackLeft, trackTop + h,
                trackRight - trackLeft, getSkinHorizSlider().getVsize());
        } else {
            if (leftToRight) {
                trackLeft = (trackRect.width - getThumbOverhang()) -
                    getTrackWidth();
                trackRight = (trackRect.width - getThumbOverhang()) - 1;
            } else {
                trackLeft = getThumbOverhang();
                trackRight = (getThumbOverhang() + getTrackWidth()) - 1;
            }

            trackBottom = trackRect.height - 1;

            int w = (trackRight - trackLeft - getSkinVertSlider().getHsize()) / 2;
            getSkinVertSlider().draw(g, 0, trackLeft + w, trackTop,
                getSkinVertSlider().getHsize(), trackBottom - trackTop);
        }

        g.translate(-trackRect.x, -trackRect.y);
    }

    protected void paintMinorTickForHorizSlider(Graphics g,
        Rectangle tickBounds, int x) {
        g.setColor(LiquidLookAndFeel.getDarkControl());
        g.drawLine(x, 0, x, (tickBounds.height / 2) - 1);
    }

    protected void paintMajorTickForHorizSlider(Graphics g,
        Rectangle tickBounds, int x) {
        g.setColor(LiquidLookAndFeel.getDarkControl());
        g.drawLine(x, 0, x, tickBounds.height - 2);
    }

    protected void paintMinorTickForVertSlider(Graphics g,
        Rectangle tickBounds, int y) {
        g.setColor(LiquidLookAndFeel.getDarkControl());
        g.drawLine(0, y, (tickBounds.width / 2) - 1, y);
    }

    protected void paintMajorTickForVertSlider(Graphics g,
        Rectangle tickBounds, int y) {
        g.setColor(LiquidLookAndFeel.getDarkControl());
        g.drawLine(0, y, tickBounds.width - 2, y);
    }

    /**
     * Returns the skinHorizSlider.
     * @return SkinGenericButton
     */
    public static Skin getSkinHorizSlider() {
        if (skinHorizSlider == null) {
            skinHorizSlider = new Skin("sliderhorizbackground.png", 1, 6, 0, 6,
                    0);
        }

        return skinHorizSlider;
    }

    /**
     * Returns the skinThumbHoriz.
     * @return SkinCenteredButton
     */
    public static Skin getSkinThumbHoriz() {
        if (skinThumbHoriz == null) {
            skinThumbHoriz = new Skin("sliderhoriz.png", 4, 0);
        }

        return skinThumbHoriz;
    }

    /**
     * Returns the skinThumbVert.
     * @return SkinCenteredButton
     */
    public static Skin getSkinThumbVert() {
        if (skinThumbVert == null) {
            skinThumbVert = new Skin("slidervert.png", 4, 0);
        }

        return skinThumbVert;
    }

    /**
     * Returns the skinVertSlider.
     * @return SkinGenericButton
     */
    public static Skin getSkinVertSlider() {
        if (skinVertSlider == null) {
            skinVertSlider = new Skin("slidervertbackground.png", 1, 0, 6, 0, 6);
        }

        return skinVertSlider;
    }

    /**
     * Returns the skinSlider.
     * @return Skin
     */
    public Skin getSkinThumb() {
        if (skinSlider == null) {
            skinSlider = (slider.getOrientation() == JSlider.HORIZONTAL)
                ? getSkinThumbHoriz() : getSkinThumbVert();
        }

        return skinSlider;
    }

    /**
     * This TrackListener extends the BasicSliderUI.TrackListener such that
     * rollover and dragging state can be tracked.
     */
    class MyTrackListener extends BasicSliderUI.TrackListener {
        public void mouseReleased(MouseEvent e) {
            super.mouseReleased(e);
            isDragging = false;
            slider.repaint();
        }

        public void mousePressed(MouseEvent e) {
            super.mousePressed(e);

            if (thumbRect.contains(e.getX(), e.getY())) {
                isDragging = true;
            }

            slider.repaint();
        }

        public void mouseEntered(MouseEvent e) {
            isRollover = false;
            wasRollover = false;

            if (thumbRect.contains(e.getX(), e.getY())) {
                isRollover = true;
            }
        }

        public void mouseExited(MouseEvent e) {
            isRollover = false;

            if (isRollover != wasRollover) {
                slider.repaint();
                wasRollover = isRollover;
            }
        }

        public void mouseDragged(MouseEvent e) {
            if (thumbRect.contains(e.getX(), e.getY())) {
                isRollover = true;
            }

            super.mouseDragged(e);
        }

        public void mouseMoved(MouseEvent e) {
            if (thumbRect.contains(e.getX(), e.getY())) {
                isRollover = true;

                if (isRollover != wasRollover) {
                    slider.repaint();
                    wasRollover = isRollover;
                }
            } else {
                isRollover = false;

                if (isRollover != wasRollover) {
                    slider.repaint();
                    wasRollover = isRollover;
                }
            }
        }
    }
}
