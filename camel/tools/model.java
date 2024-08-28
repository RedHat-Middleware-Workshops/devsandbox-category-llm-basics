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
                // .modelName("sam4096/qwen2tools:0.5b")
                .modelName("sam4096/qwen2tools:1.5b")
                // .modelName("allenporter/xlam:1b")
                // .modelName("RobinBially/xlam-8k:1b")
                .baseUrl("http://localhost:8000/v1/")
                // .responseFormat("json_object")
                .temperature(0.0)
                .timeout(ofSeconds(180))
                .logRequests(true)
                .logResponses(true)
                .build();

      return model;
    }

    @BindToRegistry
    public static Processor prepareAIrequest(){

        return new Processor() {
            public void process(Exchange exchange) throws Exception {

              String payload = exchange.getMessage().getBody(String.class);
              List<ChatMessage> messages = new ArrayList<>();

              messages.add(new SystemMessage("""
                      When saluted you introduce yourself as an assistant that specialises in providing travelling information.
                      You have access to real-time data using the functions provided.
                      Answer all the questions by calling all the functions available.
                      Use short answers to respond with concise information.
                      You are helping customers trying to query about location information.
                      Make sure the tag 'tool_call' is well formed in JSON when calling functions.
                      """));


//               messages.add(new SystemMessage("""

// When saluted you introduce yourself as an assistant that specialises in providing travelling information.
// You have access to real-time data using the functions provided.
// Answer all the questions by calling all the functions available.
// Use short answers to respond with concise information.
// You are helping customers trying to query about location information.

// When you receive a tool call response, use the output to format an answer to the orginal user question.

//                       """));



              messages.add(new UserMessage(payload));

              exchange.getIn().setBody(messages);
            }
        };
    }

}
