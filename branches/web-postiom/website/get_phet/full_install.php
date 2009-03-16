<?php

// In each web accessable script SITE_ROOT must be defined FIRST
if (!defined("SITE_ROOT")) define("SITE_ROOT", "../");

// See global.php for an explaination of the next line
require_once(dirname(dirname(__FILE__))."/include/global.php");

require_once("include/installer-utils.php");

class FullInstallPage extends SitePage {

    function render_content() {
        $result = parent::render_content();
        if (!$result) {
            return $result;
        }

        //
        // Get the distribution files and their size

        // Windows
        $win_file = installer_get_win_filename();
        $win_url = urlencode($win_file);
        $win_size = installer_get_win_filesize();
        if ($win_size == 0) $win_size = "&lt;unknown&gt;";

        // Mac
        $mac_file = installer_get_mac_filename();
        $mac_url = urlencode($mac_file);
        $mac_size = installer_get_mac_filesize();
        if ($mac_size == 0) $mac_size = "&lt;unknown&gt;";

        // Linux
        $lin_file = installer_get_linux_filename();
        $lin_url = urlencode($lin_file);
        $lin_size = installer_get_linux_filesize();
        if ($lin_size == 0) $lin_size = "&lt;unknown&gt;";

        // CD-ROM
        $cd_file = installer_get_cdrom_filename();
        $cd_url = urlencode($cd_file);
        $cd_size = installer_get_cdrom_filesize();
        if ($cd_size == 0) $cd_size = "&lt;unknown&gt;";

        $java_min_version_generic = JAVA_MIN_VERSION_GENERIC;
        $flash_min_version = FLASH_MIN_VERSION;
        print <<<EOT
            <p>
                The full PhET Offline Website Installer package installs a copy of the PhET website onto your computer. Once installed, you do not need to be connected to the Internet
                to view or run any of the simulations (as long as you have <a href="http://www.java.com/">Java</a>, <a href="http://www.adobe.com/shockwave/download/download.cgi?P1_Prod_Version=shockwaveFlash">Flash</a>, and a web browser such as <a href="http://www.mozilla.com/en-US/">Firefox</a> or <a href="http://www.microsoft.com/windows/products/winfamily/ie/default.mspx">Internet Explorer</a>).
            </p>

            <p>
                Java is included in the Windows download.  Mac OS X users alroady have Java preinstalled.  Linux users are recommended to find a suitable version for their system.
            </p>

            <p>
                These installers of the PhET simulations are updated frequently. We suggest uninstalling your earlier versions before installing a new one.
            </p>

            <ul>
                <li><a href="{$this->prefix}admin/get-member-file.php?file={$win_url}">Download installer for Windows (includes Java)</a> - {$win_size} MB</li>

                <li><a href="{$this->prefix}admin/get-member-file.php?file={$mac_url}">Download installer for Mac OS X</a> - {$mac_size} MB</li>

                <li><a href="{$this->prefix}admin/get-member-file.php?file={$lin_url}">Download installer for Linux</a> - {$lin_size} MB</li>
            </ul>

            <hr/>

            <p>If you have an Internet connection that is too slow, we can send you a CD with the full installation package. Click <em><a href="{$this->prefix}about/contact.php">here to contact us</a></em>.</p>

            <h2>Help &amp; Troubleshooting</h2>

            <p>For assistance with installation and compatibility issues, please visit our <a href="{$this->prefix}tech_support/index.php">troubleshooting and support page</a>.</p>

            <h2>Support Software</h2>

            <p>Our simulations require Java {$java_min_version_generic} or newer and Flash {$flash_min_version} or newer, both of which are available at no cost. If you don't have them on your computer or aren't sure about your versions, <a href="{$this->prefix}tech_support/index.php">click here</a>.</p>

            <h2>Creating an Installation CD-ROM</h2>

            <p>You can make a CD-ROM for doing the full PhET installation on computers without Internet access. You will need a computer equipped with a CD writer, a blank CD, and an Internet connection. Please <a href="{$this->prefix}about/contact.php">contact PhET</a> help with any questions.</p>

            <p>Follow the instructions below:</p>

            <ul>
                <li>Download the <a href="{$this->prefix}admin/get-member-file.php?file={$cd_url}">PhET CD-ROM Distribution archive</a> onto your computer ({$cd_size} MB).</li>

                <li>Extract the contents of the archive (on most operating systems, this is done by double-clicking the downloaded file).</li>

                <li>Copy all the files to the CD-ROM.</li>

                <li>Please remember to periodically check for updated versions of the PhET simulations.</li>
            </ul>

EOT;
    }

}

$page = new FullInstallPage("PhET Offline Website Installer - Full Install", NAV_GET_PHET, null);
$page->update();
$page->render();

?>