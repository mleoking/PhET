/** Sam Reid*/
package edu.colorado.phet.movingman.common;

import javax.swing.*;
import java.awt.*;

/**
 * User: Sam Reid
 * Date: Aug 13, 2004
 * Time: 10:10:30 AM
 * Copyright (c) Aug 13, 2004 by Sam Reid
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
}
