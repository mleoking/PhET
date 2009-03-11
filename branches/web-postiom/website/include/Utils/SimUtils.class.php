<?php

require_once('include/db-utils.php');

class SimUtils {
    private static $instance;

    const SIM_RATING_NONE = 0;
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

    public function getRatingImageUrl($rating) {
        static $ratingMap = array(
            self::SIM_RATING_NONE   => '',
            self::SIM_RATING_ALPHA        => 'alpha25x25.png',
            self::SIM_RATING_CHECK        => 'checkmark25x25.png'
            );

        return $ratingMap[$rating];
    }

    public function getRatingImageAnchorTag($rating) {
        static $tagMap = NULL;
        if (is_null($tagMap)) {
            // This is_null trick is needed because string concatenation is not
            // supported in the initialization of a static member variable
             $tagMap = array(
                 self::SIM_RATING_NONE         => '',
                 self::SIM_RATING_CHECK        => '<a href="'.SITE_ROOT.'about/legend.php"><img src="'.SITE_ROOT.'images/sims/ratings/'.$this->getRatingImageUrl($rating).'"         alt="Checkmark Rating Image"     width="37" title="Classroom Tested: This simulation has been used and tested in the classroom, and on multiple computer platforms. The simulation has been refined based on that experience and on student interviews." /></a>',
            self::SIM_RATING_ALPHA        => '<a href="'.SITE_ROOT.'about/legend.php"><img src="'.SITE_ROOT.'images/sims/ratings/'.$this->getRatingImageUrl($rating).'"         alt="Alpha Rating Image"         width="37" title="Under Construction: This simulation is an early preview version, and may have functional or usability bugs." /></a>'
            );
        }

        return $tagMap[$rating];
    }

    public function getContributionFromPhetImageAnchorTag() {
        $img_attributes = array(
            'alt' => 'Designed by PhET Icon',
            'title' => 'PhET Designed: This contribution was designed by PhET'
            );
        $image_tag = WebUtils::inst()->buildImageTag(
            SITE_ROOT.'images/phet-logo-icon.jpg',
            $img_attributes);
        $anchor_tag = WebUtils::inst()->buildAnchorTag(
            SITE_ROOT.'about/legend.php',
            $image_tag
            );
        return $anchor_tag;
        define("FROM_PHET_IMAGE_HTML", '<a href="'.SITE_ROOT.'about/legend.php"><img src="'.SITE_ROOT.'images/phet-logo-icon.jpg" alt="Designed by PhET Icon" title="PhET Designed: This contribution was designed by PhET." /></a>');

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
}

?>