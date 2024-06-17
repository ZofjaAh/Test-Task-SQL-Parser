package com.aheyeva.sql.parser.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@AllArgsConstructor
@Builder
@EqualsAndHashCode
@ToString
public class Source {
    private String tableName;
    private String alias;

}
