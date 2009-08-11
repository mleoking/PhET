package edu.colorado.phet.acidbasesolutions.control;

import java.awt.Color;
import java.awt.Frame;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JPanel;

import edu.colorado.phet.acidbasesolutions.ABSStrings;
import edu.colorado.phet.acidbasesolutions.dialog.SymbolLegendDialog;
import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * Miscellaneous controls.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class MiscControlsNode extends PNode {
    
    private final Frame parentFrame;
    private final JCheckBox symbolLegendCheckBox;
    
    private JDialog symbolLegendDialog;
    private Point symbolLegendDialogLocation;
    
    public MiscControlsNode( final PNode concentrationGraphNode, Color background ) {
        super();
        
        parentFrame = PhetApplication.getInstance().getPhetFrame();
        
        symbolLegendCheckBox = new JCheckBox( ABSStrings.CHECK_BOX_SYMBOL_LEGEND );
        symbolLegendCheckBox.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                updateSymbolLegendDialog();
            }
        });
        
        // layout
        JPanel panel = new JPanel();
        EasyGridBagLayout layout = new EasyGridBagLayout( panel );
        layout.setInsets( new Insets( 0, 0, 0, 0 ) );
        panel.setLayout( layout );
        int row = 0;
        int column = 0;
        layout.addComponent( symbolLegendCheckBox, row++, column );
        
        SwingUtils.setBackgroundDeep( panel, background );
        
        addChild( new PSwing( panel ) );
    }
    
    public void setSymbolLegendSelected( boolean b ) {
        symbolLegendCheckBox.setSelected( b );
        updateSymbolLegendDialog();
    }
    
    public boolean isSymbolLegendSelected() {
        return symbolLegendCheckBox.isSelected();
    }
    
    private void updateSymbolLegendDialog() {
        if ( symbolLegendCheckBox.isSelected() ) {
            openSymbolLegendDialog();
        }
        else {
            closeSymbolLegendDialog();
        }
    }
    
    private void openSymbolLegendDialog() {
        
        symbolLegendDialog = new SymbolLegendDialog( parentFrame );
        symbolLegendDialog.addWindowListener( new WindowAdapter() {

            // called when the close button in the dialog's window dressing is clicked
            public void windowClosing( WindowEvent e ) {
                closeSymbolLegendDialog();
            }

            // called by JDialog.dispose
            public void windowClosed( WindowEvent e ) {
                closeSymbolLegendDialog();
            }
        } );
        
        if ( symbolLegendDialogLocation == null ) {
            SwingUtils.centerDialogInParent( symbolLegendDialog );
        }
        else {
            symbolLegendDialog.setLocation( symbolLegendDialogLocation );
        }
        symbolLegendDialog.setVisible( true );
    }
    
    private void closeSymbolLegendDialog() {
        if ( symbolLegendDialog != null ) {
            symbolLegendDialogLocation = symbolLegendDialog.getLocation();
            symbolLegendDialog.dispose();
            symbolLegendDialog = null;
            symbolLegendCheckBox.setSelected( false );
        }
    }
}
