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
 * ------------
 * PiePlot.java
 * ------------
 * (C) Copyright 2000-2005, by Andrzej Porebski and Contributors.
 *
 * Original Author:  Andrzej Porebski;
 * Contributor(s):   David Gilbert (for Object Refinery Limited);
 *                   Martin Cordova (percentages in labels);
 *                   Richard Atkinson (URL support for image maps);
 *                   Christian W. Zuckschwerdt;
 *                   Arnaud Lelievre;
 *                   Andreas Schroeder (very minor);
 *
 * $Id$
 *
 * Changes (from 21-Jun-2001)
 * --------------------------
 * 21-Jun-2001 : Removed redundant JFreeChart parameter from constructors (DG);
 * 18-Sep-2001 : Updated header (DG);
 * 15-Oct-2001 : Data source classes moved to com.jrefinery.data.* (DG);
 * 19-Oct-2001 : Moved series paint and stroke methods from JFreeChart.java to 
 *               Plot.java (DG);
 * 22-Oct-2001 : Renamed DataSource.java --> Dataset.java etc. (DG);
 * 13-Nov-2001 : Modified plot subclasses so that null axes are possible for 
 *               pie plot (DG);
 * 17-Nov-2001 : Added PieDataset interface and amended this class accordingly,
 *               and completed removal of BlankAxis class as it is no longer 
 *               required (DG);
 * 19-Nov-2001 : Changed 'drawCircle' property to 'circular' property (DG);
 * 21-Nov-2001 : Added options for exploding pie sections and filled out range 
 *               of properties (DG);
 *               Added option for percentages in chart labels, based on code
 *               by Martin Cordova (DG);
 * 30-Nov-2001 : Changed default font from "Arial" --> "SansSerif" (DG);
 * 12-Dec-2001 : Removed unnecessary 'throws' clause in constructor (DG);
 * 13-Dec-2001 : Added tooltips (DG);
 * 16-Jan-2002 : Renamed tooltips class (DG);
 * 22-Jan-2002 : Fixed bug correlating legend labels with pie data (DG);
 * 05-Feb-2002 : Added alpha-transparency to plot class, and updated 
 *               constructors accordingly (DG);
 * 06-Feb-2002 : Added optional background image and alpha-transparency to Plot
 *               and subclasses.  Clipped drawing within plot area (DG);
 * 26-Mar-2002 : Added an empty zoom method (DG);
 * 18-Apr-2002 : PieDataset is no longer sorted (oldman);
 * 23-Apr-2002 : Moved dataset from JFreeChart to Plot.  Added 
 *               getLegendItemLabels() method (DG);
 * 19-Jun-2002 : Added attributes to control starting angle and direction 
 *               (default is now clockwise) (DG);
 * 25-Jun-2002 : Removed redundant imports (DG);
 * 02-Jul-2002 : Fixed sign of percentage bug introduced in 0.9.2 (DG);
 * 16-Jul-2002 : Added check for null dataset in getLegendItemLabels() (DG);
 * 30-Jul-2002 : Moved summation code to DatasetUtilities (DG);
 * 05-Aug-2002 : Added URL support for image maps - new member variable for
 *               urlGenerator, modified constructor and minor change to the 
 *               draw method (RA);
 * 18-Sep-2002 : Modified the percent label creation and added setters for the
 *               formatters (AS);
 * 24-Sep-2002 : Added getLegendItems() method (DG);
 * 02-Oct-2002 : Fixed errors reported by Checkstyle (DG);
 * 09-Oct-2002 : Added check for null entity collection (DG);
 * 30-Oct-2002 : Changed PieDataset interface (DG);
 * 18-Nov-2002 : Changed CategoryDataset to TableDataset (DG);
 * 02-Jan-2003 : Fixed "no data" message (DG);
 * 23-Jan-2003 : Modified to extract data from rows OR columns in 
 *               CategoryDataset (DG);
 * 14-Feb-2003 : Fixed label drawing so that foreground alpha does not apply 
 *               (bug id 685536) (DG);
 * 07-Mar-2003 : Modified to pass pieIndex on to PieSectionEntity and tooltip 
 *               and URL generators (DG);
 * 21-Mar-2003 : Added a minimum angle for drawing arcs 
 *               (see bug id 620031) (DG);
 * 24-Apr-2003 : Switched around PieDataset and KeyedValuesDataset (DG);
 * 02-Jun-2003 : Fixed bug 721733 (DG);
 * 30-Jul-2003 : Modified entity constructor (CZ);
 * 19-Aug-2003 : Implemented Cloneable (DG);
 * 29-Aug-2003 : Fixed bug 796936 (null pointer on setOutlinePaint()) (DG);
 * 08-Sep-2003 : Added internationalization via use of properties 
 *               resourceBundle (RFE 690236) (AL);
 * 16-Sep-2003 : Changed ChartRenderingInfo --> PlotRenderingInfo (DG);
 * 29-Oct-2003 : Added workaround for font alignment in PDF output (DG);
 * 05-Nov-2003 : Fixed missing legend bug (DG);
 * 10-Nov-2003 : Re-added the DatasetChangeListener to constructors (CZ);
 * 29-Jan-2004 : Fixed clipping bug in draw() method (DG);
 * 11-Mar-2004 : Major overhaul to improve labelling (DG);
 * 31-Mar-2004 : Made an adjustment for the plot area when the label generator 
 *               is null.  Fixed null pointer exception when the label 
 *               generator returns null for a label (DG);
 * 06-Apr-2004 : Added getter, setter, serialization and draw support for 
 *               labelBackgroundPaint (AS);
 * 08-Apr-2004 : Added flag to control whether null values are ignored or 
 *               not (DG);
 * 15-Apr-2004 : Fixed some minor warnings from Eclipse (DG);
 * 26-Apr-2004 : Added attributes for label outline and shadow (DG);
 * 04-Oct-2004 : Renamed ShapeUtils --> ShapeUtilities (DG);
 * 04-Nov-2004 : Fixed null pointer exception with new LegendTitle class (DG);
 * 09-Nov-2004 : Added user definable legend item shape (DG);
 * 25-Nov-2004 : Added new legend label generator (DG);
 * 20-Apr-2005 : Added a tool tip generator for legend labels (DG);
 * 26-Apr-2005 : Removed LOGGER (DG);
 * 05-May-2005 : Updated draw() method parameters (DG);
 * 10-May-2005 : Added flag to control visibility of label linking lines, plus
 *               another flag to control the handling of zero values (DG);
 * 
 */

package org.jfree.chart.plot;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Arc2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;

import org.jfree.chart.LegendItem;
import org.jfree.chart.LegendItemCollection;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.entity.PieSectionEntity;
import org.jfree.chart.event.PlotChangeEvent;
import org.jfree.chart.labels.PieSectionLabelGenerator;
import org.jfree.chart.labels.PieToolTipGenerator;
import org.jfree.chart.labels.StandardPieItemLabelGenerator;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.urls.PieURLGenerator;
import org.jfree.data.DefaultKeyedValues;
import org.jfree.data.KeyedValues;
import org.jfree.data.general.DatasetChangeEvent;
import org.jfree.data.general.DatasetUtilities;
import org.jfree.data.general.PieDataset;
import org.jfree.io.SerialUtilities;
import org.jfree.text.G2TextMeasurer;
import org.jfree.text.TextBlock;
import org.jfree.text.TextBox;
import org.jfree.text.TextUtilities;
import org.jfree.ui.RectangleAnchor;
import org.jfree.ui.RectangleInsets;
import org.jfree.util.ObjectList;
import org.jfree.util.ObjectUtilities;
import org.jfree.util.PaintList;
import org.jfree.util.Rotation;
import org.jfree.util.ShapeUtilities;
import org.jfree.util.StrokeList;

/**
 * A plot that displays data in the form of a pie chart, using data from any 
 * class that implements the {@link PieDataset} interface.
 * <P>
 * Special notes:
 * <ol>
 * <li>the default starting point is 12 o'clock and the pie sections proceed
 * in a clockwise direction, but these settings can be changed;</li>
 * <li>negative values in the dataset are ignored;</li>
 * <li>there are utility methods for creating a {@link PieDataset} from a
 * {@link org.jfree.data.category.CategoryDataset};</li>
 * </ol>
 *
 * @see Plot
 * @see PieDataset
 */
public class PiePlot extends Plot implements Cloneable, Serializable {
    
    /** For serialization. */
    private static final long serialVersionUID = -795612466005590431L;
    
    /** The default interior gap. */
    public static final double DEFAULT_INTERIOR_GAP = 0.25;

    /** The maximum interior gap (currently 40%). */
    public static final double MAX_INTERIOR_GAP = 0.40;

    /** The default starting angle for the pie chart. */
    public static final double DEFAULT_START_ANGLE = 90.0;

    /** The default section label font. */
    public static final Font DEFAULT_LABEL_FONT 
        = new Font("SansSerif", Font.PLAIN, 10);

    /** The default section label paint. */
    public static final Paint DEFAULT_LABEL_PAINT = Color.black;
    
    /** The default section label background paint. */
    public static final Paint DEFAULT_LABEL_BACKGROUND_PAINT 
        = new Color(255, 255, 192);

    /** The default section label outline paint. */
    public static final Paint DEFAULT_LABEL_OUTLINE_PAINT = Color.black;
    
    /** The default section label outline stroke. */
    public static final Stroke DEFAULT_LABEL_OUTLINE_STROKE 
        = new BasicStroke(0.5f);
    
    /** The default section label shadow paint. */
    public static final Paint DEFAULT_LABEL_SHADOW_PAINT = Color.lightGray;
    
    /** The default minimum arc angle to draw. */
    public static final double DEFAULT_MINIMUM_ARC_ANGLE_TO_DRAW = 0.00001;

    /** The dataset for the pie chart. */
    private PieDataset dataset;

    /** The pie index (used by the {@link MultiplePiePlot} class). */
    private int pieIndex;

    /** 
     * The amount of space left around the outside of the pie plot, expressed 
     * as a percentage. 
     */
    private double interiorGap;

    /** Flag determining whether to draw an ellipse or a perfect circle. */
    private boolean circular;

    /** The starting angle. */
    private double startAngle;

    /** The direction for the pie segments. */
    private Rotation direction;

    /** The paint for ALL sections (overrides list). */
    private transient Paint sectionPaint;

    /** The section paint list. */
    private PaintList sectionPaintList;

    /** The base section paint (fallback). */
    private transient Paint baseSectionPaint;

    /** The outline paint for ALL sections (overrides list). */
    private transient Paint sectionOutlinePaint;

    /** The section outline paint list. */
    private PaintList sectionOutlinePaintList;

    /** The base section outline paint (fallback). */
    private transient Paint baseSectionOutlinePaint;

    /** The outline stroke for ALL sections (overrides list). */
    private transient Stroke sectionOutlineStroke;

    /** The section outline stroke list. */
    private StrokeList sectionOutlineStrokeList;

    /** The base section outline stroke (fallback). */
    private transient Stroke baseSectionOutlineStroke;

    /** The shadow paint. */
    private transient Paint shadowPaint = Color.gray;

    /** The x-offset for the shadow effect. */
    private double shadowXOffset = 4.0f;
    
    /** The y-offset for the shadow effect. */
    private double shadowYOffset = 4.0f;
    
    /** The percentage amount to explode each pie section. */
    private ObjectList explodePercentages;
    
    /** The section label generator. */
    private PieSectionLabelGenerator labelGenerator;

    /** The font used to display the section labels. */
    private Font labelFont;

    /** The color used to draw the section labels. */
    private transient Paint labelPaint;
    
    /** The color used to draw the background of the section labels. */
    private transient Paint labelBackgroundPaint;

    /** 
     * The paint used to draw the outline of the section labels 
     * (<code>null</code> permitted). 
     */
    private transient Paint labelOutlinePaint;
    
    /** 
     * The stroke used to draw the outline of the section labels 
     * (<code>null</code> permitted). 
     */
    private transient Stroke labelOutlineStroke;
    
    /** 
     * The paint used to draw the shadow for the section labels 
     * (<code>null</code> permitted). 
     */
    private transient Paint labelShadowPaint;
    
    /** The maximum label width as a percentage of the plot width. */
    private double maximumLabelWidth = 0.20;
    
    /** 
     * The gap between the labels and the plot as a percentage of the plot 
     * width. 
     */
    private double labelGap = 0.05;

    /** A flag that controls whether or not the label links are drawn. */
    private boolean labelLinksVisible;
    
    /** The link margin. */
    private double labelLinkMargin = 0.05;
    
    /** The paint used for the label linking lines. */
    private transient Paint labelLinkPaint = Color.black;
    
    /** The stroke used for the label linking lines. */
    private transient Stroke labelLinkStroke = new BasicStroke(0.5f);
    
    /** The tooltip generator. */
    private PieToolTipGenerator toolTipGenerator;

    /** The URL generator. */
    private PieURLGenerator urlGenerator;
    
    /** The legend label generator. */
    private PieSectionLabelGenerator legendLabelGenerator;
    
    /** A tool tip generator for the legend. */
    private PieSectionLabelGenerator legendLabelToolTipGenerator;
    
    /** 
     * A flag that controls whether <code>null</code> values are ignored.  
     */
    private boolean ignoreNullValues;
    
    /**
     * A flag that controls whether zero values are ignored.
     */
    private boolean ignoreZeroValues;

    /** The legend item shape. */
    private transient Shape legendItemShape;
    
    /**
     * The smallest arc angle that will get drawn (this is to avoid a bug in 
     * various Java implementations that causes the JVM to crash).  See this 
     * link for details:
     *
     * http://www.jfree.org/phpBB2/viewtopic.php?t=2707
     *
     * ...and this bug report in the Java Bug Parade:
     *
     * http://developer.java.sun.com/developer/bugParade/bugs/4836495.html
     */
    private double minimumArcAngleToDraw;

    /** The resourceBundle for the localization. */
    protected static ResourceBundle localizationResources =
        ResourceBundle.getBundle("org.jfree.chart.plot.LocalizationBundle");

    /**
     * Creates a new plot.  The dataset is initially set to <code>null</code>.
     */
    public PiePlot() {
        this(null);
    }

    /**
     * Creates a plot that will draw a pie chart for the specified dataset.
     *
     * @param dataset  the dataset (<code>null</code> permitted).
     */
    public PiePlot(PieDataset dataset) {
        super();
        this.dataset = dataset;
        if (dataset != null) {
            dataset.addChangeListener(this);
        }
        this.pieIndex = 0;
        
        this.interiorGap = DEFAULT_INTERIOR_GAP;
        this.circular = true;
        this.startAngle = DEFAULT_START_ANGLE;
        this.direction = Rotation.CLOCKWISE;
        this.minimumArcAngleToDraw = DEFAULT_MINIMUM_ARC_ANGLE_TO_DRAW;
        
        this.sectionPaint = null;
        this.sectionPaintList = new PaintList();
        this.baseSectionPaint = null;

        this.sectionOutlinePaint = null;
        this.sectionOutlinePaintList = new PaintList();
        this.baseSectionOutlinePaint = DEFAULT_OUTLINE_PAINT;

        this.sectionOutlineStroke = null;
        this.sectionOutlineStrokeList = new StrokeList();
        this.baseSectionOutlineStroke = DEFAULT_OUTLINE_STROKE;
        
        this.explodePercentages = new ObjectList();

        this.labelGenerator = new StandardPieItemLabelGenerator();
        this.labelFont = DEFAULT_LABEL_FONT;
        this.labelPaint = DEFAULT_LABEL_PAINT;
        this.labelBackgroundPaint = DEFAULT_LABEL_BACKGROUND_PAINT;
        this.labelOutlinePaint = DEFAULT_LABEL_OUTLINE_PAINT;
        this.labelOutlineStroke = DEFAULT_LABEL_OUTLINE_STROKE;
        this.labelShadowPaint = DEFAULT_LABEL_SHADOW_PAINT;
        this.labelLinksVisible = true;
        
        this.toolTipGenerator = null;
        this.urlGenerator = null;
        this.legendLabelGenerator = new StandardPieSectionLabelGenerator();
        this.legendLabelToolTipGenerator = null;
        this.legendItemShape = Plot.DEFAULT_LEGEND_ITEM_CIRCLE;
        
        this.ignoreNullValues = false;
        this.ignoreZeroValues = false;
    }

    /**
     * Returns the dataset.
     *
     * @return The dataset (possibly <code>null</code>).
     */
    public PieDataset getDataset() {
        return this.dataset;
    }

    /**
     * Sets the dataset and sends a {@link DatasetChangeEvent} to 'this'.
     *
     * @param dataset  the dataset (<code>null</code> permitted).
     */
    public void setDataset(PieDataset dataset) {
        // if there is an existing dataset, remove the plot from the list of 
        // change listeners...
        PieDataset existing = this.dataset;
        if (existing != null) {
            existing.removeChangeListener(this);
        }

        // set the new dataset, and register the chart as a change listener...
        this.dataset = dataset;
        if (dataset != null) {
            setDatasetGroup(dataset.getGroup());
            dataset.addChangeListener(this);
        }

        // send a dataset change event to self...
        DatasetChangeEvent event = new DatasetChangeEvent(this, dataset);
        datasetChanged(event);
    }
    
    /**
     * Returns the pie index (this is used by the {@link MultiplePiePlot} class
     * to track subplots).
     * 
     * @return The pie index.
     */
    public int getPieIndex() {
        return this.pieIndex;
    }
    
    /**
     * Sets the pie index (this is used by the {@link MultiplePiePlot} class to 
     * track subplots).
     * 
     * @param index  the index.
     */
    public void setPieIndex(int index) {
        this.pieIndex = index;
    }
    
    /**
     * Returns the start angle for the first pie section.  This is measured in 
     * degrees starting from 3 o'clock and measuring anti-clockwise.
     *
     * @return The start angle.
     */
    public double getStartAngle() {
        return this.startAngle;
    }

    /**
     * Sets the starting angle and sends a {@link PlotChangeEvent} to all 
     * registered listeners.  The initial default value is 90 degrees, which 
     * corresponds to 12 o'clock.  A value of zero corresponds to 3 o'clock...
     * this is the encoding used by Java's Arc2D class.
     *
     * @param angle  the angle (in degrees).
     */
    public void setStartAngle(double angle) {
        this.startAngle = angle;
        notifyListeners(new PlotChangeEvent(this));
    }

    /**
     * Returns the direction in which the pie sections are drawn (clockwise or 
     * anti-clockwise).
     *
     * @return The direction (never <code>null</code>).
     */
    public Rotation getDirection() {
        return this.direction;
    }

    /**
     * Sets the direction in which the pie sections are drawn and sends a 
     * {@link PlotChangeEvent} to all registered listeners.
     *
     * @param direction  the direction (<code>null</code> not permitted).
     */
    public void setDirection(Rotation direction) {
        if (direction == null) {
            throw new IllegalArgumentException("Null 'direction' argument.");
        }
        this.direction = direction;
        notifyListeners(new PlotChangeEvent(this));

    }

    /**
     * Returns the interior gap, measured as a percentage of the available 
     * drawing space.
     *
     * @return The gap (as a percentage of the available drawing space).
     */
    public double getInteriorGap() {
        return this.interiorGap;
    }

    /**
     * Sets the interior gap and sends a {@link PlotChangeEvent} to all 
     * registered listeners.  This controls the space between the edges of the 
     * pie plot and the plot area itself (the region where the section labels 
     * appear).
     *
     * @param percent  the gap (as a percentage of the available drawing space).
     */
    public void setInteriorGap(double percent) {

        // check arguments...
        if ((percent < 0.0) || (percent > MAX_INTERIOR_GAP)) {
            throw new IllegalArgumentException(
                "Invalid 'percent' (" + percent + ") argument.");
        }

        // make the change...
        if (this.interiorGap != percent) {
            this.interiorGap = percent;
            notifyListeners(new PlotChangeEvent(this));
        }

    }

    /**
     * Returns a flag indicating whether the pie chart is circular, or
     * stretched into an elliptical shape.
     *
     * @return A flag indicating whether the pie chart is circular.
     */
    public boolean isCircular() {
        return this.circular;
    }

    /**
     * A flag indicating whether the pie chart is circular, or stretched into
     * an elliptical shape.
     *
     * @param flag  the new value.
     */
    public void setCircular(boolean flag) {
        setCircular(flag, true);
    }

    /**
     * Sets the circular attribute and, if requested, sends a 
     * {@link PlotChangeEvent} to all registered listeners.
     *
     * @param circular  the new value of the flag.
     * @param notify  notify listeners?
     */
    public void setCircular(boolean circular, boolean notify) {
        this.circular = circular;
        if (notify) {
            notifyListeners(new PlotChangeEvent(this));   
        }
    }

    /**
     * Returns the flag that controls whether <code>null</code> values in the 
     * dataset are ignored.  
     * 
     * @return A boolean.
     */
    public boolean getIgnoreNullValues() {
        return this.ignoreNullValues;   
    }
    
    /**
     * Sets a flag that controls whether <code>null</code> values are ignored, 
     * and sends a {@link PlotChangeEvent} to all registered listeners.  At 
     * present, this only affects whether or not the key is presented in the 
     * legend.
     * 
     * @param flag  the flag.
     */
    public void setIgnoreNullValues(boolean flag) {
        this.ignoreNullValues = flag;
        notifyListeners(new PlotChangeEvent(this));
    }
    
    /**
     * Returns the flag that controls whether zero values in the 
     * dataset are ignored.  
     * 
     * @return A boolean.
     */
    public boolean getIgnoreZeroValues() {
        return this.ignoreZeroValues;   
    }
    
    /**
     * Sets a flag that controls whether zero values are ignored, 
     * and sends a {@link PlotChangeEvent} to all registered listeners.  This 
     * only affects whether or not a label appears for the non-visible
     * pie section.
     * 
     * @param flag  the flag.
     */
    public void setIgnoreZeroValues(boolean flag) {
        this.ignoreZeroValues = flag;
        notifyListeners(new PlotChangeEvent(this));
    }
    
    //// SECTION PAINT ////////////////////////////////////////////////////////

    /**
     * Returns the paint for ALL sections in the plot.
     *
     * @return The paint (possibly <code>null</code>).
     */
    public Paint getSectionPaint() {
        return this.sectionPaint;
    }

    /**
     * Sets the paint for ALL sections in the plot.  If this is set to
     * </code>null</code>, then a list of paints is used instead (to allow
     * different colors to be used for each section).
     *
     * @param paint  the paint (<code>null</code> permitted).
     */
    public void setSectionPaint(Paint paint) {
        this.sectionPaint = paint;
        notifyListeners(new PlotChangeEvent(this));
    }

    /**
     * Returns the paint for the specified section.
     * 
     * @param section  the section index (zero-based).
     * 
     * @return The paint (never <code>null</code>).
     */
    public Paint getSectionPaint(int section) {
        
        // return the override, if there is one...
        if (this.sectionPaint != null) {
            return this.sectionPaint;
        }

        // otherwise look up the paint list
        Paint result = this.sectionPaintList.getPaint(section);
        if (result == null) {
            DrawingSupplier supplier = getDrawingSupplier();
            if (supplier != null) {
                Paint p = supplier.getNextPaint();
                this.sectionPaintList.setPaint(section, p);
                result = p;
            }
            else {
                result = this.baseSectionPaint;
            }
        }
        return result;
       
    }
    
    /**
     * Sets the paint used to fill a section of the pie and sends a 
     * {@link PlotChangeEvent} to all registered listeners.
     *
     * @param section  the section index (zero-based).
     * @param paint  the paint (<code>null</code> permitted).
     */
    public void setSectionPaint(int section, Paint paint) {
        this.sectionPaintList.setPaint(section, paint);
        notifyListeners(new PlotChangeEvent(this));
    }
    
    /**
     * Returns the base section paint.  This is used when no other paint is 
     * available.
     * 
     * @return The paint (never <code>null</code>).
     */
    public Paint getBaseSectionPaint() {
        return this.baseSectionPaint;   
    }
    
    /**
     * Sets the base section paint.
     * 
     * @param paint  the paint (<code>null</code> not permitted).
     */
    public void setBaseSectionPaint(Paint paint) {
        if (paint == null) {
            throw new IllegalArgumentException("Null 'paint' argument.");   
        }
        this.baseSectionPaint = paint;
        notifyListeners(new PlotChangeEvent(this));
    }
    
    //// SECTION OUTLINE PAINT ////////////////////////////////////////////////

    /**
     * Returns the outline paint for ALL sections in the plot.
     *
     * @return The paint (possibly <code>null</code>).
     */
    public Paint getSectionOutlinePaint() {
        return this.sectionOutlinePaint;
    }

    /**
     * Sets the outline paint for ALL sections in the plot.  If this is set to
     * </code>null</code>, then a list of paints is used instead (to allow
     * different colors to be used for each section).
     *
     * @param paint  the paint (<code>null</code> permitted).
     */
    public void setSectionOutlinePaint(Paint paint) {
        this.sectionOutlinePaint = paint;
        notifyListeners(new PlotChangeEvent(this));
    }

    /**
     * Returns the paint for the specified section.
     * 
     * @param section  the section index (zero-based).
     * 
     * @return The paint (never <code>null</code>).
     */
    public Paint getSectionOutlinePaint(int section) {
        
        // return the override, if there is one...
        if (this.sectionOutlinePaint != null) {
            return this.sectionOutlinePaint;
        }

        // otherwise look up the paint list
        Paint result = this.sectionOutlinePaintList.getPaint(section);
        if (result == null) {
            result = this.baseSectionOutlinePaint;
        }
        return result;
       
    }
    
    /**
     * Sets the paint used to fill a section of the pie and sends a 
     * {@link PlotChangeEvent} to all registered listeners.
     *
     * @param section  the section index (zero-based).
     * @param paint  the paint (<code>null</code> permitted).
     */
    public void setSectionOutlinePaint(int section, Paint paint) {
        this.sectionOutlinePaintList.setPaint(section, paint);
        notifyListeners(new PlotChangeEvent(this));
    }
    
    /**
     * Returns the base section paint.  This is used when no other paint is 
     * available.
     * 
     * @return The paint (never <code>null</code>).
     */
    public Paint getBaseSectionOutlinePaint() {
        return this.baseSectionOutlinePaint;   
    }
    
    /**
     * Sets the base section paint.
     * 
     * @param paint  the paint (<code>null</code> not permitted).
     */
    public void setBaseSectionOutlinePaint(Paint paint) {
        if (paint == null) {
            throw new IllegalArgumentException("Null 'paint' argument.");   
        }
        this.baseSectionOutlinePaint = paint;
        notifyListeners(new PlotChangeEvent(this));
    }
    
    //// SECTION OUTLINE STROKE ///////////////////////////////////////////////

    /**
     * Returns the outline stroke for ALL sections in the plot.
     *
     * @return The stroke (possibly <code>null</code>).
     */
    public Stroke getSectionOutlineStroke() {
        return this.sectionOutlineStroke;
    }

    /**
     * Sets the outline stroke for ALL sections in the plot.  If this is set to
     * </code>null</code>, then a list of paints is used instead (to allow
     * different colors to be used for each section).
     *
     * @param stroke  the stroke (<code>null</code> permitted).
     */
    public void setSectionOutlineStroke(Stroke stroke) {
        this.sectionOutlineStroke = stroke;
        notifyListeners(new PlotChangeEvent(this));
    }

    /**
     * Returns the stroke for the specified section.
     * 
     * @param section  the section index (zero-based).
     * 
     * @return The stroke (never <code>null</code>).
     */
    public Stroke getSectionOutlineStroke(int section) {
        
        // return the override, if there is one...
        if (this.sectionOutlineStroke != null) {
            return this.sectionOutlineStroke;
        }

        // otherwise look up the paint list
        Stroke result = this.sectionOutlineStrokeList.getStroke(section);
        if (result == null) {
            result = this.baseSectionOutlineStroke;
        }
        return result;
       
    }
    
    /**
     * Sets the stroke used to fill a section of the pie and sends a 
     * {@link PlotChangeEvent} to all registered listeners.
     *
     * @param section  the section index (zero-based).
     * @param stroke  the stroke (<code>null</code> permitted).
     */
    public void setSectionOutlineStroke(int section, Stroke stroke) {
        this.sectionOutlineStrokeList.setStroke(section, stroke);
        notifyListeners(new PlotChangeEvent(this));
    }
    
    /**
     * Returns the base section stroke.  This is used when no other stroke is 
     * available.
     * 
     * @return The stroke (never <code>null</code>).
     */
    public Stroke getBaseSectionOutlineStroke() {
        return this.baseSectionOutlineStroke;   
    }
    
    /**
     * Sets the base section stroke.
     * 
     * @param stroke  the stroke (<code>null</code> not permitted).
     */
    public void setBaseSectionOutlineStroke(Stroke stroke) {
        if (stroke == null) {
            throw new IllegalArgumentException("Null 'stroke' argument.");   
        }
        this.baseSectionOutlineStroke = stroke;
        notifyListeners(new PlotChangeEvent(this));
    }

    /**
     * Returns the shadow paint.
     * 
     * @return The paint (possibly <code>null</code>).
     */
    public Paint getShadowPaint() {
        return this.shadowPaint;   
    }
    
    /**
     * Sets the shadow paint and sends a {@link PlotChangeEvent} to all 
     * registered listeners.
     * 
     * @param paint  the paint (<code>null</code> permitted).
     */
    public void setShadowPaint(Paint paint) {
        this.shadowPaint = paint;
        notifyListeners(new PlotChangeEvent(this));
    }
    
    /**
     * Returns the x-offset for the shadow effect.
     * 
     * @return The offset (in Java2D units).
     */
    public double getShadowXOffset() {
        return this.shadowXOffset;
    }
    
    /**
     * Sets the x-offset for the shadow effect and sends a 
     * {@link PlotChangeEvent} to all registered listeners.
     * 
     * @param offset  the offset (in Java2D units).
     */
    public void setShadowXOffset(double offset) {
        this.shadowXOffset = offset;   
        notifyListeners(new PlotChangeEvent(this));
    }
    
    /**
     * Returns the y-offset for the shadow effect.
     * 
     * @return The offset (in Java2D units).
     */
    public double getShadowYOffset() {
        return this.shadowYOffset;
    }
    
    /**
     * Sets the y-offset for the shadow effect and sends a 
     * {@link PlotChangeEvent} to all registered listeners.
     * 
     * @param offset  the offset (in Java2D units).
     */
    public void setShadowYOffset(double offset) {
        this.shadowYOffset = offset;   
        notifyListeners(new PlotChangeEvent(this));
    }
    
    /**
     * Returns the amount that a section should be 'exploded'.
     *
     * @param section  the section number.
     *
     * @return The amount that a section should be 'exploded'.
     */
    public double getExplodePercent(int section) {
        double result = 0.0;
        if (this.explodePercentages != null) {
            Number percent = (Number) this.explodePercentages.get(section);
            if (percent != null) {
                result = percent.doubleValue();
            }
        }
        return result;
    }

    /**
     * Sets the amount that a pie section should be exploded and sends a 
     * {@link PlotChangeEvent} to all registered listeners.
     *
     * @param section  the section index.
     * @param percent  the explode percentage (0.30 = 30 percent).
     */
    public void setExplodePercent(int section, double percent) {
        if (this.explodePercentages == null) {
            this.explodePercentages = new ObjectList();
        }
        this.explodePercentages.set(section, new Double(percent));
        notifyListeners(new PlotChangeEvent(this));
    }
    
    /**
     * Returns the maximum explode percent.
     * 
     * @return The percent.
     */
    public double getMaximumExplodePercent() {
        double result = 0.0;
        for (int i = 0; i < this.explodePercentages.size(); i++) {
            Number explode = (Number) this.explodePercentages.get(i);
            if (explode != null) {
                result = Math.max(result, explode.doubleValue());   
            }
        }
        return result;
    }
    
    /**
     * Returns the section label generator. 
     * 
     * @return The generator (possibly <code>null</code>).
     */
    public PieSectionLabelGenerator getLabelGenerator() {
        return this.labelGenerator;   
    }
    
    /**
     * Sets the section label generator and sends a {@link PlotChangeEvent} to
     * all registered listeners.
     * 
     * @param generator  the generator (<code>null</code> permitted).
     */
    public void setLabelGenerator(PieSectionLabelGenerator generator) {
        this.labelGenerator = generator;
        notifyListeners(new PlotChangeEvent(this));
    }
    
    /**
     * Returns the gap between the edge of the pie and the labels, expressed as 
     * a percentage of the plot width.
     * 
     * @return The gap (a percentage, where 0.05 = five percent).
     */
    public double getLabelGap() {
        return this.labelGap;   
    }
    
    /**
     * Sets the gap between the edge of the pie and the labels (expressed as a 
     * percentage of the plot width) and sends a {@link PlotChangeEvent} to all
     * registered listeners.
     * 
     * @param gap  the gap (a percentage, where 0.05 = five percent).
     */
    public void setLabelGap(double gap) {
        this.labelGap = gap;   
        notifyListeners(new PlotChangeEvent(this));
    }
    
    /**
     * Returns the maximum label width as a percentage of the plot width.
     * 
     * @return The width (a percentage, where 0.20 = 20 percent).
     */
    public double getMaximumLabelWidth() {
        return this.maximumLabelWidth;   
    }
    
    /**
     * Sets the maximum label width as a percentage of the plot width and sends
     * a {@link PlotChangeEvent} to all registered listeners.
     * 
     * @param width  the width (a percentage, where 0.20 = 20 percent).
     */
    public void setMaximumLabelWidth(double width) {
        this.maximumLabelWidth = width;
        notifyListeners(new PlotChangeEvent(this));
    }
    
    /**
     * Returns the flag that controls whether or not label linking lines are
     * visible.
     * 
     * @return A boolean.
     */
    public boolean getLabelLinksVisible() {
        return this.labelLinksVisible;
    }
    
    /**
     * Sets the flag that controls whether or not label linking lines are 
     * visible and sends a {@link PlotChangeEvent} to all registered listeners.
     * Please take care when hiding the linking lines - depending on the data 
     * values, the labels can be displayed some distance away from the
     * corresponding pie section.
     * 
     * @param visible  the flag.
     */
    public void setLabelLinksVisible(boolean visible) {
        this.labelLinksVisible = visible;
        notifyListeners(new PlotChangeEvent(this));
    }
    
    /**
     * Returns the margin (expressed as a percentage of the width or height) 
     * between the edge of the pie and the link point.
     * 
     * @return The link margin (as a percentage, where 0.05 is five percent).
     */
    public double getLabelLinkMargin() {
        return this.labelLinkMargin;   
    }
    
    /**
     * Sets the link margin and sends a {@link PlotChangeEvent} to all 
     * registered listeners.
     * 
     * @param margin  the margin.
     */
    public void setLabelLinkMargin(double margin) {
        this.labelLinkMargin = margin;
        notifyListeners(new PlotChangeEvent(this));
    }
    
    /**
     * Returns the paint used for the lines that connect pie sections to their 
     * corresponding labels.
     * 
     * @return The paint (never <code>null</code>).
     */
    public Paint getLabelLinkPaint() {
        return this.labelLinkPaint;   
    }
    
    /**
     * Sets the paint used for the lines that connect pie sections to their 
     * corresponding labels, and sends a {@link PlotChangeEvent} to all 
     * registered listeners.
     * 
     * @param paint  the paint (<code>null</code> not permitted).
     */
    public void setLabelLinkPaint(Paint paint) {
        if (paint == null) {
            throw new IllegalArgumentException("Null 'paint' argument.");
        }
        this.labelLinkPaint = paint;
        notifyListeners(new PlotChangeEvent(this));
    }
    
    /**
     * Returns the stroke used for the label linking lines.
     * 
     * @return The stroke.
     */
    public Stroke getLabelLinkStroke() {
        return this.labelLinkStroke;   
    }
    
    /**
     * Sets the link stroke and sends a {@link PlotChangeEvent} to all 
     * registered listeners.
     * 
     * @param stroke  the stroke.
     */
    public void setLabelLinkStroke(Stroke stroke) {
        if (stroke == null) {
            throw new IllegalArgumentException("Null 'stroke' argument.");
        }
        this.labelLinkStroke = stroke;
        notifyListeners(new PlotChangeEvent(this));
    }
    
    /**
     * Returns the section label font.
     *
     * @return The font (never <code>null</code>).
     */
    public Font getLabelFont() {
        return this.labelFont;
    }

    /**
     * Sets the section label font and sends a {@link PlotChangeEvent} to all 
     * registered listeners.
     *
     * @param font  the font (<code>null</code> not permitted).
     */
    public void setLabelFont(Font font) {
        if (font == null) {
            throw new IllegalArgumentException("Null 'font' argument.");
        }
        this.labelFont = font;
        notifyListeners(new PlotChangeEvent(this));
    }

    /**
     * Returns the section label paint.
     *
     * @return The paint (never <code>null</code>).
     */
    public Paint getLabelPaint() {
        return this.labelPaint;
    }

    /**
     * Sets the section label paint and sends a {@link PlotChangeEvent} to all 
     * registered listeners.
     *
     * @param paint  the paint (<code>null</code> not permitted).
     */
    public void setLabelPaint(Paint paint) {
        if (paint == null) {
            throw new IllegalArgumentException("Null 'paint' argument.");
        }
        this.labelPaint = paint;
        notifyListeners(new PlotChangeEvent(this));
    }

    /**
     * Returns the section label background paint.
     *
     * @return The paint (possibly <code>null</code>).
     */
    public Paint getLabelBackgroundPaint() {
        return this.labelBackgroundPaint;
    }

    /**
     * Sets the section label background paint and sends a 
     * {@link PlotChangeEvent} to all registered listeners.
     *
     * @param paint  the paint (<code>null</code> permitted).
     */
    public void setLabelBackgroundPaint(Paint paint) {
        this.labelBackgroundPaint = paint;
        notifyListeners(new PlotChangeEvent(this));
    }

    /**
     * Returns the section label outline paint.
     *
     * @return The paint (possibly <code>null</code>).
     */
    public Paint getLabelOutlinePaint() {
        return this.labelOutlinePaint;
    }

    /**
     * Sets the section label outline paint and sends a 
     * {@link PlotChangeEvent} to all registered listeners.
     *
     * @param paint  the paint (<code>null</code> permitted).
     */
    public void setLabelOutlinePaint(Paint paint) {
        this.labelOutlinePaint = paint;
        notifyListeners(new PlotChangeEvent(this));
    }

    /**
     * Returns the section label outline stroke.
     *
     * @return The stroke (possibly <code>null</code>).
     */
    public Stroke getLabelOutlineStroke() {
        return this.labelOutlineStroke;
    }

    /**
     * Sets the section label outline stroke and sends a 
     * {@link PlotChangeEvent} to all registered listeners.
     *
     * @param stroke  the stroke (<code>null</code> permitted).
     */
    public void setLabelOutlineStroke(Stroke stroke) {
        this.labelOutlineStroke = stroke;
        notifyListeners(new PlotChangeEvent(this));
    }

    /**
     * Returns the section label shadow paint.
     *
     * @return The paint (possibly <code>null</code>).
     */
    public Paint getLabelShadowPaint() {
        return this.labelShadowPaint;
    }

    /**
     * Sets the section label shadow paint and sends a {@link PlotChangeEvent}
     * to all registered listeners.
     *
     * @param paint  the paint (<code>null</code> permitted).
     */
    public void setLabelShadowPaint(Paint paint) {
        this.labelShadowPaint = paint;
        notifyListeners(new PlotChangeEvent(this));
    }

    /**
     * Returns the tool tip generator, an object that is responsible for 
     * generating the text items used for tool tips by the plot.  If the 
     * generator is <code>null</code>, no tool tips will be created.
     *
     * @return The generator (possibly <code>null</code>).
     */
    public PieToolTipGenerator getToolTipGenerator() {
        return this.toolTipGenerator;
    }

    /**
     * Sets the tool tip generator and sends a {@link PlotChangeEvent} to all 
     * registered listeners.  Set the generator to <code>null</code> if you 
     * don't want any tool tips.
     *
     * @param generator  the generator (<code>null</code> permitted).
     */
    public void setToolTipGenerator(PieToolTipGenerator generator) {
        this.toolTipGenerator = generator;
        notifyListeners(new PlotChangeEvent(this));
    }

    /**
     * Returns the URL generator.
     *
     * @return The generator (possibly <code>null</code>).
     */
    public PieURLGenerator getURLGenerator() {
        return this.urlGenerator;
    }

    /**
     * Sets the URL generator and sends a {@link PlotChangeEvent} to all 
     * registered listeners.
     *
     * @param generator  the generator (<code>null</code> permitted).
     */
    public void setURLGenerator(PieURLGenerator generator) {
        this.urlGenerator = generator;
        notifyListeners(new PlotChangeEvent(this));
    }

    /**
     * Returns the minimum arc angle that will be drawn.  Pie sections for an 
     * angle smaller than this are not drawn, to avoid a JDK bug.
     *
     * @return The minimum angle.
     */
    public double getMinimumArcAngleToDraw() {
        return this.minimumArcAngleToDraw;
    }

    /**
     * Sets the minimum arc angle that will be drawn.  Pie sections for an 
     * angle smaller than this are not drawn, to avoid a JDK bug.  See this 
     * link for details:
     * <br><br>
     * <a href="http://www.jfree.org/phpBB2/viewtopic.php?t=2707">
     * http://www.jfree.org/phpBB2/viewtopic.php?t=2707</a>
     * <br><br>
     * ...and this bug report in the Java Bug Parade:
     * <br><br>
     * <a href=
     * "http://developer.java.sun.com/developer/bugParade/bugs/4836495.html">
     * http://developer.java.sun.com/developer/bugParade/bugs/4836495.html</a>
     *
     * @param angle  the minimum angle.
     */
    public void setMinimumArcAngleToDraw(double angle) {
        this.minimumArcAngleToDraw = angle;
    }
    
    /**
     * Returns the shape used for legend items.
     * 
     * @return The shape.
     */
    public Shape getLegendItemShape() {
        return this.legendItemShape;
    }

    /**
     * Sets the shape used for legend items.
     * 
     * @param shape  the shape (<code>null</code> not permitted).
     */
    public void setLegendItemShape(Shape shape) {
        if (shape == null) {
            throw new IllegalArgumentException("Null 'shape' argument.");
        }
        this.legendItemShape = shape;
        notifyListeners(new PlotChangeEvent(this));
    }
    
    /**
     * Returns the legend label tool tip generator.
     * 
     * @return The legend label tool tip generator (possibly <code>null</code>).
     */
    public PieSectionLabelGenerator getLegendLabelToolTipGenerator() {
        return this.legendLabelToolTipGenerator;
    }
    
    /**
     * Sets the legend label tool tip generator and sends a 
     * {@link PlotChangeEvent} to all registered listeners.
     * 
     * @param generator  the generator (<code>null</code> permitted).
     */
    public void setLegendLabelToolTipGenerator(
            PieSectionLabelGenerator generator) {
        this.legendLabelToolTipGenerator = generator;
        notifyListeners(new PlotChangeEvent(this));
    }
    
    /**
     * Returns the legend label generator.
     * 
     * @return The legend label generator (never <code>null</code>).
     */
    public PieSectionLabelGenerator getLegendLabelGenerator() {
        return this.legendLabelGenerator;
    }
    
    /**
     * Sets the legend label generator and sends a {@link PlotChangeEvent} to 
     * all registered listeners.
     * 
     * @param generator  the generator (<code>null</code> not permitted).
     */
    public void setLegendLabelGenerator(PieSectionLabelGenerator generator) {
        if (generator == null) {
            throw new IllegalArgumentException("Null 'generator' argument.");
        }
        this.legendLabelGenerator = generator;
        notifyListeners(new PlotChangeEvent(this));
    }
    
    /**
     * Initialises the drawing procedure.  This method will be called before 
     * the first item is rendered, giving the plot an opportunity to initialise
     * any state information it wants to maintain.
     *
     * @param g2  the graphics device.
     * @param plotArea  the plot area (<code>null</code> not permitted).
     * @param plot  the plot.
     * @param index  the secondary index (<code>null</code> for primary 
     *               renderer).
     * @param info  collects chart rendering information for return to caller.
     * 
     * @return A state object (maintains state information relevant to one 
     *         chart drawing).
     */
    public PiePlotState initialise(Graphics2D g2,
                                   Rectangle2D plotArea,
                                   PiePlot plot,
                                   Integer index,
                                   PlotRenderingInfo info) {
     
        PiePlotState state = new PiePlotState(info);
        state.setPassesRequired(2);
        state.setTotal(
            DatasetUtilities.calculatePieDatasetTotal(plot.getDataset())
        );
        state.setLatestAngle(plot.getStartAngle());
        return state;
        
    }
    
    /**
     * Draws the plot on a Java 2D graphics device (such as the screen or a 
     * printer).
     *
     * @param g2  the graphics device.
     * @param area  the area within which the plot should be drawn.
     * @param anchor  the anchor point (<code>null</code> permitted).
     * @param parentState  the state from the parent plot, if there is one.
     * @param info  collects info about the drawing 
     *              (<code>null</code> permitted).
     */
    public void draw(Graphics2D g2, Rectangle2D area, Point2D anchor,
                     PlotState parentState,
                     PlotRenderingInfo info) {

        // adjust for insets...
        RectangleInsets insets = getInsets();
        insets.trim(area);

        if (info != null) {
            info.setPlotArea(area);
            info.setDataArea(area);
        }

        drawBackground(g2, area);
        drawOutline(g2, area);

        Shape savedClip = g2.getClip();
        g2.clip(area);

        Composite originalComposite = g2.getComposite();
        g2.setComposite(
            AlphaComposite.getInstance(
                AlphaComposite.SRC_OVER, getForegroundAlpha()
            )
        );

        if (!DatasetUtilities.isEmptyOrNull(this.dataset)) {
            drawPie(g2, area, info);
        }
        else {
            drawNoDataMessage(g2, area);
        }

        g2.setClip(savedClip);
        g2.setComposite(originalComposite);

        drawOutline(g2, area);

    }

    /**
     * Draws the pie.
     *
     * @param g2  the graphics device.
     * @param plotArea  the plot area.
     * @param info  chart rendering info.
     */
    protected void drawPie(Graphics2D g2, 
                           Rectangle2D plotArea, 
                           PlotRenderingInfo info) {

        PiePlotState state = initialise(g2, plotArea, this, null, info);

        // adjust the plot area for interior spacing and labels...
        double labelWidth = 0.0;
        if (this.labelGenerator != null) {
            labelWidth = this.labelGap + this.maximumLabelWidth 
                         + this.labelLinkMargin;    
        }
        double gapHorizontal 
            = plotArea.getWidth() * (this.interiorGap + labelWidth);
        double gapVertical = plotArea.getHeight() * this.interiorGap;

        double linkX = plotArea.getX() + gapHorizontal / 2;
        double linkY = plotArea.getY() + gapVertical / 2;
        double linkW = plotArea.getWidth() - gapHorizontal;
        double linkH = plotArea.getHeight() - gapVertical;
        
        // make the link area a square if the pie chart is to be circular...
        if (this.circular) {
            double min = Math.min(linkW, linkH) / 2;
            linkX = (linkX + linkX + linkW) / 2 - min;
            linkY = (linkY + linkY + linkH) / 2 - min;
            linkW = 2 * min;
            linkH = 2 * min;
        }

        // the link area defines the dog leg points for the linking lines to 
        // the labels
        Rectangle2D linkArea = new Rectangle2D.Double(
            linkX, linkY, linkW, linkH
        );
        state.setLinkArea(linkArea);
        
        // the explode area defines the max circle/ellipse for the exploded 
        // pie sections.  it is defined by shrinking the linkArea by the 
        // linkMargin factor.
        double hh = linkArea.getWidth() * this.labelLinkMargin;
        double vv = linkArea.getHeight() * this.labelLinkMargin;
        Rectangle2D explodeArea = new Rectangle2D.Double(
            linkX + hh / 2.0, linkY + vv / 2.0, linkW - hh, linkH - vv
        );
       
        state.setExplodedPieArea(explodeArea);
        
        // the pie area defines the circle/ellipse for regular pie sections.
        // it is defined by shrinking the explodeArea by the explodeMargin 
        // factor. 
        double maximumExplodePercent = getMaximumExplodePercent();
        double percent = maximumExplodePercent / (1.0 + maximumExplodePercent);
        
        double h1 = explodeArea.getWidth() * percent;
        double v1 = explodeArea.getHeight() * percent;
        Rectangle2D pieArea = new Rectangle2D.Double(
            explodeArea.getX() + h1 / 2.0, explodeArea.getY() + v1 / 2.0,
            explodeArea.getWidth() - h1, explodeArea.getHeight() - v1
        );

        state.setPieArea(pieArea);
        state.setPieCenterX(pieArea.getCenterX());
        state.setPieCenterY(pieArea.getCenterY());
        state.setPieWRadius(pieArea.getWidth() / 2.0);
        state.setPieHRadius(pieArea.getHeight() / 2.0);
        // plot the data (unless the dataset is null)...
        if ((this.dataset != null) && (this.dataset.getKeys().size() > 0)) {

            List keys = this.dataset.getKeys();
            double totalValue 
                = DatasetUtilities.calculatePieDatasetTotal(this.dataset);

            int passesRequired = state.getPassesRequired();
            for (int pass = 0; pass < passesRequired; pass++) {
                double runningTotal = 0.0;
                for (int section = 0; section < keys.size(); section++) {
                    Number n = this.dataset.getValue(section);
                    if (n != null) {
                        double value = n.doubleValue();
                        if (value > 0.0) {
                            runningTotal += value;
                            drawItem(g2, section, explodeArea, state, pass);
                        }
                    } 
                }
            }
            
            drawLabels(g2, keys, totalValue, plotArea, linkArea, state);

        }
        else {
            drawNoDataMessage(g2, plotArea);
        }
    }
    
    /**
     * Draws a single data item.
     *
     * @param g2  the graphics device (<code>null</code> not permitted).
     * @param section  the section index.
     * @param dataArea  the data plot area.
     * @param state  state information for one chart.
     * @param currentPass  the current pass index.
     */
    protected void drawItem(Graphics2D g2,
                            int section,
                            Rectangle2D dataArea,
                            PiePlotState state,
                            int currentPass) {
    
        Number n = this.dataset.getValue(section);
        if (n == null) {
            return;   
        }
        double value = n.doubleValue();
        double angle1 = 0.0;
        double angle2 = 0.0;
        
        if (this.direction == Rotation.CLOCKWISE) {
            angle1 = state.getLatestAngle();
            angle2 = angle1 - value / state.getTotal() * 360.0;
        }
        else if (this.direction == Rotation.ANTICLOCKWISE) {
            angle1 = state.getLatestAngle();
            angle2 = angle1 + value / state.getTotal() * 360.0;         
        }
        else {
            throw new IllegalStateException("Rotation type not recognised.");   
        }
        
        double angle = (angle2 - angle1);
        if (Math.abs(angle) > getMinimumArcAngleToDraw()) {
            double ep = 0.0;
            double mep = getMaximumExplodePercent();
            if (mep > 0.0) {
                ep = getExplodePercent(section) / mep;                
            }
            Rectangle2D arcBounds = getArcBounds(
                state.getPieArea(), state.getExplodedPieArea(),
                angle1, angle, ep
            );
            Arc2D.Double arc = new Arc2D.Double(
                arcBounds, angle1, angle, Arc2D.PIE
            );
            
            if (currentPass == 0) {
                if (this.shadowPaint != null) {
                    Shape shadowArc = ShapeUtilities.createTranslatedShape(
                        arc, (float) this.shadowXOffset, 
                        (float) this.shadowYOffset
                    );
                    g2.setPaint(this.shadowPaint);
                    g2.fill(shadowArc);
                }
            }
            else if (currentPass == 1) {

                Paint paint = getSectionPaint(section);
                g2.setPaint(paint);
                g2.fill(arc);

                Paint outlinePaint = getSectionOutlinePaint(section);
                Stroke outlineStroke = getSectionOutlineStroke(section);
                if (outlinePaint != null && outlineStroke != null) {
                    g2.setPaint(outlinePaint);
                    g2.setStroke(outlineStroke);
                    g2.draw(arc);
                }
                
                // update the linking line target for later
                // add an entity for the pie section
                if (state.getInfo() != null) {
                    EntityCollection entities 
                        = state.getInfo().getOwner().getEntityCollection();
                    if (entities != null) {
                        Comparable key = this.dataset.getKey(section);
                        String tip = null;
                        if (this.toolTipGenerator != null) {
                            tip = this.toolTipGenerator.generateToolTip(
                                this.dataset, key
                            );
                        }
                        String url = null;
                        if (this.urlGenerator != null) {
                            url = this.urlGenerator.generateURL(
                                this.dataset, key, this.pieIndex
                            );
                        }
                        PieSectionEntity entity = new PieSectionEntity(
                            arc, this.dataset, this.pieIndex, section, key, 
                            tip, url
                        );
                        entities.add(entity);
                    }
                }
            }
        }    
        state.setLatestAngle(angle2);
    }
    
    /**
     * Draws the labels for the pie sections.
     * 
     * @param g2  the graphics device.
     * @param keys  the keys.
     * @param totalValue  the total value.
     * @param plotArea  the plot area.
     * @param linkArea  the link area.
     * @param state  the state.
     */
    protected void drawLabels(Graphics2D g2, List keys, double totalValue, 
                              Rectangle2D plotArea, Rectangle2D linkArea, 
                              PiePlotState state) {   

        Composite originalComposite = g2.getComposite();
        g2.setComposite(
            AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f)
        );

        // classify the keys according to which side the label will appear...
        DefaultKeyedValues leftKeys = new DefaultKeyedValues();
        DefaultKeyedValues rightKeys = new DefaultKeyedValues();
       
        double runningTotal1 = 0.0;
        Iterator iterator1 = keys.iterator();
        while (iterator1.hasNext()) {
            Comparable key = (Comparable) iterator1.next();
            Number n = this.dataset.getValue(key);
            if (n != null) {
                double v = n.doubleValue();
                if (this.ignoreZeroValues ? v > 0.0 : v >= 0.0) {
                    runningTotal1 = runningTotal1 + v;
                    // work out the mid angle (0 - 90 and 270 - 360) = right, 
                    // otherwise left
                    double mid = this.startAngle + (this.direction.getFactor()
                        * ((runningTotal1 - v / 2.0) * 360) / totalValue);
                    if (Math.cos(Math.toRadians(mid)) < 0.0) {
                        leftKeys.addValue(key, new Double(mid));
                    }
                    else {
                        rightKeys.addValue(key, new Double(mid));
                    }
                }
            }
        }
       
        g2.setFont(getLabelFont());
        float maxLabelWidth 
            = (float) (getMaximumLabelWidth() * plotArea.getWidth());
        
        // draw the left labels...
        if (this.labelGenerator != null) {
            drawLeftLabels(
                leftKeys, g2, plotArea, linkArea, maxLabelWidth, state
            );
            drawRightLabels(
                rightKeys, g2, plotArea, linkArea, maxLabelWidth, state
            );
        }
        g2.setComposite(originalComposite);

    }

    /**
     * Draws the left labels.
     * 
     * @param leftKeys  the keys.
     * @param g2  the graphics device.
     * @param plotArea  the plot area.
     * @param linkArea  the link area.
     * @param maxLabelWidth  the maximum label width.
     * @param state  the state.
     */
    protected void drawLeftLabels(KeyedValues leftKeys, Graphics2D g2, 
                                  Rectangle2D plotArea, Rectangle2D linkArea, 
                                  float maxLabelWidth, PiePlotState state) {
        
        PieLabelDistributor distributor1 = new PieLabelDistributor(
            leftKeys.getItemCount()
        );
        double lGap = plotArea.getWidth() * this.labelGap;
        double verticalLinkRadius = state.getLinkArea().getHeight() / 2.0;
        for (int i = 0; i < leftKeys.getItemCount(); i++) {   
            String label = this.labelGenerator.generateSectionLabel(
                this.dataset, leftKeys.getKey(i)
            );
            if (label != null) {
                TextBlock block = TextUtilities.createTextBlock(
                    label, 
                    this.labelFont, this.labelPaint, maxLabelWidth, 
                    new G2TextMeasurer(g2)
                );
                TextBox labelBox = new TextBox(block);
                labelBox.setBackgroundPaint(this.labelBackgroundPaint);
                labelBox.setOutlinePaint(this.labelOutlinePaint);
                labelBox.setOutlineStroke(this.labelOutlineStroke);
                labelBox.setShadowPaint(this.labelShadowPaint);
                double theta = Math.toRadians(
                    leftKeys.getValue(i).doubleValue()
                );
                double baseY = state.getPieCenterY() - Math.sin(theta) 
                               * verticalLinkRadius;
                double hh = labelBox.getHeight(g2);

                distributor1.addPieLabelRecord(
                    new PieLabelRecord(
                        leftKeys.getKey(i), theta, baseY, labelBox, hh,
                        lGap / 2.0 + lGap / 2.0 * -Math.cos(theta), 
                        0.9 + getExplodePercent(this.dataset.getIndex(
                                leftKeys.getKey(i)))
                    )
                );
            }
        }
        distributor1.distributeLabels(plotArea.getMinY(), plotArea.getHeight());
        for (int i = 0; i < distributor1.getItemCount(); i++) {
            drawLeftLabel(g2, state, distributor1.getPieLabelRecord(i));
        }
    }
    
    /**
     * Draws the right labels.
     * 
     * @param keys  the keys.
     * @param g2  the graphics device.
     * @param plotArea  the plot area.
     * @param linkArea  the link area.
     * @param maxLabelWidth  the maximum label width.
     * @param state  the state.
     */
    protected void drawRightLabels(KeyedValues keys, Graphics2D g2, 
                                   Rectangle2D plotArea, Rectangle2D linkArea, 
                                   float maxLabelWidth, PiePlotState state) {

        // draw the right labels...
        PieLabelDistributor distributor2 
            = new PieLabelDistributor(keys.getItemCount());
        double lGap = plotArea.getWidth() * this.labelGap;
        double verticalLinkRadius = state.getLinkArea().getHeight() / 2.0;

        for (int i = 0; i < keys.getItemCount(); i++) {
            String label = this.labelGenerator.generateSectionLabel(
                this.dataset, keys.getKey(i)
            );

            if (label != null) {
                TextBlock block = TextUtilities.createTextBlock(
                    label, this.labelFont, this.labelPaint, 
                    maxLabelWidth, new G2TextMeasurer(g2)
                );
                TextBox labelBox = new TextBox(block);
                labelBox.setBackgroundPaint(this.labelBackgroundPaint);
                labelBox.setOutlinePaint(this.labelOutlinePaint);
                labelBox.setOutlineStroke(this.labelOutlineStroke);
                labelBox.setShadowPaint(this.labelShadowPaint);
                double theta = Math.toRadians(keys.getValue(i).doubleValue());
                double baseY = state.getPieCenterY() 
                              - Math.sin(theta) * verticalLinkRadius;
                double hh = labelBox.getHeight(g2);
                distributor2.addPieLabelRecord(
                    new PieLabelRecord(
                        keys.getKey(i), theta, baseY, labelBox, hh,
                        lGap / 2.0 + lGap / 2.0 * Math.cos(theta), 
                        0.9 + getExplodePercent(this.dataset.getIndex(
                                keys.getKey(i)))
                    )
                );
            }
        }
        distributor2.distributeLabels(linkArea.getMinY(), linkArea.getHeight());
        for (int i = 0; i < distributor2.getItemCount(); i++) {
            drawRightLabel(g2, state, distributor2.getPieLabelRecord(i));
        }

    }
    
    /**
     * Returns a collection of legend items for the pie chart.
     *
     * @return The legend items (never <code>null</code>).
     */
    public LegendItemCollection getLegendItems() {

        LegendItemCollection result = new LegendItemCollection();

        List keys = null;
        if (this.dataset != null) {
            keys = this.dataset.getKeys();
            int section = 0;
            Shape shape = getLegendItemShape();
            Iterator iterator = keys.iterator();
            while (iterator.hasNext()) {
                Comparable key = (Comparable) iterator.next();
                Number n = this.dataset.getValue(key);
                if (n != null || !this.ignoreNullValues) {
                    String label 
                        = this.legendLabelGenerator.generateSectionLabel(
                            this.dataset, key
                        );
                    String description = label;
                    String toolTipText = null;
                    if (this.legendLabelToolTipGenerator != null) {
                        toolTipText 
                        = this.legendLabelToolTipGenerator.generateSectionLabel(
                            this.dataset, key
                        );
                    }
                    String urlText = null;
                    Paint paint = getSectionPaint(section);
                    Paint outlinePaint = getSectionOutlinePaint(section);
                    Stroke outlineStroke = getSectionOutlineStroke(section);

                    LegendItem item = new LegendItem(
                        label, description, toolTipText, urlText, 
                        true, shape, 
                        true, paint, 
                        true, outlinePaint, outlineStroke, 
                        false,          // line not visible
                        new Line2D.Float(),
                        new BasicStroke(),
                        Color.black
                    );
 
                    result.add(item);
                    section++;
                }
            }
        }

        return result;
    }

    /**
     * Returns a short string describing the type of plot.
     *
     * @return The plot type.
     */
    public String getPlotType() {
        return localizationResources.getString("Pie_Plot");
    }

    /**
     * A zoom method that does nothing.
     * <p>
     * Plots are required to support the zoom operation.  In the case of a pie
     * chart, it doesn't make sense to zoom in or out, so the method is empty.
     *
     * @param percent  the zoom percentage.
     */
    public void zoom(double percent) {
        // no zooming for pie plots
    }

    /**
     * Returns a rectangle that can be used to create a pie section (taking
     * into account the amount by which the pie section is 'exploded').
     *
     * @param unexploded  the area inside which the unexploded pie sections are
     *                    drawn.
     * @param exploded  the area inside which the exploded pie sections are 
     *                  drawn.
     * @param angle  the start angle.
     * @param extent  the extent of the arc.
     * @param explodePercent  the amount by which the pie section is exploded.
     *
     * @return A rectangle that can be used to create a pie section.
     */
    protected Rectangle2D getArcBounds(Rectangle2D unexploded, 
                                       Rectangle2D exploded,
                                       double angle, double extent, 
                                       double explodePercent) {

        if (explodePercent == 0.0) {
            return unexploded;
        }
        else {
            Arc2D arc1 = new Arc2D.Double(
                unexploded, angle, extent / 2, Arc2D.OPEN
            );
            Point2D point1 = arc1.getEndPoint();
            Arc2D.Double arc2 = new Arc2D.Double(
                exploded, angle, extent / 2, Arc2D.OPEN
            );
            Point2D point2 = arc2.getEndPoint();
            double deltaX = (point1.getX() - point2.getX()) * explodePercent;
            double deltaY = (point1.getY() - point2.getY()) * explodePercent;
            return new Rectangle2D.Double(
                unexploded.getX() - deltaX, unexploded.getY() - deltaY,
                unexploded.getWidth(), unexploded.getHeight()
            );
        }
    }
    
    /**
     * Draws a section label on the left side of the pie chart.
     * 
     * @param g2  the graphics device.
     * @param state  the state.
     * @param record  the label record.
     */
    protected void drawLeftLabel(Graphics2D g2, PiePlotState state, 
                                 PieLabelRecord record) {

        double anchorX = state.getLinkArea().getMinX();
        double targetX = anchorX - record.getGap();
        double targetY = record.getAllocatedY();
        
        if (this.labelLinksVisible) {
            double theta = record.getAngle();
            double linkX = state.getPieCenterX() + Math.cos(theta) 
                * state.getPieWRadius() * record.getLinkPercent();
            double linkY = state.getPieCenterY() - Math.sin(theta) 
                * state.getPieHRadius() * record.getLinkPercent();
            double elbowX = state.getPieCenterX() + Math.cos(theta) 
                * state.getLinkArea().getWidth() / 2.0;
            double elbowY = state.getPieCenterY() - Math.sin(theta) 
                * state.getLinkArea().getHeight() / 2.0;
            double anchorY = elbowY;
            g2.setPaint(this.labelLinkPaint);
            g2.setStroke(this.labelLinkStroke);
            g2.draw(new Line2D.Double(linkX, linkY, elbowX, elbowY));
            g2.draw(new Line2D.Double(anchorX, anchorY, elbowX, elbowY));
            g2.draw(new Line2D.Double(anchorX, anchorY, targetX, targetY));
        }
        TextBox tb = record.getLabel();
        tb.draw(g2, (float) targetX, (float) targetY, RectangleAnchor.RIGHT);
        
    }

    /**
     * Draws a section label on the right side of the pie chart.
     * 
     * @param g2  the graphics device.
     * @param state  the state.
     * @param record  the label record.
     */
    protected void drawRightLabel(Graphics2D g2, PiePlotState state, 
                                  PieLabelRecord record) {
        
        double anchorX = state.getLinkArea().getMaxX();
        double targetX = anchorX + record.getGap();
        double targetY = record.getAllocatedY();
        
        if (this.labelLinksVisible) {
            double theta = record.getAngle();
            double linkX = state.getPieCenterX() + Math.cos(theta) 
                * state.getPieWRadius() * record.getLinkPercent();
            double linkY = state.getPieCenterY() - Math.sin(theta) 
                * state.getPieHRadius() * record.getLinkPercent();
            double elbowX = state.getPieCenterX() + Math.cos(theta) 
                * state.getLinkArea().getWidth() / 2.0;
            double elbowY = state.getPieCenterY() - Math.sin(theta) 
                * state.getLinkArea().getHeight() / 2.0;
            double anchorY = elbowY;
            g2.setPaint(this.labelLinkPaint);
            g2.setStroke(this.labelLinkStroke);
            g2.draw(new Line2D.Double(linkX, linkY, elbowX, elbowY));
            g2.draw(new Line2D.Double(anchorX, anchorY, elbowX, elbowY));
            g2.draw(new Line2D.Double(anchorX, anchorY, targetX, targetY));
        }
        
        TextBox tb = record.getLabel();
        tb.draw(g2, (float) targetX, (float) targetY, RectangleAnchor.LEFT);
    
    }

    /**
     * Tests this plot for equality with an arbitrary object.  Note that the 
     * plot's dataset is NOT included in the test for equality.
     *
     * @param obj  the object to test against (<code>null</code> permitted).
     *
     * @return <code>true</code> or <code>false</code>.
     */
    public boolean equals(Object obj) {

        if (obj == this) {
            return true;
        }
        if (!(obj instanceof PiePlot)) {
            return false;
        }
        if (!super.equals(obj)) {
            return false;
        }
        PiePlot that = (PiePlot) obj;
        if (this.pieIndex != that.pieIndex) {
            return false;
        }
        if (this.interiorGap != that.interiorGap) {
            return false;
        }
        if (this.circular != that.circular) {
            return false;
        }
        if (this.startAngle != that.startAngle) {
            return false;
        }
        if (this.direction != that.direction) {
            return false;
        }
        if (this.ignoreZeroValues != that.ignoreZeroValues) {
            return false;
        }
        if (this.ignoreNullValues != that.ignoreNullValues) {
            return false;
        }
        if (!ObjectUtilities.equal(this.sectionPaint, that.sectionPaint)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.sectionPaintList, 
                that.sectionPaintList)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.baseSectionPaint, 
                that.baseSectionPaint)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.sectionOutlinePaint, 
                that.sectionOutlinePaint)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.sectionOutlinePaintList, 
                that.sectionOutlinePaintList)) {
            return false;
        }
        if (!ObjectUtilities.equal(
            this.baseSectionOutlinePaint, that.baseSectionOutlinePaint
        )) {
            return false;
        }
        if (!ObjectUtilities.equal(this.sectionOutlineStroke, 
                that.sectionOutlineStroke)) {
            return false;
        }
        if (!ObjectUtilities.equal(
            this.sectionOutlineStrokeList, that.sectionOutlineStrokeList
        )) {
            return false;
        }
        if (!ObjectUtilities.equal(
            this.baseSectionOutlineStroke, that.baseSectionOutlineStroke
        )) {
            return false;
        }
          
        if (!ObjectUtilities.equal(this.shadowPaint, that.shadowPaint)) {
            return false;
        }
        if (!(this.shadowXOffset == that.shadowXOffset)) {
            return false;
        }
        if (!(this.shadowYOffset == that.shadowYOffset)) {
            return false;
        }
            
        if (!ObjectUtilities.equal(this.explodePercentages, 
                that.explodePercentages)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.labelGenerator, 
                that.labelGenerator)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.labelFont, that.labelFont)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.labelPaint, that.labelPaint)) {
            return false;
        }
            
        if (!ObjectUtilities.equal(this.labelBackgroundPaint, 
                that.labelBackgroundPaint)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.labelOutlinePaint, 
                that.labelOutlinePaint)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.labelOutlineStroke, 
                that.labelOutlineStroke)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.labelShadowPaint, 
                that.labelShadowPaint)) {
            return false;
        }

        if (!(this.maximumLabelWidth == that.maximumLabelWidth)) {
            return false;
        }
        if (!(this.labelGap == that.labelGap)) {
            return false;
        }
        if (!(this.labelLinkMargin == that.labelLinkMargin)) {
            return false;
        }
        if (this.labelLinksVisible != that.labelLinksVisible) {
            return false;
        }
        if (!ObjectUtilities.equal(this.labelLinkPaint, that.labelLinkPaint)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.labelLinkStroke, 
                that.labelLinkStroke)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.toolTipGenerator, 
                that.toolTipGenerator)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.urlGenerator, that.urlGenerator)) {
            return false;
        }
        if (!(this.minimumArcAngleToDraw == that.minimumArcAngleToDraw)) {
            return false;
        }
        if (!ShapeUtilities.equal(this.legendItemShape, that.legendItemShape)) {
            return false;
        }

        // can't find any difference...
        return true;

    }

    /**
     * Returns a clone of the plot.
     *
     * @return A clone.
     *
     * @throws CloneNotSupportedException if some component of the plot does 
     *         not support cloning.
     */
    public Object clone() throws CloneNotSupportedException {

        PiePlot clone = (PiePlot) super.clone();
        if (clone.dataset != null) {
            clone.dataset.addChangeListener(clone);
        }
        return clone;

    }

    /**
     * Provides serialization support.
     *
     * @param stream  the output stream.
     *
     * @throws IOException  if there is an I/O error.
     */
    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        SerialUtilities.writePaint(this.sectionPaint, stream);
        SerialUtilities.writePaint(this.baseSectionPaint, stream);
        SerialUtilities.writePaint(this.sectionOutlinePaint, stream);
        SerialUtilities.writePaint(this.baseSectionOutlinePaint, stream);
        SerialUtilities.writeStroke(this.sectionOutlineStroke, stream);
        SerialUtilities.writeStroke(this.baseSectionOutlineStroke, stream);
        SerialUtilities.writePaint(this.shadowPaint, stream);
        SerialUtilities.writePaint(this.labelPaint, stream);
        SerialUtilities.writePaint(this.labelBackgroundPaint, stream);
        SerialUtilities.writePaint(this.labelOutlinePaint, stream);
        SerialUtilities.writeStroke(this.labelOutlineStroke, stream);
        SerialUtilities.writePaint(this.labelShadowPaint, stream);
        SerialUtilities.writePaint(this.labelLinkPaint, stream);
        SerialUtilities.writeStroke(this.labelLinkStroke, stream);
        SerialUtilities.writeShape(this.legendItemShape, stream);
    }

    /**
     * Provides serialization support.
     *
     * @param stream  the input stream.
     *
     * @throws IOException  if there is an I/O error.
     * @throws ClassNotFoundException  if there is a classpath problem.
     */
    private void readObject(ObjectInputStream stream) 
        throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        this.sectionPaint = SerialUtilities.readPaint(stream);
        this.baseSectionPaint = SerialUtilities.readPaint(stream);
        this.sectionOutlinePaint = SerialUtilities.readPaint(stream);
        this.baseSectionOutlinePaint = SerialUtilities.readPaint(stream);
        this.sectionOutlineStroke = SerialUtilities.readStroke(stream);
        this.baseSectionOutlineStroke = SerialUtilities.readStroke(stream);
        this.shadowPaint = SerialUtilities.readPaint(stream);
        this.labelPaint = SerialUtilities.readPaint(stream);
        this.labelBackgroundPaint = SerialUtilities.readPaint(stream);
        this.labelOutlinePaint = SerialUtilities.readPaint(stream);
        this.labelOutlineStroke = SerialUtilities.readStroke(stream);
        this.labelShadowPaint = SerialUtilities.readPaint(stream);
        this.labelLinkPaint = SerialUtilities.readPaint(stream);
        this.labelLinkStroke = SerialUtilities.readStroke(stream);
        this.legendItemShape = SerialUtilities.readShape(stream);
    }

}
