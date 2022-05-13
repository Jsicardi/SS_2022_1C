Using ParticleGenerator:

Parameters involved (in order of use):
- N: total particles to generate
- width: width of the first enclosure of the grid
- heigth: height of the grid
- radius: radius of the particles
- mass:  mass of the particles
- v: speed module of the particles
- sameR: if true, all particle share the same radius. Otherwise radius will be generated random with radius parameter as maximum radius possible

Result of execution is the generation of files static_rand.txt (static properties) and dynamic_rand.txt (dynamic properties), with the format established in class

Using GassDiffusion:

Parameters involved(in order of use):
- path to static properties file
- path to dynamic properties file
- path to positions output file
- opening_length: length of the opening in the middle of the grid
- epsilon: tolerance for the particle fraction (fp ~= 0.5 +- epsilon) 

Result of execution is the generation of file result.txt (positions and angle of particles in a given step)