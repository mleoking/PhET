/* Copyright 2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.common.view.components;

import javax.swing.*;
import java.awt.*;

/**
 * VerticalLayoutPanel
 *
 * @author ?
 * @version $Revision$
 */
public class VerticalLayoutPanel extends JPanel {
    private GridBagConstraints gridBagConstraints;

    public VerticalLayoutPanel() {
        setLayout( new GridBagLayout() );
        gridBagConstraints = new GridBagConstraints( 0, 0, 1, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets( 0, 0, 0, 0 ), 0, 0 );
    }

    public GridBagConstraints getGridBagConstraints() {
        return gridBagConstraints;
    }

    public Component add( Component comp ) {
        super.add( comp, gridBagConstraints );
        gridBagConstraints.gridy++;
        return null;
    }

    public void setFill( int fill ) {
        gridBagConstraints.fill = fill;
    }

    public void setAnchor( int anchor ) {
        gridBagConstraints.anchor = anchor;
    }

    public void setInsets( Insets insets ) {
        gridBagConstraints.insets = insets;
    }

    public int getFill() {
        return gridBagConstraints.fill;
    }
}
