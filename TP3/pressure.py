static_path = "static_rand.txt"
v_file = "v_0.02"
fp_path = "fp_v_0.02"
epsilon = 0.001

static = open(static_path)

lines_s = static.readlines()
total_particles = int(lines_s[0].replace(" ",""))
width = float(lines_s[1].replace(" ",""))*2
heigth = float(lines_s[2].replace(" ",""))
mass = float(lines_s[3].replace("   ","").replace("\n","").split(" ")[2])
perimeter = width*2 + heigth*2
total_impulse = 0


with open('p_0.02.csv', 'w') as f:
    f.write("Pressure,Temperature\n")

    for i in range(1,11):
        eq_time = 0
        fp_file = open("{0}_{1}.csv".format(fp_path,i))
        fp_lines = fp_file.readlines()

        #find eq_time
        j=0
        for j in range(1,len(fp_lines)):
            
            tokens = fp_lines[j].split(',')
            fp = float(tokens[2])
            eq_time = float(tokens[1])
            if((fp >= 0.5 - epsilon) and (fp <= 0.5 + epsilon)):
                break

        result_file = open("{0}_{1}.txt".format(v_file,str(i)))
        lines_r = result_file.readlines()
        k=0
        while k < len(lines_r):
            tokens = lines_r[k].replace("\n","").split('\t')
            if(len(tokens) == 1):
                if(float(tokens[0]) == eq_time):
                    break
            k += total_particles+3

        m = k
        while m < len(lines_r):
            tokens = lines_r[m].replace("\n","").split('\t')
            if(len(tokens) == 1): #skip lines with timesteps
                m+=1
                continue
            if (tokens[0] == "WC" and tokens[2] != "-5"): #timestep with wall colission detected and the wall is not the middle wall
                particle_id = int(tokens[1])
                particle_props = lines_r[m+particle_id+1].replace("\n","").split('\t')
                if(tokens[2] == "-1" or tokens[2] == "-4"): #collision with left and right walls
                    total_impulse += 2*mass*(abs(float(particle_props[3])))
                else:
                    total_impulse += 2*mass*(abs(float(particle_props[4])))
            
            m+=total_particles+2
        last_time = float(lines_r[len(lines_r) - total_particles - 3].replace("\n",""))
        force = total_impulse / (last_time-eq_time)
        pressure = force / perimeter
        total_energy = 0
        h=0
        for h in range(len(lines_r) - total_particles, len(lines_r)): #temperature as average cinetic energy on eq
            particle_props = lines_r[h].replace("\n","").split('\t')
            total_energy += (1/2)*mass*(float(particle_props[3])**2 + float(particle_props[4])**2)
            
        f.write("{0},{1}\n".format(pressure,total_energy/total_particles))