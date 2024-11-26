package com.project.casaberriel;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.project.casaberriel.controllers.ReservaController;
import com.project.casaberriel.model.reservas.ReservaEntity;
import com.project.casaberriel.model.reservas.ReservaForm;
import com.project.casaberriel.model.usuarios.Usuario;
import com.project.casaberriel.service.ReservaService;
import com.project.casaberriel.service.UsuarioService;

public class ReservaControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ReservaService reservaService;

    @Mock
    private UsuarioService usuarioService;

    @Mock
    private Model model;

    @InjectMocks
    private ReservaController reservaController;
    @Mock
    private RedirectAttributes redirectAttributes;
    
    @Mock
    private SecurityContext securityContext; // Simula el contexto de seguridad
    
    @Mock
    private HttpSession session;

    @Mock
    private Authentication authentication; // Simula la autenticación
    private ReservaEntity reservaExistente;
    private ReservaEntity reservaActualizada;
    private ReservaForm reservaForm;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(reservaController).build();
        SecurityContextHolder.setContext(securityContext);
        // Crear mocks y objetos de prueba
        reservaExistente = new ReservaEntity();
        reservaExistente.setId(1L);
        reservaExistente.setNombre("John");
        reservaExistente.setApellidos("Doe");
        reservaExistente.setEmail("john.doe@example.com");

        reservaActualizada = new ReservaEntity();
        reservaActualizada.setNombre("Jane");
        reservaActualizada.setApellidos("Smith");
        reservaActualizada.setEmail("jane.smith@example.com");

        reservaForm = new ReservaForm();
    }

    @Test
    public void testGuardarReserva() throws Exception {
        // Configuración de mocks
        ReservaEntity reservaMock = new ReservaEntity();
        ReservaForm reservaFormMock = new ReservaForm();
        String username = "testUser";

        // Simulamos el comportamiento del servicio
        when(reservaService.guardarReserva(any(), any(), anyString(), anyBoolean(), anyBoolean()))
        .thenReturn(new ReservaEntity()); // Devuelve un objeto simulado o una instancia real

        // Realizamos la prueba simulando una solicitud POST
        mockMvc.perform(post("/reservas/guardar")
                .param("nombre", "Juan")
                .param("apellidos", "Pérez")
                .param("fechaEntrada", "25/11/2024")
                .param("fechaSalida", "30/11/2024")
                .principal(() -> username) // Simulamos el Principal
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk()) // Validamos que la respuesta sea 200 OK
                .andExpect(view().name("reservas")) // Validamos que se retorne la vista correcta
                .andExpect(model().attributeExists("message")) // Validamos que el atributo "message" esté presente
                .andExpect(model().attribute("message", "Reserva guardada con éxito."));

        // Verificamos que el servicio fue invocado
        verify(reservaService, times(1)).guardarReserva(any(ReservaEntity.class), any(ReservaForm.class), eq(username), anyBoolean(), anyBoolean());
    }
    @Test
    public void testMostrarFormularioReserva() throws Exception {
        // Simulamos la lista de reservas
        when(reservaService.listarReservas()).thenReturn(List.of(new ReservaEntity()));

        // Realizamos la prueba simulando una solicitud GET
        mockMvc.perform(get("/reservas/formReserva")
                .param("fechaEntrada", "25/11/2024")
                .param("fechaSalida", "30/11/2024"))
                .andExpect(status().isOk()) // Validamos que la respuesta sea 200 OK
                .andExpect(view().name("reservas")) // Validamos que se retorne la vista correcta
                .andExpect(model().attributeExists("fechaEntrada", "fechaSalida")) // Validamos los atributos del modelo
                .andExpect(model().attribute("fechaEntrada", "25/11/2024"))
                .andExpect(model().attribute("fechaSalida", "30/11/2024"));

        // Verificamos que el servicio fue invocado
        verify(reservaService, times(1)).listarReservas();
    }

    @Test
    public void testDetalleReserva_ReservaExistente() {
        // Configuración del mock para el caso en que la reserva existe
        Long idReserva = 1L;
        ReservaEntity reservaMock = new ReservaEntity();
        reservaMock.setId(idReserva);
        when(reservaService.obtenerReservaPorId(idReserva)).thenReturn(reservaMock);

        // Llamada al método
        String viewName = reservaController.detalleReserva(idReserva, model);

        // Verificaciones
        verify(reservaService).obtenerReservaPorId(idReserva);
        verify(model).addAttribute("reserva", reservaMock);
        assertEquals("detalle_reserva", viewName); // Verifica que la vista sea la correcta
    }
    
    @Test
    public void testDetalleReserva_ReservaNoExistente() {
        // Configuración del mock para el caso en que la reserva no existe
        Long idReserva = 1L;
        when(reservaService.obtenerReservaPorId(idReserva)).thenReturn(null);

        // Llamada al método
        String viewName = reservaController.detalleReserva(idReserva, model);

        // Verificaciones
        verify(reservaService).obtenerReservaPorId(idReserva);
        verify(model, never()).addAttribute(eq("reserva"), any()); // No se debe agregar la reserva al modelo
        assertEquals("redirect:/error", viewName); // Verifica la redirección a la página de error
    }
    
    @Test
    public void testGuardarReservaEditada_ReservaExistente() throws Exception {
        // Configurar mocks
        when(reservaService.obtenerReservaPorId(1L)).thenReturn(reservaExistente);

        // Ejecutar el método
        String viewName = reservaController.guardarReservaEditada(
                1L, 
                reservaActualizada, 
                model, 
                reservaForm, 
                "jane.smith@example.com", 
                false, 
                true
        );

        // Verificar resultados
        assertEquals("editar_reserva", viewName);
        verify(reservaService, times(1))
                .guardarReserva(reservaExistente, reservaForm, "jane.smith@example.com", false, true);
        verify(model).addAttribute("message", "Reserva actualizada con éxito.");
        verify(model).addAttribute("modificada", true);
        verify(model).addAttribute("cancelada", false);

        // Verificar que se actualizaron los campos de la reserva existente
        assertEquals("Jane", reservaExistente.getNombre());
        assertEquals("Smith", reservaExistente.getApellidos());
        assertEquals("jane.smith@example.com", reservaExistente.getEmail());
    }
    
    @Test
    public void testGuardarReservaEditada_ReservaNoExistente() throws Exception {
        // Configurar mocks
        when(reservaService.obtenerReservaPorId(1L)).thenReturn(null);

        // Ejecutar el método
        String viewName = reservaController.guardarReservaEditada(
                1L, 
                reservaActualizada, 
                model, 
                reservaForm, 
                "jane.smith@example.com", 
                false, 
                true
        );

        // Verificar resultados
        assertEquals("redirect:/error", viewName);
        verify(reservaService, never()).guardarReserva(any(), any(), anyString(), anyBoolean(), anyBoolean());
    }

    @Test
    public void testDeleteReserva_AsAdmin() throws Exception {
        // Crear la colección de authorities
        Collection<? extends GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));

        // Configurar mocks
        when(securityContext.getAuthentication()).thenReturn(authentication);
     

        // Ejecutar el método
        String viewName = reservaController.deleteReserva(1L, null, "user@example.com", true, false, redirectAttributes);

        // Verificar resultados
        assertEquals("redirect:/reservas/miReserva", viewName);
        verify(reservaService, times(1)).eliminarReserva(1L, "user@example.com", true, false);
        verify(redirectAttributes).addFlashAttribute("message", "Reserva eliminada con éxito.");
    }
    
    @Test
    public void testComprobarDisponibilidad() {
        // Datos de entrada simulados
        String fechaEntrada = "01/12/2024";
        String fechaSalida = "10/12/2024";
        Long reservaId = 1L;

        // Configurar el mock para el servicio
        when(reservaService.comprobarDisponibilidadEdicion(any(ReservaForm.class), eq(reservaId))).thenReturn(true);

        // Llamada al método
        ResponseEntity<Map<String, Boolean>> response = reservaController.comprobarDisponibilidad(
                fechaEntrada, fechaSalida, reservaId, model);

        // Verificar que el modelo no tiene errores
        verify(model, never()).addAttribute(eq("error"), anyString());

        // Validar la respuesta
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().get("disponible"));

        // Verificar la llamada al servicio
        ArgumentCaptor<ReservaForm> reservaFormCaptor = ArgumentCaptor.forClass(ReservaForm.class);
        verify(reservaService).comprobarDisponibilidadEdicion(reservaFormCaptor.capture(), eq(reservaId));

        ReservaForm capturado = reservaFormCaptor.getValue();
        assertEquals(fechaEntrada, capturado.getFechaEntrada());
        assertEquals(fechaSalida, capturado.getFechaSalida());
    }
    
  
    @Test
    public void testComprobarDisponibilidad_ReservaIdNulo() {
        // Datos de entrada con reservaId nulo
        String fechaEntrada = "01/12/2024";
        String fechaSalida = "10/12/2024";
        Long reservaId = null;

        // Llamada al método
        ResponseEntity<Map<String, Boolean>> response = reservaController.comprobarDisponibilidad(
                fechaEntrada, fechaSalida, reservaId, model);

        // Validar la respuesta en caso de reservaId nulo
        verify(model).addAttribute(eq("error"), eq("El ID de la reserva no puede ser nulo"));
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertFalse(response.getBody().get("disponible"));

        // Verificar que el servicio no fue llamado
        verify(reservaService, never()).comprobarDisponibilidadEdicion(any(ReservaForm.class), eq(reservaId));
    }
    
    @Test
    public void testMostrarFormularioReserva_UsuarioNoLogueado() {
        // Simular la sesión sin usuario
        when(session.getAttribute("usuario")).thenReturn(null);

        // Datos de entrada
        String fechaEntrada = "01/12/2024";
        String fechaSalida = "10/12/2024";

        // Llamada al método
        String viewName = reservaController.mostrarFormularioReserva(fechaEntrada, fechaSalida, model, session);

        // Validaciones
        assertEquals("registro", viewName);
        verify(model).addAttribute(eq("redirectUrl"),
                eq("/reservas/lista?fechaEntrada=" + fechaEntrada + "&fechaSalida=" + fechaSalida));
    }

}
