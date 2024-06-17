package com.aheyeva.sql.parser;


import com.aheyeva.sql.parser.busines.SqlParser;
import com.aheyeva.sql.parser.busines.TokensCreator;
import com.aheyeva.sql.parser.model.Query;
import com.aheyeva.sql.parser.model.Source;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ParserQueriesTests {
	private SqlParser sqlParser;
	@BeforeEach
	void init(){
		this.sqlParser= new SqlParser();
	}
	@Test
	void whenSelectByAsterisk() {
		String sql = "SELECT * FROM employee";
		Query query = sqlParser.parse(sql);
		assertThat(query.getColumns())
				.isEqualTo(List.of("*"));
		assertThat(query.getFromSources())
				.isEqualTo(List.of(new Source("employee", null)));
	}

		@Test
		void whenSelectImplicitly() {
			String sql = "SELECT name FROM users";
			Query query = sqlParser.parse(sql);
			assertThat(query.getColumns())
					.isEqualTo(List.of("name"));
		}

		@Test
		void whenSelectMultiColumns() {
			String sql = "SELECT name, email FROM users";
			Query query = sqlParser.parse(sql);
			assertThat(query.getColumns())
					.isEqualTo(List.of("name", "email"));
		}

		@Test
		void whenSelectMultiTable() {
			String sql = "SELECT * FROM users, roles";
			Query query = sqlParser.parse(sql);
			assertThat(query.getFromSources())
					.isEqualTo(
							List.of(new Source("users", null), new Source("roles", null))
					);
		}

	@Test
	void whenSubSelect() {
		String sql = "SELECT * FROM (SELECT * FROM table1) AS t1";
		Query query = sqlParser.parse(sql);
		assertThat(query.getColumns())
				.isEqualTo(List.of("*"));
		assertThat(query.getFromSources())
				.isEqualTo(List.of(new Source("(SELECT * FROM table1)", "t1")));
	}


	}
