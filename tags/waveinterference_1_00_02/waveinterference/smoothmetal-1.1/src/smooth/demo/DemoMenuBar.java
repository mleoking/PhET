package smooth.demo;

import javax.swing.*;

/**
 * Menu bar for the demo app.
 *
 * @author James Shiell
 * @version 1.0
 */
public class DemoMenuBar extends JMenuBar {

    private final JMenu menuItemMenu = new JMenu( "JMenuItem" );
    private final JMenuItem standardMenuItem = new JMenuItem( "Standard" );
    private final JMenuItem disabledMenuItem = new JMenuItem( "Disabled" );

    private final JMenu menuMenu = new JMenu( "JMenu" );
    private final JMenu standardMenu = new JMenu( "Standard" );
    private final JMenuItem standardMenuMenuItem = new JMenuItem( "Standard" );
    private final JMenu disabledMenu = new JMenu( "Disabled" );

    private final JMenu checkBoxMenuItemMenu = new JMenu( "JCheckBoxMenuItem" );
    private final JCheckBoxMenuItem unselectedCheckBoxMenuItem = new JCheckBoxMenuItem( "Unselected" );
    private final JCheckBoxMenuItem selectedCheckBoxMenuItem = new JCheckBoxMenuItem( "Selected" );
    private final JCheckBoxMenuItem disabledUnselectedCheckBoxMenuItem = new JCheckBoxMenuItem( "Disabled Unselected" );
    private final JCheckBoxMenuItem disabledSelectedCheckBoxMenuItem = new JCheckBoxMenuItem( "Disabled Selected" );

    private final JMenu radioButtonMenuItemMenu = new JMenu( "JRadioButtonMenuItem" );
    private final JRadioButtonMenuItem unselectedRadioButtonMenuItem = new JRadioButtonMenuItem( "Unselected" );
    private final JRadioButtonMenuItem selectedRadioButtonMenuItem = new JRadioButtonMenuItem( "Selected" );
    private final JRadioButtonMenuItem disabledUnselectedRadioButtonMenuItem = new JRadioButtonMenuItem( "Disabled Unselected" );
    private final JRadioButtonMenuItem disabledSelectedRadioButtonMenuItem = new JRadioButtonMenuItem( "Disabled Selected" );

    public DemoMenuBar() {
        initialiseComponents();
    }

    private void initialiseComponents() {
        menuItemMenu.setMnemonic( 'i' );
        disabledMenuItem.setEnabled( false );
        menuItemMenu.add( standardMenuItem );
        menuItemMenu.add( disabledMenuItem );
        add( menuItemMenu );

        menuMenu.setMnemonic( 'm' );
        disabledMenu.setEnabled( false );
        standardMenu.add( standardMenuMenuItem );
        menuMenu.add( standardMenu );
        menuMenu.add( disabledMenu );
        add( menuMenu );

        checkBoxMenuItemMenu.setMnemonic( 'c' );
        selectedCheckBoxMenuItem.setSelected( true );
        disabledUnselectedCheckBoxMenuItem.setEnabled( false );
        disabledSelectedCheckBoxMenuItem.setEnabled( false );
        disabledSelectedCheckBoxMenuItem.setSelected( true );
        checkBoxMenuItemMenu.add( unselectedCheckBoxMenuItem );
        checkBoxMenuItemMenu.add( selectedCheckBoxMenuItem );
        checkBoxMenuItemMenu.add( disabledUnselectedCheckBoxMenuItem );
        checkBoxMenuItemMenu.add( disabledSelectedCheckBoxMenuItem );
        add( checkBoxMenuItemMenu );

        radioButtonMenuItemMenu.setMnemonic( 'r' );
        selectedRadioButtonMenuItem.setSelected( true );
        disabledUnselectedRadioButtonMenuItem.setEnabled( false );
        disabledSelectedRadioButtonMenuItem.setEnabled( false );
        disabledSelectedRadioButtonMenuItem.setSelected( true );
        radioButtonMenuItemMenu.add( unselectedRadioButtonMenuItem );
        radioButtonMenuItemMenu.add( selectedRadioButtonMenuItem );
        radioButtonMenuItemMenu.add( disabledUnselectedRadioButtonMenuItem );
        radioButtonMenuItemMenu.add( disabledSelectedRadioButtonMenuItem );
        add( radioButtonMenuItemMenu );

        validate();
    }

}
