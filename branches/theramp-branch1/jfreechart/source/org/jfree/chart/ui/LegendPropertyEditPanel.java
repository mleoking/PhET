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
 * ----------------------------
 * LegendPropertyEditPanel.java
 * ----------------------------
 * (C) Copyright 2000-2004, by Object Refinery Limited.
 *
 * Original Author:  David Gilbert;
 * Contributor(s):   Arnaud Lelievre;
 *                   Daniel Gredler;
 *
 * $Id$
 *
 * Changes (from 24-Aug-2001)
 * --------------------------
 * 24-Aug-2001 : Added standard source header. Fixed DOS encoding problem (DG);
 * 07-Nov-2001 : Separated the JCommon Class Library classes, JFreeChart now 
 *               requires jcommon.jar (DG);
 * 25-Jun-2002 : Revised header, removed redundant code (DG);
 * 08-Sep-2003 : Added internationalization via use of properties 
 *               resourceBundle (RFE 690236) (AL); 
 * 24-Aug-2004 : Applied patch 1014378 (DG);
 *
 */

package org.jfree.chart.ui;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ResourceBundle;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.OldLegend;
import org.jfree.chart.DefaultOldLegend;
import org.jfree.layout.LCBLayout;
import org.jfree.ui.FontChooserPanel;
import org.jfree.ui.FontDisplayField;
import org.jfree.ui.PaintSample;
import org.jfree.ui.StrokeChooserPanel;
import org.jfree.ui.StrokeSample;

/**
 * A panel for editing the properties of a {@link OldLegend}.
 */
class LegendPropertyEditPanel extends JPanel implements ActionListener {

    /** Whether or not to display the legend on the chart. */
    private boolean showLegend;

    /** The checkbox to indicate whether or not to display the legend. */
    private JCheckBox showLegendCheckBox;

    /** The stroke (pen) used to draw the legend outline. */
    private StrokeSample outlineStroke;

    /** The button used to select the legend outline stroke. */
    private JButton selectOutlineStrokeButton;

    /** The paint (color) used to draw the legend outline. */
    private PaintSample outlinePaint;

    /** The button used to select the legend outline color. */
    private JButton selectOutlinePaintButton;

    /** The paint (color) used to fill the legend background. */
    private PaintSample backgroundPaint;

    /** The button used to select the legend background color. */
    private JButton selectBackgroundPaintButton;

    /** The font used to draw the series names. */
    private Font seriesFont;

    /** The button used to select the series name font. */
    private JButton selectSeriesFontButton;

    /** The paint (color) used to draw the series names. */
    private PaintSample seriesPaint;

    /** The button used to select the series name paint. */
    private JButton selectSeriesPaintButton;

    /** An array of strokes (pens) to select from. */
    private StrokeSample[] availableStrokeSamples;

    /** A field for displaying the series label font. */
    private FontDisplayField fontDisplayField;

    /** The resourceBundle for the localization. */
    protected static ResourceBundle localizationResources 
        = ResourceBundle.getBundle("org.jfree.chart.ui.LocalizationBundle");

    /**
     * Standard constructor: builds a panel based on the specified legend.
     * If the specified legend is <tt>null</tt>, the panel will reflect the
     * fact that no legend is to be displayed.
     *
     * @param legend    the legend, which should be changed.
     */
    public LegendPropertyEditPanel(OldLegend legend) {

        DefaultOldLegend l = (legend != null 
            ? (DefaultOldLegend) legend : new DefaultOldLegend());
        this.showLegend = (legend != null);
        this.outlineStroke = new StrokeSample(l.getOutlineStroke());
        this.outlinePaint = new PaintSample(l.getOutlinePaint());
        this.backgroundPaint = new PaintSample(l.getBackgroundPaint());
        this.seriesFont = l.getItemFont();
        this.seriesPaint = new PaintSample(l.getItemPaint());

        this.availableStrokeSamples = new StrokeSample[4];
        this.availableStrokeSamples[0] 
            = new StrokeSample(new BasicStroke(1.0f));
        this.availableStrokeSamples[1] 
            = new StrokeSample(new BasicStroke(2.0f));
        this.availableStrokeSamples[2] 
            = new StrokeSample(new BasicStroke(3.0f));
        this.availableStrokeSamples[3] 
            = new StrokeSample(new BasicStroke(4.0f));

        setLayout(new BorderLayout());

        JPanel general = new JPanel(new BorderLayout());
        general.setBorder(
            BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), 
                localizationResources.getString("General")
            )
        );

        JPanel interior = new JPanel(new LCBLayout(6));
        interior.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));

        interior.add(
            new JLabel(localizationResources.getString("Show_Legend"))
        );
        this.showLegendCheckBox = new JCheckBox();
        this.showLegendCheckBox.setSelected(this.showLegend);
        this.showLegendCheckBox.setActionCommand("ShowLegend");
        this.showLegendCheckBox.addActionListener(this);
        interior.add(new JPanel());
        interior.add(this.showLegendCheckBox);

        interior.add(new JLabel(localizationResources.getString("Outline")));
        interior.add(this.outlineStroke);
        this.selectOutlineStrokeButton 
            = new JButton(localizationResources.getString("Select..."));
        this.selectOutlineStrokeButton.setActionCommand("OutlineStroke");
        this.selectOutlineStrokeButton.addActionListener(this);
        interior.add(this.selectOutlineStrokeButton);

        interior.add(
            new JLabel(localizationResources.getString("Outline_Paint"))
        );
        this.selectOutlinePaintButton 
            = new JButton(localizationResources.getString("Select..."));
        this.selectOutlinePaintButton.setActionCommand("OutlinePaint");
        this.selectOutlinePaintButton.addActionListener(this);
        interior.add(this.outlinePaint);
        interior.add(this.selectOutlinePaintButton);

        interior.add(new JLabel(localizationResources.getString("Background")));
        this.selectBackgroundPaintButton = new JButton(
            localizationResources.getString("Select...")
        );
        this.selectBackgroundPaintButton.setActionCommand("BackgroundPaint");
        this.selectBackgroundPaintButton.addActionListener(this);
        interior.add(this.backgroundPaint);
        interior.add(this.selectBackgroundPaintButton);

        interior.add(
            new JLabel(localizationResources.getString("Series_label_font"))
        );
        this.selectSeriesFontButton 
            = new JButton(localizationResources.getString("Select..."));
        this.selectSeriesFontButton.setActionCommand("SeriesFont");
        this.selectSeriesFontButton.addActionListener(this);
        this.fontDisplayField = new FontDisplayField(this.seriesFont);
        interior.add(this.fontDisplayField);
        interior.add(this.selectSeriesFontButton);

        interior.add(
            new JLabel(localizationResources.getString("Series_label_paint"))
        );
        this.selectSeriesPaintButton 
            = new JButton(localizationResources.getString("Select..."));
        this.selectSeriesPaintButton.setActionCommand("SeriesPaint");
        this.selectSeriesPaintButton.addActionListener(this);
        interior.add(this.seriesPaint);
        interior.add(this.selectSeriesPaintButton);

        this.enableOrDisableControls();

        general.add(interior);
        add(general, BorderLayout.NORTH);
    }

    /**
     * Returns the current outline stroke.
     *
     * @return The current outline stroke.
     */
    public Stroke getOutlineStroke() {
        return this.outlineStroke.getStroke();
    }

    /**
     * Returns the current outline paint.
     *
     * @return The current outline paint.
     */
    public Paint getOutlinePaint() {
        return this.outlinePaint.getPaint();
    }

    /**
     * Returns the current background paint.
     *
     * @return The current background paint.
     */
    public Paint getBackgroundPaint() {
        return this.backgroundPaint.getPaint();
    }

    /**
     * Returns the current series label font.
     *
     * @return The current series label font.
     */
    public Font getSeriesFont() {
        return this.seriesFont;
    }

    /**
     * Returns the current series label paint.
     *
     * @return The current series label paint.
     */
    public Paint getSeriesPaint() {
        return this.seriesPaint.getPaint();
    }

    /**
     * Handles user interactions with the panel.
     *
     * @param event  the event.
     */
    public void actionPerformed(ActionEvent event) {
        String command = event.getActionCommand();
        if (command.equals("OutlineStroke")) {
            attemptModifyOutlineStroke();
        }
        else if (command.equals("OutlinePaint")) {
            attemptModifyOutlinePaint();
        }
        else if (command.equals("BackgroundPaint")) {
            attemptModifyBackgroundPaint();
        }
        else if (command.equals("SeriesFont")) {
            attemptModifySeriesFont();
        }
        else if (command.equals("SeriesPaint")) {
            attemptModifySeriesPaint();
        }
        else if (command.equals("ShowLegend")) {
            attemptModifyShowLegend();
        }
    }

    /**
     * Allows the user the opportunity to change the outline stroke.
     */
    private void attemptModifyOutlineStroke() {

        StrokeChooserPanel panel = new StrokeChooserPanel(
            this.outlineStroke, this.availableStrokeSamples
        );
        int result = JOptionPane.showConfirmDialog(
            this, panel, 
            localizationResources.getString("Pen_Stroke_Selection"),
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE
        );

        if (result == JOptionPane.OK_OPTION) {
            this.outlineStroke.setStroke(panel.getSelectedStroke());
        }

    }

    /**
     * Allows the user the opportunity to change the outline paint.
     */
    private void attemptModifyOutlinePaint() {
        Color c;
        c = JColorChooser.showDialog(
            this, localizationResources.getString("Outline_Color"),
            Color.blue
        );
        if (c != null) {
            this.outlinePaint.setPaint(c);
        }
    }

    /**
     * Allows the user the opportunity to change the background paint.
     */
    private void attemptModifyBackgroundPaint() {
        Color c;
        c = JColorChooser.showDialog(
            this, localizationResources.getString("Background_Color"),
            Color.blue
        );
        if (c != null) {
            this.backgroundPaint.setPaint(c);
        }
    }

    /**
     * Allows the user the opportunity to change the series label font.
     */
    public void attemptModifySeriesFont() {

        FontChooserPanel panel = new FontChooserPanel(this.seriesFont);
        int result = JOptionPane.showConfirmDialog(
            this, panel, localizationResources.getString("Font_Selection"),
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE
        );

        if (result == JOptionPane.OK_OPTION) {
            this.seriesFont = panel.getSelectedFont();
            this.fontDisplayField.setText(
                this.seriesFont.getFontName() + ", " + this.seriesFont.getSize()
            );
        }

    }

    /**
     * Allows the user the opportunity to change the series label paint.
     */
    private void attemptModifySeriesPaint() {
        Color c;
        c = JColorChooser.showDialog(
            this, localizationResources.getString("Series_Label_Color"),
            Color.blue
        );
        if (c != null) {
            this.seriesPaint.setPaint(c);
        }
    }

    /**
     * Allow the user the opportunity to change whether the legend is
     * displayed on the chart or not.
     */
    private void attemptModifyShowLegend() {
        this.showLegend = this.showLegendCheckBox.isSelected();
        this.enableOrDisableControls();
    }

    /**
     * If we are supposed to show the legend, the controls are enabled.
     * If we are not supposed to show the legend, the controls are disabled.
     */
    private void enableOrDisableControls() {
        boolean enabled = (this.showLegend == true);
        this.selectOutlineStrokeButton.setEnabled(enabled);
        this.selectOutlinePaintButton.setEnabled(enabled);
        this.selectBackgroundPaintButton.setEnabled(enabled);
        this.selectSeriesFontButton.setEnabled(enabled);
        this.selectSeriesPaintButton.setEnabled(enabled);
    }

    /**
     * Sets the properties of the specified legend to match the properties
     * defined on this panel.
     *
     * @param chart    the chart whose legend is to be modified.
     */
    public void setLegendProperties(JFreeChart chart) {
        if (this.showLegend) {
            OldLegend legend = chart.getOldLegend();
            if (legend == null) {
                legend = new DefaultOldLegend();
                chart.setOldLegend(legend);
            }
            if (legend instanceof DefaultOldLegend) {
                // only supports StandardLegend at present
                DefaultOldLegend standard = (DefaultOldLegend) legend;
                standard.setOutlineStroke(getOutlineStroke());
                standard.setOutlinePaint(getOutlinePaint());
                standard.setBackgroundPaint(getBackgroundPaint());
                standard.setItemFont(getSeriesFont());
                standard.setItemPaint(getSeriesPaint());
            }
            else {
                // raise exception - unrecognised legend
            }
        }
        else {
            chart.setOldLegend(null);
        }
    }

}
