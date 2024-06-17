package com.aheyeva.sql.parser;

import com.aheyeva.sql.parser.busines.SqlParser;
import com.aheyeva.sql.parser.busines.TokensCreator;
import com.aheyeva.sql.parser.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class DifficultQueriesParserTests {
	private SqlParser sqlParser;
	@BeforeEach
	void init(){
		this.sqlParser= new SqlParser();
	}
	@Test
	void whenSelectByAsterisk() {
		String sql = """
				SELECT c.customer_name, o.order_date, p.product_name, p.price
				FROM customers c
				JOIN orders o ON c.customer_id = o.customer_id
				JOIN order_items oi ON o.order_id = oi.order_id
				JOIN products p ON oi.product_id = p.product_id
				WHERE c.country = 'USA' AND o.order_date BETWEEN '2022-01-01' AND '2022-12-31'
				ORDER BY o.order_date DESC
				LIMIT 10
				""";
		Query query = sqlParser.parse(sql);
		assertThat(query.getColumns())
				.isEqualTo(List.of("c.customer_name", "o.order_date", "p.product_name", "p.price" ));
		assertThat(query.getFromSources())
				.isEqualTo(List.of(new Source("customers", "c")));
	assertThat(query.getJoins())
				.isEqualTo(List.of(
						new Join("INNER",
								new Source("orders", "o"),
						"c.customer_id = o.customer_id"),
						new Join("INNER",
								new Source("order_items", "oi"),
								"o.order_id = oi.order_id"),
						new Join("INNER",
								new Source("products", "p"),
								"oi.product_id = p.product_id")));
		assertThat(query.getWhereClauses())
				.isEqualTo(List.of(new WhereClause(null,"c.country = 'USA'"),
						new WhereClause("AND", "o.order_date"),
						new WhereClause("BETWEEN", "'2022-01-01'"),
						new WhereClause("AND", "'2022-12-31'")));
		assertThat(query.getSortColumns())
				.isEqualTo(List.of(new Sort("o.order_date", "DESC")));
		assertThat(query.getLimit())
				.isEqualTo(10);
	}



	@Test
	void whenSelectWithVariousConditions() {
		String sql = "SELECT table1.name, count(table2.id), sum(table2.cost) " +
				"FROM table1 " +
				"LEFT JOIN table2 ON table1.id = table2.table1_id " +
				"GROUP BY table1.name " +
				"HAVING COUNT(*) > 1 AND SUM(table2.cost) > 500 " +
				"LIMIT 10";
		Query query = sqlParser.parse(sql);
		assertThat(query.getColumns())
				.isEqualTo(List.of("table1.name", "count(table2.id)", "sum(table2.cost)"));
		assertThat(query.getFromSources())
				.isEqualTo(List.of(new Source("table1", null)));
		assertThat(query.getJoins())
				.isEqualTo(List.of(new Join("LEFT",
						new Source("table2", null),
						"table1.id = table2.table1_id")));
		assertThat(query.getGroupByColumns())
				.isEqualTo(List.of("table1.name"));
		assertThat(query.getHavings())
				.isEqualTo(List.of(new Having("COUNT","(*) > 1"), new Having("SUM", "(table2.cost) > 500")));
		assertThat(query.getLimit())
				.isEqualTo(10);
	}


	}