<?php

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
}

?>