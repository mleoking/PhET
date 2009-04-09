<?php

    // In each web accessable script SITE_ROOT must be defined FIRST
    if (!defined("SITE_ROOT")) define("SITE_ROOT", "../");

    // See global.php for an explaination of the next line
    require_once(dirname(dirname(__FILE__))."/include/global.php");

    require_once("include/db.php");
    require_once("include/db-utils.php");
    require_once("include/web-utils.php");

    // TODO: naming scheme in this file is inconsistent with other parts of website code

    function is_contribution_nominated_by($contribution_id, $contributor_id) {
        $row = db_get_row_by_condition(
            'contribution_nomination',
            array(
                'contribution_id' => $contribution_id,
                'contributor_id'  => $contributor_id
            )
        );

        if (!$row) return false;

        return true;
    }

    function get_all_nominations_for_contribution($contribution_id) {
        return db_get_rows_by_condition('contribution_nomination', array('contribution_id' => $contribution_id));
    }

    function get_nomination_count_for_contribution($contribution_id) {
        return count(get_all_nominations_for_contribution($contribution_id));
    }

    function nominate_contribution($contribution_id, $contribution_nomination_desc, $contributor_id) {
        if (is_contribution_nominated_by($contribution_id, $contributor_id)) {
            return true;
        }

        return db_insert_row(
            'contribution_nomination',
            array(
                'contribution_id' => $contribution_id,
                'contributor_id'  => $contributor_id,
                'contribution_nomination_desc' => $contribution_nomination_desc
            )
        );
    }

    function get_nomination_statistics() {
        static $contrib_to_nom_count = null;

        if ($contrib_to_nom_count != null) {
            return $contrib_to_nom_count;
        }

        $nominations = db_get_all_rows('contribution_nomination');

        $contrib_to_nom_count = array();

        foreach ($nominations as $nomination) {
            $contribution_id = $nomination['contribution_id'];

            if (!isset($contrib_to_nom_count["$contribution_id"])) {
                $contrib_to_nom_count["$contribution_id"] = 0;
            }

            $contrib_to_nom_count["$contribution_id"] = $contrib_to_nom_count["$contribution_id"] + 1;
        }

        asort($contrib_to_nom_count);

        return $contrib_to_nom_count;
    }

    function get_nomination_descriptions() {
        static $contrib_to_descs = null;

        if ($contrib_to_descs != null) {
            return $contrib_to_descs;
        }

        $nominations = db_get_all_rows('contribution_nomination');

        $contrib_to_descs = array();

        foreach ($nominations as $nomination) {
            $contribution_id = $nomination['contribution_id'];

            if (!isset($contrib_to_descs["$contribution_id"])) {
                $contrib_to_descs["$contribution_id"] = '';
            }

            $contrib_to_descs["$contribution_id"] .= '<br/>"<em>'.format_string_for_html($nomination['contribution_nomination_desc']).'</em>"';
        }

        return $contrib_to_descs;
    }

?>