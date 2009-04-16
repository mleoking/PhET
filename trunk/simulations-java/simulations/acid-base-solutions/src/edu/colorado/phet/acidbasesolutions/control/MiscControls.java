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

import edu.colorado.phet.acidbasesolutions.dialog.EquilibriumExpressionsDialog;
import edu.colorado.phet.acidbasesolutions.dialog.SymbolLegendDialog;
import edu.colorado.phet.acidbasesolutions.view.ConcentrationsGraphNode;
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
    private JDialog _equilibriumExpressionsDialog;
    private Point _equilibriumExpressionsDialogLocation;
    private JDialog _reactionEquationsDialog;
    private Point _reactionEquationsDialogLocation;
    
    public MiscControls( final ConcentrationsGraphNode concentrationsGraphNode ) {
        super();
        _parentFrame = PhetApplication.getInstance().getPhetFrame();
        
        // border
        TitledBorder border = new TitledBorder( new LineBorder( Color.BLACK, 2 ), TITLE );
        border.setTitleFont( new PhetFont( Font.BOLD, 16 ) );
        setBorder( border );
        
        _concentrationGraphCheckBox = new JCheckBox( CONCENTRATION_GRAPH );
        _concentrationGraphCheckBox.setSelected( concentrationsGraphNode.isVisible() );
        _concentrationGraphCheckBox.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                concentrationsGraphNode.setVisible( _concentrationGraphCheckBox.isSelected() );
            }
        } );
        
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
        _equilibriumExpressionsCheckBox.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                if ( _equilibriumExpressionsCheckBox.isSelected() ) {
                    openEquilibriumExpressionsDialog();
                }
                else {
                    closeEquilibriumExpressionsDialog();
                }
            }
        });
        
        _reactionEquationsCheckBox = new JCheckBox( REACTION_EQUATIONS );
        _reactionEquationsCheckBox.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                if ( _reactionEquationsCheckBox.isSelected() ) {
                    openReactionEquationsDialog();
                }
                else {
                    closeReactionEquationsDialog();
                }
            }
        });
        
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
        _symbolLegendDialogLocation = closeDialog( _symbolLegendDialog );
        _symbolLegendDialog = null;
    }
    
    private void openEquilibriumExpressionsDialog() {
        
        _equilibriumExpressionsDialog = new EquilibriumExpressionsDialog( _parentFrame );
        _equilibriumExpressionsDialog.addWindowListener( new WindowAdapter() {

            // called when the close button in the dialog's window dressing is clicked
            public void windowClosing( WindowEvent e ) {
                closeEquilibriumExpressionsDialog();
            }

            // called by JDialog.dispose
            public void windowClosed( WindowEvent e ) {
                _equilibriumExpressionsDialog = null;
                _equilibriumExpressionsCheckBox.setSelected( false );
            }
        } );
        
        if ( _equilibriumExpressionsDialogLocation == null ) {
            SwingUtils.centerDialogInParent( _equilibriumExpressionsDialog );
        }
        else {
            _equilibriumExpressionsDialog.setLocation( _equilibriumExpressionsDialogLocation );
        }
        _equilibriumExpressionsDialog.setVisible( true );
    }
    
    private void closeEquilibriumExpressionsDialog() {
        _equilibriumExpressionsDialogLocation = closeDialog( _equilibriumExpressionsDialog );
        _equilibriumExpressionsDialog = null;
    }
    
    private void openReactionEquationsDialog() {
        
        _reactionEquationsDialog = new EquilibriumExpressionsDialog( _parentFrame );
        _reactionEquationsDialog.addWindowListener( new WindowAdapter() {

            // called when the close button in the dialog's window dressing is clicked
            public void windowClosing( WindowEvent e ) {
                closeReactionEquationsDialog();
            }

            // called by JDialog.dispose
            public void windowClosed( WindowEvent e ) {
                _reactionEquationsDialog = null;
                _reactionEquationsCheckBox.setSelected( false );
            }
        } );
        
        if ( _reactionEquationsDialogLocation == null ) {
            SwingUtils.centerDialogInParent( _reactionEquationsDialog );
        }
        else {
            _reactionEquationsDialog.setLocation( _reactionEquationsDialogLocation );
        }
        _reactionEquationsDialog.setVisible( true );
    }
    
    private void closeReactionEquationsDialog() {
        _reactionEquationsDialogLocation = closeDialog( _reactionEquationsDialog );
        _reactionEquationsDialog = null;
    }
    
    private static Point closeDialog( JDialog dialog ) {
        Point location = null;
        if ( dialog != null ) {
            location = dialog.getLocation();
            dialog.dispose();
        }
        return location;
    }
}
