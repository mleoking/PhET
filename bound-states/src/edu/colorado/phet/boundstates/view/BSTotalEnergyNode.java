/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.boundstates.view;

import java.awt.BasicStroke;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JCheckBox;

import edu.colorado.phet.boundstates.BSConstants;
import edu.colorado.phet.boundstates.model.BSEigenstate;
import edu.colorado.phet.boundstates.model.BSTotalEnergy;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolox.pswing.PSwing;
import edu.umd.cs.piccolox.pswing.PSwingCanvas;


/**
 * BSTotalEnergyNode displays the total energy eigenstates as a set of horizontal lines.
 * The lines are pickable, and the selected eigenstate is highlighted.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class BSTotalEnergyNode extends PNode implements Observer, ActionListener {

    private BSTotalEnergy _totalEnergy;
    private BSCombinedChartNode _chartNode;
    private PSwingCanvas _canvas;
    
    private ArrayList _lines; // array of PPath
    private ArrayList _checkBoxes; // array of PSwing
    private boolean _checkBoxesVisible;
    
    private int _selectionIndex;
    private PBasicInputEventHandler _inputHandler;
    
    private Stroke _unselectedStroke;
    private Stroke _selectedStroke;
    private double _checkBoxesScale;
    
    public BSTotalEnergyNode( BSTotalEnergy totalEnergy, BSCombinedChartNode chartNode, PSwingCanvas canvas ) {
        super();
        setPickable( false ); // children are still pickable
        
        _totalEnergy = totalEnergy;
        _totalEnergy.addObserver( this );
        _chartNode = chartNode;
        _canvas = canvas;
        
        _lines = new ArrayList();
        _selectionIndex = 0;
        _checkBoxes = new ArrayList();
        _checkBoxesVisible = false;
        _checkBoxesScale = 1.0;
        
        _unselectedStroke = BSConstants.TOTAL_ENERGY_UNSELECTED_STROKE;
        _selectedStroke = BSConstants.TOTAL_ENERGY_SELECTED_STROKE;
        
        _inputHandler = new PBasicInputEventHandler() {
            public void mousePressed( PInputEvent event ) {
                PNode pickedNode = event.getPickedNode();
                int index = getLineIndex( pickedNode );
                handleSelection( index );
            }
        };

        updateDisplay();
    }

    public void cleanup() {
        if ( _totalEnergy != null ) {
            _totalEnergy.deleteObserver( this );
            _totalEnergy = null;
        }
    }
    
    public void showEigenstateCheckBoxes( boolean visible ) {
        _checkBoxesVisible = visible;
        updateDisplay();
    }
    
    public void scaleEigenstateCheckBoxes( double scale ) {
        _checkBoxesScale = scale;
        updateDisplay();
    }
    
    public void setStrokeWidth( double width ) {
        _unselectedStroke = new BasicStroke( (float) width );
        _selectedStroke = new BasicStroke( (float) width + 1 );
        updateDisplay();
    }
    
    public void update( Observable o, Object arg ) {
        if ( o == _totalEnergy ) {
            _selectionIndex = 0;
            updateDisplay();
        }
    }

    public void updateDisplay() {

        removeAllChildren();
        _lines.clear();
        _checkBoxes.clear();

        BSEigenstate[] eigenstates = _totalEnergy.getEigenstates();
        for ( int i = 0; i < eigenstates.length; i++ ) {

            // Node the draws the eigenstate line...
            double minPosition = BSConstants.POSITION_RANGE.getLowerBound();
            double maxPosition = BSConstants.POSITION_RANGE.getUpperBound();
            double energy = eigenstates[i].getEnergy();
            Point2D pLeft = _chartNode.energyToNode( new Point2D.Double( minPosition, energy ) );
            Point2D pRight = _chartNode.energyToNode( new Point2D.Double( maxPosition, energy ) );
            Point2D[] points = new Point2D[] { pLeft, pRight };
            PPath line = new PPath();
            line.setPathToPolyline( points );
            line.setStroke( _unselectedStroke );
            line.setStrokePaint( BSConstants.TOTAL_ENERGY_UNSELECTED_COLOR );
            line.addInputEventListener( _inputHandler );
            addChild( line );
            _lines.add( line );
            
            // Node that draws a checkbox on the eigenstate line...
            JCheckBox checkBox = new JCheckBox();
            checkBox.setOpaque( false );
            checkBox.addActionListener( this );
            PSwing pswing = new PSwing( _canvas, checkBox );
            pswing.scale( _checkBoxesScale );
            pswing.setVisible( _checkBoxesVisible );
            
            double x = pRight.getX() - ( (i+1) * pswing.getFullBounds().getWidth() );
            double y = pRight.getY() - ( pswing.getFullBounds().getHeight() / 2 );
            pswing.setOffset( x, y );
            addChild( pswing );
            _checkBoxes.add( checkBox );
        }
        
        // Select the lowest eigenstate by default...
        ((JCheckBox)_checkBoxes.get( _selectionIndex )).setSelected( true );
        handleSelection( _selectionIndex );
    }
    
    public void actionPerformed( ActionEvent event ) {
        if ( event.getSource() instanceof JCheckBox ) {
            JCheckBox checkBox = (JCheckBox) event.getSource();
            int index = getCheckBoxIndex( checkBox );
            if ( checkBox.isSelected() || index == _selectionIndex ) {
                handleSelection( index );
            }
        }
    }
    
    private void handleSelection( int index ) {
        deselectEigenstate( _selectionIndex );
        selectEigenstate( index );
        _selectionIndex = index;
    }
    
    private void selectEigenstate( int index ) {
        if ( index >= 0 ) {
            PPath line = (PPath) _lines.get( index );
            line.setStroke( _selectedStroke );
            line.setStrokePaint( BSConstants.TOTAL_ENERGY_SELECTED_COLOR );
            JCheckBox checkBox = (JCheckBox) _checkBoxes.get( index );
            checkBox.setSelected( true );
        }
    }
    
    private void deselectEigenstate( int index ) {
        if ( index >= 0 ) {
            PPath line = (PPath) _lines.get( index );
            line.setStroke( _unselectedStroke );
            line.setStrokePaint( BSConstants.TOTAL_ENERGY_UNSELECTED_COLOR );
            JCheckBox checkBox = (JCheckBox) _checkBoxes.get( index );
            checkBox.setSelected( false );
        }
    }
    
    private int getCheckBoxIndex( JCheckBox checkBox ) {
        int index = -1;
        for ( int i = 0; i < _checkBoxes.size(); i++ ) {
            if ( checkBox == _checkBoxes.get( i ) ) {
                index = i;
                break;
            }
        }
        return index;
    }
    
    private int getLineIndex( PNode line ) {
        int index = -1;
        for ( int i = 0; i < _lines.size(); i++ ) {
            if ( line == _lines.get( i ) ) {
                index = i;
                break;
            }
        }
        return index;
    }
}
