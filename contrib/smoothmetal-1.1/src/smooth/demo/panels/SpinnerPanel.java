package smooth.demo.panels;

import javax.swing.*;
import java.awt.*;

/**
 * Spinner demonstration panel.
 *
 * @author James Shiell
 * @version 1.0
 */
public class SpinnerPanel extends JPanel {

    private final JPanel contentPanel = new JPanel( new GridLayout( 0, 2, 4, 4 ) );
    private final JLabel editableLabel = new JLabel( "Standard" );
    private final JSpinner editableSpinner = new JSpinner( new SpinnerNumberModel( 10.0, 0.0, 100.0, 1.0 ) );
    private final JLabel disabledLabel = new JLabel( "Disabled" );
    private final JSpinner disabledSpinner = new JSpinner( new SpinnerNumberModel( 10.0, 0.0, 100.0, 1.0 ) );

    public SpinnerPanel() {
        initialiseComponents();
    }

    private void initialiseComponents() {
        setLayout( new GridBagLayout() );
        disabledSpinner.setEnabled( false );

        disabledSpinner.setEnabled( false );

        contentPanel.add( editableSpinner );
        contentPanel.add( editableLabel );
        contentPanel.add( disabledSpinner );
        contentPanel.add( disabledLabel );

        add( contentPanel, new GridBagConstraints( 0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER,
                                                   GridBagConstraints.NONE, new Insets( 4, 4, 4, 4 ), 0, 0 ) );
    }

}
