package smooth.demo;

import javax.swing.*;

/**
 * The toolbar for the demo application.
 *
 * @author James Shiell
 * @version 1.0
 */
public class DemoToolbar extends JToolBar {

    private final JButton enabledButton = new JButton();
    private final JButton disabledButton = new JButton();
    private final JToggleButton enabledToggleButton = new JToggleButton();
    private final JToggleButton disabledToggleButton = new JToggleButton();

    public DemoToolbar() {
        initialiseComponents();
    }

    private void initialiseComponents() {
        enabledButton.setIcon( new ImageIcon( getClass().getResource( "/smooth/demo/images/back-arrow.png" ) ) );
        add( enabledButton );

        disabledButton.setIcon( new ImageIcon( getClass().getResource( "/smooth/demo/images/forward-arrow.png" ) ) );
        disabledButton.setEnabled( false );
        add( disabledButton );

        addSeparator();

        enabledToggleButton.setIcon( new ImageIcon( getClass().getResource( "/smooth/demo/images/star.png" ) ) );
        add( enabledToggleButton );

        disabledToggleButton.setIcon( new ImageIcon( getClass().getResource( "/smooth/demo/images/star.png" ) ) );
        disabledToggleButton.setEnabled( false );
        add( disabledToggleButton );

        add( Box.createHorizontalGlue() );
    }

}
