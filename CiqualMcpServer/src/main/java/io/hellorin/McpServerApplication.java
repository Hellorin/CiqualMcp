package io.hellorin;

import io.hellorin.ciqual.database.EnrichedCiqualDatabase;
import io.hellorin.ciqual.database.EnrichedCiqualParser;
import io.hellorin.ciqual.tools.FoodSearchTools;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class McpServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(McpServerApplication.class, args);
    }

    @Bean
    public EnrichedCiqualDatabase ciqualDatabase() throws Exception {
        return EnrichedCiqualParser.loadDatabase();
    }

    @Bean
    public ToolCallbackProvider foodTools(FoodSearchTools foodSearchTools) {
        return MethodToolCallbackProvider.builder()
                .toolObjects(foodSearchTools)
                .build();
    }
}
