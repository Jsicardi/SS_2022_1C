import math
import numpy as np

path_format = "result_d"
deltas = [1e-14,5e-15,1e-15,5e-16,1e-16,5e-17,1e-17]
saving_t = 1e-14
path_result = "rad_energy.csv"
n = 16*16
d = math.pow(10, -8)
q = math.pow(10, -19)
k = math.pow(10, 10)
m = math.pow(10, -27)

class Particle:
    def __init__(self,x,y,vx,vy,q):
        self.type = type
        self.x = x
        self.y = y
        self.vx = vx
        self.vy = vy
        self.q = q

def getPotEnergy(p1:Particle, p2:Particle):
    dist = math.dist([p1.x, p1.y], [p2.x, p2.y])
    return k * p1.q * p2.q / dist

def getAllPotEnergy(p:Particle, particles):
    sum = 0
    pos = 0
    neg = 0
    for p2 in particles:
        curr = getPotEnergy(p, p2)
        sum += curr
        if curr > 0:
            pos += 1
        else:
            neg += 1
    return sum

def getKinEnergy(p:Particle):
    return 0.5 * m * ((p.vx**2) + (p.vy**2))
    
def generateMatrix():
    positive = True
    particles = []
    for i in range(n):  
        y = int(i / int(math.sqrt(n)))
        x = int((i % int(math.sqrt(n))) + 1)
        if y%2 == 0:
            positive =  ((i+1)%2) * 2 - 1
        else:
            positive = (i%2) * 2 - 1
        particles.append(Particle(x * d, y * d, 0, 0, positive * q))

    return particles

def __main__():
    particles = generateMatrix()
    
    with open(path_result, "w") as f:
        f.write("Delta_t,Avg_rel_dif,Std\n")
        for i in range(len(deltas)):
            relative_difs = []
            for j in range(1,6):
                file = open("{0}{1}_{2}.txt".format(path_format,j,i+1))
                lines = file.readlines()
                tokens_initial = lines[1].replace("\n", "").split("\t")
                tokens_final = lines[-2].replace("\n", "").split("\t")

                #save initial and final values of particles
                initial_particle:Particle = Particle(float(tokens_initial[0]), float(tokens_initial[1]),float(tokens_initial[2]),float(tokens_initial[3]),q)
                final_particle:Particle = Particle(float(tokens_final[0]), float(tokens_final[1]),float(tokens_final[2]),float(tokens_final[3]),q)
                
                #calculate energies
                initial_k = getKinEnergy(initial_particle)
                final_k = getKinEnergy(final_particle)
                initial_u = getAllPotEnergy(initial_particle, particles)
                final_u = getAllPotEnergy(final_particle, particles)
                #print("IK: {0} IU: {1} FK: {2} FU: {3}".format(initial_k,initial_u,final_k,final_u))
                energy_init = (initial_k + initial_u)
                energy_dif = abs(energy_init - (final_k + final_u))
                relative_difs.append(energy_dif / energy_init)

            avg_relative_dif = 0
            for relative_dif in relative_difs:
                avg_relative_dif += relative_dif
            avg_relative_dif /= 5
            stdev = np.std(relative_difs)
            f.write("{0},{1},{2}\n".format(deltas[i],avg_relative_dif,stdev))
                                
if __name__ == "__main__":
   __main__()
