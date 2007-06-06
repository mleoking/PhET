/* Copyright 2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source: C:/Java/cvs/root/SelfDrivenParticles/phetcommon/src/edu/colorado/phet/common/view/components/VerticalLayoutPanel.java,v $
 * Branch : $Name:  $
 * Modified by : $Author: Sam Reid $
 * Revision : $Revision: 1.1.1.1 $
 * Date modified : $Date: 2005/08/10 08:22:02 $
 */
package edu.colorado.phet.common.view.components;

import javax.swing.*;
import java.awt.*;

/**
 * VerticalLayoutPanel
 *
 * @author ?
 * @version $Revision: 1.1.1.1 $
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

    public void setGridY( int gridy ) {
        this.gridBagConstraints.gridy = gridy;
    }

    public void setFill( int fill ) {
        gridBagConstraints.fill = fill;
    }

    public void setFillHorizontal() {
        setFill( GridBagConstraints.HORIZONTAL );
    }

    public void setFillNone() {
        setFill( GridBagConstraints.NONE );
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

    public Component addFullWidth( Component component ) {
        int fill = getFill();
        setFill( GridBagConstraints.HORIZONTAL );
        Component c = add( component );
        setFill( fill );
        return c;
    }
}
