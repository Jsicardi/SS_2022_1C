import sys

def generate_wall(w,h, width_of_hole):
    with open('wall.txt', 'w') as wall:
        #how many particles im about to write
        #particle_ammount = w*2 + h*2 + (h-width_of_hole)
        #particle_ammount = 708
        id = 0
        string = ""
        string+=( "\n"+ "\n")
        

        for x in range(0, w):
            y1 = 0; #top wall 
            y2 = h; #bottom wall
            #separated by tabs
            string+=(str(id) + "\t" +str(x/1000) + "\t" + str(y1/1000) + "\n")
            id += 1
            string+=(str(id) + "\t" +str(x/1000) + "\t" + str(y2/1000) + "\n")
            id += 1

        for y in range(0, h):
            x1 = 0; #left wall
            x2 = w; #right wall
            x3 = w/2 #center wall
            #separated by tabs
            string+=(str(id) + "\t" +str(x1/1000) + "\t" + str(y/1000) + "\n")
            id+=1
            string+=(str(id) + "\t" +str(x2/1000) + "\t" + str(y/1000) + "\n")
            id+=1
            #only write wall if it is not in the hole, hole is centered vertically

            if y < ((h/2) - width_of_hole/2) or y > ((h/2) + width_of_hole/2):
                string+=(str(id) + "\t" + str(x3/1000) + "\t" + str(y/1000) + "\n")
                id+=1

        wall.write(str(id))
        wall.write(string)
        wall.close()


static = open(sys.argv[1])
results = open(sys.argv[2])
delta_t = float(sys.argv[3])
t_max = 0

lines_s = static.readlines()
lines_r = results.readlines()

generate_wall(240, 90, 20)

with open('data_ovito.txt', 'w') as f:
    total_particles = int(lines_s[0].replace(" ",""))
    i = 0
    while i < len(lines_r):
        tokens = lines_r[i].replace(" ","").replace("\n","").split('\t')
        if(len(tokens) == 1 and tokens[0] != ""):
            if(float(tokens[0]) < t_max):
                i+=total_particles+3
                continue
            print(tokens[0])
            f.write(str(total_particles) + "\n\n")
            t_max+=delta_t
        elif(len(tokens) == 5):
           f.write(lines_r[i])
        i+=1