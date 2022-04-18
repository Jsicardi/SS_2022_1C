static_path = "static_rand.txt"
result_paths = ["result.txt"]

static = open(static_path)

lines_s = static.readlines()
total_particles = int(lines_s[0].replace(" ",""))
width = float(lines_s[1].replace(" ",""))*2
heigth = float(lines_s[2].replace(" ",""))
mass = float(lines_s[3].replace("   ","").replace("\n","").split(" ")[2])
perimeter = width*2 + heigth*2
total_impulse = 0


with open('pressions.csv', 'w') as f:
    f.write("Pressure,Temperature\n")
    for result_path in result_paths:
        result_file = open(result_path)
        lines_r = result_file.readlines()
        for i in range(0, len(lines_r)):
            tokens = lines_r[i].replace("\n","").split('\t')
            if(len(tokens) == 1): #skip lines with timesteps
                continue
            if (tokens[0] == "WC" and tokens[2] != "-5"): #timestep with wall colission detected and the wall is not the middle wall
                particle_id = int(tokens[1])
                particle_props = lines_r[i+particle_id+1].replace("\n","").split('\t')
                if(tokens[2] == "-1" or tokens[2] == "-4"): #collision with left and right walls
                    total_impulse += 2*mass*(abs(float(particle_props[3])))
                else:
                    total_impulse += 2*mass*(abs(float(particle_props[4])))
            
            i+=total_particles+2
        eq_time = float(lines_r[len(lines_r) - total_particles - 3].replace("\n",""))
        force = total_impulse / eq_time
        pressure = force / perimeter
        total_energy = 0
        for j in range(len(lines_r) - total_particles, len(lines_r)): #temperature as average cinetic energy on eq
            particle_props = lines_r[j].replace("\n","").split('\t')
            total_energy += (1/2)*mass*(float(particle_props[3])**2 + float(particle_props[4])**2)
            
        f.write("{0},{1}\n".format(pressure,total_energy/total_particles))