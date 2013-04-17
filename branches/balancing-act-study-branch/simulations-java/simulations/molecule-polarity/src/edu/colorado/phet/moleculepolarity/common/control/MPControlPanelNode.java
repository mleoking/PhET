// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculepolarity.common.control;

import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Frame;

import javax.swing.Box;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.simsharing.components.SimSharingIcon;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserComponentChain;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserComponents;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.common.phetcommon.view.controls.PropertyCheckBox;
import edu.colorado.phet.common.phetcommon.view.controls.PropertyRadioButton;
import edu.colorado.phet.common.phetcommon.view.util.GridPanel;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;
import edu.colorado.phet.common.piccolophet.nodes.ControlPanelNode;
import edu.colorado.phet.common.piccolophet.nodes.ResetAllButtonNode;
import edu.colorado.phet.moleculepolarity.MPConstants;
import edu.umd.cs.piccolo.PNode;

/**
 * Node that arranges multiple "floating" control panels in a vertical stack, left justified.
 * Optional "Reset All" button is added.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class MPControlPanelNode extends PNode {

    private static final int Y_SPACING = 10;

    public MPControlPanelNode( Frame parentFrame, Resettable[] resettables, MPVerticalPanel... panels ) {

        /*
         * Workaround for #3077 (PSwing clips labels in floating control panels)
         * Traverse the containment hierarchy and add a small amount of horizontal padding to each component.
         */
        for ( MPVerticalPanel panel : panels ) {
            for ( Component component : panel.getComponents() ) {
                SwingUtils.padPreferredWidthDeep( component, 5 );
            }
        }

        // Uniform width
        int minWidth = 0;
        for ( MPVerticalPanel panel : panels ) {
            minWidth = (int) Math.max( minWidth, panel.getPreferredSize().getWidth() );
        }

        PNode previousNode = null;
        for ( MPVerticalPanel panel : panels ) {

            // uniform width
            panel.setMinWidth( minWidth );

            // panel node
            PNode panelNode = new ControlPanelNode( panel );
            addChild( panelNode );

            // layout: vertical, left justified
            if ( previousNode != null ) {
                panelNode.setOffset( previousNode.getXOffset(), previousNode.getFullBoundsReference().getMaxY() + Y_SPACING );
            }
            previousNode = panelNode;
        }

        // Reset All button
        if ( resettables != null ) {
            PNode resetAllButtonNode = new MPResetAllButtonNode( resettables, parentFrame );
            addChild( resetAllButtonNode );
            if ( previousNode != null ) {
                resetAllButtonNode.setOffset( previousNode.getXOffset(), previousNode.getFullBoundsReference().getMaxY() + Y_SPACING );
            }
        }
    }

    // Base class for individual panels, vertical layout.
    public static abstract class MPVerticalPanel extends GridPanel {

        public MPVerticalPanel( String title ) {
            setGridX( 0 ); // vertical
            setAnchor( Anchor.WEST ); // left justified

            // title
            add( new JLabel( title ) {{
                setFont( MPConstants.TITLE_FONT );
            }} );

            // space between title and controls below it
            add( Box.createVerticalStrut( 5 ) );
        }

        // provided so that we can set a uniform width for all panels
        public void setMinWidth( int minWidth ) {
            add( Box.createHorizontalStrut( minWidth ) );
        }
    }

    // Encapsulates the look of check boxes
    public static class MPCheckBox extends PropertyCheckBox {
        public MPCheckBox( IUserComponent userComponent, String text, Property<Boolean> property ) {
            super( userComponent, text, property );
            setFont( MPConstants.CONTROL_FONT );
        }
    }

    // Encapsulates the look of a check box with icon
    public static class MPCheckBoxWithIcon extends JPanel {
        public MPCheckBoxWithIcon( IUserComponent userComponent, String text, Icon icon, Property<Boolean> property ) {
            super( new FlowLayout( FlowLayout.LEFT, 0, 0 ) ); // ensures proper alignment with other controls
            final MPCheckBox checkBox = new MPCheckBox( userComponent, text, property );
            JLabel iconLabel = new SimSharingIcon( UserComponentChain.chain( userComponent, UserComponents.icon ), icon,
                                                   new VoidFunction0() {
                                                       public void apply() {
                                                           checkBox.setSelected( !checkBox.isSelected() );
                                                       }
                                                   } );
            add( checkBox );
            add( iconLabel );
        }
    }

    // Encapsulates the look of radio buttons
    public static class MPRadioButton<T> extends PropertyRadioButton<T> {
        public MPRadioButton( IUserComponent userComponent, String text, Property<T> property, T value ) {
            super( userComponent, text, property, value );
            super.setFont( MPConstants.CONTROL_FONT );
        }
    }

    // Encapsulates the look of the Reset All button
    public class MPResetAllButtonNode extends ResetAllButtonNode {
        public MPResetAllButtonNode( Resettable[] resettables, Frame parentFrame ) {
            super( resettables, parentFrame, 16, Color.BLACK, Color.WHITE );
            setConfirmationEnabled( false );
        }
    }
}
