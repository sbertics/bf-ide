bf-ide
======
Scott Bertics

1/12/12

(Note: Currently under development.  Much of the functionality has not been added yet.)

APPLICATION OVERVIEW

bf-ide is a fully functional integrated developement environment (IDE) for BF written in Java.

THE LANGUAGE

BF (also know as brainfuck) is an esoteric programming language created by Urban Müller.
It is one of the simplest turing complete languages consisting of only eight instructions:

    + - > < . , [ ]

INSTRUCTIONS

Each BF program begins by initializing an array of (typically) 30,000 characters and a pointer
variable that initially points to the zeroth element of that array.  The instructions behave as follows:

    +    Increment the value at the current pointer by one.
    -    Decrement the value at the current pointer by one.
    >    Increment the pointer so it points to the next element in the array.
    <    Decrement the pointer so it points to the previous element in the array.
    .    Print the character at the pointer to the console.
    ,    Read in the next character from standard input to the current pointer.
    [    While the value at the pointer is not zero.
    ]    Jump to the matching opening bracket.

SAMPLE PROGRAMS

This program will print the character '0' (ascii value 48):
++++++[>++++++++<-]>.

This program will print 'Hello World!':
++++++++++[>+++++++>++++++++++>+++>+<<<<-]>++.>+.+++++++..+++.>++.<<+++++++++++++++.>.+++.------.--------.>+.>.
