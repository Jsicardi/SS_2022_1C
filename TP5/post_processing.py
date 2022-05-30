import statistics


path_format = "result_"
path_answer = "fz.csv"
runs = 10
nhs = [2, 5, 10, 20, 40, 80, 160, 320]
fzs = []

with open(path_answer, "w") as f:
    f.write("nh,fz,stdev\n")
    for nh in nhs:
        np = 0
        nh = 0
        analized = False
        for i in range(runs) and not analized:
            file = open("{0}{1}_{2}.txt".format(path_format, nh, i))
            lines = file.readlines()
            for line in lines[::-1]:
                tokens = line.replace("\n", "").split('\t')
                if(len(tokens) == 1):
                    analized = True
                else:
                    if tokens[4] == "0":
                        nh += 1
                    elif tokens[4] == "2":
                        np += 1
            fzs[i] = np/nh
        f.write("{0},{1},{2}\n".format(nh,statistics.mean(fzs), statistics.stdev(fzs)))
            