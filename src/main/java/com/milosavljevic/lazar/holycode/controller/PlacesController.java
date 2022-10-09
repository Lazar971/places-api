package com.milosavljevic.lazar.holycode.controller;

import com.milosavljevic.lazar.holycode.dto.SendPlaceDto;
import com.milosavljevic.lazar.holycode.service.PlacesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/places")
public class PlacesController {
  @Autowired
  private PlacesService placesService;

  @GetMapping("/{placeId}")
  public SendPlaceDto sendPlace(@PathVariable String placeId) {
    return placesService.findPlaceById(placeId);
  }
}
