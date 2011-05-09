
import codecs
import re
import os
import os.path as path

def jnlp_grep(jnlp_filename, grep_text):
    re_grep = re.compile(grep_text, re.UNICODE | re.IGNORECASE)

    for line in codecs.open(jnlp_filename, 'rb', encoding='utf-16'):
        match = re_grep.search(line)
        if match is not None:
            print '%s: %s' % (jnlp_filename, line)

def set_jnlp_codebase(jnlp_filename, new_codebase):
    re_codebase = re.compile('codebase=".+?"', re.UNICODE)

    data = codecs.open(jnlp_filename, 'rb', encoding='utf-16').read()

    out_data = re_codebase.sub('codebase="%s"' % (new_codebase), data)

    outf = codecs.open(jnlp_filename, 'wb', encoding='utf-16-be')
    outf.write(u'\ufeff')
    outf.write(out_data)
    outf.close()

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
