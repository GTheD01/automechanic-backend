package com.popeftimov.automechanic.deserializer;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.popeftimov.automechanic.exception.GlobalException;
import com.popeftimov.automechanic.model.ReportType;

import java.io.IOException;

public class ReportTypeEnumDeserializer extends JsonDeserializer<ReportType> {

    @Override
    public ReportType deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
        String value = jsonParser.getText().trim();
        try {
            return ReportType.valueOf(value);
        } catch (IllegalArgumentException e) {
            throw new GlobalException.InvalidEnumValueException("Invalid report type: " + value);
        }
    }
}
