/*  */
package edu.colorado.phet.forces1d.view;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.forces1d.phetcommon.view.ApparatusPanel2;
import edu.colorado.phet.forces1d.phetcommon.view.ControlPanel;
import edu.colorado.phet.forces1d.phetcommon.view.phetcomponents.PhetJComponent;
import edu.colorado.phet.forces1d.phetcommon.view.phetgraphics.PhetGraphic;
import edu.colorado.phet.forces1d.phetcommon.view.util.BufferedImageUtils;
import edu.colorado.phet.forces1d.phetcommon.view.util.ImageLoader;
import edu.colorado.phet.forces1d.Force1DResources;
import edu.colorado.phet.forces1d.Forces1DModule;

/**
 * User: Sam Reid
 * Date: Jan 16, 2005
 * Time: 8:11:43 PM
 */
public class FreeBodyDiagramSuite {
    private FreeBodyDiagramPanel fbdPanel;
    private JCheckBox checkBox;
    private Forces1DModule module;
    private JDialog dialog;
    private JPanel dialogContentPane;
    private ControlPanel controlPanel;
    private int dialogInsetX;
    private int dialogInsetY;
    public PhetGraphic buttonPanelGraphic;
    public ApparatusPanel2 fbdApparatusPanel;

    public FreeBodyDiagramSuite( final Forces1DModule module ) {
        this.module = module;
        fbdPanel = new FreeBodyDiagramPanel( module );
        checkBox = new JCheckBox( Force1DResources.get( "FreeBodyDiagramSuite.freeBodyDiagram" ), true );
        checkBox.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                boolean showFBD = checkBox.isSelected();
                fbdPanel.setVisible( showFBD );
                if ( showFBD ) {
                    checkBox.setVisible( false );
                }
            }
        } );

        try {
            final JPanel buttonPanel = new JPanel( new FlowLayout( FlowLayout.CENTER, 1, 1 ) );

            fbdApparatusPanel = this.fbdPanel.getFBDPanel();
            fbdApparatusPanel.setLayout( null );
            BufferedImage tearImage = ImageLoader.loadBufferedImage( "forces-1d/images/tear-20.png" );
            BufferedImage xImage = ImageLoader.loadBufferedImage( "forces-1d/images/x-20.png" );

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
                    fbdApparatusPanel.setVisible( false );
                    checkBox.setVisible( true );
                    checkBox.setSelected( false );
                }
            } );
            buttonPanelGraphic = PhetJComponent.newInstance( fbdApparatusPanel, buttonPanel );
            fbdApparatusPanel.addGraphic( buttonPanelGraphic, Double.POSITIVE_INFINITY );
            Dimension panelDim = buttonPanel.getPreferredSize();
            buttonPanel.setBounds( 0, 0, panelDim.width, panelDim.height );
            updateButtons();
            fbdApparatusPanel.addComponentListener( new ComponentAdapter() {
                public void componentResized( ComponentEvent e ) {
                    updateButtons();
                }

                public void componentShown( ComponentEvent e ) {
                    updateButtons();
                }

            } );

//            PhetShapeGraphic phetShapeGraphic = new PhetShapeGraphic( getFBDPanel(), new Rectangle( 15, 15 ), Color.blue );
//            fbdApparatusPanel.addGraphic( phetShapeGraphic, Double.POSITIVE_INFINITY );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
        checkBox.setVisible( false );

    }

    private void updateButtons() {
        reshapeTopRight( fbdApparatusPanel, buttonPanelGraphic, 3, 3 );
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
        dialogContentPane.add( fbdPanel.getFBDPanel() );
        fbdPanel.getFBDPanel().setLocation( dialogInsetX, dialogInsetY );
        dialog.setVisible( true );
        controlPanel.invalidate();
        controlPanel.doLayout();
        controlPanel.validate();
        updateButtons();
    }

    private void createDialog() {
        dialog = new JDialog( module.getPhetFrame(), Force1DResources.get( "FreeBodyDiagramSuite.freeBodyDiagram" ) );
        dialog.setResizable( false );
        dialogContentPane = new JPanel( null );

        dialogInsetX = 15;
        dialogInsetY = 15;

        JPanel windowAP = fbdPanel.getFBDPanel();
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
        controlPanel.addControl( fbdPanel.getFBDPanel() );
        dialog.setVisible( false );
        updateButtons();
        Window w = SwingUtilities.getWindowAncestor( controlPanel );
        if ( w instanceof JFrame ) {
            JFrame frame = (JFrame) w;

            frame.invalidate();
            frame.validate();
            frame.repaint();
        }

    }

    public Component getCheckBox() {
        return checkBox;
    }

    public void setControlPanel( ControlPanel controlPanel ) {
        this.controlPanel = controlPanel;
    }

    public void reshapeTopRight( JComponent container, PhetGraphic movable, int dx, int dy ) {
        int w = container.getWidth();
        Dimension d = movable.getSize();
        int x = w - d.width - dx;
        int y = 0 + dy;
        movable.setLocation( x, y );//, d.width, d.height );
    }

    public void reset() {
        fbdPanel.reset();
    }

    public void handleUserInput() {
        fbdPanel.getFBDPanel().handleUserInput();
    }

    public void updateGraphics() {
        fbdPanel.updateGraphics();
    }

    public Component getFBDPanel() {
        return fbdPanel.getFBDPanel();
    }

    public void controlsChanged() {
        updateButtons();
    }
}
