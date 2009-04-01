<?php

require_once('include/db-utils.php');

class SimUtils {
    private static $instance;

    const sim_root = SIMS_ROOT;
    const SIM_RATING_NONE = '0';
    const SIM_RATING_ALPHA = '1';
    const SIM_RATING_CHECK = '2';

    private function __construct() {
    }

    private function __clone() {
    }

    public static function inst() {
        if (!isset(self::$instance)) {
            $class = __CLASS__;
            self::$instance = new $class;
        }

        return self::$instance;
    }

    public function getGuidanceDescription() {
        return 'Guidance Recommended: This simulation is very effective when used in conjunction with a lecture, homework or other teacher designed activity.';
    }

    public function getGuidanceImageUrl() {
        return SITE_ROOT.'images/sims/ratings/crutch25x25.png';
    }

    public function getGuidanceImageTag() {
        $attributes = array(
            'alt' => 'Not standalone',
            'width' => '37',
            'title' => $this->getGuidanceDescription()
            );
        return WebUtils::inst()->buildImageTag($this->getGuidanceImageUrl(), $attributes);
    }

    public function getGuidanceImageAnchorTag() {
        $root = SITE_ROOT;
        return WebUtils::inst()->buildAnchorTag(SITE_ROOT.'about/legend.php', $this->getGuidanceImageTag());
    }

    public function getRatingImageUrl($rating) {
        $ratingMap = array(
            self::SIM_RATING_NONE   => '',
            self::SIM_RATING_ALPHA  => SITE_ROOT.'images/sims/ratings/alpha25x25.png',
            self::SIM_RATING_CHECK  => SITE_ROOT.'images/sims/ratings/checkmark25x25.png',
            );

        return $ratingMap[$rating];
    }

    public function getRatingImageTag($rating) {
        $tagMap = array(
            self::SIM_RATING_NONE         => array(
                ),
            self::SIM_RATING_CHECK        => array(
                'alt' => 'Checkmark Rating Image',
                'width' => 37,
                'title' => 'Classroom Tested: This simulation has been used and tested in the classroom, and on multiple computer platforms. The simulation has been refined based on that experience and on student interviews.',
                ),
            self::SIM_RATING_ALPHA        => array(
                'alt' => 'Alpha Rating Image',
                'width' => 37,
                'title' => 'Under Construction: This simulation is an early preview version, and may have functional or usability bugs.'
                )
            );
        return WebUtils::inst()->buildImageTag(
            $this->getRatingImageUrl($rating),
            $tagMap[$rating]
            );
    }

    public function getRatingImageAnchorTag($rating) {
        static $tagMap = NULL;
        if (is_null($tagMap)) {
            // This is_null trick is needed because string concatenation is not
            // supported in the initialization of a static member variable
             $tagMap = array(
                 self::SIM_RATING_NONE         => '',
                 self::SIM_RATING_CHECK        => '<a href="'.SITE_ROOT.'about/legend.php"><img src="'.$this->getRatingImageUrl(self::SIM_RATING_CHECK).'"         alt="Checkmark Rating Image"     width="37" title="Classroom Tested: This simulation has been used and tested in the classroom, and on multiple computer platforms. The simulation has been refined based on that experience and on student interviews." /></a>',
                 self::SIM_RATING_ALPHA        => '<a href="'.SITE_ROOT.'about/legend.php"><img src="'.$this->getRatingImageUrl(self::SIM_RATING_ALPHA).'"         alt="Alpha Rating Image"         width="37" title="Under Construction: This simulation is an early preview version, and may have functional or usability bugs." /></a>'
            );
        }

        return $tagMap[$rating];
    }

    public function getContributionFromPhetImageTag() {
        $img_attributes = array(
            'alt' => 'Designed by PhET Icon',
            'title' => 'PhET Designed: This contribution was designed by PhET'
            );
        return WebUtils::inst()->buildImageTag(
            SITE_ROOT.'images/phet-logo-icon.jpg',
            $img_attributes);
    }

    public function getContributionFromPhetImageAnchorTag() {
        $anchor_tag = WebUtils::inst()->buildAnchorTag(
            SITE_ROOT.'about/legend.php',
            $this->getContributionFromPhetImageTag()
            );
        return $anchor_tag;
    }

    public function getAllSimNames($id_prefix = 'sim_id_') {
        $sims = SimFactory::inst()->getAllSims(TRUE);
        $mapped_sims = array();
        foreach ($sims as $sim) {
            $mapped_sims["{$id_prefix}{$sim->getId()}"] = $sim->getName();
        }

        return $mapped_sims;
    }

    private function getAllImagePreviewUrls($animated) {
        // A near-copy of sim_get_image_previews
        // Get the database connection, start it if if this is the first call

        $type = ($animated) ? 'animated' : 'static';

        $query = "SELECT * FROM `category` WHERE `cat_is_visible`='0' ";
        $category_rows = db_get_rows_custom_query($query);

        foreach ($category_rows as $category_row) {
            $cat_id   = $category_row['cat_id'];
            $cat_name = $category_row['cat_name'];

            if (preg_match("/.*{$type}.*preview.*/i", $cat_name) == 1) {
                $images = array();

                $cat_sims = SimFactory::inst()->getSimsByCatId($cat_id);
                foreach ($cat_sims as $simulation) {
                    if ($animated) {
                        $animated_screenshot = $simulation->getAnimatedScreenshotUrl();
                        // This double check is because sometime the screenshots
                        // that are expected to be seen are not present.  See
                        // ticket #1444 for an example.
                        if (file_exists($animated_screenshot)) {
                            $images[] = $animated_screenshot;
                        }
                    }
                    else {
                        $static_screenshot = $simulation->getScreenshotUrl();
                        $images[] = $static_screenshot;
                    }
                }

                return $images;
            }
        }

        return FALSE;
    }

    public function getAllAnimatedPreviewUrls() {
        // A near-copy of sim_get_animated_previews
        return $this->getAllImagePreviewUrls(TRUE);
    }

    public function getAllStaticPreviewUrls() {
        // A near-copy of sim_get_static_previews
        return $this->getAllImagePreviewUrls(FALSE);
    }

    public function getTeachersGuideFilenameAndContents($teachers_guide_id, $decode_contents = false) {
        // FIXME: remove decode parameter
        //
        // TODO: Change the get-teachers-guide.php class to pass the
        // sim id rather than the teacher's guide id, then use the sim
        // class to get the teacher's guide rather than this specialty
        // function
        //
        // Get the row
        $teachers_guide = db_get_row_by_condition("teachers_guide", array("teachers_guide_id" => $teachers_guide_id));

        // Decode the contents
        if ($decode_contents) {
            $encoded_contents = $teachers_guide["teachers_guide_contents"];
            $teachers_guide["teachers_guide_contents"] = base64_decode($encoded_contents);
        }

        return $teachers_guide;
    }

    public function getProjectFilename($project_name) {
        // TODO: make work like mainstream sims?
        // Duplicated code from JavaSimulation and FlashSimulation
        // getProjectFilename()
        $query = "SELECT `sim_type` ".
            "FROM `simulation` ".
            "WHERE `sim_dirname`='{$project_name}'";
        $rows = db_get_rows_custom_query($query);
        assert(count($rows) == 1);
        if ($rows[0]['sim_type'] == '0') {
            return self::sim_root."{$project_name}/{$project_name}_all.jar";
        }
        else if ($rows[0]['sim_type'] == '1') {
            return self::sim_root."{$project_name}/{$project_name}_all.swf";
        }
    }

    public function searchForSims($search_for) {
        return db_search_for(
            'simulation',
            $search_for,
            array('sim_name', 'sim_desc', 'sim_keywords', 'sim_main_topics', 'sim_sample_goals')
        );
    }

    public function generateSortingName($name) {
        $matches = array();

        preg_match('/ *((the|a|an) +)?(.*)/i', $name, $matches);

        return $matches[3];
    }
}

?>