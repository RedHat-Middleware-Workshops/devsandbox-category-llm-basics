//DEPS dev.langchain4j:langchain4j-open-ai:0.33.0

import org.apache.camel.BindToRegistry;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;

import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;

import static java.time.Duration.ofSeconds;

import java.util.ArrayList;
import java.util.List;


public class model {

    @BindToRegistry
    public static ChatLanguageModel chatModel(){

      ChatLanguageModel model = OpenAiChatModel.builder()
                .apiKey("EMPTY")
                // .modelName("llama3.1:latest")
                .modelName("sam4096/qwen2tools:0.5b")
                // .modelName("sam4096/qwen2tools:1.5b")
                .baseUrl("http://ollama-svc:8000/v1/")
                .temperature(0.0)
                .timeout(ofSeconds(180))
                .logRequests(true)
                .logResponses(true)
                .build();

      return model;
    }

    @BindToRegistry
    public static Processor createChatMessage(){

        return new Processor() {
            public void process(Exchange exchange) throws Exception {

              String payload = exchange.getMessage().getBody(String.class);
              List<ChatMessage> messages = new ArrayList<>();

              String systemMessage = """
                  Introduce yourself as a helpful travel assistant.

                  %s

                  Respond with short answers.
                  """;

              String tools = """
                  When asked to provide real time data (information), respond with:

                  - I'm sorry, I don't have access to real time information.

                  Do not improvise answers for any real time related questions.
                  """;

              tools = """
                      You have access to a collection of tools.

                      To use a tool, respond with a JSON object with the following structure:

                      "content": null,
                      "tool_calls": [
                        {
                          "type": "function",
                          "function": {
                            "name": <name of the called tool>,
                            "arguments": <parameters for the tool matching the above JSON schema>
                          }
                        }
                      ]
                      """;

              messages.add(new SystemMessage(systemMessage.formatted(tools)));
              // messages.add(new SystemMessage(systemMessage));
              messages.add(new UserMessage(payload));

              exchange.getIn().setBody(messages);
            }
        };
    }
}
