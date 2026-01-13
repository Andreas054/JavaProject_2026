# Library Management System

## Business Requirements

1.  The system must allow a librarian to register new readers with a name, email, and unique ID.
2.  The system must allow a librarian to add new books with a title, ISBN, and publication year.
3.  The system must allow a librarian to add new authors and link them to their books (a book can have multiple authors, and an author can have multiple books).
4.  The system must allow a librarian to categorize books by genre.
5.  The system must allow a reader to search the catalog for books by title.
6.  The system must allow a librarian to let a reader borrow a book.
7.  The system must prevent a book that is already on loan from being borrowed by another reader.
8.  The system must record the due date for any loaned book.
9.  The system must allow a librarian to return a book, making it available again.
10. The system must be able to show a list of all books currently on loan.

## MVP (Minimum Viable Product) Features

1.  Reader Management: The ability to add new readers to the system and view a list of all existing readers.
2.  Book Catalog Management: The ability to add new books, authors, and genres to the system. This includes linking authors to their books.
3.  Book Search: The ability to search the catalog for available books by their titles.
4.  Book Borrowing (Create Loan): The process of loaning a book to a reader. This service will change the book's status to `BORROWED` and create a `Loan` record.
5.  Book Return (End Loan): The process of returning a book. This service will update the `Loan` record (set `returnDate`) and change the book's status back to `AVAILABLE`.