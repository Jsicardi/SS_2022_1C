import math,numpy as np
path_format = "result_v"
path_result = "absorbed_trajectory.csv"

files = 9
velocities = [5000,16250,27500,38750,50000]

def __main__():
    
    with open(path_result, "w") as f:
        f.write("V0,Trajectory\n")
        for i in range(len(velocities)):
            trajectories = []    
            for j in range(1,files+1):
                file = open("{0}{1}_{2}.txt".format(path_format,i+1,j))
                lines = file.readlines()
                distance = 0
                first_current = True
                current_x = 0
                current_y = 0
                cut_type = int(lines[-1].replace("\n",""))
                if(cut_type != 5):
                    continue
                for line in lines:
                    tokens  = line.replace("\n", "").split('\t')
                    if(len(tokens) < 4):
                        continue
                    if(first_current):
                        current_x = float(tokens[0])
                        current_y = float(tokens[1])
                        first_current = False
                    else:
                        distance+= math.dist([current_x,current_y], [float(tokens[0]),float(tokens[1])])
                        current_x = float(tokens[0])
                        current_y = float(tokens[1])
                trajectories.append(distance)
            for trajectory in trajectories:
                f.write("{0},{1}\n".format(velocities[i], trajectory))

                
                
                
if __name__ == "__main__":
    __main__()