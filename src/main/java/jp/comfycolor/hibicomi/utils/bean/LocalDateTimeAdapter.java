package jp.comfycolor.hibicomi.utils.bean;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

@FunctionalInterface
public interface LocalDateTimeAdapter extends JsonSerializer<LocalDateTime>, JsonDeserializer<LocalDateTime> {
	@Override
	public default JsonElement serialize(LocalDateTime datetime, Type type, JsonSerializationContext context) {
		return new JsonPrimitive(datetime.format(getFormatter()));
	}

	@Override
	public default LocalDateTime deserialize(JsonElement json, Type type, JsonDeserializationContext context)
			throws JsonParseException {
		return LocalDateTime.parse(json.getAsString(), getFormatter());
	}

	public static LocalDateTimeAdapter create(LocalDateTimeAdapter a) {
		return a;
	}

	public DateTimeFormatter getFormatter();
}