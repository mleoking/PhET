<?php

require_once('include/installer-utils.php');

abstract class BaseSimulation implements SimulationInterface {
    const sim_root = SIMS_ROOT;
    const thumbnail_cache = 'thumbnails';
    const thumbnail_cache_lifespan = 24;
    const thumbnail_width = 130;
    const thumbnail_height = 97;

    // Sim data
    private $id;
    private $name;
    private $type;
    private $sorting_name;
    private $description;
    protected $project_name;
    protected $sim_name;
    private $guidance_recommended;
    private $rating;
    private $keywords;
    private $teachers_guide_id;
    private $main_topics;
    private $learning_goals;
    private $design_team;
    private $libraries;
    private $thanks_to;
    private $is_real;

    // Data from the database, used during the refactor
    private $original_db_data;
    private $used_db_data;
    private $unused_db_data;

    // Map the database field names to local variable names
    private static $map = array(
        'id' => array('sim_id', 'int'),
        'name' => array('sim_name', 'string'),
        'type' => array('sim_type', 'ignore'),
        'project_name' => array('sim_dirname', 'string'),
        'sim_name' => array('sim_flavorname', 'string'),
        'sorting_name' => array('sim_sorting_name', 'tolower_string'),
        'guidance_recommended' => array('sim_crutch', 'bool'),
        'rating' => array('sim_rating', 'int'),
        'description' => array('sim_desc', 'string'),
        'keywords' => array('sim_keywords', 'delimited_list'),
        'teachers_guide_id' => array('sim_teachers_guide_id', 'int'),
        'main_topics' => array('sim_main_topics', 'delimited_list'),
        'learning_goals' => array('sim_sample_goals', 'delimited_list'),
        'design_team' => array('sim_design_team', 'delimited_list'),
        'libraries' => array('sim_libraries', 'delimited_list'),
        'thanks_to' => array('sim_thanks_to', 'delimited_list'),
        'is_real' => array('sim_is_real', 'int'),
        );

    public function __construct($db_data) {
        $this->used_db_data = array();
        $this->original_db_data = $db_data;
        foreach (self::$map as $property => $db_field) {
            $type = $db_field[1];
            $raw_data = $db_data[$db_field[0]];
            // Convert the data to the specified type
            switch ($type) {
                case 'ignore':
                    $data = $raw_data;
                    break;
                case 'bool':
                    $data = (0 != intval($raw_data));
                    break;
                case 'int':
                    $data = intval($raw_data);
                    break;
                case 'string':
                    $data = $raw_data;
                    break;
                case 'tolower_string':
                    // TODO: data should come out of database already lowercased
                    $data = strtolower($raw_data);
                    break;
                case 'delimited_list':
                    // TODO: fix data in database so the trim is not needed
                    // This will involve fixing existing data, and updating
                    // admin/edit-sim.php to do this trimming
                    $trimmed_data = rtrim($raw_data, '*, ');
                    if (strstr($trimmed_data, '*')) {
                        $data = preg_split('/ *\\* */', $trimmed_data, -1, PREG_SPLIT_NO_EMPTY);
                    }
                    else {
                        $data = preg_split('/ *, */', $trimmed_data, -1, PREG_SPLIT_NO_EMPTY);
                    }
                    break;
                default:
                    throw new RuntimeException("Type '{$type}' is not supported");
            }

            $this->$property = $data;

            // Keep track of what was used
            $this->used_db_data[$db_field[0]] = $raw_data;

            // Unset the data and later check for unused data
            unset($db_data[$db_field[0]]);
        }

        // Save what is left for easy checking for unused data
        $this->unused_db_data = $db_data;
    }

    public function getOriginalDBData() {
        return $this->original_db_data;
    }

    public function getUsedDBData() {
        return $this->used_db_data;
    }

    public function getUnusedDBData() {
        return $this->unused_db_data;
    }

    public function getId() {
        return $this->id;
    }

    public function getName() {
        return $this->name;
    }

    public function getSortingName() {
        return $this->sorting_name;
    }

    public function getSortingFirstChar() {
        if (strlen($this->sorting_name) == 0) {
            // TODO: log error
            // This case is here to get around a rare but thus far
            // non-reproducible error where the database sorting names
            // get cleared.  Note: this error happened with the old
            // sim-utils.php code, and probably will not be seen here.
            // But since the actual problem was never uncovered, one
            // can never be too careful.
            $sort = SimUtils::inst()->generateSortingName($this->getName());
            return $sort[0];
        }
        return strtoupper($this->sorting_name[0]);
    }

    public function getProjectName() {
        return $this->project_name;
    }

    public function getSimName() {
        return $this->sim_name;
    }

    public function getDescription() {
        return $this->description;
    }

    public function getGuidanceRecommended() {
        return $this->guidance_recommended;
    }

    public function getRating() {
        return $this->rating;
    }

    public function getKeywords() {
        return $this->keywords;
    }

    public function hasTeachersGuide() {
        return ($this->teachers_guide_id != 0);
    }

    public function getTeachersGuideUrl() {
        if (!$this->hasTeachersGuide()) {
            return '';
        }

        return SITE_ROOT.'admin/get-teachers-guide.php?teachers_guide_id='.$this->teachers_guide_id;
    }

    public function setTeachersGuide($filename, $contents, $size) {
        $this->removeTeachersGuide();

        $encoded_data = base64_encode($contents);

        $new_id = db_insert_row(
                "teachers_guide",
                array(
                    "teachers_guide_filename" => $filename,
                    "teachers_guide_size" => $size,
                    "teachers_guide_contents" => $encoded_data
                )
            );

        assert($new_id);
        db_update_table(
            "simulation",
            array("sim_teachers_guide_id" => $new_id),
            "sim_id",
            $this->id
            );
    }

    public function removeTeachersGuide() {
        if ($this->teachers_guide_id == 0) {
            return;
        }

        db_delete_row("teachers_guide",
                      array("teachers_guide_id" => $this->teachers_guide_id));
        db_update_table("simulation",
                        array("sim_teachers_guide_id" => 0),
                        "sim_id",
                        $this->id);
    }

    public function getTeachersGuideFilename() {
        if (!$this->hasTeachersGuide()) {
            return '';
        }

        $sql = "SELECT teachers_guide_filename ".
            "FROM teachers_guide".
            "WHERE teachers_guide_id=".$this->teachers_guide_id;
        $rows = db_get_rows_custom_query($sql);

        return $rows[0]['teachers_guide_filename'];
    }

    public function getMainTopics() {
        return $this->main_topics;
    }

    public function getLearningGoals() {
        return $this->learning_goals;
    }

    public function getDesignTeam() {
        return $this->design_team;
    }

    public function getLibraries() {
        return $this->libraries;
    }

    public function getThanksTo() {
        return $this->thanks_to;
    }

    public function getScreenshotFilename() {
        $basename = "{$this->project_name}/{$this->sim_name}-screenshot.png";
        return self::sim_root.$basename;
    }

    public function getScreenshotUrl() {
        return $this->getScreenshotFilename();
    }

    public function getAnimatedScreenshotFilename() {
        return self::sim_root."{$this->project_name}/{$this->sim_name}-animated-screenshot.gif";
    }

    public function getAnimatedScreenshotUrl() {
        return $this->getAnimatedScreenshotFilename();
    }

    public function getThumbnailFilename() {
        return self::sim_root."{$this->project_name}/{$this->sim_name}-thumbnail.jpg";
    }

    public function getThumbnailUrl() {
        return $this->getThumbnailFilename();
    }

    public function getPageUrl() {
        return SITE_ROOT.'simulations/sims.php?sim='.WebUtils::inst()->encodeString($this->getName());
    }

    public function getAbsolutePageUrl() {
        return 'http://'.PHET_DOMAIN_NAME.'/simulations/sims.php?sim='.WebUtils::inst()->encodeString($this->getName());
    }

    public function getDownloadFilename($locale = Locale::DEFAULT_LOCALE) {
        return self::sim_root."{$this->project_name}/{$this->sim_name}_{$locale}.jar";
    }

    public function getDownloadUrl($locale = Locale::DEFAULT_LOCALE) {
        return SITE_ROOT."admin/get-run-offline.php?sim_id={$this->getId()}&locale={$locale}";
    }

    private function getProjectXMLFilename() {
        return self::sim_root."{$this->project_name}/{$this->project_name}.xml";
    }

    protected function getProjectXMLFile() {
        // A slight speed enhancement, cache the XML

        // If the xml has been parsed, just return that
        if (isset($this->project_xml)) {
            return $this->project_xml;
        }

        // Get the XML filename
        $project_xml = $this->getProjectXMLFilename();
        if (!file_exists($project_xml)) {
            // No file containing localized titles
            // TODO: email an error
            return FALSE;
        }

        // Load and parse the XML into a SimpleXML object
        $this->project_xml = simplexml_load_file($project_xml);
        
        return $this->project_xml;
    }

    public function getNameFromXML($locale = Locale::DEFAULT_LOCALE) {
        $xml = $this->getProjectXMLFile();
        if (!$xml) {
            // No file containing localized titles
            // TODO: email an error
            return $this->getName();
        }

        $xpath_query = "/project/simulations/simulation".
            "[@name = '{$this->getSimName()}' and @locale='{$locale}']";
        $path = $xml->xpath($xpath_query);
        if (count($path) == 0) {
            // No entry by that path
            // TODO: email an error
            return $this->getName();
        }
        else if (count($path) > 1) {
            // Too many matches
            // TODO: email an error
            return $this->getName();
        }
        
        return $path[0]->title;
    }

    public function getDescriptionFromXML($locale = Locale::DEFAULT_LOCALE) {
        $xml = $this->getProjectXMLFile();
        if (!$xml) {
            // No file containing localized titles
            // TODO: email an error
            return $this->getDescription();
        }

        $xpath_query = "/project/simulations/simulation".
            "[@name = '{$this->getSimName()}' and @locale='{$locale}']";
        $path = $xml->xpath($xpath_query);
        if (count($path) == 0) {
            // No entry by that path
            // TODO: email an error
            return $this->getDescription();
        }
        else if (count($path) > 1) {
            // Too many matches
            // TODO: email an error
            return $this->getDescription();
        }
        
        if (!isset($path[0]->description)) {
            return $this->getDescription();
        }

        return (string)$path[0]->description;
    }

    // I'd rather declare these explicitly as abstract, but PHP thinks
    // about these differently.  If you receive a error "Cannot
    // instantiate abstract class BaseSimulation in ...file..."  It is
    // probably because not all interface functions have been defined
    abstract protected function getTranslationGlob();

    public function getTranslations() {
        $translations = array();
        $sim_glob = $this->getTranslationGlob();
        $base_glob = $sim_glob[0];
        $base_regex = $sim_glob[1];

        $files = glob($base_glob);
        foreach ($files as $file) {
            $regs = array();
            $result = ereg($base_regex, $file, $regs);
            if ($result !== false) {
                $locale = "{$regs[2]}{$regs[3]}";
            }
            else {
                // Skip the default locale, it is not a translation
                continue;
            }

            $localeUtils = Locale::inst();
            if (!$localeUtils->isValid($locale)) {
                // Locale is not in the table, log error and skip
                // TODO: log an error
                continue;
            }
            else if ($localeUtils->isDefault($locale)) {
                // Skip the default locale, it is not a translation
                continue;
            }

            if (!isset($translations[$locale])) {
                $translations[$locale] = 1;
            }
            else {
                $translations[$locale] += 1;
            }
        }

        $end = array();
        foreach ($translations as $key => $value) {
            $end[] = $key;
        }

        $translations = $end;
        usort($translations, array(Locale::inst(), 'sortCodeByNameCmp'));
        return $translations;
    }

    public function getVersion() {
        $properties_filename = self::sim_root."{$this->project_name}/{$this->project_name}.properties";

        $revision_tags = array(
            'major', 'minor', 'dev', 'revision',
            'timestamp', 'installer_timestamp');
        $regex = 'version\.('.join('|', $revision_tags).') *= *([^ \n\r\t]+)';

        $version = array();
        foreach ($revision_tags as $tag) {
            $version[$tag] = '';
        }

        $installer_timestamp = installer_get_latest_timestamp();
        if ($installer_timestamp && !empty($installer_timestamp)) {
            $version['installer_timestamp'] = installer_get_latest_timestamp();
        }

        if (!file_exists($properties_filename)) {
            return $version;
        }

        $handle = @fopen($properties_filename, "r");

        if ($handle) {
            while (!feof($handle)) {
                $buffer = fgets($handle, 4096);
                $regs = array();
                if (ereg($regex, $buffer, $regs)) {
                    if ($regs[1] && in_array($regs[1], $revision_tags)) {
                        $version[$regs[1]] = trim($regs[2]);
                    }
                }
            }
            fclose($handle);
        }

        return $version;
    }


    public function getChangelogFilename() {
        $basename = "{$this->project_name}/changes.txt";
        return self::sim_root.$basename;
    }

    public function getChangelog() {
        // Returns an array:
        
        $res = array();
        $lines = file($this->getChangelogFilename());
        foreach ($lines as $line_num => $line) {
            // Check for a commentstamp
            $regs = array();
            $match = preg_match('/^#\s*(([0-9]+)\.([0-9]+)\.([0-9]+))\s*\(([0-9]+)\)\s*(.*)/', $line, $regs);
            if ($match) {
                $res[] = array('version' => $regs, 'comments' => array());
                $last_key = count($res) - 1;
                continue;
            }

            $regs = array();
            $match = preg_match('/^\s*([-\w]+:)?\s*(.*)/', $line, $regs);
            if ($match) {
                $for_this_sim = false;
                if ($regs[1] != '') {
                    $sim_name = substr($regs[1], 0, strlen($regs[1]) -1);
                    if ($sim_name != $this->getSimName()) {
                        continue;
                    }
                    else if ($sim_name == $this->getSimName()) {
                        $for_this_sim = true;
                    }
                }

                $comment = ltrim($regs[2], '0123456789/ ');
                $comment_match = preg_match('/^\s*>/', $comment);
                if ($comment_match) { 	
                    $comment = ltrim($comment, ' >');
                    $res[$last_key]['comments'][] = $comment;
                }
            }
            
            //print "<h1>NON CONFORMING LINE {$line}</h1>";
        }

        return $res;
    }

    public function isReal() {
        return $this->is_real;
    }
}

?>