from lib2to3.pgen2 import token
from radiation_energy import Particle, generateMatrix, n, d, q, k, m
import math
import sys


def generate_animation_matrix():
    particles = generateMatrix()
    matrix_file = open("matrix.txt", "w")
    string = ("\n\n")
    i = 0
    for p in particles:
        if p.q > 0:
            p.q = 1
        else:
            p.q = -1

        string +=("{0}\t{1}\t{2}\t{3}\n".format(i,p.x, p.y, p.q))
        i += 1
    matrix_file.write(str(i))
    matrix_file.write(string)
    matrix_file.close()

def generate_animation_particle():
    particle_file= open("particle.txt", "w")

    results = open(sys.argv[1], "r")
    lines_r = results.readlines()

    for line in lines_r[:-1]:
        tokens = line.replace("\n", "").split("\t")
        if len(tokens) == 1:
            particle_file.write(str(1)+"\n\n")
        else:
            particle_file.write("{0}\t{1}\n".format(float(tokens[0]), float(tokens[1])))


    particle_file.close()
    results.close()



    

if __name__ == "__main__":
    generate_animation_matrix()
    generate_animation_particle()


