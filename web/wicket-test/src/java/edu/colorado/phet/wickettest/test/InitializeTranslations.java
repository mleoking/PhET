package edu.colorado.phet.wickettest.test;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import edu.colorado.phet.common.phetcommon.util.LocaleUtils;
import edu.colorado.phet.wickettest.data.TranslatedString;
import edu.colorado.phet.wickettest.data.Translation;
import edu.colorado.phet.wickettest.util.HibernateUtils;

public class InitializeTranslations {

    public static void addString( Session session, Translation translation, String key, String value ) {
        TranslatedString str = new TranslatedString();
        str.initializeNewString( translation, key, value );
        session.save( str );
    }

    public static void main( String[] args ) {

        Transaction tx = null;
        Session session = HibernateUtils.getInstance().openSession();
        try {
            tx = session.beginTransaction();

            Translation english = new Translation();
            english.setLocale( LocaleUtils.stringToLocale( "en" ) );
            english.setVisible( true );
            session.save( english );

            TranslatedString str;

            addString( session, english, "language.dir", "ltr" );
            addString( session, english, "language.name", "English" );

            addString( session, english, "home.title", "PhET: Free online physics, chemistry, biology, earth science and math simulations" );
            addString( session, english, "home.header", "Interactive Science Simulations" );
            addString( session, english, "home.subheader", "Fun, interactive, research-based simulations of physical phenomena from the PhET project at the University of Colorado." );
            addString( session, english, "home.playWithSims", "Play with sims... >" );
            addString( session, english, "home.runOurSims", "Run our Simulations" );
            addString( session, english, "home.onLine", "On Line" );
            addString( session, english, "home.fullInstallation", "Full Installation" );
            addString( session, english, "home.oneAtATime", "One at a Time" );
            addString( session, english, "home.teacherIdeasAndActivities", "Teacher Ideas & Activities" );
            addString( session, english, "home.workshops", "Workshops" );
            addString( session, english, "home.contribute", "Contribute" );
            addString( session, english, "home.supportPhet", "Support PhET" );
            addString( session, english, "home.translateSimulations", "Translate Simulations" );
            addString( session, english, "home.browseSims", "Browse Sims" );
            addString( session, english, "home.simulations", "Simulations" );

            addString( session, english, "simulationPage.title", "{0} ({1})" );

            addString( session, english, "simulationDisplay.title", "{0} - PhET Simulations" );

            addString( session, english, "simulationMainPanel.translatedVersions", "Translated Versions:" );
            addString( session, english, "simulationMainPanel.screenshot.alt", "{0} Screenshot" );
            addString( session, english, "simulationMainPanel.version", "Version: {0}" );
            addString( session, english, "simulationMainPanel.kilobytes", "{0} kB" );
            addString( session, english, "simulationMainPanel.runOffline", "Download" );
            addString( session, english, "simulationMainPanel.runOnline", "Run Now!" );

            addString( session, english, "nav.home", "Home" );
            addString( session, english, "nav.simulations", "Simulations" );
            addString( session, english, "nav.featured", "Featured Sims" );
            addString( session, english, "nav.new", "New Sims" );
            addString( session, english, "nav.physics", "Physics" );
            addString( session, english, "nav.motion", "Motion" );
            addString( session, english, "nav.sound-and-waves", "Sound & Waves" );
            addString( session, english, "nav.work-energy-and-power", "Work, Energy & Power" );
            addString( session, english, "nav.heat-and-thermodynamics", "Heat & Thermo" );
            addString( session, english, "nav.quantum-phenomena", "Quantum Phenomena" );
            addString( session, english, "nav.light-and-radiation", "Light & Radiation" );
            addString( session, english, "nav.electricity-magnets-and-circuits", "Electricity, Magnets & Circuits" );
            addString( session, english, "nav.biology", "Biology" );
            addString( session, english, "nav.chemistry", "Chemistry" );
            addString( session, english, "nav.earth-science", "Earth Science" );
            addString( session, english, "nav.math", "Math" );
            addString( session, english, "nav.tools", "Tools" );
            addString( session, english, "nav.applications", "Applications" );
            addString( session, english, "nav.cutting-edge-research", "Cutting Edge Research" );
            addString( session, english, "nav.all", "All Sims" );
            addString( session, english, "nav.troubleshooting.main", "Troubleshooting" );
            addString( session, english, "nav.about", "About PhET" );

            addString( session, english, "troubleshooting.main.title", "Troubleshooting - PhET Simulations" );
            addString( session, english, "troubleshooting.main.intro", "This page will help you solve some of the problems people commonly have running our programs. If you can''t solve your problem here, please notify us by email at the following email address: {0}." );
            addString( session, english, "troubleshooting.main.java", "Java Installation and Troubleshooting" );
            addString( session, english, "troubleshooting.main.flash", "Flash Installation and Troubleshooting" );
            addString( session, english, "troubleshooting.main.javascript", "JavaScript Troubleshooting (note: this is for your browser, not the simulations)" );
            addString( session, english, "troubleshooting.main.faqs", "FAQs" );
            addString( session, english, "troubleshooting.main.q1.title", "Why can I run some of the simulations but not all?" );
            addString( session, english, "troubleshooting.main.q1.answer", "<p>Some of our simulations are Java Web Start based applications and others use Macromedia''s Flash player. Flash comes with most computers while Java Web Start is a free application that can be downloaded from Sun Microsystems. To run the Java-based simulations you must have Java version 1.5 or higher installed on your computer.</p><p><a {0}>Learn about Java installation and Troubleshooting here</a>.</p>" );
            addString( session, english, "troubleshooting.main.q2.title", "What are the System Requirements for running PhET simulations?" );
            addString( session, english, "troubleshooting.main.q2.answer", "<p><strong>Windows Systems</strong><br/>Intel Pentium processor<br/>Microsoft Windows 98SE/2000/XP/Vista<br/>256MB RAM minimum<br/>Approximately 97 MB available disk space (for full <a {0}>installation</a>)<br/>1024x768 screen resolution or better<br/>Sun Java 1.5.0_15 or later<br/>Macromedia Flash 8 or later<br/>Microsoft Internet Explorer 6 or later, Firefox 2 or later</p><p><strong>Macintosh Systems</strong><br/>G3, G4, G5 or Intel processor<br/>OS 10.4 or later<br/>256MB RAM minimum<br/>Approximately 86 MB available disk space (for full <a {0}>installation</a>)<br/>1024x768 screen resolution or better<br/>Apple Java 1.5.0_19 or later<br/>Macromedia Flash 8 or later<br/>Safari 2 or later, Firefox 2 or later</p><p><strong>Linux Systems</strong><br/>Intel Pentium processor<br/>256MB RAM minimum<br/>Approximately 81 MB disk space (for full <a {0}>installation</a>)<br/>1024x768 screen resolution or better<br/>Sun Java 1.5.0_15 or later<br/>Macromedia Flash 8 or later<br/>Firefox 2 or later<br/></p><p><strong>Support Software</strong></p><p>Some of our simulations use Java, and some use Flash. Both of these are available as free downloads, and our downloadable <a {0}>PhET Offline Website Installer</a> includes Java for those who need it.</p>" );
            addString( session, english, "troubleshooting.main.q4.title", "I use Internet Explorer and the simulations do not run on my computer." );
            addString( session, english, "troubleshooting.main.q4.answer", "<p>We <strong>strongly</strong> recommend you use the latest version of Internet Explorer (IE8).</p><p><strong>Internet Explorer Security Settings</strong></p><p>Some installations of Internet Explorer, particularly under Windows XP SP2, have default security settings which can impede some aspects of how your locally installed PhET interface functions. For the best user experience while using our simulations installed on your computer, we recommend following the steps below:</p><ol><li>In Internet Explorer on your local workstation, choose Tools &gt; Internet Options.</li><li>Choose the Advanced tab, then scroll to the Security section.</li><li>Enable \"Allow active content to run in files on my computer\".</li><li>Choose OK.</li></ol>" );
            addString( session, english, "troubleshooting.main.q5.title", "Why don't Flash simulations run on my computer?" );
            addString( session, english, "troubleshooting.main.q5.answer", "<p><strong>QuickTime\u2122 and Flash\u2122 compatibility</strong></p><p>It has come to our attention that some of our users are unable to use our Flash-based simulations due to a compatibility issue between Apple Computer''s QuickTime&trade; and the Flash&trade; player. Some users have reported that uninstalling QuickTime resolves the issue.</p><p>We are aware that this is not an acceptable solution and are working to resolve this issue. If you are experiencing this problem, please contact us at at {0} and regularly check back here for more information.</p>" );
            addString( session, english, "troubleshooting.main.q6.title", "What is the ideal screen resolution to run PhET simulations?" );
            addString( session, english, "troubleshooting.main.q6.answer", "<p>PhET simulations work best at a screen resolution of 1024 x 768 pixels. (Some of them are written so that they cannot be resized.) At lower resolution (e.g. 800 x 600), all the controls may not fit on your screen. At higher resolution (e.g. 1280 x 1024), you may not be able to make the simulation fill the whole screen, or if you do, it may slow down performance. To change your screen resolution, follow the directions below:</p><p><strong>Windows Vista</strong></p><ol><li>From Start menu, click on \"Control Panel.\"</li><li>Press \"Adjust screen resolution\" under \"Appearance and Personalization.\"</li><li>Use the \"Screen resolution\" slider to select a resolution and click \"OK.\"</li></ol><p><strong>Windows 98SE/2000/XP</strong></p><ol><li>From Start menu, click on \"Control Panel.\"</li><li>Double click on \"Display\" icon.</li><li>Select the \"Settings\" tab.</li><li>Use the \"Screen resolution\" slider to select a resolution and click \"OK.\"</li></ol><p><strong>Macintosh</strong></p><ol><li>Open the System Preferences (either from the Dock or from the Apple menu).</li><li>Open the Displays Panel and choose the Display tab.</li><li>On the left of the Displays tab you can select one of the Resolutions from the list.</li><li>Quit or close the System Preferences when done.</li></ol>" );
            addString( session, english, "troubleshooting.main.q7.title", "I have Windows 2000 and can run Flash simulations but the Java based simulations do not work." );
            addString( session, english, "troubleshooting.main.q7.answer", "<p>Some Windows 2000 systems have been reported to lack part of the necessary Java configuration. These systems will typically start our Flash-based simulations reliably, but will appear to do nothing when launching our Java-based simulations.</p><p><strong>To resolve this situation, please perform the following steps:</strong></p><ol><li>From the desktop or start menu, open \"My Computer\"</li><li>Click on the \"Folder Options\" item in the \"Tools\" menu</li><li>Click on the \"File Types\" tab at the top of the window that appears</li><li>Locate \"JNLP\" in the \"extensions\" column, and click once on it to select the item</li><li>Click on the \"change\" button</li><li>When asked to choose which program to use to open JNLP files, select \"Browse\"</li><li>Locate the program \"javaws\" or \"javaws.exe\" in your Java installation folder (typically \"C:\\Program Files\\Java\\j2re1.xxxx\\javaws\", where \"xxxx\" is a series of numbers indicating the software version; choose the latest version)</li><li>Select the program file and then click \"Open\" to use the \"javaws\" program to open JNLP files.</li></ol><p>Java-based simulations should now function properly.</p><p>Please contact us by email at {0} if you have any further difficulties.</p>" );
            addString( session, english, "troubleshooting.main.q8.title", "Why do PhET simulations run slower on my laptop than on a desktop?" );
            addString( session, english, "troubleshooting.main.q8.answer", "<p>On some laptop computers, simulations may appear to run much slower than anticipated and/or exhibit unexpected graphics problems. This may be due to power management settings that affect how the computer''s graphics system runs and can be corrected by either a) changing the computer''s power management configuration, or b) using the laptop computer while plugged in to an AC power source.</p><p>Many laptop computers are configured to reduce the amount of battery power used by the graphics/video system while the computer is running on battery power. If you must use the laptop while it is not plugged in, we suggest changing your computer''s power management settings to \"maximize performance\" while unplugged. This should ensure that the graphics system runs at its peak speed. The location of this setting varies from one manufacturer to the next and we suggest contacting your computer vendor if you have difficulty locating it. Please contact us at {0} if you continue to encounter problems.</p>" );
            addString( session, english, "troubleshooting.main.q9.title", "Why does my computer crash when I run one of the simulations that has sound?" );
            addString( session, english, "troubleshooting.main.q9.answer", "<p>Simulations that use sound can be unstable when run on computers using old device driver software. If you are encountering crashes or other undesirable behavior with any of our simulations that use sound, we advise updating your sound drivers, as this may solve the problem. For assistance with updating your sound drivers, contact your computer vendor or audio hardware manufacturer. Contact us at {0} if you continue to encounter difficulty. </p>" );
            addString( session, english, "troubleshooting.main.q10.title", "I would like to translate PhET Simulations into another Language. Can this be easily done?" );
            addString( session, english, "troubleshooting.main.q10.answer", "<p>The PhET simulations have been written so that they are easily translated to languages other than English. Please <a {0}>click here</a> for more information.</p>" );
            addString( session, english, "troubleshooting.main.q11.title", "I have downloaded and installed the PhET Offline Website Installer, and I get a warning on every page. Why?" );
            addString( session, english, "troubleshooting.main.q11.answer", "<p>The <a {0}>PhET Offline Website Installer</a> creates a local copy of the current version of the PhET website on your computer. When you access this locally installed copy, your computer will use your default browser, which for many people is Internet Explorer. If the security settings are set to their default values, you may get an error that says <em>\"To help protect your security, Internet Explorer has restricted this webpage from running scripts or ActiveX controls that could access your computer. Click here for options...\"</em> (or something similar). This is a security feature of Internet Explorer version 6 and later, and is meant to warn users about running active content locally. The PhET simulations present no danger to your computer, and running them locally is no different than running them from the web site.</p><p>If you wish to disable this warning, you can do so by adjusting your browser''s security settings. For IE versions 6 and 7, the way to do this is to go into Tools->Internet Options->Advanced, find the \"Security\" heading, and check \"Allow active content to run in files on My Computer\". Note that you will need to restart Internet Explorer to get this change to take effect. You should only do this if feel confident that there is no other off-line content that you may run on your computer that could be malicious.</p><p>Alternatively, you could use a different browser (such as Firefox) that does not have this issue.</p>" );
            addString( session, english, "troubleshooting.main.q12.title", "When I run simulations from the PhET Offline Website Installer, I am seeing a dialog that says \"The application's digital signature has been verified. Do you want to run the application?\" (or something similar). What does this mean?" );
            addString( session, english, "troubleshooting.main.q12.answer", "<p>The PhET simulations that are distributed with the installer include a \"digital certificate\" that verifies that these simulations were actually created by PhET. This is a security measure that helps to prevent an unscrupulous individual from creating applications that claim to be produced by PhET but are not. If the certificate acceptance dialog says that the publisher is \"PhET, University of Colorado\", and the dialog also says that the signature was validated by a trusted source, you can have a high degree of confidence that the application was produced by the PhET team.</p><p>On most systems, it is possible to permanently accept the PhET certificate and thereby prevent this dialog from appearing each time a simulation is run locally. Most Windows and Max OSX systems have a check box on the certificate acceptance dialog that says \"Always trust content from this publisher\". Checking this box will configure your system in such a way that the dialog will no longer appear when starting up PhET simulations.</p>" );
            addString( session, english, "troubleshooting.main.q13.title", "(MAC users) When I click \"run now\" to start the simulation all I get is a text file that opens?" );
            addString( session, english, "troubleshooting.main.q13.answer", "<p>This problem will affect mac users who recently installed Apple''s \"Java for Mac OS X 10.5 Update 4\". The update will typically be done via Software Update, or automatically. After installing this update, the problem appears: clicking on JNLP files in Safari or FireFox caused the JNLP file to open in TextEdit, instead of starting Java Web Start. </p><p>The fix is:<br/>1. Go to http://support.apple.com/downloads/Java_for_Mac_OS_X_10_5_Update_4<br/>2. Click Download to download a .dmg file<br/>3. When the .dmg has downloaded, double-click on it (if it doesn''t mount automatically)<br/>4. Quit all applications<br/>5. Run the update installer</p>" );
            addString( session, english, "troubleshooting.main.licensingRequirements", "What are Licensing requirements?" );

            addString( session, english, "about.title", "About PhET" );
            addString( session, english, "about.p1", "PhET Interactive Simulations is an ongoing effort to provide an extensive suite of simulations to improve the way that physics, chemistry, biology, earth science and math are taught and learned. The <a {0}>simulations</a> are interactive tools that enable students to make connections between real life phenomena and the underlying science which explains such phenomena. Our team of scientists, software engineers and science educators use a <a {1}>research-based approach</a> - incorporating findings from prior research and our own testing - to create simulations that support student engagement with and understanding of scientific concepts." );
            addString( session, english, "about.p2", "In order to help students visually comprehend these concepts, PhET simulations animate what is invisible to the eye through the use of graphics and intuitive controls such as click-and-drag manipulation, sliders and radio buttons.  In order to further encourage quantitative exploration, the simulations also offer measurement instruments including rulers, stop-watchs, voltmeters and thermometers.  As the user manipulates these interactive tools, responses are immediately animated thus effectively illustrating cause-and-effects relationships as well as multiple linked representations (motion of the objects, graphs, number readouts, etc...)." );
            addString( session, english, "about.p3", "To ensure educational effectiveness and usability, all of the simulations are extensively tested and evaluated.  These tests include student interviews in addition to actual utilization of the simulations in a variety of settings, including lectures, group work, homework and lab work.   Our <a {0}>rating system</a> indicates what level of testing has been completed on each simulation." );
            addString( session, english, "about.p4", "All PhET simulations are freely available from the <a {0}>PhET website</a> and are easy to use and incorpate into the classroom. They are written in <a {1}>Java</a> and <a {2}>Flash</a>, and can be run using a standard web browser as long as <a {2}>Flash</a> and <a {1}>Java</a> are installed." );

            addString( session, english, "sponsors.principalSponsors", "Principal Sponsors" );
            addString( session, english, "sponsors.hewlett", "Makes grants to address the most serious social and environmental problems facing society, where risk capital, responsibly invested, may make a difference over time." );
            addString( session, english, "sponsors.nsf", "An independent federal agency created by Congress in 1950 to promote the progress of science." );
            addString( session, english, "sponsors.ksu", "King Saud University seeks to become a leader in educational and technological innovation, scientific discovery and creativity through fostering an atmosphere of intellectual inspiration and partnership for the prosperity of society." );

            //

            //

            Translation simplifiedChinese = new Translation();
            simplifiedChinese.setLocale( LocaleUtils.stringToLocale( "zh_CN" ) );
            simplifiedChinese.setVisible( true );
            session.save( simplifiedChinese );

            addString( session, simplifiedChinese, "home.header", "\u4E92\u52A8\u79D1\u5B66\u6A21\u62DF" );
            addString( session, simplifiedChinese, "nav.motion", "\u8FD0\u52A8" );
            addString( session, simplifiedChinese, "language.name", "\u4E2D\u6587" );
            addString( session, simplifiedChinese, "home.subheader", "\u6709\u8DA3\uFF0C\u4E92\u52A8\uFF0C\u4EE5\u7814\u7A76\u4E3A\u57FA\u7840\u7684\u6A21\u62DF\u7269\u7406\u73B0\u8C61\u4ECE\u78A7\u9879\u76EE\u5728\u79D1\u7F57\u62C9\u591A\u5927\u5B66" );
            addString( session, simplifiedChinese, "home.playWithSims", "\u73A9\u6A21\u62DF... >" );
            addString( session, simplifiedChinese, "nav.home", "\u5BB6" );
            addString( session, simplifiedChinese, "nav.simulations", "\u6A21\u62DF" );
            addString( session, simplifiedChinese, "nav.featured", "\u7CBE\u9009\u6A21\u62DF" );
            addString( session, simplifiedChinese, "nav.new", "\u65B0\u7684\u6A21\u62DF" );
            addString( session, simplifiedChinese, "nav.physics", "\u7269\u7406" );
            addString( session, simplifiedChinese, "nav.sound-and-waves", "\u5065\u5168\u548C\u6CE2" );
            addString( session, simplifiedChinese, "nav.work-energy-and-power", "\u80FD\u6E90\u548C\u7535\u529B\u5DE5\u4F5C" );
            addString( session, simplifiedChinese, "nav.heat-and-thermodynamics", "\u70ED\u548C\u70ED\u529B\u5B66" );
            addString( session, simplifiedChinese, "nav.quantum-phenomena", "\u91CF\u5B50\u73B0\u8C61" );
            addString( session, simplifiedChinese, "nav.light-and-radiation", "\u5149\u53CA\u8F90\u5C04" );
            addString( session, simplifiedChinese, "nav.electricity-magnets-and-circuits", "\u7535\u529B\u548C\u78C1\u94C1\u548C\u7535\u8DEF" );
            addString( session, simplifiedChinese, "nav.biology", "\u751F\u7269\u5B66" );
            addString( session, simplifiedChinese, "nav.chemistry", "\u5316\u5B66" );
            addString( session, simplifiedChinese, "nav.earth-science", "\u5730\u7403\u79D1\u5B66" );
            addString( session, simplifiedChinese, "nav.math", "\u6570\u5B66" );
            addString( session, simplifiedChinese, "nav.tools", "\u5DE5\u5177" );
            addString( session, simplifiedChinese, "nav.applications", "\u5E94\u7528" );
            addString( session, simplifiedChinese, "nav.cutting-edge-research", "\u524D\u6CBF\u7814\u7A76" );
            addString( session, simplifiedChinese, "nav.all", "\u6240\u6709\u6A21\u62DF" );
            addString( session, simplifiedChinese, "nav.about", "\u5173\u4E8EPhET" );
            addString( session, simplifiedChinese, "simulationMainPanel.translatedVersions", "\u7FFB\u8BD1\u6587\u672C" );
            addString( session, simplifiedChinese, "sponsors.principalSponsors", "\u4E3B\u8981\u63D0\u6848\u56FD" );
            addString( session, simplifiedChinese, "sponsors.hewlett", "\u4F7F\u8D60\u6B3E\uFF0C\u4EE5\u89E3\u51B3\u6700\u4E25\u91CD\u7684\u793E\u4F1A\u548C\u73AF\u5883\u95EE\u9898\u6240\u9762\u4E34\u7684\u793E\u4F1A\uFF0C\u98CE\u9669\u8D44\u672C\uFF0C\u8D1F\u8D23\u4EFB\u7684\u6295\u8D44\uFF0C\u53EF\u80FD\u4F1A\u53D1\u6325\u4F5C\u7528\u968F\u7740\u65F6\u95F4\u7684\u63A8\u79FB" );
            addString( session, simplifiedChinese, "sponsors.nsf", "\u4E00\u4E2A\u72EC\u7ACB\u7684\u8054\u90A6\u673A\u6784\u7531\u7F8E\u56FD\u56FD\u4F1A\u521B\u529E\u4E8E1950\u5E74\uFF0C\u4EE5\u4FC3\u8FDB\u79D1\u5B66\u7684\u8FDB\u6B65" );
            addString( session, simplifiedChinese, "sponsors.ksu", "\u6C99\u7279\u56FD\u738B\u5927\u5B66\u529B\u6C42\u6210\u4E3A\u4E00\u4E2A\u9886\u5148\u7684\u6559\u80B2\u548C\u6280\u672F\u521B\u65B0\uFF0C\u79D1\u5B66\u53D1\u73B0\u548C\u521B\u9020\u6027\u901A\u8FC7\u4FC3\u8FDB\u7684\u6C14\u6C1B\u4E2D\u8FDB\u884C\u7684\u667A\u529B\u542F\u8FEA\u548C\u4F19\u4F34\u5173\u7CFB\uFF0C\u793E\u4F1A\u7684\u7E41\u8363" );
            addString( session, simplifiedChinese, "home.title", "PhET: \u514D\u8D39\u5728\u7EBF\u7269\u7406\uFF0C\u5316\u5B66\uFF0C\u751F\u7269\uFF0C\u5730\u7403\u79D1\u5B66\u548C\u6570\u5B66\u6A21\u62DF" );
            addString( session, simplifiedChinese, "simulationDisplay.title", "{0} - PhET \u6A21\u62DF" );
            addString( session, simplifiedChinese, "simulationPage.title", "{0} ({1})" );
            addString( session, simplifiedChinese, "language.dir", "ltr" );
            addString( session, simplifiedChinese, "nav.troubleshooting.main", "\u7591\u96BE\u89E3\u7B54" );
            addString( session, simplifiedChinese, "troubleshooting.main.title", "\u7591\u96BE\u89E3\u7B54- PhET\u6A21\u62DF" );
            addString( session, simplifiedChinese, "troubleshooting.main.intro", "\u6B64\u9875\u5C06\u5E2E\u52A9\u60A8\u89E3\u51B3\u4E00\u4E9B\u95EE\u9898\uFF0C\u4EBA\u4EEC\u666E\u904D\u6709\u8FD0\u884C\u6211\u4EEC\u7684\u7A0B\u5E8F\u3002\u5982\u679C\u4F60\u6839\u672C\u65E0\u6CD5\u89E3\u51B3\u60A8\u7684\u95EE\u9898\uFF0C\u8BF7\u901A\u8FC7\u7535\u5B50\u90AE\u4EF6\u901A\u77E5\u6211\u4EEC\u5728\u4EE5\u4E0B\u7684\u7535\u5B50\u90AE\u4EF6\u5730\u5740 {0}" );
            addString( session, simplifiedChinese, "troubleshooting.main.java", "Java\u7684\u5B89\u88C5\u548C\u6545\u969C\u68C0\u4FEE" );
            addString( session, simplifiedChinese, "troubleshooting.main.flash", "\u95EA\u5149\u5B89\u88C5\u548C\u6545\u969C\u68C0\u4FEE" );
            addString( session, simplifiedChinese, "troubleshooting.main.javascript", "JavaScript\u7684\u7591\u96BE\u89E3\u7B54\uFF08\u6CE8\uFF1A\u8FD9\u662F\u60A8\u7684\u6D4F\u89C8\u5668\uFF0C\u800C\u4E0D\u662F\u6A21\u62DF）" );
            addString( session, simplifiedChinese, "troubleshooting.main.q2.title", "\u7684\u7CFB\u7EDF\u8981\u6C42\u662F\u4EC0\u4E48\u8FD0\u884C\u78A7\u6A21\u62DF\uFF1F" );


            tx.commit();
        }
        catch( RuntimeException e ) {
            if ( tx != null && tx.isActive() ) {
                try {
                    tx.rollback();
                }
                catch( HibernateException e1 ) {
                    System.out.println( "Error rolling back transaction" );
                }
                throw e;
            }
        }

    }

}
