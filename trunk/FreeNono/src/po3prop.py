#! /usr/bin/env python3

"""
Converter tool to get Java Properties Files from po files. Translations from
Launchpad are stored and exported as po files. These have to be converted to
be used in Java programs.

Usage:
 - Run this script like this:
     ./po3prop de.po
 - Run cmdline tool native2ascii to eliminate all unicode characters:
     native2ascii de.properties de_ascii.properties
 - Copy properties file into project folder.

"""

import polib
import os
import sys


def convert_file(filename):
    po = polib.pofile(filename)
    properties_file_filename, _ = os.path.splitext(filename)
    properties_file_filename += '.properties'
    with open(properties_file_filename, 'w') as prop_file:
        # encoding='iso-8859-1'
        for entry in po:
            # entry.msgid = original english message
            prop_file.write('{}={}{}'.format(entry.occurrences[0][0], 
                                             entry.msgstr, os.linesep))


if __name__ == '__main__':
    if len(sys.argv) == 2:
        filename = sys.argv[1]
        if os.path.isfile(filename):
            convert_file(filename)
        else:
            print('Given argument is not a file!')
    else:
        print('No enough or too many arguments!')
