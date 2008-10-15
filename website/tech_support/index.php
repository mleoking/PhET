<?php

if (!defined("SITE_ROOT")) define("SITE_ROOT", "../");
include_once(SITE_ROOT."admin/global.php");
include_once(SITE_ROOT."page_templates/SitePage.php");

class TroubleshootingPage extends SitePage {

    function render_content() {
        $result = parent::render_content();
        if (!$result) {
            return $result;
        }

        $no_mac = SIM_NO_MAC_IMAGE_HTML;

        $phet_help_email = PHET_HELP_EMAIL;

        $os_min_version_osx = OS_MIN_VERSION_OSX;

        $java_min_version_generic = JAVA_MIN_VERSION_GENERIC;
        $java_min_version_win_full = JAVA_MIN_VERSION_WIN_FULL;
        $java_min_version_osx_full = JAVA_MIN_VERSION_OSX_FULL;
        $java_min_version_lin_full = JAVA_MIN_VERSION_LIN_FULL;
        $flash_min_version_full = FLASH_MIN_VERSION_FULL;

        print <<<EOT
            <p>This page will help you solve some of the problems people commonly have running our programs. If you can't solve your problem here, please notify us by email at the following address: <a href="mailto:{$phet_help_email}?Subject=Help"><span class="red">{$phet_help_email}</span></a>.</p>

            <ul class="content-points">
                <li><a href="support-java.php">Java Installation and Troubleshooting</a></li>

                <li><a href="support-flash.php">Flash Installation and Troubleshooting</a></li>

                <li><a href="support-javascript.php">JavaScript Troubleshooting (note: this is for your browser, not the simulations)</a></li>

            </ul>

            <h2>FAQ's</h2>

            <div id="faq">
                <ul id="nav">
                    <li class="faq"><a href="#q1">Why can I run some of the simulations but not all?</a></li>

                    <li class="faq"><a href="#q2">What are the System Requirements for running PhET simulations?</a></li>

                    <li class="faq"><a href="#q4">I use Internet Explorer and the simulations do not run on my computer.</a></li>

                    <li class="faq"><a href="#q5">Why don't Flash simulations run on my computer?</a></li>

                    <li class="faq"><a href="#q6">What is the ideal screen resolution to run PhET simulations?</a></li>

                    <li class="faq"><a href="#q7">I have Windows 2000 and can run Flash simulations but the Java based simulations do not work.</a></li>

                    <li class="faq"><a href="#q8">Why do PhET simulations run slower on my laptop than on a desktop?</a></li>

                    <li class="faq"><a href="#q9">Why does my computer crash when I run one of the simulations that has sound?</a></li>

                    <li class="faq"><a href="#q10">I would like to translate PhET Simulations into another Language. Can this be easily done?</a></li>

                    <li class="faq"><a href="{$this->prefix}about/licensing.php">What are Licensing requirements?</a></li>
                </ul>
            </div><br />
            <br />

            <h3 id="q1" >Why can I run some of the simulations but not all?</h3>

            <p>Some of our simulations are Java Web Start based applications and others use Macromedia's Flash player. Flash comes with most computers while Java Web Start is a free application that can be downloaded from Sun Microsystems. To run the Java-based simulations you must have Java version {$java_min_version_generic} or higher installed on your computer.</p>

            <p><a href="{$this->prefix}tech_support/support-java.php">Learn about Java installation and Troubleshooting here</a>.</p>

            <p><a href="#top"><img src="{$this->prefix}images/top.gif" alt="Go to top" /></a></p>

            <h3 id="q2" >What are the System Requirements for running PhET simulations?</h3>

            <p><strong>Windows Systems</strong><br />
            Intel Pentium processor<br />
            Microsoft Windows 98SE/2000/XP/Vista<br />
            256MB RAM minimum<br />
            Approximately 60MB available disk space (for full installation)<br />
            1024x768 screen resolution or better<br />
            {$java_min_version_win_full} or later<br />
            {$flash_min_version_full} or later<br />
            Microsoft Internet Explorer 6 or later, Firefox 2 or later</p>

            <p><strong>Macintosh Systems</strong><br />
            G3, G4, G5 or Intel processor<br />
            OS {$os_min_version_osx} or later<br />
            256MB RAM minimum<br />
            Approximately 40 MB available disk space (for full installation)<br />
            1024x768 screen resolution or better<br />
            {$java_min_version_osx_full} or later<br />
            {$flash_min_version_full} or later<br />
            Safari 2 or later, Firefox 2 or later</p>

            <p><a href="#top"><img src="{$this->prefix}images/top.gif" alt="Go to top" /></a></p>

            <p><strong>Linux Systems</strong><br />
            Intel Pentium processor<br />
            256MB RAM minimum<br />
            Approximately 40 MB disk space (for full installation)<br />
            1024x768 screen resolution or better<br />
            {$java_min_version_lin_full} or later<br />
            {$flash_min_version_full} or later<br />
            Firefox 2 or later<br/></p>

            <p><strong>Support Software</strong></p>

            <p>Some of our simulations use Java, and some use Flash. Both of these are available as free downloads, and our downloadable installer includes Java for those who need it.</p>

            <p><a href="#top"><img src="{$this->prefix}images/top.gif" alt="Go to top" /></a></p>

            <h3 id="q4" >I use Internet Explorer and the simulations do not run on my computer.</h3>

            <p>We <strong>strongly</strong> recommend you use the latest version of Internet Explorer (IE7).</p>

            <p><strong>Internet Explorer Security Settings</strong></p>

            <p>Some installations of Internet Explorer, particularly under Windows XP SP2, have default security settings which can impede some aspects of how your locally installed PhET interface functions. For the best user experience while using our simulations installed on your computer, we recommend following the steps below:</p>

            <ol>
                <li>In Internet Explorer on your local workstation, choose Tools &gt; Internet Options.</li>
                <li>Choose the Advanced tab, then scroll to the Security section.</li>
                <li>Enable "Allow active content to run in files on my computer".</li>
                <li>Choose OK.</li>
            </ol>

            <p><a href="#top"><img src="{$this->prefix}images/top.gif" alt="Go to top" /></a></p>

            <h3 id="q5" >Why don't Flash simulations run on my computer?</h3>

            <p><strong>QuickTime™ and Flash™ compatibility</strong></p>

            <p>It has come to our attention that some of our users are unable to use our Flash-based simulations due to a compatibility issue between Apple Computer's QuickTime&trade; and the Flash&trade; player. Some users have reported that uninstalling QuickTime resolves the issue.
            </p>

            <p>We are aware that this is not an acceptable solution and are working to resolve this issue. If you are experiencing this problem, please contact us at at <a href="mailto:{$phet_help_email}?Subject=Flash%20Simulations">{$phet_help_email}</a> and regularly check back here for more information.</p>

            <p><a href="#top"><img src="{$this->prefix}images/top.gif" alt="Go to top" /></a></p>

            <h3 id="q6" >What is the ideal screen resolution to run PhET simulations?</h3>

            <p>PhET simulations work best at a screen resolution of 1024 x 768 pixels. (Some of them are written so that they cannot be resized.) At lower resolution (e.g. 800 x 600), all the controls may not fit on your screen. At higher resolution (e.g. 1280 x 1024), you may not be able to make the simulation fill the whole screen, or if you do, it may slow down performance. To change your screen resolution, follow the directions below:</p>

                <p><strong>Windows</strong></p>

                <ol>
                    <li>From Start menu, click on “Control Panel.” </li>
                    <li>Double click on “Display” icon. </li>
                    <li>Select the “Settings” tab. </li>
                    <li>Use the “Screen resolution” slider to select a resolution and click “OK.” </li>
                </ol>

                <p><strong>Macintosh</strong></p>

                <ol>
                    <li>Open the System Preferences (either from the Dock or from the Apple menu). </li>
                    <li>Open the Displays Panel and choose the Display tab. </li>
                    <li>On the left of the Displays tab you can select one of the Resolutions from the list. </li>
                    <li>Quit or close the System Preferences when done. </li>
                </ol>

            <p><a href="#top"><img src="{$this->prefix}images/top.gif" alt="Go to top" /></a></p>

            <h3 id="q7" >I have Windows 2000 and can run Flash simulations but the Java based simulations do not work.</h3>

            <p>Some Windows 2000 systems have been reported to lack part of the necessary Java configuration. These systems will typically start our Flash-based simulations reliably, but will appear to do nothing when launching our Java-based simulations.</p>

            <p><strong>To resolve this situation, please perform the following steps:</strong></p>

            <ol>
                <li>From the desktop or start menu, open "My Computer"</li>
                <li>Click on the "Folder Options" item in the "Tools" menu</li>
                <li>Click on the "File Types" tab at the top of the window that appears</li>
                <li>Locate "JNLP" in the "extensions" column, and click once on it to select the item</li>
                <li>Click on the "change" button</li>
                <li>When asked to choose which program to use to open JNLP files, select "Browse"</li>
                <li>Locate the program "javaws" or "javaws.exe" in your Java installation folder (typically "C:\Program Files\Java\j2re1.xxxx\javaws", where "xxxx" is a series of numbers indicating the software version; choose the latest version)</li>
                <li>Select the program file and then click "Open" to use the "javaws" program to open JNLP files.</li>
            </ol>

            <p>Java-based simulations should now function properly.</p>

            <p>Please contact us by email at <a href="mailto:{$phet_help_email}?Subject=Windows%202000%20Issues">{$phet_help_email}</a> if you have any further difficulties.</p>

            <p><a href="#top"><img src="{$this->prefix}images/top.gif" alt="Go to top" /></a></p>

            <h3 id="q8" >Why do PhET simulations run slower on my laptop than on a desktop?</h3>

                <p>On some laptop computers, simulations may appear to run much slower than anticipated and/or exhibit unexpected graphics problems. This may be due to power management settings that affect how the computer's graphics system runs and can be corrected by either a) changing the computer's power management configuration, or b) using the laptop computer while plugged in to an AC power source.</p>

                <p>Many laptop computers are configured to reduce the amount of battery power used by the graphics/video system while the computer is running on battery power. If you must use the laptop while it is not plugged in, we suggest changing your computer's power management settings to "maximize performance" while unplugged. This should ensure that the graphics system runs at its peak speed. The location of this setting varies from one manufacturer to the next and we suggest contacting your computer vendor if you have difficulty locating it. Please contact us at <a href="mailto:{$phet_help_email}?Subject=Laptop%20Performance%20Issues">{$phet_help_email}</a> if you continue to encounter problems.</p>

            <p><a href="#top"><img src="{$this->prefix}images/top.gif" alt="Go to top" /></a></p>

            <h3 id="q9" >Why does my computer crash when I run one of the simulations that has sound?</h3>

            <p>Simulations that use sound can be unstable when run on computers using old device driver software. If you are encountering crashes or other undesirable behavior with any of our simulations that use sound, we advise updating your sound drivers, as this may solve the problem. For assistance with updating your sound drivers, contact your computer vendor or audio hardware manufacturer. Contact us at <a href="mailto:{$phet_help_email}?Subject=Sound%20Issues">{$phet_help_email}</a> if you continue to encounter difficulty. </p>

            <p><a href="#top"><img src="{$this->prefix}images/top.gif" alt="Go to top" /></a></p>

            <h3 id="q10" >I would like to translate PhET Simulations into another Language. Can this be easily done?</h3>

            <p>The PhET simulations have been written so that they are easily translated to languages other than English. Please <a href="{$this->prefix}contribute/index.php">click here</a> for more information.</p>

EOT;
    }

}

$page = new TroubleshootingPage("Troubleshooting", NAV_TECH_SUPPORT, null);
$page->update();
$page->render();

?>
