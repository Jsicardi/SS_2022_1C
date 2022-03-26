Using ParticleGenerator:

Parameters involved (in order of use):
- N: total particles to generate
- L: size of the grid to use
- v: speed module of the particles

Result of execution is the generation of files static_rand.txt (static properties) and dynamic_rand.txt (dynamic properties), with the format established in class

Using Off-Lattice:

Parameters involved(in order of use):
- path to static properties file
- path to dynamic properties file
- path to generate result file
- r: interaction radius
- n: noise amplitude
- steps: total steps to calculate particle positions

Result of execution is the generation of file result.txt (positions and angle of particles in a given step)