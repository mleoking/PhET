<?php

class JavaSimulation extends BaseSimulation {
    public function runsInNewBrowserWindow() {
        // Java sims run as a Web Start application, not in a browser window
        return FALSE;
    }

    public function getType() {
        return 'Java';
    }

    public function getSize() {
        $filename = $this->getProjectFilename();

        if (!file_exists($filename)) {
            return 0;
            // Don't throw until file size checkers catch it
            //throw new PhetSimException("Cannot get size, project file '{$filename}' does not exist");
        }

        return (int) (filesize($filename) / 1024);
    }

    public function getLaunchFilename($locale = Locale::DEFAULT_LOCALE) {
        // TODO: When country codes have been fully implemented in the
        // simulation filenames, change this entire function to:
        //return self::sim_root."{$this->project_name}/{$this->sim_name}_{$locale}.jnlp";

        $base_file = self::sim_root."{$this->project_name}/{$this->sim_name}";
        foreach ($this->getRemappedLocales($locale) as $loc) {
            $locale_file = $this->makeLocaleFilename($base_file, $loc, '.jnlp');
            if (file_exists($locale_file)) {
                return $locale_file;
            }
        }

        return false;
    }

    public function getLaunchUrl($locale = Locale::DEFAULT_LOCALE) {
        if (!Locale::inst()->isValid($locale)) {
            return '';
        }

        return $this->getLaunchFilename($locale);
    }

    public function getProjectFilename() {
        return self::sim_root."{$this->project_name}/{$this->project_name}_all.jar";
    }

    protected function getTranslationGlob() {
        $base = self::sim_root."{$this->project_name}/{$this->sim_name}";
        $base_glob = $base."*.jnlp";
        $base_regex = $base."(_)([A-Za-z]{2})?(_([A-Za-z]{2}))?.jnlp";
        return array($base_glob, $base_regex);
    }
}

?>