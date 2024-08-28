camel run * web/* \
--maven-apache-snapshot-enabled=false \
--logging-level=ERROR \
--logging-category=dev.ai4j.openai4j=DEBUG \
--dep=dev.langchain4j:langchain4j-open-ai:0.33.0 \
--dep=com.github.javafaker:javafaker:1.0.2
