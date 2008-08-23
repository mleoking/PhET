/*  */
package edu.colorado.phet.forces1d.view;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.*;

import edu.colorado.phet.forces1d.Force1DResources;
import edu.colorado.phet.forces1d.Forces1DModule;
import edu.colorado.phet.forces1d.phetcommon.view.ControlPanel;
import edu.colorado.phet.forces1d.phetcommon.view.util.BufferedImageUtils;
import edu.colorado.phet.forces1d.phetcommon.view.util.ImageLoader;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * User: Sam Reid
 * Date: Jan 16, 2005
 * Time: 8:11:43 PM
 */
public class FreeBodyDiagramSuite {
    private FreeBodyDiagramPanel freeBodyDiagramPanel;
    private Forces1DModule module;
    private JDialog dialog;
    private JPanel dialogContentPane;
    private ControlPanel controlPanel;
    private int dialogInsetX;
    private int dialogInsetY;
    private JPanel buttonPanel;
    private PSwing buttonPanelPSwing;
    private boolean panelVisible;

    public FreeBodyDiagramSuite( final Forces1DModule module ) {
        this.module = module;
        freeBodyDiagramPanel = new FreeBodyDiagramPanel( module );
        buttonPanel = new JPanel( new FlowLayout( FlowLayout.CENTER, 1, 1 ) );

        BufferedImage tearImage = null;
        try {
            tearImage = ImageLoader.loadBufferedImage( "forces-1d/images/tear-20.png" );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
        BufferedImage xImage = null;
        try {
            xImage = ImageLoader.loadBufferedImage( "forces-1d/images/x-20.png" );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }

        JButton tearButton = new JButton( new ImageIcon( BufferedImageUtils.rescaleYMaintainAspectRatio( buttonPanel, tearImage, 14 ) ) );
        JButton closeButton = new JButton( new ImageIcon( BufferedImageUtils.rescaleYMaintainAspectRatio( buttonPanel, xImage, 14 ) ) );

        tearButton.setMargin( new Insets( 2, 2, 2, 2 ) );
        closeButton.setMargin( new Insets( 2, 2, 2, 2 ) );//todo will look bad on mac

        buttonPanel.add( tearButton );
        buttonPanel.add( closeButton );

        tearButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                if ( dialog == null || !dialog.isVisible() ) {
                    setWindowed();
                }
                else {
                    closeDialog();
                }
            }
        } );

        closeButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                setPanelVisible( false );
            }
        } );

//        getFreeBodyDiagramPanel().add( buttonPanel );
        buttonPanelPSwing = new PSwing( buttonPanel );
        getFreeBodyDiagramPanel().addScreenChild( buttonPanelPSwing );
        updateButtonLocations();

        setPanelVisible( false );
    }

    public FreeBodyDiagramPanel getFreeBodyDiagramPanel() {
        return freeBodyDiagramPanel;
    }

    public boolean isFBDVisible() {
        return panelVisible;
    }

    public void setPanelVisible( boolean b ) {
        getFreeBodyDiagramPanel().setVisible( b );
        updateButtonLocations();
        this.panelVisible = b;
        notifyListener();
    }

    public static interface Listener {
        void visibilityChanged();
    }

    private ArrayList listeners = new ArrayList();

    public void addListener( Listener listener ) {
        listeners.add( listener );
    }

    public void notifyListener() {
        for ( int i = 0; i < listeners.size(); i++ ) {
            ( (Listener) listeners.get( i ) ).visibilityChanged();
        }
    }

    private void updateButtonLocations() {
        int width = (int) getFreeBodyDiagramPanel().getPreferredSize().getWidth();
        buttonPanelPSwing.setOffset( width - 3 - buttonPanelPSwing.getFullBounds().getWidth(), 3 );
    }

    private void setWindowed() {
        if ( dialog == null ) {
            createDialog();
        }

        dialog.pack();
        int w = dialog.getWidth();
        Point togo = controlPanel.getLocationOnScreen();
        togo.x -= w;
        dialog.setLocation( togo );
        dialogContentPane.add( getFreeBodyDiagramPanel() );
        getFreeBodyDiagramPanel().setLocation( dialogInsetX, dialogInsetY );
        dialog.setVisible( true );
        controlPanel.invalidate();
        controlPanel.doLayout();
        controlPanel.validate();
        updateButtonLocations();
    }

    private void createDialog() {
        dialog = new JDialog( module.getPhetFrame(), Force1DResources.get( "FreeBodyDiagramSuite.freeBodyDiagram" ) );
        dialog.setResizable( false );
        dialogContentPane = new JPanel( null );

        dialogInsetX = 15;
        dialogInsetY = 15;

        FreeBodyDiagramPanel windowAP = getFreeBodyDiagramPanel();
        Dimension preferredSize = new Dimension( windowAP.getWidth() + dialogInsetX * 2, windowAP.getHeight() + dialogInsetY * 2 );
        dialogContentPane.setSize( preferredSize );
        dialogContentPane.setPreferredSize( preferredSize );
        dialogContentPane.add( windowAP );
        windowAP.setLocation( dialogInsetX, dialogInsetY );

        dialog.setContentPane( dialogContentPane );
        dialog.addWindowListener( new WindowAdapter() {
            public void windowClosing( WindowEvent e ) {
                closeDialog();
            }
        } );
    }

    private void closeDialog() {
        controlPanel.getControlPane().setGridY( 1 );//todo Magic number.
        controlPanel.addControl( getFreeBodyDiagramPanel() );
        dialog.setVisible( false );
        updateButtonLocations();
        Window w = SwingUtilities.getWindowAncestor( controlPanel );
        if ( w instanceof JFrame ) {
            JFrame frame = (JFrame) w;

            frame.invalidate();
            frame.validate();
            frame.repaint();
        }

    }

    public void setControlPanel( ControlPanel controlPanel ) {
        this.controlPanel = controlPanel;
    }

    public void reset() {
        freeBodyDiagramPanel.reset();
    }

    public void handleUserInput() {
        getFreeBodyDiagramPanel().handleUserInput();
    }

    public void updateGraphics() {
        freeBodyDiagramPanel.updateGraphics();
    }

    public void controlsChanged() {
        updateButtonLocations();
    }
}
