package edu.brown.cs.student.main.server.Handlers;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import edu.brown.cs.student.main.server.Events.Event;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Map;

public class Utils {

  public static String toMoshiJson(Map<String, Object> map) {
    Moshi moshi = new Moshi.Builder().build();
    Type mapStringObject = Types.newParameterizedType(Map.class, String.class, Object.class);
    JsonAdapter<Map<String, Object>> adapter = moshi.adapter(mapStringObject);

    return adapter.toJson(map);
  }

  public static Event deserializeJsonEvent(java.lang.String jsonString) throws IOException {
    // Initializes Moshi
    Moshi moshi = new Moshi.Builder().build();
    JsonAdapter<Event> jsonAdapter = moshi.adapter(Event.class);

    //    JsonAdapter<List<String>> adapter = moshi.adapter(listType);
    return jsonAdapter.fromJson(jsonString);
  }
}
