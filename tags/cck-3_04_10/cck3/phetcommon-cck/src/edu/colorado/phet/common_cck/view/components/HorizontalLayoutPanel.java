/** Sam Reid*/
package edu.colorado.phet.common_cck.view.components;

import javax.swing.*;
import java.awt.*;

/**
 * User: Sam Reid
 * Date: Aug 13, 2004
 * Time: 10:10:30 AM
 * Copyright (c) Aug 13, 2004 by Sam Reid
 */
public class HorizontalLayoutPanel extends JPanel {
    private GridBagConstraints gridBagConstraints;

    public HorizontalLayoutPanel() {
        setLayout( new GridBagLayout() );
        gridBagConstraints = new GridBagConstraints( 0, 0, 1, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets( 0, 0, 0, 0 ), 0, 0 );
    }

    public GridBagConstraints getGridBagConstraints() {
        return gridBagConstraints;
    }

    public Component add( Component comp ) {
        super.add( comp, gridBagConstraints );
        gridBagConstraints.gridx++;
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
}
