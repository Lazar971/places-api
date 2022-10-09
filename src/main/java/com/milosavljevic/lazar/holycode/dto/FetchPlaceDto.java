package com.milosavljevic.lazar.holycode.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
public class FetchPlaceDto {
  @JsonProperty("local_entry_id")
  private String id;
  @JsonProperty("displayed_where")
  private String displayedWhere;
  @JsonProperty("displayed_what")
  private String displayedWhat;
  @JsonProperty("opening_hours")
  private WorkingHours openingHours;

  @Setter
  @Getter
  public static class WorkingHours {
    private Map<String, List<WorkingHourSlot>> days;
  }

  public Map<String, List<WorkingHourSlot>> getWorkingHours() {
    return openingHours.days;
  }
}
