;; Fibonacci program by Mike Ciul, CIT 593 HW7

.CODE
.ADDR x0000
.FALIGN

init
	LEA R1, sequence	; R1=pointer to the next array element
	CONST R2, #18 		; R2=loop counter
	LDR R3, R1, #0		; R3=sequence[i-2]
	LDR R4, R1, #1		; R4=sequence[i-i]
	ADD R1, R1, #2          ; advance array pointer past the first two elements
while_loop
	CMPI R2, #0		
	BRnz done               ; while loop counter is positive
	ADD R0, R3, R4		; R0=next value to store
	STR R0, R1, #0		; save it into the array
	ADD R3, R4, #0
	ADD R4, R0, #0		; move saved values up
	ADD R2, R2, #-1		; decrement loop counter
	ADD R1, R1, #1		; increment array pointer
	JMP while_loop
done
	NOP
.DATA
.ADDR x2000
.FALIGN
sequence
.FILL #0
.FILL #1
.BLKW #18
