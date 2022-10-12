package com.milosavljevic.lazar.holycode.dto;

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
}
