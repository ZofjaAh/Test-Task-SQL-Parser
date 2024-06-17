package com.aheyeva.sql.parser.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class WhereClause {
    private String conditionType;
    private String condition;

}
