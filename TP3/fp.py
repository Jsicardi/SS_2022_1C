##nro_de_evento,tiempo,fpleft,fp right

static_file = open('static_rand.txt')
static_lines = static_file.readlines()
n = float(static_lines[0])
half_x = float(static_lines[1])
static_file.close()

results_file = open('result.txt')
results_lines = results_file.readlines()
fp_csv = open("fp.csv", "w")
fp_csv.write("event_number,time,fp_left,fp_right\n")

i = 0
time = 0
count_right = 0
for line in results_lines:
    args = line.split()
    if len(args) == 1:
        print(args[0])
        fp_csv.write(str(i) + "," + str(time) + "," + str(1-count_right/n)+ "," + str(count_right/n) +"\n")
        count_right = 0
        i += 1
        time = float(args[0])
    elif len(args) == 5:
        x = float(args[1])
        if x >= half_x:
            count_right += 1
results_file.close()
fp_csv.close()
