import statistics

import numpy


path_format = "result_"
path_answer_fz = "fz.csv"
path_answer_velocity = "velocity.csv"

runs = 10
nhs = [2, 5, 10, 20, 40, 80, 160, 320]
fzs = []
nps = []
velocities = []

fz = open(path_answer_fz, "w")
fz.write("nh,fz,stdev\n")
fv = open(path_answer_velocity, "w")
fv.write("nh,velocity,stdev\n")
for nh in nhs:
    np = 0
    nh = 0
    #itero para guardar la velocidad de contagio y el Fz del final de la simulacion
    for i in range(runs):
        iteration = -1
        nps = 0
        file = open("{0}_{1}_{2}.txt".format(path_format, nh, i))
        lines = file.readlines()
        for line in lines:
            tokens = line.replace("\n", "").split('\t')
            if(len(tokens) == 1):
                #treat nps
                if(iteration > 0):
                    nps[iteration] = nps
                nps = 0
                nh = 0
                iteration +=1
            else:
                if(tokens[4] == "2"):
                    nps += 1
                elif(tokens[4] == "0"):
                    nh += 1
        #add last iteration
        nps[iteration] = nps
        fzs[iteration] = nps/nh
        #treat velocities
        velocities[i] = statistics.mean(numpy.diff(nps))
    fv.write("{0},{1},{2}\n".format(nh, statistics.mean(velocities), statistics.stdev(velocities)))
    fz.write("{0},{1},{2}\n".format(nh,statistics.mean(fzs), statistics.stdev(fzs)))
            