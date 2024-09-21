//DEPS dev.langchain4j:langchain4j-open-ai:0.33.0
//DEPS com.github.javafaker:javafaker:1.0.2

import org.apache.camel.BindToRegistry;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;

import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;

import static java.time.Duration.ofSeconds;

import java.util.ArrayList;
import java.util.List;

import com.github.javafaker.Faker;

public class bindings extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        // Routes are loaded from YAML files
    }

    @BindToRegistry(lazy=true)
    public static ChatLanguageModel chatModel(){

        ChatLanguageModel model = OpenAiChatModel.builder()
            .apiKey("EMPTY")
            .modelName("qwen2.5:0.5b-instruct")
            .baseUrl("http://llm:8000/v1/")
            .temperature(0.0)
            .timeout(ofSeconds(180))
            .logRequests(true)
            .logResponses(true)
            .build();

        return model;
    }

    @BindToRegistry(lazy=true)
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
                    You have access to a collection of tools.

                    You can use multiple tools at the same time.

                    Complete your answer using data obtained from the tools.

                    Use short answers.
                    """;

                messages.add(new SystemMessage(systemMessage.formatted(tools)));
                messages.add(new UserMessage(payload));

                exchange.getIn().setBody(messages);
            }
        };
    }

    @BindToRegistry(lazy=true)
    public static Processor getTourGuide(){

        return new Processor() {
            public void process(Exchange exchange) throws Exception {

                String countryCode = exchange.getMessage().getBody(String.class);

                Faker faker = new Faker(new java.util.Locale(countryCode));

                String name = faker.name().fullName(); // Miss Samanta Schmidt
                String firstName = faker.name().firstName(); // Emory
                String lastName = faker.name().lastName(); // Barton
                String number = faker.phoneNumber().cellPhone();
                String streetAddress = faker.address().streetAddress(); // 60018 Sawayn Brooks Suite 449

                System.out.println("fake: " + streetAddress);

                String response = """
                    {
                        "tourGuide": {
                            "firstName":"%s",
                            "lastName" :"%s",
                            "contact": {
                                "phone":"%s"
                            }
                        }
                    }
                    """;

                response = response.formatted(firstName,lastName,number);

                exchange.getIn().setBody(response);
            }
        };
    }
}
