#	myassembler

Assembler is a demonstration in book TPDSL, it's a generic bytecode assembler whose instructions take 0..3 operands.
I rewritten it in my own coding style and implement a stackmachine executing the bytecode.


###	Demonstration

####	source code
	; int x, y	
	.globals 2 

	.def main: args=0, locals=0
	; x = 9
		iconst 9
		gstore 0
	; y = x
		gload 0
		gstore 1
	; print y
		gload 1
		print 
		halt

####	output
	1
	