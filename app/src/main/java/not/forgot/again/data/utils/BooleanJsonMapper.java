package not.forgot.again.data.utils;

import java.lang.reflect.Type;

import com.google.gson.*;


public class BooleanJsonMapper implements JsonDeserializer<Boolean> {
    public Boolean deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        int code = json.getAsInt();
        return code == 0 ? false : code == 1 ? true : null;
    }
}

