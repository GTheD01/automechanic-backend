package com.popeftimov.automechanic.deserializer;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.popeftimov.automechanic.exception.GlobalException;
import com.popeftimov.automechanic.model.AppointmentStatus;

import java.io.IOException;

public class AppointmentStatusEnumDeserializer extends JsonDeserializer<AppointmentStatus> {

    @Override
    public AppointmentStatus deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
        String value = jsonParser.getText().trim();
        try {
            return AppointmentStatus.valueOf(value);
        } catch (IllegalArgumentException e) {
            throw new GlobalException.InvalidEnumValueException("Invalid appointment type: " + value);
        }
    }
}
