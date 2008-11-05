package se.citerus.dddsample.domain.model.handling;

import junit.framework.TestCase;
import static org.easymock.EasyMock.*;
import se.citerus.dddsample.application.persistence.LocationRepositoryInMem;
import se.citerus.dddsample.application.persistence.VoyageRepositoryInMem;
import se.citerus.dddsample.domain.model.cargo.Cargo;
import se.citerus.dddsample.domain.model.cargo.CargoRepository;
import se.citerus.dddsample.domain.model.cargo.TrackingId;
import static se.citerus.dddsample.domain.model.carrier.SampleVoyages.CM001;
import se.citerus.dddsample.domain.model.carrier.Voyage;
import se.citerus.dddsample.domain.model.carrier.VoyageNumber;
import se.citerus.dddsample.domain.model.carrier.VoyageRepository;
import static se.citerus.dddsample.domain.model.handling.HandlingEvent.Type;
import se.citerus.dddsample.domain.model.location.LocationRepository;
import static se.citerus.dddsample.domain.model.location.SampleLocations.*;
import se.citerus.dddsample.domain.model.location.UnLocode;
import se.citerus.dddsample.domain.service.UnknownCargoException;
import se.citerus.dddsample.domain.service.UnknownLocationException;
import se.citerus.dddsample.domain.service.UnknownVoyageException;

import java.util.Date;

public class HandlingEventFactoryTest extends TestCase {

  HandlingEventFactory factory;
  CargoRepository cargoRepository;
  VoyageRepository voyageRepository;
  LocationRepository locationRepository;
  TrackingId trackingId;
  Cargo cargo;

  protected void setUp() throws Exception {

    cargoRepository = createMock(CargoRepository.class);
    voyageRepository = new VoyageRepositoryInMem();
    locationRepository = new LocationRepositoryInMem();
    factory = new HandlingEventFactory(cargoRepository, voyageRepository, locationRepository);



    trackingId = new TrackingId("ABC");
    cargo = new Cargo(trackingId, TOKYO, HELSINKI);
  }

  public void testCreateHandlingEventWithCarrierMovement() throws Exception {
    expect(cargoRepository.find(trackingId)).andReturn(cargo);

    replay(cargoRepository);

    VoyageNumber voyageNumber = CM001.voyageNumber();
    UnLocode unLocode = STOCKHOLM.unLocode();
    HandlingEvent handlingEvent = factory.createHandlingEvent(
      new Date(100), trackingId, voyageNumber, unLocode, Type.LOAD
    );

    assertNotNull(handlingEvent);
    assertEquals(STOCKHOLM, handlingEvent.location());
    assertEquals(CM001, handlingEvent.voyage());
    assertEquals(cargo, handlingEvent.cargo());
    assertEquals(new Date(100), handlingEvent.completionTime());
    assertTrue(handlingEvent.registrationTime().before(new Date(System.currentTimeMillis() + 1)));
  }

  public void testCreateHandlingEventWithoutCarrierMovement() throws Exception {
    expect(cargoRepository.find(trackingId)).andReturn(cargo);

    replay(cargoRepository);

    UnLocode unLocode = STOCKHOLM.unLocode();
    HandlingEvent handlingEvent = factory.createHandlingEvent(
      new Date(100), trackingId, null, unLocode, Type.CLAIM
    );

    assertNotNull(handlingEvent);
    assertEquals(STOCKHOLM, handlingEvent.location());
    assertEquals(Voyage.NONE, handlingEvent.voyage());
    assertEquals(cargo, handlingEvent.cargo());
    assertEquals(new Date(100), handlingEvent.completionTime());
    assertTrue(handlingEvent.registrationTime().before(new Date(System.currentTimeMillis() + 1)));
  }

  public void testCreateHandlingEventUnknownLocation() throws Exception {
    expect(cargoRepository.find(trackingId)).andReturn(cargo);

    replay(cargoRepository);

    UnLocode invalid = new UnLocode("NOEXT");
    try {
      factory.createHandlingEvent(
        new Date(100), trackingId, CM001.voyageNumber(), invalid, Type.LOAD
      );
      fail("Expected UnknownLocationException");
    } catch (UnknownLocationException expected) {}
  }

  public void testCreateHandlingEventUnknownCarrierMovement() throws Exception {
    expect(cargoRepository.find(trackingId)).andReturn(cargo);

    replay(cargoRepository);

    try {
      VoyageNumber invalid = new VoyageNumber("XXX");
      factory.createHandlingEvent(
        new Date(100), trackingId, invalid, STOCKHOLM.unLocode(), Type.LOAD
      );
      fail("Expected UnknownVoyageException");
    } catch (UnknownVoyageException expected) {}
  }

  public void testCreateHandlingEventUnknownTrackingId() throws Exception {
    expect(cargoRepository.find(trackingId)).andReturn(null);

    replay(cargoRepository);

    try {
      factory.createHandlingEvent(
        new Date(100), trackingId, CM001.voyageNumber(), STOCKHOLM.unLocode(), Type.LOAD
      );
      fail("Expected UnknownCargoException");
    } catch (UnknownCargoException expected) {}
  }

  protected void tearDown() throws Exception {
    verify(cargoRepository);
  }
}
