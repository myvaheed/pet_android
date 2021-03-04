package not.forgot.again.data.utils;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.util.Date;

import not.forgot.again.model.entities.Category;
import not.forgot.again.model.entities.Priority;
import not.forgot.again.model.entities.Task;

public class TaskJsonMapper implements JsonSerializer<Task>, JsonDeserializer<Task> {

    Gson gson = new Gson();

    @Override
    public JsonElement serialize(Task src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.add("title", new JsonPrimitive(src.getTitle()));
        jsonObject.add("description", new JsonPrimitive(src.getDescription()));
        if (src.isDone()) {
            jsonObject.add("done", new JsonPrimitive(1));
        } else {
            jsonObject.add("done", new JsonPrimitive(0));
        }
        jsonObject.add("deadline", new JsonPrimitive(src.getDate().getTime() / 1000));
        jsonObject.add("category_id", new JsonPrimitive(src.getCategory().getId()));
        jsonObject.add("priority_id", new JsonPrimitive(src.getPriority().getId()));
        return jsonObject;
    }

    @Override
    public Task deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject obj = json.getAsJsonObject();
        int id = obj.getAsJsonPrimitive("id").getAsInt();
        String title = obj.getAsJsonPrimitive("title").getAsString();
        String description = obj.getAsJsonPrimitive("description").getAsString();
        boolean done = obj.has("done") && obj.get("done").getAsInt() != 0;
        Date deadline = new Date(obj.get("deadline").getAsLong() * 1000);
        Category category = gson.fromJson(obj.getAsJsonObject("category"), Category.class);
        Priority priority = gson.fromJson(obj.getAsJsonObject("priority"), Priority.class);
//        return new Task(id, 0, title, description,  deadline,  done,category, priority);
        return new Task(id, 0, title, description, deadline, done, category, priority);
    }
}
