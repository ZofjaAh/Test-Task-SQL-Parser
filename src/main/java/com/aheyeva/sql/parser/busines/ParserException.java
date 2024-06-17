package com.aheyeva.sql.parser.busines;

public class ParserException extends RuntimeException {

    public ParserException() {

        super("Sorry! The program couldn't recognise the SQL-query. Please, make it simpler!");
    }
}

