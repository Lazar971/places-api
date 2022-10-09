package com.milosavljevic.lazar.holycode.service;

import com.milosavljevic.lazar.holycode.dto.SendPlaceDto;

public interface PlacesService {
  SendPlaceDto findPlaceById(String id);
}
