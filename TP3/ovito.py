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
fp_file = open(sys.argv[3])
epsilon = float(sys.argv[4])

lines_s = static.readlines()
lines_r = results.readlines()
lines_fp = fp_file.readlines()

generate_wall(240, 90, 10)

with open('data_ovito.txt', 'w') as f:
    total_particles = int(lines_s[0].replace(" ",""))
    j=0
    for j in range(1,len(lines_fp)):       
        tokens = lines_fp[j].split(',')
        fp = float(tokens[2])
        eq_time = float(tokens[1])
        if((fp >= 0.5 - epsilon) and (fp <= 0.5 + epsilon)):
            break
    k=0
    while k < len(lines_r):
        tokens = lines_r[k].replace("\n","").split('\t')
        if(len(tokens) == 1):
            if(float(tokens[0]) == eq_time):
                break
        k += total_particles+3
    for i in range(0, k+total_particles+3):
        tokens = lines_r[i].replace(" ","").replace("\n","").split('\t')
        if(len(tokens) == 1 and tokens[0] != ""):
            f.write(str(total_particles) + "\n\n")
        elif(len(tokens) == 5):
           f.write(lines_r[i])