package ru.avkurbatov_home.jdo;

import com.google.common.collect.ImmutableMap;
import lombok.Data;
import ru.avkurbatov_home.utils.StringTypeConverter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Data
public class Message {

    private static DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;

    private Integer id;

    @NotNull(message="Message must have topicId, please contact the developer")
    private Integer topicId;

    @NotBlank(message="Message must have accountUsername, please contact the developer")
    private String accountUsername;

    private LocalDateTime date;

    @NotBlank(message="No text in message")
    private String text;

    public Map<String, String> buildRedisMap(){
        LocalDateTime dbDate = date != null ? date : LocalDateTime.now();

        return ImmutableMap.<String, String>builder()
                .put("id", StringTypeConverter.fromInteger(id))
                .put("topicId", StringTypeConverter.fromInteger(topicId))
                .put("accountUsername", accountUsername)
                .put("date", dbDate.format(formatter))
                .put("text", text)
                .build();
    }

    public static Message fromRedisMap(Map<String, String> map){
        if (map == null || map.isEmpty()) {
            return null;
        }
        Message message = new Message();
        message.setId(StringTypeConverter.toInteger(map.get("id")));
        message.setTopicId(StringTypeConverter.toInteger(map.get("topicId")));
        message.setAccountUsername(map.get("accountUsername"));
        message.setDate(LocalDateTime.parse(map.get("date"), formatter));
        message.setText(map.get("text"));
        return message;
    }

}
