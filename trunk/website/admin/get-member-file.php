<?php

error_reporting(0);

$GLOBALS['IE6_DOWNLOAD_WORKAROUND'] = true;

include_once("../admin/global.php");

include_once(SITE_ROOT."admin/web-utils.php");

include_once("../page_templates/SitePage.php");
class DownloadPage extends SitePage {
    function __construct($nav_selected_page, $referrer, $page_title = "Download File") {
        $this->render = true;
        parent::__construct($page_title, $nav_selected_page, $referrer, SP_AUTHLEVEL_NONE, false);
    }

    function update() {
        $result = parent::update();
        if (!$result) {
            return $result;
        }

        if (!isset($_REQUEST['file'])) {
            // Nothing to do
            $this->add_content("No file has been specified.");
            $this->render = false;
            return;
        }

        $this->self_url = get_self_url();
        $this->file     = urldecode($_REQUEST['file']);
        $download = isset($_REQUEST['download']) && $_REQUEST['download'] == '1';

        if (!auth_user_validated()) {
            $this->set_title("Download File");
            return;
        }

        $this->set_title("Downloading File");

        if ($download) {
            if ($this->file == '../../phet-dist/'.basename($this->file) && file_exists($this->file)) {
                $contributor = contributor_get_contributor_by_username(auth_get_username());
                $contributor_id = $contributor["contributor_id"];

                // Keep track of download statistics:
                db_insert_row(
                    'download_statistics',
                    array(
                        'download_statistics_file' => $this->file,
                        'contributor_id'           => $contributor_id
                    )
                );

                //$this->render = false;
                //$this->meta_redirect($this->file, 2);
                send_file_to_browser($this->file, null, null, "attachment");
            }
            else {
                $this->add_content("Due to security restrictions, the specified file may not be accessed.");
                $this->render = false;
            }
        }
        else {
            $this->meta_refresh($this->self_url."&amp;download=1", 2);
        }
    }

    function render_content() {
        $result = parent::render_content();
        if (!$result) {
            BasePage::render_content();
            return $result;
        }

        if (!$this->render) {
            return;
        }

        if ($this->authentication_level < SP_AUTHLEVEL_USER) {
            print <<<EOT
                    <p>Before downloading this file, please tell us a bit about yourself. Providing this information will help PhET retain the support of its financial sponsors.</p>
                    <p>Your email will not be shared with anyone. We'll use your email to send you up to four newsletters per year, which describe major updates to the simulations. You may unsubscribe at any time.</p>
                    <p>Please login with your existing account information, or create a new account. If you do not wish to help PhET, you may skip the registration process and <a href="{$this->file}">download the file directly</a>.</p>

EOT;
            $form_action = "get-member-file.php?file={$this->file}";
            print_login_and_new_account_form($form_action, $form_action, "");
            return;
        }

        $name = basename($this->file);

        $size = 0;
        if (file_exists($this->file)) {
            $size = filesize($this->file);
        }

        // Estimate download times for users with fast & slow connections:
        $slow_time = floor($size * 8 / 56000   / 60 + 0.5);
        $fast_time = floor($size * 8 / 1000000 / 60 + 0.5);

        print <<<EOT
        <p>Your download of the file "{$name}" will begin automatically.</p>

        <p>If the download does not complete in a reasonable amount of time, you can try <a href="{$this->file}">downloading the file directly</a>.</p>

        <div id="downloadspeeds">
            <table>
                <tr>
                    <td>&nbsp;</td>            <td>Slow Connection</td>        <td>Fast Connection</td>
                </tr>

                <tr>
                    <td>Time</td>            <td>{$slow_time} minutes</td>        <td>{$fast_time} minutes</td>
                </tr>
            </table>
        </div>

EOT;

    }
}

$download_page = new DownloadPage(NAV_NOT_SPECIFIED, null);
$download_page->update();
$download_page->render();

exit;
    
// Old content kept for refernce
    
    
    
    function print_content() {
        $custom_title = "Download File";
        $custom_body  = <<<EOT
            <p>Before downloading this file, please tell us a bit about yourself. Providing this information will help PhET retain the support of its financial sponsors.</p>
            <p>Your email will not be shared with anyone. We'll use your email to send you up to four newsletters per year, which describe major updates to the simulations. You may unsubscribe at any time.</p>
            <p>Please login with your existing account information, or create a new account. If you do not wish to help PhET, you may skip the registration process and <a href="$file">download the file directly</a>.</p>

EOT;
        if (!auth_user_validated()) {
            print_login_and_new_account_form("get-member-file.php", "get-member-file.php", "");
        }

        global $file;

        $name = basename($file);

        $size = filesize($file);

        // Estimate download times for users with fast & slow connections:
        $slow_time = floor($size * 8 / 56000   / 60 + 0.5);
        $fast_time = floor($size * 8 / 1000000 / 60 + 0.5);

        print <<<EOT
            <h1>Downloading File</h1>

            <p>Your download of the file "$name" will begin automatically.</p>

            <p>If the download does not complete in a reasonable amount of time, you can try <a href="$file">downloading the file directly</a>.</p>

            <div id="downloadspeeds">
                <table>
                    <tr>
                        <td>&nbsp;</td>            <td>Slow Connection</td>        <td>Fast Connection</td>
                    </tr>

                    <tr>
                        <td>Time</td>            <td>$slow_time minutes</td>        <td>$fast_time minutes</td>
                    </tr>
                </table>
            </div>

EOT;

    }

    if ($download) {
        if ($file == '../../phet-dist/'.basename($file) && file_exists($file)) {
            // Keep track of download statistics:
            db_insert_row(
                'download_statistics',
                array(
                    'download_statistics_file' => $file,
                    'contributor_id'           => $contributor_id
                )
            );

            send_file_to_browser($file, null, null, "attachment");
        }
        else {
            print "Due to security restrictions, the specified file may not be accessed.";
        }
    }
    else {
        print_site_page('print_content', NAV_NOT_SPECIFIED, $self."&amp;download=1", 1);
    }

?>