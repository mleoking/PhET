// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.piccolophet.swing;

import java.awt.*;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.view.PhetTitledBorder;
import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;

/**
 * Another possible way of implementing PhetTitledPanel, see #2606
 *
 * @author Sam Reid
 */
public class PhetTitledPanel3 {
    protected VerticalLayoutPanel verticalLayoutPanel;

    public PhetTitledPanel3( String title, final Font font, Color color, JComponent child ) {
        verticalLayoutPanel = new VerticalLayoutPanel();
        verticalLayoutPanel.setBorder( new PhetTitledBorder( title, font ) );
        verticalLayoutPanel.setAnchor( GridBagConstraints.WEST );
        verticalLayoutPanel.setFillNone();
        final int width = (int) new JLabel( title ) {{
            setFont( font );
        }}.getPreferredSize().getWidth() + 5;
        System.out.println( "width = " + width );
        verticalLayoutPanel.add( Box.createHorizontalStrut( width ) );
        verticalLayoutPanel.add( child );
    }

    /**
     * This sample main demonstrates usage of the PhetTitledPanel
     *
     * @param args not used
     */
    public static void main( String[] args ) {
        JFrame frame = new JFrame( "Test" );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        final PhetTitledPanel3 contentPane = new PhetTitledPanel3( "aoensuthasnotehuBorder", new PhetFont( 18 ), Color.blue, new VerticalLayoutPanel() {{
            for ( int i = 0; i < 10; i++ ) {
                add( new JLabel( "medium sized label " + i ) );
            }
            add( new JButton( "A button" ) );
        }} );

        frame.setContentPane( contentPane.getComponent() );
        frame.pack();
        SwingUtils.centerWindowOnScreen( frame );
        frame.setVisible( true );
    }

    private Container getComponent() {
        return verticalLayoutPanel;
    }
}
