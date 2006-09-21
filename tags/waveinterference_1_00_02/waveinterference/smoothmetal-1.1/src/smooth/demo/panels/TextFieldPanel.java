package smooth.demo.panels;

import javax.swing.*;
import java.awt.*;

/**
 * Text field demonstration panel.
 *
 * @author James Shiell
 * @version 1.0
 */
public class TextFieldPanel extends JPanel {

    private final JPanel contentPanel = new JPanel( new GridLayout( 0, 2, 4, 4 ) );
    private final JLabel standardLabel = new JLabel( "Standard" );
    private final JTextField standardTextField = new JTextField( "Text" );
    private final JLabel lockedLabel = new JLabel( "Locked" );
    private final JTextField lockedTextField = new JTextField( "Uneditable" );
    private final JLabel disabledLabel = new JLabel( "Disabled" );
    private final JTextField disabledTextField = new JTextField( "Text" );
    private final JLabel passwordLabel = new JLabel( "Password" );
    private final JPasswordField passwordField = new JPasswordField( "Password" );

    public TextFieldPanel() {
        initialiseComponents();
    }

    private void initialiseComponents() {
        setLayout( new GridBagLayout() );
        disabledTextField.setEnabled( false );

        lockedTextField.setEditable( false );
        disabledTextField.setEnabled( false );

        contentPanel.add( standardTextField );
        contentPanel.add( standardLabel );
        contentPanel.add( lockedTextField );
        contentPanel.add( lockedLabel );
        contentPanel.add( disabledTextField );
        contentPanel.add( disabledLabel );
        contentPanel.add( passwordField );
        contentPanel.add( passwordLabel );

        add( contentPanel, new GridBagConstraints( 0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER,
                                                   GridBagConstraints.NONE, new Insets( 4, 4, 4, 4 ), 0, 0 ) );
    }

}
