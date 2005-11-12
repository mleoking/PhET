/* ===========================================================
 * JFreeChart : a free chart library for the Java(tm) platform
 * ===========================================================
 *
 * (C) Copyright 2000-2005, by Object Refinery Limited and Contributors.
 *
 * Project Info:  http://www.jfree.org/jfreechart/index.html
 *
 * This library is free software; you can redistribute it and/or modify it 
 * under the terms of the GNU Lesser General Public License as published by 
 * the Free Software Foundation; either version 2.1 of the License, or 
 * (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but 
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY 
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public 
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License 
 * along with this library; if not, write to the Free Software Foundation, 
 * Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307, USA.
 *
 * [Java is a trademark or registered trademark of Sun Microsystems, Inc. 
 * in the United States and other countries.]
 *
 * --------------------------
 * PlotPropertyEditPanel.java
 * --------------------------
 * (C) Copyright 2000-2005, by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   Andrzej Porebski;
 *                   Arnaud Lelievre;
 *                   Daniel Gredler;
 *
 * $Id$
 *
 * Changes (from 24-Aug-2001)
 * --------------------------
 * 24-Aug-2001 : Added standard source header. Fixed DOS encoding problem (DG);
 * 07-Nov-2001 : Separated the JCommon Class Library classes, JFreeChart now 
 *               requires jcommon.jar (DG);
 * 21-Nov-2001 : Allowed for null axes (DG);
 * 27-Aug-2002 : Small update to get existing axis properties working 
 *               again (DG);
 * 26-Nov-2002 : Changes by David M. O'Donnell for ContourPlot (DG);
 * 17-Jan-2003 : Plot classes have been moved into a separate package (DG);
 * 08-Sep-2003 : Added internationalization via use of properties 
 *               resourceBundle (RFE 690236) (AL); 
 * 24-Aug-2004 : Applied patch 1014378 (DG);
 * 17-Apr-2005 : Replaced 'new Boolean' with 'BooleanUtilities.valueOf' (DG);
 *
 */

package org.jfree.chart.ui;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ResourceBundle;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import org.jfree.chart.axis.Axis;
import org.jfree.chart.axis.ColorBar;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.ContourPlot;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.layout.LCBLayout;
import org.jfree.ui.PaintSample;
import org.jfree.ui.RectangleInsets;
import org.jfree.ui.StrokeChooserPanel;
import org.jfree.ui.StrokeSample;
import org.jfree.util.BooleanUtilities;

/**
 * A panel for editing the properties of a {@link Plot}.
 */
public class PlotPropertyEditPanel extends JPanel implements ActionListener {

    /** Orientation constants. */
    private final static String[] orientationNames = {"Vertical", "Horizontal"};
    private final static int ORIENTATION_VERTICAL = 0;
    private final static int ORIENTATION_HORIZONTAL = 1;

    /** The paint (color) used to fill the background of the plot. */
    private PaintSample backgroundPaintSample;

    /** The stroke (pen) used to draw the outline of the plot. */
    private StrokeSample outlineStrokeSample;

    /** The paint (color) used to draw the outline of the plot. */
    private PaintSample outlinePaintSample;

    /** 
     * A panel used to display/edit the properties of the domain axis (if any). 
     */
    private AxisPropertyEditPanel domainAxisPropertyPanel;

    /** 
     * A panel used to display/edit the properties of the range axis (if any).
     */
    private AxisPropertyEditPanel rangeAxisPropertyPanel;

    /** 
     * A panel used to display/edit the properties of the colorbar axis (if 
     * any).
     */
    private ColorBarPropertyEditPanel colorBarAxisPropertyPanel;

    /** An array of stroke samples to choose from. */
    private StrokeSample[] availableStrokeSamples;

    /** The insets for the plot. */
    private RectangleInsets plotInsets;

//    /** The insets text field. */
//    private InsetsTextField insetsTextField;

    /** 
     * The orientation for the plot (for <tt>CategoryPlot</tt>s and 
     * <tt>XYPlot</tt>s). 
     */
    private PlotOrientation plotOrientation;

    /** 
     * The orientation combo box (for <tt>CategoryPlot</tt>s and 
     * <tt>XYPlot</tt>s). 
     */
    private JComboBox orientationCombo;

    /** Whether or not to draw lines between each data point (for
     * <tt>LineAndShapeRenderer</tt>s and <tt>StandardXYItemRenderer</tt>s).
     */
    private Boolean drawLines;

    /**
     * The checkbox for whether or not to draw lines between each data point.
     */
    private JCheckBox drawLinesCheckBox;

    /** Whether or not to draw shapes at each data point (for
     * <tt>LineAndShapeRenderer</tt>s and <tt>StandardXYItemRenderer</tt>s).
     */
    private Boolean drawShapes;

    /**
     * The checkbox for whether or not to draw shapes at each data point.
     */
    private JCheckBox drawShapesCheckBox;

    /** The resourceBundle for the localization. */
    protected static ResourceBundle localizationResources 
        = ResourceBundle.getBundle("org.jfree.chart.ui.LocalizationBundle");

    /**
     * Standard constructor - constructs a panel for editing the properties of
     * the specified plot.
     * <P>
     * In designing the panel, we need to be aware that subclasses of Plot will
     * need to implement subclasses of PlotPropertyEditPanel - so we need to
     * leave one or two 'slots' where the subclasses can extend the user
     * interface.
     *
     * @param plot  the plot, which should be changed.
     */
    public PlotPropertyEditPanel(Plot plot) {

        this.plotInsets = plot.getInsets();
        this.backgroundPaintSample = new PaintSample(plot.getBackgroundPaint());
        this.outlineStrokeSample = new StrokeSample(plot.getOutlineStroke());
        this.outlinePaintSample = new PaintSample(plot.getOutlinePaint());
        if (plot instanceof CategoryPlot) {
            this.plotOrientation = ((CategoryPlot) plot).getOrientation();
        }
        else if (plot instanceof XYPlot) {
            this.plotOrientation = ((XYPlot) plot).getOrientation();
        }
        if (plot instanceof CategoryPlot) {
            CategoryItemRenderer renderer = ((CategoryPlot) plot).getRenderer();
            if (renderer instanceof LineAndShapeRenderer) {
                LineAndShapeRenderer r = (LineAndShapeRenderer) renderer;
                this.drawLines = BooleanUtilities.valueOf(r.isLinesVisible());
                this.drawShapes = BooleanUtilities.valueOf(r.isShapesVisible());
            }
        }
        else if (plot instanceof XYPlot) {
            XYItemRenderer renderer = ((XYPlot) plot).getRenderer();
            if (renderer instanceof StandardXYItemRenderer) {
                StandardXYItemRenderer r = (StandardXYItemRenderer) renderer;
                this.drawLines = BooleanUtilities.valueOf(r.getPlotLines());
                this.drawShapes = BooleanUtilities.valueOf(r.getPlotShapes());
            }
        }

        setLayout(new BorderLayout());

        this.availableStrokeSamples = new StrokeSample[3];
        this.availableStrokeSamples[0] 
            = new StrokeSample(new BasicStroke(1.0f));
        this.availableStrokeSamples[1] 
            = new StrokeSample(new BasicStroke(2.0f));
        this.availableStrokeSamples[2] 
            = new StrokeSample(new BasicStroke(3.0f));

        // create a panel for the settings...
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(
            BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(),
                plot.getPlotType() + localizationResources.getString(":")
            )
        );

        JPanel general = new JPanel(new BorderLayout());
        general.setBorder(
            BorderFactory.createTitledBorder(
                localizationResources.getString("General")
            )
        );

        JPanel interior = new JPanel(new LCBLayout(7));
        interior.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));

//        interior.add(new JLabel(localizationResources.getString("Insets")));
//        JButton button = new JButton(
//            localizationResources.getString("Edit...")
//        );
//        button.setActionCommand("Insets");
//        button.addActionListener(this);
//
//        this.insetsTextField = new InsetsTextField(this.plotInsets);
//        this.insetsTextField.setEnabled(false);
//        interior.add(this.insetsTextField);
//        interior.add(button);

        interior.add(
            new JLabel(localizationResources.getString("Outline_stroke"))
        );
        JButton button = new JButton(localizationResources.getString("Select..."));
        button.setActionCommand("OutlineStroke");
        button.addActionListener(this);
        interior.add(this.outlineStrokeSample);
        interior.add(button);

        interior.add(
            new JLabel(localizationResources.getString("Outline_Paint"))
        );
        button = new JButton(localizationResources.getString("Select..."));
        button.setActionCommand("OutlinePaint");
        button.addActionListener(this);
        interior.add(this.outlinePaintSample);
        interior.add(button);

        interior.add(
            new JLabel(localizationResources.getString("Background_paint"))
        );
        button = new JButton(localizationResources.getString("Select..."));
        button.setActionCommand("BackgroundPaint");
        button.addActionListener(this);
        interior.add(this.backgroundPaintSample);
        interior.add(button);

        if (this.plotOrientation != null) {
            boolean isVertical 
                = this.plotOrientation.equals(PlotOrientation.VERTICAL);
            int index 
                = isVertical ? ORIENTATION_VERTICAL : ORIENTATION_HORIZONTAL;
            interior.add(
                new JLabel(localizationResources.getString("Orientation"))
            );
            this.orientationCombo = new JComboBox(orientationNames);
            this.orientationCombo.setSelectedIndex(index);
            this.orientationCombo.setActionCommand("Orientation");
            this.orientationCombo.addActionListener(this);
            interior.add(new JPanel());
            interior.add(this.orientationCombo);
        }

        if (this.drawLines != null) {
            interior.add(
                new JLabel(localizationResources.getString("Draw_lines"))
            );
            this.drawLinesCheckBox = new JCheckBox();
            this.drawLinesCheckBox.setSelected(this.drawLines.booleanValue());
            this.drawLinesCheckBox.setActionCommand("DrawLines");
            this.drawLinesCheckBox.addActionListener(this);
            interior.add(new JPanel());
            interior.add(this.drawLinesCheckBox);
        }

        if (this.drawShapes != null) {
            interior.add(
                new JLabel(localizationResources.getString("Draw_shapes"))
            );
            this.drawShapesCheckBox = new JCheckBox();
            this.drawShapesCheckBox.setSelected(this.drawShapes.booleanValue());
            this.drawShapesCheckBox.setActionCommand("DrawShapes");
            this.drawShapesCheckBox.addActionListener(this);
            interior.add(new JPanel());
            interior.add(this.drawShapesCheckBox);
        }

        general.add(interior, BorderLayout.NORTH);

        JPanel appearance = new JPanel(new BorderLayout());
        appearance.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        appearance.add(general, BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));

        Axis domainAxis = null;
        if (plot instanceof CategoryPlot) {
            domainAxis = ((CategoryPlot) plot).getDomainAxis();
        }
        else if (plot instanceof XYPlot) {
            domainAxis = ((XYPlot) plot).getDomainAxis();
        }
        this.domainAxisPropertyPanel 
            = AxisPropertyEditPanel.getInstance(domainAxis);
        if (this.domainAxisPropertyPanel != null) {
            this.domainAxisPropertyPanel.setBorder(
                BorderFactory.createEmptyBorder(2, 2, 2, 2)
            );
            tabs.add(
                localizationResources.getString("Domain_Axis"), 
                this.domainAxisPropertyPanel
            );
        }

        Axis rangeAxis = null;
        if (plot instanceof CategoryPlot) {
            rangeAxis = ((CategoryPlot) plot).getRangeAxis();
        }
        else if (plot instanceof XYPlot) {
            rangeAxis = ((XYPlot) plot).getRangeAxis();
        }

        this.rangeAxisPropertyPanel 
            = AxisPropertyEditPanel.getInstance(rangeAxis);
        if (this.rangeAxisPropertyPanel != null) {
            this.rangeAxisPropertyPanel.setBorder(
                BorderFactory.createEmptyBorder(2, 2, 2, 2)
            );
            tabs.add(
                localizationResources.getString("Range_Axis"), 
                this.rangeAxisPropertyPanel
            );
        }

//dmo: added this panel for colorbar control. (start dmo additions)
        ColorBar colorBar = null;
        if (plot instanceof ContourPlot) {
            colorBar = ((ContourPlot) plot).getColorBar();
        }

        this.colorBarAxisPropertyPanel 
            = ColorBarPropertyEditPanel.getInstance(colorBar);
        if (this.colorBarAxisPropertyPanel != null) {
            this.colorBarAxisPropertyPanel.setBorder(
                BorderFactory.createEmptyBorder(2, 2, 2, 2)
            );
            tabs.add(
                localizationResources.getString("Color_Bar"), 
                this.colorBarAxisPropertyPanel
            );
        }
//dmo: (end dmo additions)

        tabs.add(localizationResources.getString("Appearance"), appearance);
        panel.add(tabs);

        add(panel);
    }

    /**
     * Returns the current plot insets.
     * 
     * @return The current plot insets.
     */
    public RectangleInsets getPlotInsets() {
        if (this.plotInsets == null) {
            this.plotInsets = new RectangleInsets(0.0, 0.0, 0.0, 0.0);
        }
        return this.plotInsets;
    }

    /**
     * Returns the current background paint.
     * 
     * @return The current background paint.
     */
    public Paint getBackgroundPaint() {
        return this.backgroundPaintSample.getPaint();
    }

    /**
     * Returns the current outline stroke.
     * 
     * @return The current outline stroke.
     */
    public Stroke getOutlineStroke() {
        return this.outlineStrokeSample.getStroke();
    }

    /**
     * Returns the current outline paint.
     * 
     * @return The current outline paint.
     */
    public Paint getOutlinePaint() {
        return this.outlinePaintSample.getPaint();
    }

    /**
     * Returns a reference to the panel for editing the properties of the
     * domain axis.
     *
     * @return A reference to a panel.
     */
    public AxisPropertyEditPanel getDomainAxisPropertyEditPanel() {
        return this.domainAxisPropertyPanel;
    }

    /**
     * Returns a reference to the panel for editing the properties of the
     * range axis.
     *
     * @return A reference to a panel.
     */
    public AxisPropertyEditPanel getRangeAxisPropertyEditPanel() {
        return this.rangeAxisPropertyPanel;
    }

    /**
     * Handles user actions generated within the panel.
     * @param event     the event
     */
    public void actionPerformed(ActionEvent event) {
        String command = event.getActionCommand();
        if (command.equals("BackgroundPaint")) {
            attemptBackgroundPaintSelection();
        }
        else if (command.equals("OutlineStroke")) {
            attemptOutlineStrokeSelection();
        }
        else if (command.equals("OutlinePaint")) {
            attemptOutlinePaintSelection();
        }
//        else if (command.equals("Insets")) {
//            editInsets();
//        }
        else if (command.equals("Orientation")) {
            attemptOrientationSelection();
        }
        else if (command.equals("DrawLines")) {
            attemptDrawLinesSelection();
        }
        else if (command.equals("DrawShapes")) {
            attemptDrawShapesSelection();
        }
    }

    /**
     * Allow the user to change the background paint.
     */
    private void attemptBackgroundPaintSelection() {
        Color c;
        c = JColorChooser.showDialog(
            this, localizationResources.getString("Background_Color"),
            Color.blue
        );
        if (c != null) {
            this.backgroundPaintSample.setPaint(c);
        }
    }

    /**
     * Allow the user to change the outline stroke.
     */
    private void attemptOutlineStrokeSelection() {
        StrokeChooserPanel panel 
            = new StrokeChooserPanel(null, this.availableStrokeSamples);
        int result = JOptionPane.showConfirmDialog(this, panel,
            localizationResources.getString("Stroke_Selection"),
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            this.outlineStrokeSample.setStroke(panel.getSelectedStroke());
        }
    }

    /**
     * Allow the user to change the outline paint.  We use JColorChooser, so
     * the user can only choose colors (a subset of all possible paints).
     */
    private void attemptOutlinePaintSelection() {
        Color c;
        c = JColorChooser.showDialog(
            this, localizationResources.getString("Outline_Color"), Color.blue
        );
        if (c != null) {
            this.outlinePaintSample.setPaint(c);
        }
    }

//    /**
//     * Allow the user to edit the individual insets' values.
//     */
//    private void editInsets() {
//        InsetsChooserPanel panel = new InsetsChooserPanel(this.plotInsets);
//        int result = JOptionPane.showConfirmDialog(
//            this, panel, localizationResources.getString("Edit_Insets"),
//            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE
//        );
//
//        if (result == JOptionPane.OK_OPTION) {
//            this.plotInsets = panel.getInsets();
//            this.insetsTextField.setInsets(this.plotInsets);
//        }
//
//    }
//
    /**
     * Allow the user to modify the plot orientation if this is an editor for a
     * <tt>CategoryPlot</tt> or a <tt>XYPlot</tt>.
     */
    private void attemptOrientationSelection() {

        int index = this.orientationCombo.getSelectedIndex();

        if (index == ORIENTATION_VERTICAL) {
            this.plotOrientation = PlotOrientation.VERTICAL;
        }
        else {
            this.plotOrientation = PlotOrientation.HORIZONTAL;
        }
    }

    /**
     * Allow the user to modify whether or not lines are drawn between data 
     * points by <tt>LineAndShapeRenderer</tt>s and 
     * <tt>StandardXYItemRenderer</tt>s.
     */
    private void attemptDrawLinesSelection() {
        this.drawLines = BooleanUtilities.valueOf(
            this.drawLinesCheckBox.isSelected()
        );
    }

    /**
     * Allow the user to modify whether or not shapes are drawn at data points
     * by <tt>LineAndShapeRenderer</tt>s and <tt>StandardXYItemRenderer</tt>s.
     */
    private void attemptDrawShapesSelection() {
        this.drawShapes = BooleanUtilities.valueOf(
            this.drawShapesCheckBox.isSelected()
        );
    }

    /**
     * Updates the plot properties to match the properties defined on the panel.
     *
     * @param plot  The plot.
     */
    public void updatePlotProperties(Plot plot) {

        // set the plot properties...
        plot.setOutlinePaint(getOutlinePaint());
        plot.setOutlineStroke(getOutlineStroke());
        plot.setBackgroundPaint(getBackgroundPaint());
        plot.setInsets(getPlotInsets());

        // then the axis properties...
        if (this.domainAxisPropertyPanel != null) {
            Axis domainAxis = null;
            if (plot instanceof CategoryPlot) {
                CategoryPlot p = (CategoryPlot) plot;
                domainAxis = p.getDomainAxis();
            }
            else if (plot instanceof XYPlot) {
                XYPlot p = (XYPlot) plot;
                domainAxis = p.getDomainAxis();
            }
            if (domainAxis != null) {
                this.domainAxisPropertyPanel.setAxisProperties(domainAxis);
            }
        }

        if (this.rangeAxisPropertyPanel != null) {
            Axis rangeAxis = null;
            if (plot instanceof CategoryPlot) {
                CategoryPlot p = (CategoryPlot) plot;
                rangeAxis = p.getRangeAxis();
            }
            else if (plot instanceof XYPlot) {
                XYPlot p = (XYPlot) plot;
                rangeAxis = p.getRangeAxis();
            }
            if (rangeAxis != null) {
                this.rangeAxisPropertyPanel.setAxisProperties(rangeAxis);
            }
        }

        if (this.plotOrientation != null) {
            if (plot instanceof CategoryPlot) {
                CategoryPlot p = (CategoryPlot) plot;
                p.setOrientation(this.plotOrientation);
            }
            else if (plot instanceof XYPlot) {
                XYPlot p = (XYPlot) plot;
                p.setOrientation(this.plotOrientation);
            }
        }

        if (this.drawLines != null) {
            if (plot instanceof CategoryPlot) {
                CategoryPlot p = (CategoryPlot) plot;
                CategoryItemRenderer r = p.getRenderer();
                if (r instanceof LineAndShapeRenderer) {
                    ((LineAndShapeRenderer) r).setLinesVisible(
                        this.drawLines.booleanValue()
                    );
                }
            }
            else if (plot instanceof XYPlot) {
                XYPlot p = (XYPlot) plot;
                XYItemRenderer r = p.getRenderer();
                if (r instanceof StandardXYItemRenderer) {
                    ((StandardXYItemRenderer) r).setPlotLines(
                        this.drawLines.booleanValue()
                    );
                }
            }
        }

        if (this.drawShapes != null) {
            if (plot instanceof CategoryPlot) {
                CategoryPlot p = (CategoryPlot) plot;
                CategoryItemRenderer r = p.getRenderer();
                if (r instanceof LineAndShapeRenderer) {
                    ((LineAndShapeRenderer) r).setShapesVisible(
                        this.drawShapes.booleanValue()
                    );
                }
            }
            else if (plot instanceof XYPlot) {
                XYPlot p = (XYPlot) plot;
                XYItemRenderer r = p.getRenderer();
                if (r instanceof StandardXYItemRenderer) {
                    ((StandardXYItemRenderer) r).setPlotShapes(
                        this.drawShapes.booleanValue()
                    );
                }
            }
        }

//dmo: added this panel for colorbar control. (start dmo additions)
        if (this.colorBarAxisPropertyPanel != null) {
            ColorBar colorBar = null;
            if (plot instanceof  ContourPlot) {
                ContourPlot p = (ContourPlot) plot;
                colorBar = p.getColorBar();
            }
            if (colorBar != null) {
                this.colorBarAxisPropertyPanel.setAxisProperties(colorBar);
            }
        }
//dmo: (end dmo additions)

    }

}
