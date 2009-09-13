from __future__ import division
import numpy

def species_coords(sample_coords, abundance_matrix, dims=3):
    """ computes the species coordinates for overlay on the sample coordinates

    inputs:
    * sample_coords: numpy 2d, sample (rows) by x1, x2, x3... at least up to
    dims
    * abundance_matrix: abundance of species at each sample, samples (rows) by
    species.  colunms must be in same order as rows of sample_coords
    * dims: number of dimensions to compute

    output:
    * 2d array, species (rows) by coords (x1, x2 ... xdims)

    see legendre and gallagher "ecologically meaningful..." though their
    description could be clearer.
    """
    num_samples, num_species = abundance_matrix.shape
    if sample_coords.shape[0] != num_samples:
        raise ValueError("sample coords does not match abundance matrix")
    if sample_coords.shape[1] < dims:
        raise ValueError("sample coords has too few dimensions, try fewer dims")
    from cogent.maths.stats.test import pearson # numpy should work

    sp_coords = numpy.zeros((num_species, dims))
    coords_stdev = sample_coords.std(axis=0)
    species_abund_stdev = abundance_matrix.std(axis=0)
    for i,sp in enumerate(abundance_matrix.transpose()):
        for j in range(dims):
            sp_coords[i,j] = pearson(sp, sample_coords[j]) * coords_stdev[j]
    return sp_coords
