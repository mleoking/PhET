<?php

// In each web accessable script SITE_ROOT must be defined FIRST
if (!defined("SITE_ROOT")) define("SITE_ROOT", "../");

// See global.php for an explaination of the next line
require_once(dirname(dirname(__FILE__))."/include/global.php");

require_once("page_templates/SitePage.php");

class CheckContributionItegrityPage extends SitePage {
    const ENTRY_VALID_DATA = "0";
    const ENTRY_INVALID_DATA = "1";
    const ENTRY_VALID_BLANK = "3";
    const ENTRY_INVALID_BLANK = "4";
    const ENTRY_NOT_CHECKED = "5";

    const ROW_VALID_DATA = "0";
    const ROW_INVALID_DATA = "1";

    const ICON_DIR = "images/icons";

    function __construct($page_title, $nav_selected_page) {
        $this->num_invalid_rows = 0;
        $this->num_rows = 0;

        parent::__construct($page_title, $nav_selected_page, null, AUTHLEVEL_TEAM, false);
    }

    function check_contribution_row($row) {
        $data_row = $row;
        $result = array();
        $any_invalid = false;

        // TODO: debugging measure, either remove or make more clear
        $dodo = false;
        if ($row["contribution_id"] == 445) {
            $dodo = true;
        }

        ++$this->num_rows;
        foreach ($row as $field_name => $value) {
            switch ($field_name) {
                case "contribution_id":
                    // Must be > 0
                    if ($value <= 0) {
                        $result[$field_name] = self::ENTRY_INVALID_DATA;
                        $any_invalid = true;
                    }
                    else {
                        $result[$field_name] = self::ENTRY_VALID_DATA;
                    }
                    break;

                case "contribution_title":
                case "contribution_authors":
                case "contribution_keywords":
                case "contribution_authors_organization":
                    // Must not be blank
                    if (strlen(trim($value)) == 0) {
                        $result[$field_name] = self::ENTRY_INVALID_BLANK;
                        $any_invalid = true;
                    }
                    else {
                        $result[$field_name] = self::ENTRY_VALID_DATA;
                    }
                    break;

                case "contribution_contact_email":
                    if (eregi("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\.[A-Z]{2,4}$", trim($value)) == 0) {
                        $result[$field_name] = self::ENTRY_INVALID_DATA;
                        $any_invalid = true;
                    }
                    else {
                        $result[$field_name] = self::ENTRY_VALID_DATA;
                    }
                    break;

                case "contribution_approved":
                    // Metainfo for PhET
                    $result[$field_name] = self::ENTRY_NOT_CHECKED;
                    break;

                case "contribution_desc":
                    // Give info
                    if (trim($value)) {
                        $result[$field_name] = self::ENTRY_VALID_BLANK;
                    }
                    else {
                        $result[$field_name] = self::ENTRY_VALID_DATA;
                    }
                    break;

                case "contribution_duration":
                    // Give info
                    $result[$field_name] = self::ENTRY_NOT_CHECKED;
                    break;

                case "contribution_answers_included":
                    // Give info
                    $result[$field_name] = self::ENTRY_NOT_CHECKED;
                    break;

                case "contribution_date_created":
                    $created_time = strtotime($value);
                    $earliest_time = strtotime("2005-07-01 00:00:00");
                    if ($earliest_time > $created_time) {
                        $result[$field_name] = self::ENTRY_INVALID_DATA;
                        $any_invalid = true;
                    }
                    else {
                        $result[$field_name] = self::ENTRY_VALID_DATA;
                    }
                    break;

                case "contribution_date_updated":
                    // Ensure good dates, and updated is not before created
                    $updated_time = strtotime($value);
                    $created_time = strtotime($data_row["contribution_date_created"]);
                    $earliest_time = strtotime("2005-07-01 00:00:00");
                    if ($earliest_time > $updated_time) {
                        $result[$field_name] = self::ENTRY_INVALID_BLANK;
                        $any_invalid = true;
                    }
                    else if ($created_time > $updated_time) {
                        $result[$field_name] = self::ENTRY_INVALID_DATA;
                        $any_invalid = true;
                    }
                    else {
                        $result[$field_name] = self::ENTRY_VALID_DATA;
                    }
                    break;

                case "contribution_nomination_count":
                    // Metainfo for PhET
                    $result[$field_name] = self::ENTRY_NOT_CHECKED;
                    break;

                case "contribution_flagged_count":
                    // Metainfo for PhET
                    $result[$field_name] = self::ENTRY_NOT_CHECKED;
                    break;

                case "contribution_standards_compliance":
                    // Give info
                    $result[$field_name] = self::ENTRY_NOT_CHECKED;
                    break;

                case "contribution_from_phet":
                    // Give info
                    $result[$field_name] = self::ENTRY_NOT_CHECKED;
                    break;

                case "contribution_is_gold_star":
                    // Give info
                    $result[$field_name] = self::ENTRY_NOT_CHECKED;
                    break;

                case "contributor_id":
                    // Must not be -1, must map to a real contributor
                    if ($value <= 0) {
                        $result[$field_name] = self::ENTRY_INVALID_BLANK;
                        $any_invalid = true;
                    }
                    else {
                        if (contributor_get_contributor_by_id($value) === false) {
                            $result[$field_name] = self::ENTRY_INVALID_DATA;
                            $any_invalid = true;
                        }
                        else {
                            $result[$field_name] = self::ENTRY_VALID_DATA;
                        }
                    }
                    break;

                default:
                    assert(false);
                    break;
                
            }

        }

        //
        // Now do extra checks

        // Check for files
        $data_row["files_present"] = contribution_get_contribution_file_names($row["contribution_id"]);
        if (count($data_row["files_present"]) > 0) {
            $result["files_present"] = self::ENTRY_VALID_DATA;
        }
        else {
            $result["files_present"] = self::ENTRY_VALID_BLANK;
            //$any_invalid = true;
        }

        // Simulations
        $data_row["simulations"] = contribution_get_associated_simulation_listing_names($data_row["contribution_id"]);
        if (count($data_row["simulations"]) == 0) {
            $result["simulations"] = self::ENTRY_INVALID_BLANK;
            $any_invalid = true;
        }
        else {
            $result["simulations"] = self::ENTRY_VALID_DATA;
        }

        // Types
        $data_row["types"] = contribution_get_types_for_contribution($data_row["contribution_id"]);
        if (count($data_row["types"]) == 0) {
            $result["types"] = self::ENTRY_INVALID_BLANK;
            $any_invalid = true;
        }
        else {
            $result["types"] = self::ENTRY_VALID_DATA;
        }

        // Levels
        $data_row["levels"] = contribution_get_levels_for_contribution($data_row["contribution_id"]);
        if (count($data_row["levels"]) == 0) {
            $result["levels"] = self::ENTRY_INVALID_BLANK;
            $any_invalid = true;
        }
        else {
            $result["levels"] = self::ENTRY_VALID_DATA;
        }

        if ($any_invalid) {
            $result["row_condition"] = self::ROW_INVALID_DATA;
            $data_row["row_condition"] = "Invalid data in row";
            ++$this->num_invalid_rows;
        }
        else {
            $result["row_condition"] = self::ROW_VALID_DATA;
            $data_row["row_condition"] = "Valid data in row";
        }

        return array($data_row, $result);
    }

    function update() {
        $result = parent::update();
        if (!$result) {
            return $result;
        }

        $this->contribution_info = array();

        $table_description = db_describe_table("contribution");

        $contribution_data = db_get_all_rows("contribution");
        $info = array();
        $breakout = 0;
        foreach ($contribution_data as $row) {
            //if (++$breakout == 200) break;
            $resrow = $this->check_contribution_row($row);
            $this->contribution_info[] = $resrow;
        }
    }

    function render_content() {
        $result = parent::render_content();
        if (!$result) {
            return $result;
        }

        $contributor = 
        $skip_keys = array(
                "row_condition",
                "contribution_approved",
                "contribution_duration",
                "contribution_answers_included",
                "contribution_nomination_count",
                "contribution_flagged_count",
                "contribution_standards_compliance",
                "contribution_from_phet",
                "contribution_is_gold_star"
            );

        print "<p>Not a full integrity check yet.  Contribution table is checked for required data, many fields have been skipped as they aren't strictly necessary to validate.</p>\n";
        print "<p>There are <strong>".count($this->contribution_info)."</strong> total contributions.</p>\n";
        print "<p>There are <em>{$this->num_invalid_rows}</em> contributions with invalid data.</p>\n";
        $table_description = db_describe_table("contribution");
        $icon_dir = $this->prefix.self::ICON_DIR;
        print <<<EOT
        <table>
            <thead>
            <tr>
                <th colspan="2">
                    Legend
                </th>
            </tr>
            </thead>

            <tfoot>
                <tr><td></td></tr>
            </tfoot>

            <tbody>
            <tr class="row_valid_data">
                <td class="entry_valid_data"><img src="{$icon_dir}/green_check.gif" alt="VALID" /></td>
                <td>This entry has data, and the data is valid</td>
            </tr>
            <tr class="row_valid_data">
                <td class="entry_valid_blank"><img src="{$icon_dir}/green_check.gif" alt="VALID BLANK" /></td>
                <td>This entry is blank, and data is not required</td>
            </tr>
            <tr class="row_invalid_data">
                <td class="entry_invalid_blank"><img src="{$icon_dir}/red_x.gif" alt="INVALID" /></td>
                <td>This entry has invalid data, but is not blank (note red marking on row when it contains an invalid entry)</td>
            </tr>
            <tr class="row_invalid_data">
                <td class="entry_invalid_blank"><img src="{$icon_dir}/grey_dash.gif" alt="INVALID BLANK" /></td>
                <td>This entry has no data, and data is required (note red marking on row when it contains an invalid entry)</td>
            </tr>
            <tr class="row_valid_data">
                <td class="entry_not_checked"><img src="{$icon_dir}/red_dot.gif" alt="NOT Checked" /></td>
                <td>This entry has not been checked</td>
            </tr>
            </tbody>
        </table>

        <hr />
EOT;

        print "<div class=\"tableContainer\">\n";
        print "<table>\n";
        print "<thead>\n";
        print "<tr>\n";
        print "<th>R<br />o<br />w<br />#</th>\n";
        $row = $this->contribution_info[0][0];
        foreach ($row as $key => $field) {
            if (in_array($key, $skip_keys)) continue;

            $string = str_replace("contribution_", "", $key);
            $string = str_replace("_", " ", $string);
            $string = ereg_replace("(.)", "\\1<br />", $string);
            print "<th>\n";
            print "$string";
            print "</th>\n";
        }
        print "</tr>\n";
        print "</thead>\n";

        $row_num = 0;
        print "<tbody>\n";
        foreach ($this->contribution_info as $info) {
            ++$row_num;
            $data_row = $info[0];
            $integrity_row = $info[1];
            //assert(count($data_row) == count($integrity_row));

            if ($integrity_row["row_condition"] == self::ROW_VALID_DATA) {
                $row_class = "class=\"row_valid_data\"";
            }
            else {
                $row_class = "class=\"row_invalid_data\"";
            }
            print "<tr $row_class >\n";
            print "<td>$row_num</td>\n";

            foreach (array_keys($data_row) as $field) {
                if (in_array($field, $skip_keys)) continue;

                $data = $data_row[$field];
                $integrity = $integrity_row[$field];
                switch ($integrity) {
                    case self::ENTRY_VALID_DATA:
                        // Special case, files present
                        if ($field == "files_present") {
                            $class = "class=\"entry_valid_data\"";
                            $string = "<img src=\"{$icon_dir}/file.gif\" alt=\"VALID\" />";
                        }
                        else {
                            $class = "class=\"entry_valid_data\"";
                            $string = "<img src=\"{$icon_dir}/green_check.gif\" alt=\"VALID\" />";
                        }
                        break;

                    case self::ENTRY_INVALID_DATA:
                        $class = "class=\"entry_invalid_data\"";
                        $string = "<img src=\"{$icon_dir}/red_x.gif\" alt=\"INVALID\" />";
                        break;

                    case self::ENTRY_VALID_BLANK:
                        $class = "class=\"entry_valid_blank\"";
                        $string = "<img src=\"{$icon_dir}/grey_dash.gif\" alt=\"VALID BLANK\" />";
                        break;

                    case self::ENTRY_INVALID_BLANK:
                        $class = "class=\"entry_invalid_blank\"";
                        $string = "<img src=\"{$icon_dir}/grey_dash.gif\" alt=\"INVALID BLANK\" />";
                        break;

                    case self::ENTRY_NOT_CHECKED:
                        $class = "class=\"entry_not_checked\"";
                        $string = "<img src=\"{$icon_dir}/red_dot.gif\" alt=\"NOT Checked\" />";
                        break;

                    default:
                        assert(false);
                        $class = "entry_ERROR";
                        $string = "ERROR";
                        break;
                }

                switch ($field) {
                    case "contribution_id":
                    case "contribution_title":
                        $string .= format_string_for_html(": $data");
                        $string = "<a href=\"{$this->prefix}teacher_ideas/edit-contribution.php?contribution_id={$data_row["contribution_id"]}\">$string</a>";
                        break;
                }

                print "<td $class>";
                print "$string";
                print "</td>\n";
            }

            print "</tr>\n";
        }
        print "</tbody>\n";
        print "</table>\n";
        print "</div>\n";
    }
}

$page = new CheckContributionItegrityPage("Check Database Integrity", NAV_ADMIN);
$page->add_stylesheet("css/scrollable_tables.css");
$page->add_stylesheet("css/db-integrity.css");
$page->update();
$page->render();

?>