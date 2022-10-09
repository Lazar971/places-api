package com.milosavljevic.lazar.holycode.service.impl;

import com.milosavljevic.lazar.holycode.dto.FetchPlaceDto;
import com.milosavljevic.lazar.holycode.dto.SendPlaceDto;
import com.milosavljevic.lazar.holycode.dto.WorkingHourSlot;
import com.milosavljevic.lazar.holycode.exception.NotFoundException;
import com.milosavljevic.lazar.holycode.service.PlacesService;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Setter
@Getter
@ConfigurationProperties(prefix = "places")
public class PlacesServiceImpl implements PlacesService {
  private String url;
  private RestTemplate restTemplate = new RestTemplate();
  private static String[] days = new String[] {
          "monday",
          "tuesday",
          "wednesday",
          "thursday",
          "friday",
          "saturday",
          "sunday"
  };

  @Override
  public SendPlaceDto findPlaceById(String id) {

    try {
      ResponseEntity<FetchPlaceDto> responseEntity = this.restTemplate.getForEntity(url + "/" + id, FetchPlaceDto.class);
      if (responseEntity.getBody() == null) {
        throw new NotFoundException("Place with id " + id + " does not exist");
      }
      FetchPlaceDto fetchPlaceDto = responseEntity.getBody();
      SendPlaceDto sendPlaceDto = new SendPlaceDto();
      sendPlaceDto.setAddress(fetchPlaceDto.getDisplayedWhere());
      sendPlaceDto.setName(fetchPlaceDto.getDisplayedWhat());
      sendPlaceDto.setId(fetchPlaceDto.getId());
      sendPlaceDto.setWorkingHours(reduceWorkingHours(fetchPlaceDto.getWorkingHours()));
      return sendPlaceDto;
    } catch (HttpClientErrorException ex) {
      if (ex.getStatusCode().equals(HttpStatus.NOT_FOUND)) {
        throw new NotFoundException("Place with id " + id + " does not exist");
      }
      throw new RuntimeException("System error");
    }
  }

  private Map<String, List<WorkingHourSlot>> reduceWorkingHours(Map<String, List<WorkingHourSlot>> initial) {
    Map<String, List<WorkingHourSlot>> result = new HashMap<>();
    String streakStart = null, streakEnd = null;
    List<WorkingHourSlot> streakHours = null;
    for (String workingDay : days) {
      List<WorkingHourSlot> workingHourSlots = initial.get(workingDay);
      if (workingHourSlots == null) {
        result.put(workingDay, null);
        if (streakStart != null) {
          result.put((streakStart.equals(streakEnd)) ? streakStart : streakStart + " - " + streakEnd , streakHours);
        }
        streakStart = null;
        streakEnd = null;
        streakHours = null;
        continue;
      }
      if (streakStart == null) {
        streakStart = workingDay;
        streakEnd = workingDay;
        streakHours = workingHourSlots;
        continue;
      }
      if (compareSlots(workingHourSlots, streakHours)) {
        streakEnd = workingDay;
      } else {
        result.put((streakStart.equals(streakEnd)) ? streakStart : streakStart + " - " + streakEnd , streakHours);
        streakStart = workingDay;
        streakEnd = workingDay;
        streakHours = workingHourSlots;
      }
    }
    if (streakStart != null) {
      result.put((streakStart.equals(streakEnd)) ? streakStart : streakStart + " - " + streakEnd , streakHours);
    }
    return result;
  }

  private boolean compareSlots(List<WorkingHourSlot> first, List<WorkingHourSlot> second) {
    if (first.size() != second.size()) {
      return false;
    }
    for (WorkingHourSlot slot : first) {
      if (!second.contains(slot)) {
        return false;
      }
    }
    return true;
  }

}
