// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.micro.view.periodictable;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Stroke;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.SwingUtilities;

import edu.colorado.phet.common.phetcommon.dialogs.ColorChooserFactory;
import edu.colorado.phet.common.phetcommon.dialogs.ColorChooserFactory.Listener;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.periodictable.CellFactory;
import edu.colorado.phet.common.piccolophet.nodes.periodictable.PeriodicTableNode.BasicElementCell;
import edu.colorado.phet.common.piccolophet.nodes.periodictable.PeriodicTableNode.ElementCell;

import static java.awt.Color.black;
import static java.awt.Color.pink;

/**
 * Rules for painting metal Elements in the periodic table differently than nonmetals since that is a major learning goal of this tab.
 *
 * @author Sam Reid
 */
public class HighlightMetals implements CellFactory {

    //Atomic numbers of the nonmetals to be indicated in the periodic table
    private ArrayList<Integer> nonmetals = new ArrayList<Integer>( Arrays.asList( 1, 2,
                                                                                  5, 6, 7, 8, 9, 10,
                                                                                  14, 15, 16, 17, 18,
                                                                                  33, 34, 35, 36,
                                                                                  52, 53, 54,
                                                                                  85, 86,
                                                                                  118 ) );

    //Atoms selected by the user
    private final List<Integer> selectedAtomicMasses;

    //Allow the developer to choose a different highlighter color
    private boolean debug = false;

    public HighlightMetals( Integer[] selectedAtomicMasses ) {
        this.selectedAtomicMasses = Arrays.asList( selectedAtomicMasses );
    }

    //Create a cell based on metal vs nonmetal and selected vs unselected
    public ElementCell createCellForElement( final int atomicNumberOfCell, Color backgroundColor ) {
        final boolean selected = selectedAtomicMasses.contains( atomicNumberOfCell );
        final Color background = nonmetals.contains( atomicNumberOfCell ) ? pink : backgroundColor;
        final PhetFont font = selected ? new PhetFont( PhetFont.getDefaultFontSize(), true ) : new PhetFont( 12 );
        final Stroke stroke = selected ? new BasicStroke( 3 ) : new BasicStroke( 1 );
        final Color strokeColor = selected ? Color.red : black;

        return new BasicElementCell( atomicNumberOfCell, background ) {
            {

                //Allow the developer to choose a different highlighter color
                if ( selected && debug ) {
                    ColorChooserFactory.showDialog( "", null, strokeColor, new Listener() {
                                                        public void colorChanged( Color color ) {
                                                            getBox().setStrokePaint( color );
                                                        }

                                                        public void ok( Color color ) {
                                                        }

                                                        public void cancelled( Color originalColor ) {
                                                        }
                                                    }, true );
                }

                getBox().setStroke( stroke );
                getText().setFont( font );
                getBox().setPaint( background );
                getBox().setStrokePaint( strokeColor );
            }

            //Wait until others are added so that moving to front will actually work, otherwise 2 sides would be clipped by nodes added after this
            @Override public void tableInitComplete() {
                super.tableInitComplete();

                //For unknown reasons, some nodes (Oxygen in sodium nitrate in sugar-and-salt solutions) get clipped if you don't schedule this for later
                if ( selectedAtomicMasses.contains( atomicNumberOfCell ) ) {
                    SwingUtilities.invokeLater( new Runnable() {
                        public void run() {
                            moveToFront();
                        }
                    } );
                }
            }
        };
    }
}