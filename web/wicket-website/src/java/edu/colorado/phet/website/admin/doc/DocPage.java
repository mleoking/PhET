package edu.colorado.phet.website.admin.doc;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.util.value.ValueMap;
import org.hibernate.Session;

import edu.colorado.phet.buildtools.BuildLocalProperties;
import edu.colorado.phet.buildtools.util.FileUtils;
import edu.colorado.phet.buildtools.util.PhetJarSigner;
import edu.colorado.phet.website.PhetWicketApplication;
import edu.colorado.phet.website.admin.AdminPage;
import edu.colorado.phet.website.components.StringTextField;
import edu.colorado.phet.website.data.TranslatedString;
import edu.colorado.phet.website.data.transfer.TransferData;
import edu.colorado.phet.website.notification.NotificationHandler;
import edu.colorado.phet.website.translation.PhetLocalizer;
import edu.colorado.phet.website.util.HibernateTask;
import edu.colorado.phet.website.util.HibernateUtils;
import edu.colorado.phet.website.util.SearchUtils;
import edu.colorado.phet.website.util.StringUtils;

public class DocPage extends AdminPage {

    private TextField keyText;
    private TextField valueText;

    private static Logger logger = Logger.getLogger( DocPage.class.getName() );

    public DocPage( PageParameters parameters ) {
        super( parameters );

    }

}