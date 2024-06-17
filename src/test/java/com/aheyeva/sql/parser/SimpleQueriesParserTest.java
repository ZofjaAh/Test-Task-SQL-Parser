package com.aheyeva.sql.parser;
import com.aheyeva.sql.parser.busines.SqlParser;
import com.aheyeva.sql.parser.busines.TokensCreator;
import com.aheyeva.sql.parser.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.assertj.core.api.Assertions.assertThat;

public class SimpleQueriesParserTest {


    private SqlParser sqlParser;


    @BeforeEach
    void init(){
        this.
                sqlParser= new SqlParser(new TokensCreator());
    }

        @Test
        void parseQueryWithColumnsAndFrom() {
            Query query = sqlParser.parse("SELECT name, age FROM users");
            assertEquals(List.of("name", "age"), query.getColumns());
            assertEquals(List.of(new Source("users", null)), query.getFromSources());
        }

        @Test
        void parseQueryWithJoins() {
            Query query = sqlParser.parse("SELECT name, age FROM users JOIN orders ON users.id = orders.user_id");
            assertEquals(List.of(new Join("INNER",new Source("orders", null),
                    "users.id = orders.user_id")),
                    query.getJoins());
        }

        @Test
        void parseQueryWithWhereClause() {
            Query query = sqlParser.parse("SELECT name, age FROM users WHERE age > 18");
            assertEquals(List.of(new WhereClause(null,"age > 18")), query.getWhereClauses());
        }

        @Test
        void parseQueryWithGroupBy() {
            Query query = sqlParser.parse("SELECT name, COUNT(*) FROM users GROUP BY name");
            assertThat(query.getGroupByColumns())
                    .isEqualTo(List.of("name"));
        }

        @Test
        void parseQueryWithSort() {
            Query query = sqlParser.parse("SELECT name, age FROM users ORDER BY age DESC");
            assertThat(query.getSortColumns())
                    .isEqualTo(List.of(new Sort("age", "DESC")));
        }

        @Test
        void parseQueryWithPaging() {
            Query query = sqlParser.parse("SELECT name, age FROM users LIMIT 10 OFFSET 20");
            assertThat(query.getLimit())
                    .isEqualTo(10);
            assertThat(query.getOffset())
                    .isEqualTo(20);

        }
    }


