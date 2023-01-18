package com.finalProject.checkify.jackson;


import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.finalProject.checkify.entity.Product;

import java.io.IOException;

public class ProductListDeserialization extends StdDeserializer<Product> {

    public ProductListDeserialization() {
        this(null);
    }

       public ProductListDeserialization(Class<?> vc) {
        super(vc);
    }

    @Override
    public Product deserialize(JsonParser jsonParser, DeserializationContext deserializationContext)
            throws IOException {
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);
        String barcode =  node.get("code").asText();
        String name =  node.get("description").asText();
        String imageUrl =  node.get("image_url").asText();

        return new Product(barcode, name , imageUrl);
    }


}