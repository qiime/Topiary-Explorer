""" example of pcoa based biplot.

here I'm considering an abundance matrix of species by sample, 
analagous to samples by OTUs.
Outline:
1. get (sample x sample) dist matrix from abundance matrix
2. do pcoa on that distance matrix to get sample coordinates
3. use those sample coords to generate species coordinates

sample_coords and sp_coords format:
row = sample or species, column 0 = x coord, col 1 = y coord ...
use sample_coords[:,0:2] to get an (n by 2) matrix

"""
from __future__ import division
from species_coords import species_coords
from numpy import array
#import matplotlib.pyplot as plt
import cogent.cluster.metric_scaling as pcoa
from cogent.maths.distance_transform import binary_dist_chisq,\
binary_dist_chord,\
binary_dist_euclidean,\
binary_dist_hamming,\
binary_dist_jaccard,\
binary_dist_lennon,\
binary_dist_ochiai,\
binary_dist_pearson,\
binary_dist_sorensen_dice,\
dist_bray_curtis,\
dist_canberra,\
dist_chisq,\
dist_chord,\
dist_euclidean,\
dist_gower,\
dist_hellinger,\
dist_kulczynski,\
dist_manhattan,\
dist_morisita_horn,\
dist_pearson,\
dist_soergel,\
dist_spearman_approx,\
dist_specprof

import sys
print("1")
o = open('data.txt', 'r');
l = o.read()
o.close()
ptmtx = array(eval(l),'float')
ptmtx = ptmtx.transpose()
print("1")
#get distance metric
dist_metric = sys.argv[1]
dist_functions = {
		"Bray-Curtis": dist_bray_curtis,
		"Canberra": dist_canberra,
		"Chi-squared": dist_chisq,
		"Chord": dist_chord,
		"Euclidean": dist_euclidean,
		"Gower": dist_gower,
		"Hellinger": dist_hellinger,
		"Kulczynski": dist_kulczynski,
		"Manhattan": dist_manhattan,
		"Morisita-Horn": dist_morisita_horn,
		"Pearson": dist_pearson,
		"Soergel": dist_soergel,
		"Spearman-Approx": dist_spearman_approx,
		"Species-Profile": dist_specprof,
		"Binary-Chi-Squared": binary_dist_chisq,
		"Binary-Chord": binary_dist_chord,
		"Binary-Euclidean": binary_dist_euclidean,
		"Binary-Hamming": binary_dist_hamming,
		"Binary-Jaccard": binary_dist_jaccard,
		"Binary-Lennon": binary_dist_lennon,
		"Binary-Ochiai": binary_dist_ochiai,
		"Binary-Pearson": binary_dist_pearson,
		"Binary-Sorensen-Dice": binary_dist_sorensen_dice
}
print("1")
if dist_metric == "Custom...":
    #get custom distance matrix
    filename = sys.argv[2]
    o = open(filename, 'r');
    distance_matrix = o.read().split('\n')
    #remove comment lines
    for i in range(len(distance_matrix)-1,-1,-1):
        if distance_matrix[i][0] == '#':
            del distance_matrix[i]
    #split each line by tabs
    distance_matrix = [i.split('\t') for i in distance_matrix]
    #convert each element to a number
    distance_matrix = array([[float(i) for i in j] for j in distance_matrix])
    print distance_matrix
else:
    #create distance matrix
    distance_matrix = dist_functions[dist_metric](ptmtx)
print("1")
aa = pcoa.principal_coordinates_analysis(distance_matrix)
sample_coords = aa[0].transpose()
sp_coords = species_coords(aa[0], ptmtx, dims=len(sample_coords[0])) * 3
print("1")

evals = aa[1]/sum(aa[1])

#scale axes by eigenvalues
sp_coords = sp_coords*array([list(evals)]*len(sp_coords));
sample_coords = sample_coords*array([list(evals)]*len(sample_coords))

o = open('sample_coords.txt', 'w')
for i in sample_coords:
    for j in i:
        o.write(str(j) + '\t')
    o.write('\n')
o.close()
print("1")
o = open('sp_coords.txt', 'w')
for i in sp_coords:
    for j in i:
        o.write(str(j) + '\t')
    o.write('\n')
o.close()
#output eigenvalues
o = open('evals.txt', 'w')
for i in aa[1]:
    o.write(str(i/sum(aa[1])) + '\t')
o.close()
print("1")