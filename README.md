### Test-Task-SQL-Parser

This is a simply SQL query parser for an arbitrary SELECT query, that converts query as a class with structure:
```java
class Query {
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
```


The parser supports:
- Enumeration of sample fields explicitly (with aliases) or *
- Implicit join of several tables (select * from A,B,C)
- Explicit join of tables (inner, left, right, full join)
- Filter conditions (where a = 1 and b > 100)
- Subqueries (select * from (select * from A) a_alias)
- Grouping by one or several fields (group by)
- Sorting by one or more fields (order by)
- Selection truncation (limit, offset)
