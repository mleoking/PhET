
import os
import os.path as path

def recursive_get_files_by_extension(walk_root, file_extensions, exclude_dirs = ['.svn']):
    found_files = []
    for root, dirs, files in os.walk(walk_root):
        for d in exclude_dirs:
            continue
            try:
                dirs.remove(d)
            except ValueError:
                # If the dir does not exist, ignore
                pass
        matching_files = [path.join(root, file) for file in files
                          if path.splitext(file)[1] in file_extensions]
        found_files.extend(matching_files)
    return found_files
