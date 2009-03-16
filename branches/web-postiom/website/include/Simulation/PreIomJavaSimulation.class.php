<?php

class PreIomJavaSimulation extends JavaSimulation {
    private function getPreIomProjectFilename() {
        return self::sim_root."{$this->project_name}/{$this->sim_name}.jar";
    }

    public function getSize() {
        try {
            return parent::getSize();
        }
        catch (PhetSimException $e) {
            // Ignore the exception and keep on trucking
        }

        $filename = $this->getPreIomProjectFilename();
        if (!file_exists($filename)) {
            throw new PhetSimException("Cannot get size, project file '{$file}' does not exist");
        }

        return (int) (filesize($filename) / 1024);
    }

    private function getPreIomLaunchFilename($locale = Locale::DEFAULT_LOCALE) {
        if (Locale::inst()->isDefault($locale)) {
            return self::sim_root."{$this->project_name}/{$this->sim_name}.jnlp";
        }
        else {
            return self::sim_root."{$this->project_name}/{$this->sim_name}_{$locale}.jnlp";
        }
    }

    public function getLaunchUrl($locale = Locale::DEFAULT_LOCALE) {
        $filename = parent::getLaunchFilename($locale);
        if (file_exists($filename)) {
            return $filename;
        }

        $filename = $this->getPreIomLaunchFilename($locale);
        if (file_exists($filename)) {
            return $filename;
        }

        return '';
    }

    private function getPreIomDownloadFilename($locale = Locale::DEFAULT_LOCALE) {
        if (Locale::inst()->isDefault($locale)) {
            return self::sim_root."{$this->project_name}/{$this->sim_name}.jar";
        }
        else {
            return self::sim_root."{$this->project_name}/{$this->sim_name}_{$locale}.jar";
        }
    }

    public function getDownloadFilename($locale = Locale::DEFAULT_LOCALE) {
        // Try straight post IOM
        $filename = parent::getDownloadFilename($locale);
        if (file_exists($filename)) {
            return $filename;
        }

        // Try pre iom
        $filename = $this->getPreIomDownloadFilename($locale);
        if (file_exists($filename)) {
            return $filename;
        }

        return '';
    }

    public function getDownloadUrl($locale = Locale::DEFAULT_LOCALE) {
        $filename = $this->getDownloadFilename($locale);
        if (!file_exists($filename)) {
            return '';
        }

        return SITE_ROOT."admin/get-run-offline.php?sim_id={$this->getId()}&locale={$locale}";
;
    }
}

?>