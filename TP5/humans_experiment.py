import statistics

import numpy


path_format = "result_"
path_answer_fz = "nh_fz.csv"
path_answer_velocity = "nh_velocity.csv"

runs = 10
nhs = [2, 5, 10, 20, 40, 80, 140, 200, 260, 320]

fz = open(path_answer_fz, "w")
fz.write("nh,fz,stdev\n")
fv = open(path_answer_velocity, "w")
fv.write("nh,velocity,stdev\n")
for nh in nhs:
    velocities = []
    fzs = []
    np = 0
    #itero para guardar la velocidad de contagio y el Fz del final de la simulacion
    for i in range(runs):
        iteration = -1
        np = 0
        file = open("{0}{1}_{2}.txt".format(path_format, nh, i+1))
        lines = file.readlines()
        nps = []
        transf_zombies = 0
        for line in lines:
            tokens = line.replace("\n", "").split('\t')
            if(len(tokens) == 1):
                #treat nps
                if(iteration >= 0):
                    np += (transf_zombies / 2)
                    nps.append(np)
                    transf_zombies = 0
                np = 0
                iteration += 1
            else:
                if(tokens[5] == "2"):
                    np += 1
                if(tokens[5] == "1"):
                    transf_zombies+=1
        #add last iteration
        np += (transf_zombies / 2)
        nps.append(np)
        fzs.append(np/(nh+1))
        #treat velocities
        velocities.append(statistics.mean(numpy.diff(nps)))
        file.close()
    fv.write("{0},{1},{2}\n".format(nh, statistics.mean(velocities), statistics.stdev(velocities)))
    fz.write("{0},{1},{2}\n".format(nh,statistics.mean(fzs), statistics.stdev(fzs)))

fv.close()
fz.close()