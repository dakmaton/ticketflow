package com.oscardiestra.ticketflow.event.domain.model;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Currency;
import java.util.Locale;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "events")
public class Event {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@Column(name = "name", nullable = false, length = 120)
	private String nombre;

	@Column(name = "description", length = 1000)
	private String descripcion;

	@Column(name = "venue", nullable = false, length = 150)
	private String venue;

	@Column(name = "city", nullable = false, length = 100)
	private String ciudad;

	@Column(name = "start_at", nullable = false)
	private OffsetDateTime fecha_inicio_evento;

	@Column(name = "end_at", nullable = false)
	private OffsetDateTime fecha_fin_evento;

	@Column(name = "sales_start_at", nullable = false)
	private OffsetDateTime fecha_inicio_venta;

	@Column(name = "sales_end_at", nullable = false)
	private OffsetDateTime fecha_fin_venta;

	@Column(name = "capacity", nullable = false)
	private Integer capacidad;

	@Column(name = "base_price", nullable = false, precision = 12, scale = 2)
	private BigDecimal precio_base;

	@Column(name = "currency", nullable = false, length = 3)
	private String moneda;

	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false, length = 20)
	private EventStatus estado;

	@Column(name = "created_at", nullable = false)
	private OffsetDateTime fecha_creacion;

	@Column(name = "updated_at", nullable = false)
	private OffsetDateTime fecha_actualizada;

	protected Event() {

	}

	public Event(String nombre, String descripcion, String venue, String ciudad, OffsetDateTime fecha_inicio_evento,
			OffsetDateTime fecha_fin_evento, OffsetDateTime fecha_inicio_venta, OffsetDateTime fecha_fin_venta,
			Integer capacidad, BigDecimal precio_base, String moneda) {

		validarFechas(fecha_inicio_evento, fecha_fin_evento, fecha_inicio_venta, fecha_fin_venta);
		validarCapacidad(capacidad);
		validarPrecioBase(precio_base);

		this.nombre = textoRequerido(nombre, "Nombre");
		this.descripcion = textoOpcional(descripcion);
		this.venue = textoRequerido(venue, "Venue");
		this.ciudad = textoRequerido(ciudad, "Ciudad");
		this.fecha_inicio_evento = fecha_inicio_evento;
		this.fecha_fin_evento = fecha_fin_evento;
		this.fecha_inicio_venta = fecha_inicio_venta;
		this.fecha_fin_venta = fecha_fin_venta;
		this.capacidad = capacidad;
		this.precio_base = precio_base;
		this.moneda = validarMoneda(moneda);
		this.estado = EventStatus.DRAFT;

		OffsetDateTime actual = OffsetDateTime.now(ZoneOffset.UTC);
		this.fecha_creacion = actual;
		this.fecha_actualizada = actual;

	}

	private static void validarFechas(OffsetDateTime fecha_inicio_evento, OffsetDateTime fecha_fin_evento,
			OffsetDateTime fecha_inicio_venta, OffsetDateTime fecha_fin_venta) {
		if (fecha_inicio_evento == null || fecha_fin_evento == null || fecha_inicio_venta == null
				|| fecha_fin_venta == null) {
			throw new IllegalArgumentException("Dias del evento no pueden ser nulos");
		}

		if (!fecha_fin_evento.isAfter(fecha_inicio_evento)) {
			throw new IllegalArgumentException("Fecha fin del evento no puede ser antes del inicio");
		}

		if (!fecha_fin_venta.isAfter(fecha_inicio_venta)) {
			throw new IllegalArgumentException("Fecha fin de venta no puede ser antes del inicio de venta");
		}

		if (fecha_fin_venta.isAfter(fecha_inicio_evento)) {
			throw new IllegalArgumentException("Fecha fin de venta no puede ser despues del inicio del evento");
		}
	}

	private static void validarCapacidad(Integer capacidad) {
		if (capacidad == null || capacidad <= 0) {
			throw new IllegalArgumentException("Capacidad del evento no puede ser nula o menor a 1");
		}
	}

	private static void validarPrecioBase(BigDecimal precio_base) {
		if (precio_base == null || precio_base.signum() < 0) {
			throw new IllegalArgumentException("Precio base del evento no puede ser nulo o menor a 0");
		}
	}

	private static String validarMoneda(String moneda) {
		String monedaNormalizada = textoRequerido(moneda, "Moneda").toUpperCase(Locale.ROOT);
		return Currency.getInstance(monedaNormalizada).getCurrencyCode();
	}

	private static String textoRequerido(String texto, String nombreCampo) {
		if (texto == null || texto.isBlank()) {
			throw new IllegalArgumentException(nombreCampo + " es requerido");
		}
		return texto.trim();
	}

	private static String textoOpcional(String texto) {
		if (texto == null || texto.isBlank()) {
			return null;
		}
		return texto.trim();
	}

	public void publicarEvento() {
		if (estado != EventStatus.DRAFT) {
			throw new IllegalStateException("Solo se pueden publicar eventos en estado DRAFT");
		}
		this.estado = EventStatus.PUBLISHED;
	}

	public void cancelarEvento() {
		
		if (estado == EventStatus.CANCELLED) {
			throw new IllegalStateException("Evento ya se encuentra en estado CANCELLED");
		}
		
		if (estado == EventStatus.COMPLETED) {
			throw new IllegalStateException("Evento completado no puede ser cancelado");
		}
		this.estado = EventStatus.CANCELLED;
	}

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public String getVenue() {
		return venue;
	}

	public void setVenue(String venue) {
		this.venue = venue;
	}

	public String getCiudad() {
		return ciudad;
	}

	public void setCiudad(String ciudad) {
		this.ciudad = ciudad;
	}

	public OffsetDateTime getFecha_inicio_evento() {
		return fecha_inicio_evento;
	}

	public void setFecha_inicio_evento(OffsetDateTime fecha_inicio_evento) {
		this.fecha_inicio_evento = fecha_inicio_evento;
	}

	public OffsetDateTime getFecha_fin_evento() {
		return fecha_fin_evento;
	}

	public void setFecha_fin_evento(OffsetDateTime fecha_fin_evento) {
		this.fecha_fin_evento = fecha_fin_evento;
	}

	public OffsetDateTime getFecha_inicio_venta() {
		return fecha_inicio_venta;
	}

	public void setFecha_inicio_venta(OffsetDateTime fecha_inicio_venta) {
		this.fecha_inicio_venta = fecha_inicio_venta;
	}

	public OffsetDateTime getFecha_fin_venta() {
		return fecha_fin_venta;
	}

	public void setFecha_fin_venta(OffsetDateTime fecha_fin_venta) {
		this.fecha_fin_venta = fecha_fin_venta;
	}

	public Integer getCapacidad() {
		return capacidad;
	}

	public void setCapacidad(Integer capacidad) {
		this.capacidad = capacidad;
	}

	public BigDecimal getPrecio_base() {
		return precio_base;
	}

	public void setPrecio_base(BigDecimal precio_base) {
		this.precio_base = precio_base;
	}

	public String getMoneda() {
		return moneda;
	}

	public void setMoneda(String moneda) {
		this.moneda = moneda;
	}

	public EventStatus getEstado() {
		return estado;
	}

	public void setEstado(EventStatus estado) {
		this.estado = estado;
	}

	public OffsetDateTime getFecha_creacion() {
		return fecha_creacion;
	}

	public void setFecha_creacion(OffsetDateTime fecha_creacion) {
		this.fecha_creacion = fecha_creacion;
	}

	public OffsetDateTime getFecha_actualizada() {
		return fecha_actualizada;
	}

	public void setFecha_actualizada(OffsetDateTime fecha_actualizada) {
		this.fecha_actualizada = fecha_actualizada;
	}

}
