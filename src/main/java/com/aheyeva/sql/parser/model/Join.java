package com.aheyeva.sql.parser.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class Join {
    private String joinType;
    private Source leftSource;
    private String condition;

}
