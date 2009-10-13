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

import edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.naturalselection.NaturalSelectionConstants;
import edu.colorado.phet.naturalselection.NaturalSelectionResources;
import edu.colorado.phet.naturalselection.dialog.PedigreeChartCanvas;
import edu.colorado.phet.naturalselection.module.NaturalSelectionModule;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * Holds two children, which can be switched between:
 * <p/>
 * One is a piccolo canvas with an overlaid "detach" button that will toggle the child from being displayed in this
 * panel and a separate dialog.
 * <p/>
 * The other is an arbitrary swing component that is not detachable, and is displayed when the other child is detached.
 * <p/>
 * Somewhat adapted from the detaching mechanism in forces-1d (and uses the same buttons)
 *
 * @author Jonathan Olson
 */
public class DetachOptionPanel extends JPanel {
    private PhetPCanvas detachableChild;
    private Component staticChild;

    private List<Listener> listeners = new LinkedList<Listener>();

    private JDialog dialog;
    private JPanel dialogContentPane;
    private PSwing buttonPanelPSwing;

    private String title;
    private JButton closeButton;
    private JPanel buttonPanel;

    public DetachOptionPanel( String title, PhetPCanvas detachableChild, Component staticChild ) {
        // use up all available space
        super( new GridLayout( 1, 1 ) );

        this.detachableChild = detachableChild;
        this.staticChild = staticChild;
        this.title = title;

        // initialize the button panel that will be overlaid on the detachable child

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

        detachableChild.addScreenChild( buttonPanelPSwing );

        add( staticChild );

        /* NOTE: not adding the close button. It will only be added when the detachable child is in its own window, and
           will be removed again when reattached or closed
        */

        updateButtonLocations();
    }

    public void showDetachableChild() {
        removeAll();
        add( detachableChild );
        repaint();
        detachableChild.invalidate();
        validate();
    }

    public void showStaticChild() {
        removeAll();
        add( staticChild );
        repaint();
        staticChild.invalidate();
        validate();
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
        dialog.setLocation( this.getLocationOnScreen() );
        dialogContentPane.add( detachableChild );
        dialog.setVisible( true );
        if ( detachableChild instanceof PedigreeChartCanvas ) {
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {
                    ( (PedigreeChartCanvas) detachableChild ).setCenterPoint( 0 );
                }
            } );
        }
        updateButtonLocations();
    }

    private void createDialog() {
        NaturalSelectionModule module = NaturalSelectionModule.getModule();
        dialog = new JDialog( module.getParentFrame(), title );
        dialog.setResizable( true );
        dialogContentPane = new JPanel( new GridLayout( 1, 1 ) );

        Dimension preferredSize = detachableChild.getPreferredSize();
        dialogContentPane.setSize( preferredSize );
        dialogContentPane.setPreferredSize( preferredSize );
        dialogContentPane.add( detachableChild );

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

    /**
     * Called when the detachable child is docked back into the panel
     */
    private void onDock() {
        closeDialog();

        remove( staticChild );
        add( detachableChild );

        detachableChild.invalidate();

        validate();

        if ( detachableChild instanceof PedigreeChartCanvas ) {
            ( (PedigreeChartCanvas) detachableChild ).setCenterPoint( 0 );
        }

        repaint();

        for ( Listener listener : listeners ) {
            listener.onDock();
        }
    }

    /**
     * Called when the detachable child is detached
     */
    private void onUndock() {
        remove( detachableChild );
        setWindowed();
        add( staticChild );

        staticChild.invalidate();
        repaint();
        validate();

        for ( Listener listener : listeners ) {
            listener.onUndock();
        }
    }

    /**
     * Called when the detached child is closed
     */
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
        /**
         * Called when the detachable child is docked back into the panel
         */
        public void onDock();

        /**
         * Called when the detachable child is detached
         */
        public void onUndock();

        /**
         * Called when the detached child is closed
         */
        public void onClose();
    }

}
