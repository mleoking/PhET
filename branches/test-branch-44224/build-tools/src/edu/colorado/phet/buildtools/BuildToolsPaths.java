package edu.colorado.phet.buildtools;

/**
 * Should hold information about directory structure under trunk/
 */
public class BuildToolsPaths {

    //----------------------------------------------------------------------------
    // build-tools
    //----------------------------------------------------------------------------

    public static final String BUILD_TOOLS_DIR = "build-tools";

    public static final String BUILD_LOCAL_PROPERTIES = BUILD_TOOLS_DIR + "/build-local.properties";

    public static final String BUILD_TOOLS_TEMPLATES = BUILD_TOOLS_DIR + "/templates";

    public static final String WEBSTART_TEMPLATE = BUILD_TOOLS_TEMPLATES + "/webstart-template.jnlp";
    public static final String PROGUARD_TEMPLATE = BUILD_TOOLS_TEMPLATES + "/proguard-template.pro";

    public static final String FLASH_HTML_TEMPLATE = BUILD_TOOLS_DIR + "/data/flash/flash-template.html";
    public static final String FLASH_BUILD_TEMPLATE = BUILD_TOOLS_DIR + "/data/flash/build-template.jsfl";

    public static final String FLASH_GET_FLASH_IMAGE = BUILD_TOOLS_DIR + "/data/flash/get_flash.jpg";


    //----------------------------------------------------------------------------
    // simulations-java
    //----------------------------------------------------------------------------

    public static final String SIMULATIONS_JAVA = "simulations-java";

    public static final String JAVA_SIMULATIONS_DIR = SIMULATIONS_JAVA + "/simulations";
    public static final String JAVA_COMMON = SIMULATIONS_JAVA + "/common";
    public static final String JAVA_CONTRIB = SIMULATIONS_JAVA + "/contrib";

    public static final String PHETCOMMON = JAVA_COMMON + "/phetcommon";

    public static final String PHETCOMMON_DATA = PHETCOMMON + "/data";

    public static final String PHETCOMMON_LOCALIZATION = PHETCOMMON_DATA + "/phetcommon/localization";

    public static final String JNLP_JAR = JAVA_CONTRIB + "/javaws/jnlp.jar";


    //----------------------------------------------------------------------------
    // simulations-flash
    //----------------------------------------------------------------------------

    public static final String SIMULATIONS_FLASH = "simulations-flash";

    public static final String FLASH_LAUNCHER = SIMULATIONS_FLASH + "/flash-launcher";

    public static final String FLASH_SIMULATIONS_DIR = SIMULATIONS_FLASH + "/simulations";

    public static final String FLASH_COMMON = SIMULATIONS_FLASH + "/common";
    public static final String FLASH_COMMON_SOURCE = FLASH_COMMON + "/src/edu/colorado/phet/flashcommon";
    public static final String FLASH_COMMON_LOCALIZATION = FLASH_COMMON + "/data/localization";

    public static final String FLASH_SOFTWARE_AGREEMENT_ACTIONSCRIPT = FLASH_COMMON_SOURCE + "/SoftwareAgreement.as";

    //----------------------------------------------------------------------------
    // simulations-flex
    //----------------------------------------------------------------------------

    public static final String SIMULATIONS_FLEX = "simulations-flex";

    public static final String FLEX_SIMULATIONS_DIR = SIMULATIONS_FLEX + "/simulations";

    //----------------------------------------------------------------------------
    // simulations-common
    //----------------------------------------------------------------------------

    public static final String SIMULATIONS_COMMON = "simulations-common";

    public static final String SOFTWARE_AGREEMENT_PATH = SIMULATIONS_COMMON + "/data/software-agreement";
    public static final String SOFTWARE_AGREEMENT_PROPERTIES = SOFTWARE_AGREEMENT_PATH + "/software-agreement.properties";
    public static final String SOFTWARE_AGREEMENT_HTML = SOFTWARE_AGREEMENT_PATH + "/software-agreement.htm";

    //----------------------------------------------------------------------------
    // statistics
    //----------------------------------------------------------------------------

    public static final String STATISTICS = "statistics";

    //----------------------------------------------------------------------------
    // website
    //----------------------------------------------------------------------------

    public static final String WEBSITE = "web/wicket-website";

    //----------------------------------------------------------------------------
    // util
    //----------------------------------------------------------------------------

    public static final String UTIL = "util";

    public static final String PHET_UPDATER = UTIL + "/phet-updater";
    public static final String TRANSLATION_UTILITY = UTIL + "/translation-utility";
    public static final String TIMESHEET = UTIL + "/timesheet";


    //----------------------------------------------------------------------------
    // tigercat
    //----------------------------------------------------------------------------

    // TODO: refactor these into PhetProductionServer ?
    // TODO: remove this
    public static final String TIGERCAT_HTDOCS = "/web/chroot/phet/usr/local/apache/htdocs";

    public static final String BUILD_TOOLS_PROD_SERVER_DEPLOY_PATH = "/files/build-tools";
    public static final String PHET_UPDATER_PROD_SERVER_DEPLOY_PATH = "/files/phet-updater";
    public static final String TRANSLATION_UTILITY_PROD_SERVER_DEPLOY_PATH = "/files/translation-utility";
    public static final String TIMESHEET_PROD_SERVER_DEPLOY_PATH = "/files/timesheet";


    //----------------------------------------------------------------------------
    // within jars
    //----------------------------------------------------------------------------

    public static final String JAVA_JAR_LOCALIZATION = "/phetcommon/localization/";
    public static final String FLASH_JAR_LOCALIZATION = "/";
}
