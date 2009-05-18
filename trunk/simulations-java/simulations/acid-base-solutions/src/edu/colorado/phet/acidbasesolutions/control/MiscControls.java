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

import edu.colorado.phet.acidbasesolutions.ABSStrings;
import edu.colorado.phet.acidbasesolutions.dialog.SymbolLegendDialog;
import edu.colorado.phet.acidbasesolutions.view.equilibriumexpressions.EquilibriumExpressionsDialog;
import edu.colorado.phet.acidbasesolutions.view.reactionequations.ReactionEquationsDialog;
import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;
import edu.umd.cs.piccolo.PNode;


public class MiscControls extends JPanel {
    
    private final Frame parentFrame;
    
    private final JCheckBox concentrationGraphCheckBox;
    private final JCheckBox symbolLegendCheckBox;
    private final JCheckBox equilibriumExpressionsCheckBox;
    private final JCheckBox reactionEquationsCheckBox;
    
    private JDialog symbolLegendDialog;
    private Point symbolLegendDialogLocation;
    private JDialog equilibriumExpressionsDialog;
    private Point equilibriumExpressionsDialogLocation;
    private JDialog reactionEquationsDialog;
    private Point reactionEquationsDialogLocation;
    
    public MiscControls( final PNode concentrationGraphNode ) {
        super();
        parentFrame = PhetApplication.getInstance().getPhetFrame();
        
        // border
        TitledBorder border = new TitledBorder( new LineBorder( Color.BLACK, 1 ), ABSStrings.TITLE_MISC_CONTROLS );
        border.setTitleFont( new PhetFont( Font.BOLD, 16 ) );
        setBorder( border );
        
        concentrationGraphCheckBox = new JCheckBox( ABSStrings.CHECK_BOX_CONCENTRATIONS_GRAPH );
        concentrationGraphCheckBox.setSelected( concentrationGraphNode.getVisible() );
        concentrationGraphCheckBox.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                concentrationGraphNode.setVisible( concentrationGraphCheckBox.isSelected() );
            }
        } );
        
        symbolLegendCheckBox = new JCheckBox( ABSStrings.CHECK_BOX_SYMBOL_LEGEND );
        symbolLegendCheckBox.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                if ( symbolLegendCheckBox.isSelected() ) {
                    openSymbolLegendDialog();
                }
                else {
                    closeSymbolLegendDialog();
                }
            }
        });
        
        equilibriumExpressionsCheckBox = new JCheckBox( ABSStrings.CHECK_BOX_EQUILIBRIUM_EXPRESSIONS );
        equilibriumExpressionsCheckBox.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                if ( equilibriumExpressionsCheckBox.isSelected() ) {
                    openEquilibriumExpressionsDialog();
                }
                else {
                    closeEquilibriumExpressionsDialog();
                }
            }
        });
        
        reactionEquationsCheckBox = new JCheckBox( ABSStrings.CHECK_BOX_REACTION_EQUATIONS );
        reactionEquationsCheckBox.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                if ( reactionEquationsCheckBox.isSelected() ) {
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
        layout.addComponent( concentrationGraphCheckBox, row++, column );
        layout.addComponent( symbolLegendCheckBox, row++, column );
        row = 0;
        column++;
        layout.addComponent( equilibriumExpressionsCheckBox, row++, column );
        layout.addComponent( reactionEquationsCheckBox, row++, column );
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
                symbolLegendDialog = null;
                symbolLegendCheckBox.setSelected( false );
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
        symbolLegendDialogLocation = closeDialog( symbolLegendDialog );
        symbolLegendDialog = null;
    }
    
    private void openEquilibriumExpressionsDialog() {
        
        equilibriumExpressionsDialog = new EquilibriumExpressionsDialog( parentFrame );
        equilibriumExpressionsDialog.addWindowListener( new WindowAdapter() {

            // called when the close button in the dialog's window dressing is clicked
            public void windowClosing( WindowEvent e ) {
                closeEquilibriumExpressionsDialog();
            }

            // called by JDialog.dispose
            public void windowClosed( WindowEvent e ) {
                equilibriumExpressionsDialog = null;
                equilibriumExpressionsCheckBox.setSelected( false );
            }
        } );
        
        if ( equilibriumExpressionsDialogLocation == null ) {
            SwingUtils.centerDialogInParent( equilibriumExpressionsDialog );
        }
        else {
            equilibriumExpressionsDialog.setLocation( equilibriumExpressionsDialogLocation );
        }
        equilibriumExpressionsDialog.setVisible( true );
    }
    
    private void closeEquilibriumExpressionsDialog() {
        equilibriumExpressionsDialogLocation = closeDialog( equilibriumExpressionsDialog );
        equilibriumExpressionsDialog = null;
    }
    
    private void openReactionEquationsDialog() {
        
        reactionEquationsDialog = new ReactionEquationsDialog( parentFrame );
        reactionEquationsDialog.addWindowListener( new WindowAdapter() {

            // called when the close button in the dialog's window dressing is clicked
            public void windowClosing( WindowEvent e ) {
                closeReactionEquationsDialog();
            }

            // called by JDialog.dispose
            public void windowClosed( WindowEvent e ) {
                reactionEquationsDialog = null;
                reactionEquationsCheckBox.setSelected( false );
            }
        } );
        
        if ( reactionEquationsDialogLocation == null ) {
            SwingUtils.centerDialogInParent( reactionEquationsDialog );
        }
        else {
            reactionEquationsDialog.setLocation( reactionEquationsDialogLocation );
        }
        reactionEquationsDialog.setVisible( true );
    }
    
    private void closeReactionEquationsDialog() {
        reactionEquationsDialogLocation = closeDialog( reactionEquationsDialog );
        reactionEquationsDialog = null;
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
