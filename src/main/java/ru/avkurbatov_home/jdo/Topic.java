package ru.avkurbatov_home.jdo;

import com.google.common.collect.ImmutableMap;
import lombok.Data;
import ru.avkurbatov_home.utils.StringTypeConverter;
import ru.avkurbatov_home.utils.Utils;

import javax.validation.constraints.NotBlank;
import java.util.Map;

@Data
public class Topic {

    private Integer id;

    @NotBlank(message="Topic must have title")
    private String title;

    @SuppressWarnings("unchecked")
    public Map<String, Object> buildSqlMap(){
        return Utils.OBJECT_MAPPER.convertValue(this, Map.class);
    }

    public Map<String, String> buildRedisMap(){
        return ImmutableMap.<String, String>builder()
                .put("id", StringTypeConverter.fromInteger(id))
                .put("title", title)
                .build();
    }

    public static Topic fromRedisMap(Map<String, String> map){
        if (map == null || map.isEmpty()) {
            return null;
        }
        Topic topic = new Topic();
        topic.setId(StringTypeConverter.toInteger(map.get("id")));
        topic.setTitle(map.get("title"));
        return topic;
    }
}
