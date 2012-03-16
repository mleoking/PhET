// Copyright 2002-2011, University of Colorado

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author:samreid $
 * Revision : $Revision:14669 $
 * Date modified : $Date:2007-04-17 02:12:41 -0500 (Tue, 17 Apr 2007) $
 */

package edu.colorado.phet.common.charts;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import edu.colorado.phet.common.phetgraphics.view.phetgraphics.PhetTextGraphic;


/**
 * StringLabelTable is a specialization of LabelTable that
 * allows you to specify chart labels as Strings.  For each
 * String, it generates a PhetTextGraphic.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision:14669 $
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
