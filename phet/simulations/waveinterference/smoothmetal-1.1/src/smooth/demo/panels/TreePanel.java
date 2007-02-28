package smooth.demo.panels;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Tree demonstration panel.
 *
 * @author James Shiell
 * @version 1.0
 */
public class TreePanel extends JPanel {

    private final JTree demoTree = new JTree();
    private final JScrollPane treeScrollPane = new JScrollPane();

    public TreePanel() {
        initialiseComponents();
    }

    private void initialiseComponents() {
        setLayout( new BorderLayout() );
        setBorder( new EmptyBorder( 4, 4, 4, 4 ) );

        treeScrollPane.setViewportView( demoTree );

        add( treeScrollPane, BorderLayout.CENTER );
    }

}
