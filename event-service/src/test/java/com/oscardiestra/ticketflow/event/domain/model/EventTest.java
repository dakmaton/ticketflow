package com.oscardiestra.ticketflow.event.domain.model;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import org.junit.jupiter.api.Test;

class EventTest {

	private static final OffsetDateTime SALES_START = OffsetDateTime.parse("2026-10-01T08:00:00-05:00");

	private static final OffsetDateTime SALES_END = OffsetDateTime.parse("2026-12-20T18:00:00-05:00");

	private static final OffsetDateTime EVENT_START = OffsetDateTime.parse("2026-12-20T20:00:00-05:00");

	private static final OffsetDateTime EVENT_END = OffsetDateTime.parse("2026-12-20T23:00:00-05:00");

	@Test
	void shouldCreateEventWithDraftStatus() {
		Event event = createValidEvent(new BigDecimal("150.00"), 500);

		assertAll(() -> assertNull(event.getId()), () -> assertEquals("Java Conference", event.getNombre()),
				() -> assertEquals(EventStatus.DRAFT, event.getEstado()), () -> assertEquals("USD", event.getMoneda()),
				() -> assertEquals(new BigDecimal("150.00"), event.getPrecio_base()),
				() -> assertNotNull(event.getFecha_creacion()), () -> assertNotNull(event.getFecha_actualizada()));
	}

	@Test
	void shouldNormalizeEventData() {
		Event event = new Event("  Java Conference  ", "  Backend event  ", "  Convention Center  ", "  Lima  ",
				EVENT_START, EVENT_END, SALES_START, SALES_END, 500, new BigDecimal("150.00"), "usd");

		assertAll(() -> assertEquals("Java Conference", event.getNombre()),
				() -> assertEquals("Backend event", event.getDescripcion()),
				() -> assertEquals("Convention Center", event.getVenue()),
				() -> assertEquals("Lima", event.getCiudad()), () -> assertEquals("USD", event.getMoneda()));
	}

	@Test
	void shouldAllowFreeEvent() {
		Event event = createValidEvent(BigDecimal.ZERO, 500);

		assertEquals(0, event.getPrecio_base().signum());
	}

	@Test
	void shouldRejectNegativeBasePrice() {
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
				() -> createValidEvent(new BigDecimal("-0.01"), 500));

		assertEquals("Precio base del evento no puede ser nulo o menor a 0", exception.getMessage());
	}

	@Test
	void shouldRejectZeroCapacity() {
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
				() -> createValidEvent(new BigDecimal("150.00"), 0));

		assertEquals("Capacidad del evento no puede ser nula o menor a 1", exception.getMessage());
	}

	@Test
	void shouldRejectEventEndBeforeStart() {
		OffsetDateTime invalidEnd = EVENT_START.minusHours(1);

		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
				() -> new Event("Java Conference", "Backend event", "Convention Center", "Lima", EVENT_START,
						invalidEnd, SALES_START, SALES_END, 500, new BigDecimal("150.00"), "USD"));

		assertEquals("Fecha fin del evento no puede ser antes del inicio", exception.getMessage());
	}

	@Test
	void shouldRejectSalesEndAfterEventStart() {
		OffsetDateTime invalidSalesEnd = EVENT_START.plusMinutes(30);

		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
				() -> new Event("Java Conference", "Backend event", "Convention Center", "Lima", EVENT_START, EVENT_END,
						SALES_START, invalidSalesEnd, 500, new BigDecimal("150.00"), "USD"));

		assertEquals("Fecha fin de venta no puede ser despues del inicio del evento", exception.getMessage());
	}

	@Test
	void shouldPublishDraftEvent() {
		Event event = createValidEvent(new BigDecimal("150.00"), 500);

		event.publicarEvento();

		assertEquals(EventStatus.PUBLISHED, event.getEstado());
	}

	@Test
	void shouldRejectPublishingAlreadyPublishedEvent() {
		Event event = createValidEvent(new BigDecimal("150.00"), 500);

		event.publicarEvento();

		IllegalStateException exception = assertThrows(IllegalStateException.class, event::publicarEvento);

		assertEquals("Solo se pueden publicar eventos en estado DRAFT", exception.getMessage());
	}

	@Test
	void shouldCancelDraftEvent() {
		Event event = createValidEvent(new BigDecimal("150.00"), 500);

		event.cancelarEvento();

		assertEquals(EventStatus.CANCELLED, event.getEstado());
	}

	@Test
	void shouldCancelPublishedEvent() {
		Event event = createValidEvent(new BigDecimal("150.00"), 500);

		event.publicarEvento();
		event.cancelarEvento();

		assertEquals(EventStatus.CANCELLED, event.getEstado());
	}

	@Test
	void shouldRejectCancellingAlreadyCancelledEvent() {
		Event event = createValidEvent(new BigDecimal("150.00"), 500);

		event.cancelarEvento();

		IllegalStateException exception = assertThrows(IllegalStateException.class, event::cancelarEvento);

		assertEquals("Evento ya se encuentra en estado CANCELLED", exception.getMessage());
	}

	private Event createValidEvent(BigDecimal basePrice, Integer capacity) {

		return new Event("Java Conference", "Backend event", "Convention Center", "Lima", EVENT_START, EVENT_END,
				SALES_START, SALES_END, capacity, basePrice, "USD");
	}
}