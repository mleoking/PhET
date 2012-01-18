// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.simsharing.components;

import java.awt.event.ActionEvent;
import java.util.ArrayList;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.simsharing.Parameter;
import edu.colorado.phet.common.phetcommon.simsharing.SimSharingManager;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;
import edu.colorado.phet.common.phetcommon.simsharing.messages.ParameterKeys;
import edu.colorado.phet.common.phetcommon.simsharing.messages.ParameterSet;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserComponentId;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserComponentTypes;
import edu.colorado.phet.common.phetcommon.util.function.Function0;

import static edu.colorado.phet.common.phetcommon.simsharing.messages.UserActions.pressed;

/**
 * Swing check box that sends sim-sharing events.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class SimSharingJCheckBox extends JCheckBox {

    private final IUserComponent userComponent;

    // By default there are no custom parameters
    private ArrayList<Function0<ParameterSet>> customParameterFunctions = new ArrayList<Function0<ParameterSet>>();

    public SimSharingJCheckBox( IUserComponent userComponent ) {
        this.userComponent = userComponent;
    }

    public SimSharingJCheckBox( IUserComponent userComponent, Icon icon ) {
        super( icon );
        this.userComponent = userComponent;
    }

    public SimSharingJCheckBox( IUserComponent userComponent, Icon icon, boolean selected ) {
        super( icon, selected );
        this.userComponent = userComponent;
    }

    public SimSharingJCheckBox( IUserComponent userComponent, String text ) {
        super( text );
        this.userComponent = userComponent;
    }

    public SimSharingJCheckBox( IUserComponent userComponent, Action a ) {
        super( a );
        this.userComponent = userComponent;
    }

    public SimSharingJCheckBox( IUserComponent userComponent, String text, boolean selected ) {
        super( text, selected );
        this.userComponent = userComponent;
    }

    public SimSharingJCheckBox( IUserComponent userComponent, String text, Icon icon ) {
        super( text, icon );
        this.userComponent = userComponent;
    }

    public SimSharingJCheckBox( IUserComponent userComponent, String text, Icon icon, boolean selected ) {
        super( text, icon, selected );
        this.userComponent = userComponent;
    }

    @Override protected void fireActionPerformed( ActionEvent event ) {
        SimSharingManager.sendUserMessage( userComponent, UserComponentTypes.checkBox, pressed, getParameters() );
        super.fireActionPerformed( event );
    }

    // Gets parameters. Custom parameters are added following standard parameters.
    private ParameterSet getParameters() {
        return new ParameterSet().param( ParameterKeys.isSelected, isSelected() ).addAll( getCustomParameters() );
    }

    // Override this is you want to add custom parameters via subclassing.
    protected ParameterSet getCustomParameters() {
        ParameterSet parameterSet = new ParameterSet();
        for ( Function0<ParameterSet> function : customParameterFunctions ) {
            parameterSet.addAll( function.apply() );
        }
        return parameterSet;
    }

    // Use this if you want to add custom parameters, but can't use subclassing.
    public void addCustomParametersFunction( Function0<ParameterSet> function ) {
        customParameterFunctions.add( function );
    }

    // demonstrate usage
    public static void main( String[] args ) {

        String[] myArgs = { "-study" };
        SimSharingManager.init( new PhetApplicationConfig( myArgs, "myProject" ) );

        // check box that uses subclassing to provide custom parameters
        final JCheckBox checkBox1 = new SimSharingJCheckBox( new UserComponentId( "checkBox1" ), "subclassing" ) {
            @Override protected ParameterSet getCustomParameters() {
                return Parameter.param( ParameterKeys.text, "I use subclassing." );
            }
        };

        // check box that uses mutation to provide custom parameters
        final JCheckBox checkBox2 = new SimSharingJCheckBox( new UserComponentId( "checkBox2" ), "mutation" ) {{
            addCustomParametersFunction( new Function0<ParameterSet>() {
                public ParameterSet apply() {
                    return Parameter.param( ParameterKeys.text, "I use mutation." );
                }
            } );
        }};

        JFrame frame = new JFrame() {{
            setContentPane( new JPanel() {{
                add( checkBox1 );
                add( checkBox2 );
            }} );
            pack();
            setDefaultCloseOperation( WindowConstants.EXIT_ON_CLOSE );
        }};
        frame.setVisible( true );
    }
}
