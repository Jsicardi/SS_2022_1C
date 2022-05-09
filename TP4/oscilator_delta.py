from calculate_analytic import calculateR
path_format = "result_beeman"
TF = 5.0
EPSILON = 0.0000000001
deltas = [0.1,0.05,0.01,0.005,0.001,0.0005,0.0001,0.00005,0.00001]

with open("beeman_errors.csv","w") as f:
    f.write("Delta,Error\n")
    j = 1
    for delta in deltas:
        t = 0.0
        times = []
        r = []
        errors = []
        positions = []
        while t <= TF+EPSILON:
            r.append(calculateR(t))
            t += delta
            times.append(t)
        file = open("{0}_{1}.txt".format(path_format,j))
        lines = file.readlines()
        for line in lines:
            tokens = line.replace('\n','').split("\t")
            if (len(tokens) != 2):
                continue
            positions.append(float(tokens[0]))
        sum_error = 0
        print(len(r))
        print(len(positions))
        for i in range(len(r)):
            sum_error+=((positions[i]-r[i])**2)
        f.write("{0},{1}\n".format(delta, sum_error / len(r)))
        j+=1
