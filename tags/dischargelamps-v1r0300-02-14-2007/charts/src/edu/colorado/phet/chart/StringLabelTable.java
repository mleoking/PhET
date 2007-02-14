/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.chart;

import edu.colorado.phet.common.view.phetgraphics.PhetTextGraphic;

import java.awt.*;


/**
 * StringLabelTable is a specialization of LabelTable that
 * allows you to specify chart labels as Strings.  For each
 * String, it generates a PhetTextGraphic.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class StringLabelTable extends LabelTable {

    private Component _component;
    private Font _font;
    private Color _color;

    /**
     * Sole constructor.
     *
     * @param component the parent Component
     * @param font      the font used for all labels
     * @param color     the color used for all labels
     */
    public StringLabelTable( Component component, Font font, Color color ) {
        super();
        _component = component;
        _font = font;
        _color = color;
    }

    /**
     * Adds a value/label pair to the label table.
     *
     * @param value
     * @param label
     */
    public void put( double value, String label ) {
        super.put( value, new PhetTextGraphic( _component, _font, label, _color ) );
    }
}
