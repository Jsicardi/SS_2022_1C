import math,numpy as np
path_format = "result_v"
path_result = "proportions.csv"

files = 201
velocities = [5000,16250,27500,38750,50000]
condition_names = ["Izquierda", "Derecha", "Arriba", "Abajo", "Absorbida"]


def __main__():
    
    with open(path_result, "w") as f:
        f.write("V0,Condition,Proportion\n")
        for i in range(len(velocities)):
            cut_conditions = [0,0,0,0,0]
            for j in range(1,files+1):
                file = open("{0}{1}_{2}.txt".format(path_format,i+1,j))
                lines = file.readlines()
                cut_type = int(lines[-1].replace("\n",""))
                cut_conditions[cut_type-1]+=1
            for k in range(len(condition_names)):
                f.write("{0},{1},{2}\n".format(velocities[i],condition_names[k],cut_conditions[k]/files))
                
if __name__ == "__main__":
    __main__()