package se.citerus.dddsample.service;

import org.springframework.transaction.annotation.Transactional;
import se.citerus.dddsample.domain.*;
import se.citerus.dddsample.repository.LocationRepository;

import java.util.*;

/**
 * Simple routing service implementation that randomly creates a number
 * of different itineraries.
 *
 */
public class RoutingServiceImpl implements RoutingService {

  LocationRepository locationRepository;

  @Transactional(readOnly = true)
  public Set<Itinerary> calculatePossibleRoutes(Cargo cargo, Specification specification) {
    List<Location> allLocations = locationRepository.findAll();

    allLocations.remove(cargo.origin());
    allLocations.remove(cargo.finalDestination());

    // TODO: vary the number of locations and number of candidates randomly

    int candidateCount = 3;
    Set<Itinerary> candidates = new HashSet<Itinerary>(candidateCount);
    for (int i = 0; i < candidateCount; i++) {
      Collections.shuffle(allLocations);
      List<Leg> legs = new ArrayList<Leg>(allLocations.size() - 1);

      legs.add(new Leg(new CarrierMovementId("CM000"), cargo.origin(), allLocations.get(0)));
      for (int j = 0; j < allLocations.size() - 1 ; j++) {
        legs.add(new Leg(new CarrierMovementId("CM00" + j), 
          allLocations.get(j), allLocations.get(j + 1)));
      }
      legs.add(new Leg(new CarrierMovementId("CM999"), allLocations.get(allLocations.size() - 1), cargo.finalDestination()));

      Itinerary itinerary = new Itinerary(legs);

      candidates.add(itinerary);
    }

    return candidates;
  }

  public void setLocationRepository(LocationRepository locationRepository) {
    this.locationRepository = locationRepository;
  }

}
