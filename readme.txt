Bliffoscope Detector
====================

Author: Corver Roos
Java version: 1.8

Summary:
This is a detector library for Bliffoscope image files. Given a set of Bliffoscope images it detects the partial presence of any of the supplied target patterns.

Input:
Bliffoscope images and target patterns are "binary images" formated as text files. Images and patterns are formated as '+' characters against a whitespace ' ' background. 

Note that whitespaces in patterns are masked out, only the '+' characters contribute to the pattern. 
Note that only rectangular images are supported.

Output:
Patterns match a subsection of an image if a certain percentage of '+' characters in the pattern appear in the same relative positioning in image. The match results contain the image, the target pattern, the relative offset of the pattern from the top,left corner of the image (x=0,y=0), and the percentage of overlap.  

Note that the percentage threshold above which matches are triggered is configurable. Thresholds below 40% are not suggested since many false positive can be expected. The configured threshold also has an effect on performance.

Match Technique:
Since the images and patterns are binary images, i.e. pattern matching with dictionary size of 2, a brute force approach was selected but with bit level operations for improved performance.

The images and patterns are converted to binary representations, more specifically 2D byte arrays. Each 2D pattern is bit shifted to produce eight 2D byte aligned patterns. Each of the eight shifted-patterns are compared against each possbile posistion in the image. Those positions that produce a match higher than the threshold result in a match. Hamming distance is used to calculate matches. 

Note that a lookup table was generated to improve population count calculation.
Note that by default only the '+' characters of a pattern contribute to the overlap, whitespaces are masked out.

Usage:

This project can be used as internal library by calling the Detector class returning the match results for each supplied image.

It can also be called from the command line in which case the a Terminal GUI will show the matches superimposed on the images. Please note the terminalGUI has only been tested with windows.

$> java -jar BliffoscopeDetector.jar <imageDir> <patternDir> <threshold>