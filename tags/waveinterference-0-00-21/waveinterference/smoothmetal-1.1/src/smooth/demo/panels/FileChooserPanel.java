package smooth.demo.panels;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * File chooser demonstration panel.
 *
 * @author James Shiell
 * @version 1.0
 */
public class FileChooserPanel extends JPanel {

    private final JPanel contentPanel = new JPanel( new GridLayout( 0, 1, 4, 4 ) );
    private final JButton fileButton = new JButton( "Show File Dialogue" );

    public FileChooserPanel() {
        initialiseComponents();
        initialiseControllers();
    }

    private void initialiseComponents() {
        setLayout( new GridBagLayout() );

        contentPanel.add( fileButton );

        add( contentPanel, new GridBagConstraints( 0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER,
                                                   GridBagConstraints.NONE, new Insets( 4, 4, 4, 4 ), 0, 0 ) );
    }

    private void initialiseControllers() {
        fileButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                fileButtonPressed();
            }
        } );
    }

    private void fileButtonPressed() {
        final JFileChooser fileChooser = new JFileChooser();
        fileChooser.showOpenDialog( this );
    }


}
