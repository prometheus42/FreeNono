
#----------------------------------------------------------------------------
# Name:         recognono.py
# Purpose:      Image to Nonogram converter
#
# Author:       Christian Wichmann
#
# Created:      2015-05-16
# Licence:      GNU GPL
#----------------------------------------------------------------------------

import collections
import os, glob

from PIL import Image
#from PIL import ImageFile


app_title = 'recognono'
app_version = '0.2'
app_author = 'Christian Wichmann'
header = '<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><FreeNono><Nonograms>\n'
footer = '</Nonogram>\n</Nonograms>\n</FreeNono>'
copyright_notice = 'Copyright Angela und Otto Janko. Online: http://www.janko.at/Raetsel/Nonogramme/index.htm'
DEBUG = False
REALLY_WRITE_FILES = True


# create a tupel to hold all meta data of a nonogram
Nonogram = collections.namedtuple('Nonogram', 'grid_width grid_height margin_top margin_left nonogram_height nonogram_width start_x start_y')
# set PIL to load truncated files and show no error message
#ImageFile.LOAD_TRUNCATED_IMAGES = True
# setup a dictionary for statistical data
difficulties = {}


def convert_nonograms(root_dir):
    # read rootDir
    file_list = glob.glob(root_dir + '*.gif')

    # process every image file in directory
    for image_file in file_list:
        image_file_name = os.path.split(image_file)[1]
        if DEBUG: print('\nReading image: {}'.format(image_file_name))
        #
        # read image file
        try:
            im = Image.open(image_file)
        except IOError:
            print('ERROR: Cannot open image: {}'.format(image_file_name))
            return
        #
        # checking for grid in file to determine number of rows and columns
        #
        try:
            nonogram_meta_data = analyse_nonogram(im)
            nonogram_name = image_file_name.rsplit('.', 1)[0].replace('.d', '')
            nonogram = convert_nonogram(im, nonogram_name, nonogram_meta_data)
            if (nonogram_meta_data.nonogram_height > 50
                or nonogram_meta_data.nonogram_width > 50):
                print('ERROR: Nonogram {} has width or height that is'
                      'is too big: ({}, {})'.format(nonogram_meta_data.nonogram_width,
                                                    nonogram_meta_data.nonogram_height))
            if REALLY_WRITE_FILES:
                write_nonogram(nonogram_name, course_by_difficulty(nonogram_meta_data), nonogram)
        except IOError as e:
            print('ERROR: Error during reading of file {}: {}'.format(image_file_name, e))
            continue
        im.close()


def analyse_nonogram(im):
    image_width, image_height=im.size
    #
    # find grid width
    #
    grid_width = 0
    last_pixel_color = 0
    for i in range(50):
        pixel_data = im.getpixel((i, 7))
        if pixel_data > 0:
            grid_width += 1
        if last_pixel_color > 0 and pixel_data != last_pixel_color:
            break
        last_pixel_color = pixel_data
    if DEBUG: print('Grid width is: {}'.format(grid_width))
    #
    # find grid height
    #
    grid_height = 0
    last_pixel_color = 0
    for i in range(50):
        pixel_data = im.getpixel((i, 7))
        if pixel_data > 0:
            grid_height += 1
        if last_pixel_color > 0 and pixel_data != last_pixel_color:
            break
        last_pixel_color = pixel_data
    if DEBUG: print('Grid height is: {}'.format(grid_height))
    if grid_height != grid_width:
        print('ERROR: Grid width and grid height are different!')
    #
    # find coordinates for nonogram field (minus the margins with numbers)
    #
    start_x = 7
    start_y = 7
    margin_color = pixel_data = im.getpixel((start_x, start_y))
    margin_left = 0
    for i in range(start_y, image_height, grid_height):
        count = 0
        for j in range(start_x, image_width, grid_width):
            pixel_data = im.getpixel((j, i))
            if pixel_data == margin_color:
                count += 1
        # do not count rows with margins wider than half of the image
        if count > image_width / grid_width / 2:
            continue
        if count > margin_left:
            margin_left = count
    nonogram_width = image_width // (grid_width) - margin_left
    if DEBUG: print('Margin left is: {}'.format(margin_left))
    if DEBUG: print('Nonogram width is: {}'.format(nonogram_width))
    #
    margin_top = 0
    for i in range(start_x, image_width, grid_width):
        count = 0
        for j in range(start_y, image_height, grid_height):
            pixel_data = im.getpixel((i, j))
            if pixel_data == margin_color:
                count += 1
        # do not count columns with margins wider than half of the image
        if count > image_height / grid_height / 2:
            continue
        if count > margin_top:
            margin_top = count
    nonogram_height = image_height // grid_height - margin_top
    if DEBUG: print('Margin top is: {}'.format(margin_top))
    if DEBUG: print('Nonogram height is: {}'.format(nonogram_height ))
    new_nonogram = Nonogram(grid_width, grid_height,
                            margin_top, margin_left,
                            nonogram_height, nonogram_width,
                            start_x, start_y)
    return new_nonogram


def course_by_difficulty(nonogram_meta_data):
    biggest_size = max(nonogram_meta_data.nonogram_height, nonogram_meta_data.nonogram_width)
    # save statistics
    if biggest_size in difficulties.keys():
        difficulties[biggest_size] += 1
    else:
        difficulties[biggest_size] = 1
    # return sub course name depending on size 
    if biggest_size < 20:
        return 'small'
    elif biggest_size < 30:
        return 'medium'
    elif biggest_size < 50:
        return 'large'
    else:
        return ''


def convert_nonogram(im, nonogram_name, nonogram_meta_data):
    image_width, image_height=im.size
    #
    # set header for XML file
    #
    nonogram  = header + '<Nonogram desc="' + copyright_notice + '" difficulty="0" id="" name="'
    nonogram += nonogram_name + '" height="' + str(nonogram_meta_data.nonogram_height) 
    nonogram += '" width="' + str(nonogram_meta_data.nonogram_width) + '">\n'
    #
    # extract nonogram data from image file
    #
    color_threshold = find_color_threshold(im, nonogram_meta_data)
    for i in range(nonogram_meta_data.start_y +
                   nonogram_meta_data.margin_top *
                   nonogram_meta_data.grid_height,
                   image_height, nonogram_meta_data.grid_height):
        nonogram += '<line> '
        for j in range(nonogram_meta_data.start_x + nonogram_meta_data.margin_left * nonogram_meta_data.grid_width, image_width, nonogram_meta_data.grid_width):
            pixel_data = im.getpixel((j, i))
            if pixel_data < color_threshold:
                if DEBUG: print('XX', end='')
                nonogram += 'x '
            else:
                if DEBUG: print('  ', end='')
                nonogram += '_ '
        if DEBUG: print('')
        nonogram += '</line>\n'
    nonogram += footer
    return nonogram


def find_color_threshold(im, nonogram_meta_data):
    list_of_colors = []
    image_width, image_height=im.size
    for i in range(nonogram_meta_data.start_y +
                   nonogram_meta_data.margin_top *
                   nonogram_meta_data.grid_height,
                   image_height, nonogram_meta_data.grid_height):
        for j in range(nonogram_meta_data.start_x + nonogram_meta_data.margin_left * nonogram_meta_data.grid_width, image_width, nonogram_meta_data.grid_width):
            list_of_colors.append(im.getpixel((j, i)))
    return (max(list_of_colors) - min(list_of_colors)) // 2


def write_nonogram(nonogram_name, difficulty_dir, nonogram):
    # create necessary directories and write nonogram to file
    path = os.path.join('output', difficulty_dir)
    if not os.path.exists(path):
        os.makedirs(path)
    filename = os.path.join('output', difficulty_dir, nonogram_name + '.nonogram')
    with open(filename, 'w') as levelFile:
        levelFile.write(nonogram)


if __name__ == '__main__':
    # print info message
    print('This is {} version {} by {}'.format(app_title, app_version, app_author))
    print('(c) 2015 by {}'.format(app_author))

    # start converting nonogram files        
    #convert_nonograms('./')
    convert_nonograms('./input/')
    
    # output statistics
    for sizes in sorted(difficulties.keys()):
        print('{}: {}'.format(sizes, difficulties[sizes]))
    
    # banner again
    print('Have a nice day!')

