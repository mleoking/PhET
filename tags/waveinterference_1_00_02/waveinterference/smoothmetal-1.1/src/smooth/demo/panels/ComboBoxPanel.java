package smooth.demo.panels;

import javax.swing.*;
import java.awt.*;

/**
 * Combo box demonstration panel.
 *
 * @author James Shiell
 * @version 1.0
 */
public class ComboBoxPanel extends JPanel {

    private final String[] comboBoxContents = {
            "Item 1",
            "Item 2",
            "Item 3",
            "Item 4",
            "Item 5"
    };

    private final JPanel contentPanel = new JPanel( new GridLayout( 0, 2, 4, 4 ) );
    private final JLabel editableLabel = new JLabel( "Editable" );
    private final JComboBox editableComboBox = new JComboBox( comboBoxContents );
    private final JLabel uneditableLabel = new JLabel( "Uneditable" );
    private final JComboBox uneditableComboBox = new JComboBox( comboBoxContents );
    private final JLabel disabledLabel = new JLabel( "Disabled" );
    private final JComboBox disabledComboBox = new JComboBox( comboBoxContents );

    public ComboBoxPanel() {
        initialiseComponents();
    }

    private void initialiseComponents() {
        setLayout( new GridBagLayout() );
        disabledComboBox.setEnabled( false );

        editableComboBox.setEditable( true );
        disabledComboBox.setEnabled( false );

        contentPanel.add( editableComboBox );
        contentPanel.add( editableLabel );
        contentPanel.add( uneditableComboBox );
        contentPanel.add( uneditableLabel );
        contentPanel.add( disabledComboBox );
        contentPanel.add( disabledLabel );

        add( contentPanel, new GridBagConstraints( 0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER,
                                                   GridBagConstraints.NONE, new Insets( 4, 4, 4, 4 ), 0, 0 ) );
    }

}
