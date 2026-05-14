.code
LOAD x
loop: SUB #1
BRPOS loop
SYSCALL 0
.endcode

.data
x 10
.enddata
