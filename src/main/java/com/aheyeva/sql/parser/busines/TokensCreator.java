package com.aheyeva.sql.parser.busines;

import ch.qos.logback.core.joran.sanity.Pair;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.event.KeyValuePair;

import java.util.*;
import java.util.stream.Stream;
 @Slf4j
public class TokensCreator {
    Map<String, List<String>> keyWords = Map.of(
            "SELECT", List.of("FROM"),
            "FROM", List.of("JOIN","INNER","RIGHT","LEFT", "FULL","WHERE", "GROUP", "HAVING", "ORDER", "LIMIT", "OFFSET"),
            "JOIN", List.of("WHERE", "GROUP", "HAVING", "ORDER", "LIMIT", "OFFSET"),
            "WHERE", List.of("GROUP", "HAVING", "ORDER", "LIMIT", "OFFSET"),
            "GROUP", List.of("HAVING", "ORDER", "LIMIT", "OFFSET"),
            "HAVING", List.of("ORDER", "LIMIT", "OFFSET"),
            "ORDER", List.of("LIMIT", "OFFSET"),
            "LIMIT", List.of("OFFSET")
            ,"OFFSET", List.of()
    ); 
    Map<String, List<String>> sqlTokens = new HashMap<>();

        public Map<String, List<String>> create(String expr) {
         List<String> tokens =  Arrays.stream(expr.replaceAll("\\s+", " ")
                   .replaceAll("\\n", "")
                 .replaceAll(",", " ,")
                 .split(" ")).toList();
         log.info("SQl-query has been separated on tokens: [{}]", tokens);
        keyWords.forEach((key, value)-> {
            if (tokens.contains(key)) {
                List<String> condition = range(key, value, tokens);
                log.info("We have some group of tokens select by key-word: [{}] - [{}]",key, condition);
                sqlTokens.put(key, condition);
            }
        });

    return this.sqlTokens;
        }
    private  List<String> range(String key, List<String> value, List<String> tokens ) {
        int startIndex = -1;
        int endIndex = tokens.size();
        List<String> result = new ArrayList<>();
        for (int i = 0; i < tokens.size(); i++) {
            if (startIndex == -1 && tokens.get(i).equals(key)) {
                startIndex = i;
                if(tokens.get(i).equals("JOIN")){
                    startIndex = i-2;
                }
            }
            if (value.contains(tokens.get(i))) {
                endIndex = i;
                break;
            }
        }
        if (startIndex == -1) {
            return result;
        }
        for (int i = startIndex + 1; i < endIndex; i++) {
            result.add(tokens.get(i));
        }
       /* for (int i = endIndex - 1; i >= startIndex; i--) {
            tokens.remove(i);
        }*/
        return result;
    }

    }
