import org.apache.camel.BindToRegistry;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import com.github.javafaker.Faker;

public class processors {

    @BindToRegistry
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

                //   String response = "{\"tourGuide\":{\"firstName\":\""+firstName+"\",\"lastName\":\""+lastName+"\", \"contact\":\""+number+"\"}}";
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
