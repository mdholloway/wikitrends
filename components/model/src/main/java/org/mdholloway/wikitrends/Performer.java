package org.mdholloway.wikitrends;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Performer {
    @JsonProperty("user_id") public long id;
    @JsonProperty("user_text") public String name;
}
