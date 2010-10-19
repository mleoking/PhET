package edu.colorado.phet.buildanatom.view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import edu.colorado.phet.buildanatom.BuildAnAtomConstants;
import edu.colorado.phet.common.phetcommon.model.BooleanProperty;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * @author Sam Reid
 */
public class OrbitalViewControl extends PNode {
    public OrbitalViewControl( final BooleanProperty viewOrbitals) {
        final PText textTitle = new PText( "Model:" ) {{setFont( BuildAnAtomConstants.WINDOW_TITLE_FONT );}};//TODO: I18n
        addChild( textTitle );
        JPanel panel = new VerticalLayoutPanel() {{
            final ButtonGroup buttonGroup=new ButtonGroup();//Fixes the problem that clicking on a radio button twice causes it to become de-selected
            add( new JRadioButton( "orbits" ,viewOrbitals.getValue()) {{//TODO: I18n
                buttonGroup.add( this );
                setBackground( BuildAnAtomConstants.CANVAS_BACKGROUND );
                setFont( BuildAnAtomConstants.ITEM_FONT );
                addActionListener( new ActionListener() {
                    public void actionPerformed( ActionEvent e ) {
                        viewOrbitals.setValue( true );
                    }
                } );
                viewOrbitals.addObserver( new SimpleObserver() {
                    public void update() {
                        setSelected( viewOrbitals.getValue() );
                    }
                } );
            }} );
            add( new JRadioButton( "cloud" ,!viewOrbitals.getValue()) {{//TODO: I18n
                buttonGroup.add(this);
                setBackground( BuildAnAtomConstants.CANVAS_BACKGROUND );
                setFont( BuildAnAtomConstants.ITEM_FONT );
                addActionListener( new ActionListener() {
                    public void actionPerformed( ActionEvent e ) {
                        viewOrbitals.setValue( false );
                    }
                } );
                viewOrbitals.addObserver( new SimpleObserver() {
                    public void update() {
                        setSelected( !viewOrbitals.getValue() );
                    }
                } );
            }} );
        }};
        addChild( new PSwing( panel ) {{
            setOffset( textTitle.getFullBounds().getX(), textTitle.getFullBounds().getMaxY() );
        }} );
    }
}
