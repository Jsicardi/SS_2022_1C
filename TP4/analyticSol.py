import numpy as np

A = 1.0
M = 70.0
K = 10000
TF = 5.0
GAMMA = 100.0

EPSILON = 0.0001

def __main__():
    savingT = 0.1
    t = 0.0
    r = []
    while t < TF + EPSILON:
        r.append(calculateR(t))
        t += savingT
    for i in range(len(r)):
        print(str(i) + ": " + str(r[i]))

def calculateR(t):
    return A * (np.exp(-(GAMMA/(2*M)) * t)) * (np.cos(np.power((K/M) - (GAMMA*GAMMA/(4*(M*M))) , 0.5) * t))


if __name__ == "__main__":
    __main__()