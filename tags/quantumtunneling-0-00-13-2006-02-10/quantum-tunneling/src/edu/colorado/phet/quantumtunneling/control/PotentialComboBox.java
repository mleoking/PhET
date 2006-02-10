/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.quantumtunneling.control;

import java.awt.*;
import java.awt.geom.GeneralPath;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.*;

import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.quantumtunneling.QTConstants;
import edu.colorado.phet.quantumtunneling.enum.PotentialType;


/**
 * PotentialComboBox
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class PotentialComboBox extends JComboBox {

    private static final Dimension ICON_SIZE = new Dimension( 50, 20 );
    private static final Stroke ICON_STROKE = new BasicStroke( 2f );
    private static final Color ICON_COLOR = QTConstants.POTENTIAL_ENERGY_COLOR;
    
    private static ArrayList _choices;
    


    public PotentialComboBox() {
        
        if ( _choices == null ) {
            initChoices();
        }
        
        Iterator i = _choices.iterator();
        while( i.hasNext() ) {
            addItem( i.next() );
        }
        setMaximumRowCount( _choices.size() );
        
        PotentialComboBoxRenderer renderer = new PotentialComboBoxRenderer();
        setRenderer( renderer );
    }

    public PotentialType getSelectedPotentialType() {
        return ((PotentialChoice)getSelectedItem()).getPotentialType();
    }
    
    public void setSelectedPotentialType( PotentialType potentialType ) {
        Iterator i = _choices.iterator();
        while ( i.hasNext() ) {
            PotentialChoice choice = (PotentialChoice) i.next();
            if ( choice.getPotentialType() == potentialType ) {
                setSelectedItem( choice );
                break;
            }
        }
    }
    
    private static synchronized void initChoices() {
        
        if ( _choices != null ) {
            return;
        }
        
        ImageIcon constantIcon = createConstantIcon();
        ImageIcon stepIcon = createStepIcon();
        ImageIcon singleBarrierIcon = createSingleBarrierIcon();
        ImageIcon doubleBarrierIcon = createDoubleBarrierIcon();

        PotentialChoice constantItem = new PotentialChoice( PotentialType.CONSTANT, SimStrings.get( "choice.potential.constant" ), constantIcon );
        PotentialChoice stepItem = new PotentialChoice( PotentialType.STEP, SimStrings.get( "choice.potential.step" ), stepIcon );
        PotentialChoice singleBarrierItem = new PotentialChoice( PotentialType.SINGLE_BARRIER, SimStrings.get( "choice.potential.barrier" ), singleBarrierIcon );
        PotentialChoice doubleBarrierItem = new PotentialChoice( PotentialType.DOUBLE_BARRIER, SimStrings.get( "choice.potential.double" ), doubleBarrierIcon );

        _choices = new ArrayList();
        _choices.add( constantItem );
        _choices.add( stepItem );
        _choices.add( singleBarrierItem );
        _choices.add( doubleBarrierItem );
    }
    
    private static final int ICON_MARGIN = 4;
    
    private static ImageIcon createConstantIcon() {
        final int w = ICON_SIZE.width;
        final int h = ICON_SIZE.height;
        BufferedImage bi = new BufferedImage( w, h, BufferedImage.TYPE_INT_ARGB );
        Graphics2D g2 = bi.createGraphics();
        GeneralPath path = new GeneralPath();
        path.moveTo( 0, h/2 );
        path.lineTo( w/2, h/2 );
        g2.setStroke( ICON_STROKE );
        g2.setPaint( ICON_COLOR );
        g2.draw( path );
        return new ImageIcon( bi );
    }
    
    private static ImageIcon createStepIcon() {
        final int w = ICON_SIZE.width;
        final int h = ICON_SIZE.height;
        BufferedImage bi = new BufferedImage( w, h, BufferedImage.TYPE_INT_ARGB );
        Graphics2D g2 = bi.createGraphics();
        GeneralPath path = new GeneralPath();
        path.moveTo( 0, h - ICON_MARGIN );
        path.lineTo( w/3, h - ICON_MARGIN );
        path.lineTo( w/3, ICON_MARGIN );
        path.lineTo( 2*w/3, ICON_MARGIN );
        g2.setStroke( ICON_STROKE );
        g2.setPaint( ICON_COLOR );
        g2.draw( path );
        return new ImageIcon( bi );
    }
    
    private static ImageIcon createSingleBarrierIcon() {
        final int w = ICON_SIZE.width;
        final int h = ICON_SIZE.height;
        BufferedImage bi = new BufferedImage( w, h, BufferedImage.TYPE_INT_ARGB );
        Graphics2D g2 = bi.createGraphics();
        GeneralPath path = new GeneralPath();
        path.moveTo( 0, h - ICON_MARGIN );
        path.lineTo( w/5, h - ICON_MARGIN );
        path.lineTo( w/5, ICON_MARGIN );
        path.lineTo( 2*w/5, ICON_MARGIN );
        path.lineTo( 2*w/5, h - ICON_MARGIN );
        path.lineTo( 3*w/5, h - ICON_MARGIN );
        g2.setStroke( ICON_STROKE );
        g2.setPaint( ICON_COLOR );
        g2.draw( path );
        return new ImageIcon( bi );
    }
    
    private static ImageIcon createDoubleBarrierIcon() {
        final int w = ICON_SIZE.width;
        final int h = ICON_SIZE.height;
        BufferedImage bi = new BufferedImage( w, h, BufferedImage.TYPE_INT_ARGB );
        Graphics2D g2 = bi.createGraphics();
        GeneralPath path = new GeneralPath();
        path.moveTo( 0, h - ICON_MARGIN );
        path.lineTo( w/5, h - ICON_MARGIN );
        path.lineTo( w/5, ICON_MARGIN );
        path.lineTo( 2*w/5, ICON_MARGIN );
        path.lineTo( 2*w/5, h - ICON_MARGIN );
        path.lineTo( 3*w/5, h - ICON_MARGIN );
        path.lineTo( 3*w/5, ICON_MARGIN );
        path.lineTo( 4*w/5, ICON_MARGIN );
        path.lineTo( 4*w/5, h - ICON_MARGIN );
        path.lineTo( w, h - ICON_MARGIN );
        g2.setStroke( ICON_STROKE );
        g2.setPaint( ICON_COLOR );
        g2.draw( path );
        return new ImageIcon( bi );
    }
    
    private static class PotentialChoice {

        private PotentialType _potentialType;
        private String _label;
        private ImageIcon _imageIcon;

        public PotentialChoice( PotentialType potentialType, String label, ImageIcon imageIcon ) {
            _potentialType = potentialType;
            _label = label;
            _imageIcon = imageIcon;
        }

        public PotentialType getPotentialType() {
            return _potentialType;
        }

        public ImageIcon getImageIcon() {
            return _imageIcon;
        }

        public String getLabel() {
            return _label;
        }
    }
    
    private class PotentialComboBoxRenderer extends JLabel implements ListCellRenderer {

        public PotentialComboBoxRenderer() {
            super();
            setOpaque( true ); // for Macintosh
            setHorizontalTextPosition( SwingConstants.LEFT ); // text to left of icon
        }
        
        public Component getListCellRendererComponent( JList list, Object value, int index, boolean isSelected, boolean cellHasFocus ) {
            
            if ( isSelected ) {
                setBackground( list.getSelectionBackground() );
                setForeground( list.getSelectionForeground() );
            }
            else {
                setBackground( list.getBackground() );
                setForeground( list.getForeground() );
            }

            // Set the icon and text.
            PotentialChoice choice = (PotentialChoice) value;
            setText( choice.getLabel() );
            setIcon( choice.getImageIcon() );

            return this;
        }

    }
}
