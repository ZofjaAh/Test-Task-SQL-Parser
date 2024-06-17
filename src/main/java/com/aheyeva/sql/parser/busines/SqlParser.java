package com.aheyeva.sql.parser.busines;


import com.aheyeva.sql.parser.model.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
@Slf4j
public class SqlParser {

    public Query parse(String sqlQuery) {
        TokensCreator tokensCreator = new TokensCreator();
        Map<String, List<String>> elements = tokensCreator.create(sqlQuery);
        var queryBuilder = Query.builder();

        if (elements.containsKey("SELECT")) {
            queryBuilder.columns(createColumns(elements.get("SELECT")));
        }
        if (elements.containsKey("FROM")) {
            queryBuilder.fromSources(createFromSources(elements.get("FROM")));
        }
        if (elements.containsKey("JOIN")) {
            queryBuilder.joins(createJoins(elements.get("JOIN")));
        }
        if (elements.containsKey("WHERE")) {
            queryBuilder.whereClauses(createConditions(elements.get("WHERE")));
        }
        if (elements.containsKey("GROUP")) {
            queryBuilder.groupByColumns(createGroupByColumns(elements.get("GROUP")));
        }
        if (elements.containsKey("HAVING")) {
            queryBuilder.havings(createHavings(elements.get("HAVING")));
        }
        if (elements.containsKey("ORDER")) {
            queryBuilder.sortColumns(createSortColumns(elements.get("ORDER")));
        }
        if (elements.containsKey("LIMIT")) {
            queryBuilder.limit(Integer.parseInt(elements.get("LIMIT").get(0)));
        }
        if (elements.containsKey("OFFSET")) {
            queryBuilder.offset(Integer.parseInt(elements.get("OFFSET").get(0)));
        }
        return queryBuilder.build();
    }


    private List<String> createColumns(List<String> tokens) {
        List<String> listColumns = new ArrayList<>();
        if (tokens.contains("*")) {
            listColumns.add("*");
        } else if (tokens.contains(",") && tokens.size() > 1) {
            String[] split = tokens.stream().reduce("", (priv, next) -> priv + next)
                    .split(",");
            listColumns.addAll(Arrays.asList(split));
        } else {
            listColumns.addAll(tokens);
        }

        return listColumns;
    }

    private List<Source> createFromSources(List<String> tokens) {
        if (tokens.isEmpty()) {
            throw new ParserException();
        } else if (!tokens.contains(",")) {

            if (!tokens.contains("AS") && tokens.size() == 1) {
                return List.of(new Source(tokens.get(0), null));
            } else if (!tokens.contains("AS") && tokens.size() == 2) {
                return List.of(new Source(tokens.get(0), tokens.get(1)));
            } else if (tokens.contains("AS") && tokens.size() > 3) {
                int aliasIndex = tokens.indexOf("AS");
                return List.of(new Source(getReduce(tokens, 0, aliasIndex).substring(1),
                        tokens.get(tokens.size()-1 )));
            } else {
                return List.of(new Source(tokens.get(0), tokens.get(2)));
            }
        } else {
            List<String> split = getSplit(tokens, " ,");
            if (tokens.contains("AS")) {
                //contain "," && "AS"
                return split.stream()
                        .map(source -> source.split(" AS"))
                        .map(source -> new Source(source[0], source[1]))
                        .toList();
            }// contain "," && " "
            else if (split.stream().anyMatch(element -> element.contains(" "))) {
                return split.stream()
                        .map(source -> source.split(" "))
                        .map(source -> new Source(source[0], source[1]))
                        .toList();
            } else {
                return split.stream().map(el -> new Source(el, null)).toList();
            }
        }
    }

    private List<Join> createJoins(List<String> tokens) {
        List<String> typesJoin = List.of("INNER", "RIGHT", "LEFT", "FULL");
        String joinType;
        Source leftSource;
        int joinIndex;
        int aliasIndex;
        int onIndex;
        List<Join> listJoins = new ArrayList<>();
        List<Integer> groupJoinIndex = new ArrayList<>();
        for (int i = 0; i < tokens.size(); i++) {
            if (tokens.get(i).equals("JOIN")) {
                groupJoinIndex.add(i);
            }
        }
        List<List<String>> joinConditions = new ArrayList<>();
        for (int j = 0; j < groupJoinIndex.size(); j++) {
            if (j == (groupJoinIndex.size() - 1)) {
                joinConditions.add(tokens.subList(groupJoinIndex.get(j) - 1, tokens.size()));
            } else {
                joinConditions.add(tokens.subList(groupJoinIndex.get(j) - 1, groupJoinIndex.get(j + 1)));
            }}
            for(List<String> joinCondition: joinConditions){
            joinIndex = joinCondition.indexOf("JOIN");
            aliasIndex = tokens.indexOf("AS");
            onIndex = tokens.indexOf("ON");
            if (joinIndex == 1 && !typesJoin.contains(joinCondition.get(0))) {
                joinType = "INNER";
                if (aliasIndex == -1 && (onIndex - joinIndex) > 2 ) {
                    leftSource = new Source(joinCondition.get(joinIndex + 1), joinCondition.get(joinIndex + 2));
                } else if (aliasIndex == -1) {
                    leftSource = new Source(joinCondition.get(joinIndex + 1), null);
                }
                else {
                    leftSource = new Source(joinCondition.get(joinIndex + 1), joinCondition.get(aliasIndex + 1));

                }
                listJoins.add(new Join(joinType, leftSource,
                        getReduce(joinCondition, onIndex + 1, joinCondition.size()).substring(1)));
            }
            else if (joinIndex == 1 && typesJoin.contains(joinCondition.get(0))) {
                joinType = joinCondition.get(0);
                if (aliasIndex == -1) {
                    leftSource = new Source(joinCondition.get(joinIndex + 1), null);
                } else {
                    leftSource = new Source(joinCondition.get(joinIndex + 1), joinCondition.get(aliasIndex + 1));

                }
                listJoins.add(new Join(joinType, leftSource,
                        getReduce(joinCondition, onIndex + 1, joinCondition.size()).substring(1)));

            } else {throw new ParserException();}
        }
            return listJoins;}





    private List<WhereClause> createConditions(List<String> tokens) {
        List<WhereClause> listConditions = new ArrayList<>();
        if (tokens.contains("NOT") || tokens.contains("AND") || tokens.contains("OR")) {
            String[] split = tokens.stream().reduce("", (priv, next) -> priv + " " + next)
                    .replaceAll("NOT", "/NOT)")
                    .replaceAll("AND", "/AND)")
                    .replaceAll("BETWEEN", "/BETWEEN)")
                    .replaceAll("OR", "/OR)")
                    .split(" /");
            for (String condition : split) {
                int index = condition.indexOf(")");
                condition = condition.replace(")", "");
                if (index == -1) {
                    listConditions.add(new WhereClause(null, condition.substring(1)));
                } else {
                    listConditions.add(new WhereClause(condition.substring(0, index), condition.substring(index + 1)));
                }
            }
        } else {
            listConditions.add(new WhereClause(null, getReduce(tokens,0, tokens.size()).substring(1)));
        }
        return listConditions;
    }


    private List<String> createGroupByColumns(List<String> tokens) {
        List<String> listGroups = new ArrayList<>();
        if (!tokens.contains(",") && tokens.size() == 2) {
            listGroups.add(tokens.get(1));
        } else {
            listGroups.addAll(getSplit(tokens.subList(1, tokens.size()), " ,"));
        }
        return listGroups;
    }

    private List<Having> createHavings(List<String> tokens) {
        List<Having> listHavings = new ArrayList<>();
        if ( !tokens.contains("AND")) {
            var havingCondition = getReduce(tokens,0, tokens.size());
            if(tokens.contains(")")){
            int index = havingCondition.indexOf("(");
            listHavings.add(new Having(tokens.get(0), havingCondition.substring(index)));
            }else {
                listHavings.add(new Having(null, havingCondition));

            }
        }else    {
            var splitHaving = getSplit(tokens, " AND");
            for (String having : splitHaving) {
                if(having.contains(")")){
                int index = having.indexOf("(");
                listHavings.add(new Having(having.substring(0, index), having.substring(index)));
            }else {
                    listHavings.add(new Having(null, having));

                }}}
            return listHavings;


    }

    private List<Sort> createSortColumns(List<String> tokens) {
        tokens = tokens.subList(1,tokens.size());
        List<Sort> listSorts = new ArrayList<>();
        if (!tokens.contains(",") && tokens.size() == 1) {
            listSorts.add(new Sort(tokens.get(0), "ASC"));
        } else {
            var splitToken = getSplit(tokens, " ,");
            for (String sort : splitToken) {
                if (sort.contains(" ")) {
                    int index = sort.indexOf(" ");
                    listSorts.add(new Sort(sort.substring(0, index), sort.substring(index + 1)));
                } else {
                    listSorts.add(new Sort(sort, "ASC"));
                }
            }
        }
        return listSorts;
    }

    private static String getReduce(List<String> joinCondition, int from, int to) {
        return joinCondition.subList(from, to).stream().reduce("", (priv, next) -> priv + " " + next);
    }

    private static List<String> getSplit(List<String> tokens, String regex) {
        return Arrays.stream(tokens.stream().reduce("", (priv, next) -> priv + " " + next)
                .split(regex)).map(element -> element.substring(1)).toList();

    }
}








