package br.com.wfsystems.integrationtests.testcontainers;

import java.time.Duration;
import java.util.Map;
import java.util.stream.Stream;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.containers.wait.strategy.LogMessageWaitStrategy;
import org.testcontainers.lifecycle.Startables;

import br.com.wfsystems.config.TestConfigs;

@ContextConfiguration(initializers = AbstractIntegrationTest.Initializer.class)
public class AbstractIntegrationTest {

    static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

        static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:9.1.0")
                .withStartupTimeout(Duration.ofSeconds(120))
                .waitingFor(new LogMessageWaitStrategy()
                        .withRegEx(".*ready for connections.*")
                        .withTimes(2)
                        .withStartupTimeout(Duration.ofSeconds(120)));

        private static void startContainers() {
            int maxRetries = 3;
            int retryCount = 0;
            Exception lastException = null;

            while (retryCount < maxRetries) {
                try {
                    System.out.println("🔄 Iniciando container MySQL (tentativa " + (retryCount + 1) + " de " + maxRetries + ")...");
                    
                    Startables.deepStart(Stream.of(mysql)).join();
                    
                    System.out.println("✅ Container MySQL iniciado com SUCESSO!");
                    System.out.println("   URL JDBC: " + mysql.getJdbcUrl());
                    System.out.println("   Usuário: " + mysql.getUsername());
                    return; // Sucesso
                    
                } catch (Exception e) {
                    lastException = e;
                    retryCount++;
                    
                    System.err.println("❌ ERRO ao iniciar container (tentativa " + retryCount + " de " + maxRetries + ")");
                    System.err.println("   Causa: " + e.getMessage());
                    e.printStackTrace(System.err);
                    
                    // Aguarda antes de tentar novamente
                    if (retryCount < maxRetries) {
                        try {
                            System.out.println("   ⏳ Aguardando 5 segundos antes de tentar novamente...");
                            Thread.sleep(5000);
                        } catch (InterruptedException ie) {
                            Thread.currentThread().interrupt();
                            throw new RuntimeException("Inicialização do container interrompida", ie);
                        }
                    }
                }
            }

            // Todas as tentativas falharam
            System.err.println("\n❌ FATAL: Falha ao iniciar container MySQL após " + maxRetries + " tentativas");
            if (lastException != null) {
                lastException.printStackTrace(System.err);
            }
            throw new RuntimeException(
                    "Falha ao iniciar o container MySQL após " + maxRetries + 
                    " tentativas. Causa: " + 
                    (lastException != null ? lastException.getMessage() : "Desconhecida"),
                    lastException
            );
        }

        private Map<String, String> createConnectionConfiguration() {
            return Map.of(
                    "spring.datasource.url", mysql.getJdbcUrl(),
                    "spring.datasource.username", mysql.getUsername(),
                    "spring.datasource.password", mysql.getPassword(),
                    "server.port", String.valueOf(TestConfigs.SERVER_PORT));
        }

        @Override
        public void initialize(ConfigurableApplicationContext applicationContext) {
            startContainers();
            ConfigurableEnvironment environment = applicationContext.getEnvironment();
            MapPropertySource testContainers = new MapPropertySource("testcontainers",
                    (Map) createConnectionConfiguration());
            environment.getPropertySources().addFirst(testContainers);
        }
    }
}