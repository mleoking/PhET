/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.faraday.control;

// JDK
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JComponent;
import javax.swing.JPanel;


/**
 * EasyGridBagLayout provides an improved interface to GridBagLayout.
 *
 * @author Chris Malley
 * @version $Revision$ $Date$
 *
 */
public class EasyGridBagLayout extends GridBagLayout {

    //-----------------------------------------------------------------
    // Class data
    //-----------------------------------------------------------------

    /** Default gridwidth constraint */
    public static final int DEFAULT_GRIDWIDTH = 1;

    /** Default gridheight constraint */
    public static final int DEFAULT_GRIDHEIGHT = 1;

    /** Default anchor constraint */
    public static final int DEFAULT_ANCHOR = GridBagConstraints.WEST;

    /** Default fill constraint */
    public static final int DEFAULT_FILL = GridBagConstraints.NONE;

    /** Default insets constraint */
    public static final Insets DEFAULT_INSETS = new Insets( 2, 2, 2, 2 );

    /** Default ipadx constraint */
    public static final int DEFAULT_IPADX = 0;

    /** Default ipady constraint */
    public static final int DEFAULT_IPADY = 0;

    //-----------------------------------------------------------------
    // Instance data
    //-----------------------------------------------------------------

    // Panel to be managed
    private JPanel _panel;

    // Default insets
    private Insets _insets;

    // Reuse, to conserve memory.
    private GridBagConstraints _constraints;

    //-----------------------------------------------------------------
    // Constructors
    //-----------------------------------------------------------------

    /** 
     * Constructor with default Insets.
     *
     * @param panel the panel to be managed
     * @throws NullPointerException if panel is null
     */
    public EasyGridBagLayout( JPanel panel ) throws NullPointerException {
        this( panel, DEFAULT_INSETS );
    }

    /** 
     * Constructor with specific Insets.
     *
     * @param panel the panel to be managed
     * @param insets the insets to be used for all constraints
     * @throws NullPointerException if panel is null
     */
    public EasyGridBagLayout( JPanel panel, Insets insets ) throws NullPointerException {
        super();

        if ( panel == null ) {
            throw new NullPointerException();
        }

        _panel = panel;
        _insets = insets;
        _constraints = new GridBagConstraints();
    }

    //-----------------------------------------------------------------
    // The "new and improved" interface ...
    //-----------------------------------------------------------------

    /**
     * Adds a component to the managed panel.
     * This is a convenience function for setting GridBagConstraints.
     * Default values are used for all unspecified constraints.
     *
     * @param component the component to add
     * @param row the row in the grid
     * @param column the column in the grid
     */
    public void addComponent( JComponent component, int row, int column ) {
        addComponent( component, row, column, DEFAULT_GRIDWIDTH, DEFAULT_GRIDHEIGHT, DEFAULT_ANCHOR, DEFAULT_FILL, _insets );
    }

    /**
     * Adds a component to the managed panel.
     * This is a convenience function for setting GridBagConstraints.
     * Default values are used for all unspecified constraints.
     *
     * @param component the component to add
     * @param row the row in the grid 
     * @param column the column in the grid
     * @param width the width
     * @param height the height
     */
    public void addComponent( JComponent component, int row, int column, int width, int height ) {
        addComponent( component, row, column, width, height, DEFAULT_ANCHOR, DEFAULT_FILL, _insets );
    }

    /**
     * Adds a component to the managed panel.
     * This is a convenience function for setting GridBagConstraints.
     * Default values are used for all unspecified constraints.
     *
     * @param component the component to add
     * @param row the row in the grid 
     * @param column the column in the grid
     * @param width the width
     * @param height the height
     * @param anchor the anchor
     */
    public void addComponent( JComponent component, int row, int column, int width, int height, int anchor ) {
        addComponent( component, row, column, width, height, anchor, DEFAULT_FILL, _insets );
    }

    /**
     * Adds an anchored component to the managed panel.
     * This is a convenience function for setting GridBagConstraints.
     * Default values are used for all unspecified constraints.
     *
     * @param component the component to add
     * @param row the row in the grid 
     * @param column the column in the grid
     * @param anchor the anchor
     */
    public void addAnchoredComponent( JComponent component, int row, int column, int anchor ) {
        addComponent( component, row, column, DEFAULT_GRIDWIDTH, DEFAULT_GRIDHEIGHT, anchor, DEFAULT_FILL, _insets );
    }

    /**
     * Adds an anchored component to the managed panel.
     * This is a convenience function for setting GridBagConstraints.
     * Default values are used for all unspecified constraints.
     *
     * @param component the component to add
     * @param row the row in the grid 
     * @param column the column in the grid
     * @param width the width
     * @param height the height
     * @param anchor the anchor
     */
    public void addAnchoredComponent( JComponent component, int row, int column, int width, int height, int anchor ) {
        addComponent( component, row, column, width, height, anchor, DEFAULT_FILL, _insets );
    }

    /**
     * Adds a filled component to the managed panel.
     * This is a convenience function for setting GridBagConstraints.
     * Default values are used for all unspecified constraints.
     *
     * @param component the component to add
     * @param row the row in the grid 
     * @param column the column in the grid
     * @param fill the fill
     */
    public void addFilledComponent( JComponent component, int row, int column, int fill ) {
        addComponent( component, row, column, DEFAULT_GRIDWIDTH, DEFAULT_GRIDHEIGHT, DEFAULT_ANCHOR, fill, _insets );
    }

    /**
     * Adds a filled component to the managed panel.
     * This is a convenience function for setting GridBagConstraints.
     * Default values are used for all unspecified constraints.
     *
     * @param component the component to add
     * @param row the row in the grid 
     * @param column the column in the grid
     * @param width the width
     * @param height the height
     * @param fill the fill
     */
    public void addFilledComponent( JComponent component, int row, int column, int width, int height, int fill ) {
        addComponent( component, row, column, width, height, DEFAULT_ANCHOR, fill, _insets );
    }

    /**
     * Adds a component to the managed panel.
     * This is a convenience function for setting GridBagConstraints.
     * Default values are used for all unspecified constraints.
     *
     * @param component the component to add
     * @param row the row in the grid 
     * @param column the column in the grid
     * @param width the width
     * @param height the height
     * @param anchor the anchor
     * @param fill the fill
     */
    public void addComponent( JComponent component, int row, int column, int width, int height, int anchor, int fill ) {
        addComponent( component, row, column, width, height, DEFAULT_ANCHOR, fill, _insets );
    }

    /**
     * Adds a component to the managed panel.
     * This is a convenience function for setting GridBagConstraints.
     * Default values are used for all unspecified constraints.
     *
     * @param component the component to add
     * @param row the row in the grid 
     * @param column the column in the grid
     * @param width the width
     * @param height the height
     * @param anchor the anchor
     * @param fill the fill
     * @param insets the insets
     */
    public void addComponent( JComponent component, int row, int column, int width, int height, int anchor, int fill, Insets insets ) {
        // Load the GridBagConstraints
        _constraints.gridx = column;
        _constraints.gridy = row;
        _constraints.gridwidth = width;
        _constraints.gridheight = height;
        _constraints.anchor = anchor;
        _constraints.fill = fill;
        _constraints.insets = _insets;
        _constraints.ipadx = DEFAULT_IPADX;
        _constraints.ipady = DEFAULT_IPADY;

        // Determine sensible weights
        {
            double weightx = 0.0;
            double weighty = 0.0;
            if ( width > 1 ) {
                weightx = 1.0;
            }
            if ( height > 1 ) {
                weighty = 1.0;
            }

            switch ( fill ) {
            case GridBagConstraints.HORIZONTAL:
                _constraints.weightx = weightx;
                _constraints.weighty = 0.0;
                break;
            case GridBagConstraints.VERTICAL:
                _constraints.weightx = 0.0;
                _constraints.weighty = weighty;
                break;
            case GridBagConstraints.BOTH:
                _constraints.weightx = weightx;
                _constraints.weighty = weighty;
                break;
            case GridBagConstraints.NONE:
                _constraints.weightx = 0.0;
                _constraints.weighty = 0.0;
                break;
            }
        }

        // Add the component to the managed panel.
        _panel.add( component, _constraints );
    }

    /**
     * Sets the minimum width for a column.
     *
     * @param column the column
     * @param width minimum width, in pixels
     */
    public void setMinimumWidth( int column, int width ) {
        int[] widths = this.columnWidths;
        if ( widths == null ) {
            widths = new int[column + 1];
        }
        else if ( widths.length < column + 1 ) {
            widths = new int[column + 1];
            System.arraycopy( this.columnWidths, 0, widths, 0, this.columnWidths.length );
        }
        widths[column] = width;
        this.columnWidths = widths;
    }

    /**
     * Sets the minimum height for a row.
     *
     * @param row the row
     * @param height minimum height, in pixels
     */
    public void setMinimumHeight( int row, int height ) {
        int[] heights = this.rowHeights;
        if ( heights == null ) {
            heights = new int[row + 1];
        }
        else if ( heights.length < row + 1 ) {
            heights = new int[row + 1];
            System.arraycopy( this.rowHeights, 0, heights, 0, this.rowHeights.length );
        }
        heights[row] = height;
        this.rowHeights = heights;
    }
}