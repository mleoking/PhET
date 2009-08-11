package edu.colorado.phet.acidbasesolutions.control;

import java.awt.Color;
import java.awt.Frame;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JCheckBox;
import javax.swing.JDialog;

import edu.colorado.phet.acidbasesolutions.ABSStrings;
import edu.colorado.phet.acidbasesolutions.dialog.SymbolLegendDialog;
import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * Control for opening and closing the "Symbol Legend" dialog.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class LegendControlNode extends PNode {
    
    private final Frame parentFrame;
    private final JCheckBox checkBox;
    
    private JDialog dialog;
    private Point dialogLocation;
    
    public LegendControlNode( Color background ) {
        super();
        
        parentFrame = PhetApplication.getInstance().getPhetFrame();
        
        checkBox = new JCheckBox( ABSStrings.CHECK_BOX_SYMBOL_LEGEND );
        checkBox.setOpaque( false );
        checkBox.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                updateSymbolLegendDialog();
            }
        });
        addChild( new PSwing( checkBox ) );
    }
    
    public void setSelected( boolean b ) {
        checkBox.setSelected( b );
        updateSymbolLegendDialog();
    }
    
    public boolean isSelected() {
        return checkBox.isSelected();
    }
    
    private void updateSymbolLegendDialog() {
        if ( checkBox.isSelected() ) {
            openSymbolLegendDialog();
        }
        else {
            closeSymbolLegendDialog();
        }
    }
    
    private void openSymbolLegendDialog() {
        
        dialog = new SymbolLegendDialog( parentFrame );
        dialog.addWindowListener( new WindowAdapter() {

            // called when the close button in the dialog's window dressing is clicked
            public void windowClosing( WindowEvent e ) {
                closeSymbolLegendDialog();
            }

            // called by JDialog.dispose
            public void windowClosed( WindowEvent e ) {
                closeSymbolLegendDialog();
            }
        } );
        
        if ( dialogLocation == null ) {
            SwingUtils.centerDialogInParent( dialog );
        }
        else {
            dialog.setLocation( dialogLocation );
        }
        dialog.setVisible( true );
    }
    
    private void closeSymbolLegendDialog() {
        if ( dialog != null ) {
            dialogLocation = dialog.getLocation();
            dialog.dispose();
            dialog = null;
            checkBox.setSelected( false );
        }
    }
}
