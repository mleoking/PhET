package edu.colorado.phet.website.admin.doc;

import org.apache.log4j.Logger;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.form.TextField;

import edu.colorado.phet.website.admin.AdminPage;

public class TechnicalDocPage extends AdminPage {

    private TextField keyText;
    private TextField valueText;

    private static final Logger logger = Logger.getLogger( TechnicalDocPage.class.getName() );

    public TechnicalDocPage( PageParameters parameters ) {
        super( parameters );

    }

}