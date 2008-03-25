<?php
    error_reporting(0);

    $GLOBALS['IE6_DOWNLOAD_WORKAROUND'] = true;

    include_once("../admin/global.php");

    include_once(SITE_ROOT."admin/web-utils.php");

    $self     = get_self_url();
    $file     = urldecode($_REQUEST['file']);
    $download = isset($_REQUEST['download']) && $_REQUEST['download'] == '1';

    $custom_title = "Download File";
    $custom_body  = <<<EOT
        <p>Before downloading this file, please tell us a bit about yourself. Providing this information will help PhET retain the support of its financial sponsors.</p>
        <p>Your email will not be shared with anyone. We'll use your email to send you up to four newsletters per year, which describe major updates to the simulations. You may unsubscribe at any time.</p>
        <p>Please login with your existing account information, or create a new account. If you do not wish to help PhET, you may skip the registration process and <a href="$file">download the file directly</a>.</p>
EOT;

    include_once(SITE_ROOT."admin/authentication.php");

    include_once(SITE_ROOT."admin/sys-utils.php");
    include_once(SITE_ROOT."admin/site-utils.php");
    include_once(SITE_ROOT."admin/db-utils.php");

    do_authentication(true);

    function print_content() {
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
        print_site_page('print_content', -1, $self."&amp;download=1", 1);
    }
?>