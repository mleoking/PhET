package edu.colorado.phet.buildtools;

/**
 * Should hold information about directory structure under trunk/
 */
public class BuildToolsPaths {

    //----------------------------------------------------------------------------
    // build-tools
    //----------------------------------------------------------------------------

    public static final String BUILD_TOOLS_DIR = "build-tools";

    public static final String FLASH_HTML_TEMPLATE = BUILD_TOOLS_DIR + "/data/flash/flash-template.html";
    public static final String FLASH_BUILD_TEMPLATE = BUILD_TOOLS_DIR + "/data/flash/build-template.jsfl";
    public static final String FLASH_GET_FLASH_IMAGE = BUILD_TOOLS_DIR + "/data/flash/get_flash.jpg";


    //----------------------------------------------------------------------------
    // simulations-java
    //----------------------------------------------------------------------------

    public static final String SIMULATIONS_JAVA = "simulations-java";


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
    // miscellaneous
    //----------------------------------------------------------------------------

    public static final String TIGERCAT_HTDOCS = "/web/chroot/phet/usr/local/apache/htdocs";

}
