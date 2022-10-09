package com.milosavljevic.lazar.holycode.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
public class WorkingHourSlot {
  private String start;
  private String end;
}
