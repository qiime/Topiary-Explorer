.. TopiaryExplorer documentation master file, created by
   sphinx-quickstart on Thu Jan 13 15:43:25 2011.
   You can adapt this file completely to your liking, but it should at least
   contain the root `toctree` directive.

#########################
TopiaryExplorer UserGuide
#########################

.. toctree::
   :maxdepth: 2
	
   install
   file_formats
   quickstart
   interface_guide
   database_connectivity
   bigtree_tutorial
   insertion_tutorial


Introduction
============
This user guide contains install instructions, a quickstart tutorial illustrating how to use TopiaryExplorer with an example data set, and a detailed interface guide. We recommend that after `installing <./install.html>`_ you get started by working through the `quick start tutorial <./quickstart.html>`_ and moving on to the `interface guide <./interface_guide.html>`_ as needed.


About TopiaryExplorer
=====================
TopiaryExplorer is developed in the `Knight Lab <http://chem.colorado.edu/knightgroup/>`_ at the University of Colorado at Boulder with the initial goals of supporting visualization of large phylogenetic trees and associated tip metadata and environmental data.

Current technologies for high-throughput sequencing provide an investigator with massive amounts of data. In microbial ecology, tools such as `QIIME <http://www.qiime.org>`_ have kept pace with the increasing quantity of sequences allowing computational processing and statistical analysis of those data, but tools for visualization of phylogenetic trees have lagged behind. 

TopiaryExplorer supports visualization of very large trees (it has been tested on trees with > 100,000 tips) while providing convenient interfaces for decorating the tree with taxonomic information, automatically coloring branches based on data about environments where they are observed, selecting subtrees for independent analysis, interactive expanding and collapsing of clades, and many other features. TopiaryExplorer supports database connectivity, allowing investigators to obtain, combine, edit, search and save metadata from multiple studies. Multiple tree layouts such as rectangular or polar views are available and results can be exported as publication quality PDF images. 

In short, TopiaryExplorer acts as an analysis pipeline, keeping all phylogenetic tree related information in one place so that data analysis is more interactive and easier for the researcher.

Features
--------

 * Tree visualization and manipulation
 * Integrated metadata viewing and editing capabilities 
 * Dynamic coloring based on tip metadata or environmental data
 * Publication-quality image exporting
 * Database connectivity
 * Project management using .tep files

Citing TopiaryExplorer
======================

TopiaryExplorer is currently under review. In the meantime please cite TopiaryExplorer with the following reference:

TopiaryExplorer: An application for connecting large phylogenetic trees to environmental metadata; Meg Pirrung, Ryan Kennedy, J. Gregory Caporaso and Rob Knight; Under review (2011).

Contact gregcaporaso@gmail.com with questions about citing TopiaryExplorer.

