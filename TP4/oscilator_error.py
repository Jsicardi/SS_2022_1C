import sys
from calculate_analytic import calculateR

result_path = sys.argv[1]
saving_t = float(sys.argv[2])
EPSILON = 0.00001
TF = 5.0

def __main__():
    with open("osc_gear_error.csv", "w") as f:
        f.write("Time,Analytic,Calculated\n")
        t = 0.0
        times = []
        r = []
        errors = []
        positions = []
        while t < TF+EPSILON:
            r.append(calculateR(t))
            t += saving_t
            times.append(t)
        file = open(result_path)
        lines = file.readlines()
        for line in lines:
            tokens = line.replace('\n','').split("\t")
            if (len(tokens) != 2):
                continue
            positions.append(float(tokens[0]))
        t=0 
        for i in range(len(r)):
            f.write("{0:.4f},{1},{2}\n".format(t,positions[i],r[i]))
            t+=saving_t

if __name__ == "__main__":
    __main__()