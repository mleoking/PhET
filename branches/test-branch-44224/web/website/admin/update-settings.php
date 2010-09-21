<?php

// In each web accessable script SITE_ROOT must be defined FIRST
if (!defined("SITE_ROOT")) define("SITE_ROOT", "../");

// See global.php for an explaination of the next line
require_once(dirname(dirname(__FILE__))."/include/global.php");

class UpdateConfigurationPage extends SitePage {

    function get_settings() {
        $util = UpdateUtils::inst();
        return $util->getSettings();
        $extra = "ORDER BY `update_settings_id` DESC LIMIT 1";
        $db_settings = db_get_row_by_condition($this->SETTINGS_TABLE, array(), false, $extra);
        $settings = array();

        foreach ($this->COL_MAP as $key => $column) {
            if (isset($db_settings[$column])) {
                $settings[$key] = $db_settings[$column];
            }
            else {
                $settings[$key] = $this->DEFAULT_MAP[$key];
            }
        }

        return $settings;
    }

    function set_settings($request) {
        $this->success = UpdateUtils::inst()->setSettings($request);
    }

    function update() {
        $result = parent::update();
        if (!$result) {
            return $result;
        }

        if (isset($_REQUEST['submit'])) {
            $this->success = UpdateUtils::inst()->setSettings($_REQUEST);
        }

        $this->settings = $this->get_settings();
    }

    function submit_box($box_style, $html, $title = true) {
        $title_html = '';
        if ($title) {
            $title_html = "<p class=\"result_box_heading\">Action result:</p><br />";
        }
        return <<<EOT
            <div class="{$box_style}">
            {$title_html}
            <p>{$html}</p>
            </div>

EOT;
    }
    function render_content() {
        $result = parent::render_content();
        if (!$result) {
            return $result;
        }

        if (isset($this->success)) {
            if ($this->success) {
                print $this->submit_box('result_box_success', 'Submission successful!');
            }
            else {
                print $this->submit_box('result_box_failure', 'Problems encountered, submission has <strong>NOT</strong> been recorded!');
            }
        }

        foreach ($this->settings as $field => $value) {
            $input[$field] = "<input name=\"{$field}\" value=\"{$value}\" />";
        }

        print <<<EOT
<form method="get" action="update-settings.php">
<hr />
<h2>Simulation Configuration</h2>
<h3>For users running a simulation from an individually downloaded JAR</h3>
<table class="form">
<tbody>
<tr>
<td>
            Ask me later (in days)
</td>
<td>
            {$input['sim_ask_later_duration']}
<p>            <em>When a simulation user is prompted for an update and selects "Ask me later", the number of days before it will ask again.</em>
</p>
</td>
</tr>
</tbody>
</table>
<hr />
<h2>Installation Configuration</h2>
<h3>For users running a simulation from a PhET installation</h3>
<table class="form">
<tbody>
<tr>
<td>
            Ask me later (in days)
</td>
<td>
            {$input['install_ask_later_duration']}
<p>            <em>When an installation user is prompted for an update and selects "Ask me later", the number of days before it will ask again.</em>
</p>
</td>
</tr>
</tbody>
</table>
<p>
            <strong>Recommend update condition</strong> <br />
</p>
<table class="form">
<tbody>
<tr>
<td>
            Days old
</td>
<td>
            {$input['install_recommend_update_age']}
</td>
</tr>
<tr>
<td>
            Before date
</td>
<td>
            {$input['install_recommend_update_date']}
<br /> <em><strong>format:</strong> YYYY-MM-DD, <strong>ex:</strong> 2008-02-16</em>
</td>
</tr>
<tr>
<td>
</td>
<td>
<p class="p-extra-indentation" >
            <em>These are the conditions which a user will be recommended for an update.  If the installation is older than the specified number of days, or created before the specified date, they will be recommended to update their installation.</em>
</p>
</td>
</tr>
</tbody>
</table>
<hr />
<p style="margin-left: 5em;">
<input type="submit" class="button" name="submit" value="Submit" />
</p>
<hr />

</form>

EOT;
    }

}

$page = new UpdateConfigurationPage("Update Configuration Page", NavBar::NAV_ADMIN, null, SitePage::AUTHLEVEL_TEAM, false);
$page->update();
$page->render();

?>