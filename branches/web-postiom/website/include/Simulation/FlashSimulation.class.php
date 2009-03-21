<?php


class FlashSimulation extends BaseSimulation {
    public function runsInNewBrowserWindow() {
        return TRUE;
    }

    public function getType() {
        return 'Flash';
    }

    public function getSize() {
        $filename = $this->getProjectFilename();

        if (!file_exists($filename)) {
            return 0;
            // Don't throw until file size checkers catch it
            //throw new RuntimeException("Cannot get size, SWF file '{$filename}' does not exist");
        }

        return (int) (filesize($filename) / 1024);

    }

    public function getLaunchFilename($locale = Locale::DEFAULT_LOCALE) {
        // TODO: When country codes have been fully implemented in the
        // simulation filenames, change this entire function to:
        //return self::sim_root."{$this->project_name}/{$this->sim_name}_{$locale}.html";
        $base_file = self::sim_root."{$this->project_name}/{$this->sim_name}";
        foreach ($this->getRemappedLocales($locale) as $loc) {
            $locale_file = $base_file.'_'.$loc.'.html';
            if (file_exists($locale_file)) {
                return $locale_file;
            }
        }

        return false;
    }

    public function getLaunchUrl($locale = Locale::DEFAULT_LOCALE) {
        return $this->getLaunchFilename($locale);
    }

    public function getProjectFilename() {
        return self::sim_root."{$this->project_name}/{$this->project_name}.swf";
    }

    protected function getTranslationGlob() {
        $base = self::sim_root."{$this->project_name}/{$this->sim_name}";
        $base_glob = $base."*.*ml";
        $base_regex = $base."(-strings)?_([A-Za-z]{2})?(_([A-Za-z]{2}))?.(html|xml)";
        return array($base_glob, $base_regex);
    }
}

?>