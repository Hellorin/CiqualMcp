package io.hellorin.mcp.client;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.tool.ToolCallbackProvider;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class McpClientApplicationTest {

    @Mock
    private ChatModel chatModel;

    @Mock
    private ToolCallbackProvider toolCallbackProvider;

    @Test
    void chatClientBean_buildsSuccessfullyWithProvidedModelAndTools() {
        McpClientApplication application = new McpClientApplication();

        ChatClient chatClient = application.chatClient(chatModel, toolCallbackProvider);

        assertThat(chatClient).isNotNull();
    }
}
