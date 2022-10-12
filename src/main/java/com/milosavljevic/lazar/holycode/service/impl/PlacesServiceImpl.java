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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

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
      sendPlaceDto.setOpen(isPlaceOpen(fetchPlaceDto));
      return sendPlaceDto;
    } catch (HttpClientErrorException ex) {
      if (ex.getStatusCode().equals(HttpStatus.NOT_FOUND)) {
        throw new NotFoundException("Place with id " + id + " does not exist");
      }
      throw new RuntimeException("System error");
    }
  }

  private Map<String, List<WorkingHourSlot>> reduceWorkingHours(Map<String, List<WorkingHourSlot>> initial) {
    Map<String, List<WorkingHourSlot>> result = new LinkedHashMap<>();
    String streakStart = null, streakEnd = null;
    List<WorkingHourSlot> streakHours = null;
    for (String workingDay : days) {
      List<WorkingHourSlot> workingHourSlots = initial.get(workingDay);
      if (workingHourSlots == null) {
        if (streakStart != null) {
          result.put((streakStart.equals(streakEnd)) ? streakStart : streakStart + " - " + streakEnd , streakHours);
        }
        result.put(workingDay, null);
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

  private boolean isPlaceOpen(FetchPlaceDto fetchPlaceDto) {
    LocalDateTime nowDateTime = LocalDate.now().atTime(13, 0);
    String dayOfTheWeek = nowDateTime.getDayOfWeek().toString().toLowerCase(Locale.ROOT);
    List<WorkingHourSlot> slots = fetchPlaceDto.getWorkingHours().get(dayOfTheWeek);
    if (slots != null) {
      for (WorkingHourSlot slot : slots) {
        LocalTime startTime = LocalTime.parse(slot.getStart());
        LocalTime endTime =  LocalTime.parse(slot.getEnd());
        LocalDateTime startDateTime = nowDateTime.toLocalDate().atTime(startTime);
        LocalDateTime endDateTime = nowDateTime.toLocalDate().atTime(endTime);
        if (endTime.compareTo(startTime) < 0) {
          endDateTime = endDateTime.plusDays(1);
        }
        if (!nowDateTime.isBefore(startDateTime) && nowDateTime.isBefore(endDateTime)) {
          return true;
        }
      }
    }
    return false;
  }

}
