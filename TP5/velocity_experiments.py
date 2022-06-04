import statistics

import numpy


path_format = "result_"
path_answer_fz = "v_fz.csv"
path_answer_velocity = "v_velocity.csv"

runs = 10
vdzs = [1, 1.5, 2, 2.5, 3, 3.5, 4, 4.5, 5]

fz = open(path_answer_fz, "w")
fz.write("vdz,fz,stdev\n")
fv = open(path_answer_velocity, "w")
fv.write("vdz,velocity,stdev\n")

nh = 200
for vdz in vdzs:
    velocities = []
    fzs = []
    np = 0
    #itero para guardar la velocidad de contagio y el Fz del final de la simulacion
    for i in range(runs):
        iteration = -1
        np = 0
        file = open("{0}{1}_{2}.txt".format(path_format, vz, i+1))
        lines = file.readlines()
        nps = []
        for line in lines:
            tokens = line.replace("\n", "").split('\t')
            if(len(tokens) == 1):
                #treat nps
                if(iteration >= 0):
                    nps.append(np)
                np = 0
                iteration += 1
            else:
                if(tokens[5] == "2"):
                    np += 1
        #add last iteration
        nps.append(np)
        fzs.append(np/nh)
        #treat velocities
        velocities.append(statistics.mean(numpy.diff(nps)))
        file.close()
    fv.write("{0},{1},{2}\n".format(vdz, statistics.mean(velocities), statistics.stdev(velocities)))
    fz.write("{0},{1},{2}\n".format(vdz,statistics.mean(fzs), statistics.stdev(fzs)))

fv.close()
fz.close()