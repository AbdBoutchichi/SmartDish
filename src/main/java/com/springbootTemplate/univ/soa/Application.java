package com.springbootTemplate.univ.soa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;

/**
 * Application principale MS-Recette
 *
 * Note: Les auto-configurations JDBC/JPA sont désactivées car ce service
 * ne se connecte pas directement à une base de données. Il communique
 * avec MS-Persistance via HTTP/REST pour toutes les opérations de persistance.
 */
@SpringBootApplication(exclude = {
    DataSourceAutoConfiguration.class,
    HibernateJpaAutoConfiguration.class
})
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
		System.out.println("\n");
		System.out.println("╔══════════════════════════════════════════════════════════╗");
		System.out.println("║   🚀 MS-Recette démarré avec succès !                   ║");
		System.out.println("║   📡 Communication avec MS-Persistance via HTTP          ║");
		System.out.println("║   🔗 http://localhost:8081                               ║");
		System.out.println("╚══════════════════════════════════════════════════════════╝");
		System.out.println("\n");
	}

}
