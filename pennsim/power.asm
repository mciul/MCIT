;; Power Subroutine by Mike Ciul, CIT 593 HW7

.CODE
.ADDR x0000
.FALIGN
INIT
	CONST R0, #1	; A=1 (override this in script)
	CONST R1, #0	; B=1 (override this in script)
EXEC
	JSR SUB_PWR	; call subroutine to find A^B and store it in R2
	SLL R2, R2, #1	; Shift R2 left by one bit to find C = 2*R2 = 2*(A^B)
END
	NOP

.CODE
.FALIGN
SUB_PWR			; inputs: R0=A, R1=B. output: R2=A^B
	CONST R2, #1	; initialize result variable
PWR_LOOP
	CMPI R1, #0
	BRnz RETURN_PWR	; while B>0
	MUL R2, R2, R0	; multiply result by A one time
	ADD R1, R1, #-1	; decrement loop counter
	JMP PWR_LOOP
RETURN_PWR
	RET
