package project.global.elasticsearch.document;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemDocument {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("name")
    private String name;


    public static ItemDocument createItemDoc(Long id, String name) {
        return ItemDocument.builder()
                .id(id)
                .name(name)
                .build();
    }

}

