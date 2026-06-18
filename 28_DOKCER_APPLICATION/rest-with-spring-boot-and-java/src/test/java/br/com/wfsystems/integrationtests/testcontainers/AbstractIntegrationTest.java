package br.com.wfsystems.integrationtests.testcontainers;

import java.util.Map;
import java.util.stream.Stream;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.lifecycle.Startables;

import br.com.wfsystems.config.TestConfigs;

@ContextConfiguration(initializers = AbstractIntegrationTest.Initializer.class)
public class AbstractIntegrationTest {

    static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

        static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:9.1.0");

        private static void startContainers() {
            int maxRetries = 3;
            int retryCount = 0;
            Exception lastException = null;

            while (retryCount < maxRetries) {
                try {
                    Startables.deepStart(Stream.of(mysql)).join();
                    System.out.println("Container MySQL iniciado com sucesso!");
                    return; // Sucesso
                } catch (Exception e) {
                    lastException = e;
                    retryCount++;
                    System.out.println("Falha ao iniciar o container, tentando novamente... (tentativa " + retryCount + " de " + maxRetries + ")");
                    
                    if (retryCount < maxRetries) {
                        try {
                            Thread.sleep(3000); // Aguarda 3 segundos antes de tentar novamente
                        } catch (InterruptedException ie) {
                            Thread.currentThread().interrupt();
                            break;
                        }
                    }
                }
            }

            // Se chegou aqui, todas as tentativas falharam
            throw new RuntimeException("Falha ao iniciar o container MySQL após " + maxRetries + " tentativas", lastException);
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