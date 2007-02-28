package smooth.demo.panels;

import javax.swing.*;
import java.awt.*;

/**
 * Button demonstration panel.
 *
 * @author James Shiell
 * @version 1.0
 */
public class ButtonPanel extends JPanel {

    private final JPanel contentPanel = new JPanel( new GridLayout( 0, 1, 4, 4 ) );
    private final JButton standardButton = new JButton( "Standard" );
    private final JButton defaultButton = new JButton( "Default" );
    private final JButton htmlButton = new JButton( "<html><body><b>HTML</b> Formatted <i>Text</i></body></html>" );
    private final JButton disabledButton = new JButton( "Disabled" );

    public ButtonPanel() {
        initialiseComponents();
    }

    private void initialiseComponents() {
        setLayout( new GridBagLayout() );
        disabledButton.setEnabled( false );

        contentPanel.add( standardButton );
        contentPanel.add( defaultButton );
        contentPanel.add( htmlButton );
        contentPanel.add( disabledButton );

        add( contentPanel, new GridBagConstraints( 0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER,
                                                   GridBagConstraints.NONE, new Insets( 4, 4, 4, 4 ), 0, 0 ) );
    }

    public void setDefaultButton( final JRootPane rootPane ) {
        rootPane.setDefaultButton( defaultButton );
    }

}
