package com.milosavljevic.lazar.holycode.controller;

import com.milosavljevic.lazar.holycode.dto.SendPlaceDto;
import com.milosavljevic.lazar.holycode.service.PlacesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/places")
@CrossOrigin(methods = {RequestMethod.GET}, origins = {"http://localhost:3000"})
public class PlacesController {
  @Autowired
  private PlacesService placesService;

  @GetMapping("/{placeId}")
  public SendPlaceDto sendPlace(@PathVariable String placeId) {
    return placesService.findPlaceById(placeId);
  }
}
