package smooth.demo.panels;

import javax.swing.*;
import java.awt.*;

/**
 * Button demonstration panel.
 *
 * @author James Shiell
 * @version 1.0
 */
public class ToggleButtonPanel extends JPanel {

    private final JPanel contentPanel = new JPanel( new GridLayout( 0, 1, 4, 4 ) );
    private final JToggleButton standardButton = new JToggleButton( "Standard" );
    private final JToggleButton selectedButton = new JToggleButton( "Standard Selected" );
    private final JToggleButton htmlButton = new JToggleButton( "<html><body><b>HTML</b> Formatted <i>Text</i></body></html>" );
    private final JToggleButton disabledButton = new JToggleButton( "Disabled" );
    private final JToggleButton disabledSelectedButton = new JToggleButton( "Disabled Selected" );

    public ToggleButtonPanel() {
        initialiseComponents();
    }

    private void initialiseComponents() {
        setLayout( new GridBagLayout() );
        selectedButton.setSelected( true );
        disabledButton.setEnabled( false );
        disabledSelectedButton.setEnabled( false );
        disabledSelectedButton.setSelected( true );

        contentPanel.add( standardButton );
        contentPanel.add( selectedButton );
        contentPanel.add( htmlButton );
        contentPanel.add( disabledButton );
        contentPanel.add( disabledSelectedButton );

        add( contentPanel, new GridBagConstraints( 0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER,
                                                   GridBagConstraints.NONE, new Insets( 4, 4, 4, 4 ), 0, 0 ) );
    }

}
