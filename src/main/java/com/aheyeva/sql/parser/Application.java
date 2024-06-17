package com.aheyeva.sql.parser;

import com.aheyeva.sql.parser.busines.SqlParser;

import java.util.Arrays;
import java.util.List;

public class Application {

    public static void main(String[] args) {
        List<String> listCommands = Arrays.asList(
                "SELECT * FROM users",
                "SELECT name, email, age FROM users",
                "SELECT * FROM users WHERE age > 30",
                "SELECT * FROM users WHERE age > 30 AND email LIKE '%@example.com'",
                """
                        SELECT users.name, orders.order_date, orders.total
                        FROM users
                        INNER JOIN orders ON users.id = orders.user_id""",
                """
                        SELECT users.name, orders.order_date, orders.total
                        FROM users
                        LEFT JOIN orders ON users.id = orders.user_id""",
                """
                        SELECT category, COUNT(*) as total_products
                        FROM products
                        GROUP BY category
                        HAVING total_products > 10""",
                """
                        SELECT * FROM users
                        "ORDER BY name ASC, age DESC""",
                """
                        SELECT * FROM users
                        "LIMIT 10 OFFSET 20""",
                """
                        SELECT *
                        FROM (
                          SELECT * FROM users WHERE age > 30
                        ) as older_users
                        WHERE older_users.email LIKE '%@example.com'"""
        );
        SqlParser parser = new SqlParser();
        listCommands.stream()
                .map(parser::parse)
                .forEach(query -> System.out.println(query.toString()));

    }

}
