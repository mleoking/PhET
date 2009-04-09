<?php

    // Utils to update settings
  /*
   // Don't need these
    // In each web accessable script SITE_ROOT must be defined FIRST
    if (!defined("SITE_ROOT")) define("SITE_ROOT", "../");

    // See global.php for an explaination of the next line
    require_once(dirname(dirname(dirname(__FILE__)))."/include/global.php");

    require_once("include/db-utils.php");
  */
    class UpdateUtils {
        private static $instance;

        const SETTINGS_TABLE = 'update_settings';

        private $type_map =  array(
            'sim_ask_later_duration' => 'int',
            'install_ask_later_duration' => 'int',
            'install_recommend_update_age' => 'int',
            'install_recommend_update_date' => 'date'
            );

        private function __construct() {
        }

        public static function inst() {
            if (!isset(self::$instance)) {
                $class = __CLASS__;
                self::$instance = new $class;
            }
            return self::$instance;
        }

        private function getDBSettings() {
            return db_get_row_by_condition(self::SETTINGS_TABLE, array());
        }

        private function updateDBSettings($settings) {
            return db_update_table(self::SETTINGS_TABLE, $settings, 'id', 1);
        }

        private function validateData($settings) {
            foreach ($settings as $field => $value) {
                if (!isset($this->type_map[$field])) {
                    // Don't allow partially specified data
                    return false;
                }

                switch ($this->type_map[$field]) {
                    case 'int':
                        $result = Validate::inst()->validInt($value);
                        break;
                    case 'date':
                        $result = Validate::inst()->validDate($value);
                        break;
                    default:
                        throw new RuntimeError('Invalid type found in UpdateUtils');
                        break;
                }

                if (!$result) {
                    return $result;
                }
            }

            return true;
        }

        private function parseData($settings) {
            $parsed_settings = array();
            foreach ($settings as $field => $value) {
                if ($this->type_map[$field] == 'date') {
                    $parsed_settings[$field] = Validate::inst()->parseDate($value);
                }
                else {
                    $parsed_settings[$field] = $value;
                }
            }

            return $parsed_settings;
        }

        public function getSettings() {
            $settings = $this->getDBSettings();
            unset($settings['id']);
            return $settings;
        }

        public function setSettings($input_settings) {
            $settings = $this->filterRequiredFields($input_settings);
            if (empty($settings)) {
                return false;
            }

            unset($settings['id']);

            if (!$this->validateData($settings)) {
                return false;
            }

            $parsed_settings = $this->parseData($settings);
            return $this->updateDBSettings($parsed_settings);
        }

        private function filterRequiredFields($input) {
            $result = array();
            foreach(array_keys($this->type_map) as $field) {
                if (isset($input[$field])) {
                    $result[$field] = $input[$field];
                }
            }

            return $result;
        }
    }

?>