package io.hellorin.mcp.client;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.client.ChatClient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChatControllerTest {

    @Mock
    private ChatClient chatClient;

    @Mock
    private ChatClient.ChatClientRequestSpec requestSpec;

    @Mock
    private ChatClient.CallResponseSpec callResponseSpec;

    @Test
    void chat_returnsReplyFromChatClientContent() {
        when(chatClient.prompt("What are the macros of chicken breast?")).thenReturn(requestSpec);
        when(requestSpec.call()).thenReturn(callResponseSpec);
        when(callResponseSpec.content()).thenReturn("Chicken breast has about 165 kcal per 100g.");

        ChatController controller = new ChatController(chatClient);
        ChatController.ChatReply reply = controller.chat(
            new ChatController.ChatRequest("What are the macros of chicken breast?")
        );

        assertThat(reply.reply()).isEqualTo("Chicken breast has about 165 kcal per 100g.");
    }

    @Test
    void chat_passesUserMessageThroughUnmodified() {
        when(chatClient.prompt("hello")).thenReturn(requestSpec);
        when(requestSpec.call()).thenReturn(callResponseSpec);
        when(callResponseSpec.content()).thenReturn("hi");

        ChatController controller = new ChatController(chatClient);
        controller.chat(new ChatController.ChatRequest("hello"));

        verify(chatClient).prompt("hello");
    }
}
