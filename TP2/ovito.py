import sys
from unittest import result

static = open(sys.argv[1])
results = open(sys.argv[2])

lines_s = static.readlines()
lines_r = results.readlines()
 

with open('data_ovito.txt', 'w') as f:
    total_particles = int(lines_s[0].replace(" ",""))
    for i in range(0, len(lines_r)):
        if(i % (total_particles + 2) == 0):
            f.write(str(total_particles) + "\n")
        else:
           f.write(lines_r[i])