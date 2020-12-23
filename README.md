# Seam Carving

This code is my implementation of Seam Carving project on [JetBrains Academy](https://hyperskill.org)

The complete explanation of the image algorithm is here https://en.m.wikipedia.org/wiki/Seam_carving

I used the dynamic programming way with recursion to do it (easy to understand) but I tried to implement an iterative version to see the performance difference (no real difference in fact)
Seam Carving a 1000x1000 takes about 7s on an old Macbook Pro (2014).

I also tried to use parallelStream to gain some perf but nothing crazy.

If you have any idea to largely improve perf, send me a PR :)