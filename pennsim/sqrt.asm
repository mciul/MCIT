;; Square Root program by Mike Ciul, CIT 593 HW7

.CODE
.ADDR x0000
.FALIGN

init
	CONST R0, #9	; R0=A, the number whose square root we seek
	CONST R1, #0	; R1=B, the solution candidate

exec
	CMPI R0, #0
	BRn back_up	; if A is negative, skip everything
while_loop
	MUL R2, R1, R1  ; save B*B in R2
	CMP R2, R0
	BRp back_up	; if B*B > A, we went too far, so we're done
	ADD R1, R1, #1	; increment B
	JMP while_loop
back_up
	ADD R1, R1, #-1	; B=B-1 - if A was negative, now B is -1
done
	NOP

