<?php

    // Utilities related to the research done by PhET

    if (!defined("SITE_ROOT")) define("SITE_ROOT", "../");
    include_once(SITE_ROOT."admin/global.php");
    include_once(SITE_ROOT."admin/db.php");
    include_once(SITE_ROOT."admin/db-utils.php");
    include_once(SITE_ROOT."admin/web-utils.php");

    function research_get_all() {
        return db_get_rows_by_condition('research', array(), false, false);
    }

    function research_get_by_id($research_id) {
        return db_get_row_by_id('research', 'research_id', $research_id);
    }

    function research_update($research) {
        return db_update_table('research', $research, 'research_id', $research['research_id']);
    }

    function research_delete($research_id) {
        return db_delete_row('research', array( 'research_id' => $research_id ) );
    }

    function research_update_from_script_params() {
        $research = gather_script_params_into_array('research_');

        return research_update($research);
    }

    function research_add_from_script_params() {
        // Get the database connection, start it if if this is the first call
        global $connection;
        if (!isset($connection)) {
            connect_to_db();
        }

        $research = array();

        foreach($_REQUEST as $key => $value) {
            if ("$key" == 'research_id') {
                continue;
            }
            else if (preg_match('/research_.+/i', "$key") == 1) {
                $research["$key"] = mysql_real_escape_string("$value", $connection);
            }
        }

        return db_insert_row('research', $research);
    }

    function research_print($research_id) {
        $research = research_get_by_id($research_id);

        $publication_lookup_html = '';

        $research_publication_lookup = format_for_html($research['research_publication_lookup']);
        if ($research_publication_lookup != '') {
            $publication_lookup_html = <<<EOT
                <span class="publication_lookup">
                    $research_publication_lookup
                </span>,

EOT;
        }

        $other_links_html = '';

        $research_publication_htmllink_url = format_for_html($research['research_publication_htmllink_url']);
        if ($research_publication_htmllink_url != '') {
            $other_links_html .= <<<EOT
                (<span class="publication_htmllink">
                    <a href="$research_publication_htmllink_url">html</a>
                </span>

EOT;
        }

        $research_publication_pdflink_url = format_for_html($research['research_publication_pdflink_url']);
        if ($research_publication_pdflink_url != '') {
            if (strlen($other_links_html) > 0) {
                $other_links_html .= ", ";
            }

            $other_links_html .= <<<EOT
                <span class="publication_pdflink">
                    <a href="$research_publication_pdflink_url">pdf</a>
                </span>

EOT;
        }

        if (strlen($other_links_html) > 0) {
            $other_links_html .= ")";
        }

        $publication_date_html = '';

        $research_publication_month = $research['research_publication_month'];
        if ($research_publication_month != 'NA') {
            $publication_date_html .= format_for_html($research_publication_month)." ";
        }

        $research_publication_year = format_for_html($research['research_publication_year']);
        $publication_date_html .= format_for_html($research_publication_year);

        $research_publication_mainlink_url = format_for_html($research['research_publication_mainlink_url']);
        $research_title = format_for_html($research['research_title']);
        $research_authors = format_for_html($research['research_authors']);
        $research_publication_name = format_for_html($research['research_publication_name']);
        print <<<EOT
            <div class="research">
                <span class="title"><a href="$research_publication_mainlink_url">$research_title</a></span>,

                <span class="authors">$research_authors</span>,

                <span class="publication_name">$research_publication_name</span>,

                $publication_lookup_html

                <span class="publication_date">$publication_date_html</span>.

                $other_links_html
            </div>

EOT;
    }

    function research_search_for($search_for) {
        return db_search_for(
            'research',
            $search_for,
            array('research_authors', 'research_title', 'research_publication_name')
        );
    }

    function research_sort_categories_cmp($r1, $r2) {
        if (($r1 == 'Student Beliefs and Learning') && ($r2 == 'Studies of PhET Effectiveness')) {
            return 1;
        }
        else if (($r2 == 'Student Beliefs and Learning') && ($r1 == 'Studies of PhET Effectiveness')) {
            return -1;
        }

        return strcasecmp($r1, $r2);
    }

    function research_get_all_categories() {
        $categories = array();

        foreach(research_get_all() as $research) {
            $cat = $research['research_category'];

            $categories[strtolower("$cat")] = "$cat";
        }

        usort($categories, 'research_sort_categories_cmp');

        return $categories;
    }

    function research_get_categories_as_javascript_array() {
        $default_categories = array(
            strtolower("About PhET")                    => "About PhET",
            strtolower("Studies of PhET Effectiveness") => "Studies of PhET Effectiveness",
            strtolower("Student Beliefs and Learning")  => "Student Beliefs and Learning"
        );

        $array = '[';

        $categories = array_merge($default_categories, research_get_all_categories());
        $categories = array_unique($categories);

        $is_first = true;

        foreach($categories as $category) {
            if ($is_first) {
                $is_first = false;
            }
            else {
                $array .= ', ';
            }

            $array .= '"'.$category.'"';
        }

        $array .= ']';

        return $array;
    }

    function research_get_publication_date($research) {
        $year  = $research['research_publication_year'];
        $month = $research['research_publication_month'];

        if ($month == 'NA') {
            $time_string = "January $year";
        }
        else {
            $time_string = "$month $year";
        }

        return strtotime($time_string);
    }

    function research_compare($research1, $research2) {
        return research_get_publication_date($research2) - research_get_publication_date($research1);
    }

    function research_get_all_by_category($selected_cat) {
        $researches = array();

        foreach(research_get_all() as $research) {
            $current_cat = $research['research_category'];

            if (strtolower($current_cat) == strtolower($selected_cat)) {
                $researches[] = $research;
            }
        }

        usort($researches, 'research_compare');

        return $researches;
    }

    function research_print_edit($research_id = null) {
        if ($research_id == null) {
            $research = db_get_blank_row('research');
        }
        else {
            $research = research_get_by_id($research_id);
        }

        // Removing unsafe function 'get_code_to_create_variables_from_array',
        // just doing the equivalent by hand
        $research = format_for_html($research);
        //eval(get_code_to_create_variables_from_array($research));
        $research_id = $research["research_id"];
        $research_title = $research["research_title"];
        $research_authors = $research["research_authors"];
        $research_category = $research["research_category"];
        $research_publication_name = $research["research_publication_name"];
        $research_publication_month = $research["research_publication_month"];
        $research_publication_year = $research["research_publication_year"];
        $research_publication_lookup = $research["research_publication_lookup"];
        $research_publication_pdflink_url = $research["research_publication_pdflink_url"];
        $research_publication_htmllink_url = $research["research_publication_htmllink_url"];
        $research_publication_mainlink_url = $research["research_publication_mainlink_url"];

        $cat_array = research_get_categories_as_javascript_array();

        $javascript_autocomplete = '';

        if (web_detect_browser() != 'internet-explorer') {
            $javascript_autocomplete = <<<EOT
                    <script type="text/javascript">
                        /*<![CDATA[*/

                        $("#research_category_uid").autocompleteArray($cat_array);

                        /*]]>*/
                    </script>

EOT;
        }

        print <<<EOT
            <input type="hidden" name="research_id" value="$research_id" />

            <div class="field">
                <span class="label_content">
                    <input type="text" name="research_title" value="$research_title" size="25"/>
                </span>

                <span class="label">
                    title:
                </span>
            </div>

            <div class="field">
                <span class="label_content">
                    <input type="text" name="research_category" value="$research_category" id="research_category_uid" size="25"/>

                    $javascript_autocomplete
                </span>

                <span class="label">
                    category:
                </span>
            </div>

            <div class="field">
                <span class="label_content">
                    <input type="text" name="research_authors" value="$research_authors" size="25"/>
                </span>

                <span class="label">
                    authors:
                </span>
            </div>

            <div class="field">
                <span class="label_content">
                    <input type="text" name="research_publication_name" value="$research_publication_name" size="25"/>
                </span>

                <span class="label">
                    publication:
                </span>
            </div>

            <div class="field">
                <span class="label_content">
                    <input type="text" name="research_publication_year" value="$research_publication_year" size="4"/>
                </span>

                <span class="label">
                    publication year:
                </span>
            </div>

            <div class="field">
                <span class="label_content">

EOT;

        $months = array(
            'NA'        => 'NA',
            'January'   => 'January',
            'February'  => 'February',
            'March'     => 'March',
            'April'     => 'April',
            'May'       => 'May',
            'June'      => 'June',
            'July'      => 'July',
            'August'    => 'August',
            'September' => 'September',
            'October'   => 'October',
            'November'  => 'November',
            'December'  => 'December'
        );

        if ($research_publication_month == '') {
            $research_publication_month = 'NA';
        }

        print_single_selection('research_publication_month', $months, $research_publication_month);

        print <<<EOT
                </span>

                <span class="label">
                    publication month:
                </span>
            </div>

            <div class="field">
                <span class="label_content">
                    <input type="text" name="research_publication_lookup" value="$research_publication_lookup" size="15"/>
                </span>

                <span class="label">
                    volume/pages:
                </span>
            </div>

            <div class="field">
                <span class="label_content">
                    <input type="text" name="research_publication_mainlink_url" value="$research_publication_mainlink_url" size="25"/>
                </span>

                <span class="label">
                    main link:
                </span>
            </div>

            <div class="field">
                <span class="label_content">
                    <input type="text" name="research_publication_pdflink_url" value="$research_publication_pdflink_url" size="25"/>
                </span>

                <span class="label">
                    pdf link (optional):
                </span>
            </div>

            <div class="field">
                <span class="label_content">
                    <input type="text" name="research_publication_htmllink_url" value="$research_publication_htmllink_url" size="25"/>
                </span>

                <span class="label">
                    html link (optional):
                </span>
            </div>

EOT;
    }


?>