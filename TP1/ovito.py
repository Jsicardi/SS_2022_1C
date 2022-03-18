import sys

particle_id = int(sys.argv[1])
static = open(sys.argv[2])
dynamic = open(sys.argv[3])
neighbours = open(sys.argv[4])

lines_d = dynamic.readlines()
lines_s = static.readlines()
lines_n = neighbours.readlines()
 
index = 1

with open('data_ovito.txt', 'w') as f:
    f.write(lines_s[0].replace(" ", ""))
    f.write('Particle data for ovito. Index, x, y, radius\n')
    j = 2
    for i in range(1, len(lines_d)):
        str_n = str(index) + lines_d[i].replace("   ", "    ").replace("\n", "") + lines_s[j].replace("   ", "   ").replace("\n", "   ")
        tokens = lines_n[i-1].split("\t")
        if(i == len(lines_d) - 1):
            str_n = str_n + "   "
        if(int(tokens[0]) == particle_id):
            str_n = str_n + "S\n"
        elif(tokens.count(str(particle_id)) != 0):
            str_n = str_n + "N\n"
        else:
            str_n = str_n + "NS\n"
        if(i == len(lines_d)-1):
            str_n = str_n.replace("\n","")
        f.write(str_n)
        index += 1
        j += 1