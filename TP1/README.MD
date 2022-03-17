Using ParticleGenerator:

Parameters involved (in order of use):
- N: total particles to generate
- L: size of the grid to use
- rmax: maximum radius of particles
- sameR: if true, all particles have the same radius (rmax), if false radius is generated as (random number between 0 and 1) * rmax

Result of execution is the generation of files static_rand.txt (static properties) and dynamic_rand.txt (dynamic properties), with the format established in class

Using CIM:

Parameters involved(in order of use):
- path to static properties file
- path to dynamic properties file
- path to generate neighbours file
- rc: critical radius
- M (optional): number of cells per row/column to use in CIM

Result of execution is the generation of file neighbours.txt (neighbours for each particle), with the format established in class

Using ovito's file generator:

```bash
$ python3 ovito.py [particle_index] [path to static.txt] [path to dynamic.txt] [path to neighbours.txt]
```

A file data_ovito.txt will be generated. Using ovito desktop, load file and map the columns in the file to the desired properties