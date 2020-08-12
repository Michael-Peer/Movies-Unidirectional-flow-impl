# Movies-Unidirectional-flow-impl

This project is focused of scalable, maintaible and testable app.

Focused on effiecent erorr handling, generics, declaretive&readable code.

Detaild explaination to almost each step in inside the code

# TODO

In process: Pagingation (Both cache & network)

# Known bugs

Main slider returns to first position after configuration change - FIXED

When rotate the screen after click on movie from similar movies, the data went back to the old movie - Probably related to the previous bug fix(resetUI/Scroll Position) - FIXED

Reset UI after similar clicked - FIXED

Main slider scaling and quality

AppBar overflow

Leaks - realted to adapter(set to null in onDestoryView event)

Multiple times fetches&errors on configuration change

If there is no "Similar Movies", write a place holder error - Generic








