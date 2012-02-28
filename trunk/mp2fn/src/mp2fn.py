# -*- coding: utf-8 -*-
#----------------------------------------------------------------------------
# Name:         mp2fn.py
# Purpose:      Mario's Picross to FreeNono Converter
#
# Author:       Christian Wichmann
#
# Created:      2010-12-19
# Licence:      GNU GPL
#----------------------------------------------------------------------------

import os, glob
import Image

appTitle = 'mp2fn'
appVersion = '0.2'
appAuthor = 'Christian Wichmann'
rootDir = './'
screenPath = "../ScreenShots/"
levelPath = "../LevelData/"
header = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><FreeNono><Nonograms>\n"
footer = "</Nonogram>\n</Nonograms>\n</FreeNono>"
width = 15
height = 15
DEBUG = False


def convertNonograms():    
    # read rootDir
    fileList = glob.glob(rootDir + screenPath + '*.png')
    
    # process every png file in rootDir
    for everyFile in fileList:
        fileName = os.path.split(everyFile)[1]
        imageName = fileName.rsplit('.', 1)[0]
        #
        # read image file
        try:
            im = Image.open(everyFile)
        except IOError:
            print "cannot open image", fileName
            return
        #
        # checking image size
        if (im.size != (160, 144)):
            print "wrong image size", fileName
            return
        #
        # processing image
        nonogram = ""
        #
        # image structure:
        # (0.0)
        #     * (61,54)  ...   * (146,54)
        #     ...        ...   ....
        #     * (61,138) ...   * (146,138)
        #
        nonogram += header + "<Nonogram desc=\"\" difficulty=\"0\" id=\"\" name=\""
        nonogram += imageName + "\" height=\"" + str(height) 
        nonogram += "\" width=\"" + str(width) + "\">\n"
        for y in range(54, 140, 6):
            nonogram += "<line>"
            for x in range(61, 150, 6):
                # getpixel: returns either (176, 176, 176) for empty box 
                #           or (80, 80, 80) for full box
                pixelData = im.getpixel((x, y))
                if pixelData == (176, 176, 176):
                    nonogram += " "
                elif pixelData == (80, 80, 80):
                    nonogram += "x"
                else:
                    print "wrong image format", fileName
                    return
            nonogram += "</line>\n"
        nonogram += footer
        #
        # writing nonogram to file
        levelFile = open(rootDir + levelPath + imageName + ".nonogram", "w")
        levelFile.write(nonogram)
        levelFile.close()
        print 'File ' + everyFile + ' converted...'


if __name__ == '__main__':
    #arg = sys.argv
    #filename = arg[1]

    # print info message
    print 'This is', appTitle, 'ver.', appVersion, 'by', appAuthor
    print '(c) 2011 by', appAuthor

    # start converting nonogram files        
    convertNonograms()    
    
    # banner again
    print 'Have a nice day!'

