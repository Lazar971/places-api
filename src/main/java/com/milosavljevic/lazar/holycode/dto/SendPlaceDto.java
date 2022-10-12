package com.milosavljevic.lazar.holycode.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
public class SendPlaceDto {
  private String id;
  private String name;
  private String address;
  private Map<String, List<WorkingHourSlot>> workingHours;
  private boolean open;
  @JsonInclude(JsonInclude.Include.NON_NULL)
  private String closingTime;
  @JsonInclude(JsonInclude.Include.NON_NULL)
  private String openingTime;

}
