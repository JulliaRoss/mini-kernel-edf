.code
LOAD x
loop: SUB #1
SYSCALL 1
BRPOS loop
SYSCALL 0
.endcode

.data
x 5
.enddata