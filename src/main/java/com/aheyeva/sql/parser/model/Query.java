package com.aheyeva.sql.parser.model;

import lombok.*;

import java.util.List;

@Getter
@With
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Query {
    private List<String> columns;
    private List<Source> fromSources;
    private List<Join> joins;
    private List<WhereClause> whereClauses;
    private List<Having> havings;
    private List<String> groupByColumns;
    private List<Sort> sortColumns;
    private Integer limit;
    private Integer offset;

}
