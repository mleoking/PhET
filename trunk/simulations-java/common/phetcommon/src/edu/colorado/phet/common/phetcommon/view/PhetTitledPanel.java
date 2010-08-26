package edu.colorado.phet.common.phetcommon.view;

import java.awt.*;
import java.awt.event.ContainerAdapter;
import java.awt.event.ContainerEvent;

import javax.swing.*;

/**
 * The PhET Titled Panel uses the PhetTitledBorder, but expands the rest of the component to ensure the titled border is fully visible.
 * See #2476
 *
 * @author Sam Reid
 */
public class PhetTitledPanel extends JPanel {
    private final PhetTitledBorder titledBorder;
    private static final int AMOUNT_TO_EXTEND_BEYOND_TITLE = 5;//so that you can see the curve of the line border, without this factor, the line border drops down instead of curving to the right

    public PhetTitledPanel( String title ) {
        titledBorder = new PhetTitledBorder( title );
        setBorder( titledBorder );
        addContainerListener( new ContainerAdapter() {
            @Override
            public void componentAdded( ContainerEvent e ) {
                updateLayout();
            }

            @Override
            public void componentRemoved( ContainerEvent e ) {
                updateLayout();
            }
        } );
        updateLayout();
    }

    /**
     * Updates the preferred size to account for the titled border (if necessary).
     */
    private void updateLayout() {
        setPreferredSize( null ); //forget the old preferred size so that getPreferredSize() will recompute it
        final int minBorderWidth = titledBorder.getMinimumSize( this ).width + AMOUNT_TO_EXTEND_BEYOND_TITLE;
        if ( getPreferredSize().getWidth() < minBorderWidth ) {
            setPreferredSize( new Dimension( minBorderWidth, getPreferredSize().height ) );
        }
    }
}
