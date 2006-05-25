package smooth.demo;

import javax.swing.*;
import java.awt.*;

/**
 * The main frame of the demo app.
 *
 * @author James Shiell
 * @version 1.0
 */
public class DemoFrame
        extends JFrame {
    static {
        JFrame.setDefaultLookAndFeelDecorated( true );
        JDialog.setDefaultLookAndFeelDecorated( true );
    }

    private final DemoMenuBar menuBar = new DemoMenuBar();
    private final DemoToolbar toolbar = new DemoToolbar();
    private final JDesktopPane desktop = new JDesktopPane();
    private final ControlInternalFrame controlFrame
            = new ControlInternalFrame();
    private final DemoInternalFrame demoFrame = new DemoInternalFrame();

    public DemoFrame() {
        initialiseComponents();
    }

    private void initialiseComponents() {
        setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        setTitle( "SmoothMetal Demonstration" );

        setJMenuBar( menuBar );

        getContentPane().add( toolbar, BorderLayout.NORTH );
        getContentPane().add( desktop, BorderLayout.CENTER );

        desktop.add( controlFrame );
        controlFrame.setVisible( true );
        desktop.add( demoFrame );
        demoFrame.setVisible( true );

        pack();

        final Dimension screenSize = Toolkit.getDefaultToolkit()
                .getScreenSize();
        setSize( 700, 600 );
        setLocation( ( screenSize.width - getSize().width ) / 2,
                     ( screenSize.height - getSize().height ) / 2 );

        demoFrame.setLocation( 10, 10 );
        demoFrame.setSize( 600, 450 );
        controlFrame.setLocation( getWidth() - controlFrame.getWidth() - 20, 10 );
    }

}
