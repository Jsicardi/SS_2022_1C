import sys
import math

def generate_circle(r, number_of_particles):
    with open('circle.txt', 'w') as circle:
        id = 0
        string = ""
        string+=( "\n"+ "\n")
        

        while id < number_of_particles:
            particle_x = r * math.cos(2*math.pi*id/number_of_particles);
            particle_y = r * math.sin(2*math.pi*id/number_of_particles);
            #separated by tabs
            string+=(str(id) + "\t" +str(particle_x) + "\t" + str(particle_y) + "\n")
            id += 1
        circle.write(str(id))
        circle.write(string)
        circle.close()

generate_circle(11, 1000)


static = open(sys.argv[1])
results = open(sys.argv[2])

lines_s = static.readlines()
lines_r = results.readlines()

with open('data_ovito.txt', 'w') as f:
    total_particles = int(lines_s[0].replace(" ",""))
    for line in lines_r:
        tokens = line.replace("\n","").split('\t')
        if(len(tokens) == 1):
            f.write(total_particles + "\n\n")
        else:
            f.write("{0}\t{1}\t{2}\n".format(tokens[0],tokens[1],tokens[4]))
    f.close()

static.close()
results.close()