<?php

if (!defined("SITE_ROOT")) define("SITE_ROOT", "../");
include_once(SITE_ROOT."admin/global.php");
include_once(SITE_ROOT."page_templates/SitePage.php");

class JavaSupportPage extends SitePage {

    function render_content() {
        $result = parent::render_content();
        if (!$result) {
            return $result;
        }

        $phet_help_email = PHET_HELP_EMAIL;

        $java_min_version_generic = JAVA_MIN_VERSION_GENERIC;

        print <<<EOT
            <p>PhET's Java-based simulations use Sun Microsystems' Java Web Start technology to launch the simulations. This page will help you ensure that you have Java installed properly, and address some of the problems people might have running our programs. If you can't solve your problem here, please notify us by email at the following address: <a href="mailto:{$phet_help_email}"><span class="red">{$phet_help_email}</span></a>.</p>

            <h2 id="faq_top">FAQ's</h2>

            <div id="faq">
                <ul id="nav">
                    <li class="faq"><a href="#q1">How do I get Java?</a></li>

                    <li class="faq"><a href="#q2">Why do you use Java Web Start instead of Java?</a></li>

                    <li class="faq"><a href="#q3">Are there any problems running the simulations on a Mac?</a></li>

                    <li class="faq"><a href="#q4">How do I check my computer's current version of Java?</a></li>

                    <li class="faq"><a href="#q5">I have Windows 2000 and I can only get the Flash based simulations to work.</a></li>

                    <li class="faq"><a href="#q6">General Java troubleshooting</a></li>

                    <li class="faq"><a href="#q7">Troubleshooting tips for networked computers</a></li>
                </ul>
            </div><br />
            <br />

            <h3 id="q1" >How do I get Java?</h3>

            <p>To run the Java-based simulations you must have Java version {$java_min_version_generic} or higher installed on your computer. You can obtain the free downloads by clicking on the button below:<br />
            <a href="http://www.java.com/en/index.jsp"><img src="{$this->prefix}images/java-jump.gif" alt="Java Jump" /></a></p>

            <p><strong>Note for Netscape Users</strong></p>

            <p> After you have installed Java Web Start, you will need to close and re-open your browser for Java Web Start to work.</p>

            <p><a href="#faq_top"><img src="{$this->prefix}images/top.gif" alt="Go to top" /></a></p>

            <h3 id="q2" >Why do you use Java Web Start instead of Java?</h3>

            <p>We use Java Web Start technology rather than Java Applets. Java Web Start, which is a free mechanism from Sun, is a more robust way of launching Java programs over the Web than applets. If you have had problems running applets in the past, you may well find that Java Web Start works much better. If you should have any problems, we will be happy to help you solve them. If the information on this page doesnít help, just send us email at <a href="mailto:{$phet_help_email}">{$phet_help_email}</a>.</p>

            <p><a href="#faq_top"><img src="{$this->prefix}images/top.gif" alt="Go to top" /></a></p>

            <h3 id="q3" >Are there any problems running the simulations on a Mac?</h3>

            <p>Macintosh OS X 10.4 or higher is required to run our Java programs. You must have the latest version of Java (which will already be installed if you are using the auto-update feature of OS X). See http://www.apple.com/java/ for details.</p>

            <p>Versions of Apple's Safari browser below 2.0 (included with OS X 10.4) do not launch our Java-based applications reliably.</p>

            <p><a href="#faq_top"><img src="{$this->prefix}images/top.gif" alt="Go to top" /></a></p>

            <h3 id="q4" >How do I check my computer's current version of Java?</h3>

            <p><strong><em>Windows users:</em></strong><br />
            From a command line (Start&nbsp;menu-&gt;All&nbsp;Programs-&gt;Accessories-&gt;Command&nbsp;Prompt), type: <strong>java&nbsp;-version</strong> followed by the enter key. You should see some text which will include something like "(build 1.4.2_05_05-b04)"; this number is the version of Java you are using. If you receive an error, Java is not properly installed (see above to reinstall).</p>

            <p><strong><em>Macintosh users:</em></strong><br />
            From the Apple menu, click on "About this Mac", then on "More Info...". You will find the Java software version under "Software-&gt;Applications".</p>

            <p><a href="#faq_top"><img src="{$this->prefix}images/top.gif" alt="Go to top" /></a></p>

            <h3 id="q5" >I have Windows 2000 and I can only get the Flash based simulations to work.</h3>

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

            <p><a href="#faq_top"><img src="{$this->prefix}images/top.gif" alt="Go to top" /></a></p>

            <h3 id="q6" >General Java troubleshooting</h3>

            <p>The following are some general steps that you may wish to perform when attempting to solve Java-related problems:</p>

            <ul>
                <li><strong>Empty Your Browser's Cache</strong>
                
                <br/>
                
                <table>
                    <thead>
                        <tr>
                            <td>Internet Explorer</td>  <td>Firefox</td>
                        </tr>
                    </thead>
                    
                    <tbody>
                        <tr>
                            <td>Select "Tools-&gt;Options", then click on the "Delete Files..." button under "Temporary Internet Files"</td>
                            
                            <td>Select "Tools-&gt;Options-&gt;Privacy", then click the "Clear" button next to "Cache".</td>
                        </tr>
                    </tbody>
                </table>
                
                </li>

                <li><strong>Empty Java Web Start's Cache</strong><br />
                From Windows' Start menu, select "Java Web Start", then select "Preferences" from the File menu. Click "Advanced", then click the "Clear Folder" button.
                </li>

                <li><strong>Make Sure Windows can find Java and Java Web Start</strong><br />
                From a command line (Start menu-&gt;Accessories-&gt;Command Prompt), type <em>java -version</em> followed by the enter key. If you receive an error, Java is not properly installed. Please try reinstalling Java (see above) after uninstalling any Java entries in your list of installed programs (Control Panel -&gt;Add/Remove Programs).
                </li>
            </ul>

            <p><a href="#faq_top"><img src="{$this->prefix}images/top.gif" alt="Go to top" /></a></p>

            <h3 id="q7" >Troubleshooting tips for networked computers</h3>

            <p><strong>Why do simulations run for admin but not all users?</strong></p>

            <p>Java simulations may also fail to start on networked computers running Windows for some users while running properly for administrators. When Java Web Start tries to launch a simulation, it attempts to cache certain files in a "cache folder". This folder (or "directory") is determined when Java is installed, and may point to a folder that non-administrator accounts do not have access to, resulting in a launch failure. Correct this problem by setting the cache directory to one of your own choosing: Log on as an administrator.</p>

            <p>Launch the Java Web Start Application Manager. This can be done in one of two ways. a) If the Java installation placed an icon either on your desktop or in the Start Menu -&gt; All Programs list titled "Java Web Start", click it to start the application. b) Use the Windows search function (located in the Start Menu) to search for the program "javaws.exe". Once it is found, click on the file to run the application. Once the Java Web Start Application Manager is up and running, choose File&gt;Preferences. In the Preferences dialog that appears, click on the Advanced tab. In this tab, you will find a text field labeled "Applications Folder". In this text field, put the name of an empty directory to which all users have write privileges. Note: This directory MUST be empty. All users should now be able to launch the Java simulations.</p>

            <p><strong>Issue 3: Web Proxy Settings</strong><br />
            Java simulations may fail to start if the proxy settings in Java Web Start's configuration panel (Start Menu-&gt;Java Web Start, File Menu-&gt;Preferences) do not match those your system is currently using. To identify settings being used on your system, see your web browser's proxy settings, or contact you network administrator.</p>

            <p><strong>Local Install on Boot Drive Only</strong><br />
            If you are operating in a networked environment, please ensure that both Java and the PhET simulations (if installed locally rather than running from our web site) are installed on local, non-networked drives. The PhET simulations must be installed on the boot drive.</p>

            <p><strong>Network Firewalls</strong><br />
            Please ensure that your firewall is configured to allow both Java and Java Web Start to communicate through the firewall.</p>

            <p><a href="#faq_top"><img src="{$this->prefix}images/top.gif" alt="Go to top" /></a></p>

EOT;
    }

}

$page = new JavaSupportPage("Troubleshooting Java", NAV_TECH_SUPPORT, null);
$page->update();
$page->render();

?>
