# Build information

Before building the version has to be set in:

    build.xml
    debian/changes/build.xml
	launch4j.xml (for windows installer)
	
For release reset log level in:

    log4j.xml


## Install build dependencies

Install all necessary dependencies to build FreeNono:

    sudo apt install fop
    
Update all Java dependencies with Ivy:

    ant resolve


## Build windows installer

To build a windows installer you only have to call the appropriate ant target:

    ant installer 


## Build flatpak package

### Preparing build process

    mkdir freenonorepo
    flatpak --user remote-add --no-gpg-verify freenono-repo freenonorepo
    flatpak-builder flatpak_build org.freenono.FreeNono.json --force-clean
    flatpak-builder --run flatpak_build org.freenono.FreeNono.json run.sh

### Building package

    flatpak-builder --repo=freenonorepo --force-clean flatpak_build flatpak/org.freenono.FreeNono.json
    flatpak build-bundle freenonorepo freenono.flatpak org.freenono.FreeNono

### Testing package

Install package from repo or from flatpak file: 

    flatpak --user install freenono-repo org.freenono.FreeNono

or

    flatpak --user install freenono.flatpak

Run the program from GUI or via CLI:

    flatpak run org.freenono.FreeNono

Sources:
[1] http://blog.matbooth.co.uk/08-openjdk-in-flathub.html
[2] https://github.com/giuspen/cherrytree/blob/master/flatpak/README.txt


## Build debian packages (local)

For building deb packages there are two ant targets:

    ant deb
    ant debWithLibs

The second target includes all libraries that are not in the Debian package
repositories yet. All necessary JAR files are located in the freenono
directory inside /usr/share/.


## Build debian packages (build server)

Set release information for Debian package in /debian/changelog.

Build tarball with all necessary files for building the packages.

    ant tarball_debian

Copy tarball, uncompress it and rename tarball/directory with version
information, e.g.

    freenono-0.9~svn4242
    freenono_0.9~svn4242.orig.tar.gz

Build Debian source package inside uncompressed directory:

    debuild -S

Check debian packages for errors:

    lintian -i freenono_0.8~svn1026-0ppa1_all.deb

Test Debian source package for a specific distribution:

    pbuilder-dist saucy build freenono_0.8~svn1026-0ppa1.dsc

Upload Debian source package to PPA:
  
    dput freenono-ppa freenono_0.8~svn1026-0ppa1_source.changes


## Debian package naming conventions

SVN build:

    freenono-0.8~svn994-0ppa1

Alpha/beta release:

    freenono-0.8~alpha1-0ppa1~saucy1

Release:

    freenono-0.8-0ppa1~saucy1

Further information:
https://help.launchpad.net/Packaging/PPA/BuildingASourcePackage


## Internationalization and Localization

Create pot file from properties (src/resources/i18n/) file:

    prop2po src/resources/i18n/FreeNono.properties /home/christian/Desktop/freenono.pot

Upload to Launchpad...

Download po files and convert back to properties files:

    ./po3prop de.po

Run cmdline tool native2ascii to eliminate all unicode characters:

    native2ascii de.properties de_ascii.properties

Copy properties file into project folder.
