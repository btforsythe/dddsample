package se.citerus.dddsample.tracking.core.application.booking;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static se.citerus.dddsample.tracking.core.domain.model.location.SampleLocations.*;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.stubbing.OngoingStubbing;

import se.citerus.dddsample.tracking.core.domain.model.cargo.Cargo;
import se.citerus.dddsample.tracking.core.domain.model.cargo.CargoRepository;
import se.citerus.dddsample.tracking.core.domain.model.cargo.TrackingId;
import se.citerus.dddsample.tracking.core.domain.model.cargo.TrackingIdFactory;
import se.citerus.dddsample.tracking.core.domain.model.location.Location;
import se.citerus.dddsample.tracking.core.domain.model.location.LocationRepository;
import se.citerus.dddsample.tracking.core.domain.model.location.UnLocode;
import se.citerus.dddsample.tracking.core.domain.service.RoutingService;

// TODO this test results in log messages -- a mock needs to be injected somewhere to avoid this
public class BookingServiceTest {

	// TODO this should be BookingServiceImplTest, not BookingServiceTest
	private BookingServiceImpl underTest;

	@Mock
	private RoutingService routingService;
	@Mock
	private TrackingIdFactory trackingIdFactory;
	@Mock
	private CargoRepository cargoRepository;
	@Mock
	private LocationRepository locationRepository;

	@Mock
	private Date arrivalDeadline;

	@Before
	public void setup() throws Exception {

		MockitoAnnotations.initMocks(this);

		underTest = new BookingServiceImpl(routingService, trackingIdFactory,
				cargoRepository, locationRepository);

		stubLocationRetrieval();
	}

	private void stubLocationRetrieval() {
		whenDereferencing(chicagoLocationCode()).thenReturn(CHICAGO);
		whenDereferencing(stockholmLocationCode()).thenReturn(STOCKHOLM);
	}

	private OngoingStubbing<Location> whenDereferencing(UnLocode code) {
		return when(locationRepository.find(code));
	}

	// TODO Would it be a good idea to create location code constants for use in
	// tests?
	private UnLocode chicagoLocationCode() {
		return new UnLocode("USCHI");
	}

	private UnLocode stockholmLocationCode() {
		return new UnLocode("SESTO");
	}

	@Test
	public void shouldRegisterNew() {

		// TODO put tracking id is method. will break atm because it doesn't
		// implement equals
		whenRetrievingNextTrackingId().thenReturn(expectedTrackingId());

		TrackingId result = underTest.bookNewCargo(chicagoLocationCode(),
				stockholmLocationCode(), arrivalDeadline);
		assertThat(result, is(expectedTrackingId()));

		// TODO any is bad. need to separate logic and construction in impl
		verify(cargoRepository).store(Mockito.any(Cargo.class));
	}

	private OngoingStubbing<TrackingId> whenRetrievingNextTrackingId() {
		return when(trackingIdFactory.nextTrackingId());
	}

	private TrackingId expectedTrackingId() {
		// TODO we should simply make this class not final and mock this
		return new TrackingId("TRK1");
	}

}
