package edu.colorado.phet.wickettest.translation;

import java.util.Locale;

import org.apache.wicket.Component;
import org.apache.wicket.PageParameters;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.AjaxEditableMultiLineLabel;
import org.apache.wicket.model.Model;

import edu.colorado.phet.common.phetcommon.util.LocaleUtils;
import edu.colorado.phet.wickettest.panels.PhetPanel;
import edu.colorado.phet.wickettest.panels.SponsorsPanel;
import edu.colorado.phet.wickettest.util.PageContext;

public class TranslationTestPage extends TranslationPage {

    private PhetPanel panel;
    private Model modelDir = new Model( "ltr" );
    private Model modelPrincipalSponsors = new Model( "Principal Sponsors" );
    private Model modelHewlett = new Model( "Makes grants to address the most serious social and environmental problems facing society, where risk capital, responsibly invested, may make a difference over time." );
    private Model modelNsf = new Model( "An independent federal agency created by Congress in 1950 to promote the progress of science." );
    private Model modelKsu = new Model( "King Saud University seeks to become a leader in educational and technological innovation, scientific discovery and creativity through fostering an atmosphere of intellectual inspiration and partnership for the prosperity of society." );

    public TranslationTestPage( PageParameters parameters ) {
        super( parameters, true );

        Locale testLocale = LocaleUtils.stringToLocale( "zh_CN" );

        panel = new SponsorsPanel( "panel", new PageContext( testLocale, this ) );
        panel.setOutputMarkupId( true );
        add( panel );

        addTitle( "Translation test page" );


        add( new AjaxEditableMultiLineLabel( "translation-dir", modelDir ) {
            @Override
            protected void onSubmit( AjaxRequestTarget target ) {
                super.onSubmit( target );
                target.addComponent( panel );
            }
        } );

        add( new AjaxEditableMultiLineLabel( "translation-principalSponsors", modelPrincipalSponsors ) {
            @Override
            protected void onSubmit( AjaxRequestTarget target ) {
                super.onSubmit( target );
                target.addComponent( panel );
            }
        } );

        add( new AjaxEditableMultiLineLabel( "translation-hewlett", modelHewlett ) {
            @Override
            protected void onSubmit( AjaxRequestTarget target ) {
                super.onSubmit( target );
                target.addComponent( panel );
            }
        } );

        add( new AjaxEditableMultiLineLabel( "translation-nsf", modelNsf ) {
            @Override
            protected void onSubmit( AjaxRequestTarget target ) {
                super.onSubmit( target );
                target.addComponent( panel );
            }
        } );

        add( new AjaxEditableMultiLineLabel( "translation-ksu", modelKsu ) {
            @Override
            protected void onSubmit( AjaxRequestTarget target ) {
                super.onSubmit( target );
                target.addComponent( panel );
            }
        } );

    }

    public String translateString( Component component, Locale locale, String key ) {
        /*
        if ( locale == null && key.equals( "language.dir" ) ) {
            return "ltr";
        }
        */
        if ( key.equals( "language.dir" ) ) {
            return (String) modelDir.getObject();
        }
        else if ( key.equals( "sponsors.principalSponsors" ) ) {
            return (String) modelPrincipalSponsors.getObject();
        }
        else if ( key.equals( "sponsors.hewlett" ) ) {
            return (String) modelHewlett.getObject();
        }
        else if ( key.equals( "sponsors.nsf" ) ) {
            return (String) modelNsf.getObject();
        }
        else if ( key.equals( "sponsors.ksu" ) ) {
            return (String) modelKsu.getObject();
        }
        return null;
    }
}
