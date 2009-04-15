package edu.colorado.phet.acidbasesolutions.control;

import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import edu.colorado.phet.acidbasesolutions.dialog.SymbolLegendDialog;
import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;


public class MiscControls extends JPanel {
    
    //TODO localize
    private static final String TITLE = "View";
    private static final String CONCENTRATION_GRAPH = "Concentration Graph";
    private static final String SYMBOL_LEGEND = "Symbol Legend";
    private static final String EQUILIBRIUM_EXPRESSIONS = "Equilibrium Expressions";
    private static final String REACTION_EQUATIONS = "Reaction Equations";

    private final Frame _parentFrame;
    
    private final JCheckBox _concentrationGraphCheckBox;
    private final JCheckBox _symbolLegendCheckBox;
    private final JCheckBox _equilibriumExpressionsCheckBox;
    private final JCheckBox _reactionEquationsCheckBox;
    
    private JDialog _symbolLegendDialog;
    private Point _symbolLegendDialogLocation;
    
    public MiscControls() {
        super();
        _parentFrame = PhetApplication.getInstance().getPhetFrame();
        
        // border
        TitledBorder border = new TitledBorder( new LineBorder( Color.BLACK, 2 ), TITLE );
        border.setTitleFont( new PhetFont( Font.BOLD, 16 ) );
        setBorder( border );
        
        _concentrationGraphCheckBox = new JCheckBox( CONCENTRATION_GRAPH );
        
        _symbolLegendCheckBox = new JCheckBox( SYMBOL_LEGEND );
        _symbolLegendCheckBox.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                if ( _symbolLegendCheckBox.isSelected() ) {
                    openSymbolLegendDialog();
                }
                else {
                    closeSymbolLegendDialog();
                }
            }
        });
        
        _equilibriumExpressionsCheckBox = new JCheckBox( EQUILIBRIUM_EXPRESSIONS );
        
        _reactionEquationsCheckBox = new JCheckBox( REACTION_EQUATIONS );
        
        // layout
        EasyGridBagLayout layout = new EasyGridBagLayout( this );
        this.setLayout( layout );
        int row = 0;
        int column = 0;
        layout.addComponent( _concentrationGraphCheckBox, row++, column );
        layout.addComponent( _symbolLegendCheckBox, row++, column );
        row = 0;
        column++;
        layout.addComponent( _equilibriumExpressionsCheckBox, row++, column );
        layout.addComponent( _reactionEquationsCheckBox, row++, column );
    }
    
    private void openSymbolLegendDialog() {
        
        closeSymbolLegendDialog();
        
        _symbolLegendDialog = new SymbolLegendDialog( _parentFrame );
        _symbolLegendDialog.addWindowListener( new WindowAdapter() {

            // called when the close button in the dialog's window dressing is clicked
            public void windowClosing( WindowEvent e ) {
                closeSymbolLegendDialog();
            }

            // called by JDialog.dispose
            public void windowClosed( WindowEvent e ) {
                _symbolLegendDialog = null;
                _symbolLegendCheckBox.setSelected( false );
            }
        } );
        
        if ( _symbolLegendDialogLocation == null ) {
            SwingUtils.centerDialogInParent( _symbolLegendDialog );
        }
        else {
            _symbolLegendDialog.setLocation( _symbolLegendDialogLocation );
        }
        _symbolLegendDialog.setVisible( true );
    }
    
    private void closeSymbolLegendDialog() {
        if ( _symbolLegendDialog != null ) {
            _symbolLegendDialogLocation = _symbolLegendDialog.getLocation();
            _symbolLegendDialog.dispose();
            _symbolLegendDialog = null;
        }
    }
}
