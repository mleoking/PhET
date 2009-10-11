package edu.colorado.phet.naturalselection.control;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;

import javax.swing.*;
import javax.swing.border.LineBorder;

import edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.naturalselection.NaturalSelectionConstants;
import edu.colorado.phet.naturalselection.NaturalSelectionResources;
import edu.colorado.phet.naturalselection.module.NaturalSelectionModule;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolox.pswing.PSwing;

public class DetachOptionPanel extends JPanel {
    private PhetPCanvas child;
    private Component placeholder;

    private List<Listener> listeners = new LinkedList<Listener>();

    private JDialog dialog;
    private JPanel dialogContentPane;
    private PSwing buttonPanelPSwing;

    private String title;
    private JButton closeButton;
    private JPanel buttonPanel;

    public DetachOptionPanel( String title, PhetPCanvas child, Component placeholder ) {
        super( new GridLayout( 1, 1 ) );

        this.child = child;
        this.placeholder = placeholder;
        this.title = title;

        buttonPanel = new JPanel( new FlowLayout( FlowLayout.CENTER, 1, 1 ) );

        BufferedImage detachIcon = NaturalSelectionResources.getImage( NaturalSelectionConstants.IMAGE_DETACH_ICON );
        BufferedImage closeIcon = NaturalSelectionResources.getImage( NaturalSelectionConstants.IMAGE_CLOSE_ICON );

        JButton detachButton = new JButton( new ImageIcon( BufferedImageUtils.rescaleYMaintainAspectRatio( detachIcon, 14 ) ) );
        closeButton = new JButton( new ImageIcon( BufferedImageUtils.rescaleYMaintainAspectRatio( closeIcon, 14 ) ) );

        Insets buttonInsets = new Insets( 2, 2, 2, 2 );
        detachButton.setMargin( buttonInsets );
        closeButton.setMargin( buttonInsets );

        buttonPanel.add( detachButton );
        buttonPanel.setBackground( NaturalSelectionConstants.COLOR_GENERATION_CHART );

        detachButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                if ( dialog == null || !dialog.isVisible() ) {
                    onUndock();
                }
                else {
                    onDock();
                }
            }
        } );

        closeButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                onClose();
            }
        } );

        buttonPanelPSwing = new PSwing( buttonPanel );

        child.addScreenChild( buttonPanelPSwing );

        add( placeholder );

        updateButtonLocations();
    }

    private void updateButtonLocations() {
        buttonPanelPSwing.setOffset( 3, 3 );
    }

    private void setWindowed() {
        if ( dialog == null ) {
            createDialog();
        }

        buttonPanel.add( closeButton );

        dialog.pack();
        int w = dialog.getWidth();
        Point togo = this.getLocationOnScreen();
        togo.x -= w;
        dialog.setLocation( togo );
        dialogContentPane.add( child );
        dialog.setVisible( true );
        updateButtonLocations();
    }

    private void createDialog() {
        NaturalSelectionModule module = NaturalSelectionModule.getModule();
        if ( module != null ) {
            framer = module.getParentFrame();
        }
        dialog = new JDialog( framer, title );
        dialog.setResizable( false );
        dialogContentPane = new JPanel( null );

        Dimension preferredSize = new Dimension( child.getWidth(), child.getHeight() );
        dialogContentPane.setSize( preferredSize );
        dialogContentPane.setPreferredSize( preferredSize );
        dialogContentPane.add( child );

        dialog.setContentPane( dialogContentPane );
        dialog.addWindowListener( new WindowAdapter() {
            public void windowClosing( WindowEvent e ) {
                onClose();
            }
        } );
    }

    private void closeDialog() {
        buttonPanel.remove( closeButton );

        dialog.setVisible( false );
        updateButtonLocations();
        Window w = SwingUtilities.getWindowAncestor( this );
        if ( w instanceof JFrame ) {
            JFrame frame = (JFrame) w;

            frame.invalidate();
            frame.validate();
            frame.repaint();
        }

    }

    public void setChildVisible() {
        removeAll();
        add( child );
        repaint();
        child.invalidate();
        validate();
    }

    public void setPlaceholderVisible() {
        removeAll();
        add( placeholder );
        repaint();
        placeholder.invalidate();
        validate();
    }

    private void onDock() {
        closeDialog();

        remove( placeholder );
        add( child );

        child.invalidate();
        repaint();
        validate();

        for ( Listener listener : listeners ) {
            listener.onDock();
        }
    }

    private void onUndock() {
        remove( child );
        setWindowed();
        add( placeholder );

        placeholder.invalidate();
        repaint();
        validate();

        for ( Listener listener : listeners ) {
            listener.onUndock();
        }
    }

    private void onClose() {
        closeDialog();
        for ( Listener listener : listeners ) {
            listener.onClose();
        }
    }

    public void addListener( Listener listener ) {
        listeners.add( listener );
    }

    public void removeListener( Listener listener ) {
        listeners.remove( listener );
    }

    public static interface Listener {
        public void onDock();

        public void onUndock();

        public void onClose();
    }

    public static PhetPCanvas createExampleCanvas() {
        PhetPCanvas canvas = new PhetPCanvas( new Dimension( 100, 100 ) );
        PPath path;

        path = PPath.createRectangle( 0, 0, 50, 50 );
        path.setPaint( Color.RED );
        canvas.addWorldChild( path );

        path = PPath.createRectangle( 50, 0, 50, 50 );
        path.setPaint( Color.MAGENTA );
        canvas.addWorldChild( path );

        path = PPath.createRectangle( 0, 50, 50, 50 );
        path.setPaint( Color.YELLOW );
        canvas.addWorldChild( path );

        path = PPath.createRectangle( 50, 50, 100, 50 );
        path.setPaint( Color.BLACK );
        canvas.addWorldChild( path );
        canvas.setPreferredSize( new Dimension( 200, 200 ) );

        canvas.setBorder( new LineBorder( Color.BLUE ) );

        return canvas;
    }

    private static Frame framer;

    private static void init() {
        JFrame frame = new JFrame( "Detachable Demo" );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

        JPanel container = new JPanel( new GridLayout( 1, 3 ) );
        frame.setContentPane( container );

        framer = frame;

        final DetachOptionPanel bar = new DetachOptionPanel( "Pedigree Chart", createExampleCanvas(), new JLabel( "Placeholder" ) );
        final JRadioButton radioPlaceholder = new JRadioButton( "Placeholder" );
        final JRadioButton radioPedigree = new JRadioButton( "Pedigree" );

        ButtonGroup group = new ButtonGroup();
        group.add( radioPlaceholder );
        group.add( radioPedigree );

        radioPlaceholder.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent actionEvent ) {
                System.out.println( "Placeholder!" );
                bar.setPlaceholderVisible();
            }
        } );

        radioPedigree.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent actionEvent ) {
                System.out.println( "Pedigree!" );
                bar.setChildVisible();
            }
        } );

        bar.addListener( new Listener() {
            public void onDock() {
                radioPlaceholder.setEnabled( true );
                radioPedigree.setEnabled( true );
                radioPedigree.setSelected( true );
            }

            public void onUndock() {
                radioPlaceholder.setEnabled( false );
                radioPedigree.setEnabled( false );
                radioPlaceholder.setSelected( true );
            }

            public void onClose() {
                radioPlaceholder.setEnabled( true );
                radioPedigree.setEnabled( true );
            }
        } );

        container.setLayout( new GridBagLayout() );

        GridBagConstraints statsConstraints = new GridBagConstraints();
        statsConstraints.gridx = 1;
        statsConstraints.gridy = 0;
        statsConstraints.gridwidth = 2;
        statsConstraints.fill = GridBagConstraints.BOTH;
        statsConstraints.anchor = GridBagConstraints.NORTH;
        statsConstraints.weightx = 1.0;
        statsConstraints.weighty = 1.0;
        container.add( bar, statsConstraints );
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        container.add( radioPlaceholder, c );
        c.gridx = 3;
        container.add( radioPedigree, c );

        //radioPedigree.setSelected( true );
        radioPlaceholder.setSelected( true );

        frame.pack();

        frame.setVisible( true );
    }

    public static void main( String[] args ) {
        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
                init();
            }
        } );
    }
}
