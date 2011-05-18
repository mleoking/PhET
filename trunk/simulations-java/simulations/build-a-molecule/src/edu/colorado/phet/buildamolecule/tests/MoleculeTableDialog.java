// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.buildamolecule.tests;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.*;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

import edu.colorado.phet.buildamolecule.model.CompleteMolecule;
import edu.colorado.phet.buildamolecule.model.MoleculeList;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.umd.cs.piccolo.PNode;

/**
 * @author Sam Reid
 * @author Jon Olson
 */
public class MoleculeTableDialog extends JDialog {
    public MoleculeTableDialog( final Frame owner ) {
        super( owner, false );
        final List<CompleteMolecule> list = new ArrayList<CompleteMolecule>( MoleculeList.getMasterInstance().getAllCompleteMolecules() );
        Collections.sort( list, new Comparator<CompleteMolecule>() {
            public int compare( CompleteMolecule o1, CompleteMolecule o2 ) {
                int sizeCompare = Double.compare( o1.getAtoms().size(), o2.getAtoms().size() );
                if ( sizeCompare == 0 ) {
                    return o1.getGeneralFormula().compareToIgnoreCase( o2.getGeneralFormula() );
                }
                return sizeCompare;
            }
        } );

//        final int[] rowHeights = new int[list.size()];
//        for ( int row = 0; row < list.size(); row++ ) {
//            CompleteMolecule molecule = list.get( row );
//            PNode node = molecule.createPseudo3DNode();
//            rowHeights[row] = (int) Math.max( 20, node.getHeight() + 10 );
//        }

        JTable table = new JTable( new TableModel() {
            public int getRowCount() {
                return list.size();
            }

            public int getColumnCount() {
                return 4;
            }

            public String getColumnName( int columnIndex ) {
                switch( columnIndex ) {
                    case 0:
                        return "Name";
                    case 1:
                        return "Formula";
                    case 2:
                        return "Pseudo-3D";
                    case 3:
                        return "PubChem CID";
                    default:
                        throw new RuntimeException( "unknown column: " + columnIndex );
                }
            }

            public Class<?> getColumnClass( int columnIndex ) {
                if ( columnIndex == 2 ) {
                    return MoleculeCellRenderer.class;
                }
                return JLabel.class;
//                if ( columnIndex == 0 ) {
//                    return JLabel.class;
//                }
//                else {
//                    throw new RuntimeException( "unknown column: " + columnIndex );
//                }
            }

            public boolean isCellEditable( int rowIndex, int columnIndex ) {
                return false;
            }

            public Object getValueAt( int rowIndex, int columnIndex ) {
                switch( columnIndex ) {
                    case 0:
                        return list.get( rowIndex ).getDisplayName();
                    case 1:
                        return "<html>" + list.get( rowIndex ).getGeneralFormulaFragment() + "</html>";
                    case 2:
                        return list.get( rowIndex );
                    case 3:
                        return String.valueOf( list.get( rowIndex ).cid );
                    default:
                        throw new RuntimeException( "unknown column: " + columnIndex );
                }
            }

            public void setValueAt( Object aValue, int rowIndex, int columnIndex ) {
            }

            public void addTableModelListener( TableModelListener l ) {
            }

            public void removeTableModelListener( TableModelListener l ) {
            }
        } ) {
            {
                setRowHeight( 75 );
            }
//            @Override
//            public int getRowHeight( int row ) {
//                return rowHeights[row];
//            }
        };

        table.getColumnModel().getColumn( 2 ).setCellRenderer( new MoleculeCellRenderer() );

        setContentPane( new JScrollPane( table ) );
        pack();
        setSize( new Dimension( 800, 600 ) );
        SwingUtils.centerInParent( this );
    }

    public static void main( String[] args ) {
        new MoleculeTableDialog( null ) {{
            setDefaultCloseOperation( DISPOSE_ON_CLOSE );
        }}.setVisible( true );
    }

    private static class MoleculeCellRenderer extends PhetPCanvas implements TableCellRenderer {
        private PNode node = null;

        private MoleculeCellRenderer() {
            setBorder( null );
            setBackground( new JPanel().getBackground() );
        }

        public Component getTableCellRendererComponent( JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column ) {
            if ( node != null ) {
                removeScreenChild( node );
            }

            node = ( (CompleteMolecule) value ).createPseudo3DNode();
            final Dimension preferredSize = new Dimension( (int) node.getFullBounds().getWidth() + 1, (int) node.getFullBounds().getHeight() + 1 );
            setPreferredSize( preferredSize );
            node.centerFullBoundsOnPoint( preferredSize.getWidth() / 2, preferredSize.getHeight() / 2 );
            addScreenChild( node );

            this.paintImmediately();

            return this;
        }
    }
}

